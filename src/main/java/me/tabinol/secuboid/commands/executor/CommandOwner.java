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
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandOwner.
 */
@InfoCommand(name = "owner", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@playerContainer" }) //
        })
public final class CommandOwner extends CommandPlayerThreadExec {

    /**
     * Instantiates a new command owner.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandOwner(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);

        pc = argList.getPlayerContainerFromArg(
                new PlayerContainerType[] { PlayerContainerType.EVERYBODY, PlayerContainerType.OWNER });
        secuboid.getPlayersCache().getUUIDWithNames(this, pc);
    }

    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry) throws SecuboidCommandException {

        convertPcIfNeeded(playerCacheEntry);

        landSelectNullable.setOwner(pc);
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                + secuboid.getLanguage().getMessage("COMMAND.OWNER.ISDONE", pc.getPrint(), landSelectNullable.getName()));

        // Cancel the selection
        new CommandCancel(secuboid, null, sender, argList).commandExecute();

    }
}
