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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.listeners.FlyCreativeListener;
import me.tabinol.secuboid.permissionsflags.PermissionType;

/**
 * The creative class.
 */
public final class Creative {

    public final static String CREATIVE_IGNORE_PERM = "secuboid.flycreative.ignorecreative";
    public final static String OVERRIDE_NODROP_PERM = "secuboid.flycreative.override.nodrop";
    public final static String OVERRIDE_NOOPENCHEST_PERM = "secuboid.flycreative.override.noopenchest";
    public final static String OVERRIDE_NOBUILDOUTSIDE_PERM = "secuboid.flycreative.override.nobuildoutside";
    public final static String OVERRIDE_BANNEDITEMS_PERM = "secuboid.flycreative.override.allowbanneditems";

    private final Secuboid secuboid;
    private final Config conf;
    private final PermissionType permissionType;
    private final FlyCreativeListener flyCreativeListener;

    /**
     * Instantiates a new creative.
     *
     * @param secuboid            secuboid instance.
     * @param flyCreativeListener fly creative listener instance
     */
    public Creative(Secuboid secuboid, FlyCreativeListener flyCreativeListener) {

        this.secuboid = secuboid;
        conf = secuboid.getConf();

        // Register flags
        permissionType = secuboid.getPermissionsFlags().registerPermissionType("CREATIVE", false);

        this.flyCreativeListener = flyCreativeListener;
    }

    public boolean isCreative(Event event, Player player, LandPermissionsFlags landPermissionsFlags) {

        if (!player.hasPermission(CREATIVE_IGNORE_PERM)) {
            if (askCreativeFlag(player, landPermissionsFlags)) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    flyCreativeListener.addIgnoredGMPlayers(player);
                    player.setGameMode(GameMode.CREATIVE);
                }
            } else if (player.getGameMode() == GameMode.CREATIVE) {
                if (player.isFlying() && event instanceof PlayerLandChangeEvent
                        && !((PlayerLandChangeEvent) event).isTp()) {
                    // Return the player in the last cuboid if he is flying.
                    ((PlayerLandChangeEvent) event).setCancelled(true);
                } else {
                    flyCreativeListener.addIgnoredGMPlayers(player);
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }

        return player.getGameMode() == GameMode.CREATIVE;
    }

    public void setGM(Player player, GameMode gm) {

        if (!player.hasPermission(CREATIVE_IGNORE_PERM)) {
            flyCreativeListener.addIgnoredGMPlayers(player);
            player.setGameMode(gm);
        }
    }

    public boolean dropItem(Player player) {
        return conf.isCreativeNoDrop() && !player.hasPermission(OVERRIDE_NODROP_PERM);
    }

    public void invOpen(InventoryOpenEvent event, HumanEntity player) {

        if (conf.isCreativeNoOpenChest() && !player.hasPermission(OVERRIDE_NOOPENCHEST_PERM)) {

            final InventoryType it = event.getView().getType();

            if (it == InventoryType.CHEST || it == InventoryType.DISPENSER || it == InventoryType.DROPPER
                    || it == InventoryType.ENDER_CHEST || it == InventoryType.FURNACE || it == InventoryType.HOPPER) {

                event.setCancelled(true);
            }
        }
    }

    /**
     * The player is building.
     *
     * @param event  the event
     * @param player the player
     * @return «true» if the events must be cancelled
     */
    public boolean build(Event event, Player player) {

        Location blockLoc;

        if (conf.isCreativeNoBuildOutside() && !player.hasPermission(OVERRIDE_NOBUILDOUTSIDE_PERM)) {

            if (event instanceof BlockBreakEvent) {
                blockLoc = ((BlockBreakEvent) event).getBlock().getLocation();
            } else {
                blockLoc = ((BlockPlaceEvent) event).getBlockPlaced().getLocation();
            }
            if (!askCreativeFlag(player, secuboid.getLands().getPermissionsFlags(blockLoc))) {

                return true;
            }
        }
        return false;
    }

    public void checkBannedItems(InventoryCloseEvent event, HumanEntity player) {

        if (!player.hasPermission(OVERRIDE_BANNEDITEMS_PERM)) {

            for (Material mat : conf.getCreativeBannedItems()) {
                event.getPlayer().getInventory().remove(mat);
            }
        }
    }

    private boolean askCreativeFlag(Player player, LandPermissionsFlags landPermissionsFlags) {
        return landPermissionsFlags.checkPermissionAndInherit(player, permissionType);
    }
}
