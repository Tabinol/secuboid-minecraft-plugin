/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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

package app.secuboid.api.services;

import org.bukkit.plugin.Plugin;

/**
 * Use those methods to load, enable and disable your services on normal Bukkit start, shutdown and reload (not
 * secuboid reload).
 */
public interface ServiceService extends Service {

    /**
     * Load the services. Every service are loaded in registration order.
     *
     * @param plugin the Bukkit plugin
     */
    void onLoad(Plugin plugin);

    /**
     * Enable the services. Every service are enabled in registration order.
     *
     * @param plugin the Bukkit plugin
     */
    void onEnable(Plugin plugin);

    /**
     * Disable the services. Every service are disabled in REVERSE registration order.
     *
     * @param plugin the Bukkit plugin
     */
    void onDisable(Plugin plugin);
}
