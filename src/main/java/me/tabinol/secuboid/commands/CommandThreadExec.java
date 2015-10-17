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
package me.tabinol.secuboid.commands;

import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayerName;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;


/**
 * The Class CommandThreadExec.
 */
public abstract class CommandThreadExec extends CommandExec {
	
	protected IPlayerContainer pc;

	/**
	 * Instantiates a new command thread exec.
	 *
	 * @param entity the entity
	 * @throws SecuboidCommandException the secuboid command exception
	 */
	public CommandThreadExec(CommandEntities entity) throws SecuboidCommandException {
	
	super(entity);
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
	 * Convert only if the PlayerContainer is a PlayerContainerPlayerName
	 * It takes the result from the UUID request.
	 *
	 * @param playerCacheEntry the player cache entry
	 * @throws SecuboidCommandException the secuboid command exception
	 */
	protected void convertPcIfNeeded(PlayerCacheEntry[] playerCacheEntry)
			throws SecuboidCommandException {
		
		if(pc instanceof PlayerContainerPlayerName) {
			if(playerCacheEntry.length == 1 && playerCacheEntry[0] != null) {
				pc = new PlayerContainerPlayer(playerCacheEntry[0].getUUID());
			} else {
				throw new SecuboidCommandException("Player not exist Error", entity.player, "COMMAND.CONTAINER.PLAYERNOTEXIST");
			}
		}
	}
}
