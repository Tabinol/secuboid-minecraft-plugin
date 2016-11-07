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
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 * The rent command class.
 *
 * @author michel
 */
@InfoCommand(name = "rent", forceParameter = true)
public class CommandRent extends CommandExec {

    /**
     * Instantiates a rent command.
     *
     * @param secuboid secuboid instance
     * @param infoCommand the info command
     * @param sender the sender
     * @param argList the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandRent(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
	    throws SecuboidCommandException {

	super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

	checkSelections(true, null);
	checkPermission(true, true, null, null);
	if (!playerConf.isAdminMode()) {
	    // If the player not adminmode, he must be owner && permission true
	    checkPermission(false, false, PermissionList.ECO_LAND_FOR_RENT.getPermissionType(), null);
	}

	String curArg = argList.getNext();
	double rentPrice = 0;
	int rentRenew = 0;
	boolean rentAutoRenew = true;
	EcoSign ecoSign = null;

	// Check for sign in hand
	if (player.getGameMode() != GameMode.CREATIVE && player.getItemInHand().getType() != Material.SIGN) {
	    throw new SecuboidCommandException(secuboid, "Must have a sign in hand", player, "COMMAND.ECONOMY.MUSTHAVEISIGN");
	}

	// If 'recreate'
	if (curArg.equalsIgnoreCase("recreate")) {
	    if (!land.isForRent()) {
		throw new SecuboidCommandException(secuboid, "The land is not for rent", player, "COMMAND.ECONOMY.ERRORCREATESIGN");
	    }
	    try {
		ecoSign = new EcoSign(secuboid, land, player);
		ecoSign.createSignForRent(land.getRentPrice(), land.getRentRenew(), land.getRentAutoRenew(),
			land.isRented() ? land.getTenant().getPlayerName() : null); // Tenant name if the land is rented
		removeSignFromHand();
		if (!ecoSign.getLocation().getBlock().equals(land.getRentSignLoc().getBlock())) {
		    ecoSign.removeSign(land.getRentSignLoc());
		    land.setRentSignLoc(ecoSign.getLocation());
		}
	    } catch (SignException e) {
		throw new SecuboidCommandException(secuboid, "Error in the command", player, "COMMAND.ECONOMY.ERRORCREATESIGN");
	    }

	    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));
	    secuboid.getLog().write("Sign recreated for land " + land.getName() + " by: " + playerName);

	    return;
	}

	// get price
	try {
	    rentPrice = Double.parseDouble(curArg);
	} catch (NumberFormatException ex) {
	    throw new SecuboidCommandException(secuboid, "Error in the command", player, "GENERAL.MISSINGINFO");
	}

	// get renew
	curArg = argList.getNext();
	try {
	    rentRenew = Integer.parseInt(curArg);
	} catch (NumberFormatException ex) {
	    throw new SecuboidCommandException(secuboid, "Error in the command", player, "GENERAL.MISSINGINFO");
	}

	// get auto renew
	curArg = argList.getNext();
	if (curArg != null) {
	    try {
		rentAutoRenew = Boolean.parseBoolean(curArg);
	    } catch (NumberFormatException ex) {
		// Default value
		rentAutoRenew = true;
	    }
	}

	// Land already for rent?
	if (land.isForRent()) {
	    throw new SecuboidCommandException(secuboid, "Land already for rent", player, "COMMAND.ECONOMY.ALREADYRENT");
	}

	// Create Sign
	try {
	    ecoSign = new EcoSign(secuboid, land, player);
	    ecoSign.createSignForRent(rentPrice, rentRenew, rentAutoRenew, null);
	    removeSignFromHand();
	} catch (SignException e) {
	    throw new SecuboidCommandException(secuboid, "Error in the command", player, "COMMAND.ECONOMY.ERRORCREATESIGN");
	}
	land.setForRent(rentPrice, rentRenew, rentAutoRenew, ecoSign.getLocation());
	player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
	secuboid.getLog().write("The land " + land.getName() + " is set to for rent by: " + playerName);
    }
}
