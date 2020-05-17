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

import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * Represents a bukkit permission.
 *
 * @author tabinol
 */
public final class PlayerContainerPermission implements PlayerContainer {

    /**
     * The permission: Must be a String, not a new Permission() or Bukkit
     * (sometimes) will not be able to compare.
     */
    private final String perm;

    PlayerContainerPermission(final String bukkitPermission) {
        perm = bukkitPermission;
    }

    @Override
    public boolean hasAccess(final Player player, final LandPermissionsFlags testLandPermissionsFlags) {
        return player.hasPermission(perm);
    }

    @Override
    public String getPrint() {
        return ChatColor.GRAY + "B:" + ChatColor.WHITE + perm;
    }

    @Override
    public String getName() {
        return perm;
    }

    @Override
    public PlayerContainerType getContainerType() {
        return PlayerContainerType.PERMISSION;
    }

    @Override
    public String toFileFormat() {
        return PlayerContainerType.PERMISSION + ":" + perm;
    }

    @Override
    public boolean isLandRelative() {
        return false;
    }

    @Override
    public int compareTo(final PlayerContainer t) {
        final int result = PlayerContainerType.PERMISSION.compareTo(t.getContainerType());
        if (result != 0) {
            return result;
        }
        return perm.compareTo(t.getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PlayerContainerPermission)) {
            return false;
        }
        final PlayerContainerPermission playerContainerPermission = (PlayerContainerPermission) o;
        return Objects.equals(perm, playerContainerPermission.perm);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(perm);
    }

}
