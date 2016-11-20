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
import me.tabinol.secuboid.commands.*;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.LandSelection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Class CommandSelect.
 */
@InfoCommand(name = "select", aliases = {"sel"})
public class CommandSelect extends CommandCollisionsThreadExec {

    /**
     * The location.
     */
    private final Location location;

    /**
     * Instantiates a new command select.
     *
     * @param secuboid secuboid instance
     * @param infoCommand the info command
     * @param sender the sender
     * @param argList the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelect(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
	    throws SecuboidCommandException {

	super(secuboid, infoCommand, sender, argList);
	location = null;
    }

    /**
     * Instantiates a new command select (from a player action).
     *
     * @param secuboid secuboid instance
     * @param player the player
     * @param argList the arg list
     * @param location the location
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelect(Secuboid secuboid, Player player, ArgList argList, Location location) throws SecuboidCommandException {

	super(secuboid, null, player, argList);
	this.location = location;
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

	// Done nothing but for future use
	checkSelections(null, null);

	String curArg;

	if (playerConf.getSelection().getArea() == null) {
	    secuboid.getLog().debug(player.getName() + " join select mode");

	    if (!argList.isLast()) {

		curArg = argList.getNext();
		if (curArg.equalsIgnoreCase("worldedit") || curArg.equalsIgnoreCase("we")) {
		    if (secuboid.getDependPlugin().getWorldEdit() == null) {
			throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
		    }
		    new CommandSelectWorldedit(secuboid, player, playerConf).MakeSelect();

		} else {

		    RealLand landtest;
		    if (curArg.equalsIgnoreCase("here")) {

			// add select Here to select the the cuboid
			if (location != null) {

			    // With an item
			    landtest = secuboid.getLands().getLand(location);
			} else {

			    // Player location
			    landtest = secuboid.getLands().getLand(player.getLocation());
			}

		    } else {

			landtest = secuboid.getLands().getLand(curArg);
		    }

		    if (landtest == null) {
			throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.NOLAND");

		    }
		    PlayerContainer ownerLocal = landtest.getOwner();

		    if (!ownerLocal.hasAccess(player) && !playerConf.isAdminMode()
			    && !(landtest.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.RESIDENT_MANAGER.getPermissionType())
			    && (landtest.isResident(player) || landtest.isOwner(player)))) {
			throw new SecuboidCommandException(secuboid, "CommandSelect", player, "GENERAL.MISSINGPERMISSION");
		    }
		    if (playerConf.getSelection().getLand() == null) {

			playerConf.getSelection().addSelection(new LandSelection(secuboid, player, landtest));

			player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY + secuboid.getLanguage().getMessage("COMMAND.SELECT.SELECTEDLAND", landtest.getName()));
			playerConf.setAutoCancelSelect(true);
		    } else {

			player.sendMessage(ChatColor.RED + "[Secuboid] " + ChatColor.DARK_GRAY + secuboid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
		    }
		}
	    } else {

		player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
		player.sendMessage(ChatColor.DARK_GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
		AreaSelection select = new AreaSelection(secuboid, player, null, false, AreaType.CUBOID, AreaSelection.MoveType.ACTIVE);
		playerConf.getSelection().addSelection(select);
		playerConf.setAutoCancelSelect(true);
	    }
	} else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {

	    //if (playerConf.getSelection().getLand() != null) {
	    //    throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.CANTDONE");
	    //}
	    //if (playerConf.getSelection().getCuboidArea() != null) {
	    doSelectAreaDone();
	    //}

	} else if (curArg != null && curArg.equalsIgnoreCase("info")) {

	    doSelectAreaInfo();

	} else {
	    throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.ALREADY");
	}
    }

    /**
     * Do select area done.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void doSelectAreaDone() throws SecuboidCommandException {

	checkSelections(null, true);

	AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
	playerConf.getSelection().addSelection(new AreaSelection(secuboid, player, select.getVisualSelection().getArea(),
		false, null, AreaSelection.MoveType.PASSIVE));
	playerConf.setAutoCancelSelect(true);

	if (!select.getVisualSelection().hasCollision()) {

	    player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
		    + secuboid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
	} else {
	    player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.RED
		    + secuboid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
	}
    }

    /**
     * Do select area info.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void doSelectAreaInfo() throws SecuboidCommandException {

	checkSelections(null, true);

	AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
	Area area = select.getVisualSelection().getArea();

	player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO1",
		area.getPrint()));
	if (area.getVolume() != 0) {
	    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO2",
		    area.getVolume() + ""));
	}

	checkCollision(null, null, null, Collisions.LandAction.LAND_ADD,
		0, playerConf.getSelection().getArea(), null, playerConf.getPlayerContainer(), true);
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {
	// Info only

	double price;

	// Price (economy)
	price = collisions.getPriceLand();
	if (price != 0L) {
	    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO3",
		    secuboid.getPlayerMoney().toFormat(price)));
	}
	price = collisions.getPriceArea();
	if (price != 0L) {
	    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO4",
		    secuboid.getPlayerMoney().toFormat(price)));
	}
    }
}
