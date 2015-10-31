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
package me.tabinol.secuboid.lands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.WorldConfig;
import me.tabinol.secuboidapi.events.LandDeleteEvent;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.approve.ApproveList;
import me.tabinol.secuboid.lands.areas.AreaIndex;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.ApiLands;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiType;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.lands.collisions.Collisions.LandError;
import me.tabinol.secuboidapi.parameters.ApiFlagType;
import me.tabinol.secuboidapi.parameters.ApiFlagValue;
import me.tabinol.secuboidapi.parameters.ApiPermissionType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerPlayer;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


/**
 * The Class Lands.
 */
public class Lands implements ApiLands {

    /** The Constant INDEX_X1. */
    public final static int INDEX_X1 = 0;
    
    /** The Constant INDEX_Z1. */
    public final static int INDEX_Z1 = 1;
    
    /** The Constant INDEX_X2. */
    public final static int INDEX_X2 = 2;
    
    /** The Constant INDEX_Z2. */
    public final static int INDEX_Z2 = 3;
    
    /** The area list. */
    private final TreeMap<String, TreeSet<AreaIndex>>[] areaList; // INDEX first, Tree by worlds (then by Areas)
    
    /** The land uuid list. */
    private final TreeMap<UUID, ApiLand> landUUIDList; // Lands by UUID;
    
    /** The land list. */
    private final TreeMap<String, ApiLand> landList; // Tree by name
    
    /** The outside area. */
    protected TreeMap<String, DummyLand> outsideArea; // Outside a Land (in specific worlds)
    
    private final DummyLand defaultConfNoType; // Default config (Type not exist or Type null)
    
    /** The default conf. */
    private final TreeMap<ApiType, DummyLand> defaultConf; // Default config of a land
    
    /** The pm. */
    private final PluginManager pm;
    
    /** The approve list. */
    private final ApproveList approveList;
    
    /**  List of forSale. */
    private final HashSet<ApiLand> forSale;

    /**  List of forRent and rented. */
    private final HashSet<ApiLand> forRent;
    
    /**
     * Instantiates a new lands.
     */
    @SuppressWarnings("unchecked")
    public Lands() {

        areaList = new TreeMap[4];
        pm = Secuboid.getThisPlugin().getServer().getPluginManager();
        for (int t = 0; t < areaList.length; t++) {
            areaList[t] = new TreeMap<String, TreeSet<AreaIndex>>();
        }
        WorldConfig worldConfig = new WorldConfig();

        // Load World Config
        this.outsideArea = worldConfig.getLandOutsideArea();

        // Load Land default
        this.defaultConf = worldConfig.getTypeDefaultConf();
        this.defaultConfNoType = worldConfig.getDefaultconfNoType();

        landList = new TreeMap<String, ApiLand>();
        landUUIDList = new TreeMap<UUID, ApiLand>();
        approveList = new ApproveList();
        forSale = new HashSet<ApiLand>();
        forRent = new HashSet<ApiLand>();
    }

    public DummyLand getDefaultConf(ApiType type) {
        
        DummyLand land;
        
        // No type? Return default config
        if(type == null) {
            return defaultConfNoType;
        }
        
        land = defaultConf.get(type);
        
        // Type not found? Return default config
        if(land == null) {
            return defaultConfNoType;
        }
        
        return land;
    }
    
    /**
     * Gets the approve list.
     *
     * @return the approve list
     */
    public ApproveList getApproveList() {

        return approveList;
    }

    // For Land with no parent
    /**
     * Creates the land.
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public Land createLand(String landName, ApiPlayerContainer owner, ApiCuboidArea area)
            throws SecuboidLandException {

        return createLand(landName, owner, area, null, 1, null);
    }

    // For Land with parent
    /**
     * Creates the land.
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @param parent the parent
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public Land createLand(String landName, ApiPlayerContainer owner, ApiCuboidArea area,
            ApiLand parent)
            throws SecuboidLandException {

        return createLand(landName, owner, area, parent, 1, null);
    }

    // For Land with parent and price
    /**
     * Creates the land.
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @param parent the parent
     * @param price the price
     * @param type the type
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public Land createLand(String landName, ApiPlayerContainer owner, ApiCuboidArea area,
            ApiLand parent, double price, ApiType type)
            throws SecuboidLandException {
        
        getPriceFromPlayer(area.getWorldName(), owner, price);

        return createLand(landName, owner, area, parent, 1, null, type);
    }

    // Only for Land load at start
    /**
     * Creates the land.
     *
     * @param landName the land name
     * @param owner the owner
     * @param area the area
     * @param parent the parent
     * @param areaId the area id
     * @param uuid the uuid
     * @param type the type
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public Land createLand(String landName, ApiPlayerContainer owner, ApiCuboidArea area,
            ApiLand parent, int areaId, UUID uuid, ApiType type)
            throws SecuboidLandException {

        String landNameLower = landName.toLowerCase();
        int genealogy = 0;
        Land land;
        UUID landUUID;
        
        if (uuid == null) {
            landUUID = UUID.randomUUID();
        } else {
            landUUID = uuid;
        }

        if (parent != null) {
            genealogy = parent.getGenealogy() + 1;
        }

        if (isNameExist(landName)) {
            throw new SecuboidLandException(landName, (CuboidArea) area, 
                    LandAction.LAND_ADD, LandError.NAME_IN_USE);
        }

        land = new Land(landNameLower, landUUID, owner, area, genealogy, (Land) parent, areaId, type);

        addLandToList(land);
        Secuboid.getThisPlugin().getLog().write("add land: " + landNameLower);

        return land;
    }

    /**
     * Checks if is name exist.
     *
     * @param landName the land name
     * @return true, if is name exist
     */
    public boolean isNameExist(String landName) {

        return landList.containsKey(landName.toLowerCase());
    }

    /**
     * Removes the land.
     *
     * @param land the land
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    @SuppressWarnings("deprecation")
    public boolean removeLand(ApiLand land) throws SecuboidLandException {

        if (land == null) {
            return false;
        }

        LandDeleteEvent landEvent = new LandDeleteEvent((Land) land);

        if (!landList.containsKey(land.getName())) {
            return false;
        }

        // If the land has children
        if (!land.getChildren().isEmpty()) {
            throw new SecuboidLandException(land.getName(), null, LandAction.LAND_REMOVE, LandError.HAS_CHILDREN);
        }

        // Call Land Event and check if it is cancelled
        pm.callEvent(landEvent);
        
        if (landEvent.isCancelled()) {
            return false;
        }

        removeLandFromList((Land) land);
        if (land.getParent() != null) {
            ((Land) land.getParent()).removeChild(land.getUUID());
        }
        Secuboid.getThisPlugin().getStorageThread().removeLand((Land) land);
        Secuboid.getThisPlugin().getLog().write("remove land: " + land);
        return true;
    }

    /**
     * Removes the land.
     *
     * @param landName the land name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean removeLand(String landName) throws SecuboidLandException {

        return removeLand(landList.get(landName.toLowerCase()));
    }

    /**
     * Removes the land.
     *
     * @param uuid the uuid
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean removeLand(UUID uuid) throws SecuboidLandException {

        return removeLand(landUUIDList.get(uuid));
    }

    /**
     * Rename land.
     *
     * @param landName the land name
     * @param newName the new name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean renameLand(String landName, String newName) throws SecuboidLandException {

        Land land = (Land) getLand(landName);

        if (land != null) {
            return renameLand(land, newName);
        } else {
            return false;
        }
    }

    /**
     * Rename land.
     *
     * @param uuid the uuid
     * @param newName the new name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean renameLand(UUID uuid, String newName) throws SecuboidLandException {

        Land land = (Land) getLand(uuid);

        if (land != null) {
            return renameLand(land, newName);
        } else {
            return false;
        }
    }

    /**
     * Rename land.
     *
     * @param land the land
     * @param newName the new name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean renameLand(ApiLand land, String newName)
            throws SecuboidLandException {

        String oldNameLower = land.getName();
        String newNameLower = newName.toLowerCase();

        if (isNameExist(newNameLower)) {
            throw new SecuboidLandException(newNameLower, null, LandAction.LAND_RENAME, LandError.NAME_IN_USE);
        }

        landList.remove(oldNameLower);

        ((Land) land).setName(newNameLower);
        landList.put(newNameLower, (Land) land);

        return true;
    }

    /**
     * Gets the land.
     *
     * @param landName the land name
     * @return the land
     */
    public Land getLand(String landName) {

        return (Land) landList.get(landName.toLowerCase());
    }

    /**
     * Gets the land.
     *
     * @param uuid the uuid
     * @return the land
     */
    public Land getLand(UUID uuid) {

        return (Land) landUUIDList.get(uuid);
    }

    /**
     * Gets the land.
     *
     * @param loc the loc
     * @return the land
     */
    public Land getLand(Location loc) {

        ApiCuboidArea ca;

        if ((ca = getCuboidArea(loc)) == null) {
            return null;
        }
        return (Land) ca.getLand();
    }

    /**
     * Gets the lands.
     *
     * @return the lands
     */
    public Collection<ApiLand> getLands() {

        return landList.values();
    }

    /**
     * Gets the land or outside area.
     *
     * @param loc the loc
     * @return the land or outside area
     */
    public DummyLand getLandOrOutsideArea(Location loc) {

        Land land;

        if ((land = getLand(loc)) != null) {
            return land;
        }

        return getOutsideArea(loc);
    }

    /**
     * Gets the outside area.
     *
     * @param loc the loc
     * @return the outside area
     */
    public DummyLand getOutsideArea(Location loc) {

        return getOutsideArea(loc.getWorld().getName());
    }

    /**
     * Gets the outside area.
     *
     * @param worldName the world name
     * @return the outside area
     */
    public DummyLand getOutsideArea(String worldName) {

        String worldNameLower = worldName.toLowerCase();
        DummyLand dummyLand = outsideArea.get(worldNameLower);

        // Not exist, create one
        if (dummyLand == null) {
            dummyLand = new DummyLand(worldNameLower);
            outsideArea.get(Config.GLOBAL).copyPermsFlagsTo(dummyLand);
            outsideArea.put(worldNameLower, dummyLand);
        }

        return dummyLand;
    }

    /**
     * Gets the lands.
     *
     * @param loc the loc
     * @return the lands
     */
    public Collection<ApiLand> getLands(Location loc) {

        Collection<ApiCuboidArea> areas = getCuboidAreas(loc);
        HashMap<String, ApiLand>
            lands = new HashMap<String, ApiLand>();

        for (ApiCuboidArea area : areas) {
            lands.put(area.getLand().getName(), area.getLand());
        }

        return lands.values();
    }

    /**
     * Gets the lands.
     *
     * @param owner the owner
     * @return the lands
     */
    public Collection<ApiLand> getLands(ApiPlayerContainer owner) {

        Collection<ApiLand>
            lands = new HashSet<ApiLand>();

        for (ApiLand land : landList.values()) {
            if (land.getOwner().equals(owner)) {
                lands.add(land);
            }
        }

        return lands;
    }

    /**
     * Gets the lands from type.
     *
     * @param type the type
     * @return the lands
     */
    public Collection<ApiLand> getLands(ApiType type) {
        
        Collection<ApiLand>
        lands = new HashSet<ApiLand>();

    for (ApiLand land : landList.values()) {
        if (land.getType() == type) {
            lands.add(land);
        }
    }

    return lands;
    }

    /**
     * Gets the price from player.
     *
     * @param worldName the world name
     * @param pc the pc
     * @param price the price
     * @return the price from player
     */
    protected boolean getPriceFromPlayer(String worldName, ApiPlayerContainer pc, double price) {
        
        if(pc.getContainerType() == ApiPlayerContainerType.PLAYER && price > 0) {
            return Secuboid.getThisPlugin().getPlayerMoney().getFromPlayer(((ApiPlayerContainerPlayer)pc).getOfflinePlayer(), worldName, price);
        }
    
    return true;
    }
    
    /**
     * Gets the permission in world.
     *
     * @param worldName the world name
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the permission in world
     */
    protected boolean getPermissionInWorld(String worldName, Player player, ApiPermissionType pt, boolean onlyInherit) {

        Boolean result;
        DummyLand dl;

        if ((dl = outsideArea.get(worldName.toLowerCase())) != null && (result = dl.getPermission(player, pt, onlyInherit)) != null) {
            return result;
        }

        return pt.getDefaultValue();
    }

    /**
     * Gets the flag in world.
     *
     * @param worldName the world name
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag value in world
     */
    protected ApiFlagValue getFlagInWorld(String worldName, ApiFlagType ft, boolean onlyInherit) {

        ApiFlagValue result;
        DummyLand dl;

        if ((dl = outsideArea.get(worldName.toLowerCase())) != null && (result = dl.getFlag(ft, onlyInherit)) != null) {
            return result;
        }

        return ft.getDefaultValue();
    }

    /**
     * Gets the cuboid areas.
     *
     * @param loc the loc
     * @return the cuboid areas
     */
    public Collection<ApiCuboidArea> getCuboidAreas(Location loc) {

        Collection<ApiCuboidArea> areas = new ArrayList<ApiCuboidArea>();
        String worldName = loc.getWorld().getName();
        int SearchIndex;
        int nbToFind;
        boolean ForwardSearch;
        TreeSet<AreaIndex> ais;
        AreaIndex ai;
        Iterator<AreaIndex> it;

        // First, determinate if what is the highest number between x1, x2, z1 and z2
        if (Math.abs(loc.getBlockX()) > Math.abs(loc.getBlockZ())) {
            nbToFind = loc.getBlockX();
            if (loc.getBlockX() < 0) {
                SearchIndex = INDEX_X1;
                ForwardSearch = true;
            } else {
                SearchIndex = INDEX_X2;
                ForwardSearch = false;
            }
        } else {
            nbToFind = loc.getBlockZ();
            if (loc.getBlockZ() < 0) {
                SearchIndex = INDEX_Z1;
                ForwardSearch = true;
            } else {
                SearchIndex = INDEX_Z2;
                ForwardSearch = false;
            }
        }
        Secuboid.getThisPlugin().getLog().write("Search Index dir: " + SearchIndex + ", Forward Search: " + ForwardSearch);

        // Now check for area in location
        ais = areaList[SearchIndex].get(worldName);
        if (ais == null || ais.isEmpty()) {
            return areas;
        }
        if (ForwardSearch) {
            it = ais.iterator();
        } else {
            it = ais.descendingIterator();
        }

        // Adds all areas to the list
        while (it.hasNext() && checkContinueSearch((ai = it.next()).getArea(), nbToFind, SearchIndex)) {

            if (ai.getArea().isLocationInside(loc)) {
                Secuboid.getThisPlugin().getLog().write("add this area in list for cuboid: " + ai.getArea().getLand().getName());
                areas.add(ai.getArea());
            }
        }
        Secuboid.getThisPlugin().getLog().write("Number of Areas found for location : " + areas.size());

        return areas;
    }

    /**
     * Gets the cuboid area.
     *
     * @param loc the loc
     * @return the cuboid area
     */
    public ApiCuboidArea getCuboidArea(Location loc) {

        int actualPrio = Short.MIN_VALUE;
        int curPrio;
        int actualGen = 0;
        int curGen;
        ApiCuboidArea actualArea = null;
        Location resLoc; // Resolved location
        
        // Give the position from the sky to underbedrock if the Y is greater than 255 or lower than 0
        if(loc.getBlockY() >= loc.getWorld().getMaxHeight()) {
            resLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getMaxHeight() - 1, loc.getZ()); 
        } else if(loc.getBlockY() < 0){
            resLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ()); 
        } else resLoc = loc; 
        
        Collection<ApiCuboidArea> areas = getCuboidAreas(resLoc);

        Secuboid.getThisPlugin().getLog().write("Area check in" + resLoc.toString());

        // Compare priorities of parents (or main)
        for (ApiCuboidArea area : areas) {

            Secuboid.getThisPlugin().getLog().write("Check for: " + area.getLand().getName()
                    + ", area: " + area.toString());

            curPrio = area.getLand().getPriority();
            curGen = area.getLand().getGenealogy();

            if (actualPrio < curPrio
                    || (actualPrio == curPrio && actualGen <= curGen)) {
                actualArea = area;
                actualPrio = curPrio;
                actualGen = area.getLand().getGenealogy();

                Secuboid.getThisPlugin().getLog().write("Found, update:  actualPrio: " + actualPrio + ", actualGen: " + actualGen);
            }
        }

        return actualArea;
    }

    /**
     * Check continue search.
     *
     * @param area the area
     * @param nbToFind the nb to find
     * @param SearchIndex the search index
     * @return true, if successful
     */
    private boolean checkContinueSearch(ApiCuboidArea area, int nbToFind, int SearchIndex) {

        switch (SearchIndex) {
            case INDEX_X1:
                if (nbToFind >= area.getX1()) {
                    return true;
                }
                return false;
            case INDEX_X2:
                if (nbToFind <= area.getX2()) {
                    return true;
                }
                return false;
            case INDEX_Z1:
                if (nbToFind >= area.getZ1()) {
                    return true;
                }
                return false;
            case INDEX_Z2:
                if (nbToFind <= area.getZ2()) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Adds the area to list.
     *
     * @param area the area
     */
    protected void addAreaToList(ApiCuboidArea area) {

        if (!areaList[0].containsKey(area.getWorldName())) {
            for (int t = 0; t < 4; t++) {
                areaList[t].put(area.getWorldName(), new TreeSet<AreaIndex>());
            }
        }
        Secuboid.getThisPlugin().getLog().write("Add area for " + area.getLand().getName());
        areaList[INDEX_X1].get(area.getWorldName()).add(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].get(area.getWorldName()).add(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].get(area.getWorldName()).add(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].get(area.getWorldName()).add(new AreaIndex(area.getZ2(), area));
    }

    /**
     * Removes the area from list.
     *
     * @param area the area
     */
    protected void removeAreaFromList(ApiCuboidArea area) {

        areaList[INDEX_X1].get(area.getWorldName()).remove(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].get(area.getWorldName()).remove(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].get(area.getWorldName()).remove(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].get(area.getWorldName()).remove(new AreaIndex(area.getZ2(), area));
    }

    /**
     * Adds the land to list.
     *
     * @param land the land
     */
    private void addLandToList(Land land) {

        landList.put(land.getName(), land);
        landUUIDList.put(land.getUUID(), land);
    }

    /**
     * Removes the land from list.
     *
     * @param land the land
     */
    private void removeLandFromList(Land land) {

        landList.remove(land.getName());
        landUUIDList.remove(land.getUUID());
        for (ApiCuboidArea area : land.getAreas()) {
            removeAreaFromList(area);
        }
    }
    
    /**
     * Adds the for sale.
     *
     * @param land the land
     */
    protected void addForSale(Land land) {
        
        forSale.add(land);
    }
    
    /**
     * Removes the for sale.
     *
     * @param land the land
     */
    protected void removeForSale(Land land) {
        
        forSale.remove(land);
    }
    
    /**
     * Gets the for sale.
     *
     * @return the for sale
     */
    public Collection<ApiLand> getForSale() {
        
        return forSale;
    }

    /**
     * Adds the for rent.
     *
     * @param land the land
     */
    protected void addForRent(Land land) {
        
        forRent.add(land);
    }
    
    /**
     * Removes the for rent.
     *
     * @param land the land
     */
    protected void removeForRent(Land land) {
        
        forRent.remove(land);
    }
    
    /**
     * Gets the for rent.
     *
     * @return the for rent
     */
    public Collection<ApiLand> getForRent() {
        
        return forRent;
    }
}
