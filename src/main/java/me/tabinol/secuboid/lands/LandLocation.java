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
package me.tabinol.secuboid.lands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Because bukkit Location is associated with a loaded world only, we need to
 * redefine the location class with the world string name.
 */
public final class LandLocation {

    final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float pitch;
    private final float yaw;

    public LandLocation(final String worldName, final double x, final double y, final double z, final float pitch,
            final float yaw) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    /**
     * Location to file format.
     *
     * @return the file format
     */
    public String toFileFormat() {
        return worldName + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

    public Location toLocation() {
        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * File format to location.
     *
     * @param locStr the string
     * @return the location
     */
    public static LandLocation fromFileFormat(final String locStr) {

        final String[] strs = locStr.split("\\;");

        // Wrong parameter
        if (strs.length != 6) {
            return null;
        }

        // Get the location
        final LandLocation location;
        try {
            location = new LandLocation(strs[0], Double.parseDouble(strs[1]), Double.parseDouble(strs[2]),
                    Double.parseDouble(strs[3]), Float.parseFloat(strs[4]), Float.parseFloat(strs[5]));
        } catch (final NumberFormatException e) {
            // if location is wrong, set null
            return null;
        }

        return location;
    }

    /**
     * Bukkit location to land (Secuboid) location.
     *
     * @param loc the Bukkit location
     * @return the location
     */
    public static LandLocation fromLocation(final Location loc) {
        return new LandLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(),
                loc.getYaw());
    }
}