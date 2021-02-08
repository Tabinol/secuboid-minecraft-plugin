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

import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.inventories.Inventories;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.players.PlayerConfEntry;

/**
 * Represents an inventory command.
 */
@InfoCommand(name = "inv", aliases = { "inventory" }, allowConsole = true, forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "default", "forcesave", "list", "loaddeath", "purge" }), //
                @CompletionMap(regex = "^default$", completions = { "save", "remove" }), //
                @CompletionMap(regex = "^loaddeath$", completions = { "@player" }), //
                @CompletionMap(regex = "^purge$", completions = { "@inventory" }) //
        })
public final class CommandInv extends CommandExec {

    private final Inventories inventories;

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
        inventories = secuboid.getInventoriesOpt().orElse(null);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        // Verify if Multiple inventories is active
        if (inventories == null) {
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
        } else if (function.equalsIgnoreCase("list")) {
            checkPermission(false, false, null, InventoryConfig.PERM_LIST);
            list();
        } else if (function.equalsIgnoreCase("purge")) {
            checkPermission(false, false, null, InventoryConfig.PERM_PURGE);
            purge();
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
            inventories.saveDefaultInventory(playerConf);
            player.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.DEFAULTSAVE"));

        } else if (subCom != null && subCom.equalsIgnoreCase("remove")) {

            // Remove inventory
            final PlayerInvEntry playerInvEntry = playerConf.getPlayerInventoryCacheOpt().get().getCurInvEntry();
            inventories.removeInventoryDefault(playerInvEntry);
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
        if (!inventories.loadDeathInventory(player, lastTime)) {
            throw new SecuboidCommandException(secuboid, "Death save not found!", sender,
                    "COMMAND.INV.ERRORDEATHNOTFOUND");
        }

        player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                + secuboid.getLanguage().getMessage("COMMAND.INV.DEATHDONE", player.getName()));
    }

    private void forceSave() {
        inventories.forceSave();
        player.sendMessage(
                ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.SAVEDONE"));
    }

    private void list() throws SecuboidCommandException {
        Set<String> inventoryNamesSort = new TreeSet<>();

        for (InventorySpec inventorySpec : inventories.getActiveInventorySpecs()) {
            inventoryNamesSort.add(inventorySpec.getInventoryName());
        }

        StringBuilder stList = new StringBuilder();
        stList.append(ChatColor.YELLOW);
        for (String inventoryName : inventoryNamesSort) {
            stList.append(inventoryName).append(" ");
        }
        new ChatPage(secuboid, "COMMAND.INV.LISTSTART", stList.toString(), player, null).getPage(1);
    }

    private void purge() {
        String inventoryName = argList.getNext();
        InventorySpec inventorySpec;
        if (inventoryName == null || (inventorySpec = inventories.getInvSpec(inventoryName)) == null) {
            player.sendMessage(
                    ChatColor.RED + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.ERRORMISSING"));
            return;
        }

        playerConf.setCommandConfirmable(new CommandInvPurgeConfirm(inventorySpec));
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.CONFIRM"));
    }

    final class CommandInvPurgeConfirm implements CommandConfirmable {

        private final InventorySpec inventorySpec;

        private CommandInvPurgeConfirm(InventorySpec inventorySpec) {
            this.inventorySpec = inventorySpec;
        }

        @Override
        public void execConfirm() throws SecuboidCommandException {
            if (checkIfInvActive()) {
                player.sendMessage(
                        ChatColor.RED + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.INV.ERRORACTIVE"));
                return;
            }

            inventories.purgeInventory(inventorySpec);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.INV.PURGEDONE", inventorySpec.getInventoryName()));
        }

        private boolean checkIfInvActive() {
            for (Player checkPlayer : Bukkit.getOnlinePlayers()) {
                PlayerConfEntry curConfEntry = secuboid.getPlayerConf().get(checkPlayer);
                if (curConfEntry != null) {
                    if (curConfEntry.getPlayerInventoryCacheOpt().map(playerInvCache -> {
                        PlayerInvEntry curInvEntry = playerInvCache.getCurInvEntry();
                        if (curInvEntry != null) {
                            InventorySpec curInventorySpec = curInvEntry.getInventorySpec();
                            if (Objects.equal(curInventorySpec, inventorySpec)) {
                                player.sendMessage(ChatColor.RED + "[Secuboid] "
                                        + secuboid.getLanguage().getMessage("COMMAND.INV.ERRORPLAYERUSEINV",
                                                player.getName(), inventorySpec.getInventoryName()));
                                return true;
                            }
                        }
                        return false;
                    }).orElse(false)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
