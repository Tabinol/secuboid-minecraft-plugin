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
package me.tabinol.secuboid.lands;

import me.tabinol.secuboid.Secuboid;
import org.bukkit.entity.Player;

/**
 * Represents global, entire world.
 *
 * @author tabinol
 */
public class WorldLand implements Land {

    private final LandPermissionsFlags landPermissionsFlags;
    private final String worldName;

    public WorldLand(Secuboid secuboid, String worldName) {
        this.worldName = worldName;
        landPermissionsFlags = new LandPermissionsFlags(secuboid, this);
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public LandType getLandType() {
        return LandType.WORLD;
    }

    @Override
    public LandPermissionsFlags getPermissionsFlags() {
        return landPermissionsFlags;
    }

    @Override
    public boolean isBanned(Player player) {
        return false;
    }
}
