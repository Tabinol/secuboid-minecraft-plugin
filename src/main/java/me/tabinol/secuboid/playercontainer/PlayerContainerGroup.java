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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.RealLand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Represents a bukkit group.
 *
 * @author tabinol
 */
public class PlayerContainerGroup implements PlayerContainer {

    String groupName;

    public PlayerContainerGroup(String groupName) {
	this.groupName = groupName;
    }

    @Override
    public boolean hasAccess(Player player) {
	if (player != null) {
	    return Secuboid.getThisPlugin().getDependPlugin().getPermission().playerInGroup(player, groupName);
	} else {
	    return false;
	}
    }

    @Override
    public boolean hasAccess(Player player, RealLand land) {
	return hasAccess(player);
    }

    @Override
    public RealLand getLand() {
	return null;
    }

    @Override
    public void setLand(RealLand land) {
    }

    @Override
    public String getPrint() {

	return ChatColor.BLUE + "G:" + ChatColor.WHITE + groupName;
    }

    @Override
    public String getName() {
	return groupName;
    }

    @Override
    public PlayerContainerType getContainerType() {
	return PlayerContainerType.GROUP;
    }

    @Override
    public String toFileFormat() {
	return PlayerContainerType.GROUP + ":" + groupName;
    }

    @Override
    public int compareTo(PlayerContainer t) {
	int result = PlayerContainerType.EVERYBODY.compareTo(t.getContainerType());
	if (result == 0) {
	    return result;
	}
	return groupName.compareTo(t.getName());
    }
}
