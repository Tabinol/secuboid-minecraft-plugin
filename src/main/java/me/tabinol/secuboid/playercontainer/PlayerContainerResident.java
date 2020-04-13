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

/**
 * Represents a land resident.
 *
 * @author tabinol
 */
public final class PlayerContainerResident implements PlayerContainer {

    private static final PlayerContainerResident instance = new PlayerContainerResident();

    private PlayerContainerResident() {
    }

    public static PlayerContainerResident getInstance() {
        return instance;
    }

    @Override
    public boolean hasAccess(final Player player, final LandPermissionsFlags testLandPermissionsFlags) {
        final Land testLandNullable = testLandPermissionsFlags.getLandNullable();
        if (testLandNullable == null) {
            return false;
        }

        for (final PlayerContainer playerContainer : testLandNullable.getResidents()) {
            // First check is anti inifite loop
            if (!playerContainer.isLandRelative() && playerContainer.hasAccess(player, testLandPermissionsFlags)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PlayerContainerType getContainerType() {
        return PlayerContainerType.RESIDENT;
    }

    @Override
    public String getPrint() {
        return PlayerContainerType.RESIDENT.getPrint();
    }

    @Override
    public String toFileFormat() {
        return PlayerContainerType.RESIDENT.getPrint() + ":";
    }

    @Override
    public boolean isLandRelative() {
        return true;
    }

    @Override
    public int compareTo(final PlayerContainer t) {
        return PlayerContainerType.RESIDENT.compareTo(t.getContainerType());
    }
}
