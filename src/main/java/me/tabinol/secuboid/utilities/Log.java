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
package me.tabinol.secuboid.utilities;

import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * Log information for Secuboid.
 */
public class Log {

    private final boolean isDebug;

    /**
     * Creates log instance.
     *
     * @param isDebug is debugging is enable
     */
    public Log(boolean isDebug) {
        this.isDebug = isDebug;
    }

    /**
     * Logs info message.
     *
     * @param msg the message
     */
    public void info(String msg) {
        Bukkit.getLogger().log(Level.INFO, msg);
    }

    /**
     * Logs warning message.
     *
     * @param msg the message
     */
    public void warning(String msg) {
        Bukkit.getLogger().log(Level.WARNING, msg);
    }

    /**
     * Logs severe message.
     *
     * @param msg the message
     */
    public void severe(String msg) {
        Bukkit.getLogger().log(Level.SEVERE, msg);
    }

    /**
     * Logs debugging message.
     *
     * @param msg the message
     */
    public void debug(String msg) {
        if (isDebug) {
            Bukkit.getLogger().log(Level.INFO, "[DEBUG] " + msg);
        }
    }
}
