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

import java.util.Map;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.*;
import me.tabinol.secuboid.commands.ConfirmEntry.ConfirmType;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import org.bukkit.ChatColor;

/**
 * The Class CommandArea.
 */
@InfoCommand(name = "area", forceParameter = true)
public class CommandArea extends CommandCollisionsThreadExec {

    private String fonction;

    /**
     * Instantiates a new command area.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandArea(CommandEntities entity) throws SecuboidCommandException {

	super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

	fonction = entity.argList.getNext();

	if (fonction.equalsIgnoreCase("add")) {

	    checkPermission(true, true, null, null);
	    checkSelections(true, true);

	    Area area = entity.playerConf.getSelection().getArea();

	    // Check for collision
	    checkCollision(land.getName(), land, null, LandAction.AREA_ADD, 0, area, land.getParent(),
		    land.getOwner(), true);

	} else if (fonction.equalsIgnoreCase("remove") || fonction.equalsIgnoreCase("replace")) {

	    checkPermission(true, true, null, null);
	    checkSelections(true, null);

	    String areaNbStr = entity.argList.getNext();
	    int areaNb = 0;

	    // check here if there is an area to replace
	    Area areaToReplace = entity.playerConf.getSelection().getAreaToReplace();
	    if (areaToReplace != null) {
		areaNb = areaToReplace.getKey();
	    }

	    // set area to the only one if there is only one area
	    if (land.getAreas().size() == 1 && areaNbStr == null && areaNb == 0) {
		areaNb = land.getAreas().iterator().next().getKey();
	    }

	    // 0 is same has not set
	    if (areaNb == 0) {
		if (areaNbStr == null) {
		    throw new SecuboidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.EMPTY");
		}
		try {
		    areaNb = Integer.parseInt(areaNbStr);
		} catch (NumberFormatException ex) {
		    throw new SecuboidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
		}
		if (land.getArea(areaNb) == null) {
		    throw new SecuboidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
		}
	    }

	    // Only for a remove
	    if (fonction.equalsIgnoreCase("remove")) {

		// Check for collision
		checkCollision(fonction, land, null, LandAction.AREA_REMOVE, areaNb, null, land.getParent(),
			land.getOwner(), true);

	    } else {

		//Only for a replace
		checkSelections(true, true);

		Area area = entity.playerConf.getSelection().getArea();

		// Check for collision
		checkCollision(land.getName(), land, null, LandAction.AREA_MODIFY, areaNb, area, land.getParent(),
			land.getOwner(), true);
	    }

	} else if (fonction.equalsIgnoreCase("list")) {

	    checkSelections(true, null);
	    StringBuilder stList = new StringBuilder();
	    for (Map.Entry<Integer, Area> entry : land.getIdsAndAreas().entrySet()) {
		stList.append("ID: ").append(entry.getKey()).append(", ").append(entry.getValue().getPrint()).append(Config.NEWLINE);
	    }
	    new ChatPage("COMMAND.AREA.LISTSTART", stList.toString(), entity.sender, land.getName()).getPage(1);
	} else {
	    throw new SecuboidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
	}
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {

	if (collisions.hasCollisions()) {
	    return;
	}

	if (fonction.equalsIgnoreCase("add")) {

	    // Add Area
	    ((Land) land).addArea(newArea, collisions.getPrice());

	    entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
	    Secuboid.getThisPlugin().getLog().write(entity.playerName + " have create an area named " + land.getName() + ".");
	    new CommandCancel(entity.playerConf, false).commandExecute();
	    entity.playerConf.getSelection().refreshLand();
	} else if (fonction.equalsIgnoreCase("remove")) {

	    // Check if exist
	    if (land.getArea(removeId) == null) {
		throw new SecuboidCommandException("Area", entity.sender, "COMMAND.REMOVE.AREA.INVALID");
	    }

	    entity.playerConf.setConfirm(new ConfirmEntry(
		    ConfirmType.REMOVE_AREA, land, removeId));
	    entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.CONFIRM"));

	} else if (fonction.equalsIgnoreCase("replace")) {

	    // Replace Area
	    ((Land) land).replaceArea(removeId, newArea, collisions.getPrice());

	    entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", land.getName()));
	    Secuboid.getThisPlugin().getLog().write(entity.playerName + " have create an area named " + land.getName() + ".");
	    new CommandCancel(entity.playerConf, false).commandExecute();
	    entity.playerConf.getSelection().refreshLand();
	}

    }
}
