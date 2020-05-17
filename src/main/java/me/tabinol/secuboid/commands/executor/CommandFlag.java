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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;

/**
 * The Class CommandFlag.
 */
@InfoCommand(name = "flag", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "add", "remove", "list" }), //
                @CompletionMap(regex = "^(add|remove)$", completions = { "@flag" }), //
                @CompletionMap(regex = "^add (.*)$", completions = { "@boolean" }) //
        })
public final class CommandFlag extends CommandExec {

    private List<LandPermissionsFlags> precDL; // Listed Precedent lands (no duplicates)
    private StringBuilder stList;

    /**
     * Instantiates a new command flag.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandFlag(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        String curArg = argList.getNext();

        if (curArg.equalsIgnoreCase("add")) {

            // Permission check is on getFlagFromArg
            Flag landFlag = argList.getFlagFromArg(playerConf.isAdminMode(), landSelectNullable.isOwner(player));

            if (!landFlag.getFlagType().isRegistered()) {
                throw new SecuboidCommandException(secuboid, "Flag not registered", player, "COMMAND.FLAGS.FLAGNULL");
            }

            landSelectNullable.getPermissionsFlags().addFlag(landFlag);
            player.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE",
                            landFlag.getFlagType().toString(), landFlag.getValue().getValuePrint() + ChatColor.YELLOW));

        } else if (curArg.equalsIgnoreCase("remove")) {

            FlagType flagType = argList.getFlagTypeFromArg(playerConf.isAdminMode(), landSelectNullable.isOwner(player));
            if (!landSelectNullable.getPermissionsFlags().removeFlag(flagType)) {
                throw new SecuboidCommandException(secuboid, "Flags", player, "COMMAND.FLAGS.REMOVENOTEXIST");
            }
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));

        } else if (curArg.equalsIgnoreCase("list")) {

            precDL = new ArrayList<>();
            stList = new StringBuilder();

            // For the actual land
            importDisplayFlagsFrom(landSelectNullable.getPermissionsFlags(), false);

            // For default Type
            if (landSelectNullable.getType() != null) {
                stList.append(ChatColor.DARK_GRAY)
                        .append(secuboid.getLanguage().getMessage("GENERAL.FROMDEFAULTTYPE", landSelectNullable.getType().getName()))
                        .append(Config.NEWLINE);
                importDisplayFlagsFrom(secuboid.getLands().getDefaultConf(landSelectNullable.getType()), false);
            }

            // For parent (if exist)
            Land parLand = landSelectNullable;
            while ((parLand = parLand.getParent()) != null) {
                stList.append(ChatColor.DARK_GRAY).append(secuboid.getLanguage().getMessage("GENERAL.FROMPARENT",
                        ChatColor.GREEN + parLand.getName() + ChatColor.DARK_GRAY)).append(Config.NEWLINE);
                importDisplayFlagsFrom(parLand.getPermissionsFlags(), true);
            }

            // For world
            stList.append(ChatColor.DARK_GRAY)
                    .append(secuboid.getLanguage().getMessage("GENERAL.FROMWORLD", landSelectNullable.getWorldName()))
                    .append(Config.NEWLINE);
            importDisplayFlagsFrom(secuboid.getLands().getOutsideLandPermissionsFlags(landSelectNullable.getWorldName()), true);

            new ChatPage(secuboid, "COMMAND.FLAGS.LISTSTART", stList.toString(), player, landSelectNullable.getName()).getPage(1);

        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", player, "GENERAL.MISSINGINFO");
        }
    }

    private void importDisplayFlagsFrom(LandPermissionsFlags landPermissionsFlags, boolean onlyInherit) {

        final StringBuilder stSubList = new StringBuilder();
        for (Flag flag : landPermissionsFlags.getFlags()) {
            if (stSubList.length() != 0 && !stSubList.toString().endsWith(" ")) {
                stSubList.append(" ");
            }
            if ((!onlyInherit || flag.isInheritable()) && !flagInList(flag)) {
                stSubList.append(flag.getFlagType().getPrint()).append(":").append(flag.getValue().getValuePrint());
            }
        }

        if (stSubList.length() > 0) {
            stList.append(stSubList).append(Config.NEWLINE);
            precDL.add(landPermissionsFlags);
        }
    }

    private boolean flagInList(Flag flag) {

        for (LandPermissionsFlags listLandPermissionsFlags : precDL) {
            for (Flag listFlag : listLandPermissionsFlags.getFlags()) {
                if (flag.getFlagType() == listFlag.getFlagType()) {
                    return true;
                }
            }
        }

        return false;
    }
}
