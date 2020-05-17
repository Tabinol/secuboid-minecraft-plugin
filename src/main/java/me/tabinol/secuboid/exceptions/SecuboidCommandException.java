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
package me.tabinol.secuboid.exceptions;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;

/**
 * The Class SecuboidCommandException.
 */
public class SecuboidCommandException extends Exception {
    private static final long serialVersionUID = 5585486767311219615L;

    private final Secuboid secuboid;
    private final CommandSender sender;
    private final String langMsg;
    private final String[] params;

    /**
     * Instantiates a new secuboid command exception.
     *
     * @param secuboid the secuboid instance
     * @param logMsg   the log msg
     * @param sender   the sender
     * @param langMsg  the lang msg
     * @param params   the params
     */
    public SecuboidCommandException(final Secuboid secuboid, final String logMsg, final CommandSender sender,
            final String langMsg, final String... params) {
        super(logMsg);
        this.secuboid = secuboid;
        this.sender = sender;
        this.langMsg = langMsg;
        this.params = params;
    }

    /**
     * Instantiates a new secuboid command exception with Throwable.
     * 
     * @param secuboid the secuboid instance
     * @param sender   the sender
     * @param message  the message
     * @param cause    the cause
     */
    public SecuboidCommandException(final Secuboid secuboid, final CommandSender sender, final String message,
            final Throwable cause) {
        super(message, cause);
        this.secuboid = secuboid;
        this.sender = sender;
        this.langMsg = "GENERAL.ERROR";
        this.params = new String[0];
    }

    /**
     * Notifies the sender or just return if the sender is null.
     */
    public void notifySender() {
        if (sender != null) {
            sender.sendMessage(ChatColor.RED + "[Secuboid] " + secuboid.getLanguage().getMessage(langMsg, params));
        }
    }
}
