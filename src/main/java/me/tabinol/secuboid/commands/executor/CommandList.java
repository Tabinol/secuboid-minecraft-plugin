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

import java.util.Collection;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandPlayerThreadExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import org.bukkit.ChatColor;

/**
 * The Class CommandList.
 *
 * @author Tabinol
 */
@InfoCommand(name = "list")
public class CommandList extends CommandPlayerThreadExec {

    private String worldName = null;
    private Type type = null;

    /**
     * Instantiates a new command list.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandList(CommandEntities entity) throws SecuboidCommandException {

	super(entity);

    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

	String curArg = entity.argList.getNext();

	if (curArg != null) {
	    if (curArg.equalsIgnoreCase("world")) {

		// Get worldName
		worldName = entity.argList.getNext();
		if (worldName == null) {
		    // No worldName has parameter
		    worldName = entity.player.getLocation().getWorld().getName().toLowerCase();
		}

	    } else if (curArg.equalsIgnoreCase("type")) {

		// Get the category name
		String typeName = entity.argList.getNext();

		if (typeName != null) {
		    type = Secuboid.getThisPlugin().getTypes().getType(typeName);
		}

		if (type == null) {
		    throw new SecuboidCommandException("CommandList", entity.sender, "COMMAND.LAND.TYPENOTEXIST");
		}

	    } else {

		// Get the player Container
		entity.argList.setPos(0);
		pc = entity.argList.getPlayerContainerFromArg(null, null);

	    }
	}

	Secuboid.getThisPlugin().getPlayersCache().getUUIDWithNames(this, pc);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandPlayerThreadExec#commandThreadExecute(me.tabinol.secuboid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
	    throws SecuboidCommandException {

	convertPcIfNeeded(playerCacheEntry);

	// Check if the player is AdminMod or send only owned lands
	Collection<RealLand> lands;

	if (entity.playerConf.isAdminMod()) {
	    lands = Secuboid.getThisPlugin().getLands().getLands();
	} else {
	    lands = Secuboid.getThisPlugin().getLands().getLands(entity.playerConf.getPlayerContainer());
	}

	// Get the list of the land
	StringBuilder stList = new StringBuilder();
	stList.append(ChatColor.YELLOW);

	for (RealLand land : lands) {
	    if (((worldName != null && worldName.equals(land.getWorldName()))
		    || (type != null && type == land.getType())
		    || (worldName == null && type == null))
		    && (pc == null || land.getOwner().equals(pc))) {
		stList.append(land.getName()).append(" ");
	    }
	}

	new ChatPage("COMMAND.LAND.LISTSTART", stList.toString(), entity.player, null).getPage(1);
    }
}
