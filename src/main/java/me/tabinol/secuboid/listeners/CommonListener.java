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
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

/**
 * Common methods for Listeners
 */
class CommonListener {

    static final String BUTTON_SUFFIX = "_BUTTON";
    static final String PRESSURE_PLATE_SUFFIX = "_PRESSURE_PLATE";

    final Secuboid secuboid;

    CommonListener(Secuboid secuboid) {
        this.secuboid = secuboid;
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
        player.sendMessage(
                ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
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
        return (block.getRelative(BlockFace.UP).getLocation().equals(ecoSignLoc)
                && Sign.class.isAssignableFrom(block.getRelative(BlockFace.UP).getType().data))
                || isEcoSignAttached(block, BlockFace.NORTH, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.SOUTH, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.EAST, ecoSignLoc)
                || isEcoSignAttached(block, BlockFace.WEST, ecoSignLoc);
    }

    private boolean isEcoSignAttached(Block block, BlockFace face, Location ecoSignLoc) {
        Block checkBlock = block.getRelative(face);
        return checkBlock.getLocation().equals(ecoSignLoc)
                && checkBlock.getType().name().endsWith(EcoSign.WALL_SIGN_SUFFIX)
                && ((Directional) checkBlock.getState().getData()).getFacing() == face;
    }

    boolean isDoor(Material material) {
        return Openable.class.isAssignableFrom(material.data);
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
            if (!(strLoc = land.getPermissionsFlags().getFlagAndInherit(FlagList.SPAWN.getFlagType()).getValueString())
                    .isEmpty() && (loc = StringChanges.stringToLocation(strLoc)) != null) {
                return loc;
            }
            secuboid.getLog().warning(
                    "Teleportation requested and no spawn point for land \"" + ((RealLand) land).getName() + "\"!");
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
