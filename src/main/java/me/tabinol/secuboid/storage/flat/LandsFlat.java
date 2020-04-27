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
package me.tabinol.secuboid.storage.flat;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.FileLoadException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainers;
import me.tabinol.secuboid.utilities.MavenAppProperties;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * LandsFlat
 */
public final class LandsFlat {

    /**
     * The Constant EXT_CONF.
     */
    private static final String EXT_CONF = ".conf";

    private final Secuboid secuboid;

    /**
     * The Constant land file version.
     */
    private final int landVersion;

    /**
     * The lands dir.
     */
    private String landsDir;

    public LandsFlat(final Secuboid secuboid) {
        this.secuboid = secuboid;
        landVersion = MavenAppProperties.getPropertyInt("landVersion", 1);
        createDirFiles();
    }

    /**
     * Creates the dir files.
     */
    private void createDirFiles() {
        landsDir = secuboid.getDataFolder() + "/" + "lands" + "/";
        createDir(landsDir);
    }

    /**
     * Creates the dir.
     *
     * @param dir the dir
     */
    private void createDir(final String dir) {

        final File file = new File(dir);

        if (!file.exists() && !file.mkdir()) {
            secuboid.getLogger().severe("Unable to create directory " + file.getPath() + ".");
        }
    }

    /**
     * Gets the land file.
     *
     * @param land the land
     * @return the land file
     */
    private File getLandFile(final Land land) {
        return new File(landsDir + "/" + land.getUUID() + EXT_CONF);
    }

    void loadLands() {
        final Map<Land, UUID> orphanToParentUUID = new HashMap<>();
        final File[] files = new File(landsDir).listFiles();
        int loadedlands = 0;

        assert files != null;
        if (files.length == 0) {
            // No land yet
            return;
        }

        // Pass 1: load lands
        for (final File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(EXT_CONF)) {
                final Entry<Land, UUID> orphanToParentUUIDEntry;
                try {
                    orphanToParentUUIDEntry = loadLand(file);
                } catch (final RuntimeException e) {
                    secuboid.getLogger().log(Level.SEVERE,
                            String.format("Unable to load the land from file: %s", file.getName()), e);
                    continue;
                }
                if (orphanToParentUUIDEntry != null) {
                    loadedlands++;
                    if (orphanToParentUUIDEntry.getValue() != null) {
                        orphanToParentUUID.put(orphanToParentUUIDEntry.getKey(), orphanToParentUUIDEntry.getValue());
                    }
                }
            }
        }

        // Pass 2: find parents
        secuboid.getLands().setParents(orphanToParentUUID);
        secuboid.getLogger().info(loadedlands + " land(s) loaded.");
    }

    /**
     * Load land.
     *
     * @param file the file
     * @return a map with a land (if success) and the parent (if exists)
     */
    private Entry<Land, UUID> loadLand(final File file) {
        final PlayerContainers playerContainers = secuboid.getPlayerContainers();

        int version;
        ConfLoaderFlat cf = null;
        UUID uuid;
        String landName;
        boolean isApproved = true;
        String type;
        Land land = null;
        final Map<Integer, Area> areas = new TreeMap<>();
        boolean isLandCreated = false;
        PlayerContainer owner;
        String parentUUID;
        final Set<PlayerContainer> residents = new TreeSet<>();
        final Set<PlayerContainer> banneds = new TreeSet<>();
        final Map<PlayerContainer, TreeMap<PermissionType, Permission>> permissions = new TreeMap<>();
        final Set<Flag> flags = new HashSet<>();
        short priority;
        double money;
        final Set<PlayerContainerPlayer> pNotifs = new TreeSet<>();

        // For economy
        boolean forSale;
        Location forSaleSignLoc = null;
        double salePrice = 0;
        boolean forRent;
        Location forRentSignLoc = null;
        double rentPrice = 0;
        int rentRenew = 0;
        boolean rentAutoRenew = false;
        boolean rented = false;
        PlayerContainerPlayer tenant = null;
        long lastPayment = 0;

        try {
            cf = new ConfLoaderFlat(secuboid, file);
            String str;
            version = cf.getVersion();
            uuid = cf.getUUID();
            landName = cf.getName();
            if (version >= 8) {
                cf.readParam();
                isApproved = cf.getValueBoolean();
            }
            cf.readParam();
            type = cf.getValueString();
            cf.readParam();
            final String ownerS = cf.getValueString();

            // create owner (PlayerContainer)
            owner = playerContainers.getPlayerContainerFromFileFormat(ownerS);
            if (owner == null) {
                throw new FileLoadException(secuboid, file.getName(), cf.getLine(), cf.getLineNb(), "Invalid owner.");
            }

            cf.readParam();
            parentUUID = cf.getValueString();

            cf.readParam();

            // Create areas
            while ((str = cf.getNextString()) != null) {
                final String[] multiStr = str.split(":", 2);
                areas.put(Integer.parseInt(multiStr[0]), Area.getFromFileFormat(multiStr[1]));
            }
            if (areas.isEmpty()) {
                throw new FileLoadException(secuboid, file.getName(), cf.getLine(), cf.getLineNb(),
                        "No areas in the list.");
            }

            cf.readParam();

            // Residents
            while ((str = cf.getNextString()) != null) {
                residents.add(playerContainers.getPlayerContainerFromFileFormat(str));
            }
            cf.readParam();

            // Banneds
            while ((str = cf.getNextString()) != null) {
                banneds.add(playerContainers.getPlayerContainerFromFileFormat(str));
            }
            cf.readParam();

            // Create permissions
            while ((str = cf.getNextString()) != null) {
                final String[] multiStr = str.split(":");
                TreeMap<PermissionType, Permission> permPlayer;
                final PlayerContainer pc = playerContainers
                        .getPlayerContainerFromFileFormat(multiStr[0] + ":" + multiStr[1]);
                final PermissionType permType = secuboid.getPermissionsFlags().getPermissionTypeNoValid(multiStr[2]);
                if (!permissions.containsKey(pc)) {
                    permPlayer = new TreeMap<>();
                    permissions.put(pc, permPlayer);
                } else {
                    permPlayer = permissions.get(pc);
                }
                permPlayer.put(permType, secuboid.getPermissionsFlags().newPermission(permType,
                        Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
            }
            cf.readParam();

            // Create flags
            while ((str = cf.getNextString()) != null) {
                flags.add(getFlagFromFileFormat(str));
            }
            cf.readParam();

            // Set Priority
            priority = cf.getValueShort();
            cf.readParam();

            // Money
            money = cf.getValueDouble();
            cf.readParam();

            // Players Notify
            while ((str = cf.getNextString()) != null) {
                pNotifs.add((PlayerContainerPlayer) playerContainers.getPlayerContainerFromFileFormat(str));
            }

            // Economy
            cf.readParam();
            forSale = Boolean.parseBoolean(cf.getValueString());
            if (forSale) {
                cf.readParam();
                forSaleSignLoc = StringChanges.stringToLocation(cf.getValueString());
                cf.readParam();
                salePrice = cf.getValueDouble();
            }
            cf.readParam();
            forRent = Boolean.parseBoolean(cf.getValueString());
            if (forRent) {
                cf.readParam();
                forRentSignLoc = StringChanges.stringToLocation(cf.getValueString());
                cf.readParam();
                rentPrice = cf.getValueDouble();
                cf.readParam();
                rentRenew = cf.getValueInt();
                cf.readParam();
                rentAutoRenew = Boolean.parseBoolean(cf.getValueString());
                cf.readParam();
                rented = Boolean.parseBoolean(cf.getValueString());
                if (rented) {
                    cf.readParam();
                    tenant = (PlayerContainerPlayer) playerContainers
                            .getPlayerContainerFromFileFormat(cf.getValueString());
                    cf.readParam();
                    lastPayment = cf.getValueLong();
                }
            }

            cf.close();

            // Catch errors here
        } catch (final NullPointerException ex) {
            try {
                throw new FileLoadException(secuboid, file.getName(), cf != null ? cf.getLine() : "-NOT FOUND-",
                        cf != null ? cf.getLineNb() : 0, "Problem with parameter.");
            } catch (final FileLoadException ex2) {
                // Catch load
                return null;
            }
        } catch (final FileLoadException ex) {
            // Catch load
            return null;
        }

        // Create land
        for (final Map.Entry<Integer, Area> entry : areas.entrySet()) {
            if (!isLandCreated) {
                try {
                    land = secuboid.getLands().createLand(landName, isApproved, owner, entry.getValue(), null,
                            entry.getKey(), uuid, secuboid.getTypes().addOrGetType(type));
                } catch (final SecuboidLandException ex) {
                    secuboid.getLogger().severe("Error on loading land " + landName + ":" + ex.getLocalizedMessage());
                    return null;
                }
                isLandCreated = true;
            } else {
                if (land == null) {
                    secuboid.getLogger().severe("Error: Land not created: " + landName);
                    return null;
                }
                land.addArea(entry.getValue(), entry.getKey());
            }
        }

        if (land == null) {
            secuboid.getLogger().severe("Error: Land not created: " + landName);
            return null;
        }

        // Load land params form memory
        for (final PlayerContainer resident : residents) {
            land.addResident(resident);
        }
        for (final PlayerContainer banned : banneds) {
            land.addBanned(banned);
        }
        for (final Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> entry : permissions.entrySet()) {
            for (final Map.Entry<PermissionType, Permission> entryP : entry.getValue().entrySet()) {
                land.getPermissionsFlags().addPermission(entry.getKey(), entryP.getValue());
            }
        }
        for (final Flag flag : flags) {
            land.getPermissionsFlags().addFlag(flag);
        }
        land.setPriority(priority);
        land.addMoney(money);
        for (final PlayerContainerPlayer pNotif : pNotifs) {
            land.addPlayerNotify(pNotif);
        }

        // Economy add
        if (forSale) {
            land.setForSale(true, salePrice, forSaleSignLoc);
        }
        if (forRent) {
            land.setForRent(rentPrice, rentRenew, rentAutoRenew, forRentSignLoc);
            if (rented) {
                land.setRented(tenant);
                land.setLastPaymentTime(lastPayment);
            }
        }

        return new AbstractMap.SimpleEntry<Land, UUID>(land, parentUUID != null ? UUID.fromString(parentUUID) : null);
    }

    void saveLand(final Land land) {
        try {
            List<String> strs;

            if (secuboid.getStorageThread().isInLoad()) {
                return;
            }

            final ConfBuilderFlat cb = new ConfBuilderFlat(land.getName(), land.getUUID(), getLandFile(land),
                    landVersion);
            cb.writeParam("IsApproved", land.isApproved());
            cb.writeParam("Type", land.getType() != null ? land.getType().getName() : null);
            cb.writeParam("Owner", land.getOwner().toFileFormat());

            // Parent
            if (land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {
                cb.writeParam("Parent", land.getParent().getUUID().toString());
            }

            // CuboidAreas
            strs = new ArrayList<>();
            for (final int index : land.getAreasKey()) {
                strs.add(index + ":" + land.getArea(index).toFileFormat());
            }
            cb.writeParam("CuboidAreas", strs.toArray(new String[strs.size()]));

            // Residents
            strs = new ArrayList<>();
            for (final PlayerContainer pc : land.getResidents()) {
                strs.add(pc.toFileFormat());
            }
            cb.writeParam("Residents", strs.toArray(new String[strs.size()]));

            // Banneds
            strs = new ArrayList<>();
            for (final PlayerContainer pc : land.getBanneds()) {
                strs.add(pc.toFileFormat());
            }
            cb.writeParam("Banneds", strs.toArray(new String[strs.size()]));

            // Permissions
            strs = new ArrayList<>();
            for (final PlayerContainer pc : land.getPermissionsFlags().getSetPCHavePermission()) {
                for (final Permission perm : land.getPermissionsFlags().getPermissionsForPC(pc)) {
                    strs.add(pc.toFileFormat() + ":" + perm.toFileFormat());
                }
            }
            cb.writeParam("Permissions", strs.toArray(new String[strs.size()]));

            // Flags
            strs = new ArrayList<>();
            for (final Flag flag : land.getPermissionsFlags().getFlags()) {
                strs.add(flag.toFileFormat());
            }
            cb.writeParam("Flags", strs.toArray(new String[strs.size()]));

            // Priority
            cb.writeParam("Priority", land.getPriority());

            // Money
            cb.writeParam("Money", land.getMoney());

            // PlayersNotify
            strs = new ArrayList<>();
            for (final PlayerContainerPlayer pc : land.getPlayersNotify()) {
                strs.add(pc.toFileFormat());
            }
            cb.writeParam("PlayersNotify", strs.toArray(new String[strs.size()]));

            // Economy
            cb.writeParam("ForSale", land.isForSale() + "");
            if (land.isForSale()) {
                cb.writeParam("ForSaleSignLoc", StringChanges.locationToString(land.getSaleSignLoc()));
                cb.writeParam("SalePrice", land.getSalePrice());
            }
            cb.writeParam("ForRent", land.isForRent() + "");
            if (land.isForRent()) {
                cb.writeParam("ForRentSignLoc", StringChanges.locationToString(land.getRentSignLoc()));
                cb.writeParam("RentPrice", land.getRentPrice());
                cb.writeParam("RentRenew", land.getRentRenew());
                cb.writeParam("RentAutoRenew", land.getRentAutoRenew() + "");
                cb.writeParam("Rented", land.isRented() + "");
                if (land.isRented()) {
                    cb.writeParam("Tenant", land.getTenant().toFileFormat());
                    cb.writeParam("LastPayment", land.getLastPaymentTime() + "");
                }
            }

            cb.close();
        } catch (final IOException ex) {
            secuboid.getLogger().severe("Error on saving land " + land.getName() + ":" + ex);
        }
    }

    void removeLand(final Land land) {
        if (!getLandFile(land).delete()) {
            secuboid.getLogger().severe("Enable to delete the land " + land.getName());
        }
    }

    private Flag getFlagFromFileFormat(final String str) {

        final String[] multiStr = StringChanges.splitKeepQuote(str, ":");
        final FlagType ft = secuboid.getPermissionsFlags().getFlagTypeNoValid(multiStr[0]);
        final Object value = FlagValue.getFlagValueFromFileFormat(multiStr[1], ft);

        return secuboid.getPermissionsFlags().newFlag(ft, value, Boolean.parseBoolean(multiStr[2]));
    }
}