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

import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * Represents every players.
 *
 * @author tabinol
 */
public final class PlayerContainerEverybody implements PlayerContainer {

    private static final PlayerContainerEverybody instance = new PlayerContainerEverybody();

    private PlayerContainerEverybody() {
    }

    public static PlayerContainerEverybody getInstance() {
        return instance;
    }

    @Override
    public boolean hasAccess(final Player player, final LandPermissionsFlags testLandPermissionsFlags) {
        return true;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PlayerContainerType getContainerType() {
        return PlayerContainerType.EVERYBODY;
    }

    @Override
    public int compareTo(final PlayerContainer t) {
        return PlayerContainerType.EVERYBODY.compareTo(t.getContainerType());
    }

    @Override
    public String getPrint() {
        return PlayerContainerType.EVERYBODY.getPrint();
    }

    @Override
    public String toFileFormat() {
        return PlayerContainerType.EVERYBODY.getPrint() + ":";
    }

    @Override
    public boolean isLandRelative() {
        return false;
    }
}
