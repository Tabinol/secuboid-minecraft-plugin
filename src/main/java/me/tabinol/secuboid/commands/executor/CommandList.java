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

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;

/**
 * The Class CommandList.
 *
 * @author Tabinol
 */
@InfoCommand(name = "list", //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "world", "type", "@playerContainer" }), //
                @CompletionMap(regex = "^type$", completions = { "@type" }) //
        })
public final class CommandList extends CommandPlayerThreadExec {

    private String worldName = null;
    private Type type = null;

    /**
     * Instantiates a new command list.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandList(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);

    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        String curArg = argList.getNext();

        if (curArg != null) {
            if (curArg.equalsIgnoreCase("world")) {

                // Get worldName
                worldName = argList.getNext();
                if (worldName == null) {
                    // No worldName has parameter
                    worldName = player.getLocation().getWorld().getName().toLowerCase();
                }

            } else if (curArg.equalsIgnoreCase("type")) {

                // Get the category name
                String typeName = argList.getNext();

                if (typeName != null) {
                    type = secuboid.getTypes().getType(typeName);
                }

                if (type == null) {
                    throw new SecuboidCommandException(secuboid, "CommandList", sender, "COMMAND.LAND.TYPENOTEXIST");
                }

            } else {

                // Get the player Container
                argList.setPosZero();
                pc = argList.getPlayerContainerFromArg(null);

            }
        }

        secuboid.getPlayersCache().getUUIDWithNameAsync(this, pc);
    }

    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry) throws SecuboidCommandException {

        convertPcIfNeeded(playerCacheEntry);

        // Check if the player is AdminMode or send only owned lands
        Collection<Land> lands;

        if (playerConf.isAdminMode()) {
            lands = secuboid.getLands().getLands();
        } else {
            lands = secuboid.getLands().getLands(playerConf.getPlayerContainer());
        }

        // Get the list of the land
        StringBuilder stList = new StringBuilder();
        stList.append(ChatColor.YELLOW);

        for (Land land : lands) {
            if (((worldName != null && worldName.equals(land.getWorldName()))
                    || (type != null && type == land.getType()) || (worldName == null && type == null))
                    && (pc == null || land.getOwner().equals(pc))) {
                stList.append(land.getName()).append(" ");
            }
        }

        new ChatPage(secuboid, "COMMAND.LAND.LISTSTART", stList.toString(), player, null).getPage(1);
    }
}
