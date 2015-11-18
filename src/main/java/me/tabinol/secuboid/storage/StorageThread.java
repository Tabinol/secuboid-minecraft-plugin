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


/**
 * The Class StorageThread.
 */
public class StorageThread extends Thread {
    
    /** The exit request. */
    private boolean exitRequest = false;

    /** The in load. */
    protected boolean inLoad = true; // True if the Database is in Loaded

    /** The storage. */
    private final Storage storage;
    
    /** The land save list request. */
    private final List<Object> saveList;

    /** The land save list request. */
    private final List<Object> removeList;

    /** The lock. */
    final Lock lock = new ReentrantLock();
    
    /** The lock command request. */
    final Condition commandRequest  = lock.newCondition(); 
    
    /** The lock not saved. */
    final Condition notSaved = lock.newCondition(); 

    /** Class internally used to store landName et LandGenealogy in a list */
    private class NameGenealogy {
        
        String landName;
        int landGenealogy;
        
        NameGenealogy(String landName, int landGenealogy) {
            
            this.landName = landName;
            this.landGenealogy = landGenealogy;
        }
    }
    
    /**
     * Instantiates a new storage thread.
     */
    public StorageThread() {
        
        this.setName("Secuboid Storage");
        storage = new StorageFlat();
        saveList = Collections.synchronizedList(new ArrayList<Object>());
        removeList = Collections.synchronizedList(new ArrayList<Object>());
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

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        
        lock.lock();
        
        // Output request loop (waiting for a command)
        while(!exitRequest) {
            
               // Save Lands or Factions
               while(!saveList.isEmpty()) {
                   
                   Object saveEntry = saveList.remove(0);
                storage.saveLand((Land)saveEntry);
               }
               
               // Remove Lands or Factions
               while(!removeList.isEmpty()) {
                   
                   Object removeEntry = removeList.remove(0);
                   if(removeEntry instanceof Land) {
                       storage.removeLand((Land)removeEntry);
                   } else if( removeEntry instanceof NameGenealogy) {
                    storage.removeLand(((NameGenealogy) removeEntry).landName,
                            ((NameGenealogy) removeEntry).landGenealogy);
                }
               }
               
            // wait!
            try {
                commandRequest.await();
                Secuboid.getThisPlugin().getLog().write("Storage Thread wake up!");
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
        }
        notSaved.signal();
        lock.unlock();
    }
    
    /**
     * Stop next run.
     */
    public void stopNextRun() {
        
        if(!isAlive()) {
            Secuboid.getThisPlugin().getLogger().log(Level.SEVERE, "Problem with save Thread. Possible data loss!");
            return;
        }
        exitRequest = true;
        lock.lock();
        commandRequest.signal();
        try {
            notSaved.await();
        } catch (InterruptedException e) {
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
    public void saveLand(Land land) {
        
        if(!inLoad) {
            saveList.add(land);
            wakeUp();
        }
    }

    /**
     * Removes the land.
     *
     * @param land the land
     */
    public void removeLand(Land land) {
        
        removeList.add(land);
        wakeUp();
    }

    /**
     * Removes the land.
     *  
     * @param landName the land name
     * @param landGenealogy The land genealogy
     */
    public void removeLand(String landName, int landGenealogy) {
        
        removeList.add(new NameGenealogy(landName, landGenealogy));
        wakeUp();
    }

    private void wakeUp() {
        
        lock.lock();
        commandRequest.signal();
        Secuboid.getThisPlugin().getLog().write("Storage request (Thread wake up...)");
        lock.unlock();
    }
}
