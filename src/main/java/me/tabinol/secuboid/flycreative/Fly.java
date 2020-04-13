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
package me.tabinol.secuboid.flycreative;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.PermissionType;

/**
 * The fly class.
 */
public final class Fly {

    public final static String FLY_IGNORE_PERM = "secuboid.flycreative.ignorefly";

    private final PermissionType permissionType;

    /**
     * Instantiates a new fly instance and register the fly flag.
     *
     * @param secuboid secuboid instance.
     */
    public Fly(Secuboid secuboid) {
        permissionType = secuboid.getPermissionsFlags().registerPermissionType("FLY", false);
    }

    public void isFly(Event event, Player player, LandPermissionsFlags landPermissionsFlags) {

        if (!player.hasPermission(FLY_IGNORE_PERM)) {
            if (askFlyFlag(player, landPermissionsFlags)) {
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);

                    // Bug fix : Prevent player fall
                    final Location loc = player.getLocation().clone();
                    final Block block = loc.subtract(0, 1, 0).getBlock();
                    if (block.isLiquid() || block.getType() == Material.AIR) {
                        player.setFlying(true);
                    }
                }
            } else if (player.isFlying() && event instanceof PlayerLandChangeEvent
                    && !((PlayerLandChangeEvent) event).isTp()) {
                // Return the player in the last cuboid if he is flying.
                ((PlayerLandChangeEvent) event).setCancelled(true);
            } else {
                player.setAllowFlight(false);
            }
        }
    }

    private boolean askFlyFlag(Player player, LandPermissionsFlags landPermissionsFlags) {
        return landPermissionsFlags.checkPermissionAndInherit(player, permissionType);
    }
}
