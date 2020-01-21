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

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.players.PlayerConfig;
import me.tabinol.secuboid.utilities.ExpirableHashMap;

/**
 * PVP Listener
 */
public final class PvpListener extends CommonListener implements Listener {

    /**
     * The Constant FIRE_EXPIRE.
     */
    public static final long FIRE_EXPIRE = 20l * 30l;

    /**
     * The player conf.
     */
    private final PlayerConfig playerConf;

    /**
     * The player fire location.
     */
    private final ExpirableHashMap<Location, PlayerContainerPlayer> playerFireLocation;

    /**
     * Instantiates a new pvp listener.
     *
     * @param secuboid secuboid instance
     */
    public PvpListener(Secuboid secuboid) {

        super(secuboid);
        playerConf = secuboid.getPlayerConf();
        playerFireLocation = new ExpirableHashMap<>(secuboid, FIRE_EXPIRE);
    }

    /**
     * On entity damage by entity.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        // Check if a player break a ItemFrame
        final Player player = getSourcePlayer(event.getDamager());

        if (player != null) {
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getEntity().getLocation());
            Entity entity = event.getEntity();

            // For PVP
            if (entity instanceof Player && playerConf.get(player) != null && playerConf.get((Player) entity) != null
                    && !isPvpValid(landPermissionsFlags)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * On block place.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getBlockPlaced().getType() == Material.FIRE) {

            Player player = event.getPlayer();
            checkForPvpFire(event, player);
        }

    }

    /**
     * On block ignite.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        final Player player = event.getPlayer();
        checkForPvpFire(event, player);
    }

    /**
     * On block spread.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {

        final Block blockSource = event.getSource();
        final PlayerContainerPlayer pc = playerFireLocation.get(blockSource.getLocation());

        if (pc != null) {

            // Add fire for pvp listen
            playerFireLocation.put(event.getBlock().getLocation(), pc);
        }
    }

    /**
     * On entity damage.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        // Check for fire cancel
        if (event.getEntity() instanceof Player
                && (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK)) {

            final Player player = (Player) event.getEntity();
            final PlayerConfEntry entry = playerConf.get(player);

            if (entry != null) {
                final Location loc = player.getLocation();
                final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);

                // Check for fire near the player
                for (Map.Entry<Location, PlayerContainerPlayer> fireEntry : playerFireLocation.entrySet()) {

                    if (loc.getWorld() == fireEntry.getKey().getWorld()
                            && loc.distanceSquared(fireEntry.getKey()) < 5) {
                        final Block block = loc.getBlock();
                        if ((block.getType() == Material.FIRE || block.getType() == Material.AIR)
                                && !isPvpValid(landPermissionsFlags)) {

                            // remove fire
                            block.setType(Material.AIR);
                            player.setFireTicks(0);
                            event.setDamage(0);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check when a player deposits fire and add it to list
     *
     * @param event  the events
     * @param player the player
     */
    private void checkForPvpFire(BlockEvent event, Player player) {

        PlayerConfEntry entry;

        if (player != null && (entry = playerConf.get(player)) != null) {

            final Location loc = event.getBlock().getLocation();
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);

            if (!landPermissionsFlags.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean()) {

                // Add fire for pvp listen
                playerFireLocation.put(loc, entry.getPlayerContainer());
            }
        }
    }

    /**
     * Checks if pvp is valid.
     *
     * @param landPermissionsFlags the land permissions flags
     * @return true, if is pvp valid
     */
    private boolean isPvpValid(LandPermissionsFlags landPermissionsFlags) {
        return landPermissionsFlags.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean();
    }
}
