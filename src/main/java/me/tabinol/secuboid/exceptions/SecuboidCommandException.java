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
package me.tabinol.secuboid.exceptions;

import me.tabinol.secuboid.Secuboid;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class SecuboidCommandException.
 */
public class SecuboidCommandException extends Exception {

    private static final long serialVersionUID = 5585486767311219615L;

    /**
     * Instantiates a new secuboid command exception.
     *
     * @param secuboid the secuboid instance
     * @param logMsg   the log msg
     * @param sender   the sender
     * @param langMsg  the lang msg
     * @param param    the param
     */
    public SecuboidCommandException(Secuboid secuboid, String logMsg, CommandSender sender, String langMsg, String... param) {

        super(logMsg);
        if (sender != null) {
            secuboid.getLog().write("Player: " + sender.getName() + ", Lang Msg: " + langMsg + ", " + logMsg);
        } else {
            secuboid.getLog().write(logMsg);
        }
        if (sender != null) {
            sender.sendMessage(ChatColor.RED + "[Secuboid] " + secuboid.getLanguage().getMessage(langMsg, param));
        }
    }
}
