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
package me.tabinol.secuboid.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandLocation;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.AreaSelection.MoveType;
import me.tabinol.secuboid.selection.region.RegionSelection;

/**
 * Common methods for Listeners
 */
abstract class CommonListener {

    static final String BUTTON_SUFFIX = "_BUTTON";
    static final String PRESSURE_PLATE_SUFFIX = "_PRESSURE_PLATE";

    final Secuboid secuboid;

    /**
     * The pm.
     */
    private final PluginManager pm;

    CommonListener(final Secuboid secuboid) {
        this.secuboid = secuboid;
        pm = secuboid.getServer().getPluginManager();
    }

    /**
     * Check permission.
     *
     * @param landSelectNullable the land
     * @param player             the player
     * @param pt                 the pt
     * @return true, if successful
     */
    final boolean checkPermission(final LandPermissionsFlags landPermissionsFlags, final Player player,
            final PermissionType pt) {
        return landPermissionsFlags.checkPermissionAndInherit(player, pt) == pt.getDefaultValue();
    }

    /**
     * Message permission.
     *
     * @param player the player
     */
    final void messagePermission(final Player player) {
        player.sendMessage(
                ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
    }

    /**
     * Gets the source player from entity or projectile
     *
     * @param entity the entity
     * @return the source player
     */
    final Player getSourcePlayer(final Entity entity) {

        Projectile damagerProjectile;

        // Check if the damager is a player
        if (entity instanceof Player) {
            return (Player) entity;
        } else if (entity instanceof Projectile && entity.getType() != EntityType.EGG
                && entity.getType() != EntityType.SNOWBALL) {
            damagerProjectile = (Projectile) entity;
            if (damagerProjectile.getShooter() instanceof Player) {
                return (Player) damagerProjectile.getShooter();
            }
        }

        return null;
    }

    /**
     * Check is the block to destroy is attached to an eco sign
     *
     * @param land  the land
     * @param block the block
     * @return true if the sign is attached
     */
    final boolean hasEcoSign(final Land land, final Block block) {
        return (land.getSaleSignLoc() != null && hasEcoSign(block, land.getSaleSignLoc()))
                || (land.getRentSignLoc() != null && hasEcoSign(block, land.getRentSignLoc()));
    }

    /**
     * Check if the block to destroy is attached to an eco sign
     *
     * @param block      the block
     * @param ecoSignLoc the eco sign location
     * @return true if the sign is attached
     */
    private boolean hasEcoSign(final Block block, final LandLocation ecoSignLandLoc) {
        final Location ecoSignLoc = ecoSignLandLoc.toLocation();
        return (block.getRelative(BlockFace.UP).getLocation().equals(ecoSignLoc)
                && Sign.class.isAssignableFrom(block.getRelative(BlockFace.UP).getType().data))
                || isEcoSignAttached(block, BlockFace.NORTH, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.SOUTH, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.EAST, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.WEST, ecoSignLoc);
    }

    private boolean isEcoSignAttached(final Block block, final BlockFace face, final Location ecoSignLoc) {
        final Block checkBlock = block.getRelative(face);
        return checkBlock.getLocation().equals(ecoSignLoc)
                && checkBlock.getType().name().endsWith(EcoSign.WALL_SIGN_SUFFIX)
                && ((Directional) checkBlock.getBlockData()).getFacing() == face;
    }

    final boolean isDoor(final Material material) {
        return Openable.class.isAssignableFrom(material.data);
    }

    /**
     * Gets the spawn point for a land and transform it to location
     *
     * @param landSelectNullable the land
     * @return the location
     */
    final Location getLandSpawnPoint(final LandPermissionsFlags landPermissionsFlags) {
        String strLoc;
        LandLocation landLoc;
        Location loc;

        // Check for land spawn
        final Land landNullable = landPermissionsFlags.getLandNullable();
        if (landNullable != null) {
            if (!(strLoc = landNullable.getPermissionsFlags().getFlagAndInherit(FlagList.SPAWN.getFlagType())
                    .getValueString()).isEmpty() && (landLoc = LandLocation.fromFileFormat(strLoc)) != null
                    && (loc = landLoc.toLocation()) != null) {
                return loc;
            }
            secuboid.getLogger()
                    .warning("Teleportation requested and no spawn point for land \"" + landNullable.getName() + "\"!");
            return null;
        }

        // Check for world spawn (if land is world)
        return getWorldSpawnPoint(landPermissionsFlags.getWorldNameNullable());
    }

    final Location getWorldSpawnPoint(final String worldNameNullable) {

        if (worldNameNullable != null) {
            final World world = Bukkit.getWorld(worldNameNullable);
            if (world != null) {
                return world.getSpawnLocation();
            }
        }

        secuboid.getLogger().warning("Teleportation requested and no world named \"" + worldNameNullable + "\"!");
        return null;
    }

    /**
     * Update pos info.
     *
     * @param event     the events
     * @param entry     the entry
     * @param loc       the loc
     * @param newPlayer the new player
     */
    protected void updatePosInfo(final Event event, final PlayerConfEntry entry, final Location loc,
            final boolean newPlayer) {

        final LandPermissionsFlags landPermissionsFlags;
        final LandPermissionsFlags oldPermissionsFlags;
        final Player player = entry.getPlayer();
        PlayerLandChangeEvent landEvent;
        Boolean isTp;

        landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);

        if (newPlayer) {
            entry.setLastLandPermissionsFlags(oldPermissionsFlags = landPermissionsFlags);
        } else {
            oldPermissionsFlags = entry.getLastLandPermissionsFlags();
        }
        if (newPlayer || landPermissionsFlags != oldPermissionsFlags) {
            isTp = event instanceof PlayerTeleportEvent;
            // First parameter : If it is a new player, it is null, if not new
            // player, it is "old"
            landEvent = new PlayerLandChangeEvent(newPlayer ? null : oldPermissionsFlags, landPermissionsFlags, player,
                    entry.getLastLoc(), loc, isTp);
            pm.callEvent(landEvent);

            if (landEvent.isCancelled()) {
                if (isTp) {
                    ((PlayerTeleportEvent) event).setCancelled(true);
                    return;
                }
                if (landPermissionsFlags == oldPermissionsFlags) {
                    player.teleport(player.getWorld().getSpawnLocation());
                } else {
                    final Location retLoc = entry.getLastLoc();
                    player.teleport(new Location(retLoc.getWorld(), retLoc.getX(), retLoc.getBlockY(), retLoc.getZ(),
                            loc.getYaw(), loc.getPitch()));
                }
                entry.setTpCancel(true);
                return;
            }
            entry.setLastLandPermissionsFlags(landPermissionsFlags);

            // Update player in the lands
            final Land oldLandNullable = oldPermissionsFlags.getLandNullable();
            if (oldLandNullable != null && oldPermissionsFlags != landPermissionsFlags) {
                oldLandNullable.removePlayerInLand(player);
            }
            final Land landNullable = landPermissionsFlags.getLandNullable();
            if (landNullable != null) {
                landNullable.addPlayerInLand(player);
            }
        }
        entry.setLastLoc(loc);

        // Update visual selection
        if (entry.getSelection().hasSelection()) {
            for (final RegionSelection sel : entry.getSelection().getSelections()) {
                if (sel instanceof AreaSelection && ((AreaSelection) sel).getMoveType() != MoveType.PASSIVE) {
                    ((AreaSelection) sel).playerMove();
                }
            }
        }
    }
}
