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

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;

/**
 * The Class StorageThread.
 */
public class StorageThread extends Thread {

    private final Secuboid secuboid;

    /**
     * The storage.
     */
    private final Storage storage;

    /**
     * The save list queue request.
     */
    private final BlockingQueue<SaveEntry> saveEntryQueue;

    /**
     * True if the Database is in Loaded.
     */
    private boolean inLoad = true;

    public enum SaveActionEnum {
        APPROVE_REMOVE, APPROVE_REMOVE_ALL, APPROVE_SAVE, LAND_REMOVE, LAND_SAVE, PLAYERS_CACHE_SAVE, SHUTDOWN
    }

    private static class SaveEntry {
        final SaveActionEnum saveActionEnum;
        final Optional<Savable> savableOpt;

        SaveEntry(final SaveActionEnum saveActionEnum, final Optional<Savable> savableOpt) {
            this.saveActionEnum = saveActionEnum;
            this.savableOpt = savableOpt;
        }
    }

    /**
     * Instantiates a new storage thread.
     *
     * @param secuboid secuboid instance
     * @param storage  storage instance
     */
    public StorageThread(final Secuboid secuboid, final Storage storage) {
        this.secuboid = secuboid;
        this.storage = storage;
        this.setName("Secuboid Storage");
        saveEntryQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Load all and start.
     */
    public void loadAllAndStart() {
        inLoad = true;
        storage.loadAll();
        inLoad = false;
        this.start();
    }

    /**
     * Checks if is in load.
     *
     * @return true, if is in load
     */
    public boolean isInLoad() {
        return inLoad;
    }

    @Override
    public void run() {
        try {
            loopSaveList();
        } catch (final InterruptedException e) {
            secuboid.getLogger().log(Level.SEVERE, String.format("Interruption requested for %s", getName()), e);
        }
    }

    private void loopSaveList() throws InterruptedException {
        // Save loop
        SaveEntry saveEntry;
        while ((saveEntry = saveEntryQueue.take()).saveActionEnum != SaveActionEnum.SHUTDOWN) {
            try {
                doSave(saveEntry);
            } catch (final Exception e) {
                final String savableNameNullable = saveEntry.savableOpt.map(o -> o.getName()).orElse(null);
                final String savableUUIDNullable = saveEntry.savableOpt.map(o -> o.getUUID().toString()).orElse(null);
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Unable to save \"%s\" for \"%s\" on disk, UUID \"%s\". Possible data loss!",
                                saveEntry.saveActionEnum, savableNameNullable, savableUUIDNullable),
                        e);
            }
        }
    }

    private void doSave(final SaveEntry saveEntry) {
        final Savable savableNullable = saveEntry.savableOpt.orElse(null);
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
        case LAND_REMOVE:
            storage.removeLand((Land) savableNullable);
            break;
        case LAND_SAVE:
            storage.saveLand((Land) savableNullable);
            break;
        default:
        }
    }

    /**
     * Stop next run.
     */
    public void stopNextRun() {
        if (!isAlive()) {
            secuboid.getLogger().severe("Problem with storage thread. Possible data loss!");
            return;
        }
        addSaveAction(SaveActionEnum.SHUTDOWN, Optional.empty());
    }

    /**
     * Add a save action for lands, approves or any objects to save to the disk or
     * the database.
     *
     * @param saveActionEnum the action type to do
     * @param savableOpt     the savable object optional
     */
    public void addSaveAction(final SaveActionEnum saveActionEnum, final Optional<Savable> savableOpt) {
        if (!inLoad) {
            saveEntryQueue.add(new SaveEntry(saveActionEnum, savableOpt));
        }
    }
}
