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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;

/**
 * Represents an inventory command.
 */
@InfoCommand(name = "inv", aliases = { "inventory" }, allowConsole = true, forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "default", "loaddeath", "forcesave" }), //
                @CompletionMap(regex = "^default$", completions = { "save", "remove" }), //
                @CompletionMap(regex = "^loaddeath$", completions = { "@player" }) //
        })
public final class CommandInv extends CommandExec {

    /**
     * Instantiates a new command inventory.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandInv(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        // Verify if Multiple inventories is active
        if (secuboid.getInventoryConf() == null) {
            throw new SecuboidCommandException(secuboid, "Multiple Inventories is not active", player,
                    "COMMAND.INV.NOTACTIVE");
        }

        final String function = argList.getNext();

        if (function.equalsIgnoreCase("default")) {
            checkPermission(false, false, null, InventoryConfig.PERM_DEFAULT);
            saveDefault();
        } else if (function.equalsIgnoreCase("loaddeath")) {
            checkPermission(false, false, null, InventoryConfig.PERM_LOADDEATH);
            loadDeath();
        } else if (function.equalsIgnoreCase("forcesave")) {
            checkPermission(false, false, null, InventoryConfig.PERM_FORCESAVE);
            forceSave();
        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", sender, "GENERAL.MISSINGINFO");
        }
    }

    private void saveDefault() throws SecuboidCommandException {

        if (player == null) {
            throw new SecuboidCommandException(secuboid, "Impossible to do from console", Bukkit.getConsoleSender(),
                    "CONSOLE");
        }

        // Get the land name
        final String subCom = argList.getNext();

        if (subCom != null && subCom.equalsIgnoreCase("save")) {

            // Save the inventory
            secuboid.getInventoryListener().saveDefaultInventory(playerConf);
            player.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.DEFAULTSAVE"));

        } else if (subCom != null && subCom.equalsIgnoreCase("remove")) {

            // Remove inventory
            secuboid.getInventoryListener().removeDefaultInventory(playerConf);
            player.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.DEFAULTREMOVE"));

        } else {

            // Bad parameter
            player.sendMessage(
                    ChatColor.RED + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.ERRORSAVEREMOVE"));
        }
    }

    private void loadDeath() throws SecuboidCommandException {

        final String playerName = argList.getNext();

        if (playerName == null) {
            throw new SecuboidCommandException(secuboid, "No player!", sender, "COMMAND.INV.ERRORNOPLAYER");
        }

        // Check for player
        final Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            throw new SecuboidCommandException(secuboid, "Player offline!", sender, "COMMAND.INV.ERRORPLAYEROFFLINE");
        }

        // Check for the last time version
        int lastTime;
        final String stNumber = argList.getNext();
        if (stNumber != null) {
            try {
                lastTime = Integer.parseInt(stNumber);
            } catch (final NumberFormatException ex) {
                // Number unreadable
                lastTime = 1;
            }
        } else {
            lastTime = 1;
        }

        // Execute
        if (!secuboid.getInventoryListener().loadDeathInventory(player, lastTime)) {
            throw new SecuboidCommandException(secuboid, "Death save not found!", sender,
                    "COMMAND.INV.ERRORDEATHNOTFOUND");
        }

        player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                + secuboid.getLanguage().getMessage("COMMAND.INV.DEATHDONE", player.getName()));
    }

    private void forceSave() {
        secuboid.getInventoryListener().forceSave();
        player.sendMessage(
                ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.SAVEDONE"));
    }
}
