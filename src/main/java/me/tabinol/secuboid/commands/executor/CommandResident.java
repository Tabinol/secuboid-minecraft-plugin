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
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandResident.
 */
@InfoCommand(name = "resident", aliases = {"res"}, forceParameter = true)
public class CommandResident extends CommandPlayerThreadExec {

    private String fonction;

    /**
     * Instantiates a new command resident.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandResident(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, PermissionList.RESIDENT_MANAGER.getPermissionType(), null);

        // Double check: The player must be resident, owner or adminmode
        if (!playerConf.isAdminMode() && !land.isResident(player) && !land.isOwner(player)) {
            throw new SecuboidCommandException(secuboid, "No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
        }

        fonction = argList.getNext();

        if (fonction.equalsIgnoreCase("add")) {

            pc = argList.getPlayerContainerFromArg(land,
                    new PlayerContainerType[]{PlayerContainerType.EVERYBODY,
                            PlayerContainerType.OWNER, PlayerContainerType.VISITOR,
                            PlayerContainerType.RESIDENT});
            secuboid.getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("remove")) {

            pc = argList.getPlayerContainerFromArg(land, null);
            secuboid.getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getResidents().isEmpty()) {
                for (PlayerContainer pc : land.getResidents()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.RESIDENT.LISTROWNULL"));
            }
            new ChatPage(secuboid, "COMMAND.RESIDENT.LISTSTART", stList.toString(), player, land.getName()).getPage(1);

        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", player, "GENERAL.MISSINGINFO");
        }
    }

    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
            throws SecuboidCommandException {

        convertPcIfNeeded(playerCacheEntry);

        if (fonction.equalsIgnoreCase("add")) {

            land.addResident(pc);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.RESIDENT.ISDONE", pc.getPrint(), land.getName()));
            secuboid.getLog().write("Resident added: " + pc.toString());

        } else if (fonction.equalsIgnoreCase("remove")) {

            if (!land.removeResident(pc)) {
                throw new SecuboidCommandException(secuboid, "Resident", player, "COMMAND.RESIDENT.REMOVENOTEXIST");
            }
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.RESIDENT.REMOVEISDONE", pc.getPrint(), land.getName()));
            secuboid.getLog().write("Resident removed: " + pc.toString());
        }
    }
}
