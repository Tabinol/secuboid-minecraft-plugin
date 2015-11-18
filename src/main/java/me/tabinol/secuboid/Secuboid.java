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

import java.io.IOException;

import me.tabinol.secuboid.commands.OnCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.DependPlugin;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.config.players.PlayerStaticConfig;
import me.tabinol.secuboid.economy.EcoScheduler;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.approve.ApproveNotif;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.listeners.*;
import me.tabinol.secuboid.parameters.Parameters;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playerscache.PlayersCache;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.utilities.Lang;
import me.tabinol.secuboid.utilities.Log;
import me.tabinol.secuboid.utilities.MavenAppProperties;
import me.tabinol.secuboidapi.ApiSecuboidSta;
import me.tabinol.secuboidapi.ApiSecuboid;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Class Secuboid.
 */
public class Secuboid extends JavaPlugin implements ApiSecuboid {

    /**  The Economy schedule interval. */
    public static final int ECO_SCHEDULE_INTERVAL = 20 * 60 * 5;
    
    /** The maven app properties. */
    private static MavenAppProperties mavenAppProperties;

    /** The this plugin. */
    private static Secuboid thisPlugin;

    /** The types */
    protected static Types types;
    
    /** The lands. */
    protected static Lands lands;
    
    /** The parameters. */
    protected static Parameters parameters;
    
    /** The player conf. */
    protected PlayerStaticConfig playerConf;

    /** The Command listener. */
    private OnCommand CommandListener;
    
    /** The player listener. */
    private PlayerListener playerListener;
    
    /** The player listener 18. */
    private PlayerListener18 playerListener18;

    /** The player listener. */
    private PvpListener pvpListener;

    /** The world listener. */
    private WorldListener worldListener;
    
    /** The land listener. */
    private LandListener landListener;
    
    /** The chat listener. */
    private ChatListener chatListener;

    /** The inventory listener */
    private InventoryListener inventoryListener = null;

    /** The inventory listener */
    private FlyCreativeListener flyCreativeListener = null;

    /**  The economy scheduler. */
    private EcoScheduler ecoScheduler;
    
    /** The approve notif. */
    private ApproveNotif approveNotif;
    
    /** The storage thread. */
    private StorageThread storageThread = null;
    
    /** The log. */
    private Log log;
    
    /** The conf. */
    private Config conf;

    private InventoryConfig inventoryConf = null;
    
    /** The language. */
    private Lang language;
    
    /** The depend plugin. */
    private DependPlugin dependPlugin;
    
    /** The player money. */
    private PlayerMoney playerMoney;
    
    /** The players cache. */
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

    /* (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        mavenAppProperties = new MavenAppProperties();
        mavenAppProperties.loadProperties();
        // Static access to «this» Secuboid
        thisPlugin = this;
        BKVersion.initVersion();
        ApiSecuboidSta.initSecuboidPluginAccess();
        parameters = new Parameters();
        types = new Types();
        conf = new Config();

        // For inventory config
        if(conf.isMultipleInventories()) {
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
        storageThread.loadAllAndStart();
        worldListener = new WorldListener();
        playerListener = new PlayerListener();
        if(BKVersion.isPlayerInteractAtEntityEventExist()) {
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
        if(BKVersion.isPlayerInteractAtEntityEventExist()) {
            getServer().getPluginManager().registerEvents(playerListener18, this);
        }
        getServer().getPluginManager().registerEvents(pvpListener, this);
        getServer().getPluginManager().registerEvents(landListener, this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getCommand("secuboid").setExecutor(CommandListener);

        // Register events only if Inventory is active
        if(inventoryConf != null) {
            inventoryListener = new InventoryListener();
            getServer().getPluginManager().registerEvents(inventoryListener, this);
        }

        // Register events only if Fly and Creative is active
        if(conf.isFlyAndCreative()) {
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
        if(inventoryConf != null) {
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
        storageThread= new StorageThread();
        storageThread.loadAllAndStart();
        approveNotif.stopNextRun();
        approveNotif.runApproveNotifLater();
    }

    /* (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {

        log.write(getLanguage().getMessage("DISABLE"));

        // Save all inventories
        if(inventoryListener != null) {
            inventoryListener.removeAndSave();
        }

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
     * @return the config
     */
    public InventoryConfig getInventoryConf() {

        return inventoryConf;
    }

    /**
     * Inventory Listener
     * @return the inventory listener
     */
    public InventoryListener getInventoryListener() {

        return inventoryListener;
    }

    /**
     * fly and creative Listener
     * @return the fly and creative listener
     */
    public FlyCreativeListener getFlyCreativeListener() {

        return flyCreativeListener;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.ApiSecuboid#getPlayerConf()
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

    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.ApiSecuboid#getParameters()
     */
    public Parameters getParameters() {
        
        return parameters;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.ApiSecuboid#getLands()
     */
    public Lands getLands() {
        
        return lands;
    }
    
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
    
    /*
     * Creators to forward
     */
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.ApiSecuboid#createPlayerContainer(me.tabinol.secuboidapi.lands.ApiLand, me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType, java.lang.String)
     */
    public PlayerContainer createPlayerContainer(ApiLand land,
            ApiPlayerContainerType pct, String name) {
        
        return PlayerContainer.create(land, pct, name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.ApiSecuboid#createCuboidArea(java.lang.String, int, int, int, int, int, int)
     */
    public ApiCuboidArea createCuboidArea(String worldName, int x1, int y1,
            int z1, int x2, int y2, int z2) {
        
        return new CuboidArea(worldName, x1, y1, z1, x2, y2, z2);
    }
    
}
