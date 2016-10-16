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

package me.tabinol.secuboid.flycreative;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.lands.DummyLand;
import me.tabinol.secuboid.parameters.PermissionType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Fly {

    public final static String FLY_IGNORE_PERM = "secuboid.flycreative.ignorefly";
    private final PermissionType permissionType;

    public Fly() {

        // Register flags
        permissionType = Secuboid.getThisPlugin().getParameters().registerPermissionType("FLY", false);
    }

    public void fly(Event event, Player player, DummyLand dummyLand) {

        if (!player.hasPermission(FLY_IGNORE_PERM)) {
            if (askFlyFlag(player, dummyLand)) {
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);

                    // Bug fix : Prevent player fall
                    Location loc = player.getLocation().clone();
                    Block block = loc.subtract(0, 1, 0).getBlock();
                    if (block.isLiquid() || block.getType() == Material.AIR) {
                        player.setFlying(true);
                    }
                }
            } else {
                if (player.isFlying() && event instanceof PlayerLandChangeEvent
                        && !((PlayerLandChangeEvent) event).isTp()) {
                    // Return the player in the last cuboid if he is flying.
                    ((PlayerLandChangeEvent) event).setCancelled(true);
                } else {
                    player.setAllowFlight(false);
                }
            }
        }
    }

    public void removeFly(Player player) {

        if (!player.hasPermission(FLY_IGNORE_PERM)) {
            player.setAllowFlight(false);
        }
    }

    private boolean askFlyFlag(Player player, DummyLand dummyLand) {

    	return dummyLand.checkPermissionAndInherit(player, permissionType);
    }
}
