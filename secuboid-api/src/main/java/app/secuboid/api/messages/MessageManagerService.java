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
package app.secuboid.api.messages;

import app.secuboid.api.flagtypes.FlagType;
import app.secuboid.api.services.Service;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

/**
 * Message manager for language files inside the plugin jar. At least,
 * lang/en.yml should be present.
 */
public interface MessageManagerService extends Service {

    /**
     * Gets the message from a yaml path.
     *
     * @param messageType the message type
     * @param path        the yaml path with arguments
     * @return the message
     */
    String get(MessageType messageType, MessagePath path);

    /**
     * Sends a message to this sender.
     *
     * @param sender      the command sender (player or console)
     * @param messageType the message type
     * @param path        the yaml path with arguments
     */
    void sendMessage(CommandSender sender, MessageType messageType, MessagePath path);

    /**
     * Broadcasts a message to all connected players.
     *
     * @param messageType the message type
     * @param path        the yaml path with arguments
     */
    void broadcastMessage(MessageType messageType, MessagePath path);

    /**
     * Gets a text component (Bukkit) from a message path. You can use this when you need a clickable action
     *
     * @param messageType the message type
     * @param path        the yaml path with arguments
     * @return the text component
     */
    TextComponent getTextComponent(MessageType messageType, MessagePath path);

    /**
     * Gets the flag description.
     *
     * @param flagType the flag type
     * @return the flag description
     */
    String getFlagDescription(FlagType flagType);

    /**
     * Sends the flag description to this sender.
     *
     * @param sender   the command sender (player or console)
     * @param flagType the flag type
     */
    void sendFlagDescription(CommandSender sender, FlagType flagType);
}
