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

package app.secuboid.api;

/**
 * Only for secuboid component plugin (Secuboid, SecuboidPermissionGroup, ...). Implement this interface is not
 * enough. You need also the call those methods from the plugin class.
 */
public interface SecuboidComponent {

    /**
     * When the plugin is loaded.
     */
    default void onLoad() {
        // Override if needed
    }

    /**
     * When the plugin is enabled.
     *
     * @param isServerBoot is the server is booting (true) or reload plugin (false)
     */
    default void onEnable(boolean isServerBoot) {
        // Override if needed
    }

    /**
     * When the plugin is disabled
     */
    default void onDisable() {
        // Override if needed
    }

    /**
     * On Secuboid reload
     */
    default void reload() {
        // Override if needed
    }
}
