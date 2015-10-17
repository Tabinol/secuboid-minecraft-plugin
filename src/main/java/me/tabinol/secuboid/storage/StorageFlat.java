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
package me.tabinol.secuboid.storage;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.exceptions.FileLoadException;
import me.tabinol.secuboid.factions.Faction;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.parameters.LandFlag;
import me.tabinol.secuboid.parameters.Permission;
import me.tabinol.secuboid.parameters.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboidapi.SecuboidAPI;
import me.tabinol.secuboidapi.utilities.StringChanges;
import me.tabinol.secuboidapi.parameters.ILandFlag;
import me.tabinol.secuboidapi.parameters.IPermission;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainerPlayer;


/**
 * The Class StorageFlat.
 */
public class StorageFlat extends Storage implements StorageInt {

    /** The Constant EXT_CONF. */
    public static final String EXT_CONF = ".conf";
    
    /** The factions dir. */
    private String factionsDir;
    
    /** The lands dir. */
    private String landsDir;
    
    /**
     * Instantiates a new storage flat.
     */
    public StorageFlat() {

        super();

        createDirFiles();
    }

    /**
     * Creates the dir files.
     */
    private void createDirFiles() {

        factionsDir = Secuboid.getThisPlugin().getDataFolder() + "/" + "factions" + "/";
        landsDir = Secuboid.getThisPlugin().getDataFolder() + "/" + "lands" + "/";

        createDir(landsDir);
        createDir(factionsDir);
    }

    /**
     * Creates the dir.
     *
     * @param dir the dir
     */
    private void createDir(String dir) {

        File file = new File(dir);

        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * Gets the faction file.
     *
     * @param faction the faction
     * @return the faction file
     */
    private File getFactionFile(Faction faction) {

        return new File(factionsDir + "/" + faction.getName() + EXT_CONF);
    }

    /**
     * Gets the land file.
     *
     * @param land the land
     * @return the land file
     */
    private File getLandFile(Land land) {

        return new File(landsDir + "/" + land.getName() + "." + land.getGenealogy() + EXT_CONF);
    }

    /**
     * Gets the land file.
     *
     * @param landName the land
     * @param landGenealogy the land genealogy
     * @return the land file
     */
    private File getLandFile(String landName, int landGenealogy) {

        return new File(landsDir + "/" + landName + "." + landGenealogy + EXT_CONF);
    }
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.storage.StorageInt#loadFactions()
     */
    @Override
    public void loadFactions() {

        File[] files = new File(factionsDir).listFiles();
        int loadedfactions = 0;

        if (files.length == 0) {
            Secuboid.getThisPlugin().iLog().write(loadedfactions + " faction(s) loaded.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(EXT_CONF)) {
                loadFaction(file);
                loadedfactions++;
            }
        }
        Secuboid.getThisPlugin().iLog().write(loadedfactions + " faction(s) loaded.");
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.storage.StorageInt#loadLands()
     */
    @Override
    public void loadLands() {

        File[] files = new File(landsDir).listFiles();
        int loadedlands = 0;
        int pass = 0;
        boolean empty = false;

        if (files.length == 0) {
            Secuboid.getThisPlugin().iLog().write(loadedlands + " land(s) loaded.");
            return;
        }

        while (!empty) {
            empty = true;
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(pass + EXT_CONF)) {
                    empty = false;
                    loadLand(file);
                    loadedlands++;
                }
            }
            pass++;
        }
        Secuboid.getThisPlugin().iLog().write(loadedlands + " land(s) loaded.");
    }

    /**
     * Load faction.
     *
     * @param file the file
     */
    private void loadFaction(File file) {

        Faction faction;
        ConfLoader cf = null;
        ArrayList<PlayerContainerPlayer> playerNames = new ArrayList<PlayerContainerPlayer>();
        UUID uuid;

        try {
            cf = new ConfLoader(file);
            String str;
            
            @SuppressWarnings("unused")
			int version = cf.getVersion();
            
            uuid = cf.getUUID();
            cf.readParam();
            while ((str = cf.getNextString()) != null) {
                playerNames.add((PlayerContainerPlayer) PlayerContainer.getFromString(str));
            }

            cf.close();

            // Catch errors here
        } catch (NullPointerException ex) {
            try {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Problem with parameter.");
            } catch (FileLoadException ex2) {
                // Catch load
                return;
            }
        } catch (FileLoadException ex) {
            // Catch load
            return;
        }

        // Create Faction
        faction = Secuboid.getThisPlugin().iFactions().createFaction(cf.getName(), uuid);
        for (PlayerContainerPlayer player : playerNames) {
            faction.addPlayer(player);
        }
    }

    /**
     * Load land.
     *
     * @param file the file
     */
    private void loadLand(File file) {

        int version;
        ConfLoader cf = null;
        UUID uuid;
        String landName;
        String type = null;
        Land land = null;
        Map<Integer, CuboidArea> areas = new TreeMap<Integer, CuboidArea>();
        boolean isLandCreated = false;
        PlayerContainer owner;
        String parentName;
        String factionTerritory;
        Set<PlayerContainer> residents = new TreeSet<PlayerContainer>();
        Set<PlayerContainer> banneds = new TreeSet<PlayerContainer>();
        Map<PlayerContainer, TreeMap<PermissionType, Permission>> permissions
                = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        Set<LandFlag> flags = new HashSet<LandFlag>();
        short priority;
        double money;
        Set<PlayerContainerPlayer> pNotifs = new TreeSet<PlayerContainerPlayer>();
        Land parent;

        // For economy
        boolean forSale = false;
        Location forSaleSignLoc = null;
        double salePrice = 0;
        boolean forRent = false;
        Location forRentSignLoc = null;
        double rentPrice = 0;
        int rentRenew = 0;
        boolean rentAutoRenew = false;
        boolean rented = false;
        PlayerContainerPlayer tenant = null;
        Timestamp lastPayment = null;

        Secuboid.getThisPlugin().iLog().write("Open file : " + file.getName());

        try {
            cf = new ConfLoader(file);
            String str;
            version = cf.getVersion();
            uuid = cf.getUUID();
            landName = cf.getName();
            if(version >= 5) {
            	cf.readParam();
            	type = cf.getValueString();
            }
            cf.readParam();
            String ownerS = cf.getValueString();

            // create owner (PlayerContainer)
            owner = PlayerContainer.getFromString(ownerS);
            if (owner == null) {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Invalid owner.");
            }

            cf.readParam();
            parentName = cf.getValueString();
            cf.readParam();
            factionTerritory = cf.getValueString();

            cf.readParam();

            // Create areas
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":", 2);
                areas.put(Integer.parseInt(multiStr[0]), CuboidArea.getFromString(multiStr[1]));
            }
            if (areas.isEmpty()) {
            	throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "No areas in the list.");
            }

            cf.readParam();

            //Residents
            while ((str = cf.getNextString()) != null) {
                residents.add(PlayerContainer.getFromString(str));
            }
            cf.readParam();

            //Banneds
            while ((str = cf.getNextString()) != null) {
                banneds.add(PlayerContainer.getFromString(str));
            }
            cf.readParam();

            //Create permissions
            while ((str = cf.getNextString()) != null) {
                String[] multiStr = str.split(":");
                TreeMap<PermissionType, Permission> permPlayer;
                PlayerContainer pc = PlayerContainer.getFromString(multiStr[0] + ":" + multiStr[1]);
                PermissionType permType = Secuboid.getThisPlugin().iParameters().getPermissionTypeNoValid(multiStr[2]);
                if (!permissions.containsKey(pc)) {
                    permPlayer = new TreeMap<PermissionType, Permission>();
                    permissions.put(pc, permPlayer);
                } else {
                    permPlayer = permissions.get(pc);
                }
                permPlayer.put(permType, new Permission(permType,
                        Boolean.parseBoolean(multiStr[3]), Boolean.parseBoolean(multiStr[4])));
            }
            cf.readParam();

            //Create flags
            while ((str = cf.getNextString()) != null) {
                flags.add(LandFlag.getFromString(str));
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
                pNotifs.add((PlayerContainerPlayer) PlayerContainer.getFromString(str));
            }
            
            // Economy
            if(version >= 4) {
            	cf.readParam();
            	forSale = Boolean.parseBoolean(cf.getValueString());
            	if(forSale) {
            		cf.readParam();
            		forSaleSignLoc = StringChanges.stringToLocation(cf.getValueString());
            		cf.readParam();
            		salePrice = cf.getValueDouble();
            	}
            	cf.readParam();
            	forRent = Boolean.parseBoolean(cf.getValueString());
            	if(forRent) {
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
            		if(rented) {
            			cf.readParam();
            			tenant = (PlayerContainerPlayer) PlayerContainer.getFromString(cf.getValueString());
            			cf.readParam();
            			lastPayment = Timestamp.valueOf(cf.getValueString());
            		}
            	}
            }

            cf.close();

            // Catch errors here
        } catch (NullPointerException ex) {
            try {
                throw new FileLoadException(file.getName(), cf.getLine(), cf.getLineNb(), "Problem with parameter.");
            } catch (FileLoadException ex2) {
                // Catch load
                return;
            }
        } catch (FileLoadException ex) {
            // Catch load
            return;
        }

        // Create land
        for (Map.Entry<Integer, CuboidArea> entry : areas.entrySet()) {
            if (!isLandCreated) {
                if (parentName != null) {

                    parent = Secuboid.getThisPlugin().iLands().getLand(UUID.fromString(parentName));

                    try {
                        land = Secuboid.getThisPlugin().iLands().createLand(landName, owner, entry.getValue(), parent,
                                entry.getKey(), uuid, SecuboidAPI.iTypes().addOrGetType(type));
                    } catch (SecuboidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                        return;
                    }
                } else {
                    try {
                        land = Secuboid.getThisPlugin().iLands().createLand(landName, owner, entry.getValue(), 
                        		null, entry.getKey(), uuid, SecuboidAPI.iTypes().addOrGetType(type));
                    } catch (SecuboidLandException ex) {
                        Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on loading land: " + landName, ex);
                        return;
                    }
                }
                isLandCreated = true;
            } else {
                land.addArea(entry.getValue(), entry.getKey());
            }
        }

        // Load land params form memory
        if (factionTerritory != null) {
            land.setFactionTerritory(Secuboid.getThisPlugin().iFactions().getFaction(factionTerritory));
        }
        for (PlayerContainer resident : residents) {
            land.addResident(resident);
        }
        for (PlayerContainer banned : banneds) {
            land.addResident(banned);
        }
        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> entry : permissions.entrySet()) {
            for (Map.Entry<PermissionType, Permission> entryP : entry.getValue().entrySet()) {
                land.addPermission(entry.getKey(), entryP.getValue());
            }
        }
        for (LandFlag flag : flags) {
            land.addFlag(flag);
        }
        land.setPriority(priority);
        land.addMoney(money);
        for (PlayerContainerPlayer pNotif : pNotifs) {
            land.addPlayerNotify(pNotif);
        }
        
        // Economy add
        if (version >= 4) {
        	if(forSale) {
        		land.setForSale(true, salePrice, forSaleSignLoc);
        	}
        	if(forRent) {
        		land.setForRent(rentPrice, rentRenew, rentAutoRenew, forRentSignLoc);
        		if(rented) {
        			land.setRented(tenant);
        			land.setLastPaymentTime(lastPayment);
        		}
        	}
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.storage.StorageInt#saveLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void saveLand(Land land) {
        try {
            ArrayList<String> strs;

            if (Secuboid.getThisPlugin().iStorageThread().isInLoad()) {
                return;
            }

            Secuboid.getThisPlugin().iLog().write("Saving land: " + land.getName());
            ConfBuilder cb = new ConfBuilder(land.getName(), land.getUUID(), getLandFile(land), LAND_VERSION);
            cb.writeParam("Type", land.getType() != null ? land.getType().getName() : null);
            cb.writeParam("Owner", land.getOwner().toString());

            //Parent
            if (land.getParent() == null) {
                cb.writeParam("Parent", (String) null);
            } else {
                cb.writeParam("Parent", land.getParent().getUUID().toString());
            }

            //factionTerritory
            if (land.getFactionTerritory() == null) {
                cb.writeParam("FactionTerritory", (String) null);
            } else {
                cb.writeParam("FactionTerritory", land.getFactionTerritory().getName());
            }

            //CuboidAreas
            strs = new ArrayList<String>();
            for (int index : land.getAreasKey()) {
                strs.add(index + ":" + land.getArea(index).toString());
            }
            cb.writeParam("CuboidAreas", strs.toArray(new String[0]));

            //Residents
            strs = new ArrayList<String>();
            for (IPlayerContainer pc : land.getResidents()) {
                strs.add(pc.toString());
            }
            cb.writeParam("Residents", strs.toArray(new String[0]));

            //Banneds
            strs = new ArrayList<String>();
            for (IPlayerContainer pc : land.getBanneds()) {
                strs.add(pc.toString());
            }
            cb.writeParam("Banneds", strs.toArray(new String[0]));

            //Permissions
            strs = new ArrayList<String>();
            for (IPlayerContainer pc : land.getSetPCHavePermission()) {
                for (IPermission perm : land.getPermissionsForPC(pc)) {
                    strs.add(pc.toString() + ":" + perm.toString());
                }
            }
            cb.writeParam("Permissions", strs.toArray(new String[0]));

            //Flags
            strs = new ArrayList<String>();
            for (ILandFlag flag : land.getFlags()) {
                strs.add(flag.toString());
            }
            cb.writeParam("Flags", strs.toArray(new String[0]));

            // Priority
            cb.writeParam("Priority", land.getPriority());

            // Money
            cb.writeParam("Money", land.getMoney());

            // PlayersNotify
            strs = new ArrayList<String>();
            for (IPlayerContainerPlayer pc : land.getPlayersNotify()) {
                strs.add(pc.toString());
            }
            cb.writeParam("PlayersNotify", strs.toArray(new String[0]));

        	// Economy
        	cb.writeParam("ForSale", land.isForSale() + "");
        	if(land.isForSale()) {
        		cb.writeParam("ForSaleSignLoc", StringChanges.locationToString(land.getSaleSignLoc()));
        		cb.writeParam("SalePrice", land.getSalePrice());
        	}
        	if(land.isForRent()) {
        		cb.writeParam("ForRent", land.isForRent() + "");
        		cb.writeParam("ForRentSignLoc", StringChanges.locationToString(land.getRentSignLoc()));
        		cb.writeParam("RentPrice", land.getRentPrice());
        		cb.writeParam("ForRenew", land.getRentRenew());
        		cb.writeParam("ForAutoRenew", land.getRentAutoRenew() + "");
        		cb.writeParam("Rented", land.isRented() + "");
        		if(land.isRented()) {
        			cb.writeParam("Tenant", land.getTenant().toString());
        			cb.writeParam("LastPayment", land.getLastPaymentTime().toString());
        		}
        	}

            cb.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on saving land: " + land.getName(), ex);
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.storage.StorageInt#removeLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void removeLand(Land land) {

        getLandFile(land).delete();
    }

    @Override
    public void removeLand(String landName, int landGenealogy) {

        getLandFile(landName, landGenealogy).delete();
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.storage.StorageInt#saveFaction(me.tabinol.secuboid.factions.Faction)
     */
    @Override
    public void saveFaction(Faction faction) {
        try {
            if (Secuboid.getThisPlugin().iStorageThread().isInLoad()) {
                return;
            }

            Secuboid.getThisPlugin().iLog().write("Saving faction: " + faction.getName());
            ConfBuilder cb = new ConfBuilder(faction.getName(), faction.getUUID(), getFactionFile(faction), FACTION_VERSION);

            List<String> strs = new ArrayList<String>();
            for (IPlayerContainerPlayer pc : faction.getPlayers()) {
                strs.add(pc.toString());
            }
            cb.writeParam("Players", strs.toArray(new String[0]));

            cb.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageFlat.class.getName()).log(Level.SEVERE, "Error on saving Faction: " + faction.getName(), ex);
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.storage.StorageInt#removeFaction(me.tabinol.secuboid.factions.Faction)
     */
    @Override
    public void removeFaction(Faction faction) {

        getFactionFile(faction).delete();
    }
}
