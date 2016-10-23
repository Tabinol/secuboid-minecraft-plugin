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
package me.tabinol.secuboid.config.players;

import java.util.HashMap;
import java.util.Map;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.chat.Chat;
import me.tabinol.secuboid.config.chat.ChatEssentials;
import me.tabinol.secuboid.config.chat.ChatSecuboid;
import me.tabinol.secuboid.config.vanish.DummyVanish;
import me.tabinol.secuboid.config.vanish.Vanish;
import me.tabinol.secuboid.config.vanish.VanishEssentials;
import me.tabinol.secuboid.config.vanish.VanishNoPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


// Contain lists for player (selection, ect, ...)
/**
 * The Class PlayerStaticConfig.
 */
public class PlayerStaticConfig {
    
    /** The player conf list. */
    private final Map<CommandSender, PlayerConfEntry> playerConfList;
    
    /** The vanish. */
    private final Vanish vanish;
    
    /** The chat. */
    private final Chat chat;

    /**
     * Instantiates a new player static config.
     */
    public PlayerStaticConfig() {

        playerConfList = new HashMap<CommandSender, PlayerConfEntry>();

        // Check for VanishNoPacket plugin
        if (Secuboid.getThisPlugin().getDependPlugin().getVanishNoPacket() != null) {
            vanish = new VanishNoPacket();

            // Check for Essentials plugin
        } else if (Secuboid.getThisPlugin().getDependPlugin().getEssentials() != null) {
            vanish = new VanishEssentials();

            // Dummy Vanish if no plugins
        } else {
            vanish = new DummyVanish();
        }
        
        // Check for Chat plugin
        if (Secuboid.getThisPlugin().getDependPlugin().getEssentials() != null) {
            chat = new ChatEssentials();
        } else {
            chat = new ChatSecuboid();
        }
    }

    // Methods for geting a player static config
    /**
     * Adds the.
     *
     * @param sender the sender
     * @return the player conf entry
     */
    public PlayerConfEntry add(CommandSender sender) {

        PlayerConfEntry entry = new PlayerConfEntry(sender);
        playerConfList.put(sender, entry);

        return entry;
    }

    /**
     * Removes the.
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
     *
     * @param sender
     * @return
     */
    public PlayerConfEntry get(CommandSender sender) {

        return playerConfList.get(sender);
    }

    /**
     * Adds the all.
     */
    public void addAll() {

        // Add the consle in the list
        add(Secuboid.getThisPlugin().getServer().getConsoleSender());

        // Add online players
        for (CommandSender sender : Secuboid.getThisPlugin().getServer().getOnlinePlayers()) {
            add(sender);
        }
    }

    /**
     * Removes the all.
     */
    public void removeAll() {

        for (PlayerConfEntry entry : playerConfList.values()) {

            // First, remove AutoCancelSelect
            entry.setAutoCancelSelect(false);

        }
        playerConfList.clear();
    }
    
    /**
     *
     * @param player
     * @return
     */
    public boolean isVanished(Player player) {
        
        return vanish.isVanished(player);
    }
    
    /**
     *
     * @return
     */
    public Chat getChat() {
        
        return chat;
    }
}
