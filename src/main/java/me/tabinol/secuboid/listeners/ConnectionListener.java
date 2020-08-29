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
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.players.PlayerConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * ConnectionListener
 */
public class ConnectionListener extends CommonListener implements Listener {

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

        final UUID playerUUID = event.getUniqueId();
        final String playerName = event.getName();
        if (!doAsyncPlayerPreLogin(playerUUID, playerName)) {
            event.disallow(Result.KICK_OTHER, "Problem with Secuboid inventory. Contact an administrator.");
        }
    }

    public boolean doAsyncPlayerPreLogin(final UUID playerUUID, final String playerName) {
        if (secuboid.getInventoriesOpt().isPresent()) {
            // Inventory prelogin
            final PlayerInventoryCache playerInventoryCache = new PlayerInventoryCache(playerUUID, playerName);
            if (!playerInventoryCache.inventoryPreLogin(secuboid)) {
                return false;
            }
            // Add the player to preload map
            playerUUIDToInventoryCachePreLoad.put(playerUUID, playerInventoryCache);
        }
        return true;
    }

    /**
     * On player join.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        doPlayerJoin(player);
    }

    public void doPlayerJoin(final Player player) {
        final UUID playerUUID = player.getUniqueId();
        final String playerName = player.getName();

        // Update players cache
        secuboid.getPlayersCache().updatePlayer(player.getUniqueId(), player.getName());

        // Create a new static config
        PlayerInventoryCache playerInventoryCacheNullable = null;
        if (secuboid.getInventoriesOpt().isPresent()) {
            playerInventoryCacheNullable = playerUUIDToInventoryCachePreLoad.remove(playerUUID);
            if (playerInventoryCacheNullable == null) {
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Inventory not loaded for player [uuid=%s, name=%s]", playerUUID, playerName));
                playerInventoryCacheNullable = new PlayerInventoryCache(playerUUID, playerName);
            }
        }
        final PlayerConfEntry entry = playerConf.add(player, playerInventoryCacheNullable);

        updatePosInfo(null, entry, player.getLocation(), true);

        // Check if AdminMode is auto
        if (player.hasPermission("secuboid.adminmode.auto")) {
            playerConf.get(player).setAdminMode(true);
        }

        // Notify for approves
        secuboid.getApproveNotif().notifyListApprove(player);
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