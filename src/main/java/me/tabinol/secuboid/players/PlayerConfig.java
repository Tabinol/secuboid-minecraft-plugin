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
package me.tabinol.secuboid.players;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import me.tabinol.secuboid.commands.executor.CommandCancel;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.selection.PlayerSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.dependencies.chat.Chat;
import me.tabinol.secuboid.dependencies.vanish.Vanish;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;

/**
 * The Class PlayerConfig. Contains lists for player (selection, ect, ...).
 */
public final class PlayerConfig {

    private final Secuboid secuboid;

    /**
     * The player conf list.
     */
    private final Map<CommandSender, PlayerConfEntry> playerConfList;

    /**
     * The vanish.
     */
    private Vanish vanish;

    /**
     * The chat.
     */
    private Chat chat;

    /**
     * Instantiates a new player static config.
     *
     * @param secuboid secuboid instance
     */
    public PlayerConfig(final Secuboid secuboid) {
        this.secuboid = secuboid;
        playerConfList = new HashMap<>();
    }

    /**
     * Load config.
     *
     * @param isServerBoot is first boot?
     */
    public void loadConfig(final boolean isServerBoot) {
        if (isServerBoot) {
            vanish = secuboid.getDependPlugin().getVanish();
            chat = secuboid.getDependPlugin().getChat();
        }
        addConsoleSender();
    }

    /**
     * Adds the static configuration.
     *
     * @param sender                       the sender
     * @param playerInventoryCacheNullable the player inventory cache optional
     * @return the player conf entry
     */
    public PlayerConfEntry add(final CommandSender sender,
                               final PlayerInventoryCache playerInventoryCacheNullable) {
        final PlayerConfEntry entry = new PlayerConfEntry(secuboid, sender, playerInventoryCacheNullable);
        playerConfList.put(sender, entry);

        return entry;
    }

    /**
     * Removes the static configuration.
     *
     * @param sender the sender
     */
    public void remove(final CommandSender sender) {
        final PlayerConfEntry entry = playerConfList.get(sender);

        // First, remove AutoCancelSelect
        entry.setAutoCancelSelect(false);

        playerConfList.remove(sender);
    }

    /**
     * Gets the static configuration for a command sender.
     *
     * @param sender the command sender
     * @return the player static configuration
     */
    public PlayerConfEntry get(final CommandSender sender) {
        return playerConfList.get(sender);
    }

    /**
     * Removes all static configurations.
     */
    public void removeAll() {
        for (final PlayerConfEntry entry : playerConfList.values()) {
            final PlayerSelection playerSelection = entry.getSelection();
            if (playerSelection != null && playerSelection.hasSelection()) {
                try {
                    // Cancel selection
                    new CommandCancel(secuboid, null, entry.getPlayer(), null).commandExecute();
                } catch (SecuboidCommandException e) {
                    secuboid.getLogger().log(Level.WARNING, String.format("Unable to cancel the selection for the player [name=%s, uuid=%s]", entry.getPlayer().getUniqueId(), entry.getPlayer().getName()));
                }
            }
        }
        playerConfList.clear();
    }

    /**
     * Checks if the player is vanished.
     *
     * @param player the player
     * @return true if vanished
     */
    public boolean isVanished(final Player player) {
        return vanish.isVanished(player);
    }

    /**
     * Gets the chat instance.
     *
     * @return the chat instance
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Adds console sender.
     */
    private void addConsoleSender() {
        add(secuboid.getServer().getConsoleSender(), null);
    }
}
