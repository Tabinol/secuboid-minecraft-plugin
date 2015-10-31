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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.parameters.FlagList;
import me.tabinol.secuboid.utilities.ExpirableHashMap;
import me.tabinol.secuboidapi.config.players.ApiPlayerConfEntry;
import me.tabinol.secuboidapi.config.players.ApiPlayerStaticConfig;
import me.tabinol.secuboidapi.lands.ApiDummyLand;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerPlayer;

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

/**
 * PVP Listener
 */
public class PvpListener extends CommonListener implements Listener {

    /** The Constant FIRE_EXPIRE. */
    public final static long FIRE_EXPIRE = 20 * 30;

    /** The player conf. */
    private ApiPlayerStaticConfig playerConf;

    /** The player fire location. */
    private ExpirableHashMap<Location, ApiPlayerContainerPlayer> playerFireLocation;

    /**
     * Instantiates a new pvp listener.
     */
    public PvpListener() {

        super();
        playerConf = Secuboid.getThisPlugin().getPlayerConf();
        playerFireLocation = new ExpirableHashMap<Location, ApiPlayerContainerPlayer>(FIRE_EXPIRE);
    }

    /**
     * On entity damage by entity.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        ApiPlayerConfEntry entry;
        ApiPlayerConfEntry entryVictim;

        // Check if a player break a ItemFrame
        Player player = getSourcePlayer(event.getDamager());

        if (player != null) {
            ApiDummyLand land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(
                    event.getEntity().getLocation());
            Entity entity = event.getEntity();

            // For PVP
            if (entity instanceof Player &&  (entry = playerConf.get(player)) != null
                    && (entryVictim = playerConf.get((Player) entity)) != null
                    && !isPvpValid(land, entry.getPlayerContainer(),
                            entryVictim.getPlayerContainer())) {
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
        
        if(event.getBlockPlaced().getType() == Material.FIRE) {
            
            Player player = event.getPlayer();
            checkForPvpFire(event, player);
        }
        
    }

    /**
     * On block ignite.
     * 
     * @param event
     *            the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

        Player player = event.getPlayer();
        checkForPvpFire(event, player);
    }
    
    /**
     * On block spread.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        
        Block blockSource = event.getSource();
        ApiPlayerContainerPlayer pc = playerFireLocation.get(blockSource.getLocation());
        
        if(pc != null) {
            
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
        if(event.getEntity() instanceof Player && 
                (event.getCause() == DamageCause.FIRE
                || event.getCause() == DamageCause.FIRE_TICK)) {
            
            Player player = (Player) event.getEntity();
            ApiPlayerConfEntry entry = playerConf.get(player);
            
            if(entry != null) {
                Location loc = player.getLocation();
                ApiDummyLand land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(loc);
                
                // Check for fire near the player
                for(Map.Entry<Location, ApiPlayerContainerPlayer> fireEntry : playerFireLocation.entrySet()) {
                    
                    if(loc.getWorld() == fireEntry.getKey().getWorld() 
                            && loc.distanceSquared(fireEntry.getKey()) < 5) {
                        Block block = loc.getBlock();
                        if((block.getType() == Material.FIRE || block.getType() == Material.AIR) 
                                && !isPvpValid(land, fireEntry.getValue(), entry.getPlayerContainer())) {
                            
                            // remove fire
                            Secuboid.getThisPlugin().getLog().write("Anti-pvp from "
                                    + entry.getPlayerContainer().getPlayer().getName()
                                    + " to " + player.getName());
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
     * @param event the events
     * @param player the player
     */
    private void checkForPvpFire(BlockEvent event, Player player) {
        
        ApiPlayerConfEntry entry;
        
        if (player != null && (entry = playerConf.get(player)) != null) {

            Location loc = event.getBlock().getLocation();
            ApiDummyLand land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(loc);

            if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false
                    || land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {
                
                // Add fire for pvp listen
                playerFireLocation.put(loc, entry.getPlayerContainer());
            }
        }
    }
    
    /**
     * Checks if pvp is valid.
     *
     * @param land the land
     * @param attacker the attacker
     * @param victim the victim
     * @return true, if is pvp valid
     */
    private boolean isPvpValid(ApiDummyLand land, ApiPlayerContainerPlayer attacker,
            ApiPlayerContainerPlayer victim) {
        
        if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {
            return false;
        }
        
        return true;    
    }
}
