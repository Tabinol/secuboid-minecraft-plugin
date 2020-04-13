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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import static me.tabinol.secuboid.config.Config.NEWLINE;

/**
 * The Class CommandInfo.
 */
@InfoCommand(name = "info", aliases = { "current", "here" }, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@land" }),//
        })
public final class CommandInfo extends CommandExec {

    /**
     * The area.
     */
    private Area area;

    /**
     * Instantiates a new command info.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandInfo(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
        Location playerloc = player.getLocation();
        area = secuboid.getLands().getArea(playerloc);
    }

    /**
     * Instantiates a new command info (when a user click).
     *
     * @param secuboid secuboid instance
     * @param sender   the sender
     * @param area     the aera
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandInfo(Secuboid secuboid, CommandSender sender, Area area) throws SecuboidCommandException {

        super(secuboid, null, sender, null);
        this.area = area;
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        landSelectNullable = null;

        // Get the land name from arg
        if (argList != null && !argList.isLast()) {
            landSelectNullable = secuboid.getLands().getLand(argList.getNext());

            if (landSelectNullable == null) {
                throw new SecuboidCommandException(secuboid, "CommandInfo", player, "COMMAND.INFO.NOTEXIST");
            }

            // If the land is in parameter, cancel Area
            area = null;
        }

        // get the Land from area
        if (landSelectNullable == null && area != null) {
            landSelectNullable = area.getLand();
        }

        // Show the land
        if (landSelectNullable != null) {
            // Create list
            StringBuilder stList = new StringBuilder();
            stList.append(ChatColor.YELLOW)
                    .append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.NAME",
                            ChatColor.GREEN + landSelectNullable.getName() + ChatColor.YELLOW,
                            ChatColor.GREEN + landSelectNullable.getUUID().toString() + ChatColor.YELLOW));
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW)
                    .append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.PRIORITY", landSelectNullable.getPriority() + ""));
            if (landSelectNullable.isForSale()) {
                stList.append(ChatColor.RED).append(" ")
                        .append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.FORSALE"));
            }
            if (landSelectNullable.isForRent() && !landSelectNullable.isRented()) {
                stList.append(ChatColor.RED).append(" ")
                        .append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.FORRENT"));
            }
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW).append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.TYPE",
                    landSelectNullable.getType() != null ? landSelectNullable.getType().getName() : "-null-"));
            if (landSelectNullable.getParent() != null) {
                stList.append(ChatColor.YELLOW).append(
                        secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.PARENT", landSelectNullable.getParent().getName()));
            }
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW)
                    .append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.OWNER", landSelectNullable.getOwner().getPrint()));
            if (landSelectNullable.isRented()) {
                stList.append(ChatColor.YELLOW).append(
                        secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.TENANT", landSelectNullable.getTenant().getPrint()));
            }
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW)
                    .append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.MAINPERMISSION",
                            getPermissionInColForPl(landSelectNullable, PermissionList.BUILD.getPermissionType()) + " "
                                    + getPermissionInColForPl(landSelectNullable, PermissionList.USE.getPermissionType()) + " "
                                    + getPermissionInColForPl(landSelectNullable, PermissionList.OPEN.getPermissionType())));
            stList.append(NEWLINE);
            if (area != null) {
                stList.append(ChatColor.YELLOW).append(secuboid.getLanguage().getMessage("COMMAND.INFO.LAND.ACTIVEAREA",
                        "ID: " + area.getKey() + ", " + area.getPrint()));
                stList.append(NEWLINE);
            }
            // Create the multiple page
            new ChatPage(secuboid, "COMMAND.INFO.LAND.LISTSTART", stList.toString(), player, landSelectNullable.getName()).getPage(1);

        } else {
            player.sendMessage(
                    ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INFO.NOLAND"));
        }
    }

    /**
     * Gets the permission in col for pl.
     *
     * @param land the land
     * @param pt   the pt
     * @return the permission in col for pl
     */
    private String getPermissionInColForPl(Land land, PermissionType pt) {

        boolean result = land.getPermissionsFlags().checkPermissionAndInherit(player, pt);

        if (result) {
            return ChatColor.GREEN + pt.getName();
        } else {
            return ChatColor.RED + pt.getName();
        }
    }
}
