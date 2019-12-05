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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.WorldConfig;
import me.tabinol.secuboid.events.LandDeleteEvent;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.approve.ApproveList;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaIndex;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.lands.collisions.Collisions.LandError;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import org.bukkit.Location;

/**
 * The Class Lands manager.
 */
public class Lands {

    private final Secuboid secuboid;

    /**
     * The Constant INDEX_X1.
     */
    private final static int INDEX_X1 = 0;

    /**
     * The Constant INDEX_Z1.
     */
    private final static int INDEX_Z1 = 1;

    /**
     * The Constant INDEX_X2.
     */
    private final static int INDEX_X2 = 2;

    /**
     * The Constant INDEX_Z2.
     */
    private final static int INDEX_Z2 = 3;

    /**
     * Area list put in an area for finding lands.
     */
    private class AreaMap {
        TreeMap<String, TreeSet<AreaIndex>> areaMap;
    }

    /**
     * INDEX first, Tree by worlds (then by Areas).
     */
    private final AreaMap[] areaList;

    /**
     * The land uuid list.
     */
    private final TreeMap<UUID, RealLand> landUUIDList;

    /**
     * The land list, sorted by names.
     */
    private final TreeMap<String, RealLand> landList;

    /**
     * The outside area in worlds.
     */
    private final Map<String, WorldLand> outsideArea;

    /**
     * Default configuration (Type not exist or Type null).
     */
    private final DefaultLand defaultConfNoType;

    /**
     * The default configuration for a land.
     */
    private final Map<Type, DefaultLand> defaultConf;

    /**
     * The approve list.
     */
    private final ApproveList approveList;

    /**
     * List of forSale.
     */
    private final HashSet<RealLand> forSale;

    /**
     * List of forRent and rented.
     */
    private final HashSet<RealLand> forRent;

    /**
     * Instantiates a new lands manager.
     *
     * @param secuboid secuboid instance
     */
    public Lands(Secuboid secuboid) {

        this.secuboid = secuboid;
        areaList = new AreaMap[4];

        for (int i = 0; i < 4; i++) {
            areaList[i] = new AreaMap();
            areaList[i].areaMap = new TreeMap<String, TreeSet<AreaIndex>>();
        }
        WorldConfig worldConfig = new WorldConfig(secuboid);

        // Load World Config
        this.outsideArea = null; // TODO

        // Load Land default
        this.defaultConf = null; // TODO
        this.defaultConfNoType = null; // TODO

        landList = new TreeMap<String, RealLand>();
        landUUIDList = new TreeMap<UUID, RealLand>();
        approveList = new ApproveList(secuboid);
        forSale = new HashSet<RealLand>();
        forRent = new HashSet<RealLand>();
    }

    /**
     * Gets the the default configuration for a land type.
     *
     * @param type the land type
     * @return the default configuration (land format).
     */
    public DefaultLand getDefaultConf(Type type) {

        DefaultLand land;

        // No type? Return default config
        if (type == null) {
            return defaultConfNoType;
        }

        land = defaultConf.get(type);

        // Type not found? Return default config
        if (land == null) {
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

    /**
     * Creates the land with no parent.
     *
     * @param landName the land name
     * @param owner    the owner
     * @param area     the area
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public RealLand createLand(String landName, PlayerContainer owner, Area area) throws SecuboidLandException {
        return createLand(landName, owner, area, null, 1, null);
    }

    /**
     * Creates the land with parent.
     *
     * @param landName the land name
     * @param owner    the owner
     * @param area     the area
     * @param parent   the parent
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public RealLand createLand(String landName, PlayerContainer owner, Area area, RealLand parent)
            throws SecuboidLandException {
        return createLand(landName, owner, area, parent, 1, null);
    }

    /**
     * Creates the land with parent (or null) and price.
     *
     * @param landName the land name
     * @param owner    the owner
     * @param area     the area
     * @param parent   the parent
     * @param price    the price
     * @param type     the type
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public RealLand createLand(String landName, PlayerContainer owner, Area area, RealLand parent, double price, Type type)
            throws SecuboidLandException {
        getPriceFromPlayer(area.getWorldName(), owner, price);
        return createLand(landName, owner, area, parent, 1, null, type);
    }

    /**
     * Creates the land.
     *
     * @param landName the land name
     * @param owner    the owner
     * @param area     the area
     * @param parent   the parent
     * @param areaId   the area id
     * @param uuid     the uuid
     * @param type     the type
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public RealLand createLand(String landName, PlayerContainer owner, Area area, RealLand parent, int areaId, UUID uuid,
                               Type type) throws SecuboidLandException {

        String landNameLower = landName.toLowerCase();
        int genealogy = 0;
        RealLand land;
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
            throw new SecuboidLandException(secuboid, landName, area, LandAction.LAND_ADD, LandError.NAME_IN_USE);
        }

        land = new RealLand(secuboid, landNameLower, landUUID, owner, area, genealogy, parent, areaId, type);

        addLandToList(land);

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
    public boolean removeLand(RealLand land) throws SecuboidLandException {

        if (land == null) {
            return false;
        }

        LandDeleteEvent landEvent = new LandDeleteEvent(land);

        if (!landList.containsKey(land.getName())) {
            return false;
        }

        // If the land has children
        if (!land.getChildren().isEmpty()) {
            throw new SecuboidLandException(secuboid, land.getName(), null, LandAction.LAND_REMOVE, LandError.HAS_CHILDREN);
        }

        // Call Land Event and check if it is cancelled
        secuboid.getServer().getPluginManager().callEvent(landEvent);

        if (landEvent.isCancelled()) {
            return false;
        }

        removeLandFromList(land);
        if (land.getParent() != null) {
            land.getParent().removeChild(land.getUUID());
        }
        secuboid.getStorageThread().removeLand(land);

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
     * @param newName  the new name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean renameLand(String landName, String newName) throws SecuboidLandException {
        RealLand land = getLand(landName);
        return land != null && renameLand(land, newName);
    }

    /**
     * Rename land.
     *
     * @param uuid    the uuid
     * @param newName the new name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean renameLand(UUID uuid, String newName) throws SecuboidLandException {
        RealLand land = getLand(uuid);
        return land != null && renameLand(land, newName);
    }

    /**
     * Rename land.
     *
     * @param land    the land
     * @param newName the new name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean renameLand(RealLand land, String newName) throws SecuboidLandException {

        String oldNameLower = land.getName();
        String newNameLower = newName.toLowerCase();

        if (isNameExist(newNameLower)) {
            throw new SecuboidLandException(secuboid, newNameLower, null, LandAction.LAND_RENAME, LandError.NAME_IN_USE);
        }

        landList.remove(oldNameLower);

        land.setName(newNameLower);
        landList.put(newNameLower, land);

        return true;
    }

    /**
     * Gets the land.
     *
     * @param landName the land name
     * @return the land
     */
    public RealLand getLand(String landName) {
        return landList.get(landName.toLowerCase());
    }

    /**
     * Gets the land.
     *
     * @param uuid the uuid
     * @return the land
     */
    public RealLand getLand(UUID uuid) {
        return landUUIDList.get(uuid);
    }

    /**
     * Gets the land.
     *
     * @param loc the loc
     * @return the land
     */
    public RealLand getLand(Location loc) {
        Area ca;

        if ((ca = getArea(loc)) == null) {
            return null;
        }
        return ca.getLand();
    }

    /**
     * Gets the lands.
     *
     * @return the lands
     */
    public Collection<RealLand> getLands() {
        return landList.values();
    }

    /**
     * Gets the land or outside area.
     *
     * @param loc the loc
     * @return the land or outside area
     */
    public Land getLandOrOutsideArea(Location loc) {
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
    public WorldLand getOutsideArea(Location loc) {
        return getOutsideArea(loc.getWorld().getName());
    }

    /**
     * Gets the outside area.
     *
     * @param worldName the world name
     * @return the outside area
     */
    public WorldLand getOutsideArea(String worldName) {

        String worldNameLower = worldName.toLowerCase();
        WorldLand globalLand = outsideArea.get(worldNameLower);

        // Not exist, create one
        if (globalLand == null) {
            globalLand = new WorldLand(secuboid, worldName);
            outsideArea.get(Config.GLOBAL).getPermissionsFlags().copyPermsFlagsTo(globalLand.getPermissionsFlags());
            outsideArea.put(worldNameLower, globalLand);
        }

        return globalLand;
    }

    /**
     * Gets the lands.
     *
     * @param worldName the world name
     * @param x         the x
     * @param z         the z
     * @return the lands
     */
    public Collection<RealLand> getLands(String worldName, int x, int z) {

        Collection<Area> areas = getAreas(worldName, x, z);
        Collection<RealLand> lands = new HashSet<RealLand>();

        for (Area area : areas) {
            lands.add(area.getLand());
        }

        return lands;
    }

    /**
     * Gets the lands.
     *
     * @param loc the loc
     * @return the lands
     */
    public Collection<RealLand> getLands(Location loc) {

        Collection<Area> areas = getAreas(loc);
        Collection<RealLand> lands = new HashSet<RealLand>();

        for (Area area : areas) {
            lands.add(area.getLand());
        }

        return lands;
    }

    /**
     * Gets the lands.
     *
     * @param owner the owner
     * @return the lands
     */
    public Collection<RealLand> getLands(PlayerContainer owner) {

        Collection<RealLand> lands = new HashSet<RealLand>();

        for (RealLand land : landList.values()) {
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
    public Collection<RealLand> getLands(Type type) {

        Collection<RealLand> lands = new HashSet<RealLand>();

        for (RealLand land : landList.values()) {
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
     * @param pc        the pc
     * @param price     the price
     * @return the price from player
     */
    boolean getPriceFromPlayer(String worldName, PlayerContainer pc, double price) {

        return !(pc.getContainerType() == PlayerContainerType.PLAYER && price > 0)
                || secuboid.getPlayerMoney().getFromPlayer(((PlayerContainerPlayer) pc).getOfflinePlayer(), worldName, price);

    }

    /**
     * Gets the cuboid areas. This method ignore Y value.
     *
     * @param worldName the world name
     * @param x         the x
     * @param z         the z
     * @return the cuboid areas
     */
    public Collection<Area> getAreas(String worldName, int x, int z) {
        return getAreas(worldName, x, 0, z, false);
    }

    /**
     * Gets the cuboid areas.
     *
     * @param loc the loc
     * @return the cuboid areas
     */
    public Collection<Area> getAreas(Location loc) {
        return getAreas(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
    }

    private Collection<Area> getAreas(String worldName, int x, int y, int z, boolean isY) {

        Collection<Area> areas = new ArrayList<Area>();
        int SearchIndex;
        int nbToFind;
        boolean ForwardSearch;
        TreeSet<AreaIndex> ais;
        AreaIndex ai;
        Iterator<AreaIndex> it;

        // First, determinate if what is the highest number between x1, x2, z1 and z2
        if (Math.abs(x) > Math.abs(z)) {
            nbToFind = x;
            if (x < 0) {
                SearchIndex = INDEX_X1;
                ForwardSearch = true;
            } else {
                SearchIndex = INDEX_X2;
                ForwardSearch = false;
            }
        } else {
            nbToFind = z;
            if (z < 0) {
                SearchIndex = INDEX_Z1;
                ForwardSearch = true;
            } else {
                SearchIndex = INDEX_Z2;
                ForwardSearch = false;
            }
        }

        // Now check for area in location
        ais = areaList[SearchIndex].areaMap.get(worldName);
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

            if (isY ? ai.getArea().isLocationInside(worldName, x, y, z) : ai.getArea().isLocationInsideSquare(x, z)) {
                areas.add(ai.getArea());
            }
        }
        return areas;
    }

    /**
     * Gets the cuboid area.
     *
     * @param loc the loc
     * @return the cuboid area
     */
    public Area getArea(Location loc) {

        int actualPrio = Short.MIN_VALUE;
        int curPrio;
        int actualGen = 0;
        int curGen;
        Area actualArea = null;
        Location resLoc; // Resolved location

        // Give the position from the sky to underbedrock if the Y is greater than 255 or lower than 0
        if (loc.getBlockY() >= loc.getWorld().getMaxHeight()) {
            resLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getMaxHeight() - 1, loc.getZ());
        } else if (loc.getBlockY() < 0) {
            resLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
        } else {
            resLoc = loc;
        }

        Collection<Area> areas = getAreas(resLoc);

        // Compare priorities of parents (or main)
        for (Area area : areas) {
            curPrio = area.getLand().getPriority();
            curGen = area.getLand().getGenealogy();

            if (actualPrio < curPrio
                    || (actualPrio == curPrio && actualGen <= curGen)) {
                actualArea = area;
                actualPrio = curPrio;
                actualGen = area.getLand().getGenealogy();
            }
        }

        return actualArea;
    }

    /**
     * Check continue search.
     *
     * @param area        the area
     * @param nbToFind    the nb to find
     * @param SearchIndex the search index
     * @return true, if successful
     */
    private boolean checkContinueSearch(Area area, int nbToFind, int SearchIndex) {

        switch (SearchIndex) {
            case INDEX_X1:
                return nbToFind >= area.getX1();
            case INDEX_X2:
                return nbToFind <= area.getX2();
            case INDEX_Z1:
                return nbToFind >= area.getZ1();
            case INDEX_Z2:
                return nbToFind <= area.getZ2();
            default:
                return false;
        }
    }

    /**
     * Adds the area to list.
     *
     * @param area the area
     */
    void addAreaToList(Area area) {

        if (!areaList[0].areaMap.containsKey(area.getWorldName())) {
            for (int t = 0; t < 4; t++) {
                areaList[t].areaMap.put(area.getWorldName(), new TreeSet<AreaIndex>());
            }
        }
        areaList[INDEX_X1].areaMap.get(area.getWorldName()).add(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].areaMap.get(area.getWorldName()).add(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].areaMap.get(area.getWorldName()).add(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].areaMap.get(area.getWorldName()).add(new AreaIndex(area.getZ2(), area));
    }

    /**
     * Removes the area from list.
     *
     * @param area the area
     */
    void removeAreaFromList(Area area) {
        areaList[INDEX_X1].areaMap.get(area.getWorldName()).remove(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].areaMap.get(area.getWorldName()).remove(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].areaMap.get(area.getWorldName()).remove(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].areaMap.get(area.getWorldName()).remove(new AreaIndex(area.getZ2(), area));
    }

    /**
     * Adds the land to list.
     *
     * @param land the land
     */
    private void addLandToList(RealLand land) {
        landList.put(land.getName(), land);
        landUUIDList.put(land.getUUID(), land);
    }

    /**
     * Removes the land from list.
     *
     * @param land the land
     */
    private void removeLandFromList(RealLand land) {
        landList.remove(land.getName());
        landUUIDList.remove(land.getUUID());
        for (Area area : land.getAreas()) {
            removeAreaFromList(area);
        }
    }

    /**
     * Adds the for sale.
     *
     * @param land the land
     */
    void addForSale(RealLand land) {
        forSale.add(land);
    }

    /**
     * Removes the for sale.
     *
     * @param land the land
     */
    void removeForSale(RealLand land) {
        forSale.remove(land);
    }

    /**
     * Gets the for sale.
     *
     * @return the for sale
     */
    public Collection<RealLand> getForSale() {
        return forSale;
    }

    /**
     * Adds the for rent.
     *
     * @param land the land
     */
    void addForRent(RealLand land) {
        forRent.add(land);
    }

    /**
     * Removes the for rent.
     *
     * @param land the land
     */
    void removeForRent(RealLand land) {
        forRent.remove(land);
    }

    /**
     * Gets the for rent.
     *
     * @return the for rent
     */
    public Collection<RealLand> getForRent() {
        return forRent;
    }
}
