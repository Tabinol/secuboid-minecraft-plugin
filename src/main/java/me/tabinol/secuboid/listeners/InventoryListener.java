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

import org.bukkit.Bukkit;
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
     * @param secuboid secuboid instance
     */
    public InventoryListener(Secuboid secuboid, Inventories inventories) {
        super(secuboid);
        this.inventories = inventories;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(playerConfEntry, getDummyLand(player.getLocation()),
                player.getGameMode() == GameMode.CREATIVE, Inventories.PlayerAction.JOIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    /**
     * Called when there is a shutdown
     */
    public void removeAndSave() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removePlayer(player);
        }
    }

    public void forceSave() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
            inventories.saveInventory(player, null, true, true, false, false, false);
            final InventorySpec invSpec = secuboid.getInventoryConf().getInvSpec(getDummyLand(player.getLocation()));
            inventories.saveInventory(player, invSpec.getInventoryName(),
                    player.getGameMode() == GameMode.CREATIVE, false, false, false, false);
        }
    }

    private void removePlayer(Player player) {
        inventories.switchInventory(player, getDummyLand(player.getLocation()),
                player.getGameMode() == GameMode.CREATIVE, inventories.PlayerAction.QUIT);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerGameModeChange(PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
        inventories.switchInventory(player, getDummyLand(player.getLocation()),
                event.getNewGameMode() == GameMode.CREATIVE, inventories.PlayerAction.CHANGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(player, event.getLandPermissionsFlags(),
                player.getGameMode() == GameMode.CREATIVE, inventories.PlayerAction.CHANGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLandModify(LandModifyEvent event) {
        final LandModifyEvent.LandModifyReason reason = event.getLandModifyReason();

        // Test to be specific (take specific players)
        if (reason == LandModifyEvent.LandModifyReason.AREA_ADD
                || reason == LandModifyEvent.LandModifyReason.AREA_REMOVE
                || reason == LandModifyEvent.LandModifyReason.AREA_REPLACE) {

            // Land area change, all players in the world affected
            for (Player player : event.getLand().getWorld().getPlayers()) {
                final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
                inventories.switchInventory(player, secuboid.getLands().getPermissionsFlags(player.getLocation()),
                        player.getGameMode() == GameMode.CREATIVE, inventories.PlayerAction.CHANGE);
            }
        } else if (reason != LandModifyEvent.LandModifyReason.PERMISSION_SET
                && reason != LandModifyEvent.LandModifyReason.RENAME) {

            // No land resize or area replace, only players in the land affected
            for (Player player : event.getLand().getPlayersInLandAndChildren()) {
                final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
                inventories.switchInventory(player, secuboid.getLands().getPermissionsFlags(player.getLocation()),
                        player.getGameMode() == GameMode.CREATIVE, inventories.PlayerAction.CHANGE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        inventories.switchInventory(player, getDummyLand(player.getLocation()),
                player.getGameMode() == GameMode.CREATIVE, inventories.PlayerAction.DEATH);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        final Player player = event.getPlayer();
        final PlayerInvEntry entry = inventories.getPlayerInvEntry(player);

        // For Citizens bugfix
        if (entry == null) {
            return;
        }

        // Cancel if the world is no drop
        InventorySpec invSpec = entry.getInventorySpec();

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
    public void onEntityDeath(EntityDeathEvent event) {

        // Not a player
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final PlayerInvEntry invEntry = inventories.getPlayerInvEntry(player);

        // Is from Citizens plugin
        if (invEntry == null) {
            return;
        }

        // Cancel if the world is no drop at death
        final InventorySpec invSpec = invEntry.getInventorySpec();

        if (!invSpec.isAllowDrop()) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);

        if (!player.hasPermission(InventoryConfig.PERM_IGNORE_DISABLED_COMMANDS)
                && inventories.getPlayerInvEntry(player).getInventorySpec()
                        .isDisabledCommand(event.getMessage().substring(1).split(" ")[0])) {
            event.setCancelled(true);
        }
    }

    private LandPermissionsFlags getDummyLand(Location location) {
        return secuboid.getLands().getPermissionsFlags(location);
    }

    public boolean loadDeathInventory(Player player, int deathVersion) {
        InventorySpec invSpec = inventories.getPlayerInvEntry(player).getInventorySpec();
        final PlayerConfEntry playerConfEntry = secuboid.getPlayerConf().get(player);
        return inventories.loadInventory(player, invSpec.getInventoryName(),
                player.getGameMode() == GameMode.CREATIVE, true, deathVersion);
    }

    public void saveDefaultInventory(Player player, InventorySpec invSpec) {
        inventories.saveInventory(player, invSpec.getInventoryName(), player.getGameMode() == GameMode.CREATIVE,
                false, true, true, false);
    }
}
