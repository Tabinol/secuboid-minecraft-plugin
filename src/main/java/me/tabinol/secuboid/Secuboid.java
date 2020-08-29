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
package me.tabinol.secuboid;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.secuboid.commands.CommandListener;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.config.WorldConfig;
import me.tabinol.secuboid.dependencies.DependPlugin;
import me.tabinol.secuboid.economy.EcoScheduler;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.inventories.Inventories;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.approve.ApproveNotif;
import me.tabinol.secuboid.lands.approve.Approves;
import me.tabinol.secuboid.lands.collisions.CollisionsManagerThread;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.listeners.ChatListener;
import me.tabinol.secuboid.listeners.ConnectionListener;
import me.tabinol.secuboid.listeners.FlyCreativeListener;
import me.tabinol.secuboid.listeners.InventoryListener;
import me.tabinol.secuboid.listeners.LandListener;
import me.tabinol.secuboid.listeners.PlayerListener;
import me.tabinol.secuboid.listeners.PvpListener;
import me.tabinol.secuboid.listeners.WorldListener;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.playercontainer.PlayerContainers;
import me.tabinol.secuboid.players.PlayerConfig;
import me.tabinol.secuboid.playerscache.PlayersCache;
import me.tabinol.secuboid.storage.Storage;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.utilities.Lang;
import me.tabinol.secuboid.utilities.MavenAppProperties;

/**
 * The Class Secuboid.
 */
public final class Secuboid extends JavaPlugin {

    /**
     * The Economy schedule interval.
     */
    private static final int ECO_SCHEDULE_INTERVAL = 20 * 60 * 5;

    /**
     * the player containers.
     */
    private final PlayerContainers playerContainers;

    /**
     * The types.
     */
    private final Types types;

    /**
     * The lands.
     */
    private final Lands lands;

    /**
     * The world config.
     */
    private final WorldConfig worldConfig;

    /**
     * The inventories cache.
     */
    private final Inventories inventories;

    /**
     * The parameters.
     */
    private final PermissionsFlags permissionsFlags;

    /**
     * The player conf.
     */
    private final PlayerConfig playerConf;

    /**
     * The Command listener.
     */
    private final CommandListener commandListener;

    /**
     * The approve notif.
     */
    private final ApproveNotif approveNotif;

    private final EcoScheduler ecoScheduler;

    /**
     * The Lands manager thread
     */
    private final CollisionsManagerThread collisionsManagerThread;

    /**
     * The conf.
     */
    private final Config conf;

    /**
     * The language.
     */
    private final Lang language;

    /**
     * The depend plugin.
     */
    private final DependPlugin dependPlugin;

    /**
     * The player money (optional).
     */
    private final PlayerMoney playerMoney;

    /**
     * The players cache.
     */
    private final PlayersCache playersCache;

    /**
     * The connection listener.
     */
    private ConnectionListener connectionListener = null;

    /**
     * The storage thread.
     */
    private StorageThread storageThread = null;

    private boolean isInventoriesEnabled = false;
    private boolean isEconomy = false;

    public Secuboid() {
        // Load properties first
        new MavenAppProperties().loadProperties();

        permissionsFlags = new PermissionsFlags(); // Must be before the configuration!
        types = new Types();
        conf = new Config(this);
        playerContainers = new PlayerContainers(this);

        // Inventories
        final InventoryConfig inventoryConfig = new InventoryConfig(this);
        inventories = new Inventories(this, inventoryConfig);

        dependPlugin = new DependPlugin(this);
        playerMoney = new PlayerMoney(dependPlugin);
        playerConf = new PlayerConfig(this);
        language = new Lang(this);
        playersCache = new PlayersCache(this);
        worldConfig = new WorldConfig(this);
        final Approves approves = new Approves(this);
        lands = new Lands(this, worldConfig, approves);
        collisionsManagerThread = new CollisionsManagerThread(this);
        commandListener = new CommandListener(this);
        approveNotif = new ApproveNotif(this);
        ecoScheduler = new EcoScheduler(this);
    }

    @Override
    public void onEnable() {
        loadSecuboid(true);
    }

    @Override
    public void onDisable() {
        unloadSecuboid();
    }

    private void loadSecuboid(final boolean isServerBoot) {
        conf.loadConfig(isServerBoot);
        dependPlugin.loadConfig(isServerBoot);
        playerMoney.loadConfig(isServerBoot);

        if (isServerBoot) {
            isInventoriesEnabled = conf.isMultipleInventories();
            isEconomy = conf.useEconomy() && dependPlugin.getVaultEconomy() != null;
        }

        if (isInventoriesEnabled) {
            inventories.loadConfig(isServerBoot);
        }

        playerConf.loadConfig(isServerBoot);
        language.loadConfig(isServerBoot);
        worldConfig.loadConfig(isServerBoot);

        // Storage
        if (isServerBoot) {
            final Storage storage = Storage.getStorageFromConfig(this, getConf().getStorage());
            storageThread = new StorageThread(this, storage);
        }

        lands.loadConfig(isServerBoot);

        // Start threads
        playersCache.start();
        collisionsManagerThread.start();
        storageThread.loadAllAndStart();
        approveNotif.runApproveNotifLater();
        if (isServerBoot && isEconomy) {
            ecoScheduler.runTaskTimer(this, ECO_SCHEDULE_INTERVAL, ECO_SCHEDULE_INTERVAL);
        }

        if (isServerBoot) {
            // register events
            final WorldListener worldListener = new WorldListener(this);
            connectionListener = new ConnectionListener(this);
            final PlayerListener playerListener = new PlayerListener(this);
            final PvpListener pvpListener = new PvpListener(this);
            final LandListener landListener = new LandListener(this);
            final ChatListener chatListener = new ChatListener(this);
            getServer().getPluginManager().registerEvents(worldListener, this);
            getServer().getPluginManager().registerEvents(connectionListener, this);
            getServer().getPluginManager().registerEvents(playerListener, this);
            getServer().getPluginManager().registerEvents(pvpListener, this);
            getServer().getPluginManager().registerEvents(landListener, this);
            getServer().getPluginManager().registerEvents(chatListener, this);

            if (isInventoriesEnabled) {
                // Register events only if Inventory is active
                final InventoryListener inventoryListener = new InventoryListener(this, inventories);
                getServer().getPluginManager().registerEvents(inventoryListener, this);
            }

            if (conf.isFlyAndCreative()) {
                final FlyCreativeListener flyCreativeListener = new FlyCreativeListener(this);
                getServer().getPluginManager().registerEvents(flyCreativeListener, this);
            }

            // Commands
            final PluginCommand pluginCommand = getCommand("secuboid");
            pluginCommand.setExecutor(commandListener);
            pluginCommand.setTabCompleter(commandListener);
        }

        // Reload players, not only on "sd reload" because there is also de bukkit "reload" command.
        for (final Player player : this.getServer().getOnlinePlayers()) {
            final UUID playerUUID = player.getUniqueId();
            final String playerName = player.getName();

            // Reload players
            if (!connectionListener.doAsyncPlayerPreLogin(playerUUID, playerName)) {
                player.kickPlayer("Problem with Secuboid inventory. Contact an administrator.");
                continue;
            }
            connectionListener.doPlayerJoin(player);
        }
    }

    private void unloadSecuboid() {
        if (isInventoriesEnabled) {
            inventories.removeAndSave();
        }
        collisionsManagerThread.stopNextRun();
        playerConf.removeAll();
        playersCache.stopNextRun();
        approveNotif.stopNextRun();
        storageThread.stopNextRun();
    }

    /**
     * Reload.
     */
    public void reload() {
        unloadSecuboid();
        loadSecuboid(false);
    }

    /**
     * Gets the player containers.
     *
     * @return the player containers
     */
    public PlayerContainers getPlayerContainers() {
        return playerContainers;
    }

    /**
     * Get conf.
     *
     * @return the config
     */
    public Config getConf() {
        return conf;
    }

    /**
     * Inventories. Optional if inventories are enabled.
     *
     * @return the optional inventories
     */
    public Optional<Inventories> getInventoriesOpt() {
        return isInventoriesEnabled ? Optional.of(inventories) : Optional.empty();
    }

    /**
     * Gets player static configurations.
     *
     * @return the player configurations
     */
    public PlayerConfig getPlayerConf() {
        return playerConf;
    }

    /**
     * Get language.
     *
     * @return the lang
     */
    public Lang getLanguage() {
        return language;
    }

    /**
     * Gets permissions and flags instance.
     *
     * @return the permissions and flags instance
     */
    public PermissionsFlags getPermissionsFlags() {
        return permissionsFlags;
    }

    /**
     * Gets lands.
     *
     * @return the lands
     */
    public Lands getLands() {
        return lands;
    }

    /**
     * Gets land types.
     *
     * @return the land types
     */
    public Types getTypes() {
        return types;
    }

    /**
     * Get storage thread.
     *
     * @return the storage thread
     */
    public StorageThread getStorageThread() {
        return storageThread;
    }

    /**
     * Get the lands manager thread for calculations
     *
     * @return the lands manager thread
     */
    public CollisionsManagerThread getCollisionsManagerThread() {
        return collisionsManagerThread;
    }

    /**
     * Get depend plugin.
     *
     * @return the depend plugin
     */
    public DependPlugin getDependPlugin() {
        return dependPlugin;
    }

    /**
     * Get player money if economy is active.
     *
     * @return the optional player money
     */
    public Optional<PlayerMoney> getPlayerMoneyOpt() {
        return isEconomy ? Optional.of(playerMoney) : Optional.empty();
    }

    /**
     * Get players cache.
     *
     * @return the players cache
     */
    public PlayersCache getPlayersCache() {
        return playersCache;
    }

    /**
     * Gets the command listener.
     *
     * @return the command listener
     */
    public CommandListener getCommandListener() {
        return commandListener;
    }

    /**
     * Gets the approve notification system.
     *
     * @return the approve notification
     */
    public ApproveNotif getApproveNotif() {
        return approveNotif;
    }
}
