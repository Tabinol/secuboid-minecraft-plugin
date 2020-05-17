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
package me.tabinol.secuboid.config;

import java.util.EnumSet;
import java.util.TreeSet;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.PermissionType;

/**
 * The Class Config.
 */
public final class Config {

    /**
     * The Constant NEWLINE.
     */
    public static final String NEWLINE = System.lineSeparator();

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
    public Config(final Secuboid secuboid) {

        this.secuboid = secuboid;
        secuboid.saveDefaultConfig();

        // Get Bukkit Config for this plugin, not this class!!!
        config = secuboid.getConfig();

        reloadConfig();
    }

    // **********************************
    // ****** CONFIGURATION SECTION *****
    // **********************************

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
     * The Storage.
     */
    private String storage;

    /**
     * Gets the storage.
     *
     * @return the storage
     */
    public String getStorage() {
        return storage;
    }

    private String mySqlHostName;

    public String mySqlHostName() {
        return mySqlHostName;
    }

    private int mySqlPort;

    public int mySqlPort() {
        return mySqlPort;
    }

    private String mySqlDatabase;

    public String mySqlDatabase() {
        return mySqlDatabase;
    }

    private String mySqlUser;

    public String mySqlUser() {
        return mySqlUser;
    }

    private String mySqlPassword;

    public String mySqlPassword() {
        return mySqlPassword;
    }

    private String mySqlPrefix;

    public String mySqlPrefix() {
        return mySqlPrefix;
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
     * The default radius.
     */
    private int defaultRadius;

    /**
     * Gets the default radius.
     *
     * @return the default radius
     */
    public int getDefaultRadius() {
        return defaultRadius;
    }

    /**
     * The maximum bottom.
     */
    private int maxBottom;

    /**
     * Gets the maximum bottom.
     *
     * @return the maximum bottom
     */
    public int getMaxBottom() {
        return maxBottom;
    }

    /**
     * The maximum top.
     */
    private int maxTop;

    /**
     * Gets the maximum top.
     *
     * @return the maximum top
     */
    public int getMaxTop() {
        return maxTop;
    }

    private EnumSet<Material> defaultNonSelectedMaterials;

    public EnumSet<Material> getDefaultNonSelectedMaterials() {
        return defaultNonSelectedMaterials;
    }

    /**
     * The maximum radius.
     */
    private int maxRadius;

    /**
     * Gets the maximum radius.
     *
     * @return the maximum radius
     */
    public int getMaxRadius() {
        return maxRadius;
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

    public boolean isFlyAndCreative() {
        return flyAndCreative;
    }

    /**
     * Fly and Creative specific configuration
     */
    private EnumSet<GameMode> ignoredGameMode;

    public EnumSet<GameMode> getIgnoredGameMode() {
        return ignoredGameMode;
    }

    private boolean creativeNoDrop;

    public boolean isCreativeNoDrop() {
        return creativeNoDrop;
    }

    private boolean creativeNoOpenChest;

    public boolean isCreativeNoOpenChest() {
        return creativeNoOpenChest;
    }

    private boolean creativeNoBuildOutside;

    public boolean isCreativeNoBuildOutside() {
        return creativeNoBuildOutside;
    }

    private EnumSet<Material> creativeBannedItems;

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

        config.addDefault("General.worlds", new String[] { "world", "world_nether", "world_the_end" });
        lang = config.getString("General.Lang", "english");
        storage = config.getString("General.Storage", "flat");
        mySqlHostName = config.getString("General.MySQL.HostName", "localhost");
        mySqlPort = config.getInt("General.MySQL.Port", 3306);
        mySqlDatabase = config.getString("General.MySQL.Database", "secuboid");
        mySqlUser = config.getString("General.MySQL.User", "secuboid");
        mySqlPassword = config.getString("General.MySQL.Password", "mypass");
        mySqlPrefix = config.getString("General.MySQL.Prefix", "secuboid_");

        useEconomy = config.getBoolean("General.UseEconomy", false);
        multipleInventories = config.getBoolean("General.MultipleInventories", false);

        final String infoItemS = config.getString("General.InfoItem", "BONE");
        try {
            infoItem = Material.valueOf(infoItemS.toUpperCase());
        } catch (final IllegalArgumentException ex) {
            secuboid.getLogger().warning("Error in config.yml on General.InfoItem : No " + infoItemS
                    + " item found in Bukkit! Using default.");
            infoItem = Material.BONE;
        }

        final String selectItemS = config.getString("General.SelectItem", "ROTTEN_FLESH");
        try {
            selectItem = Material.valueOf(selectItemS.toUpperCase());
        } catch (final IllegalArgumentException ex) {
            secuboid.getLogger().warning("Error in config.yml on General.SelectItem : No " + selectItemS
                    + " item found in Bukkit! Using default.");
            selectItem = Material.ROTTEN_FLESH;
        }

        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType
                    .valueOf(config.getString("Lands.AllowCollision", "approve").toUpperCase());
        } catch (final NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        isLandChat = config.getBoolean("Lands.LandChat", true);
        isSpectatorIsVanish = config.getBoolean("Lands.SpectatorIsVanish", true);
        approveNotifyTime = config.getLong("Lands.ApproveNotifyTime", 24002);
        selectAutoCancel = config.getLong("Lands.SelectAutoCancel", 12000);
        defaultXSize = config.getInt("Lands.DefaultXSize", 10);
        defaultZSize = config.getInt("Lands.DefaultZSize", 10);
        defaultBottom = config.getInt("Lands.DefaultBottom", 0);
        defaultTop = config.getInt("Lands.DefaultTop", 255);
        defaultRadius = config.getInt("Lands.DefaultRadius", 10);
        maxBottom = config.getInt("Lands.MaxBottom", 0);
        maxTop = config.getInt("Lands.MaxTop", 255);
        maxRadius = config.getInt("Lands.MaxRadius", 10);

        // Default non selected materials
        config.addDefault("Lands.DefaultNonSelectedMaterials", new String[] { "DIAMOND", "GOLD_INGOT" });
        defaultNonSelectedMaterials = EnumSet.noneOf(Material.class);
        for (final String value : config.getStringList("Lands.DefaultNonSelectedMaterials")) {
            try {
                defaultNonSelectedMaterials.add(Material.valueOf(value.toUpperCase()));
            } catch (final IllegalArgumentException ex) {
                secuboid.getLogger().warning("Error in config.yml on Lands.DefaultNonSelectedMaterials : No " + value
                        + " item found in Bukkit!");
            }
        }

        maxAreaPerLand = config.getInt("Lands.Areas.MaxAreaPerLand", 3);
        maxLandPerPlayer = config.getInt("Lands.MaxLandPerPlayer", 5);
        overrideExplosions = config.getBoolean("General.OverrideExplosions", true);

        config.addDefault("Lands.OwnerCanSet.Flags", new String[] { "MESSAGE_ENTER", "MESSAGE_EXIT" });
        ownerConfigFlag = new TreeSet<FlagType>();
        for (final String value : config.getStringList("Lands.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(secuboid.getPermissionsFlags().getFlagTypeNoValid(value.toUpperCase()));
        }
        config.addDefault("Lands.OwnerCanSet.Permissions", new String[] { "BUILD", "OPEN", "USE" });
        ownerConfigPerm = new TreeSet<PermissionType>();
        for (final String value : config.getStringList("Lands.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(secuboid.getPermissionsFlags().getPermissionTypeNoValid(value.toUpperCase()));
        }

        // Fly and creative
        flyAndCreative = config.getBoolean("General.FlyAndCreative", false);

        config.addDefault("FlyCreativeListener.IgnoredGameMode", new String[] { "ADVENTURE", "SPECTATOR" });
        ignoredGameMode = EnumSet.noneOf(GameMode.class);
        for (final String value : config.getStringList("FlyCreativeListener.IgnoredGameMode")) {
            try {
                ignoredGameMode.add(GameMode.valueOf(value.toUpperCase()));
            } catch (final IllegalArgumentException ex) {
                secuboid.getLogger().warning(
                        "Error in config.yml on FlyCreativeListener.IgnoredGameMode : No " + value + " game mode!");
            }
        }

        creativeNoDrop = config.getBoolean("FlyCreativeListener.Creative.NoDrop", true);
        creativeNoOpenChest = config.getBoolean("FlyCreativeListener.Creative.NoOpenChest", true);
        creativeNoBuildOutside = config.getBoolean("FlyCreativeListener.Creative.NoBuildOutside", true);

        config.addDefault("FlyCreativeListener.Creative.BannedItems", new String[] { "DIAMOND", "GOLD_INGOT" });
        creativeBannedItems = EnumSet.noneOf(Material.class);
        for (final String value : config.getStringList("FlyCreativeListener.Creative.BannedItems")) {
            try {
                creativeBannedItems.add(Material.valueOf(value.toUpperCase()));
            } catch (final IllegalArgumentException ex) {
                secuboid.getLogger().warning("Error in config.yml on FlyCreativeListener.Creative.BannedItems : No "
                        + value + "item found in Bukkit!");
            }
        }

        // Add types
        for (final String typeName : config.getStringList("Lands.Types.List")) {
            secuboid.getTypes().addOrGetType(typeName);
        }
        typeAdminMode = secuboid.getTypes().addOrGetType(getStringOrNull("Lands.Types.OnCreate.AdminMode", "admin"));
        typeNoneAdminMode = secuboid.getTypes()
                .addOrGetType(getStringOrNull("Lands.Types.OnCreate.NoneAdminMode", "player"));
    }

    private String getStringOrNull(final String path, final String defaultSt) {

        String result = config.getString(path, defaultSt);
        if (result.equalsIgnoreCase("-null-")) {
            result = null;
        }

        return result;
    }
}
