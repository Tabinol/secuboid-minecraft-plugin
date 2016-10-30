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
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;

/**
 *
 * @author michel
 */
@InfoCommand(name = "rent", forceParameter = true)
public class CommandRent extends CommandExec {

    /**
     *
     * @param entity
     * @throws SecuboidCommandException
     */
    public CommandRent(CommandEntities entity) throws SecuboidCommandException {

	super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

	checkSelections(true, null);
	checkPermission(true, true, null, null);
	if (!entity.playerConf.isAdminMode()) {
	    // If the player not adminmode, he must be owner && permission true
	    checkPermission(false, false, PermissionList.ECO_LAND_FOR_RENT.getPermissionType(), null);
	}

	String curArg = entity.argList.getNext();
	double rentPrice = 0;
	int rentRenew = 0;
	boolean rentAutoRenew = true;
	EcoSign ecoSign = null;

	// Check for sign in hand
	if (entity.player.getGameMode() != GameMode.CREATIVE && entity.player.getItemInHand().getType() != Material.SIGN) {
	    throw new SecuboidCommandException("Must have a sign in hand", entity.player, "COMMAND.ECONOMY.MUSTHAVEISIGN");
	}

	// If 'recreate'
	if (curArg.equalsIgnoreCase("recreate")) {
	    if (!land.isForRent()) {
		throw new SecuboidCommandException("The land is not for rent", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
	    }
	    try {
		ecoSign = new EcoSign(land, entity.player);
		ecoSign.createSignForRent(land.getRentPrice(), land.getRentRenew(), land.getRentAutoRenew(),
			land.isRented() ? land.getTenant().getPlayerName() : null); // Tenant name if the land is rented
		removeSignFromHand();
		if (!ecoSign.getLocation().getBlock().equals(land.getRentSignLoc().getBlock())) {
		    ecoSign.removeSign(land.getRentSignLoc());
		    land.setRentSignLoc(ecoSign.getLocation());
		}
	    } catch (SignException e) {
		throw new SecuboidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
	    }

	    entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));
	    Secuboid.getThisPlugin().getLog().write("Sign recreated for land " + land.getName() + " by: " + entity.playerName);

	    return;
	}

	// get price
	try {
	    rentPrice = Double.parseDouble(curArg);
	} catch (NumberFormatException ex) {
	    throw new SecuboidCommandException("Error in the command", entity.player, "GENERAL.MISSINGINFO");
	}

	// get renew
	curArg = entity.argList.getNext();
	try {
	    rentRenew = Integer.parseInt(curArg);
	} catch (NumberFormatException ex) {
	    throw new SecuboidCommandException("Error in the command", entity.player, "GENERAL.MISSINGINFO");
	}

	// get auto renew
	curArg = entity.argList.getNext();
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
	    throw new SecuboidCommandException("Land already for rent", entity.player, "COMMAND.ECONOMY.ALREADYRENT");
	}

	// Create Sign
	try {
	    ecoSign = new EcoSign(land, entity.player);
	    ecoSign.createSignForRent(rentPrice, rentRenew, rentAutoRenew, null);
	    removeSignFromHand();
	} catch (SignException e) {
	    throw new SecuboidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
	}
	land.setForRent(rentPrice, rentRenew, rentAutoRenew, ecoSign.getLocation());
	entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
	Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is set to for rent by: " + entity.playerName);
    }
}
