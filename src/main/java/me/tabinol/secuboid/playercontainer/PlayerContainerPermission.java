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

import me.tabinol.secuboid.lands.Land;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Represents a bukkit permission.
 *
 * @author tabinol
 */
public class PlayerContainerPermission implements PlayerContainer {

    /**
     * The permission: Must be a String, not a new Permission() or Bukkit (sometimes) will not be able to compare.
     */
    private final String perm;

    public PlayerContainerPermission(String bukkitPermission) {
        perm = bukkitPermission;
    }

    @Override
    public boolean hasAccess(Player player, Land PCLand, Land testLand) {
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
    public int compareTo(PlayerContainer t) {
        int result = PlayerContainerType.PERMISSION.compareTo(t.getContainerType());
        if (result != 0) {
            return result;
        }
        return perm.compareTo(t.getName());
    }
}
