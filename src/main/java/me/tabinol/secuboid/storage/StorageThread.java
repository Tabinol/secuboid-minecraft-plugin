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
package me.tabinol.secuboid.storage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.flat.StorageFlat;
import me.tabinol.secuboid.storage.mysql.StorageMySql;
import me.tabinol.secuboid.utilities.SecuboidQueueThread;

/**
 * The Class StorageThread.
 */
public class StorageThread extends SecuboidQueueThread<StorageThread.SaveEntry> {

    /**
     * The storage.
     */
    private final Storage storage;

    private final Map<UUID, Object> playerUUIDToLock;

    public enum SaveActionEnum {
        APPROVE_REMOVE, APPROVE_REMOVE_ALL, APPROVE_SAVE, INVENTORY_DEFAULT_REMOVE, INVENTORY_DEFAULT_SAVE,
        INVENTORY_PLAYER_LOAD, INVENTORY_PLAYER_SAVE, INVENTORY_PLAYER_DEATH_HISTORY_SAVE, INVENTORY_PLAYER_DEATH_SAVE,
        LAND_AREA_REMOVE, LAND_AREA_SAVE, LAND_BANNED_REMOVE, LAND_BANNED_SAVE, LAND_FLAG_REMOVE, LAND_FLAG_REMOVE_ALL,
        LAND_FLAG_SAVE, LAND_PERMISSION_REMOVE, LAND_PERMISSION_REMOVE_ALL, LAND_PERMISSION_SAVE,
        LAND_PLAYER_NOTIFY_REMOVE, LAND_PLAYER_NOTIFY_REMOVE_ALL, LAND_PLAYER_NOTIFY_SAVE, LAND_REMOVE,
        LAND_RESIDENT_REMOVE, LAND_RESIDENT_REMOVE_ALL, LAND_RESIDENT_SAVE, LAND_SAVE, PLAYERS_CACHE_SAVE, THREAD_NOTIFY
    }

    public enum SaveOn {
        BOTH, DATABASE, FLAT;
    }

    protected static final class SaveEntry {
        final SaveActionEnum saveActionEnum;
        final SaveOn saveOn;
        final Optional<Savable> savableOpt;
        final SavableParameter[] savableParameters;

        private SaveEntry(final SaveActionEnum saveActionEnum, final SaveOn saveOn, final Optional<Savable> savableOpt,
                final SavableParameter[] savableParameters) {
            this.saveActionEnum = saveActionEnum;
            this.saveOn = saveOn;
            this.savableOpt = savableOpt;
            this.savableParameters = savableParameters;
        }
    }

    /**
     * Instantiates a new storage thread.
     *
     * @param secuboid secuboid instance
     * @param storage  storage instance
     */
    public StorageThread(final Secuboid secuboid, final Storage storage) {
        super(secuboid, "Secuboid Storage");
        this.storage = storage;
        playerUUIDToLock = new ConcurrentHashMap<>();
    }

    /**
     * Load all and start.
     */
    public void loadAllAndStart() {
        isQueueActive = false;
        final boolean conversionNeeded = storage.loadAll();
        isQueueActive = true;
        this.start();

        // Conversion Flat to MySQL
        if (conversionNeeded) {
            final FlatToMySql flatToMySql = new FlatToMySql(secuboid);
            if (flatToMySql.isConversionNeeded()) {
                secuboid.getLogger().info("Converting flat files to MySQL. This may take several minutes!");
                flatToMySql.playersCacheConversion();
                flatToMySql.landConversion();
                flatToMySql.approveConversion();
                flatToMySql.inventoriesConversion();
            }
        }
    }

    /**
     * Checks if is in load.
     *
     * @return true, if is in load
     */
    public boolean isInLoad() {
        return !isQueueActive;
    }

    @Override
    protected boolean doElement(final SaveEntry saveEntry) {
        if ((saveEntry.saveOn == SaveOn.DATABASE && storage instanceof StorageFlat)
                || (saveEntry.saveOn == SaveOn.FLAT && storage instanceof StorageMySql)) {
            // Skip save for flat file or database
            return true;
        }
        try {
            doSave(saveEntry);
        } catch (final RuntimeException e) {
            final String savableNameNullable = saveEntry.savableOpt.map(o -> o.getName()).orElse(null);
            final String savableUUIDNullable = saveEntry.savableOpt.map(o -> o.getUUID().toString()).orElse(null);
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Unable to save or load \"%s\" for \"%s\", UUID \"%s\". Possible data loss!",
                            saveEntry.saveActionEnum, savableNameNullable, savableUUIDNullable),
                    e);
        }
        return true;
    }

    private void doSave(final SaveEntry saveEntry) {
        final Savable savableNullable = saveEntry.savableOpt.orElse(null);
        final SavableParameter[] savableParameters = saveEntry.savableParameters;
        switch (saveEntry.saveActionEnum) {
            case APPROVE_REMOVE:
                storage.removeApprove((Approve) savableNullable);
                break;
            case APPROVE_REMOVE_ALL:
                storage.removeAllApproves();
                break;
            case APPROVE_SAVE:
                storage.saveApprove((Approve) savableNullable);
                break;
            case INVENTORY_DEFAULT_REMOVE:
                storage.removeInventoryDefault((PlayerInvEntry) savableNullable);
                break;
            case INVENTORY_DEFAULT_SAVE:
                storage.saveInventoryDefault((PlayerInvEntry) savableNullable);
                break;
            case INVENTORY_PLAYER_LOAD: {
                final PlayerInventoryCache playerInventoryCache = (PlayerInventoryCache) savableNullable;
                storage.loadInventoriesPlayer(playerInventoryCache);
                preLoginThreadNotify(playerInventoryCache.getUUID());
            }
                break;
            case INVENTORY_PLAYER_SAVE:
                storage.saveInventoryPlayer((PlayerInvEntry) savableNullable);
                break;
            case INVENTORY_PLAYER_DEATH_HISTORY_SAVE:
                storage.saveInventoryPlayerDeathHistory((PlayerInvEntry) savableNullable);
                break;
            case INVENTORY_PLAYER_DEATH_SAVE:
                storage.saveInventoryPlayerDeath((PlayerInvEntry) savableNullable);
                break;
            case LAND_AREA_REMOVE:
                storage.removeLandArea((Land) savableNullable, (Area) savableParameters[0]);
                break;
            case LAND_AREA_SAVE:
                storage.saveLandArea((Land) savableNullable, (Area) savableParameters[0]);
                break;
            case LAND_BANNED_REMOVE:
                storage.removeLandBanned((Land) savableNullable, (PlayerContainer) savableParameters[0]);
                break;
            case LAND_BANNED_SAVE:
                storage.saveLandBanned((Land) savableNullable, (PlayerContainer) savableParameters[0]);
                break;
            case LAND_FLAG_REMOVE:
                storage.removeLandFlag((Land) savableNullable, (Flag) savableParameters[0]);
                break;
            case LAND_FLAG_REMOVE_ALL:
                storage.removeAllLandFlags((Land) savableNullable);
                break;
            case LAND_FLAG_SAVE:
                storage.saveLandFlag((Land) savableNullable, (Flag) savableParameters[0]);
                break;
            case LAND_PERMISSION_REMOVE:
                storage.removeLandPermission((Land) savableNullable, (PlayerContainer) savableParameters[0],
                        (Permission) savableParameters[1]);
                break;
            case LAND_PERMISSION_REMOVE_ALL:
                storage.removeAllLandPermissions((Land) savableNullable);
                break;
            case LAND_PERMISSION_SAVE:
                storage.saveLandPermission((Land) savableNullable, (PlayerContainer) savableParameters[0],
                        (Permission) savableParameters[1]);
                break;
            case LAND_PLAYER_NOTIFY_REMOVE:
                storage.removeLandPlayerNotify((Land) savableNullable, (PlayerContainerPlayer) savableParameters[0]);
                break;
            case LAND_PLAYER_NOTIFY_REMOVE_ALL:
                storage.removeAllLandPlayerNotify((Land) savableNullable);
                break;
            case LAND_PLAYER_NOTIFY_SAVE:
                storage.saveLandPlayerNotify((Land) savableNullable, (PlayerContainerPlayer) savableParameters[0]);
                break;
            case LAND_REMOVE:
                storage.removeLand((Land) savableNullable);
                break;
            case LAND_RESIDENT_REMOVE:
                storage.removeLandResident((Land) savableNullable, (PlayerContainer) savableParameters[0]);
                break;
            case LAND_RESIDENT_REMOVE_ALL:
                storage.removeAllLandResidents((Land) savableNullable);
                break;
            case LAND_RESIDENT_SAVE:
                storage.saveLandResident((Land) savableNullable, (PlayerContainer) savableParameters[0]);
                break;
            case LAND_SAVE:
                storage.saveLand((Land) savableNullable);
                break;
            case PLAYERS_CACHE_SAVE:
                storage.savePlayerCacheEntry((PlayerCacheEntry) savableNullable);
                break;
            case THREAD_NOTIFY:
                threadNotify();
                break;
            default:
                throw new SecuboidRuntimeException("Enum case not in list");
        }
    }

    /**
     * Add a save action for lands, approves or any objects to save to the disk or
     * the database.
     *
     * @param saveActionEnum    the action type to do
     * @param saveOn            Both, database or flat file only
     * @param savableOpt        the savable object optional
     * @param savableParameters An array of savable parameters
     */
    public void addSaveAction(final SaveActionEnum saveActionEnum, final SaveOn saveOn,
            final Optional<Savable> savableOpt, final SavableParameter... savableParameters) {
        addElement(new SaveEntry(saveActionEnum, saveOn, savableOpt, savableParameters));
    }

    public void addPlayerUUIDPreLogin(final UUID uuid, final Object lock) {
        playerUUIDToLock.put(uuid, lock);
    }

    public Object removePlayerUUIDPreLogin(final UUID uuid) {
        return playerUUIDToLock.remove(uuid);
    }

    private void preLoginThreadNotify(final UUID uuid) {
        final Object lock = removePlayerUUIDPreLogin(uuid);
        if (lock != null) {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    private void threadNotify() {
        final Object lock = getLock();
        synchronized (lock) {
            lock.notify();
        }
    }
}
