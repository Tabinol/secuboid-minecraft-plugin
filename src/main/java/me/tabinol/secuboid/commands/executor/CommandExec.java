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
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainerOwner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Class CommandExec.
 */
public abstract class CommandExec {

    /**
     * Secuboid instance.
     */
    final Secuboid secuboid;

    /**
     * The command.
     */
    final InfoCommand infoCommand;

    /**
     * The sender.
     */
    final CommandSender sender;

    /**
     * The arg list.
     */
    final ArgList argList;

    /**
     * The player.
     */
    final Player player;

    /**
     * The player name.
     */
    final String playerName;

    /**
     * The player conf.
     */
    final PlayerConfEntry playerConf;

    /**
     * The land.
     */
    protected RealLand land;

    /**
     * The is executable.
     */
    private boolean isExecutable = true;

    /**
     * The reset select cancel.
     */
    public boolean resetSelectCancel = false; // If reset select cancel is done (1 time only)

    /**
     * Instantiates a new command exec.
     *
     * @param secuboid secuboid instance
     * @param infoCommand the info command
     * @param sender the sender
     * @param argList the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected CommandExec(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
	    throws SecuboidCommandException {

	this.secuboid = secuboid;
	this.infoCommand = infoCommand;
	this.sender = sender;
	if (argList != null) {
	    this.argList = argList;
	} else {
	    this.argList = new ArgList(secuboid, new String[0], sender);
	}

	if (sender instanceof Player) {
	    player = (Player) sender;
	} else {
	    player = null;
	}

	playerName = sender.getName();
	playerConf = secuboid.getPlayerConf().get(sender);

	if (player != null) {
	    // get the land Selected or null
	    land = playerConf.getSelection().getLand();
	}

	if (player
		== null && !infoCommand.allowConsole()) {

	    // Send a message if this command is player only
	    throw new SecuboidCommandException(secuboid, "Impossible to do from console", Bukkit.getConsoleSender(), "CONSOLE");
	}

	// Show help if there is no more parameter and the command needs one
	if (infoCommand.forceParameter()
		&& argList != null && argList.isLast()) {
	    new CommandHelp(secuboid, infoCommand, sender, argList).commandExecute();
	    isExecutable = false;
	}
    }

    /**
     * Command execute.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    public abstract void commandExecute() throws SecuboidCommandException;

    /**
     * Checks if is executable.
     *
     * @return true, if is executable
     */
    public boolean isExecutable() {

	return isExecutable;
    }

    // Check for needed selection and not needed (null for no verification)
    /**
     * Check selections.
     *
     * @param mustBeSelectMode the must be select mode
     * @param mustBeAreaSelected the must be area selected
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected void checkSelections(Boolean mustBeSelectMode, Boolean mustBeAreaSelected) throws SecuboidCommandException {

	if (mustBeSelectMode != null) {
	    // Pasted to variable land, can take direcly
	    checkSelection(land != null, mustBeSelectMode, null, "GENERAL.JOIN.SELECTMODE",
		    playerConf.getSelection().getLand() != null);
	}
	if (mustBeAreaSelected != null) {
	    checkSelection(playerConf.getSelection().getArea() != null, mustBeAreaSelected, null, "GENERAL.JOIN.SELECTAREA", true);
	}
    }

    // Check selection for per type
    /**
     * Check selection.
     *
     * @param result the result
     * @param neededResult the needed result
     * @param messageTrue the message true
     * @param messageFalse the message false
     * @param startSelectCancel the start select cancel
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void checkSelection(boolean result, boolean neededResult, String messageTrue, String messageFalse,
	    boolean startSelectCancel) throws SecuboidCommandException {

	if (result != neededResult) {
	    if (result == true) {
		throw new SecuboidCommandException(secuboid, "Player Select", player, messageTrue);
	    } else {
		throw new SecuboidCommandException(secuboid, "Player Select", player, messageFalse);
	    }
	} else if (startSelectCancel && !resetSelectCancel && result == true) {

	    // Reset autocancel if there is a command executed that need it
	    playerConf.setAutoCancelSelect(true);
	    resetSelectCancel = true;
	}
    }

    // Check if the player has permission
    /**
     * Check permission.
     *
     * @param mustBeAdminMode the must be admin mod
     * @param mustBeOwner the must be owner
     * @param neededPerm the needed perm
     * @param bukkitPermission the bukkit permission
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected void checkPermission(boolean mustBeAdminMode, boolean mustBeOwner,
	    PermissionType neededPerm, String bukkitPermission) throws SecuboidCommandException {

	boolean canDo = false;

	if (mustBeAdminMode && playerConf.isAdminMode()) {
	    canDo = true;
	}
	if (mustBeOwner && (land == null || (land != null && new PlayerContainerOwner(land).hasAccess(player)))) {
	    canDo = true;
	}
	if (neededPerm != null && land.getPermissionsFlags().checkPermissionAndInherit(player, neededPerm)) {
	    canDo = true;
	}
	if (bukkitPermission != null && sender.hasPermission(bukkitPermission)) {
	    canDo = true;
	}

	// No permission, this is an exception
	if (canDo == false) {
	    throw new SecuboidCommandException(secuboid, "No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
	}
    }

    // The name says what it does!!!
    /**
     * Gets the land from command if no land selected.
     */
    protected void getLandFromCommandIfNoLandSelected() {

	if (land == null && !argList.isLast()) {
	    land = secuboid.getLands().getLand(argList.getNext());
	}
    }

    /**
     * Removes the sign from hand.
     */
    protected void removeSignFromHand() {

	if (player.getGameMode() != GameMode.CREATIVE) {
	    if (player.getItemInHand().getAmount() == 1) {
		player.setItemInHand(new ItemStack(Material.AIR));
	    } else {
		player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
	    }
	}
    }
}
