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

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Bat;
import org.bukkit.event.Cancellable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractEvent;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;

/**
 * World listener
 */
public final class WorldListener extends CommonListener implements Listener {

    /**
     * The conf.
     */
    private final Config conf;

    /**
     * Instantiates a new world listener.
     *
     * @param secuboid secuboid instance
     */
    public WorldListener(final Secuboid secuboid) {

        super(secuboid);
        conf = secuboid.getConf();
    }

    /**
     * On explosion prime.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExplosionPrime(final ExplosionPrimeEvent event) {
        final Location loc = event.getEntity().getLocation();
        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);
        final EntityType entityType = event.getEntityType();

        // Check for Explosion cancel
        if ((entityType == EntityType.CREEPER
                && !landPermissionsFlags.getFlagAndInherit(FlagList.CREEPER_EXPLOSION.getFlagType()).getValueBoolean())
                || ((entityType == EntityType.PRIMED_TNT || entityType == EntityType.MINECART_TNT)
                        && !landPermissionsFlags.getFlagAndInherit(FlagList.TNT_EXPLOSION.getFlagType())
                                .getValueBoolean())
                || (entityType == EntityType.ENDER_CRYSTAL && !landPermissionsFlags
                        .getFlagAndInherit(FlagList.END_CRYSTAL_EXPLOSION.getFlagType()).getValueBoolean())
                || !landPermissionsFlags.getFlagAndInherit(FlagList.EXPLOSION.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
            if (entityType == EntityType.CREEPER) {
                event.getEntity().remove();
            }
        }
    }

    /**
     * On entity explode.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (conf.isOverrideExplosions()) {

            final float power;

            if (null != event.getEntityType()) // Creeper Explosion
            {
                switch (event.getEntityType()) {

                    case CREEPER:
                        power = 0L;
                        explodeBlocks(event, event.blockList(), FlagList.CREEPER_DAMAGE.getFlagType(),
                                event.getLocation(), event.getYield(), power, false);
                        break;

                    case WITHER_SKULL:
                        explodeBlocks(event, event.blockList(), FlagList.WITHER_DAMAGE.getFlagType(),
                                event.getLocation(), event.getYield(), 1L, false);
                        break;

                    case WITHER:
                        explodeBlocks(event, event.blockList(), FlagList.WITHER_DAMAGE.getFlagType(),
                                event.getLocation(), event.getYield(), 7L, false);
                        break;

                    case FIREBALL:
                        explodeBlocks(event, event.blockList(), FlagList.GHAST_DAMAGE.getFlagType(),
                                event.getLocation(), event.getYield(), 1L, true);
                        break;

                    case MINECART_TNT:
                    case PRIMED_TNT:
                        explodeBlocks(event, event.blockList(), FlagList.TNT_DAMAGE.getFlagType(), event.getLocation(),
                                event.getYield(), 4L, false);
                        break;

                    case ENDER_DRAGON:
                        explodeBlocks(event, event.blockList(), FlagList.ENDERDRAGON_DAMAGE.getFlagType(),
                                event.getLocation(), event.getYield(), 4L, false);
                        break;

                    case ENDER_CRYSTAL:
                        explodeBlocks(event, event.blockList(), FlagList.END_CRYSTAL_DAMAGE.getFlagType(),
                                event.getLocation(), event.getYield(), 4L, false);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockExplodeEvent(BlockExplodeEvent event) {
        final Location loc = event.getBlock().getLocation();

        explodeBlocks(event, event.blockList(), FlagList.BLOCK_DAMAGE.getFlagType(), loc, event.getYield(), 5L, true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            final Location loc = event.getClickedBlock().getLocation();
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);

            if (!landPermissionsFlags.getFlagAndInherit(FlagList.BLOCK_EXPLOSION.getFlagType()).getValueBoolean()) {
                final BlockData blockData = event.getClickedBlock().getBlockData();
                final Environment environment = loc.getWorld().getEnvironment();

                if (environment == Environment.NETHER && blockData instanceof Bed) {
                    event.setCancelled(true);
                } else if (environment == Environment.NORMAL && blockData instanceof RespawnAnchor) {
                    final RespawnAnchor respawnAnchor = (RespawnAnchor) blockData;
                    if (respawnAnchor.getCharges() == respawnAnchor.getMaximumCharges()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * On firework explosion
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFireworkExplode(final FireworkExplodeEvent event) {
        final Location loc = event.getEntity().getLocation();
        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);

        // Check if the explosion is in a land
        if (!landPermissionsFlags.getFlagAndInherit(FlagList.FIREWORK_EXPLOSION.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
            // Remove entity to stop completly the event
            event.getEntity().remove();
        }
    }

    /**
     * On hanging break.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreak(final HangingBreakEvent event) {
        if (conf.isOverrideExplosions()) {
            // Check for painting
            if (event.getCause() == RemoveCause.EXPLOSION) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Explode blocks.
     *
     * @param event   The cancellable events
     * @param blocks  the blocks
     * @param ft      the ft
     * @param loc     the loc
     * @param yield   the yield
     * @param power   the power
     * @param setFire the set fire
     */
    private void explodeBlocks(final Cancellable event, final List<Block> blocks, final FlagType ft, final Location loc,
            final float yield, final float power, final boolean setFire) {

        FlagValue value;
        boolean cancelEvent = false;
        final Iterator<Block> itBlock = blocks.iterator();
        Block block;

        // Check if 1 block or more is in a protected place
        while (itBlock.hasNext() && !cancelEvent) {
            block = itBlock.next();
            value = secuboid.getLands().getPermissionsFlags(block.getLocation()).getFlagAndInherit(ft);
            if (!value.getValueBoolean()) {
                cancelEvent = true;
            }
        }
        if (cancelEvent) {
            // Cancel Event and do a false explosion
            event.setCancelled(true);
            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, false);
        }

        // If not the events will be executed has is
    }

    /**
     * On entity change block.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());
        final Material matFrom = event.getBlock().getType();
        final Material matTo = event.getTo();

        // Enderman removeblock
        if ((event.getEntityType() == EntityType.ENDERMAN
                && !landPermissionsFlags.getFlagAndInherit(FlagList.ENDERMAN_DAMAGE.getFlagType()).getValueBoolean())
                || (event.getEntityType() == EntityType.WITHER && !landPermissionsFlags
                        .getFlagAndInherit(FlagList.WITHER_DAMAGE.getFlagType()).getValueBoolean())) {
            event.setCancelled(true);

            // Crop trample
        } else if (matFrom == Material.FARMLAND && matTo == Material.DIRT
                && !landPermissionsFlags.getFlagAndInherit(FlagList.CROP_TRAMPLE.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
        }
    }

    /**
     * On block ignite.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());

        if (((event.getCause() == IgniteCause.SPREAD || event.getCause() == IgniteCause.LAVA)
                && !landPermissionsFlags.getFlagAndInherit(FlagList.FIRESPREAD.getFlagType()).getValueBoolean())
                || !landPermissionsFlags.getFlagAndInherit(FlagList.FIRE.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
        }
    }

    /**
     * On block burn.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBurn(final BlockBurnEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());

        if ((!landPermissionsFlags.getFlagAndInherit(FlagList.FIRESPREAD.getFlagType()).getValueBoolean())
                || (!landPermissionsFlags.getFlagAndInherit(FlagList.FIRE.getFlagType()).getValueBoolean())) {
            event.setCancelled(true);
        }
    }

    /**
     * On creature spawn.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getEntity().getLocation());

        if ((event.getEntity() instanceof Animals
                && !landPermissionsFlags.getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean())
                || ((event.getEntity() instanceof Monster || event.getEntity() instanceof Slime
                        || event.getEntity() instanceof Flying)
                        && !landPermissionsFlags.getFlagAndInherit(FlagList.MOB_SPAWN.getFlagType()).getValueBoolean())
                || (event.getEntity() instanceof Villager && !landPermissionsFlags
                        .getFlagAndInherit(FlagList.VILLAGER_SPAWN.getFlagType()).getValueBoolean())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntitySpawn(final EntitySpawnEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getEntity().getLocation());

        if (event.getEntity() instanceof Bat
                && !landPermissionsFlags.getFlagAndInherit(FlagList.BAT_SPAWN.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
        }
    }

    /**
     * On leaves decay.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeavesDecay(final LeavesDecayEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());

        if (!landPermissionsFlags.getFlagAndInherit(FlagList.LEAF_DECAY.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
        }
    }

    /**
     * On block from to.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());
        final Material ml = event.getBlock().getType();

        // Liquid flow
        if ((ml == Material.LAVA
                && !landPermissionsFlags.getFlagAndInherit(FlagList.LAVA_FLOW.getFlagType()).getValueBoolean())
                || (ml == Material.WATER && !landPermissionsFlags.getFlagAndInherit(FlagList.WATER_FLOW.getFlagType())
                        .getValueBoolean())) {
            event.setCancelled(true);
        }
    }

    /**
     * On entity damage.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {

        if (conf.isOverrideExplosions() && event.getEntity() instanceof Hanging
                && (event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION
                        || event.getCause() == DamageCause.PROJECTILE)) {
            // Check for ItemFrame
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFertilize(final BlockFertilizeEvent event) {

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());

        if (!landPermissionsFlags.getFlagAndInherit(FlagList.FERTILIZE.getFlagType()).getValueBoolean()) {
            event.setCancelled(true);
        }
    }
}
