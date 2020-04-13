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
package me.tabinol.secuboid.commands.executor;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayerName;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandPlayerThreadExec.
 */
public abstract class CommandPlayerThreadExec extends CommandExec {

    protected PlayerContainer pc;

    /**
     * Instantiates a new command thread exec.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    CommandPlayerThreadExec(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    /**
     * Command thread execute.
     *
     * @param playerCacheEntry the player cache entry
     * @throws SecuboidCommandException the secuboid command exception
     */
    public abstract void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
            throws SecuboidCommandException;

    /**
     * Convert only if the PlayerContainer is a PlayerContainerPlayerName It takes the result from the UUID request.
     *
     * @param playerCacheEntry the player cache entry
     * @throws SecuboidCommandException the secuboid command exception
     */
    final void convertPcIfNeeded(PlayerCacheEntry[] playerCacheEntry) throws SecuboidCommandException {

        if (pc instanceof PlayerContainerPlayerName) {
            if (playerCacheEntry.length == 1 && playerCacheEntry[0] != null) {
                pc = new PlayerContainerPlayer(secuboid, playerCacheEntry[0].getUUID());
            } else {
                throw new SecuboidCommandException(secuboid, "Player not exist Error", player, "COMMAND.CONTAINER.PLAYERNOTEXIST");
            }
        }
    }
}
