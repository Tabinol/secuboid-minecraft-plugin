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
import me.tabinol.secuboid.permissionsflags.FlagList;
import org.bukkit.entity.Player;

/**
 * Represents a land resident.
 *
 * @author tabinol
 */
public class PlayerContainerResident implements PlayerContainer {

    private Land land;

    public PlayerContainerResident(Land land) {
	this.land = land;
    }

    @Override
    public boolean hasAccess(Player player) {
	return hasAccess(player, land);
    }

    @Override
    public boolean hasAccess(Player player, Land land) {

	if (land == null) {
	    return false;
	}

	boolean value = land.isResident(player);
	Land actual = land;
	Land parent;

	while (!value && (parent = actual.getParent()) != null
		&& actual.getFlagAndInherit(FlagList.INHERIT_RESIDENTS.getFlagType()).getValueBoolean() == true) {

	    value = parent.isResident(player);
	    actual = parent;
	}

	return value;
    }

    @Override
    public Land getLand() {
	return land;
    }

    @Override
    public void setLand(Land land) {
	this.land = land;
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
    public int compareTo(PlayerContainer t) {
	int result = PlayerContainerType.RESIDENT.compareTo(t.getContainerType());
	if (result == 0) {
	    return result;
	}
	return land.compareTo(t.getLand());
    }
}
