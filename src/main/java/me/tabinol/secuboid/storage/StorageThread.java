/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2015 Tabinol
 Forked from Factoid (Copyright (C) 2014 Kaz00, Tabinol)

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
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.storage.flat.StorageFlat;
import me.tabinol.secuboid.utilities.SecuboidQueueThread;

/**
 * The Class StorageThread.
 */
public class StorageThread extends SecuboidQueueThread<StorageThread.SaveEntry> {

    /**
     * The storage.
     */
    private final Storage storage;

    private final Map<UUID, Thread> playerUuidToPreLoginThread;

    public enum SaveActionEnum {
        APPROVE_REMOVE, APPROVE_REMOVE_ALL, APPROVE_SAVE, INVENTORY_DEFAULT_REMOVE, INVENTORY_DEFAULT_SAVE,
        INVENTORY_PLAYER_LOAD, INVENTORY_PLAYER_SAVE, INVENTORY_PLAYER_DEATH_HISTORY_SAVE, INVENTORY_PLAYER_DEATH_SAVE,
        LAND_AREA_REMOVE, LAND_AREA_SAVE, LAND_BANNED_REMOVE, LAND_BANNED_SAVE, LAND_FLAG_REMOVE, LAND_FLAG_SAVE,
        LAND_PERMISSION_REMOVE, LAND_PERMISSION_SAVE, LAND_PLAYER_NOTIFY_REMOVE, LAND_PLAYER_NOTIFY_SAVE, LAND_REMOVE,
        LAND_RESIDENT_REMOVE, LAND_RESIDENT_SAVE, LAND_SAVE, PLAYERS_CACHE_SAVE
    }

    protected static final class SaveEntry {
        final SaveActionEnum saveActionEnum;
        final boolean skipFlatSave;
        final Optional<Savable> savableOpt;
        final SavableParameter[] savableParameters;

        private SaveEntry(final SaveActionEnum saveActionEnum, final boolean skipFlatSave,
                final Optional<Savable> savableOpt, final SavableParameter[] savableParameters) {
            this.saveActionEnum = saveActionEnum;
            this.skipFlatSave = skipFlatSave;
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
        playerUuidToPreLoginThread = new ConcurrentHashMap<>();
    }

    /**
     * Load all and start.
     */
    public void loadAllAndStart() {
        isQueueActive = false;
        storage.loadAll();
        isQueueActive = true;
        this.start();
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
    protected void doElement(final SaveEntry saveEntry) {
        if (saveEntry.skipFlatSave == true && storage instanceof StorageFlat) {
            // Skip save for flat file
            return;
        }
        try {
            doSave(saveEntry);
        } catch (final RuntimeException e) {
            final String savableNameNullable = saveEntry.savableOpt.map(o -> o.getName()).orElse(null);
            final String savableUUIDNullable = saveEntry.savableOpt.map(o -> o.getUUID().toString()).orElse(null);
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Unable to save \"%s\" for \"%s\" on disk, UUID \"%s\". Possible data loss!",
                            saveEntry.saveActionEnum, savableNameNullable, savableUUIDNullable),
                    e);
        }
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
        case INVENTORY_PLAYER_LOAD:
            storage.loadInventoriesPlayer((PlayerInventoryCache) savableNullable);
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
        case LAND_FLAG_SAVE:
            storage.saveLandFlag((Land) savableNullable, (Flag) savableParameters[0]);
            break;
        case LAND_PERMISSION_REMOVE:
            storage.removeLandPermission((Land) savableNullable, (PlayerContainer) savableParameters[0],
                    (Permission) savableParameters[1]);
            break;
        case LAND_PERMISSION_SAVE:
            storage.saveLandPermission((Land) savableNullable, (PlayerContainer) savableParameters[0],
                    (Permission) savableParameters[1]);
            break;
        case LAND_PLAYER_NOTIFY_REMOVE:
            storage.removeLandPlayerNotify((Land) savableNullable, (PlayerContainerPlayer) savableParameters[0]);
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
        case LAND_RESIDENT_SAVE:
            storage.saveLandResident((Land) savableNullable, (PlayerContainer) savableParameters[0]);
            break;
        case LAND_SAVE:
            storage.saveLand((Land) savableNullable);
            break;
        default:
        }
    }

    /**
     * Add a save action for lands, approves or any objects to save to the disk or
     * the database.
     *
     * @param saveActionEnum    the action type to do
     * @param skipFlatSave      true means skip the save on flat save (to prevent
     *                          repetitive saves)
     * @param savableOpt        the savable object optional
     * @param savableParameters An array of savable parameters
     */
    public void addSaveAction(final SaveActionEnum saveActionEnum, final boolean skipFlatSave,
            final Optional<Savable> savableOpt, final SavableParameter... savableParameters) {
        addElement(new SaveEntry(saveActionEnum, skipFlatSave, savableOpt, savableParameters));
    }

    public void preloginAddThread(final UUID uuid, final Thread thread) {
        playerUuidToPreLoginThread.put(uuid, thread);
    }

    public Thread preloginRemoveThread(final UUID uuid) {
        return playerUuidToPreLoginThread.remove(uuid);
    }

    public Thread preloginGetThread(final UUID uuid) {
        return playerUuidToPreLoginThread.get(uuid);
    }
}
