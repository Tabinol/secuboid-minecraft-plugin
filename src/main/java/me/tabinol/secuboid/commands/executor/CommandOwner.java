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
package me.tabinol.secuboid.commands.executor;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandThreadExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboidapi.playercontainer.EPlayerContainerType;

import org.bukkit.ChatColor;


/**
 * The Class CommandOwner.
 */
@InfoCommand(name="owner", forceParameter=true)
public class CommandOwner extends CommandThreadExec {

	/**
     * Instantiates a new command owner.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandOwner(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        
        pc = entity.argList.getPlayerContainerFromArg(land,
                new EPlayerContainerType[]{EPlayerContainerType.EVERYBODY,
                    EPlayerContainerType.OWNER, EPlayerContainerType.VISITOR});
        Secuboid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.secuboid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws SecuboidCommandException {
        
    	convertPcIfNeeded(playerCacheEntry);

        land.setOwner(pc);
        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.OWNER.ISDONE", pc.getPrint(), land.getName()));
        Secuboid.getThisPlugin().iLog().write("The land " + land.getName() + "is set to owner: " + pc.getPrint());

        // Cancel the selection
        new CommandCancel(entity.playerConf, true).commandExecute();

    }
}
