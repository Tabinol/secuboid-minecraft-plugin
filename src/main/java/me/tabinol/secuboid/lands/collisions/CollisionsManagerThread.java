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
package me.tabinol.secuboid.lands.collisions;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandCollisionsThreadExec;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * This class is for lands and cuboids calculation. It need to be threaded
 */
public class CollisionsManagerThread extends Thread {

    /** The exit request. */
    private boolean exitRequest = false;

    /** The lock. */
    private final Lock lock = new ReentrantLock();

    /** The lock command request. */
    private final Condition commandRequest  = lock.newCondition();

    /** The lock not saved. */
    private final Condition notSaved = lock.newCondition();

    /** The calculated list. */
    private final List<OutputRequest> requests;

    /**
     * The Class OutputRequest.
     */
    private class OutputRequest {

        /** The command exec. */
        CommandCollisionsThreadExec commandExec;

        /** The collisions. */
        Collisions collisions;

        /**
         * Instantiates a new output request.
         *
         * @param commandExec the command exec
         * @param collisions the collisions
         */
        OutputRequest(CommandCollisionsThreadExec commandExec, Collisions collisions) {

            this.commandExec = commandExec;
            this.collisions = collisions;
        }
    }


    public CollisionsManagerThread() {

        this.setName("Secuboid collisions manager");
        requests = Collections.synchronizedList(new ArrayList<OutputRequest>());
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        lock.lock();

        // Output request loop (waiting for a command)
        while(!exitRequest) {

            while(!requests.isEmpty()) {

                // Do collsion and price check
                OutputRequest output = requests.remove(0);
                output.collisions.doCollisionCheck();

                // Return the result to main thread
                ReturnCollisionsToCommand returnToCommand = new ReturnCollisionsToCommand(output.commandExec, output.collisions);
                Bukkit.getScheduler().callSyncMethod(Secuboid.getThisPlugin(), returnToCommand);

            }

            // wait!
            try {
                commandRequest.await();
                Secuboid.getThisPlugin().getLog().write("Secuboid collisions manager Thread wake up!");
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
            Secuboid.getThisPlugin().getLogger().log(Level.SEVERE, "Problem with collisions manager Thread. Possible data loss!");
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
     * Wake up the thread and check for collisions
     * @param commandThreadExec The command instance
     * @param collisionsReq An instance of collision
     */
    public void lookForCollisions(CommandCollisionsThreadExec commandThreadExec, Collisions collisionsReq) {

        requests.add(new OutputRequest(commandThreadExec, collisionsReq));
        wakeUp();
    }

    private void wakeUp() {

        lock.lock();
        commandRequest.signal();
        Secuboid.getThisPlugin().getLog().write("Secuboid collisions manager request (Thread wake up...)");
        lock.unlock();
    }
}
