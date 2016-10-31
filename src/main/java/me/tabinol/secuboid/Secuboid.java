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

import java.io.File;
import me.tabinol.secuboid.commands.OnCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.DependPlugin;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.config.players.PlayerStaticConfig;
import me.tabinol.secuboid.economy.EcoScheduler;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.approve.ApproveNotif;
import me.tabinol.secuboid.lands.collisions.CollisionsManagerThread;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.listeners.*;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.playerscache.PlayersCache;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.utilities.Lang;
import me.tabinol.secuboid.utilities.Log;
import me.tabinol.secuboid.utilities.MavenAppProperties;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Class Secuboid.
 */
public class Secuboid extends JavaPlugin {

    /**
     * The Economy schedule interval.
     */
    public static final int ECO_SCHEDULE_INTERVAL = 20 * 60 * 5;

    /**
     * The maven app properties.
     */
    private static MavenAppProperties mavenAppProperties;

    /**
     * The this plugin.
     */
    private static Secuboid thisPlugin;

    /**
     * The types
     */
    protected static Types types;

    /**
     * The lands.
     */
    protected static Lands lands;

    /**
     * The parameters.
     */
    protected static PermissionsFlags PermissionsFlags;

    /**
     * The player conf.
     */
    protected PlayerStaticConfig playerConf;

    /**
     * The Command listener.
     */
    private OnCommand CommandListener;

    /**
     * The player listener.
     */
    private PlayerListener playerListener;

    /**
     * The player listener 18.
     */
    private PlayerListener18 playerListener18;

    /**
     * The player listener.
     */
    private PvpListener pvpListener;

    /**
     * The world listener.
     */
    private WorldListener worldListener;

    /**
     * The land listener.
     */
    private LandListener landListener;

    /**
     * The chat listener.
     */
    private ChatListener chatListener;

    /**
     * The inventory listener
     */
    private InventoryListener inventoryListener = null;

    /**
     * The inventory listener
     */
    private FlyCreativeListener flyCreativeListener = null;

    /**
     * The economy scheduler.
     */
    private EcoScheduler ecoScheduler;

    /**
     * The approve notif.
     */
    private ApproveNotif approveNotif;

    /**
     * The storage thread.
     */
    private StorageThread storageThread = null;

    /**
     * The Lands manager thread
     */
    private CollisionsManagerThread collisionsManagerThread = null;

    /**
     * The log.
     */
    private Log log;

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

    /**
     * Gets the maven app properties.
     *
     * @return the maven app properties
     */
    public static MavenAppProperties getMavenAppProperties() {

	return mavenAppProperties;
    }

    /**
     * Gets the this plugin.
     *
     * @return the this plugin
     */
    public static Secuboid getThisPlugin() {

	return thisPlugin;
    }

    /**
     * Gets the configuration folder for this plugin
     *
     * @return a File instance of the data folder
     */
    public static File getConfigFolder() {

	return thisPlugin.getDataFolder();
    }

    /**
     *
     * @return
     */
    public static PermissionsFlags getStaticParameters() {

	return thisPlugin.getPermissionsFlags();
    }

    /**
     *
     * @return
     */
    public static Types getStaticTypes() {

	return thisPlugin.getTypes();
    }

    /* (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    /**
     *
     */
    @Override
    public void onEnable() {

	mavenAppProperties = new MavenAppProperties();
	mavenAppProperties.loadProperties();
	// Static access to «this» Secuboid
	thisPlugin = this;
	BKVersion.initVersion();
	// TODO Ractivate API ApiSecuboidSta.initSecuboidPluginAccess();
	PermissionsFlags = new PermissionsFlags(); // Must be before the configuration!
	types = new Types();
	conf = new Config();

	// For inventory config
	if (conf.isMultipleInventories()) {
	    inventoryConf = new InventoryConfig();
	}

	log = new Log();
	dependPlugin = new DependPlugin();
	if (conf.useEconomy() && dependPlugin.getEconomy() != null) {
	    playerMoney = new PlayerMoney();
	} else {
	    playerMoney = null;
	}
	playerConf = new PlayerStaticConfig();
	playerConf.addAll();
	language = new Lang();
	storageThread = new StorageThread();
	lands = new Lands();
	collisionsManagerThread = new CollisionsManagerThread();
	collisionsManagerThread.start();
	storageThread.loadAllAndStart();
	worldListener = new WorldListener();
	playerListener = new PlayerListener();
	if (BKVersion.isPlayerInteractAtEntityEventExist()) {
	    playerListener18 = new PlayerListener18();
	}
	pvpListener = new PvpListener();
	landListener = new LandListener();
	chatListener = new ChatListener();
	CommandListener = new OnCommand();
	approveNotif = new ApproveNotif();
	approveNotif.runApproveNotifLater();
	ecoScheduler = new EcoScheduler();
	ecoScheduler.runTaskTimer(this, ECO_SCHEDULE_INTERVAL, ECO_SCHEDULE_INTERVAL);
	playersCache = new PlayersCache();
	playersCache.start();
	getServer().getPluginManager().registerEvents(worldListener, this);
	getServer().getPluginManager().registerEvents(playerListener, this);
	if (BKVersion.isPlayerInteractAtEntityEventExist()) {
	    getServer().getPluginManager().registerEvents(playerListener18, this);
	}
	getServer().getPluginManager().registerEvents(pvpListener, this);
	getServer().getPluginManager().registerEvents(landListener, this);
	getServer().getPluginManager().registerEvents(chatListener, this);
	getCommand("secuboid").setExecutor(CommandListener);

	// Register events only if Inventory is active
	if (inventoryConf != null) {
	    inventoryListener = new InventoryListener();
	    getServer().getPluginManager().registerEvents(inventoryListener, this);
	}

	// Register events only if Fly and Creative is active
	if (conf.isFlyAndCreative()) {
	    flyCreativeListener = new FlyCreativeListener();
	    getServer().getPluginManager().registerEvents(flyCreativeListener, this);
	}

	log.write(getLanguage().getMessage("ENABLE"));
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
	if (conf.useEconomy() && dependPlugin.getEconomy() != null) {
	    playerMoney = new PlayerMoney();
	} else {
	    playerMoney = null;
	}
	log.setDebug(conf.isDebug());
	language.reloadConfig();
	lands = new Lands();
	storageThread.stopNextRun();
	storageThread = new StorageThread();
	storageThread.loadAllAndStart();
	approveNotif.stopNextRun();
	approveNotif.runApproveNotifLater();
    }

    /* (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    /**
     *
     */
    @Override
    public void onDisable() {

	log.write(getLanguage().getMessage("DISABLE"));

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
     * Inventory Listener
     *
     * @return the inventory listener
     */
    public InventoryListener getInventoryListener() {

	return inventoryListener;
    }

    /**
     * fly and creative Listener
     *
     * @return the fly and creative listener
     */
    public FlyCreativeListener getFlyCreativeListener() {

	return flyCreativeListener;
    }

    /**
     *
     * @return
     */
    public PlayerStaticConfig getPlayerConf() {

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
     * Get log.
     *
     * @return the log
     */
    public Log getLog() {

	return log;
    }

    /**
     *
     * @return
     */
    public PermissionsFlags getPermissionsFlags() {

	return PermissionsFlags;
    }

    /**
     *
     * @return
     */
    public Lands getLands() {

	return lands;
    }

    /**
     *
     * @return
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
}
