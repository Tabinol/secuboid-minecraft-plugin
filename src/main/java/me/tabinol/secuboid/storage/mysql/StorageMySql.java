/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.storage.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.secuboid.NewInstance;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.ChunkMatrix;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.RegionMatrix;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.Storage;
import me.tabinol.secuboid.storage.mysql.dao.AreasDao;
import me.tabinol.secuboid.storage.mysql.dao.AreasRoadsMatricesDao;
import me.tabinol.secuboid.storage.mysql.dao.GenericIdValueDao;
import me.tabinol.secuboid.storage.mysql.dao.LandsDao;
import me.tabinol.secuboid.storage.mysql.dao.PlayerContainersDao;
import me.tabinol.secuboid.storage.mysql.dao.SecuboidDao;
import me.tabinol.secuboid.storage.mysql.pojo.AreaPojo;
import me.tabinol.secuboid.storage.mysql.pojo.LandPojo;
import me.tabinol.secuboid.storage.mysql.pojo.PlayerContainerPojo;
import me.tabinol.secuboid.storage.mysql.pojo.RoadMatrixPojo;

/**
 * StorageMySql
 */
public class StorageMySql implements Storage {

    private final Secuboid secuboid;
    private final Logger log;
    private final DatabaseConnection dbConn;

    // DAO
    private final AreasDao areasDao;
    private final AreasRoadsMatricesDao areasRoadsMatricesDao;
    private final GenericIdValueDao<Integer, String> areasTypesDao;
    private final LandsDao landsDao;
    private final GenericIdValueDao<Integer, String> landsTypesDao;
    private final PlayerContainersDao playerContainersDao;
    private final GenericIdValueDao<Integer, String> playerContainersTypesDao;
    private final GenericIdValueDao<UUID, Integer> landsResidentsDao;
    private final GenericIdValueDao<UUID, Integer> landsBannedDao;

    public StorageMySql(final Secuboid secuboid, final DatabaseConnection dbConn) {
        this.secuboid = secuboid;
        this.dbConn = dbConn;
        log = secuboid.getLogger();

        areasDao = new AreasDao(dbConn);
        areasRoadsMatricesDao = new AreasRoadsMatricesDao(dbConn);
        areasTypesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "areas_types", "id", "name");
        landsDao = new LandsDao(dbConn);
        landsTypesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "lands_types", "id", "name");
        playerContainersDao = new PlayerContainersDao(dbConn);
        playerContainersTypesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "player_containers_types", "id", "name");
        landsResidentsDao = new GenericIdValueDao<>(dbConn, UUID.class, Integer.class, "lands_residents", "land_uuid", "player_container_id");
        landsBannedDao= new GenericIdValueDao<>(dbConn, UUID.class, Integer.class, "lands_banneds", "land_uuid", "player_container_id");
    }

    @Override
    public void loadAll() {
        try {
            initDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            log.log(Level.SEVERE, "Unable to create or access the database", e);
        }
        loadLands();
        loadApproves();
        loadPlayersCache();
        loadInventories();
    }

    private void initDatabase() throws ClassNotFoundException, SQLException {
        dbConn.loadDriver();
        try (final Connection conn = dbConn.openConnection()) {
            // Verify if the database is empty
            final SecuboidDao secuboidDao = new SecuboidDao(dbConn);
            secuboidDao.createVarables(conn);
            final Optional<Integer> versionOpt = secuboidDao.getVersionOpt(conn);
            if (!versionOpt.isPresent() || versionOpt.get() < 1) {
                // Create database
                secuboidDao.initDatabase(conn);
                secuboidDao.setVersion(conn);
            }
        }
    }

    @Override
    public void loadLands() {
        final Map<Land, UUID> orphanToParentUUID = new HashMap<>();
        int loadedlands = 0;

        // Pass 1: load lands
        final Map<UUID, List<AreaPojo>> landUUIDToAreas;
        final Map<UUID, List<RoadMatrixPojo>> landUUIDToMatrices;
        final Map<Integer, String> idToAreaType;
        final List<LandPojo> landPojos;
        final Map<Integer, String> idToType;
        final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo;
        final Map<Integer, String> idToPlayerContainerType;
        final Map<UUID, List<Integer>> landUUIDToResidentIds;
        final Map<UUID, List<Integer>> landUUIDToBannedIds;
        try (final Connection conn = dbConn.openConnection()) {
            landUUIDToAreas = areasDao.getLandUUIDToAreas(conn);
            landUUIDToMatrices = areasRoadsMatricesDao.getLandUUIDToMatrices(conn);
            idToAreaType = areasTypesDao.getIdToName(conn);
            landPojos = landsDao.getLands(conn);
            idToType = landsTypesDao.getIdToName(conn);
            idToPlayerContainerPojo = playerContainersDao.getIdToPlayerContainer(conn);
            idToPlayerContainerType = playerContainersTypesDao.getIdToName(conn);
            landUUIDToResidentIds = landsResidentsDao.getIdToNames(conn);
            landUUIDToBannedIds = landsBannedDao.getIdToNames(conn);
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Unable to load lands from database", e);
            return;
        }
        for (final LandPojo landPojo : landPojos) {
            final Entry<Land, UUID> orphanToParentUUIDEntry;
            final UUID landUUID = landPojo.getUUID();
            try {
                orphanToParentUUIDEntry = loadLand(landPojo, idToType, idToPlayerContainerPojo, idToPlayerContainerType, landUUIDToAreas.get(landUUID), landUUIDToMatrices, 
                        idToAreaType, landUUIDToResidentIds.get(landUUID), landUUIDToBannedIds.get(landUUID));
            } catch (final RuntimeException e) {
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Unable to load the land from database: %s", landPojo.getName()), e);
                continue;
            }
            if (orphanToParentUUIDEntry != null) {
                loadedlands++;
                if (orphanToParentUUIDEntry.getValue() != null) {
                    orphanToParentUUID.put(orphanToParentUUIDEntry.getKey(), orphanToParentUUIDEntry.getValue());
                }
            }
        }

        // Pass 2: find parents
        secuboid.getLands().setParents(orphanToParentUUID);
        secuboid.getLogger().info(loadedlands + " land(s) loaded.");
    }

    @Override
    public void saveLand(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLand(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandArea(final Land land, final Area area) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandArea(final Land land, final Area area) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandBanned(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandBanned(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandFlag(final Land land, final Flag flag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandFlags(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandFlag(final Land land, final Flag flag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandPermissions(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandPlayerNotify(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandResident(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandResidents(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandResident(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadApproves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveApprove(final Approve approve) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeApprove(final Approve approve) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllApproves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadInventories() {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadInventoriesPlayer(final PlayerInventoryCache playerInventoryCache) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadPlayersCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void savePlayerCacheEntry(final PlayerCacheEntry playerCacheEntry) {
        // TODO Auto-generated method stub

    }

    private PlayerContainer getPlayerContainer(final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo, final Map<Integer, String> idToPlayerContainerType, final int id) {
        final PlayerContainerPojo playerContainerPojo = idToPlayerContainerPojo.get(id);
        final String PlayerContainerTypeStr = idToPlayerContainerType.get(playerContainerPojo.getPlayerContainerTypeId());
        final PlayerContainerType playerContainerType = PlayerContainerType.getFromString(PlayerContainerTypeStr);
        return secuboid.getNewInstance().createPlayerContainer(playerContainerType, playerContainerPojo.getParameterOpt().orElse(null));
    }
    
    /**
     * Load a land.
     *
     * @return a map with a land (if success) and the parent (if exists)
     */
    private Entry<Land, UUID> loadLand(final LandPojo landPojo, final Map<Integer, String> idToType, final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo, 
            final Map<Integer, String> idToPlayerContainerType, List<AreaPojo> AreaPojos, final Map<UUID, List<RoadMatrixPojo>> landUUIDToMatrices, 
            final Map<Integer, String> idToAreaType, List<Integer> residentIds, List<Integer> bannedIds) {

        final String type = landPojo.getTypeIdOpt().map(idToType::get).orElse(null);
        final UUID landUUID = landPojo.getUUID();
        Land land = null;
        final Map<Integer, Area> areas = new TreeMap<>();
        boolean isLandCreated = false;
        final Set<PlayerContainer> residents = new TreeSet<>();
        final Set<PlayerContainer> banneds = new TreeSet<>();
        final Map<PlayerContainer, Map<PermissionType, Permission>> permissions = new TreeMap<>();
        final Set<Flag> flags = new HashSet<>();
        final Set<PlayerContainerPlayer> pNotifs = new TreeSet<>();

        // For economy
        PlayerContainerPlayer tenant = null;

        try {
            // create owner (PlayerContainer)
            final PlayerContainer owner = getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, landPojo.getOwnerId());
            if (owner == null) {
                log.log(Level.SEVERE, String.format("Invalid owner [land=%s]", landPojo.getName()));
                return null;
            }

            // Create areas
            for (final AreaPojo areaPojo : AreaPojos) {
                final String areaTypeStr = idToAreaType.get(areaPojo.getAreaTypeId());
                final AreaType areaType;
                try {
                    areaType = AreaType.valueOf(areaTypeStr);
                } catch(final IllegalArgumentException e) {
                    log.log(Level.SEVERE, String.format("Invalid area type [land=%s, areaType=%s, areaId=%s]", landPojo.getName(), areaTypeStr, areaPojo.getAreaId()), e);
                    return null;
                }
                final Area area;
                switch (areaType) {
                    case CUBOID:
                        area = new CuboidArea(areaPojo.isApproved(), areaPojo.getWorldName(), areaPojo.getX1(),areaPojo.getY1(), areaPojo.getZ1(), 
                                areaPojo.getX2(), areaPojo.getY2(), areaPojo.getZ2());
                    break;
                    case CYLINDER:
                        area = new CylinderArea(areaPojo.isApproved(), areaPojo.getWorldName(), areaPojo.getX1(),areaPojo.getY1(), areaPojo.getZ1(), 
                        areaPojo.getX2(), areaPojo.getY2(), areaPojo.getZ2());
                    break;
                    case ROAD:
                        final Map<Integer, Map<Integer, ChunkMatrix>> points = new HashMap<>();
                        for (final RoadMatrixPojo roadMatrixPojo : landUUIDToMatrices.get(landUUID)) {
                            final ChunkMatrix chunkMatrix = new ChunkMatrix(roadMatrixPojo.getMatrix());
                            points.computeIfAbsent(roadMatrixPojo.getChunkX(), k -> new HashMap<>()).put(roadMatrixPojo.getChunkZ(), chunkMatrix);
                        }
                        area = new RoadArea(areaPojo.isApproved(), areaPojo.getWorldName(), areaPojo.getY1(), areaPojo.getY2(), new RegionMatrix(points));
                    default:
                }
    
                areas.put(areaPojo.getAreaId(), area);
            }
            if (areas.isEmpty()) {
                log.log(Level.SEVERE, String.format("No area for this land [land=%s]", landPojo.getName()));
                return null;
            }

            // Residents
            for (int id : residentIds) {
                residents.add(getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, id));
            }

            // Banneds
            for (int id : bannedIds) {
                banneds.add(getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, id));
            }

            // Create permissions
            while ((str = cf.getNextString()) != null) {
                final String[] multiStr = str.split(":");
                TreeMap<PermissionType, Permission> permPlayer;
                final PlayerContainer pc = newInstance
                        .getPlayerContainerFromFileFormat(multiStr[0] + ":" + multiStr[1]);
                final PermissionType permType = secuboid.getPermissionsFlags().getPermissionTypeNoValid(multiStr[2]);
                if (!permissions.containsKey(pc)) {
                    permPlayer = new TreeMap<>();
                    permissions.put(pc, permPlayer);
                } else {
                    permPlayer = permissions.get(pc);
                }
                permPlayer.put(permType, secuboid.getPermissionsFlags().newPermission(permType,
                        Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
            }
            cf.readParam();

            // Create flags
            while ((str = cf.getNextString()) != null) {
                flags.add(secuboid.getNewInstance().getFlagFromFileFormat(str));
            }
            cf.readParam();

            // Set Priority
            priority = cf.getValueShort();
            cf.readParam();

            // Money
            money = cf.getValueDouble();
            cf.readParam();

            // Players Notify
            while ((str = cf.getNextString()) != null) {
                pNotifs.add((PlayerContainerPlayer) newInstance.getPlayerContainerFromFileFormat(str));
            }

            // Economy
            cf.readParam();
            forSale = Boolean.parseBoolean(cf.getValueString());
            if (forSale) {
                cf.readParam();
                forSaleSignLoc = StringChanges.stringToLocation(cf.getValueString());
                cf.readParam();
                salePrice = cf.getValueDouble();
            }
            cf.readParam();
            forRent = Boolean.parseBoolean(cf.getValueString());
            if (forRent) {
                cf.readParam();
                forRentSignLoc = StringChanges.stringToLocation(cf.getValueString());
                cf.readParam();
                rentPrice = cf.getValueDouble();
                cf.readParam();
                rentRenew = cf.getValueInt();
                cf.readParam();
                rentAutoRenew = Boolean.parseBoolean(cf.getValueString());
                cf.readParam();
                rented = Boolean.parseBoolean(cf.getValueString());
                if (rented) {
                    cf.readParam();
                    tenant = (PlayerContainerPlayer) newInstance.getPlayerContainerFromFileFormat(cf.getValueString());
                    cf.readParam();
                    lastPayment = cf.getValueLong();
                }
            }

            cf.close();

            // Catch errors here
        } catch (final NullPointerException ex) {
            try {
                throw new FileLoadException(secuboid, file.getName(), cf != null ? cf.getLine() : "-NOT FOUND-",
                        cf != null ? cf.getLineNb() : 0, "Problem with parameter.");
            } catch (final FileLoadException ex2) {
                // Catch load
                return null;
            }
        } catch (final FileLoadException ex) {
            // Catch load
            return null;
        }

        // Create land
        for (final Map.Entry<Integer, Area> entry : areas.entrySet()) {
            if (!isLandCreated) {
                try {
                    land = secuboid.getLands().createLand(landName, isApproved, owner, entry.getValue(), null,
                            entry.getKey(), uuid, secuboid.getTypes().addOrGetType(type));
                } catch (final SecuboidLandException ex) {
                    secuboid.getLogger().severe("Error on loading land " + landName + ":" + ex.getLocalizedMessage());
                    return null;
                }
                isLandCreated = true;
            } else {
                if (land == null) {
                    secuboid.getLogger().severe("Error: Land not created: " + landName);
                    return null;
                }
                land.addArea(entry.getValue(), entry.getKey());
            }
        }

        if (land == null) {
            secuboid.getLogger().severe("Error: Land not created: " + landName);
            return null;
        }

        // Load land params form memory
        for (final PlayerContainer resident : residents) {
            land.addResident(resident);
        }
        for (final PlayerContainer banned : banneds) {
            land.addBanned(banned);
        }
        for (final Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> entry : permissions.entrySet()) {
            for (final Map.Entry<PermissionType, Permission> entryP : entry.getValue().entrySet()) {
                land.getPermissionsFlags().addPermission(entry.getKey(), entryP.getValue());
            }
        }
        for (final Flag flag : flags) {
            land.getPermissionsFlags().addFlag(flag);
        }
        land.setPriority(priority);
        land.addMoney(money);
        for (final PlayerContainerPlayer pNotif : pNotifs) {
            land.addPlayerNotify(pNotif);
        }

        // Economy add
        if (forSale) {
            land.setForSale(true, salePrice, forSaleSignLoc);
        }
        if (forRent) {
            land.setForRent(rentPrice, rentRenew, rentAutoRenew, forRentSignLoc);
            if (rented) {
                land.setRented(tenant);
                land.setLastPaymentTime(lastPayment);
            }
        }

        return new AbstractMap.SimpleEntry<Land, UUID>(land, parentUUID != null ? UUID.fromString(parentUUID) : null);
    }

}