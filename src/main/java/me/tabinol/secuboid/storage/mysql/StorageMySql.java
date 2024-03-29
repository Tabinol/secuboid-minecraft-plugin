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
import me.tabinol.secuboid.storage.mysql.dao.FlagsValuesListDao;
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
    private final GenericIdValueDao<Long, String> areasTypesDao;
    private final LandsDao landsDao;
    private final GenericIdValueDao<Long, String> landsTypesDao;
    private final PlayerContainersDao playerContainersDao;
    private final GenericIdValueDao<Long, String> playerContainersTypesDao;
    private final GenericIdValueDao<UUID, Long> landsResidentsDao;
    private final GenericIdValueDao<UUID, Long> landsBannedDao;
    private final PermissionsDao permissionsDao;
    private final GenericIdValueDao<Long, String> permissionsTypesDao;
    private final FlagsDao flagsDao;
    private final GenericIdValueDao<Long, String> flagsTypesDao;
    private final GenericIdValueDao<Long, String> flagsValuesStringDao;
    private final GenericIdValueDao<Long, Double> flagsValuesDoubleDao;
    private final GenericIdValueDao<Long, Boolean> flagsValuesBooleanDao;
    private final FlagsValuesListDao flagsValuesListDao;
    private final GenericIdValueDao<UUID, UUID> playerNotifiesDao;
    private final ApprovesDao approvesDao;
    private final GenericIdValueDao<Long, String> approvesActionsDao;
    private final GenericIdValueDao<UUID, String> playersDao;
    private final GenericIdValueDao<Long, String> inventoriesDao;
    private final InventoriesEntriesDao inventoriesEntriesDao;
    private final GenericIdValueDao<Long, Long> inventoriesDefaultsDao;
    private final GenericIdValueDao<Long, String> gameModesDao;
    private final InventoriesSavesDao inventoriesSavesDao;
    private final InventoriesDeathsDao inventoriesDeathsDao;
    private final InventoriesPotionEffectsDao inventoriesPotionEffectsDao;

    public StorageMySql(final Secuboid secuboid, final DatabaseConnection dbConn) {
        this.secuboid = secuboid;
        this.dbConn = dbConn;
        log = secuboid.getLogger();

        areasDao = new AreasDao(dbConn);
        areasRoadsMatricesDao = new AreasRoadsMatricesDao(dbConn);
        areasTypesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "areas_types", "id", "name");
        landsDao = new LandsDao(dbConn);
        landsTypesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "lands_types", "id", "name");
        playerContainersDao = new PlayerContainersDao(dbConn);
        playerContainersTypesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "player_containers_types",
                "id", "name");
        landsResidentsDao = new GenericIdValueDao<>(dbConn, UUID.class, Long.class, "lands_residents", "land_uuid",
                "player_container_id");
        landsBannedDao = new GenericIdValueDao<>(dbConn, UUID.class, Long.class, "lands_banneds", "land_uuid",
                "player_container_id");
        permissionsDao = new PermissionsDao(dbConn);
        permissionsTypesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "permissions", "id", "name");
        flagsDao = new FlagsDao(dbConn);
        flagsTypesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "flags", "id", "name");
        flagsValuesStringDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "lands_flags_values_string",
                "land_flag_id", "value_string");
        flagsValuesDoubleDao = new GenericIdValueDao<>(dbConn, Long.class, Double.class, "lands_flags_values_double",
                "land_flag_id", "value_double");
        flagsValuesBooleanDao = new GenericIdValueDao<>(dbConn, Long.class, Boolean.class, "lands_flags_values_boolean",
                "land_flag_id", "value_boolean");
        flagsValuesListDao = new FlagsValuesListDao(dbConn);
        playerNotifiesDao = new GenericIdValueDao<>(dbConn, UUID.class, UUID.class, "lands_players_notifies",
                "land_uuid", "player_uuid");
        approvesDao = new ApprovesDao(dbConn);
        approvesActionsDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "approves_actions", "id",
                "name");
        playersDao = new GenericIdValueDao<>(dbConn, UUID.class, String.class, "players", "uuid", "name");
        inventoriesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "inventories", "id", "name");
        inventoriesEntriesDao = new InventoriesEntriesDao(dbConn);
        inventoriesDefaultsDao = new GenericIdValueDao<>(dbConn, Long.class, Long.class, "inventories_defaults",
                "inventory_id", "inventories_entries_id");
        gameModesDao = new GenericIdValueDao<>(dbConn, Long.class, String.class, "game_modes", "id", "name");
        inventoriesSavesDao = new InventoriesSavesDao(dbConn);
        inventoriesDeathsDao = new InventoriesDeathsDao(dbConn);
        inventoriesPotionEffectsDao = new InventoriesPotionEffectsDao(dbConn);
    }

    @Override
    public boolean loadAll() {
        final boolean newDatabase;
        try {
            newDatabase = initDatabase();
        } catch (final ClassNotFoundException | SQLException e) {
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
            final Integer versionNullable = secuboidDao.getVersionNullable(conn);
            if (versionNullable == null || versionNullable < 1) {
                // Create database
                log.info("Creating tables...");
                secuboidDao.initDatabase(conn);
                secuboidDao.setVersion(conn);
                log.info("Creating tables done.");

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
        final Map<Long, String> idToAreaType;
        final List<LandPojo> landPojos;
        final Map<Long, String> idToType;
        final Map<Long, PlayerContainerPojo> idToPlayerContainerPojo;
        final Map<Long, String> idToPlayerContainerType;
        final Map<UUID, List<Long>> landUUIDToResidentIds;
        final Map<UUID, List<Long>> landUUIDToBannedIds;
        final Map<UUID, List<PermissionPojo>> landUUIDToPermissionPojos;
        final Map<Long, String> idToPermissionType;
        final Map<UUID, List<FlagPojo>> landUUIDToFlagPojos;
        final Map<Long, String> landFlagIdtoValueString;
        final Map<Long, Double> landFlagIdtoValueDouble;
        final Map<Long, Boolean> landFlagIdtoValueBoolean;
        final Map<Long, List<String>> landFlagIdtoValueList;
        final Map<Long, String> idToFlagType;
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
            landFlagIdtoValueString = flagsValuesStringDao.getIdToValue(conn);
            landFlagIdtoValueDouble = flagsValuesDoubleDao.getIdToValue(conn);
            landFlagIdtoValueBoolean = flagsValuesBooleanDao.getIdToValue(conn);
            landFlagIdtoValueList = flagsValuesListDao.getLandFlagIdToValueList(conn);
            idToFlagType = flagsTypesDao.getIdToValue(conn);
            landUUIDToPlayerNotifyUUIDs = playerNotifiesDao.getIdToValues(conn);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load lands from database", e);
            return;
        }

        for (final LandPojo landPojo : landPojos) {
            final Entry<Land, UUID> orphanToParentUUIDNullableEntry;
            final UUID landUUID = landPojo.getUUID();
            try {
                orphanToParentUUIDNullableEntry = loadLand(landPojo, idToType, idToPlayerContainerPojo,
                        idToPlayerContainerType, landUUIDToAreas.getOrDefault(landUUID, Collections.emptyList()),
                        landUUIDToMatrices, idToAreaType,
                        landUUIDToResidentIds.getOrDefault(landUUID, Collections.emptyList()),
                        landUUIDToBannedIds.getOrDefault(landUUID, Collections.emptyList()),
                        landUUIDToPermissionPojos.getOrDefault(landUUID, Collections.emptyList()), idToPermissionType,
                        landUUIDToFlagPojos.getOrDefault(landUUID, Collections.emptyList()), landFlagIdtoValueString,
                        landFlagIdtoValueDouble, landFlagIdtoValueBoolean, landFlagIdtoValueList, idToFlagType,
                        landUUIDToPlayerNotifyUUIDs.getOrDefault(landUUID, Collections.emptyList()));
            } catch (final RuntimeException e) {
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Unable to load the land from database: %s", landPojo.getName()), e);
                continue;
            }
            if (orphanToParentUUIDNullableEntry != null) {
                loadedlands++;
                final UUID parentUUIDNullable = orphanToParentUUIDNullableEntry.getValue();
                if (parentUUIDNullable != null) {
                    orphanToParentUUID.put(orphanToParentUUIDNullableEntry.getKey(), parentUUIDNullable);
                }
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
            final Long typeIdNullable;
            if (type != null) {
                typeIdNullable = landsTypesDao.insertOrGetId(conn, type.getName());
            } else {
                typeIdNullable = null;
            }

            // Get ownerId
            final long ownerId = getOrAddPlayerContainer(conn, land.getOwner());

            // For sale
            final boolean isForSale = land.isForSale();
            final String forSaleSignLocationNullable;
            final Double salePriceNullable;
            if (isForSale) {
                forSaleSignLocationNullable = land.getSaleSignLoc().toFileFormat();
                salePriceNullable = land.getSalePrice();
            } else {
                forSaleSignLocationNullable = null;
                salePriceNullable = null;
            }

            // For rent
            final boolean isForRent = land.isForRent();
            final String forRentSignLocationNullable;
            final Double rentPriceNullable;
            final Integer rentRenewNullable;
            final Boolean rentAutoRenewNullable;
            final UUID tenantUUIDNullable;
            final Long lastPaymentMillisNullable;
            if (isForRent) {
                forRentSignLocationNullable = land.getRentSignLoc().toFileFormat();
                rentPriceNullable = land.getRentPrice();
                rentRenewNullable = land.getRentRenew();
                rentAutoRenewNullable = land.getRentAutoRenew();
                if (land.isRented()) {
                    final UUID tenantUUID = land.getTenant().getMinecraftUUID();
                    addUserInDatabaseIfNeeded(conn, tenantUUID);
                    tenantUUIDNullable = tenantUUID;
                    lastPaymentMillisNullable = land.getLastPaymentTime();
                } else {
                    tenantUUIDNullable = null;
                    lastPaymentMillisNullable = null;
                }
            } else {
                forRentSignLocationNullable = null;
                rentPriceNullable = null;
                rentRenewNullable = null;
                rentAutoRenewNullable = null;
                tenantUUIDNullable = null;
                lastPaymentMillisNullable = null;
            }

            // Update land table
            final UUID parentUUIDNullable = Optional.ofNullable(land.getParent()).map(Land::getUUID).orElse(null);
            final LandPojo landPojo = new LandPojo(land.getUUID(), land.getName(), land.isApproved(), typeIdNullable,
                    ownerId, parentUUIDNullable, land.getPriority(), land.getMoney(), isForSale, forSaleSignLocationNullable,
                    salePriceNullable, isForRent, forRentSignLocationNullable, rentPriceNullable, rentRenewNullable, rentAutoRenewNullable,
                    tenantUUIDNullable, lastPaymentMillisNullable);
            landsDao.insertOrUpdateLand(conn, landPojo);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to save the land to database [landUUID=%s, landName=%s]",
                    land.getUUID(), land.getName()), e);
        }
    }

    @Override
    public void removeLand(final Land land) {
        try (final Connection conn = dbConn.openConnection()) {
            // Remove areas
            for (Area area : land.getAreas()) {
                removeLandArea(land, area);
            }

            // Remove land
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
            final long areaTypeId = areasTypesDao.insertOrGetId(conn, area.getAreaType().name());
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
            final long playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
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
            final long playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
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
            final long flagId = flagsTypesDao.insertOrGetId(conn, flag.getFlagType().getName());
            final Object flagValueObj = flag.getValue().getValue();

            // Delete flag before (foreign key)
            final Long landFlagIdNullable = flagsDao.getLandFlagIdNullable(conn, land.getUUID(), flagId);
            if (landFlagIdNullable != null) {
                if (flagValueObj instanceof String) {
                    flagsValuesStringDao.delete(conn, landFlagIdNullable);
                } else if (flagValueObj instanceof Double) {
                    flagsValuesDoubleDao.delete(conn, landFlagIdNullable);
                } else if (flagValueObj instanceof Boolean) {
                    flagsValuesBooleanDao.delete(conn, landFlagIdNullable);
                } else if (flagValueObj instanceof String[]) {
                    flagsValuesListDao.deleteLandFlagValueList(conn, landFlagIdNullable);
                }
            }

            // Delete flag
            flagsDao.deleteLandFlag(conn, land.getUUID(), flagId);
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
        final UUID landUUID = land.getUUID();
        try (final Connection conn = dbConn.openConnection()) {
            final Set<Long> flagIds = flagsDao.getLandFlagIds(conn, landUUID);
            for (final long flagId : flagIds) {
                flagsValuesBooleanDao.delete(conn, flagId);
                flagsValuesStringDao.delete(conn, flagId);
                flagsValuesDoubleDao.delete(conn, flagId);
                flagsValuesListDao.deleteLandFlagValueList(conn, flagId);
            }
            flagsDao.deleteAllLandFlags(conn, landUUID);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to remove all flags from the land from database [landUUID=%s, landName=%s]",
                            landUUID, land.getName()),
                    e);
        }
    }

    @Override
    public void saveLandFlag(final Land land, final Flag flag) {
        try (final Connection conn = dbConn.openConnection()) {
            final long flagId = flagsTypesDao.insertOrGetId(conn, flag.getFlagType().getName());

            // Insert flag first (foreign key)
            final long landFlagId = flagsDao.insertFlagOrUpdateGetId(conn, land.getUUID(), flagId,
                    flag.isInheritable());

            // Add flag value
            final Object flagValueObj = flag.getValue().getValue();
            if (flagValueObj instanceof String) {
                flagsValuesStringDao.insertOrUpdate(conn, landFlagId, (String) flagValueObj);
            } else if (flagValueObj instanceof Double) {
                flagsValuesDoubleDao.insertOrUpdate(conn, landFlagId, (Double) flagValueObj);
            } else if (flagValueObj instanceof Boolean) {
                flagsValuesBooleanDao.insertOrUpdate(conn, landFlagId, (Boolean) flagValueObj);
            } else if (flagValueObj instanceof String[]) {
                flagsValuesListDao.deleteLandFlagValueList(conn, landFlagId);
                flagsValuesListDao.insertLandFlagValueListItem(conn, landFlagId, (String[]) flagValueObj);
            }
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
            final long playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            final long permissionId = permissionsTypesDao.insertOrGetId(conn, permission.getPermType().getName());
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
            final long playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            final long permissionId = permissionsTypesDao.insertOrGetId(conn, permission.getPermType().getName());
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
            final long playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
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
            final long playerContainerId = getOrAddPlayerContainer(conn, playerContainer);
            landsResidentsDao.insertIgnore(conn, land.getUUID(), playerContainerId);
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
            final Map<Long, LandAction> idToActionName = approvesActionsDao.getIdToValue(conn).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> LandAction.valueOf(e.getValue())));
            final Map<Long, PlayerContainerPojo> idToPlayerContainerPojo = playerContainersDao
                    .getIdToPlayerContainer(conn);
            final Map<Long, String> idToPlayerContainerType = playerContainersTypesDao.getIdToValue(conn);

            final List<Approve> approves = new ArrayList<>();
            for (final ApprovePojo approvePojo : approvesDao.getApproves(conn)) {
                final Land land = lands.getLand(approvePojo.getLandUUID());
                final LandAction action = idToActionName.get(approvePojo.getApproveActionId());
                final PlayerContainer owner = getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType,
                        approvePojo.getOwnerId());
                final Land parentNullable = Optional.ofNullable(approvePojo.getParentUUIDNullable()).map(lands::getLand).orElse(null);
                approves.add(new Approve(land, action, approvePojo.getRemovedAreaIdNullable(), approvePojo.getNewAreaIdNullable(),
                        owner, parentNullable, approvePojo.getPrice(), approvePojo.getTransactionDatetime()));
            }
            lands.getApproves().loadApproves(approves);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load the approves from database", e);
        }
    }

    @Override
    public void saveApprove(final Approve approve) {
        try (final Connection conn = dbConn.openConnection()) {
            final long approveActionId = approvesActionsDao.insertOrGetId(conn, approve.getAction().name());
            final long ownerId = getOrAddPlayerContainer(conn, approve.getOwner());
            final UUID parentUUIDNullable = Optional.ofNullable(approve.getParentNullable()).map(Land::getUUID).orElse(null);
            final ApprovePojo approvePojo = new ApprovePojo(approve.getUUID(), approveActionId,
                    approve.getRemovedAreaIdNullable(), approve.getNewAreaIdNullable(), ownerId, parentUUIDNullable,
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
            // Prior 1.6.0 to lowercase
            inventoriesDao.valueToLowerCase(conn);

            final Map<Long, String> idToInventoryName = inventoriesDao.getIdToValue(conn);
            for (final Map.Entry<Long, Long> inventoryIdToEntryIdEntry : inventoriesDefaultsDao.getIdToValue(conn)
                    .entrySet()) {
                final long inventoryId = inventoryIdToEntryIdEntry.getKey();
                final long inventoryEntryId = inventoryIdToEntryIdEntry.getValue();
                final InventoryEntryPojo inventoryEntryPojo = inventoriesEntriesDao.getInventoryEntry(conn,
                        inventoryEntryId);
                final String inventoryName = idToInventoryName.get(inventoryId);
                final InventorySpec inventorySpec = inventories.getInvSpec(inventoryName);
                final List<InventoryPotionEffectPojo> inventoryPotionEffectPojos = inventoriesPotionEffectsDao
                        .getPotionEffectsFromEntryId(conn, inventoryEntryId);
                final PlayerInvEntry playerInvEntry = loadInventoryFromPojo(inventoryEntryPojo,
                        inventoryPotionEffectPojos, null, inventorySpec, false);

                inventories.saveInventory(null, playerInvEntry, false, true, false);
            }
        } catch (final SQLException e) {
            log.log(Level.SEVERE, "Unable to load the inventories from database", e);
        }
    }

    @Override
    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), false, false, true);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the inventory default to database %s", playerInvEntry.getName()), e);
        }
    }

    @Override
    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            final long inventoryId = inventoriesDao.insertOrGetId(conn,
                    playerInvEntry.getInventorySpec().getInventoryName());
            final Long inventoryEntryIdNullable = inventoriesDefaultsDao.getValueNullable(conn, inventoryId);
            if (inventoryEntryIdNullable != null) {
                inventoriesDefaultsDao.delete(conn, inventoryId);
                deleteInvEntry(conn, inventoryEntryIdNullable);
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
            for (Map.Entry<Long, String> entry : inventoriesDao.getIdToValue(conn).entrySet()) {
                long inventoryId = entry.getKey();
                String inventoryName = entry.getValue();
                InventorySpec inventorySpec = secuboid.getInventoriesOpt().get().getOrCreateInvSpec(inventoryName);

                // Survival
                final PlayerInvEntry survivalInvEntryNullable = createInventoriesEntryPlayerNullable(conn, playerInventoryCache, inventoryId, inventorySpec, false,
                        null);
                if (survivalInvEntryNullable != null) {
                    inventories.saveInventory(null, survivalInvEntryNullable, false, false, false);
                }

                // Creative
                final PlayerInvEntry creativeInvEntryNullable = createInventoriesEntryPlayerNullable(conn, playerInventoryCache, inventoryId, inventorySpec, true,
                        null);
                if (creativeInvEntryNullable != null) {
                    inventories.saveInventory(null, creativeInvEntryNullable, false, false, false);
                }

                // Death
                for (int deathVersion = PlayerInventoryCache.DEATH_SAVE_MAX_NBR; deathVersion > 0; deathVersion--) {
                    final PlayerInvEntry deathInvEntryNullable = createInventoriesEntryPlayerNullable(conn, playerInventoryCache, inventoryId, inventorySpec, false,
                            deathVersion);
                    if (deathInvEntryNullable != null) {
                        inventories.saveInventory(null, deathInvEntryNullable, true, false, false);
                    }
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
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), false, false, false);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the player inventory to database %s", playerInvEntry.getName()), e);
        }
    }

    @Override
    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), false, true, false);
        } catch (final SQLException e) {
            log.log(Level.SEVERE,
                    String.format("Unable to save the player death inventory to database %s", playerInvEntry.getName()),
                    e);
        }
    }

    @Override
    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        try (final Connection conn = dbConn.openConnection()) {
            saveInventory(conn, playerInvEntry, playerInvEntry.getPlayerUUIDNullable(), true, false, false);
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to save the player death inventory history to database %s",
                    playerInvEntry.getName()), e);
        }
    }

    @Override
    public void purgeInventory(InventorySpec inventorySpec) {
        try (final Connection conn = dbConn.openConnection()) {
            purgeInventoryDb(conn, inventorySpec.getInventoryName());
        } catch (final SQLException e) {
            log.log(Level.SEVERE, String.format("Unable to purge inventory from database %s",
                    inventorySpec.getInventoryName()), e);
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

    private PlayerContainer getPlayerContainer(final Map<Long, PlayerContainerPojo> idToPlayerContainerPojo,
                                               final Map<Long, String> idToPlayerContainerType, final long id) {
        final PlayerContainerPojo playerContainerPojo = idToPlayerContainerPojo.get(id);
        final String PlayerContainerTypeStr = idToPlayerContainerType
                .get(playerContainerPojo.getPlayerContainerTypeId());
        final PlayerContainerType playerContainerType = PlayerContainerType.getFromString(PlayerContainerTypeStr);

        return secuboid.getPlayerContainers().getOrAddPlayerContainer(playerContainerType,
                playerContainerPojo.getParameterNullable(), playerContainerPojo.getPlayerUUIDNullable());
    }

    private long getOrAddPlayerContainer(final Connection conn, final PlayerContainer playerContainer)
            throws SQLException {
        final PlayerContainerType playerContainerType = playerContainer.getContainerType();
        final long playerContainerTypeId = playerContainersTypesDao.insertOrGetId(conn, playerContainerType.name());

        if (playerContainerType.hasParameter()) {
            if (playerContainerType == PlayerContainerType.PLAYER) {
                final UUID playerUUID = ((PlayerContainerPlayer) playerContainer).getMinecraftUUID();
                addUserInDatabaseIfNeeded(conn, playerUUID);
                return playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId,
                        playerUUID, null);
            }
            return playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId, null,
                    playerContainer.getName());
        }
        return playerContainersDao.insertOrGetPlayerContainer(conn, playerContainerTypeId, null,
                null);
    }

    /**
     * Load a land.
     *
     * @return a map with a land (if success) and the parent (if exists)
     */
    private Entry<Land, UUID> loadLand(final LandPojo landPojo, final Map<Long, String> idToType,
                                       final Map<Long, PlayerContainerPojo> idToPlayerContainerPojo,
                                       final Map<Long, String> idToPlayerContainerType, final List<AreaPojo> areaPojos,
                                       final Map<UUID, List<RoadMatrixPojo>> landUUIDToMatrices, final Map<Long, String> idToAreaType,
                                       final List<Long> residentIds, final List<Long> bannedIds, final List<PermissionPojo> permissionPojos,
                                       final Map<Long, String> idToPermissionType, final List<FlagPojo> flagPojos,
                                       final Map<Long, String> landFlagIdtoValueString, final Map<Long, Double> landFlagIdtoValueDouble,
                                       final Map<Long, Boolean> landFlagIdtoValueBoolean, final Map<Long, List<String>> landFlagIdtoValueList,
                                       final Map<Long, String> idToFlagType, final List<UUID> playerNotifyUUIDs) {

        final String type = Optional.ofNullable(landPojo.getTypeIdNullable()).map(idToType::get).orElse(null);
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
        for (final long id : residentIds) {
            residents.add(getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, id));
        }
        // Banneds
        for (final long id : bannedIds) {
            banneds.add(getPlayerContainer(idToPlayerContainerPojo, idToPlayerContainerType, id));
        }
        // Create permissions
        for (final PermissionPojo permissionPojo : permissionPojos) {
            final TreeMap<PermissionType, Permission> permPlayer;
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
            Object valueObj = null;
            final Object flagTypeDefaultValue = flagType.getDefaultValue().getValue();
            final long landFlagId = flagPojo.getId();
            if (flagTypeDefaultValue instanceof String) {
                valueObj = landFlagIdtoValueString.get(landFlagId);
            } else if (flagTypeDefaultValue instanceof Double) {
                valueObj = landFlagIdtoValueDouble.get(landFlagId);
            } else if (flagTypeDefaultValue instanceof Boolean) {
                valueObj = landFlagIdtoValueBoolean.get(landFlagId);
            } else if (flagTypeDefaultValue instanceof String[]) {
                valueObj = landFlagIdtoValueList.get(landFlagId).toArray(new String[0]);
            }
            if (valueObj == null) {
                log.log(Level.WARNING,
                        String.format("No value for a flag [land=%s, flagType=%s]", landName, flagTypeStr));
                continue;
            }
            flags.add(secuboid.getPermissionsFlags().newFlag(flagType, valueObj, flagPojo.isInheritance()));
        }
        // Players Notify
        for (final UUID playerUUID : playerNotifyUUIDs) {
            pNotifs.add(secuboid.getPlayerContainers().getOrAddPlayerContainerPlayer(playerUUID));
        }
        // Economy
        boolean forSale = landPojo.isForSale();
        LandLocation forSaleSignLocNullable = null;
        if (forSale) {
            forSaleSignLocNullable = Optional.ofNullable(landPojo.getForSaleSignLocationNullable()).map(LandLocation::fromFileFormat)
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
            forRentSignLocNullable = Optional.ofNullable(landPojo.getForRentSignLocationNullable()).map(LandLocation::fromFileFormat)
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
            land.setForSale(true, Optional.ofNullable(landPojo.getSalePriceNullable()).orElse(0D), forSaleSignLocNullable);
        }
        if (forRent) {
            land.setForRent(Optional.ofNullable(landPojo.getRentPriceNullable()).orElse(0D), Optional.ofNullable(landPojo.getRentRenewNullable()).orElse(7),
                    Optional.ofNullable(landPojo.getRentAutoRenewNullable()).orElse(false), forRentSignLocNullable);
            Optional.ofNullable(landPojo.getTenantUUIDNullable()).ifPresent(tenantUUID -> {
                final PlayerContainerPlayer tenant = secuboid.getPlayerContainers().getOrAddPlayerContainerPlayer(tenantUUID);
                land.setRented(tenant, false);
                land.setLastPaymentTime(Optional.ofNullable(landPojo.getLastPaymentMillisNullable()).orElse(0L));

            });
        }

        return new AbstractMap.SimpleEntry<>(land, landPojo.getParentUUIDNullable());
    }

    private PlayerInvEntry loadInventoryFromPojo(final InventoryEntryPojo inventoryEntryPojo,
                                                 final List<InventoryPotionEffectPojo> inventoryPotionEffectPojos,
                                                 final PlayerInventoryCache playerInventoryCacheNullable, final InventorySpec inventorySpec,
                                                 final boolean isCreative) {
        final PlayerInvEntry playerInvEntry = new PlayerInvEntry(playerInventoryCacheNullable, inventorySpec, isCreative);

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
                              final UUID playerUUIDNullable, final boolean isDeathHistory, final boolean enderChestOnly,
                              final boolean isDefaultInv)
            throws SQLException {
        final InventorySpec inventorySpec = playerInvEntry.getInventorySpec();

        if (playerUUIDNullable != null) {
            addUserInDatabaseIfNeeded(conn, playerUUIDNullable);
        } else {
            // If for some reasons whe have to skip save (ex: SaveInventory = false)
            if (!inventorySpec.isSaveInventory()) {
                return;
            }
        }

        // Get the suffix name
        final long gameModeId = gameModesDao.insertOrGetId(conn,
                getGameModeFromBoolean(playerInvEntry.isCreativeInv()));
        final long inventoryId = inventoriesDao.insertOrGetId(conn,
                playerInvEntry.getInventorySpec().getInventoryName());

        if (isDeathHistory && playerUUIDNullable != null) {
            // Save death inventory
            // Death rename
            final Long inventoryEntryId9Nullable = inventoriesDeathsDao.getEntryIdNullable(conn, playerUUIDNullable,
                    inventoryId, gameModeId, 9);
            inventoriesDeathsDao.deleteNinth(conn, playerUUIDNullable, inventoryId, gameModeId);
            if (inventoryEntryId9Nullable != null) {
                deleteInvEntry(conn, inventoryEntryId9Nullable);
            }
            for (int t = 8; t >= 1; t--) {
                inventoriesDeathsDao.incrementDeathNumber(conn, playerUUIDNullable, inventoryId, gameModeId, t);
            }
            final Long inventoryEntryIdNullable = inventoriesDeathsDao.getEntryIdNullable(conn, playerUUIDNullable, inventoryId,
                    gameModeId, 1);
            saveInventoryEntry(conn, playerInvEntry, enderChestOnly, inventoryEntryIdNullable,
                    i -> inventoriesDeathsDao.insertInventoryDeath(conn, playerUUIDNullable, inventoryId, gameModeId, 1, i));

        } else if (isDefaultInv) {
            // Save default inventory
            final Long inventoryEntryIdNullable = inventoriesDefaultsDao.getValueNullable(conn, inventoryId);
            saveInventoryEntry(conn, playerInvEntry, enderChestOnly, inventoryEntryIdNullable,
                    i -> inventoriesDefaultsDao.insert(conn, inventoryId, i));

        } else {
            // Save normal inventory
            final Long inventoryEntryIdNullable = inventoriesSavesDao.getEntryIdNullable(conn, playerUUIDNullable, inventoryId,
                    gameModeId);
            saveInventoryEntry(conn, playerInvEntry, enderChestOnly, inventoryEntryIdNullable,
                    i -> inventoriesSavesDao.insertInventorySave(conn, playerUUIDNullable, inventoryId, gameModeId, i));
        }
    }

    private void saveInventoryEntry(final Connection conn, final PlayerInvEntry playerInvEntry,
                                    final boolean enderChestOnly, final Long inventoryEntryIdNullable,
                                    final SqlConsumer<Long> updateInvEntryIdConsumer) throws SQLException {

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
        final long inventoryEntryId;
        if (inventoryEntryIdNullable != null) {
            inventoryEntryId = inventoryEntryIdNullable;

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

    private PlayerInvEntry createInventoriesEntryPlayerNullable(final Connection conn,
                                                                final PlayerInventoryCache playerInventoryCache, final long inventoryId, final InventorySpec inventorySpec,
                                                                final boolean isCreative, final Integer deathVersionNullable) throws SQLException {
        final long gameModeId = gameModesDao.insertOrGetId(conn, getGameModeFromBoolean(isCreative));
        final UUID playerUUID = playerInventoryCache.getUUID();

        final Long inventoryEntryIdNullable;
        if (deathVersionNullable != null) {
            // Death load
            inventoryEntryIdNullable = inventoriesDeathsDao.getEntryIdNullable(conn, playerUUID, inventoryId, gameModeId,
                    deathVersionNullable);

        } else {
            // Normal load
            inventoryEntryIdNullable = inventoriesSavesDao.getEntryIdNullable(conn, playerUUID, inventoryId, gameModeId);
        }

        if (inventoryEntryIdNullable != null) {
            final InventoryEntryPojo inventoryEntryPojo = inventoriesEntriesDao.getInventoryEntry(conn,
                    inventoryEntryIdNullable);
            final List<InventoryPotionEffectPojo> inventoryPotionEffectPojos = inventoriesPotionEffectsDao
                    .getPotionEffectsFromEntryId(conn, inventoryEntryIdNullable);

            return loadInventoryFromPojo(inventoryEntryPojo, inventoryPotionEffectPojos,
                    playerInventoryCache, inventorySpec, isCreative);
        }
        return null;
    }

    private void purgeInventoryDb(Connection conn, String inventoryName) throws SQLException {
        Long inventoryId = inventoriesDao.getIdNullable(conn, inventoryName);
        if (inventoryId == null) {
            return;
        }

        Long defaultInvEntryId = inventoriesDefaultsDao.getValueNullable(conn, inventoryId);
        if (defaultInvEntryId != null) {
            inventoriesDefaultsDao.delete(conn, inventoryId);
            deleteInvEntry(conn, defaultInvEntryId);
        }

        List<Long> saveEntryIds = inventoriesSavesDao.getEntryIdsFromInventoryId(conn, inventoryId);
        inventoriesSavesDao.deleteFromInventoryId(conn, inventoryId);
        for (long saveEntryId : saveEntryIds) {
            deleteInvEntry(conn, saveEntryId);
        }

        List <Long> deathEntryIds = inventoriesDeathsDao.getEntryIdsFromInventoryId(conn, inventoryId);
        inventoriesDeathsDao.deleteFromInventoryId(conn, inventoryId);
        for (long deathEntryId : deathEntryIds) {
            deleteInvEntry(conn, deathEntryId);
        }

        inventoriesDao.delete(conn, inventoryId);
    }

    private void deleteInvEntry(Connection conn, Long inventoryEntryId) throws SQLException {
        inventoriesPotionEffectsDao.deletePotionEffectsFromEntryId(conn, inventoryEntryId);
        inventoriesEntriesDao.deleteInventoryEntry(conn, inventoryEntryId);
    }

    private String getGameModeFromBoolean(final boolean isCreative) {
        return isCreative ? "CREATIVE" : "SURVIVAL";
    }

    private void addUserInDatabaseIfNeeded(final Connection conn, final UUID playerUUID) throws SQLException {
        final String playerNameNullable = playersDao.getValueNullable(conn, playerUUID);
        if (playerNameNullable == null) {
            log.warning("Player " + playerUUID + " is not present in players cache. Adding the UUID as player name.");
            playersDao.insert(conn, playerUUID, playerUUID.toString());
        }
    }
}