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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.storage.flat.StorageFlat;

/**
 * The Class StorageThread.
 */
public class StorageThread extends Thread {

    private final Secuboid secuboid;

    /**
     * The exit request.
     */
    private boolean exitRequest = false;

    /**
     * True if the Database is in Loaded.
     */
    private boolean inLoad = true;

    /**
     * The storage.
     */
    private final Storage storage;

    /**
     * The land save list request.
     */
    private final List<Land> saveList;

    /**
     * The land delete list request.
     */
    private final List<Land> removeList;

    /**
     * The lock.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * The lock command request.
     */
    private final Condition commandRequest = lock.newCondition();

    /**
     * The lock not saved.
     */
    private final Condition notSaved = lock.newCondition();

    /**
     * Instantiates a new storage thread.
     *
     * @param secuboid secuboid instance
     */
    public StorageThread(final Secuboid secuboid) {
        this.secuboid = secuboid;
        this.setName("Secuboid Storage");
        storage = new StorageFlat(secuboid);
        saveList = Collections.synchronizedList(new ArrayList<>());
        removeList = Collections.synchronizedList(new ArrayList<>());
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

        lock.lock();
        try {
            // Output request loop (waiting for a command)
            while (!exitRequest) {

                // Save Lands or Factions
                while (!saveList.isEmpty()) {

                    final Land saveEntry = saveList.remove(0);
                    try {
                        storage.saveLand(saveEntry);
                    } catch (final Exception e) {
                        secuboid.getLogger().log(Level.SEVERE,
                                String.format("Unable to save land \"%s\" on disk, UUID \"%s\". Possible data loss!",
                                        saveEntry.getName(), saveEntry.getUUID()),
                                e);
                    }
                }

                // Remove Lands or Factions
                while (!removeList.isEmpty()) {

                    final Land removeEntry = removeList.remove(0);
                    try {
                        storage.removeLand(removeEntry);
                    } catch (final Exception e) {
                        secuboid.getLogger().log(Level.SEVERE,
                                String.format("Unable to delete land \"%s\" on disk, UUID \"%s\". Possible data loss!",
                                        removeEntry.getName(), removeEntry.getUUID()),
                                e);
                    }
                }

                // wait!
                try {
                    commandRequest.await();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notSaved.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stop next run.
     */
    public void stopNextRun() {

        if (!isAlive()) {
            secuboid.getLogger().severe("Problem with save Thread. Possible data loss!");
            return;
        }
        exitRequest = true;
        lock.lock();
        commandRequest.signal();
        try {
            notSaved.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Save land.
     *
     * @param land the land
     */
    public void saveLand(final Land land) {
        if (!inLoad) {
            saveList.add(land);
            wakeUp();
        }
    }

    /**
     * Removes the land.
     *
     * @param land the land
     */
    public void removeLand(final Land land) {
        removeList.add(land);
        wakeUp();
    }

    private void wakeUp() {
        lock.lock();
        try {
            commandRequest.signal();
        } finally {
            lock.unlock();
        }
    }
}
