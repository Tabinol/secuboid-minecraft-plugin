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
package me.tabinol.secuboidapi.config.players;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Interface ApiPlayerStaticConfig. It is where you can get an information
 * from an online player.
 */
public interface ApiPlayerStaticConfig {

    /**
     * Gets the configuration from an online player.
     *
     * @param sender the online player
     * @return the player configuration
     */
    public ApiPlayerConfEntry get(CommandSender sender);

    /**
     * Checks if the player is vanished.
     *
     * @param player the player
     * @return true, if is vanished
     */
    public boolean isVanished(Player player);

}