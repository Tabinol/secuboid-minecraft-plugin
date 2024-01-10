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

package app.secuboid.permission.group.recipients;

import app.secuboid.api.SecuboidComponent;
import app.secuboid.permission.group.PermissionGroupService;
import app.secuboid.permission.group.SecuboidPermissionGroupPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.ServicesManager;

public class SecuboidPermissionGroup implements SecuboidComponent {

    final private Permission permission;

    final private PermissionGroupService permissionGroupService;

    public SecuboidPermissionGroup(SecuboidPermissionGroupPlugin secuboidPermissionGroupPlugin) {
        ServicesManager servicesManager = secuboidPermissionGroupPlugin.getServer().getServicesManager();
        permissionGroupService = new PermissionGroupService(servicesManager);
        permission = permissionGroupService.getPermission();
    }


    public Permission getPermission() {
        return permission;
    }

    @Override
    public void onEnable(boolean isServerBoot) {
        //permissionGroupService.load(true);
    }
}
