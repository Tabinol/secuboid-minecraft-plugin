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
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Class CommandKick.
 */
@InfoCommand(name = "kick", forceParameter = true)
public class CommandKick extends CommandExec {

    /**
     * Instantiates a new command kick.
     *
     * @param secuboid secuboid instance
     * @param infoCommand the info command
     * @param sender the sender
     * @param argList the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandKick(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
	    throws SecuboidCommandException {

	super(secuboid, infoCommand, sender, argList);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

	String playerKickName = argList.getNext();

	getLandFromCommandIfNoLandSelected();

	// Only if it is from Kick command
	checkSelections(true, null);
	checkPermission(true, true, PermissionList.LAND_KICK.getPermissionType(), null);

	// No player name?
	if (playerKickName == null) {
	    throw new SecuboidCommandException(secuboid, "Kicked", player, "COMMAND.KICK.PLAYERNULL");
	}

	@SuppressWarnings("deprecation")
	Player playerKick = secuboid.getServer().getPlayer(playerKickName);

	// Player not in land?
	if (playerKick == null || !land.isPlayerinLandNoVanish(playerKick, player)
		|| secuboid.getPlayerConf().get(playerKick).isAdminMode()
		|| playerKick.hasPermission("secuboid.bypassban")) {
	    throw new SecuboidCommandException(secuboid, "Kicked", player, "COMMAND.KICK.NOTINLAND");
	}

	//Kick the player
	playerKick.teleport(playerKick.getLocation().getWorld().getSpawnLocation());
	player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.KICK.DONE", playerKickName, land.getName()));
	playerKick.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.KICK.KICKED", land.getName()));
	secuboid.getLog().write("Player " + playerKick + " kicked from " + land.getName() + ".");
    }
}
