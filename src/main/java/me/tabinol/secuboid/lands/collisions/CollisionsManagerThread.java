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

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandCollisionsThreadExec;
import me.tabinol.secuboid.lands.Land;

/**
 * This class is for lands and cuboids calculation. It need to be threaded
 */
public class CollisionsManagerThread extends Thread {

    private final Secuboid secuboid;

    /**
     * The request queue.
     */
    private final BlockingQueue<Optional<OutputRequest>> requestQueue;

    /**
     * The Class OutputRequest.
     */
    private class OutputRequest {

        /**
         * The command exec.
         */
        CommandCollisionsThreadExec commandExec;

        /**
         * The collisions.
         */
        Collisions collisions;

        /**
         * Instantiates a new output request.
         *
         * @param commandExec the command exec
         * @param collisions  the collisions
         */
        OutputRequest(final CommandCollisionsThreadExec commandExec, final Collisions collisions) {

            this.commandExec = commandExec;
            this.collisions = collisions;
        }
    }

    /**
     * Instantiates a new collisions manager thread.
     *
     * @param secuboid secuboid instance
     */
    public CollisionsManagerThread(final Secuboid secuboid) {

        this.secuboid = secuboid;
        this.setName("Secuboid collisions manager");
        requestQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try {
            loopCollisionsManagerList();
        } catch (final InterruptedException e) {
            secuboid.getLogger().log(Level.SEVERE, String.format("Interruption requested for %s", getName()), e);
        }
    }

    private void loopCollisionsManagerList() throws InterruptedException {
        Optional<OutputRequest> outputRequestOpt;
        // Output request loop (waiting for a command)
        // An optional empty request stops the thread.
        while ((outputRequestOpt = requestQueue.take()).isPresent()) {
            // Do collision and price check
            final OutputRequest output = outputRequestOpt.get();
            try {
                output.collisions.doCollisionCheck();
            } catch (final Exception e) {
                final Land land = output.collisions.getLand();
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Unable to complete collision check for \"%s\", UUID \"%s\"", land.getName(),
                                land.getUUID()),
                        e);
            }

            // Return the result to main thread
            final ReturnCollisionsToCommand returnToCommand = new ReturnCollisionsToCommand(secuboid,
                    output.commandExec, output.collisions);
            Bukkit.getScheduler().callSyncMethod(secuboid, returnToCommand);
        }
    }

    /**
     * Stop next run.
     */
    public void stopNextRun() {
        if (!isAlive()) {
            secuboid.getLogger().severe("Problem with collisions manager thread. Possible data loss!");
            return;
        }
        requestQueue.add(Optional.empty());
    }

    /**
     * Wake up the thread and check for collisions
     *
     * @param commandThreadExec The command instance
     * @param collisionsReq     An instance of collision
     */
    public void lookForCollisions(final CommandCollisionsThreadExec commandThreadExec, final Collisions collisionsReq) {

        requestQueue.add(Optional.of(new OutputRequest(commandThreadExec, collisionsReq)));
    }
}
