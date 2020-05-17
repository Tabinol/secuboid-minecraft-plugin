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

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.players.PlayerConfig;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.storage.StorageThread.SaveOn;

/**
 * ConnectionListener
 */
public class ConnectionListener extends CommonListener implements Listener {

    public static final long MAX_PLAYER_INVENTORIES_LOAD_TIME_MILLIS = Duration.ofSeconds(20).toMillis();

    /**
     * The player conf.
     */
    private final PlayerConfig playerConf;

    private final Map<UUID, PlayerInventoryCache> playerUUIDToInventoryCachePreLoad;

    /**
     * Instantiates a player connection listener.
     *
     * @param secuboid secuboid instance
     */
    public ConnectionListener(final Secuboid secuboid) {
        super(secuboid);
        playerConf = secuboid.getPlayerConf();
        playerUUIDToInventoryCachePreLoad = new ConcurrentHashMap<>();
    }

    /**
     * On async player prelogin. This method is async, do not call bukkit/Secuboid
     * direct methods here.
     * 
     * @param event the event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != Result.ALLOWED) {
            // Player not allowed, nothing to do
            return;
        }

        // Inventory prelogin
        if (secuboid.getInventoriesOpt().isPresent()) {
            inventoryPreLogin(event);
        }
    }

    private void inventoryPreLogin(final AsyncPlayerPreLoginEvent event) {
        final UUID playerUUID = event.getUniqueId();
        final String playerName = event.getName();

        // Put the new login thread in the map for wake up after inventories load
        final StorageThread storageThread = secuboid.getStorageThread();
        final Object lock = new Object();
        storageThread.addPlayerUUIDPreLogin(playerUUID, lock);

        // Load inventories from save thread
        final PlayerInventoryCache playerInventoryCache = new PlayerInventoryCache(playerUUID, playerName);
        storageThread.addSaveAction(SaveActionEnum.INVENTORY_PLAYER_LOAD, SaveOn.BOTH,
                Optional.of(playerInventoryCache));

        // Waiting for inventory load
        synchronized (lock) {
            try {
                lock.wait(MAX_PLAYER_INVENTORIES_LOAD_TIME_MILLIS);
            } catch (final InterruptedException e) {
                throw new SecuboidRuntimeException(
                        String.format("Interruption on player connexion [uuid=%s, name=%s]", playerUUID, playerName),
                        e);
            }
        }

        // Check if the inventory load is completed
        if (storageThread.removePlayerUUIDPreLogin(playerUUID) != null) {
            secuboid.getLogger().log(Level.WARNING,
                    String.format("Unable to load the inventoy of player [uuid=%s, name=%s]", playerUUID, playerName));
            event.disallow(Result.KICK_OTHER, "Problem with Secuboid inventory. Contact an administrator.");
            return;
        }

        // Add the player to preload map
        playerUUIDToInventoryCachePreLoad.put(playerUUID, playerInventoryCache);
    }

    /**
     * On player join.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        final String playerName = player.getName();

        // Update players cache
        secuboid.getPlayersCache().updatePlayer(player.getUniqueId(), player.getName());

        // Create a new static config
        final Optional<PlayerInventoryCache> playerInventoryCacheOpt;
        if (secuboid.getInventoriesOpt().isPresent()) {
            PlayerInventoryCache playerInventoryCache = playerUUIDToInventoryCachePreLoad.remove(playerUUID);
            if (playerInventoryCache == null) {
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Inventory not loaded for player [uuid=%s, name=%s]", playerUUID, playerName));
                playerInventoryCache = new PlayerInventoryCache(playerUUID, playerName);
            }
            playerInventoryCacheOpt = Optional.of(playerInventoryCache);
        } else {
            playerInventoryCacheOpt = Optional.empty();
        }
        final PlayerConfEntry entry = playerConf.add(player, playerInventoryCacheOpt);

        updatePosInfo(event, entry, player.getLocation(), true);

        // Check if AdminMode is auto
        if (player.hasPermission("secuboid.adminmode.auto")) {
            playerConf.get(player).setAdminMode(true);
        }
    }

    /**
     * On player quit. Must be running after LandListener.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {

        final Player player = event.getPlayer();

        // Remove player from the land
        final LandPermissionsFlags landPermissionsFlags = playerConf.get(player).getLastLandPermissionsFlags();
        final Land landNullable = landPermissionsFlags.getLandNullable();
        if (landNullable != null) {
            landNullable.removePlayerInLand(player);
        }

        // Remove player from Static Config
        playerConf.remove(player);
    }

}