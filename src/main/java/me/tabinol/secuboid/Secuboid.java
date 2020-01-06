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
package me.tabinol.secuboid;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.secuboid.commands.CommandListener;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.config.WorldConfig;
import me.tabinol.secuboid.config.players.PlayerConfig;
import me.tabinol.secuboid.dependencies.DependPlugin;
import me.tabinol.secuboid.economy.EcoScheduler;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.approve.ApproveNotif;
import me.tabinol.secuboid.lands.approve.Approves;
import me.tabinol.secuboid.lands.collisions.CollisionsManagerThread;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.listeners.ChatListener;
import me.tabinol.secuboid.listeners.FlyCreativeListener;
import me.tabinol.secuboid.listeners.InventoryListener;
import me.tabinol.secuboid.listeners.LandListener;
import me.tabinol.secuboid.listeners.PlayerListener;
import me.tabinol.secuboid.listeners.PvpListener;
import me.tabinol.secuboid.listeners.WorldListener;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
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
     * The maven app properties.
     */
    private MavenAppProperties mavenAppProperties;

    /**
     * the new instance.
     */
    private NewInstance newInstance;

    /**
     * The types.
     */
    private Types types;

    /**
     * The lands.
     */
    private Lands lands;

    /**
     * The world config.
     */
    private WorldConfig worldConfig;

    /**
     * The approve list.
     */
    private Approves approves;

    /**
     * The parameters.
     */
    private PermissionsFlags permissionsFlags;

    /**
     * The player conf.
     */
    private PlayerConfig playerConf;

    /**
     * The Command listener.
     */
    private CommandListener commandListener;

    /**
     * The inventory listener
     */
    private InventoryListener inventoryListener = null;

    /**
     * The inventory listener
     */
    private FlyCreativeListener flyCreativeListener = null;

    /**
     * The approve notif.
     */
    private ApproveNotif approveNotif;

    private Storage storage;

    /**
     * The storage thread.
     */
    private StorageThread storageThread = null;

    /**
     * The Lands manager thread
     */
    private CollisionsManagerThread collisionsManagerThread = null;

    /**
     * The conf.
     */
    private Config conf;

    private InventoryConfig inventoryConf = null;

    /**
     * The language.
     */
    private Lang language;

    /**
     * The depend plugin.
     */
    private DependPlugin dependPlugin;

    /**
     * The player money.
     */
    private PlayerMoney playerMoney;

    /**
     * The players cache.
     */
    private PlayersCache playersCache;

    @Override
    public void onEnable() {
        mavenAppProperties = new MavenAppProperties();
        mavenAppProperties.loadProperties();
        // Static access to «this» Secuboid
        permissionsFlags = new PermissionsFlags(this); // Must be before the configuration!
        types = new Types();
        conf = new Config(this);

        // For inventory config
        if (conf.isMultipleInventories()) {
            inventoryConf = new InventoryConfig(this);
        }

        newInstance = new NewInstance(this);
        dependPlugin = new DependPlugin(this);
        if (conf.useEconomy() && dependPlugin.getVaultEconomy() != null) {
            playerMoney = new PlayerMoney(dependPlugin.getVaultEconomy());
        } else {
            playerMoney = null;
        }
        playerConf = new PlayerConfig(this);
        playerConf.addAll();
        language = new Lang(this);
        storage = Storage.getStorageFromConfig(this, getConf().getStorage());
        storageThread = new StorageThread(this, storage);
        worldConfig = new WorldConfig(this);
        worldConfig.loadResources();
        approves = new Approves(this);

        lands = new Lands(this, worldConfig, approves);
        collisionsManagerThread = new CollisionsManagerThread(this);
        collisionsManagerThread.start();
        storageThread.loadAllAndStart();
        final WorldListener worldListener = new WorldListener(this);
        final PlayerListener playerListener = new PlayerListener(this);
        final PvpListener pvpListener = new PvpListener(this);
        final LandListener landListener = new LandListener(this);
        final ChatListener chatListener = new ChatListener(this);
        commandListener = new CommandListener(this);
        approveNotif = new ApproveNotif(this);
        approveNotif.runApproveNotifLater();
        if (dependPlugin.getVaultEconomy() != null) {
            final EcoScheduler ecoScheduler = new EcoScheduler(this);
            ecoScheduler.runTaskTimer(this, ECO_SCHEDULE_INTERVAL, ECO_SCHEDULE_INTERVAL);
        }
        playersCache = new PlayersCache(this);
        playersCache.start();
        getServer().getPluginManager().registerEvents(worldListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(pvpListener, this);
        getServer().getPluginManager().registerEvents(landListener, this);
        getServer().getPluginManager().registerEvents(chatListener, this);

        final PluginCommand pluginCommand = getCommand("secuboid");
        pluginCommand.setExecutor(commandListener);
        pluginCommand.setTabCompleter(commandListener);

        // Register events only if Inventory is active
        if (inventoryConf != null) {
            inventoryListener = new InventoryListener(this);
            getServer().getPluginManager().registerEvents(inventoryListener, this);
        }

        // Register events only if Fly and Creative is active
        if (conf.isFlyAndCreative()) {
            flyCreativeListener = new FlyCreativeListener(this);
            getServer().getPluginManager().registerEvents(flyCreativeListener, this);
        }
    }

    /**
     * Reload.
     */
    public void reload() {

        types = new Types();
        // No reload of Parameters to avoid Deregistering external parameters
        conf.reloadConfig();
        if (inventoryConf != null) {
            inventoryConf.reloadConfig();
        }
        if (conf.useEconomy() && dependPlugin.getVaultEconomy() != null) {
            playerMoney = new PlayerMoney(dependPlugin.getVaultEconomy());
        } else {
            playerMoney = null;
        }
        language.reloadConfig();
        worldConfig.loadResources();
        lands = new Lands(this, worldConfig, approves);
        storageThread.stopNextRun();
        storageThread = new StorageThread(this, storage);
        storageThread.loadAllAndStart();
        approveNotif.stopNextRun();
        approveNotif.runApproveNotifLater();
    }

    @Override
    public void onDisable() {
        // Save all inventories
        if (inventoryListener != null) {
            inventoryListener.removeAndSave();
        }
        collisionsManagerThread.stopNextRun();
        playersCache.stopNextRun();
        approveNotif.stopNextRun();
        storageThread.stopNextRun();
        playerConf.removeAll();
    }

    /**
     * Gets the maven app properties.
     *
     * @return the maven app properties
     */
    public MavenAppProperties getMavenAppProperties() {
        return mavenAppProperties;
    }

    /**
     * Gets the new instance to create some objects.
     *
     * @return the new instance
     */
    public NewInstance getNewInstance() {
        return newInstance;
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
     * Inventory config
     *
     * @return the config
     */
    public InventoryConfig getInventoryConf() {
        return inventoryConf;
    }

    /**
     * Inventory Listener,
     *
     * @return the inventory listener
     */
    public InventoryListener getInventoryListener() {
        return inventoryListener;
    }

    /**
     * fly and creative Listener.
     *
     * @return the fly and creative listener
     */
    public FlyCreativeListener getFlyCreativeListener() {
        return flyCreativeListener;
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
     * Get approve notif.
     *
     * @return the approve notif
     */
    public ApproveNotif getApproveNotif() {
        return approveNotif;
    }

    /**
     * Get player money.
     *
     * @return the player money
     */
    public PlayerMoney getPlayerMoney() {
        return playerMoney;
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
}
