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

package app.secuboid.permission.group;

import app.secuboid.permission.group.recipients.SecuboidPermissionGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class SecuboidPermissionGroupPlugin extends JavaPlugin {

    private final SecuboidPermissionGroup secuboidPermissionGroup;

    @SuppressWarnings("java:S3010")
    public SecuboidPermissionGroupPlugin() {
        secuboidPermissionGroup = new SecuboidPermissionGroup(this);
    }

    @Override
    public void onLoad() {
        secuboidPermissionGroup.onLoad();
    }

    @Override
    public void onEnable() {
        secuboidPermissionGroup.onEnable(true);
    }

    @Override
    public void onDisable() {
        secuboidPermissionGroup.onDisable();
    }
}
