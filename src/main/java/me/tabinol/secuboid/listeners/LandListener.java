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

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import me.tabinol.secuboid.config.players.PlayerConfig;
import me.tabinol.secuboid.events.PlayerContainerAddNoEnterEvent;
import me.tabinol.secuboid.events.PlayerContainerLandBanEvent;
import me.tabinol.secuboid.events.PlayerLandChangeEvent;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;

/**
 * Land listener
 */
public final class LandListener extends CommonListener implements Listener {

    /**
     * Number of times retry random tp
     */
    private static final int MAX_TP_PASS = 32;

    private final Random random;

    /**
     * The player heal.
     */
    private final ArrayList<Player> playerHeal;

    /**
     * The land heal.
     */
    private final LandHeal landHeal;

    /**
     * The player conf.
     */
    private final PlayerConfig playerConf;

    /**
     * The Class LandHeal.
     */
    private class LandHeal extends BukkitRunnable {

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            int foodLevel;
            double health;
            double maxHealth;

            for (Player player : playerHeal) {
                if (!player.isDead()) {
                    foodLevel = player.getFoodLevel();
                    if (foodLevel < 20) {
                        foodLevel += 5;
                        if (foodLevel > 20) {
                            foodLevel = 20;
                        }
                        player.setFoodLevel(foodLevel);
                    }
                    health = player.getHealth();
                    maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    if (health < maxHealth) {
                        health += maxHealth / 10;
                        if (health > maxHealth) {
                            health = maxHealth;
                        }
                        player.setHealth(health);
                    }
                }
            }
        }
    }

    /**
     * Instantiates a new land listener.
     *
     * @param secuboid secuboid instance
     */
    public LandListener(Secuboid secuboid) {
        super(secuboid);
        random = new Random();
        playerConf = secuboid.getPlayerConf();
        playerHeal = new ArrayList<>();
        landHeal = new LandHeal();
        landHeal.runTaskTimer(secuboid, 20, 20);
    }

    /**
     * On player quit. Must be running before FlyCreativeListener.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final LandPermissionsFlags landPermissionsFlags = playerConf.get(player).getLastLandPermissionsFlags();
        final Land landNullable = landPermissionsFlags.getLandNullable();

        // Notify for quit
        while (landNullable != null) {
            notifyPlayers(landNullable, "ACTION.PLAYEREXIT", player);
        }

        if (playerHeal.contains(player)) {
            playerHeal.remove(player);
        }
    }

    /**
     * On player land change.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {
        final Player player = event.getPlayer();
        final Land lastLandNullable = event.getLastLandPermissionsFlags().getLandNullable();
        final Land landNullable = event.getLandPermissionsFlags().getLandNullable();
        LandPermissionsFlags permissionsFlags;
        String value;

        if (lastLandNullable != null) {

            if (!(landNullable != null && lastLandNullable.isDescendants(landNullable))) {

                // Notify players for exit
                notifyPlayers(lastLandNullable, "ACTION.PLAYEREXIT", player);

                // Message quit
                value = lastLandNullable.getPermissionsFlags().getFlagNoInherit(FlagList.MESSAGE_EXIT.getFlagType())
                        .getValueString();
                if (!value.isEmpty()) {
                    player.sendMessage(ChatColor.GRAY + "[Secuboid] (" + ChatColor.GREEN + lastLandNullable.getName()
                            + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
                }
            }
        }
        if (landNullable != null) {
            permissionsFlags = landNullable.getPermissionsFlags();

            if (!playerConf.get(player).isAdminMode()) {
                // is banned or can enter
                PermissionType permissionType = PermissionList.LAND_ENTER.getPermissionType();
                if ((landNullable.isBanned(player) || permissionsFlags.checkPermissionAndInherit(player,
                        permissionType) != permissionType.getDefaultValue()) && !landNullable.isOwner(player)
                        && !player.hasPermission("secuboid.bypassban")) {
                    String message;
                    if (landNullable.isBanned(player)) {
                        message = "ACTION.BANNED";
                    } else {
                        message = "ACTION.NOENTRY";
                    }
                    if (landNullable == lastLandNullable || lastLandNullable == null) {
                        tpSpawn(player, landNullable, message);
                        return;
                    } else {
                        player.sendMessage(ChatColor.GRAY + "[Secuboid] "
                                + secuboid.getLanguage().getMessage(message, landNullable.getName()));
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (!(lastLandNullable != null && landNullable.isDescendants(lastLandNullable))) {

                // Notify players for Enter
                Land landTest = landNullable;
                while (landTest != null && landTest != lastLandNullable) {
                    notifyPlayers(landTest, "ACTION.PLAYERENTER", player);
                    landTest = landTest.getParent();
                }
                // Message join
                value = landNullable.getPermissionsFlags().getFlagNoInherit(FlagList.MESSAGE_ENTER.getFlagType())
                        .getValueString();
                if (!value.isEmpty()) {
                    player.sendMessage(ChatColor.GRAY + "[Secuboid] (" + ChatColor.GREEN + landNullable.getName()
                            + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
                }
            }

        } else {
            permissionsFlags = secuboid.getLands().getOutsideLandPermissionsFlags(event.getToLoc());
        }

        // Check for Healing
        PermissionType permissionType = PermissionList.AUTO_HEAL.getPermissionType();

        if (permissionsFlags.checkPermissionAndInherit(player, permissionType) != permissionType.getDefaultValue()) {
            if (!playerHeal.contains(player)) {
                playerHeal.add(player);
            }
        } else if (playerHeal.contains(player)) {
            playerHeal.remove(player);
        }

        // Death land
        permissionType = PermissionList.LAND_DEATH.getPermissionType();

        if (!playerConf.get(player).isAdminMode() && permissionsFlags.checkPermissionAndInherit(player,
                permissionType) != permissionType.getDefaultValue()) {
            player.setHealth(0);
        }
    }

    /**
     * On player land change (teleport).
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLandChangeTp(PlayerLandChangeEvent event) {
        final Player player = event.getPlayer();
        final Land landNullable = event.getLandPermissionsFlags().getLandNullable();
        PlayerConfEntry entry;

        if (landNullable == null || (entry = playerConf.get(player)) == null) {
            return;
        }
        LandPermissionsFlags lpf = landNullable.getPermissionsFlags();
        PermissionType permissionType = PermissionList.PORTAL_TP.getPermissionType();

        if (entry.isAdminMode() || lpf.checkPermissionAndInherit(player, permissionType)) {
            String targetTp;
            Land targetLand;
            Location targetLoc;
            World world;

            if (!(targetTp = landNullable.getPermissionsFlags().getFlagAndInherit(FlagList.PORTAL_LAND.getFlagType())
                    .getValueString()).isEmpty() && (targetLand = secuboid.getLands().getLand(targetTp)) != null
                    && (targetLoc = getLandSpawnPoint(targetLand.getPermissionsFlags())) != null) {
                player.teleport(targetLoc);
            }

            if (!(targetTp = landNullable.getPermissionsFlags()
                    .getFlagAndInherit(FlagList.PORTAL_LAND_RANDOM.getFlagType()).getValueString()).isEmpty()
                    && (targetLand = secuboid.getLands().getLand(targetTp)) != null) {
                randomTp(player, targetLand.getWorld(), targetLand);
            }

            if (!(targetTp = landNullable.getPermissionsFlags().getFlagAndInherit(FlagList.PORTAL_WORLD.getFlagType())
                    .getValueString()).isEmpty() && (targetLoc = getWorldSpawnPoint(targetTp)) != null) {
                player.teleport(targetLoc);
            }

            if (!(targetTp = landNullable.getPermissionsFlags()
                    .getFlagAndInherit(FlagList.PORTAL_WORLD_RANDOM.getFlagType()).getValueString()).isEmpty()
                    && (world = Bukkit.getWorld(targetTp)) != null) {
                randomTp(player, world, null);
            }
        }
    }

    private void randomTp(Player player, World world, Land land) {
        double randomX;
        double randomZ;
        boolean tpOk;
        int pass = 0;
        int floor;

        switch (world.getEnvironment()) {
        case NETHER:
            floor = 10;
            break;
        case THE_END:
            floor = 45;
            break;
        case NORMAL:
        default:
            floor = 63;
            break;
        }

        Location loc;
        do {
            tpOk = true;
            ++pass;
            if (land != null) {
                // tp to land
                Area[] areas = land.getAreas().toArray(new Area[0]);
                Area area = areas[random.nextInt(areas.length)];
                randomX = area.getX1() + (area.getX2() - area.getX1()) * random.nextDouble();
                randomZ = area.getZ1() + (area.getZ2() - area.getZ1()) * random.nextDouble();
            } else {
                // tp to world
                WorldBorder worldBorder = world.getWorldBorder();
                Location center = worldBorder.getCenter();
                double size = worldBorder.getSize();
                int warningDistance = worldBorder.getWarningDistance();
                double radius = (size - warningDistance) / 2;
                randomX = center.getX() - radius + radius * 2.0D * random.nextDouble();
                randomZ = center.getZ() - radius + radius * 2.0D * random.nextDouble();
            }
            loc = new Location(world, randomX, floor, randomZ);

            // Check for land (or not land if world)
            if (land != null) {
                if (!land.isLocationInside(loc)) {
                    tpOk = false;
                }
            } else {
                if (secuboid.getLands().getLand(loc) != null) {
                    tpOk = false;
                }
            }

            if (tpOk && !this.locSafe(loc)) {
                tpOk = false;
            }
        } while (!tpOk && pass <= MAX_TP_PASS);

        if (tpOk) {
            player.teleport(loc);
        } else {
            secuboid.getLogger().warning("Unable to random teleport player " + player.getName() + "!");
        }
    }

    private boolean locSafe(Location loc) {
        int max;
        if (loc.getWorld().getEnvironment() == World.Environment.NETHER) {
            max = 125;
        } else {
            max = loc.getWorld().getMaxHeight();
        }
        while (loc.getBlockY() <= max) {
            if (loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
                    && loc.getBlock().getType() == Material.AIR
                    && loc.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                return true;
            }
            loc.add(0.0D, 1.0D, 0.0D);
        }

        return false;
    }

    /**
     * On player container land ban.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerLandBan(PlayerContainerLandBanEvent event) {
        checkForBannedPlayers(event.getLand(), event.getPlayerContainer(), "ACTION.BANNED");
    }

    /**
     * On player container add no enter.
     *
     * @param event the events
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerAddNoEnter(PlayerContainerAddNoEnterEvent event) {
        checkForBannedPlayers(event.getLand(), event.getPlayerContainer(), "ACTION.NOENTRY");
    }

    /**
     * Check for banned players.
     *
     * @param land    the land
     * @param pc      the pc
     * @param message the message
     */
    private void checkForBannedPlayers(Land land, PlayerContainer pc, String message) {
        checkForBannedPlayers(land, pc, message, new ArrayList<Player>());
    }

    /**
     * Check for banned players.
     *
     * @param land        the land
     * @param pc          the pc
     * @param message     the message
     * @param kickPlayers the kicked players list
     */
    private void checkForBannedPlayers(Land land, PlayerContainer pc, String message, ArrayList<Player> kickPlayers) {
        final Player[] playersArray = land.getPlayersInLand().toArray(new Player[0]); // Fix
                                                                                      // ConcurrentModificationException

        for (final Player players : playersArray) {
            if (pc.hasAccess(players, land, land.getPermissionsFlags()) && !land.isOwner(players)
                    && !playerConf.get(players).isAdminMode() && !players.hasPermission("secuboid.bypassban")
                    && (!land.getPermissionsFlags().checkPermissionAndInherit(players,
                            PermissionList.LAND_ENTER.getPermissionType()) || land.isBanned(players))
                    && !kickPlayers.contains(players)) {
                tpSpawn(players, land, message);
                kickPlayers.add(players);
            }
        }

        // check for children
        for (final Land children : land.getChildren()) {
            checkForBannedPlayers(children, pc, message);
        }
    }

    // Notify players for land Enter/Exit

    /**
     * Notify players.
     *
     * @param land     the land
     * @param message  the message
     * @param playerIn the player in
     */
    private void notifyPlayers(Land land, String message, Player playerIn) {
        for (PlayerContainerPlayer playerC : land.getPlayersNotify()) {
            final Player player = playerC.getPlayer();

            if (player != null && player != playerIn
            // Only adminmode can see vanish
                    && (!playerConf.isVanished(playerIn) || playerConf.get(player).isAdminMode())) {
                player.sendMessage(ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage(message,
                        playerIn.getDisplayName(), land.getName() + ChatColor.GRAY));
            }
        }
    }

    /**
     * Tp spawn.
     *
     * @param player  the player
     * @param land    the land
     * @param message the message
     */
    private void tpSpawn(Player player, Land land, String message) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage(message, land.getName()));
    }
}
