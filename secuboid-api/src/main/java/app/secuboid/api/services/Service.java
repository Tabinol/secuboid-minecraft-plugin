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

/**
 * You can implement this class for you services, but it is not required. It's used in Secuboid for automatic
 * load/reload. The default methods are not started automatically (except on Secuboid reload). See
 * {@link ServiceService}.
 */
public interface Service {

    /**
     * When the plugin is loaded. This method is not called on Secuboid reload. No yaml configuration related call
     * from this method.
     */
    default void onLoad() {
    }

    /**
     * When the plugin is enabled.
     *
     * @param isServerBoot is a server boot? (Not a plugin reload but a Bukkit reload give true)
     */
    default void onEnable(boolean isServerBoot) {
    }

    /**
     * When the plugin is disabled.
     *
     * @param isServerShutdown is a server shut down? (Not a plugin reload but a Bukkit shut down give true)
     */
    default void onDisable(boolean isServerShutdown) {
    }
}
