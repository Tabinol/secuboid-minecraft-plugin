/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.ConfirmEntry;
import me.tabinol.secuboid.commands.ConfirmEntry.ConfirmType;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandArea.
 */
@InfoCommand(name = "area", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "add", "list", "remove", "replace" }), //
                @CompletionMap(regex = "^(remove|replace)$", completions = { "@areaLand" }) //
        })
public final class CommandArea extends CommandCollisionsThreadExec {

    private String fonction;

    /**
     * Instantiates a new command area.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandArea(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        fonction = argList.getNext();

        if (fonction.equalsIgnoreCase("add")) {

            checkPermission(true, true, null, null);
            checkSelections(true, true);

            Area area = playerConf.getSelection().getArea();

            // Check for collision
            checkCollision(area.getWorldName(), landSelectNullable.getName(), landSelectNullable, null,
                    LandAction.AREA_ADD, 0, area, landSelectNullable.getParent(), landSelectNullable.getOwner(), true);

        } else if (fonction.equalsIgnoreCase("remove") || fonction.equalsIgnoreCase("replace")) {

            checkPermission(true, true, null, null);
            checkSelections(true, null);

            String areaNbStr = argList.getNext();
            int areaNb = 0;

            // check here if there is an area to replace
            Area areaToReplace = playerConf.getSelection().getAreaToReplace();
            if (areaToReplace != null) {
                areaNb = areaToReplace.getKey();
            }

            // set area to the only one if there is only one area
            if (landSelectNullable.getAreas().size() == 1 && areaNbStr == null && areaNb == 0) {
                areaNb = landSelectNullable.getAreas().iterator().next().getKey();
            }

            // 0 is same has not set
            if (areaNb == 0) {
                if (areaNbStr == null) {
                    throw new SecuboidCommandException(secuboid, "Area", player, "COMMAND.REMOVE.AREA.EMPTY");
                }
                try {
                    areaNb = Integer.parseInt(areaNbStr);
                } catch (NumberFormatException ex) {
                    throw new SecuboidCommandException(secuboid, "Area", player, "COMMAND.REMOVE.AREA.INVALID");
                }
                if (landSelectNullable.getArea(areaNb) == null) {
                    throw new SecuboidCommandException(secuboid, "Area", player, "COMMAND.REMOVE.AREA.INVALID");
                }
            }

            // Only for a remove
            if (fonction.equalsIgnoreCase("remove")) {

                // Check for collision
                checkCollision(landSelectNullable.getWorldName(), fonction, landSelectNullable, null,
                        LandAction.AREA_REMOVE, areaNb, null, landSelectNullable.getParent(),
                        landSelectNullable.getOwner(), true);

            } else {

                // Only for a replace
                checkSelections(true, true);

                Area area = playerConf.getSelection().getArea();

                // Check for collision
                checkCollision(landSelectNullable.getWorldName(), landSelectNullable.getName(), landSelectNullable,
                        null, LandAction.AREA_MODIFY, areaNb, area, landSelectNullable.getParent(),
                        landSelectNullable.getOwner(), true);
            }

        } else if (fonction.equalsIgnoreCase("list")) {

            checkSelections(true, null);
            StringBuilder stList = new StringBuilder();
            for (Map.Entry<Integer, Area> entry : landSelectNullable.getIdsAndAreas().entrySet()) {
                stList.append("ID: ").append(entry.getKey()).append(", ").append(entry.getValue().getPrint())
                        .append(Config.NEWLINE);
            }
            new ChatPage(secuboid, "COMMAND.AREA.LISTSTART", stList.toString(), sender, landSelectNullable.getName())
                    .getPage(1);
        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", sender, "GENERAL.MISSINGINFO");
        }
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {

        if (collisions.hasCollisions()) {
            return;
        }

        if (fonction.equalsIgnoreCase("add")) {

            // Add Area
            newArea.setApproved();
            landSelectNullable.addArea(newArea, collisions.getPrice());

            player.sendMessage(ChatColor.GREEN + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", landSelectNullable.getName()));
            new CommandCancel(secuboid, null, sender, null).commandExecute();
            playerConf.getSelection().refreshLand();
        } else if (fonction.equalsIgnoreCase("remove")) {

            // Check if exist
            if (landSelectNullable.getArea(removeId) == null) {
                throw new SecuboidCommandException(secuboid, "Area", sender, "COMMAND.REMOVE.AREA.INVALID");
            }

            playerConf.setConfirm(new ConfirmEntry(ConfirmType.REMOVE_AREA, landSelectNullable, removeId));
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.CONFIRM"));

        } else if (fonction.equalsIgnoreCase("replace")) {

            // Replace Area
            newArea.setApproved();
            landSelectNullable.replaceArea(removeId, newArea, collisions.getPrice());

            player.sendMessage(ChatColor.GREEN + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.CREATE.AREA.ISDONE", landSelectNullable.getName()));
            new CommandCancel(secuboid, null, sender, null).commandExecute();
            playerConf.getSelection().refreshLand();
        }

    }
}
