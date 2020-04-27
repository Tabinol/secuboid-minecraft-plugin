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
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
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
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.Storage;
import me.tabinol.secuboid.storage.mysql.dao.AreasDao;
import me.tabinol.secuboid.storage.mysql.dao.AreasRoadsMatricesDao;
import me.tabinol.secuboid.storage.mysql.dao.FlagsDao;
import me.tabinol.secuboid.storage.mysql.dao.GenericIdValueDao;
import me.tabinol.secuboid.storage.mysql.dao.LandsDao;
import me.tabinol.secuboid.storage.mysql.dao.PermissionsDao;
import me.tabinol.secuboid.storage.mysql.dao.PlayerContainersDao;
import me.tabinol.secuboid.storage.mysql.dao.SecuboidDao;
import me.tabinol.secuboid.storage.mysql.pojo.AreaPojo;
import me.tabinol.secuboid.storage.mysql.pojo.FlagPojo;
import me.tabinol.secuboid.storage.mysql.pojo.LandPojo;
import me.tabinol.secuboid.storage.mysql.pojo.PermissionPojo;
import me.tabinol.secuboid.storage.mysql.pojo.PlayerContainerPojo;
import me.tabinol.secuboid.storage.mysql.pojo.RoadMatrixPojo;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * StorageMySql
 */
public final class StorageMySql implements Storage {

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
    private final PermissionsDao permissionsDao;
    private final GenericIdValueDao<Integer, String> permissionsTypesDao;
    private final FlagsDao flagsDao;
    private final GenericIdValueDao<Integer, String> flagsTypesDao;
    private final GenericIdValueDao<UUID, UUID> playerNotifiesDao;

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
        playerContainersTypesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class,
                "player_containers_types", "id", "name");
        landsResidentsDao = new GenericIdValueDao<>(dbConn, UUID.class, Integer.class, "lands_residents", "land_uuid",
                "player_container_id");
        landsBannedDao = new GenericIdValueDao<>(dbConn, UUID.class, Integer.class, "lands_banneds", "land_uuid",
                "player_container_id");
        permissionsDao = new PermissionsDao(dbConn);
        permissionsTypesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "permissions", "id", "name");
        flagsDao = new FlagsDao(dbConn);
        flagsTypesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "flags", "id", "name");
        playerNotifiesDao = new GenericIdValueDao<>(dbConn, UUID.class, UUID.class, "lands_players_notifies",
                "land_uuid", "player_uuid");
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
        final Map<UUID, List<PermissionPojo>> landUUIDToPermissionPojos;
        final Map<Integer, String> idToPermissionType;
        final Map<UUID, List<FlagPojo>> landUUIDToFlagPojos;
        final Map<Integer, String> idToFlagType;
        final Map<UUID, List<UUID>> landUUIDToPlayerNotifyUUIDs;

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
            landUUIDToPermissionPojos = permissionsDao.getLandUUIDToPermissions(conn);
            idToPermissionType = permissionsTypesDao.getIdToName(conn);
            landUUIDToFlagPojos = flagsDao.getLandUUIDToFlags(conn);
            idToFlagType = flagsTypesDao.getIdToName(conn);
            landUUIDToPlayerNotifyUUIDs = playerNotifiesDao.getIdToNames(conn);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load lands from database", e);
            return;
        }

        for (final LandPojo landPojo : landPojos) {
            final Entry<Land, Optional<UUID>> orphanToParentUUIDOptEntry;
            final UUID landUUID = landPojo.getUUID();
            try {
                orphanToParentUUIDOptEntry = loadLand(landPojo, idToType, idToPlayerContainerPojo,
                        idToPlayerContainerType, landUUIDToAreas.get(landUUID), landUUIDToMatrices, idToAreaType,
                        landUUIDToResidentIds.get(landUUID), landUUIDToBannedIds.get(landUUID),
                        landUUIDToPermissionPojos.get(landUUID), idToPermissionType, landUUIDToFlagPojos.get(landUUID),
                        idToFlagType, landUUIDToPlayerNotifyUUIDs.get(landUUID));
            } catch (final RuntimeException e) {
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Unable to load the land from database: %s", landPojo.getName()), e);
                continue;
            }
            if (orphanToParentUUIDOptEntry != null) {
                loadedlands++;
                orphanToParentUUIDOptEntry.getValue().ifPresent(
                        parentUUID -> orphanToParentUUID.put(orphanToParentUUIDOptEntry.getKey(), parentUUID));
            }
        }

        // Pass 2: find parents
        secuboid.getLands().setParents(orphanToParentUUID);
        secuboid.getLogger().info(loadedlands + " land(s) loaded.");
    }

    @Override
    public void saveLand(final Land land) {

        try (final Connection conn = dbConn.openConnection()) {

            // Get landType
            final Type type = land.getType();
            final Optional<Integer> typeIdOpt;
            if (type != null) {
                typeIdOpt = Optional.of(landsTypesDao.insertOrGetId(conn, type.getName()));
            } else {
                typeIdOpt = Optional.empty();
            }

            // Get ownerId

            // Update land table
            // TODO Ccontinue
            // final LandPojo landPojo = new LandPojo(land.getUUID(), land.getName(),
            // land.isApproved(), typeIdOpt, ownerId, parentUUIDOpt, priority, money,
            // forSale, forSaleSignLocationOpt, salePriceOpt, forRent,
            // forRentSignLocationOpt, rentPriceOpt, rentRenewOpt, rentAutoRenewOpt,
            // tenantUUIDOpt, lastPaymentMillisOpt)
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to save land to database [landUUID=%s, landName=%s]",
                    land.getUUID(), land.getName()), e);
            return;
        }
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

    private PlayerContainer getPlayerContainer(final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo,
            final Map<Integer, String> idToPlayerContainerType, final int id) {
        final PlayerContainerPojo playerContainerPojo = idToPlayerContainerPojo.get(id);
        final String PlayerContainerTypeStr = idToPlayerContainerType
                .get(playerContainerPojo.getPlayerContainerTypeId());
        final PlayerContainerType playerContainerType = PlayerContainerType.getFromString(PlayerContainerTypeStr);
        // TODO Change for PlayerContainerPlayer (UUID)
        return secuboid.getPlayerContainers().getOrAddPlayerContainer(playerContainerType,
                playerContainerPojo.getParameterOpt(), Optional.empty());
    }

    private int getOrAddPlayerContainer(final Connection conn, final PlayerContainer playerContainer)
            throws SQLException {
        final int playerContainerTypeId = playerContainersTypesDao.insertOrGetId(conn,
                playerContainer.getContainerType().name());
        // final int playerContainerId =
        // playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId,
        // playerUUIDOpt, parameterOpt)
        // TODO Contineu
        return 0;
    }

    /**
     * Load a land.
     *
     * @return a map with a land (if success) and the parent (if exists)
     */
    private Entry<Land, Optional<UUID>> loadLand(final LandPojo landPojo, final Map<Integer, String> idToType,
            final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo,
            final Map<Integer, String> idToPlayerContainerType, final List<AreaPojo> areaPojos,
            final Map<UUID, List<RoadMatrixPojo>> landUUIDToMatrices, final Map<Integer, String> idToAreaType,
            final List<Integer> residentIds, final List<Integer> bannedIds, final List<PermissionPojo> permissionPojos,
            final Map<Integer, String> idToPermissionType, final List<FlagPojo> flagPojos,
            final Map<Integer, String> idToFlagType, final List<UUID> playerNotifyUUIDs) {

        final String type = landPojo.getTypeIdOpt().map(idToType::get).orElse(null);
        final UUID landUUID = landPojo.getUUID();
        final String landName = landPojo.getName();
        final Map<Integer, Area> areas = new TreeMap<>();
        final Set<PlayerContainer> residents = new TreeSet<>();
        final Set<PlayerContainer> banneds = new TreeSet<>();
        final Map<PlayerContainer, TreeMap<PermissionType, Permission>> permissions = new TreeMap<>();
        final Set<Flag> flags = new HashSet<>();
        final Set<PlayerContainerPlayer> pNotifs = new TreeSet<>();

        // create owner (PlayerContainer)
        final PlayerContainer owner = getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType,
                landPojo.getOwnerId());
        if (owner == null) {
            log.log(Level.SEVERE, String.format("Invalid owner [land=%s]", landName));
            return null;
        }
        // Create areas
        for (final AreaPojo areaPojo : areaPojos) {
            final String areaTypeStr = idToAreaType.get(areaPojo.getAreaTypeId());
            final AreaType areaType;
            try {
                areaType = AreaType.valueOf(areaTypeStr);
            } catch (final IllegalArgumentException e) {
                log.log(Level.SEVERE, String.format("Invalid area type [land=%s, areaType=%s, areaId=%s]", landName,
                        areaTypeStr, areaPojo.getAreaId()), e);
                return null;
            }
            final Area area;
            switch (areaType) {
                case CUBOID:
                    area = new CuboidArea(areaPojo.isApproved(), areaPojo.getWorldName(), areaPojo.getX1(),
                            areaPojo.getY1(), areaPojo.getZ1(), areaPojo.getX2(), areaPojo.getY2(), areaPojo.getZ2());
                    break;
                case CYLINDER:
                    area = new CylinderArea(areaPojo.isApproved(), areaPojo.getWorldName(), areaPojo.getX1(),
                            areaPojo.getY1(), areaPojo.getZ1(), areaPojo.getX2(), areaPojo.getY2(), areaPojo.getZ2());
                    break;
                case ROAD:
                    final Map<Integer, Map<Integer, ChunkMatrix>> points = new HashMap<>();
                    for (final RoadMatrixPojo roadMatrixPojo : landUUIDToMatrices.get(landUUID)) {
                        final ChunkMatrix chunkMatrix = new ChunkMatrix(roadMatrixPojo.getMatrix());
                        points.computeIfAbsent(roadMatrixPojo.getChunkX(), k -> new HashMap<>())
                                .put(roadMatrixPojo.getChunkZ(), chunkMatrix);
                    }
                    area = new RoadArea(areaPojo.isApproved(), areaPojo.getWorldName(), areaPojo.getY1(),
                            areaPojo.getY2(), new RegionMatrix(points));
                    break;
                default:
                    // Impossible
                    area = null;
            }

            areas.put(areaPojo.getAreaId(), area);
        }
        if (areas.isEmpty()) {
            log.log(Level.SEVERE, String.format("No area for this land [land=%s]", landName));
            return null;
        }
        // Residents
        for (final int id : residentIds) {
            residents.add(getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, id));
        }
        // Banneds
        for (final int id : bannedIds) {
            banneds.add(getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, id));
        }
        // Create permissions
        for (final PermissionPojo permissionPojo : permissionPojos) {
            TreeMap<PermissionType, Permission> permPlayer;
            final PlayerContainer pc = getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType,
                    permissionPojo.getPlayerContainerId());
            final String permissionTypeStr = idToPermissionType.get(permissionPojo.getPermissionId());
            final PermissionType permType = secuboid.getPermissionsFlags().getPermissionTypeNoValid(permissionTypeStr);
            if (!permissions.containsKey(pc)) {
                permPlayer = new TreeMap<>();
                permissions.put(pc, permPlayer);
            } else {
                permPlayer = permissions.get(pc);
            }
            permPlayer.put(permType, secuboid.getPermissionsFlags().newPermission(permType, permissionPojo.getValue(),
                    permissionPojo.isInheritance()));
        }
        // Create flags
        for (final FlagPojo flagPojo : flagPojos) {
            final String flagTypeStr = idToFlagType.get(flagPojo.getFlagId());
            final FlagType flagType = secuboid.getPermissionsFlags().getFlagTypeNoValid(flagTypeStr);
            final Object valueObj = flagPojo.getValueBooleanOpt().map(v -> (Object) new Boolean(v))
                    .orElse(flagPojo.getValueDoubleOpt().map(v -> (Object) new Double(v))
                            .orElse(flagPojo.getValueStringOpt().map(v -> (Object) v).orElse(null)));
            if (valueObj == null) {
                log.log(Level.WARNING,
                        String.format("No value for a flag [land=%s, flagType=%s]", landName, flagTypeStr));
                continue;
            }
            flags.add(secuboid.getPermissionsFlags().newFlag(flagType, valueObj, flagPojo.isInheritance()));
        }
        // Players Notify
        for (final UUID playerUUID : playerNotifyUUIDs) {
            pNotifs.add(
                    (PlayerContainerPlayer) secuboid.getPlayerContainers().getOrAddPlayerContainerPlayer(playerUUID));
        }
        // Economy
        boolean forSale = landPojo.isForSale();
        Location forSaleSignLocNullable = null;
        if (forSale) {
            forSaleSignLocNullable = landPojo.getForSaleSignLocationOpt().map(StringChanges::stringToLocation)
                    .orElse(null);
            if (forSaleSignLocNullable == null) {
                log.log(Level.WARNING,
                        String.format("Land for sale with no suitable sign location [land=%s]", landName));
                forSale = false;
            }
        }
        boolean forRent = landPojo.getForRent();
        Location forRentSignLocNullable = null;
        if (forRent) {
            forRentSignLocNullable = landPojo.getForRentSignLocationOpt().map(StringChanges::stringToLocation)
                    .orElse(null);
            if (forSaleSignLocNullable == null) {
                log.log(Level.WARNING,
                        String.format("Land for rent with no suitable sign location [land=%s]", landName));
                forRent = false;
            }
        }
        // Create land
        final Iterator<Entry<Integer, Area>> it = areas.entrySet().iterator();
        final Entry<Integer, Area> firstEntry = it.next(); // Already verified
        final Land land;

        // Create land with first area
        try {
            land = secuboid.getLands().createLand(landName, landPojo.isApproved(), owner, firstEntry.getValue(), null,
                    firstEntry.getKey(), landUUID, secuboid.getTypes().addOrGetType(type));
        } catch (final SecuboidLandException e) {
            log.log(Level.SEVERE, String.format("Error on create land [land=%s]", landName), e);
            return null;
        }

        // next areas
        while (it.hasNext()) {
            final Entry<Integer, Area> entry = it.next();
            land.addArea(entry.getValue(), entry.getKey());
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
        land.setPriority(landPojo.getPriority());
        land.addMoney(landPojo.getMoney());
        for (final PlayerContainerPlayer pNotif : pNotifs) {
            land.addPlayerNotify(pNotif);
        }

        // Economy add
        if (forSale) {
            land.setForSale(true, landPojo.getSalePriceOpt().orElse(0d), forSaleSignLocNullable);
        }
        if (forRent) {
            land.setForRent(landPojo.getRentPriceOpt().orElse(0d), landPojo.getRentRenewOpt().orElse(7),
                    landPojo.getRentAutoRenewOpt().orElse(false), forRentSignLocNullable);
            landPojo.getTenantUUIDOpt().ifPresent(tenantUUID -> {
                final PlayerContainerPlayer tenant = (PlayerContainerPlayer) secuboid.getPlayerContainers()
                        .getOrAddPlayerContainerPlayer(tenantUUID);
                land.setRented(tenant);
                land.setLastPaymentTime(landPojo.getLastPaymentMillisOpt().orElse(0l));

            });
        }

        return new AbstractMap.SimpleEntry<Land, Optional<UUID>>(land, landPojo.getParentUUIDOpt());
    }
}