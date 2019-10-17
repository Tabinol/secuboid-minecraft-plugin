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
package me.tabinol.secuboid.storage.flat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import me.tabinol.secuboid.NewInstance;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.FileLoadException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaUtil;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.storage.Storage;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.Location;

/**
 * The Class StorageFlat.
 */
public class StorageFlat implements Storage {

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

    /**
     * Represents lands with non resolved parents. In a second loop, we will try to resolve.
     */
    private Map<RealLand, UUID> orphans;

    /**
     * Instantiates a new storage flat.
     *
     * @param secuboid secuboid instance
     */
    public StorageFlat(Secuboid secuboid) {
        this.secuboid = secuboid;
        landVersion = secuboid.getMavenAppProperties().getPropertyInt("landVersion");
        createDirFiles();
    }

    @Override
    public void loadAll() {
        loadLands();
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
    private void createDir(String dir) {

        File file = new File(dir);

        if (!file.exists() && !file.mkdir()) {
            secuboid.getLog().severe("Unable to create directory " + file.getPath() + ".");
        }
    }

    /**
     * Gets the land file.
     *
     * @param land the land
     * @return the land file
     */
    private File getLandFile(RealLand land) {
        return new File(landsDir + "/" + land.getUUID() + EXT_CONF);
    }

    /**
     * Gets the land file.
     *
     * @param landUUID the land uuid
     * @return the land file
     */
    private File getLandFile(UUID landUUID) {
        return new File(landsDir + "/" + landUUID + EXT_CONF);
    }

    @Override
    public void loadLands() {

        File[] files = new File(landsDir).listFiles();
        int loadedlands = 0;
        orphans = new HashMap<RealLand, UUID>();

        assert files != null;
        if (files.length == 0) {
            return;
        }

        // Pass 1: load lands
        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(EXT_CONF)) {
                try {
                    loadLand(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    secuboid.getLog().severe("Unable to load the land from file: " + file.getName());
                }
                loadedlands++;
            }
        }

        // Pass 2: find parents
        for (Map.Entry<RealLand, UUID> entry : orphans.entrySet()) {
            RealLand land = entry.getKey();
            RealLand parent = secuboid.getLands().getLand(entry.getValue());
            if (parent != null) {
                land.setParent(parent);
            } else {
                secuboid.getLog().severe("Error: The parent is not found! [name=" + land.getName() 
                    + ", uuid=" + land.getUUID() + ", parentUuid=" + entry.getValue() + "]");
            }
        }
        secuboid.getLog().info(loadedlands + " land(s) loaded.");
    }

    /**
     * Load land.
     *
     * @param file the file
     */
    private void loadLand(File file) {

        NewInstance newInstance = secuboid.getNewInstance();
        // int version;
        ConfLoaderFlat cf = null;
        UUID uuid;
        String landName;
        String type;
        RealLand land = null;
        Map<Integer, Area> areas = new TreeMap<Integer, Area>();
        boolean isLandCreated = false;
        PlayerContainer owner;
        String parentUUID;
        Set<PlayerContainer> residents = new TreeSet<PlayerContainer>();
        Set<PlayerContainer> banneds = new TreeSet<PlayerContainer>();
        Map<PlayerContainer, TreeMap<PermissionType, Permission>> permissions
                = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        Set<Flag> flags = new HashSet<Flag>();
        short priority;
        double money;
        Set<PlayerContainerPlayer> pNotifs = new TreeSet<PlayerContainerPlayer>();

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
            // Add for different version support: version = cf.getVersion();
            uuid = cf.getUUID();
            landName = cf.getName();
            cf.readParam();
            type = cf.getValueString();
            cf.readParam();
            String ownerS = cf.getValueString();

            // create owner (PlayerContainer)
            owner = newInstance.getPlayerContainerFromFileFormat(ownerS);
            if (owner == null) {
                throw new FileLoadException(secuboid, file.getName(), cf.getLine(), cf.getLineNb(), "Invalid owner.");
            }

            cf.readParam();
            parentUUID = cf.getValueString();

            cf.readParam();

            // Create areas
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":", 2);
                areas.put(Integer.parseInt(multiStr[0]), AreaUtil.getFromFileFormat(multiStr[1]));
            }
            if (areas.isEmpty()) {
                throw new FileLoadException(secuboid, file.getName(), cf.getLine(), cf.getLineNb(), "No areas in the list.");
            }

            cf.readParam();

            //Residents
            while ((str = cf.getNextString()) != null) {
                residents.add(newInstance.getPlayerContainerFromFileFormat(str));
            }
            cf.readParam();

            //Banneds
            while ((str = cf.getNextString()) != null) {
                banneds.add(newInstance.getPlayerContainerFromFileFormat(str));
            }
            cf.readParam();

            //Create permissions
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":");
                TreeMap<PermissionType, Permission> permPlayer;
                PlayerContainer pc = newInstance.getPlayerContainerFromFileFormat(multiStr[0] + ":" + multiStr[1]);
                PermissionType permType = secuboid.getPermissionsFlags().getPermissionTypeNoValid(multiStr[2]);
                if (!permissions.containsKey(pc)) {
                    permPlayer = new TreeMap<PermissionType, Permission>();
                    permissions.put(pc, permPlayer);
                } else {
                    permPlayer = permissions.get(pc);
                }
                permPlayer.put(permType, secuboid.getPermissionsFlags().newPermission(permType,
                        Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
            }
            cf.readParam();

            //Create flags
            while ((str = cf.getNextString()) != null) {
                flags.add(secuboid.getNewInstance().getFlagFromFileFormat(str));
            }
            cf.readParam();

            //Set Priority
            priority = cf.getValueShort();
            cf.readParam();

            //Money
            money = cf.getValueDouble();
            cf.readParam();

            //Players Notify
            while ((str = cf.getNextString()) != null) {
                pNotifs.add((PlayerContainerPlayer) newInstance.getPlayerContainerFromFileFormat(str));
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
                    tenant = (PlayerContainerPlayer) newInstance.getPlayerContainerFromFileFormat(cf.getValueString());
                    cf.readParam();
                    lastPayment = cf.getValueLong();
                }
            }

            cf.close();

            // Catch errors here
        } catch (NullPointerException ex) {
            try {
                throw new FileLoadException(secuboid, file.getName(),
                        cf != null ? cf.getLine() : "-NOT FOUND-",
                        cf != null ? cf.getLineNb() : 0, "Problem with parameter.");
            } catch (FileLoadException ex2) {
                // Catch load
                return;
            }
        } catch (FileLoadException ex) {
            // Catch load
            return;
        }

        // Create land
        for (Map.Entry<Integer, Area> entry : areas.entrySet()) {
            if (!isLandCreated) {
                try {
                    land = secuboid.getLands().createLand(landName, owner, entry.getValue(),
                            null, entry.getKey(), uuid, secuboid.getTypes().addOrGetType(type));
                    if (parentUUID != null) {
                        orphans.put(land, UUID.fromString(parentUUID));
                    }
                } catch (SecuboidLandException ex) {
                    secuboid.getLog().severe("Error on loading land " + landName + ":" + ex.getLocalizedMessage());
                    return;
                }
                isLandCreated = true;
            } else {
                if (land == null) {
                    secuboid.getLog().severe("Error: Land not created: " + landName);
                    return;
                }
                land.addArea(entry.getValue(), entry.getKey());
            }
        }

        if (land == null) {
            secuboid.getLog().severe("Error: Land not created: " + landName);
            return;
        }

        // Load land params form memory
        for (PlayerContainer resident : residents) {
            land.addResident(resident);
        }
        for (PlayerContainer banned : banneds) {
            land.addBanned(banned);
        }
        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> entry : permissions.entrySet()) {
            for (Map.Entry<PermissionType, Permission> entryP : entry.getValue().entrySet()) {
                land.getPermissionsFlags().addPermission(entry.getKey(), entryP.getValue());
            }
        }
        for (Flag flag : flags) {
            land.getPermissionsFlags().addFlag(flag);
        }
        land.setPriority(priority);
        land.addMoney(money);
        for (PlayerContainerPlayer pNotif : pNotifs) {
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
    }

    @Override
    public void saveLand(RealLand land) {
        try {
            ArrayList<String> strs;

            if (secuboid.getStorageThread().isInLoad()) {
                return;
            }

            ConfBuilderFlat cb = new ConfBuilderFlat(land.getName(), land.getUUID(), getLandFile(land), landVersion);
            cb.writeParam("Type", land.getType() != null ? land.getType().getName() : null);
            cb.writeParam("Owner", land.getOwner().toFileFormat());

            //Parent
            if (land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {
                cb.writeParam("Parent", land.getParent().getUUID().toString());
            }

            //CuboidAreas
            strs = new ArrayList<String>();
            for (int index : land.getAreasKey()) {
                strs.add(index + ":" + land.getArea(index).toFileFormat());
            }
            cb.writeParam("CuboidAreas", strs.toArray(new String[strs.size()]));

            //Residents
            strs = new ArrayList<String>();
            for (PlayerContainer pc : land.getResidents()) {
                strs.add(pc.toFileFormat());
            }
            cb.writeParam("Residents", strs.toArray(new String[strs.size()]));

            //Banneds
            strs = new ArrayList<String>();
            for (PlayerContainer pc : land.getBanneds()) {
                strs.add(pc.toFileFormat());
            }
            cb.writeParam("Banneds", strs.toArray(new String[strs.size()]));

            //Permissions
            strs = new ArrayList<String>();
            for (PlayerContainer pc : land.getPermissionsFlags().getSetPCHavePermission()) {
                for (Permission perm : land.getPermissionsFlags().getPermissionsForPC(pc)) {
                    strs.add(pc.toFileFormat() + ":" + perm.toFileFormat());
                }
            }
            cb.writeParam("Permissions", strs.toArray(new String[strs.size()]));

            //Flags
            strs = new ArrayList<String>();
            for (Flag flag : land.getPermissionsFlags().getFlags()) {
                strs.add(flag.toFileFormat());
            }
            cb.writeParam("Flags", strs.toArray(new String[strs.size()]));

            // Priority
            cb.writeParam("Priority", land.getPriority());

            // Money
            cb.writeParam("Money", land.getMoney());

            // PlayersNotify
            strs = new ArrayList<String>();
            for (PlayerContainerPlayer pc : land.getPlayersNotify()) {
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
        } catch (IOException ex) {
            secuboid.getLog().severe("Error on saving land " + land.getName() + ":" + ex);
        }
    }

    @Override
    public void removeLand(RealLand land) {
        if (!getLandFile(land).delete()) {
            secuboid.getLog().severe("Enable to delete the land " + land.getName());
        }
    }

    @Override
    public void removeLand(UUID landUUID, int landGenealogy) {
        if (!getLandFile(landUUID).delete()) {
            secuboid.getLog().severe("Enable to delete the land " + landUUID);
        }
    }
}
