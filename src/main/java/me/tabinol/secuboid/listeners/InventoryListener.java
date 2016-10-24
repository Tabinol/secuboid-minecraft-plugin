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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.InventoryStorage;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.lands.Land;
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

/**
 *
 * @author michel
 */
public class InventoryListener extends CommonListener implements Listener {

    private final InventoryStorage inventoryStorage;

    /**
     *
     */
    public InventoryListener() {

	super();
	inventoryStorage = new InventoryStorage();
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

	Player player = event.getPlayer();

	inventoryStorage.switchInventory(player,
		getDummyLand(player.getLocation()), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.JOIN);
    }

    /**
     *
     * @param event
     */
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

    /**
     *
     */
    public void forceSave() {

	for (Player player : Bukkit.getOnlinePlayers()) {
	    inventoryStorage.saveInventory(player, null, true, true, false, false, false);
	    InventorySpec invSpec = Secuboid.getThisPlugin().getInventoryConf().getInvSpec(getDummyLand(player.getLocation()));
	    inventoryStorage.saveInventory(player, invSpec.getInventoryName(),
		    player.getGameMode() == GameMode.CREATIVE, false, false, false, false);
	}
    }

    /**
     *
     * @param player
     */
    public void removePlayer(Player player) {

	inventoryStorage.switchInventory(player,
		getDummyLand(player.getLocation()), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.QUIT);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerGameModeChange(PlayerGameModeChangeEvent event) {

	Player player = event.getPlayer();

	inventoryStorage.switchInventory(player,
		getDummyLand(player.getLocation()), event.getNewGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {

	Player player = event.getPlayer();

	inventoryStorage.switchInventory(player,
		event.getLandOrOutside(), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLandModify(LandModifyEvent event) {

	LandModifyEvent.LandModifyReason reason = event.getLandModifyReason();

	// Test to be specific (take specific players)
	if (reason == LandModifyEvent.LandModifyReason.AREA_ADD || reason == LandModifyEvent.LandModifyReason.AREA_REMOVE
		|| reason == LandModifyEvent.LandModifyReason.AREA_REPLACE) {

	    // Land area change, all players in the world affected
	    for (Player player : event.getLand().getWorld().getPlayers()) {
		inventoryStorage.switchInventory(player,
			Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation()),
			player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
	    }
	} else if (reason != LandModifyEvent.LandModifyReason.PERMISSION_SET && reason != LandModifyEvent.LandModifyReason.PERMISSION_SET
		&& reason != LandModifyEvent.LandModifyReason.RENAME) {

	    // No land resize or area replace, only players in the land affected
	    for (Player player : event.getLand().getPlayersInLandAndChildren()) {
		inventoryStorage.switchInventory(player,
			Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation()),
			player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.CHANGE);
	    }
	}
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {

	Player player = event.getEntity();

	inventoryStorage.switchInventory(player,
		getDummyLand(player.getLocation()), player.getGameMode() == GameMode.CREATIVE, InventoryStorage.PlayerAction.DEATH);
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

	Player player = event.getPlayer();
	PlayerInvEntry entry = inventoryStorage.getPlayerInvEntry(player);

	// For Citizens bugfix
	if (entry == null) {
	    return;
	}

	// Cancel if the world is no drop
	InventorySpec invSpec = entry.getActualInv();

	if (!invSpec.isAllowDrop()) {
	    event.setCancelled(true);
	}
    }

    // On player death, prevent drop
    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

	// Not a player
	if (event.getEntityType() != EntityType.PLAYER) {
	    return;
	}

	Player player = (Player) event.getEntity();
	PlayerInvEntry invEntry = inventoryStorage.getPlayerInvEntry(player);

	// Is from Citizens plugin
	if (invEntry == null) {
	    return;
	}

	// Cancel if the world is no drop at death
	InventorySpec invSpec = invEntry.getActualInv();

	if (!invSpec.isAllowDrop()) {
	    event.setDroppedExp(0);
	    event.getDrops().clear();
	}

    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

	Player player = event.getPlayer();

	if (inventoryStorage.getPlayerInvEntry(player).getActualInv().isDisabledCommand(event.getMessage().substring(1).split(" ")[0])) {
	    event.setCancelled(true);
	}
    }

    /**
     *
     * @param location
     * @return
     */
    public Land getDummyLand(Location location) {

	return Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(location);
    }

    /**
     *
     * @param player
     * @return
     */
    public PlayerInvEntry getPlayerInvEntry(Player player) {

	return inventoryStorage.getPlayerInvEntry(player);
    }

    /**
     *
     * @param player
     * @param deathVersion
     * @return
     */
    public boolean loadDeathInventory(Player player, int deathVersion) {

	InventorySpec invSpec = inventoryStorage.getPlayerInvEntry(player).getActualInv();

	return inventoryStorage.loadInventory(player, invSpec.getInventoryName(), player.getGameMode() == GameMode.CREATIVE, true, deathVersion);
    }

    /**
     *
     * @param player
     * @param invSpec
     */
    public void saveDefaultInventory(Player player, InventorySpec invSpec) {

	inventoryStorage.saveInventory(player, invSpec.getInventoryName(),
		player.getGameMode() == GameMode.CREATIVE, false, true, true, false);
    }
}
