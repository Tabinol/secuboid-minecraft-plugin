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
package me.tabinol.secuboid.economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.RealLand;

/**
 * Represent the economy sign.
 *
 * @author Tabinol
 */
public class EcoSign {

    private static final String OAK_PREFIX = "OAK";
    private static final String SIGN_SUFFIX = "_SIGN";
    public static final String WALL_SIGN_SUFFIX = "_WALL_SIGN";

    private final Secuboid secuboid;

    /**
     * The land.
     */
    private final RealLand land;

    /**
     * The location.
     */
    private final Location location;

    /**
     * The facing.
     */
    private final BlockFace facing;

    /**
     * The is wall sign.
     */
    private final boolean isWallSign;

    /**
     * The prefix (kind of wook)
     */
    private final String signPrefix;

    /**
     * Instantiates a new eco sign. Creates from player position.
     *
     * @param secuboid the secuboid instance
     * @param land     the land
     * @param player   the player
     * @throws SignException the sign exception
     */
    public EcoSign(Secuboid secuboid, RealLand land, Player player) throws SignException {

        this.secuboid = secuboid;
        Block targetBlock = player.getTargetBlock(null, 10);
        Block testBlock;
        this.land = land;
        final String materialInHandName = player.getEquipment().getItemInMainHand().getType().name();
        if (materialInHandName.endsWith(SIGN_SUFFIX)) {
            // Sign in hand
            signPrefix = materialInHandName.replace(SIGN_SUFFIX, "");
        } else {
            // No sign in hand (admin)
            signPrefix = OAK_PREFIX;
        }

        if (targetBlock == null) {
            throw new SignException();
        }

        testBlock = targetBlock.getRelative(BlockFace.UP);
        if (testBlock.getType() == Material.AIR && land.isLocationInside(testBlock.getLocation())) {

            // If the block as air upside, put the block on top of it
            location = testBlock.getLocation();
            facing = signFacing(player.getLocation().getYaw());
            isWallSign = false;

        } else {

            // A Wall Sign
            facing = wallFacing(player.getLocation().getYaw());
            testBlock = targetBlock.getRelative(facing);
            if (testBlock.getType() != Material.AIR) {
                // Error no place to put the wall sign
                throw new SignException();
            }
            location = testBlock.getLocation();
            isWallSign = true;
        }

        // Target is outside the land
        if (!land.isLocationInside(this.location)) {
            throw new SignException();
        }
    }

    /**
     * Instantiates a new eco sign (If the sign is already existing only).
     *
     * @param secuboid the secuboid instance
     * @param land     the land
     * @param location the location
     * @throws SignException the sign exception
     */
    public EcoSign(Secuboid secuboid, RealLand land, Location location) throws SignException {

        this.secuboid = secuboid;
        this.land = land;
        this.location = location;

        // Load chunk
        location.getChunk().load();

        // Get Sign parameter
        Block blockPlace = location.getBlock();

        final String materialName = blockPlace.getType().name();
        if (materialName.endsWith(WALL_SIGN_SUFFIX)) {
            isWallSign = true;
            signPrefix = materialName.replace(WALL_SIGN_SUFFIX, "");
        } else if (materialName.endsWith(SIGN_SUFFIX)) {
            isWallSign = false;
            signPrefix = materialName.replace(SIGN_SUFFIX, "");
        } else {
            throw new SignException();
        }
        if (isWallSign) {
            this.facing = ((Directional) blockPlace.getBlockData()).getFacing();
        } else {
            this.facing = ((Rotatable) blockPlace.getBlockData()).getRotation();
        }
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Creates the sign for sale.
     *
     * @param price the price
     * @throws SignException the sign exception
     */
    public void createSignForSale(double price) throws SignException {

        String[] lines = new String[4];
        lines[0] = ChatColor.GREEN + secuboid.getLanguage().getMessage("SIGN.SALE.FORSALE");
        lines[1] = ChatColor.GREEN + land.getName();
        lines[2] = "";
        lines[3] = ChatColor.BLUE + secuboid.getPlayerMoney().toFormat(price);

        createSign(lines);
    }

    /**
     * Creates the sign for rent.
     *
     * @param price      the price
     * @param renew      the renew
     * @param autoRenew  the auto renew
     * @param tenantName the tenant name
     * @throws SignException the sign exception
     */
    public void createSignForRent(double price, int renew, boolean autoRenew, String tenantName) throws SignException {

        String[] lines = new String[4];

        if (tenantName != null) {
            lines[0] = ChatColor.RED + secuboid.getLanguage().getMessage("SIGN.RENT.RENTED");
            lines[1] = ChatColor.RED + tenantName;
        } else {
            lines[0] = ChatColor.GREEN + secuboid.getLanguage().getMessage("SIGN.RENT.FORRENT");
            lines[1] = ChatColor.GREEN + land.getName();
        }

        if (autoRenew) {
            lines[2] = ChatColor.BLUE + secuboid.getLanguage().getMessage("SIGN.RENT.AUTORENEW");
        } else {
            lines[2] = "";
        }

        lines[3] = ChatColor.BLUE + secuboid.getPlayerMoney().toFormat(price) + "/" + renew;

        createSign(lines);
    }

    /**
     * Creates the sign.
     *
     * @param lines the lines
     * @throws SignException the sign exception
     */
    public void createSign(String[] lines) throws SignException {

        Block blockPlace = location.getBlock();

        // Impossible to create the sign here
        if (secuboid.getLands().getLand(location) != land) {
            throw new SignException();
        }

        // Check if the facing block is solid
        if (isWallSign) {
            if (!blockPlace.getRelative(facing.getOppositeFace()).getType().isSolid()) {
                throw new SignException();
            }
        } else if (!blockPlace.getRelative(BlockFace.DOWN).getType().isSolid()) {
            throw new SignException();
        }

        // Determinate material
        Material mat;
        if (isWallSign) {
            mat = Material.getMaterial(signPrefix + WALL_SIGN_SUFFIX);
        } else {
            mat = Material.getMaterial(signPrefix + SIGN_SUFFIX);
        }

        // Create sign
        final BlockData blockData = secuboid.getServer().createBlockData(mat);

        // Set facing
        if (isWallSign) {
            ((Directional) blockData).setFacing(facing);
        } else {
            ((Rotatable) blockData).setRotation(facing);
        }
        
        // Create new sign block
        blockPlace.setBlockData(blockData);

        // Add lines
        final Sign sign = (Sign) blockPlace.getState();
        for (int t = 0; t <= 3; t++) {
            sign.setLine(t, lines[t]);
        }

        // Update sign
        sign.update();
    }

    /**
     * Removes the sign.
     */
    public void removeSign() {
        removeSign(location);
    }

    /**
     * Removes the old sign.
     *
     * @param oldSignLocation the old sign location
     */
    public void removeSign(Location oldSignLocation) {

        final Block block = oldSignLocation.getBlock();
        final BlockData blockData = block.getBlockData();

        block.getChunk().load();

        // Remove only if it is a sign;
        if (blockData instanceof org.bukkit.block.data.type.Sign) {
            block.setType(Material.AIR);

            // Drop item
            oldSignLocation.getWorld().dropItem(oldSignLocation,
                    new ItemStack(Material.getMaterial(signPrefix + SIGN_SUFFIX), 1));
        }
    }

    /**
     * Sign facing.
     *
     * @param yaw the yaw
     * @return the block face
     */
    private BlockFace signFacing(float yaw) {

        BlockFace facing;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw > 360 - 11.25 || yaw <= 11.25) {
            facing = BlockFace.NORTH;
        } else if (yaw <= (360 / 16 * 2) - 11.25) {
            facing = BlockFace.NORTH_NORTH_EAST;
        } else if (yaw <= (360 / 16 * 3) - 11.25) {
            facing = BlockFace.NORTH_EAST;
        } else if (yaw <= (360 / 16 * 4) - 11.25) {
            facing = BlockFace.EAST_NORTH_EAST;
        } else if (yaw <= (360 / 16 * 5) - 11.25) {
            facing = BlockFace.EAST;
        } else if (yaw <= (360 / 16 * 6) - 11.25) {
            facing = BlockFace.EAST_SOUTH_EAST;
        } else if (yaw <= (360 / 16 * 7) - 11.25) {
            facing = BlockFace.SOUTH_EAST;
        } else if (yaw <= (360 / 16 * 8) - 11.25) {
            facing = BlockFace.SOUTH_SOUTH_EAST;
        } else if (yaw <= (360 / 16 * 9) - 11.25) {
            facing = BlockFace.SOUTH;
        } else if (yaw <= (360 / 16 * 10) - 11.25) {
            facing = BlockFace.SOUTH_SOUTH_WEST;
        } else if (yaw <= (360 / 16 * 11) - 11.25) {
            facing = BlockFace.SOUTH_WEST;
        } else if (yaw <= (360 / 16 * 12) - 11.25) {
            facing = BlockFace.WEST_SOUTH_WEST;
        } else if (yaw <= (360 / 16 * 13) - 11.25) {
            facing = BlockFace.WEST;
        } else if (yaw <= (360 / 16 * 14) - 11.25) {
            facing = BlockFace.WEST_NORTH_WEST;
        } else if (yaw <= (360 / 16 * 15) - 11.25) {
            facing = BlockFace.NORTH_WEST;
        } else {
            facing = BlockFace.NORTH_NORTH_WEST;
        }

        return facing;
    }

    /**
     * Wall facing.
     *
     * @param yaw the yaw
     * @return the block face
     */
    private BlockFace wallFacing(float yaw) {

        BlockFace facing;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw > 315 || yaw <= 45) {
            facing = BlockFace.NORTH;
        } else if (yaw <= 135) {
            facing = BlockFace.EAST;
        } else if (yaw <= 225) {
            facing = BlockFace.SOUTH;
        } else {
            facing = BlockFace.WEST;
        }

        return facing;
    }
}
