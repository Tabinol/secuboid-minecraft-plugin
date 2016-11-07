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

import java.util.EnumSet;
import java.util.TreeSet;
import java.util.logging.Level;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The Class Config.
 */
public class Config {

    /**
     * The Constant NEWLINE.
     */
    public static final String NEWLINE = "\n";
    //public static final String NEWLINE = System.getProperty("line.separator");

    /**
     * The Constant GLOBAL.
     */
    public static final String GLOBAL = "_global_";

    /**
     * The this plugin.
     */
    private final Secuboid secuboid;

    /**
     * The config.
     */
    private FileConfiguration config;

    /**
     * Instantiates a new config.
     *
     * @param secuboid secuboid instance
     */
    public Config(Secuboid secuboid) {

	this.secuboid = secuboid;
	secuboid.saveDefaultConfig();

	// Get Bukkit Config for this plugin, not this class!!!
	config = secuboid.getConfig();

	reloadConfig();
    }

    /*
    * ***** CONFIGURATION SECTION *****
     */
    /**
     * The debug.
     */
    private boolean debug;

    /**
     * Checks if is debug.
     *
     * @return true, if is debug
     */
    public boolean isDebug() {
	return debug;
    }

    /**
     * The lang.
     */
    private String lang;

    /**
     * Gets the lang.
     *
     * @return the lang
     */
    public String getLang() {
	return lang;
    }

    /**
     * The use economy.
     */
    private boolean useEconomy;

    /**
     * Use economy.
     *
     * @return true, if successful
     */
    public boolean useEconomy() {
	return useEconomy;
    }

    /**
     * For multiple inventories.
     */
    private boolean multipleInventories;

    /**
     * Use multiple inventories.
     *
     * @return true, if successful
     */
    public boolean isMultipleInventories() {
	return multipleInventories;
    }

    /**
     * The info item.
     */
    private Material infoItem;

    /**
     * Gets the info item.
     *
     * @return the info item
     */
    public Material getInfoItem() {
	return infoItem;
    }

    /**
     * The select item.
     */
    private Material selectItem;

    /**
     * Gets the select item.
     *
     * @return the select item
     */
    public Material getSelectItem() {
	return selectItem;
    }

    /**
     * The Enum AllowCollisionType.
     */
    public enum AllowCollisionType {

	/**
	 * The true.
	 */
	TRUE,
	/**
	 * The approve.
	 */
	APPROVE,
	/**
	 * The false.
	 */
	FALSE
    }

    /**
     * The allow collision.
     */
    private AllowCollisionType allowCollision;

    /**
     * Gets the allow collision.
     *
     * @return the allow collision
     */
    public AllowCollisionType getAllowCollision() {
	return allowCollision;
    }

    /**
     * The land chat.
     */
    private boolean isLandChat;

    /**
     * Gets if land chat is activated.
     *
     * @return the land chat
     */
    public boolean isLandChat() {
	return isLandChat;
    }

    /**
     * The is spectator is vanish.
     */
    private boolean isSpectatorIsVanish;

    /**
     * Checks if is spectator is vanish.
     *
     * @return true, if is spectator is vanish
     */
    public boolean isSpectatorIsVanish() {
	return isSpectatorIsVanish;
    }

    /**
     * The approve notify time.
     */
    private long approveNotifyTime;

    /**
     * Gets the approve notify time.
     *
     * @return the approve notify time
     */
    public long getApproveNotifyTime() {
	return approveNotifyTime;
    }

    /**
     * The select auto cancel.
     */
    private long selectAutoCancel;

    /**
     * Gets the select auto cancel.
     *
     * @return the select auto cancel
     */
    public long getSelectAutoCancel() {
	return selectAutoCancel;
    }

    /**
     * The max area per Lands.
     */
    private int maxAreaPerLand;

    /**
     * Gets the max area per Lands.
     *
     * @return the max area per land
     */
    public int getMaxAreaPerLand() {
	return maxAreaPerLand;
    }

    /**
     * The max land per player.
     */
    private int maxLandPerPlayer;

    /**
     * Gets the max land per player.
     *
     * @return the max land per player
     */
    public int getMaxLandPerPlayer() {
	return maxLandPerPlayer;
    }

    /**
     * The default x size.
     */
    private int defaultXSize;

    /**
     * Gets the default x size.
     *
     * @return the default x size
     */
    public int getDefaultXSize() {
	return defaultXSize;
    }

    /**
     * The default z size.
     */
    private int defaultZSize;

    /**
     * Gets the default z size.
     *
     * @return the default z size
     */
    public int getDefaultZSize() {
	return defaultZSize;
    }

    /**
     * The default bottom.
     */
    private int defaultBottom;

    /**
     * Gets the default bottom.
     *
     * @return the default bottom
     */
    public int getDefaultBottom() {
	return defaultBottom;
    }

    /**
     * The default top.
     */
    private int defaultTop;

    /**
     * Gets the default top.
     *
     * @return the default top
     */
    public int getDefaultTop() {
	return defaultTop;
    }

    /**
     * The override explosions.
     */
    private boolean overrideExplosions;

    /**
     * Checks if is override explosions.
     *
     * @return true, if is override explosions
     */
    public boolean isOverrideExplosions() {
	return overrideExplosions;
    }

    /**
     * The owner config flag.
     */
    private TreeSet<FlagType> ownerConfigFlag; // Flags a owner can set

    /**
     * Gets the owner config flag.
     *
     * @return the owner config flag
     */
    public TreeSet<FlagType> getOwnerConfigFlag() {
	return ownerConfigFlag;
    }

    /**
     * The owner config perm.
     */
    private TreeSet<PermissionType> ownerConfigPerm; // Permissions a owner can set

    /**
     * Gets the owner config perm.
     *
     * @return the owner config perm
     */
    public TreeSet<PermissionType> getOwnerConfigPerm() {
	return ownerConfigPerm;
    }

    /**
     * The type admin mod.
     */
    private Type typeAdminMode;

    /**
     * Gets the type admin mod.
     *
     * @return the type admin mod
     */
    public Type getTypeAdminMode() {
	return typeAdminMode;
    }

    /**
     * The type none admin mod.
     */
    private Type typeNoneAdminMode;

    /**
     * Gets the type none admin mod.
     *
     * @return the type none admin mod
     */
    public Type getTypeNoneAdminMode() {
	return typeNoneAdminMode;
    }

    private boolean flyAndCreative;

    /**
     *
     * @return
     */
    public boolean isFlyAndCreative() {
	return flyAndCreative;
    }

    // Fly and Creative specific configuration
    private EnumSet<GameMode> ignoredGameMode;

    /**
     *
     * @return
     */
    public EnumSet<GameMode> getIgnoredGameMode() {
	return ignoredGameMode;
    }

    private boolean creativeNoDrop;

    /**
     *
     * @return
     */
    public boolean isCreativeNoDrop() {
	return creativeNoDrop;
    }

    private boolean creativeNoOpenChest;

    /**
     *
     * @return
     */
    public boolean isCreativeNoOpenChest() {
	return creativeNoOpenChest;
    }

    private boolean creativeNoBuildOutside;

    /**
     *
     * @return
     */
    public boolean isCreativeNoBuildOutside() {
	return creativeNoBuildOutside;
    }

    private EnumSet<Material> creativeBannedItems;

    /**
     *
     * @return
     */
    public EnumSet<Material> getCreativeBannedItems() {
	return creativeBannedItems;
    }

    /**
     * Reload config.
     */
    public final void reloadConfig() {

	secuboid.reloadConfig();
	config = secuboid.getConfig();
	getConfig();
    }

    /**
     * Gets the config.
     */
    private void getConfig() {

	debug = config.getBoolean("General.Debug", false);
	config.addDefault("General.worlds", new String[]{"world", "world_nether", "world_the_end"});
	lang = config.getString("General.Lang", "english");
	useEconomy = config.getBoolean("General.UseEconomy", false);
	multipleInventories = config.getBoolean("General.MultipleInventories", false);

	String infoItemS = config.getString("General.InfoItem", "BONE");
	try {
	    infoItem = Material.valueOf(infoItemS.toUpperCase());
	} catch (IllegalArgumentException ex) {
	    secuboid.getLogger().log(Level.WARNING, "Error in config.yml on General.InfoItem : No {0} item found in Bukkit! Using default.", infoItemS);
	    infoItem = Material.BONE;
	}

	String selectItemS = config.getString("General.SelectItem", "ROTTEN_FLESH");
	try {
	    selectItem = Material.valueOf(selectItemS.toUpperCase());
	} catch (IllegalArgumentException ex) {
	    secuboid.getLogger().log(Level.WARNING, "Error in config.yml on General.SelectItem : No {0} item found in Bukkit! Using default.", selectItemS);
	    selectItem = Material.ROTTEN_FLESH;
	}

	// Remove error if the parameter is not here (AllowCollision)
	try {
	    allowCollision = AllowCollisionType.valueOf(config.getString("Lands.AllowCollision", "approve").toUpperCase());
	} catch (NullPointerException ex) {
	    allowCollision = AllowCollisionType.APPROVE;
	}
	isLandChat = config.getBoolean("Lands.LandChat", true);
	isSpectatorIsVanish = config.getBoolean("Lands.SpectatorIsVanish", true);
	approveNotifyTime = config.getLong("Lands.ApproveNotifyTime", 24002);
	selectAutoCancel = config.getLong("Lands.SelectAutoCancel", 12000);
	defaultXSize = config.getInt("Lands.defaultXSize", 10);
	defaultZSize = config.getInt("Lands.defaultZSize", 10);
	defaultBottom = config.getInt("Lands.defaultBottom", 0);
	defaultTop = config.getInt("Lands.defaultTop", 255);
	maxAreaPerLand = config.getInt("Lands.Areas.MaxAreaPerLand", 3);
	maxLandPerPlayer = config.getInt("Lands.MaxLandPerPlayer", 5);
	overrideExplosions = config.getBoolean("General.OverrideExplosions", true);

	config.addDefault("Lands.OwnerCanSet.Flags", new String[]{"MESSAGE_JOIN", "MESSAGE_QUIT"});
	ownerConfigFlag = new TreeSet<FlagType>();
	for (String value : config.getStringList("Lands.OwnerCanSet.Flags")) {
	    ownerConfigFlag.add(secuboid.getPermissionsFlags().getFlagTypeNoValid(value.toUpperCase()));
	}
	config.addDefault("Lands.OwnerCanSet.Permissions", new String[]{"BUILD", "OPEN", "USE"});
	ownerConfigPerm = new TreeSet<PermissionType>();
	for (String value : config.getStringList("Lands.OwnerCanSet.Permissions")) {
	    ownerConfigPerm.add(secuboid.getPermissionsFlags().getPermissionTypeNoValid(value.toUpperCase()));
	}

	// Fly and creative
	flyAndCreative = config.getBoolean("General.flyAndCreative", false);

	config.addDefault("FlyCreativeListener.IgnoredGameMode", new String[]{"ADVENTURE", "SPECTATOR"});
	ignoredGameMode = EnumSet.noneOf(GameMode.class);
	for (String value : config.getStringList("FlyCreativeListener.IgnoredGameMode")) {
	    try {
		ignoredGameMode.add(GameMode.valueOf(value.toUpperCase()));
	    } catch (IllegalArgumentException ex) {
		secuboid.getLogger().log(Level.WARNING, "Error in config.yml on FlyCreativeListener.IgnoredGameMode : No {0} game mode!", value);
	    }
	}

	creativeNoDrop = config.getBoolean("FlyCreativeListener.Creative.NoDrop", true);
	creativeNoOpenChest = config.getBoolean("FlyCreativeListener.Creative.NoOpenChest", true);
	creativeNoBuildOutside = config.getBoolean("FlyCreativeListener.Creative.NoBuildOutside", true);

	config.addDefault("FlyCreativeListener.Creative.BannedItems", new String[]{"DIAMOND", "GOLD_INGOT"});
	creativeBannedItems = EnumSet.noneOf(Material.class);
	for (String value : config.getStringList("FlyCreativeListener.Creative.BannedItems")) {
	    try {
		creativeBannedItems.add(Material.valueOf(value.toUpperCase()));
	    } catch (IllegalArgumentException ex) {
		secuboid.getLogger().log(Level.WARNING, "Error in config.yml on FlyCreativeListener.Creative.BannedItems : No {0} item found in Bukkit!", value);
	    }
	}

	// Add types
	for (String typeName : config.getStringList("Lands.Types.List")) {
	    secuboid.getTypes().addOrGetType(typeName);
	}
	typeAdminMode = secuboid.getTypes().addOrGetType(getStringOrNull("Lands.Types.OnCreate.AdminMode", "admin"));
	typeNoneAdminMode = secuboid.getTypes().addOrGetType(getStringOrNull("Lands.Types.OnCreate.NoneAdminMode", "player"));
    }

    private String getStringOrNull(String path, String defaultSt) {

	String result = config.getString(path, defaultSt);
	if (result.equalsIgnoreCase("-null-")) {
	    result = null;
	}

	return result;
    }
}
