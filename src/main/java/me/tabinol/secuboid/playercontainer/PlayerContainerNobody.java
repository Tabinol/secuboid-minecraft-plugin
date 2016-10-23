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
import org.bukkit.entity.Player;

/**
 * Represents nobody.
 *
 * @author tabinol
 */
public class PlayerContainerNobody implements PlayerContainer {

    @Override
    public boolean hasAccess(Player player) {
	return true;
    }

    @Override
    public boolean hasAccess(Player player, Land land) {
	return true;
    }

    @Override
    public void setLand(Land land) {
    }

    @Override
    public String getName() {
	return "";
    }

    @Override
    public PlayerContainerType getContainerType() {
	return PlayerContainerType.NOBODY;
    }

    @Override
    public int compareTo(PlayerContainer t) {
	return PlayerContainerType.NOBODY.compareTo(t.getContainerType());
    }

    @Override
    public String getPrint() {
	return PlayerContainerType.NOBODY.getPrint();
    }

    @Override
    public String toFileFormat() {
	return PlayerContainerType.NOBODY.getPrint() + ":";
    }

    @Override
    public Land getLand() {
	return null;
    }
}
