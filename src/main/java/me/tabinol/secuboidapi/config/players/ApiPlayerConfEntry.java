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

import me.tabinol.secuboidapi.lands.ApiDummyLand;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerPlayer;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Interface ApiPlayerConfEntry. Contains diver Secuboid informations from a
 * player.
 */
public interface ApiPlayerConfEntry {

    /**
     * Gets the player container from this player.
     *
     * @return the player container
     */
    public ApiPlayerContainerPlayer getPlayerContainer();

    /**
     * Gets the command sender from this player.
     *
     * @return the sender
     */
    public CommandSender getSender();

    /**
     * Gets the player.
     *
     * @return the player
     */
    public Player getPlayer();

    /**
     * Checks if the player is in admin mod.
     *
     * @return true, if is in admin mod
     */
    public boolean isAdminMod();

    /**
     * Gets the last move update from the border control.
     *
     * @return the last move update in millisecond
     */
    public long getLastMoveUpdate();

    /**
     * Gets the last land take from the border control.
     *
     * @return the last land
     */
    public ApiDummyLand getLastLand();

    /**
     * Gets the last location take from the border control.
     *
     * @return the last location
     */
    public Location getLastLoc();

}