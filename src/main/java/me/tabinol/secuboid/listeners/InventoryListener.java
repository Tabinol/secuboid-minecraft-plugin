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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.inventories.Inventories;
import me.tabinol.secuboid.inventories.Inventories.PlayerAction;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.players.PlayerConfEntry;

/**
 * The inventory listener class.
 */
public final class InventoryListener extends CommonListener implements Listener {

    private final Inventories inventories;

    /**
     * Instantiates a new inventory listener.
     *
     * @param secuboid    secuboid instance
     * @param inventories inventories instance
     */
    public InventoryListener(final Secuboid secuboid, final Inventories inventories) {
        super(secuboid);
        this.inventories = inventories;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(playerConfEntry, getDummyLand(player.getLocation()),
                player.getGameMode() == GameMode.CREATIVE, Inventories.PlayerAction.JOIN);
    }

    @EventHandler(priority = EventPriority.LOWEST) // Before Connection listener
    public void onPlayerQuit(final PlayerQuitEvent event) {
        inventories.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerGameModeChange(final PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(playerConfEntry, getDummyLand(player.getLocation()),
                event.getNewGameMode() == GameMode.CREATIVE, PlayerAction.CHANGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLandChange(final PlayerLandChangeEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(playerConfEntry, event.getLandPermissionsFlags(),
                player.getGameMode() == GameMode.CREATIVE, PlayerAction.CHANGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLandModify(final LandModifyEvent event) {
        final LandModifyEvent.LandModifyReason reason = event.getLandModifyReason();

        // Test to be specific (take specific players)
        if (reason == LandModifyEvent.LandModifyReason.AREA_ADD
                || reason == LandModifyEvent.LandModifyReason.AREA_REMOVE
                || reason == LandModifyEvent.LandModifyReason.AREA_REPLACE) {

            // Land area change, all players in the world affected
            for (final Player player : event.getLand().getWorld().getPlayers()) {
                final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
                inventories.switchInventory(playerConfEntry,
                        secuboid.getLands().getPermissionsFlags(player.getLocation()),
                        player.getGameMode() == GameMode.CREATIVE, PlayerAction.CHANGE);
            }
        } else if (reason != LandModifyEvent.LandModifyReason.PERMISSION_SET
                && reason != LandModifyEvent.LandModifyReason.RENAME) {

            // No land resize or area replace, only players in the land affected
            for (final Player player : event.getLand().getPlayersInLandAndChildren()) {
                final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
                inventories.switchInventory(playerConfEntry,
                        secuboid.getLands().getPermissionsFlags(player.getLocation()),
                        player.getGameMode() == GameMode.CREATIVE, PlayerAction.CHANGE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(playerConfEntry, getDummyLand(player.getLocation()),
                player.getGameMode() == GameMode.CREATIVE, PlayerAction.DEATH);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        // For Citizens bugfix
        if (playerConfEntry == null) {
            return;
        }

        // Cancel if the world is no drop
        final PlayerInvEntry entry = playerConfEntry.getPlayerInventoryCacheOpt().get().getCurInvEntry();
        final InventorySpec invSpec = entry.getInventorySpec();

        if (!invSpec.isAllowDrop()) {
            event.setCancelled(true);
        }
    }

    /**
     * On player death, prevent drop.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent event) {

        // Not a player
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        // For Citizens bugfix
        if (playerConfEntry == null) {
            return;
        }

        final PlayerInvEntry invEntry = playerConfEntry.getPlayerInventoryCacheOpt().get().getCurInvEntry();

        // Cancel if the world is no drop at death
        final InventorySpec invSpec = invEntry.getInventorySpec();

        if (!invSpec.isAllowDrop()) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        // Fix player disconnect before the command (mycmd)
        if (playerConfEntry == null) {
            return;
        }

        final InventorySpec inventorySpec = playerConfEntry.getPlayerInventoryCacheOpt().get().getCurInvEntry()
                .getInventorySpec();

        if (!player.hasPermission(InventoryConfig.PERM_IGNORE_DISABLED_COMMANDS)
                && inventorySpec.isDisabledCommand(event.getMessage().substring(1).split(" ")[0])) {
            event.setCancelled(true);
        }
    }

    private LandPermissionsFlags getDummyLand(final Location location) {
        return secuboid.getLands().getPermissionsFlags(location);
    }
}
