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
package me.tabinol.secuboid.playercontainer;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.FlagList;

/**
 * Represents the land owner.
 *
 * @author tabinol
 */
public final class PlayerContainerOwner implements PlayerContainer {

    @Override
    public boolean hasAccess(final Player player, final Land pcLandNullable,
            final LandPermissionsFlags testLandPermissionsFlags) {
        final Land testLandNullable = testLandPermissionsFlags.getLandNullable();
        if (pcLandNullable == null || testLandNullable == null) {
            return false;
        }

        boolean value = pcLandNullable.getOwner().hasAccess(player, pcLandNullable, testLandPermissionsFlags);
        Land actual = pcLandNullable;
        Land parentNullable;

        while (!value && (parentNullable = actual.getParent()) != null && actual.getPermissionsFlags()
                .getFlagAndInherit(FlagList.INHERIT_OWNER.getFlagType()).getValueBoolean()) {
            value = parentNullable.isOwner(player);
            actual = parentNullable;
        }

        return value;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PlayerContainerType getContainerType() {
        return PlayerContainerType.OWNER;
    }

    @Override
    public String getPrint() {
        return PlayerContainerType.OWNER.getPrint();
    }

    @Override
    public String toFileFormat() {
        return PlayerContainerType.OWNER.getPrint() + ":";
    }

    @Override
    public int compareTo(final PlayerContainer t) {
        return PlayerContainerType.OWNER.compareTo(t.getContainerType());
    }
}
