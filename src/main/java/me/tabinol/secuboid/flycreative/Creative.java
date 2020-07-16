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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.PermissionType;
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

import java.util.HashSet;
import java.util.Set;

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

    private final Set<HumanEntity> justChangedByThisPluginGMPlayers;
    private final Set<HumanEntity> manualChangeCreativeGMPlayers;

    /**
     * Instantiates a new creative.
     *
     * @param secuboid secuboid instance.
     */
    public Creative(final Secuboid secuboid) {

        this.secuboid = secuboid;
        conf = secuboid.getConf();
        justChangedByThisPluginGMPlayers = new HashSet<>();
        manualChangeCreativeGMPlayers = new HashSet<>();

        // Register flags
        permissionType = secuboid.getPermissionsFlags().registerPermissionType("CREATIVE", false);
    }

    public boolean isCreative(final Event event, final Player player, final LandPermissionsFlags landPermissionsFlags) {

        if (askCreativeFlag(player, landPermissionsFlags)) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                setGM(player, GameMode.CREATIVE);
            }
        } else if (player.getGameMode() == GameMode.CREATIVE) {
            if (player.isFlying() && event instanceof PlayerLandChangeEvent
                    && !((PlayerLandChangeEvent) event).isTp()) {
                // Return the player in the last cuboid if he is flying.
                ((PlayerLandChangeEvent) event).setCancelled(true);
            } else {
                setGM(player, GameMode.SURVIVAL);
            }
        }

        return player.getGameMode() == GameMode.CREATIVE;
    }

    public void setGM(final Player player, final GameMode gm) {

        if (!player.hasPermission(CREATIVE_IGNORE_PERM)) {
            justChangedByThisPluginGMPlayers.add(player);
            player.setGameMode(gm);
        }
    }

    public boolean removeJustChangedByThisPluginGMPlayers(final Player player) {
        return justChangedByThisPluginGMPlayers.remove(player);
    }

    public void addManualChangeCreativeGMPlayers(final HumanEntity player) {
        manualChangeCreativeGMPlayers.add(player);
    }

    public boolean isManualChangeCreativeGMPlayers(final HumanEntity player) {
        return manualChangeCreativeGMPlayers.contains(player);
    }

    public boolean removeManualChangeCreativeGMPlayers(final HumanEntity player) {
        return manualChangeCreativeGMPlayers.remove(player);
    }

    public boolean dropItem(final Player player) {
        return !isManualChangeCreativeGMPlayers(player) && conf.isCreativeNoDrop() && !player.hasPermission(OVERRIDE_NODROP_PERM);
    }

    public void invOpen(final InventoryOpenEvent event, final HumanEntity player) {

        if (!isManualChangeCreativeGMPlayers(player) && conf.isCreativeNoOpenChest() && !player.hasPermission(OVERRIDE_NOOPENCHEST_PERM)) {

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
    public boolean build(final Event event, final Player player) {

        final Location blockLoc;

        if (!isManualChangeCreativeGMPlayers(player) && conf.isCreativeNoBuildOutside() && !player.hasPermission(OVERRIDE_NOBUILDOUTSIDE_PERM)) {

            if (event instanceof BlockBreakEvent) {
                blockLoc = ((BlockBreakEvent) event).getBlock().getLocation();
            } else {
                blockLoc = ((BlockPlaceEvent) event).getBlockPlaced().getLocation();
            }
            return !askCreativeFlag(player, secuboid.getLands().getPermissionsFlags(blockLoc));
        }
        return false;
    }

    public void checkBannedItems(final InventoryCloseEvent event, final HumanEntity player) {

        if (!isManualChangeCreativeGMPlayers(player) && !player.hasPermission(OVERRIDE_BANNEDITEMS_PERM)) {

            for (final Material mat : conf.getCreativeBannedItems()) {
                event.getPlayer().getInventory().remove(mat);
            }
        }
    }

    private boolean askCreativeFlag(final Player player, final LandPermissionsFlags landPermissionsFlags) {
        return landPermissionsFlags.checkPermissionAndInherit(player, permissionType);
    }
}
