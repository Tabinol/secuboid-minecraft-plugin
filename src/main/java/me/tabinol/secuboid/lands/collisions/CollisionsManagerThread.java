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
package me.tabinol.secuboid.lands.collisions;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandCollisionsThreadExec;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.utilities.SecuboidQueueThread;

/**
 * This class is for lands and cuboids calculation. It need to be threaded
 */
public class CollisionsManagerThread extends SecuboidQueueThread<CollisionsManagerThread.OutputRequest> {

    /**
     * The Class OutputRequest.
     */
    protected static final class OutputRequest {

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
        private OutputRequest(final CommandCollisionsThreadExec commandExec, final Collisions collisions) {

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
        super(secuboid, "Secuboid collisions manager");
    }

    @Override
    protected boolean doElement(final OutputRequest outputRequest) {
        // Do collision and price check
        try {
            outputRequest.collisions.doCollisionCheck();
        } catch (final RuntimeException e) {
            final Land land = outputRequest.collisions.getLand();
            secuboid.getLogger().log(Level.SEVERE, String.format(
                    "Unable to complete collision check for \"%s\", UUID \"%s\"", land.getName(), land.getUUID()), e);
        }

        // Return the result to main thread
        final ReturnCollisionsToCommand returnToCommand = new ReturnCollisionsToCommand(secuboid,
                outputRequest.commandExec, outputRequest.collisions);
        Bukkit.getScheduler().callSyncMethod(secuboid, returnToCommand);

        return true;
    }

    /**
     * Wake up the thread and check for collisions
     *
     * @param commandThreadExec The command instance
     * @param collisionsReq     An instance of collision
     */
    public void lookForCollisions(final CommandCollisionsThreadExec commandThreadExec, final Collisions collisionsReq) {

        addElement(new OutputRequest(commandThreadExec, collisionsReq));
    }
}
