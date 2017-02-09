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

import java.util.ArrayList;
import java.util.List;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandPermission.
 */
@InfoCommand(name = "permission", aliases = {"perm"}, forceParameter = true)
public class CommandPermission extends CommandPlayerThreadExec {

    private List<Land> precDL; // Listed Precedent lands (no duplicates)
    private StringBuilder stList;

    private String fonction;

    /**
     * Instantiates a new command permission.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandPermission(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);

        fonction = argList.getNext();

        if (fonction.equalsIgnoreCase("add")) {

            pc = argList.getPlayerContainerFromArg(null);

            secuboid.getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("remove")) {

            pc = argList.getPlayerContainerFromArg(null);
            secuboid.getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

            precDL = new ArrayList<Land>();
            stList = new StringBuilder();

            // For the actual land
            importDisplayPermsFrom(land, false);

            // For default Type
            if (land.getType() != null) {
                stList.append(ChatColor.DARK_GRAY).append(secuboid.getLanguage().getMessage("GENERAL.FROMDEFAULTTYPE",
                        land.getType().getName())).append(Config.NEWLINE);
                importDisplayPermsFrom(secuboid.getLands().getDefaultConf(land.getType()), false);
            }

            // For parent (if exist)
            RealLand parLand = land;
            while ((parLand = parLand.getParent()) != null) {
                stList.append(ChatColor.DARK_GRAY).append(secuboid.getLanguage().getMessage("GENERAL.FROMPARENT",
                        ChatColor.GREEN + parLand.getName() + ChatColor.DARK_GRAY)).append(Config.NEWLINE);
                importDisplayPermsFrom(parLand, true);
            }

            // For world
            stList.append(ChatColor.DARK_GRAY).append(secuboid.getLanguage().getMessage("GENERAL.FROMWORLD",
                    land.getWorldName())).append(Config.NEWLINE);
            importDisplayPermsFrom((secuboid.getLands()).getOutsideArea(land.getWorldName()), true);

            new ChatPage(secuboid, "COMMAND.PERMISSION.LISTSTART", stList.toString(), player, land.getName()).getPage(1);

        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", player, "GENERAL.MISSINGINFO");
        }
    }

    private void importDisplayPermsFrom(Land land, boolean onlyInherit) {

        boolean addToList = false;

        for (PlayerContainer pc : land.getPermissionsFlags().getSetPCHavePermission()) {
            StringBuilder stSubList = new StringBuilder();

            for (Permission perm : land.getPermissionsFlags().getPermissionsForPC(pc)) {
                if ((!onlyInherit || perm.isInheritable()) && !permInList(pc, perm)) {
                    addToList = true;
                    stSubList.append(" ").append(perm.getPermType().getPrint()).append(":").append(perm.getValuePrint());
                }
            }

            // Append to list
            if (stSubList.length() > 0) {
                stList.append(ChatColor.WHITE).append(pc.getPrint()).append(":");
                stList.append(stSubList).append(Config.NEWLINE);
            }

        }

        if (addToList) {
            precDL.add(land);
        }
    }

    private boolean permInList(PlayerContainer pc, Permission perm) {

        for (Land listLand : precDL) {

            if (listLand.getPermissionsFlags().getSetPCHavePermission().contains(pc)) {
                for (Permission listPerm : listLand.getPermissionsFlags().getPermissionsForPC(pc)) {
                    if (perm.getPermType() == listPerm.getPermType()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
            throws SecuboidCommandException {

        convertPcIfNeeded(playerCacheEntry);

        if (fonction.equalsIgnoreCase("add")) {

            Permission perm = argList.getPermissionFromArg(playerConf.isAdminMode(), land.isOwner(player));

            if (!perm.getPermType().isRegistered()) {
                throw new SecuboidCommandException(secuboid, "Permission not registered", player, "COMMAND.PERMISSIONTYPE.TYPENULL");
            }

            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                    && perm.getValue() != perm.getPermType().getDefaultValue()
                    && land.isLocationInside(land.getWorld().getSpawnLocation())) {
                throw new SecuboidCommandException(secuboid, "Permission", player, "COMMAND.PERMISSION.NOENTERNOTINSPAWN");
            }
            land.getPermissionsFlags().addPermission(pc, perm);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getPermType().getPrint(),
                    pc.getPrint() + ChatColor.YELLOW, land.getName()));

        } else if (fonction.equalsIgnoreCase("remove")) {

            PermissionType pt = argList.getPermissionTypeFromArg(playerConf.isAdminMode(), land.isOwner(player));
            if (!land.getPermissionsFlags().removePermission(pc, pt)) {
                throw new SecuboidCommandException(secuboid, "Permission", player, "COMMAND.PERMISSION.REMOVENOTEXIST");
            }
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
        }
    }
}
