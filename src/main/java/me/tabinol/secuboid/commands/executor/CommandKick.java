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
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * The Class CommandKick.
 */
@InfoCommand(name = "kick", forceParameter = true)
public class CommandKick extends CommandExec {

    /**
     * The arg list.
     */
    private final ArgList argList;

    /**
     * The player.
     */
    private final Player player;

    /**
     * Instantiates a new command kick.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandKick(CommandEntities entity) throws SecuboidCommandException {

	super(entity);
	argList = entity.argList;
	player = entity.player;

    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

	String playerKickName = argList.getNext();

	getLandFromCommandIfNoLandSelected();

	// Only if it is from Kick command
	if (entity != null) {
	    checkSelections(true, null);
	    checkPermission(true, true, PermissionList.LAND_KICK.getPermissionType(), null);
	}

	// No player name?
	if (playerKickName == null) {
	    throw new SecuboidCommandException("Kicked", player, "COMMAND.KICK.PLAYERNULL");
	}

	@SuppressWarnings("deprecation")
	Player playerKick = Secuboid.getThisPlugin().getServer().getPlayer(playerKickName);

	// Player not in land?
	if (playerKick == null || !land.isPlayerinLandNoVanish(playerKick, player)
		|| Secuboid.getThisPlugin().getPlayerConf().get(playerKick).isAdminMode()
		|| playerKick.hasPermission("secuboid.bypassban")) {
	    throw new SecuboidCommandException("Kicked", player, "COMMAND.KICK.NOTINLAND");
	}

	//Kick the player
	playerKick.teleport(playerKick.getLocation().getWorld().getSpawnLocation());
	player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.KICK.DONE", playerKickName, land.getName()));
	playerKick.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.KICK.KICKED", land.getName()));
	Secuboid.getThisPlugin().getLog().write("Player " + playerKick + " kicked from " + land.getName() + ".");
    }
}
