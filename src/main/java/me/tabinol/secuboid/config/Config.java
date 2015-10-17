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
package me.tabinol.secuboid.config;

import java.util.TreeSet;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.parameters.FlagType;
import me.tabinol.secuboid.parameters.PermissionType;
import me.tabinol.secuboidapi.SecuboidAPI;
import me.tabinol.secuboidapi.lands.types.IType;

import org.bukkit.configuration.file.FileConfiguration;


// TODO: Auto-generated Javadoc
/**
 * The Class Config.
 */
public class Config {

    // Global
    /** The Constant NEWLINE. */
	public static final String NEWLINE = "\n";
    //public static final String NEWLINE = System.getProperty("line.separator");
    
    /** The Constant GLOBAL. */
    public static final String GLOBAL = "_global_";
    
    /** The this plugin. */
    private final Secuboid thisPlugin;
    
    /** The config. */
    private FileConfiguration config;
    // Configuration
    /** The debug. */
    private boolean debug;
    
    /**
     * Checks if is debug.
     *
     * @return true, if is debug
     */
    public boolean isDebug() { return debug; }
    
    /** The lang. */
    private String lang;
    
    /**
     * Gets the lang.
     *
     * @return the lang
     */
    public String getLang() { return lang; }
    
    /** The use economy. */
    private boolean useEconomy;
    
    /**
     * Use economy.
     *
     * @return true, if successful
     */
    public boolean useEconomy() { return useEconomy; }
    
    /** The info item. */
    private int infoItem;
    
    /**
     * Gets the info item.
     *
     * @return the info item
     */
    public int getInfoItem() { return infoItem; }
    
    /** The select item. */
    private int selectItem;
    
    /**
     * Gets the select item.
     *
     * @return the select item
     */
    public int getSelectItem() { return selectItem; }
    
    /**
     * The Enum AllowCollisionType.
     */
    public enum AllowCollisionType {

        /** The true. */
        TRUE,
        
        /** The approve. */
        APPROVE,
        
        /** The false. */
        FALSE;
    }
    
    /** The allow collision. */
    private AllowCollisionType allowCollision;
    
    /**
     * Gets the allow collision.
     *
     * @return the allow collision
     */
    public AllowCollisionType getAllowCollision() { return allowCollision; }
    
    /** The land chat. */
    private boolean isLandChat;
    
    /**
     * Gets if land chat is activated.
     *
     * @return the land chat
     */
    public boolean isLandChat() { return isLandChat; }

    /** The is spectator is vanish. */
    private boolean isSpectatorIsVanish;
    
    /**
     * Checks if is spectator is vanish.
     *
     * @return true, if is spectator is vanish
     */
    public boolean isSpectatorIsVanish() { return isSpectatorIsVanish; }
    
    /** The approve notify time. */
    private long approveNotifyTime;
    
    /**
     * Gets the approve notify time.
     *
     * @return the approve notify time
     */
    public long getApproveNotifyTime() { return approveNotifyTime; }
    
    /** The select auto cancel. */
    private long selectAutoCancel;
    
    /**
     * Gets the select auto cancel.
     *
     * @return the select auto cancel
     */
    public long getSelectAutoCancel() { return selectAutoCancel; }
    
    /** The max visual select. */
    private int maxVisualSelect;
    
    /**
     * Gets the max visual select.
     *
     * @return the max visual select
     */
    public int getMaxVisualSelect() { return maxVisualSelect; }
    
    /** The max visual select from player. */
    private int maxVisualSelectFromPlayer;
    
    /**
     * Gets the max visual select from player.
     *
     * @return the max visual select from player
     */
    public int getMaxVisualSelectFromPlayer() { return maxVisualSelectFromPlayer; }

    /** The max area per land. */
    private int maxAreaPerLand;
    
    /**
     * Gets the max area per land.
     *
     * @return the max area per land
     */
    public int getMaxAreaPerLand() { return maxAreaPerLand; }
    
    /** The max land per player. */
    private int maxLandPerPlayer;
    
    /**
     * Gets the max land per player.
     *
     * @return the max land per player
     */
    public int getMaxLandPerPlayer() { return maxLandPerPlayer; }
    
    /** The default x size. */
    private int defaultXSize;
    
    /**
     * Gets the default x size.
     *
     * @return the default x size
     */
    public int getDefaultXSize() { return defaultXSize; }
  
    /** The default z size. */
    private int defaultZSize;
    
    /**
     * Gets the default z size.
     *
     * @return the default z size
     */
    public int getDefaultZSize() { return defaultZSize; }
  
    /** The default bottom. */
    private int defaultBottom;
    
    /**
     * Gets the default bottom.
     *
     * @return the default bottom
     */
    public int getDefaultBottom() { return defaultBottom; }
  
    /** The default top. */
    private int defaultTop;
    
    /**
     * Gets the default top.
     *
     * @return the default top
     */
    public int getDefaultTop() { return defaultTop; }

    
    /** The beacon light. */
    private boolean beaconLight;
    
    /**
     * Checks if is beacon light.
     *
     * @return true, if is beacon light
     */
    public boolean isBeaconLight() { return beaconLight; }
    
    /** The override explosions. */
    private boolean overrideExplosions;
    
    /**
     * Checks if is override explosions.
     *
     * @return true, if is override explosions
     */
    public boolean isOverrideExplosions() { return overrideExplosions; }
    
    /** The owner config flag. */
    private TreeSet<FlagType> ownerConfigFlag; // Flags a owner can set
    
    /**
     * Gets the owner config flag.
     *
     * @return the owner config flag
     */
    public TreeSet<FlagType> getOwnerConfigFlag() { return ownerConfigFlag; }
    
    /** The owner config perm. */
    private TreeSet<PermissionType> ownerConfigPerm; // Permissions a owner can set
    
    /**
     * Gets the owner config perm.
     *
     * @return the owner config perm
     */
    public TreeSet<PermissionType> getOwnerConfigPerm() { return ownerConfigPerm; }

    /** The type admin mod. */
    private IType typeAdminMod;
    
    /**
     * Gets the type admin mod.
     *
     * @return the type admin mod
     */
    public IType getTypeAdminMod() { return typeAdminMod; } 
    
    /** The type none admin mod. */
    private IType typeNoneAdminMod;
    
    /**
     * Gets the type none admin mod.
     *
     * @return the type none admin mod
     */
    public IType getTypeNoneAdminMod() { return typeNoneAdminMod; } 

    /**
     * Instantiates a new config.
     */
    public Config() {

        thisPlugin = Secuboid.getThisPlugin();
        thisPlugin.saveDefaultConfig();

        // Get Bukkit Config for this plugin, not this class!!!
        config = thisPlugin.getConfig();

        reloadConfig();
    }

    /**
     * Reload config.
     */
    public final void reloadConfig() {

        thisPlugin.reloadConfig();
        config = thisPlugin.getConfig();
        getConfig();
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    private void getConfig() {

        debug = config.getBoolean("general.debug", false);
        config.addDefault("general.worlds", new String[] {"world", "world_nether", "world_the_end"});
        lang = config.getString("general.lang", "english");
        useEconomy = config.getBoolean("general.UseEconomy", false);
        infoItem = config.getInt("general.InfoItem", 352);
        selectItem = config.getInt("general.SelectItem", 367);
        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType.valueOf(config.getString("land.AllowCollision", "approve").toUpperCase());
        } catch (NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        isLandChat = config.getBoolean("land.LandChat", true);
        isSpectatorIsVanish = config.getBoolean("land.SpectatorIsVanish", true);
        approveNotifyTime = config.getLong("land.ApproveNotifyTime", 24002);
        selectAutoCancel = config.getLong("land.SelectAutoCancel", 12000);
        maxVisualSelect = config.getInt("land.MaxVisualSelect", 256);
        maxVisualSelectFromPlayer = config.getInt("land.MaxVisualSelectFromPlayer", 128);
        defaultXSize = config.getInt("land.defaultXSize", 10);
        defaultZSize = config.getInt("land.defaultZSize", 10);
        defaultBottom = config.getInt("land.defaultBottom", 0);
        defaultTop = config.getInt("land.defaultTop", 255);
        maxAreaPerLand = config.getInt("land.area.MaxAreaPerLand", 3);
        maxLandPerPlayer = config.getInt("land.MaxLandPerPlayer", 5);
        beaconLight = config.getBoolean("land.BeaconLight", false);
        overrideExplosions = config.getBoolean("general.OverrideExplosions", true);

        config.addDefault("land.OwnerCanSet.Flags", new String[] {"MESSAGE_JOIN", "MESSAGE_QUIT"});
        ownerConfigFlag = new TreeSet<FlagType>();
        for (String value : config.getStringList("land.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(Secuboid.getThisPlugin().iParameters().getFlagTypeNoValid(value.toUpperCase()));
        }
        config.addDefault("land.OwnerCanSet.Permissions", new String[] {"BUILD", "OPEN", "USE"});
        ownerConfigPerm = new TreeSet<PermissionType>();
        for (String value : config.getStringList("land.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(Secuboid.getThisPlugin().iParameters().getPermissionTypeNoValid(value.toUpperCase()));
        }
        
        // Add types
        for(String typeName : config.getStringList("land.Types.List")) {
        	SecuboidAPI.iTypes().addOrGetType(typeName);
        }
        typeAdminMod = SecuboidAPI.iTypes().addOrGetType(getStringOrNull("land.Types.OnCreate.AdminMod", "admin"));
        typeNoneAdminMod = SecuboidAPI.iTypes().addOrGetType(getStringOrNull("land.Types.OnCreate.NoneAdminMod", "player"));
    }
    
    private String getStringOrNull(String path, String defaultSt) {
    	
    	String result = config.getString(path, defaultSt);
    	if(result.equalsIgnoreCase("-null-")) {
    		result = null;
    	}
    	
    	return result;
    }
}
