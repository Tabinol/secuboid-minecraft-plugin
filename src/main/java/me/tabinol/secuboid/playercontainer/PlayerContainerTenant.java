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
package me.tabinol.secuboid.playercontainer;

import org.bukkit.entity.Player;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.PermissionList;

/**
 * Represents a land tenant.
 *
 * @author tabinol
 */
public final class PlayerContainerTenant implements PlayerContainer {

    private static final PlayerContainerTenant instance = new PlayerContainerTenant();

    private PlayerContainerTenant() {
    }

    public static PlayerContainerTenant getInstance() {
        return instance;
    }

    @Override
    public boolean hasAccess(final Player player, final LandPermissionsFlags testLandPermissionsFlags) {
        final Land testLandNullable = testLandPermissionsFlags.getLandNullable();
        if (testLandNullable == null || !testLandNullable.isRented()) {
            return false;
        }

        // Check for permission
        if (testLandPermissionsFlags.checkPermissionAndInherit(player,
                PermissionList.LAND_TENANT.getPermissionType())) {
            return true;
        }

        // Anti inifite loop
        final PlayerContainer playerContainer = testLandNullable.getTenant();
        if (playerContainer == null || playerContainer.isLandRelative()) {
            return false;
        }

        return playerContainer.hasAccess(player, testLandPermissionsFlags);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PlayerContainerType getContainerType() {
        return PlayerContainerType.TENANT;
    }

    @Override
    public String getPrint() {
        return PlayerContainerType.TENANT.getPrint();
    }

    @Override
    public String toFileFormat() {
        return PlayerContainerType.TENANT.getPrint() + ":";
    }

    @Override
    public boolean isLandRelative() {
        return true;
    }

    @Override
    public int compareTo(final PlayerContainer t) {
        return PlayerContainerType.TENANT.compareTo(t.getContainerType());
    }
}
