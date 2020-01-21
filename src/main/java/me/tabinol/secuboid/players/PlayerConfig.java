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
package me.tabinol.secuboid.players;

import java.util.HashMap;
import java.util.Map;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.dependencies.chat.Chat;
import me.tabinol.secuboid.dependencies.vanish.Vanish;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Class PlayerConfig. Contains lists for player (selection, ect, ...).
 */
public class PlayerConfig {

    /**
     * The player conf list.
     */
    private final Map<CommandSender, PlayerConfEntry> playerConfList;

    /**
     * The vanish.
     */
    private final Vanish vanish;

    /**
     * The chat.
     */
    private final Chat chat;

    private final Secuboid secuboid;

    /**
     * Instantiates a new player static config.
     *
     * @param secuboid secuboid instance
     */
    public PlayerConfig(Secuboid secuboid) {
        this.secuboid = secuboid;
        playerConfList = new HashMap<CommandSender, PlayerConfEntry>();
        vanish = secuboid.getDependPlugin().getVanish();
        chat = secuboid.getDependPlugin().getChat();
    }

    /**
     * Adds the static configuration.
     *
     * @param sender the sender
     * @return the player conf entry
     */
    public PlayerConfEntry add(CommandSender sender) {
        PlayerConfEntry entry = new PlayerConfEntry(secuboid, sender);
        playerConfList.put(sender, entry);

        return entry;
    }

    /**
     * Removes the static configuration.
     *
     * @param sender the sender
     */
    public void remove(CommandSender sender) {
        PlayerConfEntry entry = playerConfList.get(sender);

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
    public PlayerConfEntry get(CommandSender sender) {
        return playerConfList.get(sender);
    }

    /**
     * Adds all static configurations.
     */
    public void addAll() {
        // Add the consle in the list
        add(secuboid.getServer().getConsoleSender());

        // Add online players
        for (CommandSender sender : secuboid.getServer().getOnlinePlayers()) {
            add(sender);
        }
    }

    /**
     * Removes all static configurations.
     */
    public void removeAll() {
        for (PlayerConfEntry entry : playerConfList.values()) {
            // First, remove AutoCancelSelect
            entry.setAutoCancelSelect(false);
        }
        playerConfList.clear();
    }

    /**
     * Checks if the player is vanished.
     *
     * @param player the player
     * @return true if vanished
     */
    public boolean isVanished(Player player) {
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
}
