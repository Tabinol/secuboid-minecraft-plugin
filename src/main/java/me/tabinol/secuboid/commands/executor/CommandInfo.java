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
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import static me.tabinol.secuboid.config.Config.NEWLINE;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboidapi.parameters.IPermissionType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * The Class CommandInfo.
 */
@InfoCommand(name="info", aliases={"current", "here"})
public class CommandInfo extends CommandExec {

    /** The area. */
    private ICuboidArea area;
    
    /** The player. */
    private final Player player;
    
    /** The arg list. */
    private final ArgList argList;

    /**
     * Instantiates a new command info.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandInfo(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
        player = entity.player;
        Location playerloc = entity.player.getLocation();
        area = Secuboid.getThisPlugin().iLands().getCuboidArea(playerloc);
        argList = entity.argList;
    }

    // called from the bone
    /**
     * Instantiates a new command info.
     *
     * @param player the player
     * @param area the area
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandInfo(Player player, ICuboidArea area) throws SecuboidCommandException {

        super(null);
        this.player = player;
        this.area = area;
        argList = null;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        land = null;

        // Get the land name from arg
        if (argList != null && !argList.isLast()) {
            land = Secuboid.getThisPlugin().iLands().getLand(argList.getNext());

            if (land == null) {
                throw new SecuboidCommandException("CommandInfo", player, "COMMAND.INFO.NOTEXIST");
            }

            // If the land is in parameter, cancel Area
            area = null;
        }

        // get the Land from area
        if (land == null && area != null) {
            land = area.getLand();
        }

        // Show the land
        if (land != null) {
            // Create list
            StringBuilder stList = new StringBuilder();
            stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.NAME",
                    ChatColor.GREEN + land.getName() + ChatColor.YELLOW, ChatColor.GREEN + land.getUUID().toString() + ChatColor.YELLOW));
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.PRIORITY", land.getPriority() + ""));
            if(land.isForSale()) {
            	stList.append(ChatColor.RED + " " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.FORSALE"));
            }
            if(land.isForRent() && !land.isRented()) {
            	stList.append(ChatColor.RED + " " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.FORRENT"));
            }
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.TYPE", 
            		land.getType() != null ? land.getType().getName() : "-null-"));
            if(land.getParent() != null) {
              	stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.PARENT", land.getParent().getName()));
            }
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.OWNER", land.getOwner().getPrint()));
            if(land.isRented()) {
            	stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.TENANT", land.getTenant().getPrint()));
            }
            stList.append(NEWLINE);
            stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.MAINPERMISSION",
                    getPermissionInColForPl(land, PermissionList.BUILD.getPermissionType()) + " "
                    + getPermissionInColForPl(land, PermissionList.USE.getPermissionType()) + " "
                    + getPermissionInColForPl(land, PermissionList.OPEN.getPermissionType())));
            stList.append(NEWLINE);
            if (area != null) {
                stList.append(ChatColor.YELLOW + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.LAND.ACTIVEAREA",
                        "ID: " + area.getKey() + ", " + area.getPrint()));
                stList.append(NEWLINE);
            }
            // Create the multiple page
            new ChatPage("COMMAND.INFO.LAND.LISTSTART", stList.toString(), player, land.getName()).getPage(1);

        } else {
            player.sendMessage(ChatColor.GRAY + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.INFO.NOLAND"));
        }
    }

    /**
     * Gets the permission in col for pl.
     *
     * @param land the land
     * @param pt the pt
     * @return the permission in col for pl
     */
    private String getPermissionInColForPl(ILand land, IPermissionType pt) {

        boolean result = land.checkPermissionAndInherit(player, pt);

        if (result) {
            return ChatColor.GREEN + pt.getName();
        } else {
            return ChatColor.RED + pt.getName();
        }
    }
}
