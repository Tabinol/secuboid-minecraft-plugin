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
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandThreadExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboidapi.playercontainer.EPlayerContainerType;

import org.bukkit.ChatColor;


/**
 * The Class CommandBan.
 */
@InfoCommand(name="ban", forceParameter=true)
public class CommandBan extends CommandThreadExec {

	private String fonction;

	/**
     * Instantiates a new command ban.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandBan(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, PermissionList.LAND_BAN.getPermissionType(), null);

        fonction = entity.argList.getNext();

        if (fonction.equalsIgnoreCase("add")) {

            pc = entity.argList.getPlayerContainerFromArg(land,
                    new EPlayerContainerType[]{EPlayerContainerType.EVERYBODY,
                        EPlayerContainerType.OWNER, EPlayerContainerType.VISITOR,
                        EPlayerContainerType.RESIDENT});
            Secuboid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("remove")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            Secuboid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getBanneds().isEmpty()) {
                for (IPlayerContainer pc : land.getBanneds()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.BANNED.LISTROWNULL"));
            }
            new ChatPage("COMMAND.BANNED.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        
        } else {
            throw new SecuboidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.secuboid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public synchronized void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws SecuboidCommandException {
	
    	convertPcIfNeeded(playerCacheEntry);

    	if (fonction.equalsIgnoreCase("add")) {

    		if (land.isLocationInside(land.getWorld().getSpawnLocation())) {
    			throw new SecuboidCommandException("Banned", entity.player, "COMMAND.BANNED.NOTINSPAWN");
    		}
    		land.addBanned(pc);

    		entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.BANNED.ISDONE", 
    				pc.getPrint() + ChatColor.YELLOW, land.getName()));
    		Secuboid.getThisPlugin().iLog().write("Ban added: " + pc.toString());

    	} else if (fonction.equalsIgnoreCase("remove")) {

    		if (!land.removeBanned(pc)) {
    			throw new SecuboidCommandException("Banned", entity.player, "COMMAND.BANNED.REMOVENOTEXIST");
    		}
    		entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.BANNED.REMOVEISDONE", 
    				pc.getPrint() + ChatColor.YELLOW, land.getName()));
    		Secuboid.getThisPlugin().iLog().write("Ban removed: " + pc.toString());
    	}
    }
}