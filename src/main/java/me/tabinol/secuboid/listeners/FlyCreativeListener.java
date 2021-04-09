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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.flycreative.Creative;
import me.tabinol.secuboid.flycreative.Fly;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * The fly creative class.
 */
public final class FlyCreativeListener implements Listener {

    private final Secuboid secuboid;
    private final Fly fly;
    private final Creative creative;
    private final Config conf;

    /**
     * Instantiates a new fly creative.
     *
     * @param secuboid secuboid instance
     */
    public FlyCreativeListener(final Secuboid secuboid) {

        this.secuboid = secuboid;
        fly = new Fly(secuboid);
        creative = new Creative(secuboid);
        conf = secuboid.getConf();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {

        setFlyCreative(event, event.getPlayer(),
                secuboid.getLands().getPermissionsFlags(event.getPlayer().getLocation()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        creative.setGM(player, GameMode.SURVIVAL);
        creative.removeJustChangedByThisPluginGMPlayers(player);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerLandChange(final PlayerLandChangeEvent event) {

        setFlyCreative(event, event.getPlayer(), event.getLandPermissionsFlags());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            onPlayerTeleportRespawn(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        onPlayerTeleportRespawn(player);
    }

    /*
     * Bugfix when tp is from an other worlds
     */
    private void onPlayerTeleportRespawn(Player player) {

        Bukkit.getScheduler().runTaskLater(secuboid, () -> {
            if (player.isOnline()) {
                setFlyCreative(null, player, secuboid.getLands().getPermissionsFlags(player.getLocation()));
            }
        }, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerGameModeChangeEvent(final PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        final GameMode newGameMode = event.getNewGameMode();

        // Noting to do, just changed by Secuboid, ignored game mode or player has
        // ignore permission
        if (creative.removeJustChangedByThisPluginGMPlayers(player) || conf.getIgnoredGameMode().contains(newGameMode)
                || player.hasPermission(Creative.CREATIVE_IGNORE_PERM)) {
            return;
        }

        // Detect manual GM change out of survival
        if (newGameMode == GameMode.SURVIVAL) {
            creative.removeManualChangeCreativeGMPlayers(player);

            // Put back creative/fly of the land 20 tick after, outside of this event
            Bukkit.getScheduler().runTaskLater(secuboid, () -> setFlyCreative(null, player,
                    secuboid.getLands().getPermissionsFlags(event.getPlayer().getLocation())), 1);

        } else {
            creative.addManualChangeCreativeGMPlayers(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLandModify(final LandModifyEvent event) {

        final LandModifyEvent.LandModifyReason reason = event.getLandModifyReason();

        // Test to be specific (take specific players)
        if (reason == LandModifyEvent.LandModifyReason.AREA_ADD
                || reason == LandModifyEvent.LandModifyReason.AREA_REMOVE
                || reason == LandModifyEvent.LandModifyReason.AREA_REPLACE) {

            // Land area change, all players in the world affected
            for (final Player player : event.getLand().getWorld().getPlayers()) {
                setFlyCreative(event, player, secuboid.getLands().getPermissionsFlags(player.getLocation()));
            }
        } else if (reason != LandModifyEvent.LandModifyReason.FLAG_SET
                && reason != LandModifyEvent.LandModifyReason.FLAG_UNSET
                && reason != LandModifyEvent.LandModifyReason.RENAME) {

            // No land resize or area replace, only players in the land affected
            for (final Player player : event.getLand().getPlayersInLandAndChildren()) {
                setFlyCreative(event, player, secuboid.getLands().getPermissionsFlags(player.getLocation()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && creative.dropItem(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final HumanEntity player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            creative.invOpen(event, player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            if (creative.build(event, player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            if (creative.build(event, player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final HumanEntity player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            creative.checkBannedItems(event, player);
        }

    }

    private void setFlyCreative(final Event event, final Player player, final LandPermissionsFlags permissionsFlags) {
        if (!secuboid.getDependPlugin().getVanish().isVanished(player)
                && !conf.getIgnoredGameMode().contains(player.getGameMode())
                && !creative.isManualChangeCreativeGMPlayers(player)
                && !creative.isCreative(event, player, permissionsFlags)) {
            fly.isFly(event, player, permissionsFlags);
        }
    }

    public Creative getCreative() {
        return creative;
    }
}
