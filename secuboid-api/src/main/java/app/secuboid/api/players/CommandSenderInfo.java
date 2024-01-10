/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.api.players;

import app.secuboid.api.selection.SenderSelection;
import org.bukkit.command.CommandSender;

/**
 * Contains information for a command sender.
 */
public interface CommandSenderInfo {

    /**
     * Gets the command sender for this player.
     *
     * @return the command sender
     */
    CommandSender sender();

    /**
     * Gets the player name.
     *
     * @return the player name
     */
    String getName();

    /**
     * Is the player admin mode?
     *
     * @return true if the player is admin mode
     */
    boolean isAdminMode();

    /**
     * Gets the sender selection.
     *
     * @return the sender selection
     */
    public SenderSelection getSelection();
}
