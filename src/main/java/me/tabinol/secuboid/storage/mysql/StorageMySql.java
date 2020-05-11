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
import java.util.ArrayList;
import java.util.Collections;
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
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.inventories.Inventories;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandLocation;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.areas.ChunkMatrix;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.RegionMatrix;
import me.tabinol.secuboid.lands.areas.RoadArea;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
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
import me.tabinol.secuboid.storage.mysql.dao.ApprovesDao;
import me.tabinol.secuboid.storage.mysql.dao.AreasDao;
import me.tabinol.secuboid.storage.mysql.dao.AreasRoadsMatricesDao;
import me.tabinol.secuboid.storage.mysql.dao.FlagsDao;
import me.tabinol.secuboid.storage.mysql.dao.GenericIdValueDao;
import me.tabinol.secuboid.storage.mysql.dao.InventoriesDeathsDao;
import me.tabinol.secuboid.storage.mysql.dao.InventoriesEntriesDao;
import me.tabinol.secuboid.storage.mysql.dao.InventoriesPotionEffectsDao;
import me.tabinol.secuboid.storage.mysql.dao.InventoriesSavesDao;
import me.tabinol.secuboid.storage.mysql.dao.LandsDao;
import me.tabinol.secuboid.storage.mysql.dao.PermissionsDao;
import me.tabinol.secuboid.storage.mysql.dao.PlayerContainersDao;
import me.tabinol.secuboid.storage.mysql.dao.SecuboidDao;
import me.tabinol.secuboid.storage.mysql.pojo.ApprovePojo;
import me.tabinol.secuboid.storage.mysql.pojo.AreaPojo;
import me.tabinol.secuboid.storage.mysql.pojo.FlagPojo;
import me.tabinol.secuboid.storage.mysql.pojo.InventoryEntryPojo;
import me.tabinol.secuboid.storage.mysql.pojo.InventoryPotionEffectPojo;
import me.tabinol.secuboid.storage.mysql.pojo.LandPojo;
import me.tabinol.secuboid.storage.mysql.pojo.PermissionPojo;
import me.tabinol.secuboid.storage.mysql.pojo.PlayerContainerPojo;
import me.tabinol.secuboid.storage.mysql.pojo.RoadMatrixPojo;
import me.tabinol.secuboid.utilities.DbUtils.SqlConsumer;

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
    private final ApprovesDao approvesDao;
    private final GenericIdValueDao<Integer, String> approvesActionsDao;
    private final GenericIdValueDao<UUID, String> playersDao;
    private final GenericIdValueDao<Integer, String> inventoriesDao;
    private final InventoriesEntriesDao inventoriesEntriesDao;
    private final GenericIdValueDao<Integer, Integer> inventoriesDefaultsDao;
    private final GenericIdValueDao<Integer, String> gameModesDao;
    private final InventoriesSavesDao inventoriesSavesDao;
    private final InventoriesDeathsDao inventoriesDeathsDao;
    private final InventoriesPotionEffectsDao inventoriesPotionEffectsDao;

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
        approvesDao = new ApprovesDao(dbConn);
        approvesActionsDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "approves_actions", "id",
                "name");
        playersDao = new GenericIdValueDao<>(dbConn, UUID.class, String.class, "players", "uuid", "name");
        inventoriesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "inventories", "id", "name");
        inventoriesEntriesDao = new InventoriesEntriesDao(dbConn);
        inventoriesDefaultsDao = new GenericIdValueDao<>(dbConn, Integer.class, Integer.class, "inventories_defaults",
                "inventory_id", "inventories_entries_id");
        gameModesDao = new GenericIdValueDao<>(dbConn, Integer.class, String.class, "game_modes", "id", "name");
        inventoriesSavesDao = new InventoriesSavesDao(dbConn);
        inventoriesDeathsDao = new InventoriesDeathsDao(dbConn);
        inventoriesPotionEffectsDao = new InventoriesPotionEffectsDao(dbConn);
    }

    @Override
    public boolean loadAll() {
        final boolean newDatabase;
        try {
            newDatabase = initDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            throw new SecuboidRuntimeException(
                    "Unable to create or access the database. Is the database up and running, user and password created and added in Secuboid configuration?",
                    e);
        }

        // Load all
        loadPlayersCache();
        loadLands();
        loadApproves();
        loadInventories();

        // Conversion needed?
        return newDatabase;
    }

    private boolean initDatabase() throws ClassNotFoundException, SQLException {
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

                return true;
            }
        }
        return false;
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
            idToAreaType = areasTypesDao.getIdToValue(conn);
            landPojos = landsDao.getLands(conn);
            idToType = landsTypesDao.getIdToValue(conn);
            idToPlayerContainerPojo = playerContainersDao.getIdToPlayerContainer(conn);
            idToPlayerContainerType = playerContainersTypesDao.getIdToValue(conn);
            landUUIDToResidentIds = landsResidentsDao.getIdToValues(conn);
            landUUIDToBannedIds = landsBannedDao.getIdToValues(conn);
            landUUIDToPermissionPojos = permissionsDao.getLandUUIDToPermissions(conn);
            idToPermissionType = permissionsTypesDao.getIdToValue(conn);
            landUUIDToFlagPojos = flagsDao.getLandUUIDToFlags(conn);
            idToFlagType = flagsTypesDao.getIdToValue(conn);
            landUUIDToPlayerNotifyUUIDs = playerNotifiesDao.getIdToValues(conn);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load lands from database", e);
            return;
        }

        for (final LandPojo landPojo : landPojos) {
            final Entry<Land, Optional<UUID>> orphanToParentUUIDOptEntry;
            final UUID landUUID = landPojo.getUUID();
            try {
                orphanToParentUUIDOptEntry = loadLand(landPojo, idToType, idToPlayerContainerPojo,
                        idToPlayerContainerType, landUUIDToAreas.getOrDefault(landUUID, Collections.emptyList()),
                        landUUIDToMatrices, idToAreaType,
                        landUUIDToResidentIds.getOrDefault(landUUID, Collections.emptyList()),
                        landUUIDToBannedIds.getOrDefault(landUUID, Collections.emptyList()),
                        landUUIDToPermissionPojos.getOrDefault(landUUID, Collections.emptyList()), idToPermissionType,
                        landUUIDToFlagPojos.getOrDefault(landUUID, Collections.emptyList()), idToFlagType,
                        landUUIDToPlayerNotifyUUIDs.getOrDefault(landUUID, Collections.emptyList()));
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
            final int ownerId = getOrAddPlayerContainer(conn, land.getOwner());

            // For sale
            final boolean isForSale = land.isForSale();
            final Optional<String> forSaleSignLocationOpt;
            final Optional<Double> salePriceOpt;
            if (isForSale) {
                forSaleSignLocationOpt = Optional.of(land.getSaleSignLoc().toFileFormat());
                salePriceOpt = Optional.of(land.getSalePrice());
            } else {
                forSaleSignLocationOpt = Optional.empty();
                salePriceOpt = Optional.empty();
            }

            // For rent
            final boolean isForRent = land.isForRent();
            final Optional<String> forRentSignLocationOpt;
            final Optional<Double> rentPriceOpt;
            final Optional<Integer> rentRenewOpt;
            final Optional<Boolean> rentAutoRenewOpt;
            final Optional<UUID> tenantUUIDOpt;
            final Optional<Long> lastPaymentMillisOpt;
            if (isForRent) {
                forRentSignLocationOpt = Optional.of(land.getRentSignLoc().toFileFormat());
                rentPriceOpt = Optional.of(land.getRentPrice());
                rentRenewOpt = Optional.of(land.getRentRenew());
                rentAutoRenewOpt = Optional.of(land.getRentAutoRenew());
                if (land.isRented()) {
                    final UUID tenantUUID = land.getTenant().getMinecraftUUID();
                    addUserInDatabaseIfNeeded(conn, tenantUUID);
                    tenantUUIDOpt = Optional.of(tenantUUID);
                    lastPaymentMillisOpt = Optional.of(land.getLastPaymentTime());
                } else {
                    tenantUUIDOpt = Optional.empty();
                    lastPaymentMillisOpt = Optional.empty();
                }
            } else {
                forRentSignLocationOpt = Optional.empty();
                rentPriceOpt = Optional.empty();
                rentRenewOpt = Optional.empty();
                rentAutoRenewOpt = Optional.empty();
                tenantUUIDOpt = Optional.empty();
                lastPaymentMillisOpt = Optional.empty();
            }

            // Update land table
            final Optional<UUID> parentUUIDOpt = Optional.ofNullable(land.getParent()).map(l -> l.getUUID());
            final LandPojo landPojo = new LandPojo(land.getUUID(), land.getName(), land.isApproved(), typeIdOpt,
                    ownerId, parentUUIDOpt, land.getPriority(), land.getMoney(), isForSale, forSaleSignLocationOpt,
                    salePriceOpt, isForRent, forRentSignLocationOpt, rentPriceOpt, rentRenewOpt, rentAutoRenewOpt,
                    tenantUUIDOpt, lastPaymentMillisOpt);
            landsDao.insertOrUpdateLand(conn, landPojo);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to save the land to database [landUUID=%s, landName=%s]",
                    land.getUUID(), land.getName()), e);
        }
    }

    @Override
    public void removeLand(final Land land) {
        try (final Connection conn = dbConn.openConnection()) {
            landsDao.deleteLand(conn, land.getUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to delete the land from database [landUUID=%s, landName=%s]",
                    land.getUUID(), land.getName()), e);
        }
    }

    @Override
    public void removeLandArea(final Land land, final Area area) {
        try (final Connection conn = dbConn.openConnection()) {
            final UUID landUUID = land.getUUID();
            final int areaId = area.getKey();
            if (area.getAreaType() == AreaType.ROAD) {
                areasRoadsMatricesDao.deleteRoadMatrix(conn, landUUID, areaId);
            }
            areasDao.deleteArea(conn, landUUID, areaId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to remove the land area from database [landUUID=%s, landName=%s, areaId=%s]",
                            land.getUUID(), land.getName(), area.getKey()),
                    e);
        }
    }

    @Override
    public void saveLandArea(final Land land, final Area area) {
        try (final Connection conn = dbConn.openConnection()) {
            final int areaTypeId = areasTypesDao.insertOrGetId(conn, area.getAreaType().name());
            final AreaPojo areaPojo = new AreaPojo(land.getUUID(), area.getKey(), area.isApproved(),
                    area.getWorldName(), areaTypeId, area.getX1(), area.getY1(), area.getZ1(), area.getX2(),
                    area.getY2(), area.getZ2());
            areasDao.insertOrUpdateArea(conn, areaPojo);

            // For road matrices
            if (area.getAreaType() == AreaType.ROAD) {
                for (final Map.Entry<Integer, Map<Integer, ChunkMatrix>> pointsEntry : ((RoadArea) area).getPoints()
                        .entrySet()) {
                    final int chunkX = pointsEntry.getKey();
                    for (final Map.Entry<Integer, ChunkMatrix> pointsZEntry : pointsEntry.getValue().entrySet()) {
                        final RoadMatrixPojo roadMatrixPojo = new RoadMatrixPojo(land.getUUID(), area.getKey(), chunkX,
                                pointsZEntry.getKey(), pointsZEntry.getValue().getMatrix());
                        areasRoadsMatricesDao.insertOrUpdateRoadMatrix(conn, roadMatrixPojo);
                    }
                }
            }
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the land area to database [landUUID=%s, landName=%s, areaId=%s]",
                            land.getUUID(), land.getName(), area.getKey()),
                    e);
        }
    }

    @Override
    public void removeLandBanned(final Land land, final PlayerContainer playerContainer) {
        try (final Connection conn = dbConn.openConnection()) {
            final int playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            landsBannedDao.delete(conn, land.getUUID(), playerContainerId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to remove the banned from the land from database [landUUID=%s, landName=%s, playerContainer=%s]",
                    land.getUUID(), land.getName(), playerContainer.toFileFormat()), e);
        }
    }

    @Override
    public void saveLandBanned(final Land land, final PlayerContainer playerContainer) {
        try (final Connection conn = dbConn.openConnection()) {
            final int playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            landsBannedDao.insert(conn, land.getUUID(), playerContainerId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to add the banned to the land to database [landUUID=%s, landName=%s, playerContainer=%s]",
                    land.getUUID(), land.getName(), playerContainer.toFileFormat()), e);
        }
    }

    @Override
    public void removeLandFlag(final Land land, final Flag flag) {
        try (final Connection conn = dbConn.openConnection()) {
            final int flagId = flagsTypesDao.insertOrGetId(conn, flag.getFlagType().getName());
            flagsDao.deleteFlag(conn, land.getUUID(), flagId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format(
                            "Unable to remove the flag from the land from database [landUUID=%s, landName=%s, flag=%s]",
                            land.getUUID(), land.getName(), flag.toFileFormat()),
                    e);
        }
    }

    @Override
    public void removeAllLandFlags(final Land land) {
        try (final Connection conn = dbConn.openConnection()) {
            flagsDao.deleteAllLandFlags(conn, land.getUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to remove all flags from the land from database [landUUID=%s, landName=%s]",
                            land.getUUID(), land.getName()),
                    e);
        }
    }

    @Override
    public void saveLandFlag(final Land land, final Flag flag) {
        try (final Connection conn = dbConn.openConnection()) {
            final int flagId = flagsTypesDao.insertOrGetId(conn, flag.getFlagType().getName());
            final Object flagValueObj = flag.getValue().getValue();
            final Optional<String> valueStringOpt = flagValueObj instanceof String ? Optional.of((String) flagValueObj)
                    : Optional.empty();
            final Optional<Double> valueDoubleOpt = flagValueObj instanceof Double ? Optional.of((Double) flagValueObj)
                    : Optional.empty();
            final Optional<Boolean> valueBooleanOpt = flagValueObj instanceof Boolean
                    ? Optional.of((Boolean) flagValueObj)
                    : Optional.empty();
            final FlagPojo flagPojo = new FlagPojo(land.getUUID(), flagId, valueStringOpt, valueDoubleOpt,
                    valueBooleanOpt, flag.isInheritable());
            flagsDao.insertOrUpdateFlag(conn, flagPojo);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to add the flag to the land to database [landUUID=%s, landName=%s, flag=%s]",
                            land.getUUID(), land.getName(), flag.toFileFormat()),
                    e);
        }
    }

    @Override
    public void removeLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        try (final Connection conn = dbConn.openConnection()) {
            final int playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            final int permissionId = permissionsTypesDao.insertOrGetId(conn, permission.getPermType().getName());
            permissionsDao.deletePermission(conn, land.getUUID(), playerContainerId, permissionId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to remove the permission from the land from database [landUUID=%s, landName=%s, playerContainer=%s, permission=%s]",
                    land.getUUID(), land.getName(), playerContainer.toFileFormat(), permission.toFileFormat()), e);
        }
    }

    @Override
    public void removeAllLandPermissions(final Land land) {
        try (final Connection conn = dbConn.openConnection()) {
            permissionsDao.deleteAllLandPermissions(conn, land.getUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format(
                            "Unable to remove all permissions from the land from database [landUUID=%s, landName=%s]",
                            land.getUUID(), land.getName()),
                    e);
        }
    }

    @Override
    public void saveLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        try (final Connection conn = dbConn.openConnection()) {
            final int playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            final int permissionId = permissionsTypesDao.insertOrGetId(conn, permission.getPermType().getName());
            final PermissionPojo permissionPojo = new PermissionPojo(land.getUUID(), playerContainerId, permissionId,
                    permission.getValue(), permission.isInheritable());
            permissionsDao.insertOrUpdatePermission(conn, permissionPojo);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to add the permission to the land to database [landUUID=%s, landName=%s, playerContainer=%s, permission=%s]",
                    land.getUUID(), land.getName(), playerContainer.toFileFormat(), permission.toFileFormat()), e);
        }
    }

    @Override
    public void removeLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        try (final Connection conn = dbConn.openConnection()) {
            playerNotifiesDao.delete(conn, land.getUUID(), pcp.getMinecraftUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to remove the player notify from the land from database [landUUID=%s, landName=%s, playerUUID=%s]",
                    land.getUUID(), land.getName(), pcp.getMinecraftUUID()), e);
        }
    }

    @Override
    public void removeAllLandPlayerNotify(final Land land) {
        try (final Connection conn = dbConn.openConnection()) {
            playerNotifiesDao.delete(conn, land.getUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to delete all player notifies from the land from database [landUUID=%s, landName=%s]",
                    land.getUUID(), land.getName()), e);
        }
    }

    @Override
    public void saveLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        final UUID playerUUID = pcp.getMinecraftUUID();
        try (final Connection conn = dbConn.openConnection()) {
            addUserInDatabaseIfNeeded(conn, playerUUID);
            playerNotifiesDao.insert(conn, land.getUUID(), playerUUID);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to add the player notify to the land to database [landUUID=%s, landName=%s, playerUUID=%s]",
                    land.getUUID(), land.getName(), playerUUID), e);
        }
    }

    @Override
    public void removeLandResident(final Land land, final PlayerContainer playerContainer) {
        try (final Connection conn = dbConn.openConnection()) {
            final int playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            landsResidentsDao.delete(conn, land.getUUID(), playerContainerId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to remove the resident from the land from database [landUUID=%s, landName=%s, playerContainer=%s]",
                    land.getUUID(), land.getName(), playerContainer.toFileFormat()), e);
        }
    }

    @Override
    public void removeAllLandResidents(final Land land) {
        try (final Connection conn = dbConn.openConnection()) {
            landsResidentsDao.delete(conn, land.getUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format(
                            "Unable to remove all the residents from the land from database [landUUID=%s, landName=%s]",
                            land.getUUID(), land.getName()),
                    e);
        }
    }

    @Override
    public void saveLandResident(final Land land, final PlayerContainer playerContainer) {
        try (final Connection conn = dbConn.openConnection()) {
            final int playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            landsResidentsDao.insert(conn, land.getUUID(), playerContainerId);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format(
                    "Unable to add the resident to the land to database [landUUID=%s, landName=%s, playerContainer=%s]",
                    land.getUUID(), land.getName(), playerContainer.toFileFormat()), e);
        }
    }

    @Override
    public void loadApproves() {
        try (final Connection conn = dbConn.openConnection()) {
            final Lands lands = secuboid.getLands();
            final Map<Integer, LandAction> idToActionName = approvesActionsDao.getIdToValue(conn).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> LandAction.valueOf(e.getValue())));
            final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo = playerContainersDao
                    .getIdToPlayerContainer(conn);
            final Map<Integer, String> idToPlayerContainerType = playerContainersTypesDao.getIdToValue(conn);

            final List<Approve> approves = new ArrayList<>();
            for (final ApprovePojo approvePojo : approvesDao.getApproves(conn)) {
                final Land land = lands.getLand(approvePojo.getLandUUID());
                final LandAction action = idToActionName.get(approvePojo.getApproveActionId());
                final PlayerContainer owner = getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType,
                        approvePojo.getOwnerId());
                final Optional<Land> parentOpt = approvePojo.getParentUUIDOpt().map(lands::getLand);
                approves.add(new Approve(land, action, approvePojo.getRemovedAreaIdOpt(), approvePojo.getNewAreaIdOpt(),
                        owner, parentOpt, approvePojo.getPrice(), approvePojo.getTransactionDatetime()));
            }
            lands.getApproves().loadApproves(approves);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load the approves from database", e);
        }
    }

    @Override
    public void saveApprove(final Approve approve) {
        try (final Connection conn = dbConn.openConnection()) {
            final int approveActionId = approvesActionsDao.insertOrGetId(conn, approve.getAction().name());
            final int ownerId = getOrAddPlayerContainer(conn, approve.getOwner());
            final Optional<UUID> parentUUIDOpt = approve.getParentOpt().map(Land::getUUID);
            final ApprovePojo approvePojo = new ApprovePojo(approve.getUUID(), approveActionId,
                    approve.getRemovedAreaIdOpt(), approve.getNewAreaIdOpt(), ownerId, parentUUIDOpt,
                    approve.getPrice(), approve.getDateTime());
            approvesDao.insertOrUpdateApprove(conn, approvePojo);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the approve to database [landUUID=%s]", approve.getUUID()), e);
        }
    }

    @Override
    public void removeApprove(final Approve approve) {
        try (final Connection conn = dbConn.openConnection()) {
            approvesDao.deleteApprove(conn, approve.getUUID());
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to delete the approve from database [landUUID=%s]", approve.getUUID()), e);
        }
    }

    @Override
    public void removeAllApproves() {
        try (final Connection conn = dbConn.openConnection()) {
            approvesDao.deleteAllApproves(conn);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to delete all approves from database", e);
        }
    }

    @Override
    public void loadInventories() {
        if (!secuboid.getInventoriesOpt().isPresent()) {
            return;
        }

        final Inventories inventories = secuboid.getInventoriesOpt().get();
        try (final Connection conn = dbConn.openConnection()) {
            final Map<Integer, String> idToInventoryName = inventoriesDao.getIdToValue(conn);
            for (final Map.Entry<Integer, Integer> inventoryIdToEntryIdEntry : inventoriesDefaultsDao.getIdToValue(conn)
                    .entrySet()) {
                final int inventoryId = inventoryIdToEntryIdEntry.getKey();
                final int inventoryEntryId = inventoryIdToEntryIdEntry.getValue();
                final InventoryEntryPojo inventoryEntryPojo = inventoriesEntriesDao.getInventoryEntry(conn,
                        inventoryEntryId);
                final String inventoryName = idToInventoryName.get(inventoryId);
                final InventorySpec inventorySpec = inventories.getInvSpec(inventoryName);
                final List<InventoryPotionEffectPojo> inventoryPotionEffectPojos = inventoriesPotionEffectsDao
                        .getPotionEffectsFromEntryId(conn, inventoryEntryId);
                final PlayerInvEntry playerInvEntry = loadInventoryFromPojo(inventoryEntryPojo,
                        inventoryPotionEffectPojos, Optional.empty(), inventorySpec, false);

                inventories.saveInventory(Optional.empty(), playerInvEntry, false, true, false);
            }
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load the inventories from database", e);
        }
    }

    @Override
    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDOpt(), false, false);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the inventory default to database %s", playerInvEntry.getName()), e);
        }
    }

    @Override
    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            final int inventoryId = inventoriesDao.insertOrGetId(conn,
                    playerInvEntry.getInventorySpec().getInventoryName());
            final Optional<Integer> inventoryEntryIdOpt = inventoriesDefaultsDao.getValueOpt(conn, inventoryId);
            if (inventoryEntryIdOpt.isPresent()) {
                inventoriesDefaultsDao.delete(conn, inventoryId);
                final int inventoryEntryId = inventoryEntryIdOpt.get();
                inventoriesEntriesDao.deleteInventoryEntry(conn, inventoryEntryId);
            }
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to remove the inventory default from database %s", playerInvEntry.getName()),
                    e);
        }
    }

    @Override
    public void loadInventoriesPlayer(final PlayerInventoryCache playerInventoryCache) {
        final Inventories inventories = secuboid.getInventoriesOpt().get();

        try (final Connection conn = dbConn.openConnection()) {
            for (final InventorySpec inventorySpec : inventories.getInvSpecs()) {
                final int inventoryId = inventoriesDao.insertOrGetId(conn, inventorySpec.getInventoryName());

                // Survival
                createInventoriesEntryPlayerOpt(conn, playerInventoryCache, inventoryId, inventorySpec, false,
                        Optional.empty())
                                .ifPresent(e -> inventories.saveInventory(Optional.empty(), e, false, false, false));

                // Creative
                createInventoriesEntryPlayerOpt(conn, playerInventoryCache, inventoryId, inventorySpec, true,
                        Optional.empty())
                                .ifPresent(e -> inventories.saveInventory(Optional.empty(), e, false, false, false));

                // Death
                for (int deathVersion = PlayerInventoryCache.DEATH_SAVE_MAX_NBR; deathVersion > 0; deathVersion--) {
                    createInventoriesEntryPlayerOpt(conn, playerInventoryCache, inventoryId, inventorySpec, false,
                            Optional.of(deathVersion))
                                    .ifPresent(e -> inventories.saveInventory(Optional.empty(), e, true, false, false));
                }
            }
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to load the inventory from database [name=%s]",
                    playerInventoryCache.getName()), e);
        }
    }

    @Override
    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDOpt(), false, false);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the player inventory to database %s", playerInvEntry.getName()), e);
        }
    }

    @Override
    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDOpt(), false, true);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the player death inventory to database %s", playerInvEntry.getName()),
                    e);
        }
    }

    @Override
    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDOpt(), true, false);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to save the player death inventory history to database %s",
                    playerInvEntry.getName()), e);
        }
    }

    @Override
    public void loadPlayersCache() {
        try (final Connection conn = dbConn.openConnection()) {
            final Map<UUID, String> idToName = playersDao.getIdToValue(conn);
            final List<PlayerCacheEntry> playerCacheEntries = idToName.entrySet().stream()
                    .map(e -> new PlayerCacheEntry(e.getKey(), e.getValue())).collect(Collectors.toList());
            secuboid.getPlayersCache().loadPlayerscache(playerCacheEntries);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load the player cache from database", e);
        }
    }

    @Override
    public void savePlayerCacheEntry(final PlayerCacheEntry playerCacheEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            playersDao.insertOrUpdate(conn, playerCacheEntry.getUUID(), playerCacheEntry.getName());
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to save the player cache to database [uuid=%s, name=%s]",
                    playerCacheEntry.getUUID(), playerCacheEntry.getName()), e);
        }
    }

    private PlayerContainer getPlayerContainer(final Map<Integer, PlayerContainerPojo> idToPlayerContainerPojo,
            final Map<Integer, String> idToPlayerContainerType, final int id) {
        final PlayerContainerPojo playerContainerPojo = idToPlayerContainerPojo.get(id);
        final String PlayerContainerTypeStr = idToPlayerContainerType
                .get(playerContainerPojo.getPlayerContainerTypeId());
        final PlayerContainerType playerContainerType = PlayerContainerType.getFromString(PlayerContainerTypeStr);

        return secuboid.getPlayerContainers().getOrAddPlayerContainer(playerContainerType,
                playerContainerPojo.getParameterOpt(), playerContainerPojo.getPlayerUUIDOpt());
    }

    private int getOrAddPlayerContainer(final Connection conn, final PlayerContainer playerContainer)
            throws SQLException {
        final PlayerContainerType playerContainerType = playerContainer.getContainerType();
        final int playerContainerTypeId = playerContainersTypesDao.insertOrGetId(conn, playerContainerType.name());

        if (playerContainerType.hasParameter()) {
            if (playerContainerType == PlayerContainerType.PLAYER) {
                final UUID playerUUID = ((PlayerContainerPlayer) playerContainer).getMinecraftUUID();
                addUserInDatabaseIfNeeded(conn, playerUUID);
                return playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId,
                        Optional.of(playerUUID), Optional.empty());
            }
            return playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId, Optional.empty(),
                    Optional.of(playerContainer.getName()));
        }
        return playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId, Optional.empty(),
                Optional.empty());
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
        LandLocation forSaleSignLocNullable = null;
        if (forSale) {
            forSaleSignLocNullable = landPojo.getForSaleSignLocationOpt().map(LandLocation::fromFileFormat)
                    .orElse(null);
            if (forSaleSignLocNullable == null) {
                log.log(Level.WARNING,
                        String.format("Land for sale with no suitable sign location [land=%s]", landName));
                forSale = false;
            }
        }
        boolean forRent = landPojo.getForRent();
        LandLocation forRentSignLocNullable = null;
        if (forRent) {
            forRentSignLocNullable = landPojo.getForRentSignLocationOpt().map(LandLocation::fromFileFormat)
                    .orElse(null);
            if (forRentSignLocNullable == null) {
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

    private PlayerInvEntry loadInventoryFromPojo(final InventoryEntryPojo inventoryEntryPojo,
            final List<InventoryPotionEffectPojo> inventoryPotionEffectPojos,
            final Optional<PlayerInventoryCache> playerInventoryCacheOpt, final InventorySpec inventorySpec,
            final boolean isCreative) {
        final PlayerInvEntry playerInvEntry = new PlayerInvEntry(playerInventoryCacheOpt, inventorySpec, isCreative);

        playerInvEntry.setLevel(inventoryEntryPojo.getLevel());
        playerInvEntry.setExp(inventoryEntryPojo.getExp());

        playerInvEntry.setHealth(inventoryEntryPojo.getHealth());
        playerInvEntry.setFoodLevel(inventoryEntryPojo.getFoodLevel());

        playerInvEntry.setSlotItems(inventoryEntryPojo.getContents());
        playerInvEntry.setEnderChestItems(inventoryEntryPojo.getEnderChestContents());

        // PotionsEffects
        for (final InventoryPotionEffectPojo inventoryPotionEffectPojo : inventoryPotionEffectPojos) {
            final PotionEffectType type = PotionEffectType.getByName(inventoryPotionEffectPojo.getName());
            playerInvEntry.addPotionEffect(new PotionEffect(type, inventoryPotionEffectPojo.getDuration(),
                    inventoryPotionEffectPojo.getAmplifier(), inventoryPotionEffectPojo.isAmbient()));

        }

        return playerInvEntry;
    }

    public void saveInventory(final Connection conn, final PlayerInvEntry playerInvEntry,
            final Optional<UUID> playerUUIDOpt, final boolean isDeathHistory, final boolean enderChestOnly)
            throws SQLException {
        final InventorySpec inventorySpec = playerInvEntry.getInventorySpec();

        if (playerUUIDOpt.isPresent()) {
            addUserInDatabaseIfNeeded(conn, playerUUIDOpt.get());
        } else {
            // If for some reasons whe have to skip save (ex: SaveInventory = false)
            if (!inventorySpec.isSaveInventory()) {
                return;
            }
        }

        // Get the suffix name
        final int gameModeId = gameModesDao.insertOrGetId(conn, getGameModeFromBoolean(playerInvEntry.isCreativeInv()));
        final int inventoryId = inventoriesDao.insertOrGetId(conn,
                playerInvEntry.getInventorySpec().getInventoryName());

        if (isDeathHistory && playerUUIDOpt.isPresent()) {
            // Save death inventory
            final UUID playerUUID = playerUUIDOpt.get();

            // Death rename
            final Optional<Integer> inventoryEntryId9Opt = inventoriesDeathsDao.getEntryIdOpt(conn, playerUUID,
                    inventoryId, gameModeId, 9);
            inventoriesDeathsDao.deleteNinth(conn, playerUUID, inventoryId, gameModeId);
            if (inventoryEntryId9Opt.isPresent()) {
                inventoriesEntriesDao.deleteInventoryEntry(conn, inventoryEntryId9Opt.get());
            }
            for (int t = 8; t >= 1; t--) {
                inventoriesDeathsDao.incrementDeathNumber(conn, playerUUID, inventoryId, gameModeId, t);
            }
            final Optional<Integer> inventoryEntryIdOpt = inventoriesDeathsDao.getEntryIdOpt(conn, playerUUID,
                    inventoryId, gameModeId, 1);
            saveInventoryEntry(conn, playerInvEntry, enderChestOnly, inventoryEntryIdOpt,
                    i -> inventoriesDeathsDao.insertInventoryDeath(conn, playerUUID, inventoryId, gameModeId, 1, i));

        } else if (!playerUUIDOpt.isPresent()) {
            // Save default inventory
            final Optional<Integer> inventoryEntryIdOpt = inventoriesDefaultsDao.getValueOpt(conn, inventoryId);
            saveInventoryEntry(conn, playerInvEntry, enderChestOnly, inventoryEntryIdOpt,
                    i -> inventoriesDefaultsDao.insert(conn, inventoryId, i));

        } else {
            // Save normal inventory
            final UUID playerUUID = playerUUIDOpt.get();
            final Optional<Integer> inventoryEntryIdOpt = inventoriesSavesDao.getEntryIdOpt(conn, playerUUID,
                    inventoryId, gameModeId);
            saveInventoryEntry(conn, playerInvEntry, enderChestOnly, inventoryEntryIdOpt,
                    i -> inventoriesSavesDao.insertInventorySave(conn, playerUUID, inventoryId, gameModeId, i));
        }
    }

    private void saveInventoryEntry(final Connection conn, final PlayerInvEntry playerInvEntry,
            final boolean enderChestOnly, final Optional<Integer> inventoryEntryIdOpt,
            final SqlConsumer<Integer> updateInvEntryIdConsumer) throws SQLException {

        final int level;
        final float exp;
        final double health;
        final int foodLevel;
        final ItemStack[] contents;
        final ItemStack[] enderChestContents = playerInvEntry.getEnderChestItems();

        if (enderChestOnly) {
            // Save Only ender chest (Death)
            level = 0;
            exp = 0f;
            health = PlayerInvEntry.MAX_HEALTH;
            foodLevel = PlayerInvEntry.MAX_FOOD_LEVEL;
            contents = new ItemStack[PlayerInvEntry.INVENTORY_LIST_SIZE];
        } else {
            // Save all
            level = playerInvEntry.getLevel();
            exp = playerInvEntry.getExp();
            health = playerInvEntry.getHealth();
            foodLevel = playerInvEntry.getFoodLevel();
            contents = playerInvEntry.getSlotItems();
        }

        // If the entry id does not exist, a new entry will be created.
        final int inventoryEntryId;
        if (inventoryEntryIdOpt.isPresent()) {
            inventoryEntryId = inventoryEntryIdOpt.get();

            // Remove potions
            inventoriesPotionEffectsDao.deletePotionEffectsFromEntryId(conn, inventoryEntryId);

            // Update inventory entry
            final InventoryEntryPojo inventoryEntryPojo = new InventoryEntryPojo(inventoryEntryId, level, exp, health,
                    foodLevel, contents, enderChestContents);
            inventoriesEntriesDao.updateInventoryEntry(conn, inventoryEntryPojo);
        } else {
            // Create inventory entry
            inventoryEntryId = inventoriesEntriesDao.insertInventoryEntry(conn, level, exp, health, foodLevel, contents,
                    enderChestContents);

            // Insert a new entry in upstream table
            updateInvEntryIdConsumer.accept(inventoryEntryId);
        }

        // PotionsEffects
        if (inventoryEntryIdOpt.isPresent()) {
            inventoriesPotionEffectsDao.deletePotionEffectsFromEntryId(conn, inventoryEntryIdOpt.get());
        }
        if (!enderChestOnly) {
            final List<PotionEffect> activePotionEffects = playerInvEntry.getPotionEffects();
            for (final PotionEffect effect : activePotionEffects) {
                final InventoryPotionEffectPojo inventoryPotionEffectPojo = new InventoryPotionEffectPojo(
                        inventoryEntryId, effect.getType().getName(), effect.getDuration(), effect.getAmplifier(),
                        effect.isAmbient());
                inventoriesPotionEffectsDao.addPotionEffectsFromEntryId(conn, inventoryPotionEffectPojo);
            }
        }
    }

    private Optional<PlayerInvEntry> createInventoriesEntryPlayerOpt(final Connection conn,
            final PlayerInventoryCache playerInventoryCache, final int inventoryId, final InventorySpec inventorySpec,
            final boolean isCreative, final Optional<Integer> deathVersionOpt) throws SQLException {
        final int gameModeId = gameModesDao.insertOrGetId(conn, getGameModeFromBoolean(isCreative));
        final UUID playerUUID = playerInventoryCache.getUUID();

        final Optional<Integer> inventoryEntryIdOpt;
        if (deathVersionOpt.isPresent()) {
            // Death load
            final int deathVersion = deathVersionOpt.get();
            inventoryEntryIdOpt = inventoriesDeathsDao.getEntryIdOpt(conn, playerUUID, inventoryId, gameModeId,
                    deathVersion);

        } else {
            // Normal load
            inventoryEntryIdOpt = inventoriesSavesDao.getEntryIdOpt(conn, playerUUID, inventoryId, gameModeId);
        }

        if (inventoryEntryIdOpt.isPresent()) {
            final int inventoryEntryId = inventoryEntryIdOpt.get();
            final InventoryEntryPojo inventoryEntryPojo = inventoriesEntriesDao.getInventoryEntry(conn,
                    inventoryEntryId);
            final List<InventoryPotionEffectPojo> inventoryPotionEffectPojos = inventoriesPotionEffectsDao
                    .getPotionEffectsFromEntryId(conn, inventoryEntryId);

            return Optional.of(loadInventoryFromPojo(inventoryEntryPojo, inventoryPotionEffectPojos,
                    Optional.of(playerInventoryCache), inventorySpec, isCreative));
        }
        return Optional.empty();
    }

    private String getGameModeFromBoolean(final boolean isCreative) {
        return isCreative ? "CREATIVE" : "SURVIVAL";
    }

    private void addUserInDatabaseIfNeeded(Connection conn, UUID playerUUID) throws SQLException {
        Optional<String> playerNameOpt = playersDao.getValueOpt(conn, playerUUID);
        if (!playerNameOpt.isPresent()) {
            log.warning("Player " + playerUUID + " is not present in players cache. Adding the UUID as player name.");
            playersDao.insert(conn, playerUUID, playerUUID.toString());
        }
    }
}