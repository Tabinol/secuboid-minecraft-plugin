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

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.flycreative.Creative;
import me.tabinol.secuboid.flycreative.Fly;
import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * The fly creative class.
 */
public final class FlyCreativeListener implements Listener {

    private final Secuboid secuboid;
    private final Fly fly;
    private final Creative creative;
    private final Config conf;
    private final ArrayList<Player> ignoredGMPlayers;

    /**
     * Instantiates a new fly creative.
     *
     * @param secuboid secuboid instance
     */
    public FlyCreativeListener(Secuboid secuboid) {

        this.secuboid = secuboid;
        fly = new Fly(secuboid);
        creative = new Creative(secuboid, this);
        conf = secuboid.getConf();
        ignoredGMPlayers = new ArrayList<Player>();
    }

    public void addIgnoredGMPlayers(Player player) {

        ignoredGMPlayers.add(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        setFlyCreative(event, event.getPlayer(),
                secuboid.getLands().getPermissionsFlags(event.getPlayer().getLocation()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {

        creative.setGM(event.getPlayer(), GameMode.SURVIVAL);
        ignoredGMPlayers.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {

        setFlyCreative(event, event.getPlayer(), event.getLandPermissionsFlags());
    }

    /**
     * Bugfix when tp is from an other worlds
     *
     * @param event the player teleport event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Player player = event.getPlayer();

        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            Bukkit.getScheduler().runTaskLater(secuboid, new Runnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        setFlyCreative(null, player, secuboid.getLands().getPermissionsFlags(player.getLocation()));
                    }
                }
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();

        if (!ignoredGMPlayers.remove(player) && !conf.getIgnoredGameMode().contains(event.getNewGameMode())
                && !player.hasPermission(Creative.CREATIVE_IGNORE_PERM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLandModify(LandModifyEvent event) {

        final LandModifyEvent.LandModifyReason reason = event.getLandModifyReason();

        // Test to be specific (take specific players)
        if (reason == LandModifyEvent.LandModifyReason.AREA_ADD
                || reason == LandModifyEvent.LandModifyReason.AREA_REMOVE
                || reason == LandModifyEvent.LandModifyReason.AREA_REPLACE) {

            // Land area change, all players in the world affected
            for (Player player : event.getLand().getWorld().getPlayers()) {
                setFlyCreative(event, player, secuboid.getLands().getPermissionsFlags(player.getLocation()));
            }
        } else if (reason != LandModifyEvent.LandModifyReason.FLAG_SET
                && reason != LandModifyEvent.LandModifyReason.FLAG_UNSET
                && reason != LandModifyEvent.LandModifyReason.RENAME) {

            // No land resize or area replace, only players in the land affected
            for (Player player : event.getLand().getPlayersInLandAndChildren()) {
                setFlyCreative(event, player, secuboid.getLands().getPermissionsFlags(player.getLocation()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && creative.dropItem(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            creative.invOpen(event, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (creative.build(event, event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (creative.build(event, event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            creative.checkBannedItems(event, event.getPlayer());
        }

    }

    private void setFlyCreative(Event event, Player player, LandPermissionsFlags permissionsFlags) {
        if (!conf.getIgnoredGameMode().contains(player.getGameMode())
                && !creative.isCreative(event, player, permissionsFlags)) {
            fly.isFly(event, player, permissionsFlags);
        }
    }

    public Creative getCreative() {
        return creative;
    }
}
