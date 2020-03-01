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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.executor.CommandCancel;
import me.tabinol.secuboid.commands.executor.CommandEcosign;
import me.tabinol.secuboid.commands.executor.CommandEcosign.SignType;
import me.tabinol.secuboid.commands.executor.CommandInfo;
import me.tabinol.secuboid.commands.executor.CommandSelect;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags.SpecialPermPrefix;
import me.tabinol.secuboid.players.PlayerConfEntry;
import me.tabinol.secuboid.players.PlayerConfig;

/**
 * Players listener
 */
public final class PlayerListener extends CommonListener implements Listener {

    /**
     * The Constant DEFAULT_TIME_LAPS.
     */
    public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds

    /**
     * The conf.
     */
    private final Config conf;

    /**
     * The player conf.
     */
    private final PlayerConfig playerConf;

    /**
     * Instantiates a new player listener.
     *
     * @param secuboid secuboid instance
     */
    public PlayerListener(final Secuboid secuboid) {
        super(secuboid);
        conf = secuboid.getConf();
        playerConf = secuboid.getPlayerConf();
    }

    /**
     * On player teleport.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {

        final Location loc = event.getTo();
        final Player player = event.getPlayer();
        final PlayerConfEntry entry = playerConf.get(player);

        // BugFix Citizens plugin
        if (entry == null) {
            return;
        }

        if (!entry.hasTpCancel()) {
            updatePosInfo(event, entry, loc, false);
        } else {
            entry.setTpCancel(false);
        }

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(player.getLocation());

        // TP With ender pearl
        if (!playerConf.get(event.getPlayer()).isAdminMode() && event.getCause() == TeleportCause.ENDER_PEARL
                && !checkPermission(landPermissionsFlags, player, PermissionList.ENDERPEARL_TP.getPermissionType())) {
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
    public void onPlayerMove(final PlayerMoveEvent event) {

        final Player player = event.getPlayer();
        final PlayerConfEntry entry = playerConf.get(player);

        if (player == null || entry == null) {
            return;
        }
        final long last = entry.getLastMoveUpdate();
        final long now = System.currentTimeMillis();
        if (now - last < DEFAULT_TIME_LAPS) {
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
    public void onPlayerInteract(final PlayerInteractEvent event) {

        final BlockState blockState = event.getClickedBlock().getState();
        final Material ml = event.getClickedBlock().getType();
        final Player player = event.getPlayer();
        final Action action = event.getAction();
        final Location loc = event.getClickedBlock().getLocation();
        final ItemStack itemInMainHand = player.getEquipment().getItemInMainHand();

        PlayerConfEntry entry;

        // For infoItem
        if (itemInMainHand != null && action == Action.LEFT_CLICK_BLOCK
                && itemInMainHand.getType() == conf.getInfoItem()) {
            try {
                final Area foundArea = secuboid.getLands().getArea(event.getClickedBlock().getLocation());
                new CommandInfo(secuboid, player, foundArea).commandExecute();
            } catch (final SecuboidCommandException ex) {
                secuboid.getLogger().log(Level.SEVERE, "Error in info command", ex);
                ex.notifySender();
            }
            event.setCancelled(true);

            // For Select
        } else if (itemInMainHand != null && action == Action.LEFT_CLICK_BLOCK
                && itemInMainHand.getType() == conf.getSelectItem()) {

            try {
                new CommandSelect(secuboid, player, new ArgList(secuboid, new String[] { "here" }, player),
                        event.getClickedBlock().getLocation()).commandExecute();
            } catch (final SecuboidCommandException ex) {
                ex.notifySender();
            }

            event.setCancelled(true);

            // For Select Cancel
        } else if (itemInMainHand != null && action == Action.RIGHT_CLICK_BLOCK
                && itemInMainHand.getType() == conf.getSelectItem()
                && playerConf.get(player).getSelection().hasSelection()) {

            try {
                new CommandCancel(secuboid, null, player, null).commandExecute();
            } catch (final SecuboidCommandException ex) {
                ex.notifySender();
            }

            event.setCancelled(true);

            // For economy (buy or rent/unrent)
        } else if (conf.useEconomy() && (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)
                && (blockState instanceof Sign)) {

            final Land trueLand = secuboid.getLands().getLand(loc);

            if (trueLand != null) {
                try {
                    if (trueLand.getSaleSignLoc() != null
                            && trueLand.getSaleSignLoc().getBlock().equals(loc.getBlock())) {
                        event.setCancelled(true);
                        new CommandEcosign(secuboid, player, trueLand, action, SignType.SALE).commandExecute();

                    } else if (trueLand.getRentSignLoc() != null
                            && trueLand.getRentSignLoc().getBlock().equals(loc.getBlock())) {
                        event.setCancelled(true);
                        new CommandEcosign(secuboid, player, trueLand, action, SignType.RENT).commandExecute();
                    }
                } catch (final SecuboidCommandException ex) {
                    ex.notifySender();
                }
            }

            // Citizen bug, check if entry exist before
        } else if ((entry = playerConf.get(player)) != null && !entry.isAdminMode()) {
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);
            if (landPermissionsFlags.isBanned(player)
                    || (action == Action.RIGHT_CLICK_BLOCK && isDoor(ml)
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_DOOR.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml.name().endsWith(BUTTON_SUFFIX)
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_BUTTON.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.LEVER
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_LEVER.getPermissionType()))
                    || (action == Action.PHYSICAL && ml.name().endsWith(PRESSURE_PLATE_SUFFIX)
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_PRESSUREPLATE.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.TRAPPED_CHEST
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_TRAPPEDCHEST.getPermissionType()))
                    || (action == Action.PHYSICAL && ml == Material.STRING
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_STRING.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.SPAWNER
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_MOBSPAWNER.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.DAYLIGHT_DETECTOR
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_LIGHTDETECTOR.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.ENCHANTING_TABLE
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_ENCHANTTABLE.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.ANVIL
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_ANVIL.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.COMPARATOR
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_COMPARATOR.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.REPEATER
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_REPEATER.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.NOTE_BLOCK
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_NOTEBLOCK.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.GRINDSTONE
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_GRINDSTONE.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.STONECUTTER
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_STONECUTTER.getPermissionType()))
                    || (action == Action.RIGHT_CLICK_BLOCK && ml == Material.BELL
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.USE_BELL.getPermissionType()))) {

                if (action != Action.PHYSICAL) {
                    messagePermission(player);
                }
                event.setCancelled(true);

            } else if (action == Action.RIGHT_CLICK_BLOCK && ((ml == Material.CHEST
                    && !checkPermission(landPermissionsFlags, player, PermissionList.OPEN_CHEST.getPermissionType()))
                    || (ml == Material.ENDER_CHEST && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_ENDERCHEST.getPermissionType()))
                    || (ml == Material.CRAFTING_TABLE && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_CRAFT.getPermissionType()))
                    || (ml == Material.BREWING_STAND && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_BREW.getPermissionType()))
                    || (Furnace.class.isAssignableFrom(ml.data) && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_FURNACE.getPermissionType()))
                    || (ml == Material.BEACON && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_BEACON.getPermissionType()))
                    || (ml == Material.DISPENSER && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_DISPENSER.getPermissionType()))
                    || (ml == Material.DROPPER && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_DROPPER.getPermissionType()))
                    || (ml == Material.HOPPER && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_HOPPER.getPermissionType()))
                    || (ml == Material.JUKEBOX && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_JUKEBOX.getPermissionType()))
                    || (ml.name().matches(".*SHULKER_BOX$") && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_SHULKER_BOX.getPermissionType()))
                    || (ml == Material.LECTERN && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_LECTERN.getPermissionType()))
                    || (ml == Material.BARREL && !checkPermission(landPermissionsFlags, player,
                            PermissionList.OPEN_BARREL.getPermissionType()))
                    || ((ml == Material.BEEHIVE || ml == Material.BEE_NEST) && !checkPermission(landPermissionsFlags,
                            player, PermissionList.OPEN_BEEHIVE.getPermissionType())))
                    // For dragon egg fix
                    || (ml == Material.DRAGON_EGG && !checkPermission(landPermissionsFlags, event.getPlayer(),
                            PermissionList.BUILD_DESTROY.getPermissionType()))) {

                messagePermission(player);
                event.setCancelled(true);

                // For armor stand
            } else if (player.getEquipment().getItemInMainHand() != null && action == Action.RIGHT_CLICK_BLOCK
                    && player.getEquipment().getItemInMainHand().getType() == Material.ARMOR_STAND
                    && (landPermissionsFlags.isBanned(event.getPlayer()) || !checkPermission(landPermissionsFlags,
                            event.getPlayer(), PermissionList.BUILD_PLACE.getPermissionType()))) {

                messagePermission(player);
                event.setCancelled(true);

                // For head place fix (do not spawn a wither)
            } else if (player.getEquipment().getItemInMainHand() != null && action == Action.RIGHT_CLICK_BLOCK
                    && Rotatable.class.isAssignableFrom(player.getEquipment().getItemInMainHand().getType().data)
                    && (landPermissionsFlags.isBanned(event.getPlayer()) || !checkPermission(landPermissionsFlags,
                            event.getPlayer(), PermissionList.BUILD_PLACE.getPermissionType()))) {
                messagePermission(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {

        final EntityType et = event.getRightClicked().getType();
        final Player player = event.getPlayer();
        final Material mat = player.getEquipment().getItemInMainHand().getType();
        final Location loc = event.getRightClicked().getLocation();

        // Citizen bug, check if entry exist before
        PlayerConfEntry entry;
        if ((entry = playerConf.get(player)) != null && !entry.isAdminMode()) {
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(loc);

            // Remove and add an item from an armor stand
            if (et == EntityType.ARMOR_STAND) {
                if ((!checkPermission(landPermissionsFlags, event.getPlayer(),
                        PermissionList.BUILD_DESTROY.getPermissionType()) && mat == Material.AIR)
                        || (!checkPermission(landPermissionsFlags, event.getPlayer(),
                                PermissionList.BUILD_PLACE.getPermissionType()) && mat != Material.AIR)) {
                    messagePermission(player);
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * On block place.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {

        // Check for fire init
        final Player player = event.getPlayer();

        if (event.getBlock().getType() == Material.FIRE) {
            if (checkForPutFire(event, player)) {
                event.setCancelled(true);
            }
        } else if (!playerConf.get(player).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getBlock().getLocation());
            final Material mat = event.getBlock().getType();

            if (landPermissionsFlags.isBanned(player)) {
                // Player banned!!
                messagePermission(player);
                event.setCancelled(true);

            } else if (!checkPermission(landPermissionsFlags, player, PermissionList.BUILD_PLACE.getPermissionType())) {
                if (checkPermission(landPermissionsFlags, player,
                        secuboid.getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.PLACE, mat))) {
                    messagePermission(player);
                    event.setCancelled(true);
                }
            } else if (!checkPermission(landPermissionsFlags, player,
                    secuboid.getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.NOPLACE, mat))) {
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
    public void onHangingPlace(final HangingPlaceEvent event) {

        if (!playerConf.get(event.getPlayer()).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getEntity().getLocation());
            final Player player = event.getPlayer();

            if (landPermissionsFlags.isBanned(player)
                    || !checkPermission(landPermissionsFlags, player, PermissionList.BUILD_PLACE.getPermissionType())) {

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
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        if (playerConf.get(event.getPlayer()).isAdminMode()) {
            return;
        }

        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getRightClicked().getLocation());
        if (landPermissionsFlags.isBanned(player) || (event.getRightClicked() instanceof ItemFrame
                && !checkPermission(landPermissionsFlags, player, PermissionList.BUILD_PLACE.getPermissionType()))
                || (event.getRightClicked() instanceof Tameable
                        && !event.getPlayer().equals(((Tameable) event.getRightClicked()).getOwner())
                        && !checkPermission(landPermissionsFlags, player, PermissionList.TAME.getPermissionType()))
                || (event.getRightClicked() instanceof Merchant
                        && !checkPermission(landPermissionsFlags, player, PermissionList.TRADE.getPermissionType()))
                || (event.getRightClicked() instanceof StorageMinecart && !checkPermission(landPermissionsFlags, player,
                        PermissionList.OPEN_CHEST.getPermissionType()))
                || (event.getRightClicked() instanceof Vehicle && !checkPermission(landPermissionsFlags, player,
                        PermissionList.USE_VEHICLE.getPermissionType()))) {
            messagePermission(player);
            event.setCancelled(true);
        }
    }

    /**
     * On block break.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();

        if (!playerConf.get(player).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getBlock().getLocation());
            final Land landNullable = landPermissionsFlags.getLandNullable();
            final Material mat = event.getBlock().getType();

            if (landPermissionsFlags.isBanned(player)
                    || (landNullable != null && hasEcoSign(landNullable, event.getBlock()))) {
                // Player banned (or ecosign)
                messagePermission(player);
                event.setCancelled(true);
            } else if (!checkPermission(landPermissionsFlags, player,
                    PermissionList.BUILD_DESTROY.getPermissionType())) {
                if (checkPermission(landPermissionsFlags, player,
                        secuboid.getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.DESTROY, mat))) {
                    messagePermission(player);
                    event.setCancelled(true);
                }
            } else if (!checkPermission(landPermissionsFlags, player,
                    secuboid.getPermissionsFlags().getSpecialPermission(SpecialPermPrefix.NODESTROY, mat))) {
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
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {

        Player player;

        if (event.getRemover() instanceof Player
                && !playerConf.get((player = (Player) event.getRemover())).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getEntity().getLocation());

            if (landPermissionsFlags.isBanned(player) || !checkPermission(landPermissionsFlags, player,
                    PermissionList.BUILD_DESTROY.getPermissionType())) {
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
    public void onPlayerDropItem(final PlayerDropItemEvent event) {

        final Player player = event.getPlayer();
        final PlayerConfEntry entry = playerConf.get(player);

        if (entry != null && !entry.isAdminMode()) {
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(player.getLocation());

            if (!checkPermission(landPermissionsFlags, event.getPlayer(), PermissionList.DROP.getPermissionType())) {
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
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();

            if (!playerConf.get(player).isAdminMode()) {
                final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                        .getPermissionsFlags(player.getLocation());

                if (!checkPermission(landPermissionsFlags, player, PermissionList.PICKUP.getPermissionType())) {
                    messagePermission(player);
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * On player bed enter.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {

        if (!playerConf.get(event.getPlayer()).isAdminMode()) {
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getBed().getLocation());

            if (landPermissionsFlags.isBanned(event.getPlayer()) || !checkPermission(landPermissionsFlags,
                    event.getPlayer(), PermissionList.SLEEP.getPermissionType())) {
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
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {

        final PlayerConfEntry entry;
        final Player player = getSourcePlayer(event.getDamager());

        // Check for non-player kill
        if (player != null) {
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getEntity().getLocation());
            final Entity entity = event.getEntity();
            final EntityType et = entity.getType();

            // kill an entity (none player)
            if ((entry = playerConf.get(player)) != null // Citizens bugfix
                    && !entry.isAdminMode()
                    && (landPermissionsFlags.isBanned(player)
                            || ((et == EntityType.ARMOR_STAND || entity instanceof Hanging)
                                    && !checkPermission(landPermissionsFlags, player,
                                            PermissionList.BUILD_DESTROY.getPermissionType()))
                            || (entity instanceof Animals && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.ANIMAL_KILL.getPermissionType()))
                            || (entity instanceof Monster && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.MOB_KILL.getPermissionType()))
                            || (et == EntityType.VILLAGER && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.VILLAGER_KILL.getPermissionType()))
                            || (et == EntityType.IRON_GOLEM && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.VILLAGER_GOLEM_KILL.getPermissionType()))
                            || (et == EntityType.HORSE && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.HORSE_KILL.getPermissionType()))
                            || (entity instanceof WaterMob && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.WATERMOB_KILL.getPermissionType()))
                            || (entity instanceof Tameable && ((Tameable) entity).isTamed()
                                    && ((Tameable) entity).getOwner() != player
                                    && !checkPermission(landPermissionsFlags, player,
                                            PermissionList.TAMED_KILL.getPermissionType())))) {

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
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {

        if (!playerConf.get(event.getPlayer()).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getBlockClicked().getLocation());
            final Material mt = event.getBlockClicked().getType();

            if (landPermissionsFlags.isBanned(event.getPlayer())
                    || (mt == Material.LAVA_BUCKET && !checkPermission(landPermissionsFlags, event.getPlayer(),
                            PermissionList.BUCKET_LAVA.getPermissionType()))
                    || (mt == Material.WATER_BUCKET && !checkPermission(landPermissionsFlags, event.getPlayer(),
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
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {

        if (!playerConf.get(event.getPlayer()).isAdminMode()) {
            final Block block = event.getBlockClicked().getRelative(event.getBlockFace());
            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(block.getLocation());
            final Material mt = event.getBucket();

            if (landPermissionsFlags.isBanned(event.getPlayer())
                    || (mt == Material.LAVA_BUCKET && !checkPermission(landPermissionsFlags, event.getPlayer(),
                            PermissionList.BUCKET_LAVA.getPermissionType()))
                    || (mt == Material.WATER_BUCKET && !checkPermission(landPermissionsFlags, event.getPlayer(),
                            PermissionList.BUCKET_WATER.getPermissionType()))) {
                messagePermission(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    /**
     * On entity block form.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityBlockForm(final EntityBlockFormEvent event) {

        // Crop trample
        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                .getPermissionsFlags(event.getBlock().getLocation());
        final Material matFrom = event.getBlock().getType();
        final Material matTo = event.getNewState().getType();
        Player player;

        if (event.getEntity() instanceof Player && playerConf.get(player = (Player) event.getEntity()) != null // Citizens
                                                                                                               // bugfix
                && ((landPermissionsFlags != null && landPermissionsFlags.isBanned(player))
                        || (matFrom == Material.FARMLAND && matTo == Material.DIRT
                                && !checkPermission(landPermissionsFlags, player,
                                        PermissionList.CROP_TRAMPLE.getPermissionType()))
                        || (matFrom == Material.WATER && matTo == Material.FROSTED_ICE
                                && !checkPermission(landPermissionsFlags, player,
                                        PermissionList.FROST_WALKER.getPermissionType())))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    // Must be after Essentials
    public void onPlayerRespawn(final PlayerRespawnEvent event) {

        final Player player = event.getPlayer();
        final PlayerConfEntry entry = playerConf.get(player);
        final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(player.getLocation());
        Location loc;

        // For repsawn after death
        if (entry != null
                && landPermissionsFlags.checkPermissionAndInherit(player, PermissionList.TP_DEATH.getPermissionType())
                && (loc = getLandSpawnPoint(landPermissionsFlags)) != null) {
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
    public void onPlayerRespawn2(final PlayerRespawnEvent event) {

        final Player player = event.getPlayer();
        final PlayerConfEntry entry = playerConf.get(player);
        final Location loc = event.getRespawnLocation();

        updatePosInfo(event, entry, loc, false);
    }

    /**
     * On block ignite.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event) {

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
    public void onPotionSplash(final PotionSplashEvent event) {

        if (event.getEntity() != null && event.getEntity().getShooter() instanceof Player) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getPotion().getLocation());
            final Player player = (Player) event.getEntity().getShooter();

            if (!checkPermission(landPermissionsFlags, player, PermissionList.POTION_SPLASH.getPermissionType())) {
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
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {

        final Entity entity = event.getEntity();
        Player player;
        PlayerConfEntry entry;

        if (entity != null && event.getEntity() instanceof Player
                && (event.getRegainReason() == RegainReason.REGEN || event.getRegainReason() == RegainReason.SATIATED)
                && (entry = playerConf.get((player = (Player) event.getEntity()))) != null && !entry.isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(player.getLocation());

            if (!checkPermission(landPermissionsFlags, player, PermissionList.FOOD_HEAL.getPermissionType())) {
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
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {

        final Player player = event.getPlayer();
        PlayerConfEntry entry;

        if ((entry = playerConf.get(player)) != null && !entry.isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(player.getLocation());

            if (!checkPermission(landPermissionsFlags, player, PermissionList.EAT.getPermissionType())
                    || (event.getItem().getType() == Material.CHORUS_FRUIT) && !checkPermission(landPermissionsFlags,
                            player, PermissionList.EAT_CHORUS_FRUIT.getPermissionType())) {
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
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {

        final Player player = event.getPlayer();

        if (!playerConf.get(event.getPlayer()).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(player.getLocation());
            final String[] excludedCommands = landPermissionsFlags
                    .getFlagAndInherit(FlagList.EXCLUDE_COMMANDS.getFlagType()).getValueStringList();

            if (excludedCommands.length > 0) {
                final String commandTyped = event.getMessage().substring(1).split(" ")[0];

                for (final String commandTest : excludedCommands) {

                    if (commandTest.equalsIgnoreCase(commandTyped)) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "[Secuboid] "
                                + secuboid.getLanguage().getMessage("GENERAL.MISSINGPERMISSIONHERE"));
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {

        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (playerConf.get(player) != null) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(player.getLocation());

            if (!checkPermission(landPermissionsFlags, player, PermissionList.GOD.getPermissionType())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPortal(final PlayerPortalEvent event) {

        final Player player = (Player) event.getPlayer();
        final PlayerConfEntry entry;

        if ((entry = playerConf.get(player)) != null && !entry.isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands().getPermissionsFlags(event.getFrom());
            final World.Environment worldEnvFrom = event.getFrom().getWorld().getEnvironment();
            final World.Environment worldEnvTo = event.getTo().getWorld().getEnvironment();

            if (((worldEnvFrom == World.Environment.NETHER || worldEnvTo == World.Environment.NETHER)
                    && !checkPermission(landPermissionsFlags, player,
                            PermissionList.NETHER_PORTAL_TP.getPermissionType()))
                    || ((worldEnvFrom == World.Environment.THE_END || worldEnvTo == World.Environment.THE_END)
                            && !checkPermission(landPermissionsFlags, player,
                                    PermissionList.END_PORTAL_TP.getPermissionType()))) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Check when a player deposits fire
     *
     * @param event  the events
     * @param player the player
     * @return if the events must be cancelled
     */
    private boolean checkForPutFire(final BlockEvent event, final Player player) {

        if (player != null && !playerConf.get(player).isAdminMode()) {

            final LandPermissionsFlags landPermissionsFlags = secuboid.getLands()
                    .getPermissionsFlags(event.getBlock().getLocation());

            if ((landPermissionsFlags != null && landPermissionsFlags.isBanned(player))
                    || (!checkPermission(landPermissionsFlags, player, PermissionList.FIRE.getPermissionType()))) {
                messagePermission(player);
                return true;
            }
        }

        return false;
    }

}
