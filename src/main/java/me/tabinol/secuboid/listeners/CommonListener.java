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
package me.tabinol.secuboid.listeners;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.EnumSet;

/**
 * Common methods for Listeners
 */
class CommonListener {

    final Secuboid secuboid;
    private static final EnumSet<Material> doors = EnumSet.noneOf(Material.class);

    CommonListener(Secuboid secuboid) {
        this.secuboid = secuboid;

        // Adds doors
        doors.add(Material.WOODEN_DOOR);
        doors.add(Material.TRAP_DOOR);
        doors.add(Material.FENCE_GATE);
        doors.add(Material.SPRUCE_DOOR);
        doors.add(Material.SPRUCE_FENCE_GATE);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.BIRCH_FENCE_GATE);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.JUNGLE_FENCE_GATE);
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.ACACIA_FENCE_GATE);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.DARK_OAK_FENCE_GATE);
    }

    /**
     * Check permission.
     *
     * @param land   the land
     * @param player the player
     * @param pt     the pt
     * @return true, if successful
     */
    boolean checkPermission(Land land, Player player, PermissionType pt) {
        return land.getPermissionsFlags().checkPermissionAndInherit(player, pt) == pt.getDefaultValue();
    }

    /**
     * Message permission.
     *
     * @param player the player
     */
    void messagePermission(Player player) {
        player.sendMessage(ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
    }

    /**
     * Gets the source player from entity or projectile
     *
     * @param entity the entity
     * @return the source player
     */
    Player getSourcePlayer(Entity entity) {

        Projectile damagerProjectile;

        // Check if the damager is a player
        if (entity instanceof Player) {
            return (Player) entity;
        } else if (entity instanceof Projectile
                && entity.getType() != EntityType.EGG
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
    boolean hasEcoSign(RealLand land, Block block) {
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
    private boolean hasEcoSign(Block block, Location ecoSignLoc) {
        return (block.getRelative(BlockFace.UP).getLocation().equals(ecoSignLoc) && block.getRelative(BlockFace.UP).getType() == Material.SIGN_POST)
                || isEcoSignAttached(block, BlockFace.NORTH, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.SOUTH, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.EAST, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.WEST, ecoSignLoc);
    }

    private boolean isEcoSignAttached(Block block, BlockFace face, Location ecoSignLoc) {
        Block checkBlock = block.getRelative(face);
        return checkBlock.getLocation().equals(ecoSignLoc) && checkBlock.getType() == Material.WALL_SIGN
                && ((org.bukkit.material.Sign) checkBlock.getState().getData()).getFacing() == face;
    }

    boolean isDoor(Material material) {
        return doors.contains(material);
    }

    /**
     * Gets the spawn point for a land and transform it to location
     *
     * @param land the land
     * @return the location
     */
    Location getLandSpawnPoint(Land land) {
        String strLoc;
        Location loc;

        // Check for land spawn
        if (land.getLandType() == Land.LandType.REAL) {
            if (!(strLoc = land.getPermissionsFlags().getFlagAndInherit(FlagList.SPAWN.getFlagType()).getValueString()).isEmpty()
                    && (loc = StringChanges.stringToLocation(strLoc)) != null) {
                return loc;
            }
            secuboid.getLog().warning("Teleportation requested and no spawn point for land \"" + ((RealLand) land).getName() + "\"!");
            return null;
        }

        // Check for world spawn (if land is world)
        return getWorldSpawnPoint(land.getWorldName());
    }

    Location getWorldSpawnPoint(String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            return world.getSpawnLocation();
        }

        secuboid.getLog().warning("Teleportation requested and no world named \"" + worldName + "\"!");
        return null;
    }
}
