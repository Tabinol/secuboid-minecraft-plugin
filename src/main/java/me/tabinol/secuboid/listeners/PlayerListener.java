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

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.secuboid.BKVersion;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.executor.CommandCancel;
import me.tabinol.secuboid.commands.executor.CommandEcosign;
import me.tabinol.secuboid.commands.executor.CommandEcosign.SignType;
import me.tabinol.secuboid.commands.executor.CommandInfo;
import me.tabinol.secuboid.commands.executor.CommandSelect;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import me.tabinol.secuboid.config.players.PlayerStaticConfig;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags.SpecialPermPrefix;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.AreaSelection.MoveType;
import me.tabinol.secuboid.selection.region.RegionSelection;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginManager;

/**
 * Players listener
 */
public class PlayerListener extends CommonListener implements Listener {

    /**
     * The conf.
     */
    private final Config conf;

    /**
     * The player conf.
     */
    private final PlayerStaticConfig playerConf;

    /**
     * The Constant DEFAULT_TIME_LAPS.
     */
    public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds

    /**
     * The time check.
     */
    private final int timeCheck;

    /**
     * The pm.
     */
    private final PluginManager pm;

    /**
     * Instantiates a new player listener.
     */
    public PlayerListener() {

	super();
	conf = Secuboid.getThisPlugin().getConf();
	playerConf = Secuboid.getThisPlugin().getPlayerConf();
	timeCheck = DEFAULT_TIME_LAPS;
	pm = Secuboid.getThisPlugin().getServer().getPluginManager();
    }

    /**
     * On player join.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

	Player player = event.getPlayer();

	// Update players cache
	Secuboid.getThisPlugin().getPlayersCache().updatePlayer(player.getUniqueId(), player.getName());

	// Create a new static config
	PlayerConfEntry entry = playerConf.add(player);

	updatePosInfo(event, entry, player.getLocation(), true);

	// Check if AdminMod is auto
	if (player.hasPermission("secuboid.adminmod.auto")) {
	    playerConf.get(player).setAdminMod(true);
	}
    }

    // Must be running after LandListener
    /**
     * On player quit.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

	Player player = event.getPlayer();

	// Remove player from the land
	Land land = playerConf.get(player).getLastLand();
	if (land.isRealLand()) {
	    ((RealLand) land).removePlayerInLand(player);
	}

	// Remove player from Static Config
	playerConf.remove(player);
    }

    /**
     * On player teleport.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

	Location loc = event.getTo();
	Player player = event.getPlayer();
	PlayerConfEntry entry = playerConf.get(player);
	Land land;

	// BugFix Citizens plugin
	if (entry == null) {
	    return;
	}

	if (!entry.hasTpCancel()) {
	    updatePosInfo(event, entry, loc, false);
	} else {
	    entry.setTpCancel(false);
	}

	land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation());

	// TP With ender pearl
	if (!playerConf.get(event.getPlayer()).isAdminMod()
		&& event.getCause() == TeleportCause.ENDER_PEARL
		&& !checkPermission(land, player,
			PermissionList.ENDERPEARL_TP.getPermissionType())) {
	    messagePermission(player);
	    event.setCancelled(true);
	}
    }

    /**
     * On player move.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

	Player player = event.getPlayer();
	PlayerConfEntry entry = playerConf.get(player);

	if (player == null) {
	    return;
	}
	long last = entry.getLastMoveUpdate();
	long now = System.currentTimeMillis();
	if (now - last < timeCheck) {
	    return;
	}
	entry.setLastMoveUpdate(now);
	if (event.getFrom().getWorld() == event.getTo().getWorld()) {
	    if (event.getFrom().distance(event.getTo()) == 0) {
		return;
	    }
	}
	updatePosInfo(event, entry, event.getTo(), false);
    }

    /**
     * On player interact.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

	Land land;
	Material ml = event.getClickedBlock().getType();
	Player player = event.getPlayer();
	Action action = event.getAction();
	PlayerConfEntry entry;
	Location loc = event.getClickedBlock().getLocation();

	Secuboid.getThisPlugin().getLog().write(
		"PlayerInteract player name: " + event.getPlayer().getName()
		+ ", Action: " + event.getAction()
		+ ", Material: " + ml.name());

	// For infoItem
	if (player.getItemInHand() != null && action == Action.LEFT_CLICK_BLOCK
		&& player.getItemInHand().getType() == conf.getInfoItem()) {
	    try {
		new CommandInfo(player,
			Secuboid.getThisPlugin().getLands().getArea(
				event.getClickedBlock().getLocation()))
			.commandExecute();
	    } catch (SecuboidCommandException ex) {
		Logger.getLogger(PlayerListener.class.getName()).log(
			Level.SEVERE, "Error when trying to get area", ex);
	    }
	    event.setCancelled(true);

	    // For Select
	} else if (player.getItemInHand() != null
		&& action == Action.LEFT_CLICK_BLOCK
		&& player.getItemInHand().getType() == conf.getSelectItem()) {

	    try {
		new CommandSelect(player, new ArgList(new String[]{"here"},
			player), event.getClickedBlock().getLocation())
			.commandExecute();
	    } catch (SecuboidCommandException ex) {
		// Empty, message is sent by the catch
	    }

	    event.setCancelled(true);

	    // For Select Cancel
	} else if (player.getItemInHand() != null
		&& action == Action.RIGHT_CLICK_BLOCK
		&& player.getItemInHand().getType() == conf.getSelectItem()
		&& (entry = playerConf.get(player)).getSelection()
		.hasSelection()) {

	    try {
		new CommandCancel(entry, false).commandExecute();
	    } catch (SecuboidCommandException ex) {
		// Empty, message is sent by the catch
	    }

	    event.setCancelled(true);

	    // For economy (buy or rent/unrent)
	} else if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
		&& (ml == Material.SIGN_POST || ml == Material.WALL_SIGN)) {

	    RealLand trueLand = Secuboid.getThisPlugin().getLands().getLand(loc);

	    if (trueLand != null) {

		Secuboid.getThisPlugin().getLog().write("EcoSignClick: ClickLoc: " + loc + ", SignLoc" + trueLand.getSaleSignLoc());

		try {
		    if (trueLand.getSaleSignLoc() != null
			    && trueLand.getSaleSignLoc().getBlock().equals(loc.getBlock())) {
			event.setCancelled(true);
			new CommandEcosign(playerConf.get(player), trueLand,
				action, SignType.SALE).commandExecute();

		    } else if (trueLand.getRentSignLoc() != null
			    && trueLand.getRentSignLoc().getBlock().equals(loc.getBlock())) {
			event.setCancelled(true);
			new CommandEcosign(playerConf.get(player), trueLand,
				action, SignType.RENT).commandExecute();
		    }
		} catch (SecuboidCommandException ex) {
		    // Empty, message is sent by the catch
		}
	    }

	    // Citizen bug, check if entry exist before
	} else if ((entry = playerConf.get(player)) != null
		&& !entry.isAdminMod()) {
	    land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(loc);
	    if (land.isBanned(player)
		    || (action == Action.RIGHT_CLICK_BLOCK
		    && BKVersion.isDoor(ml) && !checkPermission(land, player, PermissionList.USE_DOOR.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK
		    && (ml == Material.STONE_BUTTON || ml == Material.WOOD_BUTTON)
		    && !checkPermission(land, player, PermissionList.USE_BUTTON.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.LEVER
		    && !checkPermission(land, player, PermissionList.USE_LEVER.getPermissionType()))
		    || (action == Action.PHYSICAL && (ml == Material.WOOD_PLATE || ml == Material.STONE_PLATE)
		    && !checkPermission(land, player, PermissionList.USE_PRESSUREPLATE.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.TRAPPED_CHEST
		    && !checkPermission(land, player, PermissionList.USE_TRAPPEDCHEST.getPermissionType()))
		    || (action == Action.PHYSICAL && ml == Material.STRING
		    && !checkPermission(land, player, PermissionList.USE_STRING.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.MOB_SPAWNER
		    && !checkPermission(land, player, PermissionList.USE_MOBSPAWNER.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK && (ml == Material.DAYLIGHT_DETECTOR || ml == Material.DAYLIGHT_DETECTOR_INVERTED)
		    && !checkPermission(land, player, PermissionList.USE_LIGHTDETECTOR.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.ENCHANTMENT_TABLE
		    && !checkPermission(land, player, PermissionList.USE_ENCHANTTABLE.getPermissionType()))
		    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.ANVIL
		    && !checkPermission(land, player, PermissionList.USE_ANVIL.getPermissionType()))) {

		if (action != Action.PHYSICAL) {
		    messagePermission(player);
		}
		event.setCancelled(true);

	    } else if (action == Action.RIGHT_CLICK_BLOCK
		    && ((ml == Material.CHEST
		    && !checkPermission(land, player, PermissionList.OPEN_CHEST.getPermissionType()))
		    || (ml == Material.ENDER_CHEST
		    && !checkPermission(land, player, PermissionList.OPEN_ENDERCHEST.getPermissionType()))
		    || (ml == Material.WORKBENCH
		    && !checkPermission(land, player, PermissionList.OPEN_CRAFT.getPermissionType()))
		    || (ml == Material.BREWING_STAND
		    && !checkPermission(land, player, PermissionList.OPEN_BREW.getPermissionType()))
		    || ((ml == Material.FURNACE || ml == Material.BURNING_FURNACE)
		    && !checkPermission(land, player, PermissionList.OPEN_FURNACE.getPermissionType()))
		    || (ml == Material.BEACON
		    && !checkPermission(land, player, PermissionList.OPEN_BEACON.getPermissionType()))
		    || (ml == Material.DISPENSER
		    && !checkPermission(land, player, PermissionList.OPEN_DISPENSER.getPermissionType()))
		    || (ml == Material.DROPPER
		    && !checkPermission(land, player, PermissionList.OPEN_DROPPER.getPermissionType()))
		    || (ml == Material.HOPPER
		    && !checkPermission(land, player, PermissionList.OPEN_HOPPER.getPermissionType()))
		    || (ml == Material.JUKEBOX && !checkPermission(land, player, PermissionList.OPEN_JUKEBOX.getPermissionType())))
		    // For dragon egg fix
		    || (ml == Material.DRAGON_EGG
		    && !checkPermission(land, event.getPlayer(), PermissionList.BUILD_DESTROY.getPermissionType()))) {

		messagePermission(player);
		event.setCancelled(true);

		// For armor stand
	    } else if (player.getItemInHand() != null
		    && action == Action.RIGHT_CLICK_BLOCK
		    && BKVersion.isArmorStand(player.getItemInHand().getType())
		    && ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
		    || !checkPermission(land, event.getPlayer(), PermissionList.BUILD_PLACE.getPermissionType()))) {

		messagePermission(player);
		event.setCancelled(true);

		// For head place fix (do not spawn a wither)
	    } else if (player.getItemInHand() != null
		    && action == Action.RIGHT_CLICK_BLOCK
		    && player.getItemInHand().getType() == Material.SKULL_ITEM
		    && ((land instanceof Land && ((Land) land).isBanned(event.getPlayer()))
		    || !checkPermission(land, event.getPlayer(), PermissionList.BUILD_PLACE.getPermissionType()))) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On block place.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

	// Check for fire init
	Player player = event.getPlayer();

	if (event.getBlock().getType() == Material.FIRE) {
	    if (checkForPutFire(event, player)) {
		event.setCancelled(true);
	    }
	} else if (!playerConf.get(player).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(
		    event.getBlock().getLocation());
	    Material mat = event.getBlock().getType();

	    if (land.isBanned(player)) {
		// Player banned!!
		messagePermission(player);
		event.setCancelled(true);

	    } else if (!checkPermission(land, player, PermissionList.BUILD_PLACE.getPermissionType())) {
		if (checkPermission(land, player,
			Secuboid.getThisPlugin().getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.PLACE, mat))) {
		    messagePermission(player);
		    event.setCancelled(true);
		}
	    } else if (!checkPermission(land, player,
		    Secuboid.getThisPlugin().getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.NOPLACE, mat))) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On hanging place.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {

	if (!playerConf.get(event.getPlayer()).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getEntity().getLocation());
	    Player player = event.getPlayer();

	    if (land.isBanned(player) || !checkPermission(land, player, PermissionList.BUILD_PLACE.getPermissionType())) {

		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player interact entity.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
	if (!playerConf.get(event.getPlayer()).isAdminMod()
		&& event.getRightClicked() instanceof ItemFrame) {

	    Player player = event.getPlayer();
	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getRightClicked().getLocation());

	    if (land.isBanned(player) || !checkPermission(land, player, PermissionList.BUILD_PLACE.getPermissionType())) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On block break.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

	Player player = event.getPlayer();

	if (!playerConf.get(player).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getBlock().getLocation());
	    Material mat = event.getBlock().getType();

	    if (land.isBanned(player) || (land.isRealLand() && hasEcoSign((RealLand) land, event.getBlock()))) {
		// Player banned (or ecosign)
		messagePermission(player);
		event.setCancelled(true);
	    } else if (!checkPermission(land, player, PermissionList.BUILD_DESTROY.getPermissionType())) {
		if (checkPermission(land, player,
			Secuboid.getThisPlugin().getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.DESTROY, mat))) {
		    messagePermission(player);
		    event.setCancelled(true);
		}
	    } else if (!checkPermission(land, player,
		    Secuboid.getThisPlugin().getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.NODESTROY, mat))) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On hanging break by entity.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {

	Player player;

	if (event.getRemover() instanceof Player
		&& !playerConf.get((player = (Player) event.getRemover())).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getEntity().getLocation());

	    if (land.isBanned(player) || !checkPermission(land, player, PermissionList.BUILD_DESTROY.getPermissionType())) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player drop item.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

	Player player = event.getPlayer();
	PlayerConfEntry entry = playerConf.get(player);

	if (entry != null && !entry.isAdminMod()) {
	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation());

	    if (!checkPermission(land, event.getPlayer(), PermissionList.DROP.getPermissionType())) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player pickup item.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

	if (!playerConf.get(event.getPlayer()).isAdminMod()) {
	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getPlayer().getLocation());

	    if (!checkPermission(land, event.getPlayer(), PermissionList.PICKUP.getPermissionType())) {
		messagePermission(event.getPlayer());
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player bed enter.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

	if (!playerConf.get(event.getPlayer()).isAdminMod()) {
	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(
		    event.getBed().getLocation());

	    if (land.isBanned(event.getPlayer()) || !checkPermission(land, event.getPlayer(),
		    PermissionList.SLEEP.getPermissionType())) {
		messagePermission(event.getPlayer());
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On entity damage by entity.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

	PlayerConfEntry entry;
	Player player = getSourcePlayer(event.getDamager());

	// Check for non-player kill
	if (player != null) {
	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getEntity().getLocation());
	    Entity entity = event.getEntity();
	    EntityType et = entity.getType();

	    // kill an entity (none player)
	    if ((entry = playerConf.get(player)) != null // Citizens bugfix
		    && !entry.isAdminMod()
		    && (land.isBanned(player)
		    || ((BKVersion.isArmorStand(et) || entity instanceof Hanging)
		    && !checkPermission(land, player, PermissionList.BUILD_DESTROY.getPermissionType()))
		    || (entity instanceof Animals
		    && !checkPermission(land, player, PermissionList.ANIMAL_KILL.getPermissionType()))
		    || (entity instanceof Monster
		    && !checkPermission(land, player, PermissionList.MOB_KILL.getPermissionType()))
		    || (et == EntityType.VILLAGER
		    && !checkPermission(land, player, PermissionList.VILLAGER_KILL.getPermissionType()))
		    || (et == EntityType.IRON_GOLEM
		    && !checkPermission(land, player, PermissionList.VILLAGER_GOLEM_KILL.getPermissionType()))
		    || (et == EntityType.HORSE
		    && !checkPermission(land, player, PermissionList.HORSE_KILL.getPermissionType()))
		    || (entity instanceof Tameable && ((Tameable) entity).isTamed()
		    && ((Tameable) entity).getOwner() != player
		    && !checkPermission(land, player, PermissionList.TAMED_KILL.getPermissionType())))) {

		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player bucket fill.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {

	if (!playerConf.get(event.getPlayer()).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(
		    event.getBlockClicked().getLocation());
	    Material mt = event.getBlockClicked().getType();

	    if (land.isBanned(event.getPlayer())
		    || (mt == Material.LAVA_BUCKET && !checkPermission(land, event.getPlayer(),
			    PermissionList.BUCKET_LAVA.getPermissionType()))
		    || (mt == Material.WATER_BUCKET && !checkPermission(land, event.getPlayer(),
			    PermissionList.BUCKET_WATER.getPermissionType()))) {
		messagePermission(event.getPlayer());
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player bucket empty.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

	if (!playerConf.get(event.getPlayer()).isAdminMod()) {
	    Block block = event.getBlockClicked().getRelative(event.getBlockFace());
	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(block.getLocation());
	    Material mt = event.getBucket();

	    if (land.isBanned(event.getPlayer())
		    || (mt == Material.LAVA_BUCKET && !checkPermission(land, event.getPlayer(),
			    PermissionList.BUCKET_LAVA.getPermissionType()))
		    || (mt == Material.WATER_BUCKET && !checkPermission(land, event.getPlayer(),
			    PermissionList.BUCKET_WATER.getPermissionType()))) {
		messagePermission(event.getPlayer());
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On entity change block.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

	// Crop trample
	Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getBlock().getLocation());
	Material matFrom = event.getBlock().getType();
	Material matTo = event.getTo();
	Player player;

	if (event.getEntity() instanceof Player
		&& playerConf.get(player = (Player) event.getEntity()) != null // Citizens bugfix
		&& ((land instanceof Land && ((Land) land).isBanned(player))
		|| (matFrom == Material.SOIL && matTo == Material.DIRT
		&& !checkPermission(land, player, PermissionList.CROP_TRAMPLE.getPermissionType())))) {
	    event.setCancelled(true);
	}
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    // Must be after Essentials
    public void onPlayerRespawn(PlayerRespawnEvent event) {

	Player player = event.getPlayer();
	PlayerConfEntry entry = playerConf.get(player);
	Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation());
	String strLoc;
	Location loc;

	// For repsawn after death
	if (entry != null && land.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.TP_DEATH.getPermissionType())
		&& !(strLoc = land.getPermissionsFlags().getFlagAndInherit(FlagList.SPAWN.getFlagType()).getValueString()).isEmpty()
		&& (loc = StringChanges.stringToLocation(strLoc)) != null) {
	    event.setRespawnLocation(loc);
	}
    }

    /**
     * On player respawn2.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    // For land listener
    public void onPlayerRespawn2(PlayerRespawnEvent event) {

	Player player = event.getPlayer();
	PlayerConfEntry entry = playerConf.get(player);
	Location loc = event.getRespawnLocation();

	updatePosInfo(event, entry, loc, false);
    }

    /**
     * On block ignite.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {

	if (checkForPutFire(event, event.getPlayer())) {
	    event.setCancelled(true);
	}
    }

    /**
     * On potion splash.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {

	if (event.getEntity() != null
		&& event.getEntity().getShooter() instanceof Player) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(event.getPotion().getLocation());
	    Player player = (Player) event.getEntity().getShooter();

	    if (!checkPermission(land, player,
		    PermissionList.POTION_SPLASH.getPermissionType())) {
		if (player.isOnline()) {
		    messagePermission(player);
		}
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On entity regain health.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {

	Entity entity = event.getEntity();
	Player player;
	PlayerConfEntry entry;

	if (entity != null && event.getEntity() instanceof Player
		&& (event.getRegainReason() == RegainReason.REGEN
		|| event.getRegainReason() == RegainReason.SATIATED)
		&& (entry = playerConf.get((player = (Player) event.getEntity()))) != null
		&& !entry.isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation());

	    if (!checkPermission(land, player, PermissionList.FOOD_HEAL.getPermissionType())) {
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player item consume.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {

	Player player = event.getPlayer();
	PlayerConfEntry entry;

	if ((entry = playerConf.get(player)) != null
		&& !entry.isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation());

	    if (!checkPermission(land, player, PermissionList.EAT.getPermissionType())) {
		messagePermission(player);
		event.setCancelled(true);
	    }
	}
    }

    /**
     * On player command preprocess.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

	Player player = event.getPlayer();

	if (!playerConf.get(event.getPlayer()).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(
		    player.getLocation());
	    String[] excludedCommands = land.getPermissionsFlags().getFlagAndInherit(
		    FlagList.EXCLUDE_COMMANDS.getFlagType()).getValueStringList();

	    if (excludedCommands.length > 0) {
		String commandTyped = event.getMessage().substring(1).split(" ")[0];

		for (String commandTest : excludedCommands) {

		    if (commandTest.equalsIgnoreCase(commandTyped)) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "[Secuboid] "
				+ Secuboid.getThisPlugin().getLanguage().getMessage("GENERAL.MISSINGPERMISSIONHERE"));
			return;
		    }
		}
	    }
	}
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

	if (event.getEntityType() != EntityType.PLAYER) {
	    return;
	}

	Player player = (Player) event.getEntity();

	if (playerConf.get(player) != null) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(player.getLocation());

	    if (!checkPermission(land, player, PermissionList.GOD.getPermissionType())) {
		event.setCancelled(true);
	    }
	}
    }

    /**
     * Check when a player deposits fire
     *
     * @param event the events
     * @param player the player
     * @return if the events must be cancelled
     */
    private boolean checkForPutFire(BlockEvent event, Player player) {

	if (player != null && !playerConf.get(player).isAdminMod()) {

	    Land land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(
		    event.getBlock().getLocation());

	    if ((land instanceof Land && ((Land) land).isBanned(player)) || (!checkPermission(land, player,
		    PermissionList.FIRE.getPermissionType()))) {
		messagePermission(player);
		return true;
	    }
	}

	return false;
    }

    /**
     * Update pos info.
     *
     * @param event the events
     * @param entry the entry
     * @param loc the loc
     * @param newPlayer the new player
     */
    private void updatePosInfo(Event event, PlayerConfEntry entry,
	    Location loc, boolean newPlayer) {

	Land land;
	Land landOld;
	PlayerLandChangeEvent landEvent;
	Boolean isTp;
	Player player = entry.getPlayer();

	land = Secuboid.getThisPlugin().getLands().getLandOrOutsideArea(loc);

	if (newPlayer) {
	    entry.setLastLand(landOld = land);
	} else {
	    landOld = entry.getLastLand();
	}
	if (newPlayer || land != landOld) {
	    isTp = event instanceof PlayerTeleportEvent;
	    // First parameter : If it is a new player, it is null, if not new
	    // player, it is "landOld"
	    landEvent = new PlayerLandChangeEvent(newPlayer ? null : landOld,
		    land, player, entry.getLastLoc(), loc, isTp);
	    pm.callEvent(landEvent);

	    if (landEvent.isCancelled()) {
		if (isTp && event != null) {
		    ((PlayerTeleportEvent) event).setCancelled(true);
		    return;
		}
		if (land == landOld) {
		    player.teleport(player.getWorld().getSpawnLocation());
		} else {
		    Location retLoc = entry.getLastLoc();
		    player.teleport(new Location(retLoc.getWorld(), retLoc
			    .getX(), retLoc.getBlockY(), retLoc.getZ(), loc
			    .getYaw(), loc.getPitch()));
		}
		entry.setTpCancel(true);
		return;
	    }
	    entry.setLastLand(land);

	    // Update player in the lands
	    if (landOld.isRealLand() && landOld != land) {
		((RealLand) landOld).removePlayerInLand(player);
	    }
	    if (land.isRealLand()) {
		((RealLand) land).addPlayerInLand(player);
	    }
	}
	entry.setLastLoc(loc);

	// Update visual selection
	if (entry.getSelection().hasSelection()) {
	    for (RegionSelection sel : entry.getSelection().getSelections()) {
		if (sel instanceof AreaSelection
			&& ((AreaSelection) sel).getMoveType() != MoveType.PASSIVE) {
		    ((AreaSelection) sel).playerMove();
		}
	    }
	}
    }
}
