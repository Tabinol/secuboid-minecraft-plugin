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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Location;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.WorldConfig;
import me.tabinol.secuboid.events.LandDeleteEvent;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.approve.Approves;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaIndex;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.lands.collisions.Collisions.LandError;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.storage.StorageThread.SaveOn;

/**
 * The Class Lands manager.
 */
public final class Lands {

    private static final int INDEX_X1 = 0;
    private static final int INDEX_Z1 = 1;
    private static final int INDEX_X2 = 2;
    private static final int INDEX_Z2 = 3;

    private final Secuboid secuboid;
    private final WorldConfig worldConfig;

    /**
     * Area list put in an area for finding lands.
     */
    private class AreaMap {
        TreeMap<String, TreeSet<AreaIndex>> worldToAreaIndex;
    }

    /**
     * INDEX first, Tree by worlds (then by Areas).
     */
    private final AreaMap[] areaList;

    /**
     * The land uuid list.
     */
    private final SortedMap<UUID, Land> landUUIDList;

    /**
     * The land list, sorted by names.
     */
    private final SortedMap<String, Land> landList;

    /**
     * The approve list isntance.
     */
    private final Approves approves;

    /**
     * List of forSale.
     */
    private final Set<Land> forSale;

    /**
     * List of forRent and rented.
     */
    private final Set<Land> forRent;

    /**
     * Instantiates a new lands manager.
     *
     * @param secuboid    secuboid instance
     * @param worldConfig the world config
     * @param approveList the approve list
     */
    public Lands(final Secuboid secuboid, final WorldConfig worldConfig, final Approves approves) {

        this.secuboid = secuboid;
        this.worldConfig = worldConfig;
        this.approves = approves;
        areaList = new AreaMap[4];

        for (int i = 0; i < 4; i++) {
            areaList[i] = new AreaMap();
            areaList[i].worldToAreaIndex = new TreeMap<>();
        }

        landList = new TreeMap<>();
        landUUIDList = new TreeMap<>();
        forSale = new HashSet<>();
        forRent = new HashSet<>();
    }

    /**
     * Gets the the default configuration for a land type.
     *
     * @param typeNullable the land type nullable
     * @return the default land permissions flags configuration
     */
    public LandPermissionsFlags getDefaultConf(final Type typeNullable) {
        // No type? Return default config
        if (typeNullable == null) {
            return worldConfig.getDefaultPermissionsFlags();
        }

        // Type not found? Return default config
        final LandPermissionsFlags landPermissionsFlags = worldConfig.getTypeToDefaultPermissionsFlags()
                .get(typeNullable);
        if (landPermissionsFlags == null) {
            return worldConfig.getDefaultPermissionsFlags();
        }

        return landPermissionsFlags;
    }

    /**
     * Gets the approves.
     *
     * @return the approves
     */
    public Approves getApproves() {
        return approves;
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
    public Land createLand(final String landName, final PlayerContainer owner, final Area area)
            throws SecuboidLandException {
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
    public Land createLand(final String landName, final PlayerContainer owner, final Area area, final Land parent)
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
    public Land createLand(final String landName, final PlayerContainer owner, final Area area, final Land parent,
            final double price, final Type type) throws SecuboidLandException {
        getPriceFromPlayer(area.getWorldName(), owner, price);
        return createLand(landName, true, owner, area, parent, 1, null, type);
    }

    /**
     * Creates the land. (Internal only)
     *
     * @param landName   the land name
     * @param isApproved if the land is approved
     * @param owner      the owner
     * @param area       the area
     * @param parent     the parent
     * @param areaId     the area id
     * @param uuid       the uuid
     * @param type       the type
     * @return the land
     * @throws SecuboidLandException the secuboid land exception
     */
    public Land createLand(final String landName, final boolean isApproved, final PlayerContainer owner,
            final Area area, final Land parent, final int areaId, final UUID uuid, final Type type)
            throws SecuboidLandException {

        final String landNameLower = landName.toLowerCase();
        final Land land;
        final UUID landUUID;

        if (uuid == null) {
            landUUID = UUID.randomUUID();
        } else {
            landUUID = uuid;
        }

        if (isNameExist(landName)) {
            throw new SecuboidLandException(secuboid, landName, area, LandAction.LAND_ADD, LandError.NAME_IN_USE);
        }

        land = new Land(secuboid, landNameLower, landUUID, isApproved, owner, type, area.getWorldName());
        land.init(area, areaId, parent);
        addLandToList(land);

        return land;
    }

    /**
     * Checks if is name exist.
     *
     * @param landName the land name
     * @return true, if is name exist
     */
    public boolean isNameExist(final String landName) {
        return landList.containsKey(landName.toLowerCase());
    }

    /**
     * Removes the land.
     *
     * @param land the land
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean removeLand(final Land land) throws SecuboidLandException {

        if (land == null) {
            return false;
        }

        final LandDeleteEvent landEvent = new LandDeleteEvent(land);

        if (!landList.containsKey(land.getName())) {
            return false;
        }

        // If the land has children
        if (!land.getChildren().isEmpty()) {
            throw new SecuboidLandException(secuboid, land.getName(), null, LandAction.LAND_REMOVE,
                    LandError.HAS_CHILDREN);
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
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.LAND_REMOVE, SaveOn.BOTH, Optional.of(land));

        return true;
    }

    /**
     * Removes the land.
     *
     * @param landName the land name
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean removeLand(final String landName) throws SecuboidLandException {
        return removeLand(landList.get(landName.toLowerCase()));
    }

    /**
     * Removes the land.
     *
     * @param uuid the uuid
     * @return true, if successful
     * @throws SecuboidLandException the secuboid land exception
     */
    public boolean removeLand(final UUID uuid) throws SecuboidLandException {
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
    public boolean renameLand(final String landName, final String newName) throws SecuboidLandException {
        final Land land = getLand(landName);
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
    public boolean renameLand(final UUID uuid, final String newName) throws SecuboidLandException {
        final Land land = getLand(uuid);
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
    public boolean renameLand(final Land land, final String newName) throws SecuboidLandException {
        final String oldNameLower = land.getName();
        final String newNameLower = newName.toLowerCase();

        if (isNameExist(newNameLower)) {
            throw new SecuboidLandException(secuboid, newNameLower, null, LandAction.LAND_RENAME,
                    LandError.NAME_IN_USE);
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
    public Land getLand(final String landName) {
        return landList.get(landName.toLowerCase());
    }

    /**
     * Gets the land.
     *
     * @param uuid the uuid
     * @return the land
     */
    public Land getLand(final UUID uuid) {
        return landUUIDList.get(uuid);
    }

    /**
     * Gets the land.
     *
     * @param loc the loc
     * @return the land
     */
    public Land getLand(final Location loc) {
        final Area ca;

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
    public Collection<Land> getLands() {
        return landList.values();
    }

    /**
     * Gets the land or world permissions flags.
     *
     * @param loc the loc
     * @return the land or outside area
     */
    public LandPermissionsFlags getPermissionsFlags(final Location loc) {
        final Land land;

        if ((land = getLand(loc)) != null) {
            return land.getPermissionsFlags();
        }

        return getOutsideLandPermissionsFlags(loc);
    }

    /**
     * Gets the outside area permissions flags.
     *
     * @param loc the loc
     * @return the outside area permissions flags
     */
    public LandPermissionsFlags getOutsideLandPermissionsFlags(final Location loc) {
        return getOutsideLandPermissionsFlags(loc.getWorld().getName());
    }

    /**
     * Gets the outside area permissions flags (Global if null).
     *
     * @param worldNameNullable the world name
     * @return the outside area permissions flags
     */
    public LandPermissionsFlags getOutsideLandPermissionsFlags(final String worldNameNullable) {
        if (worldNameNullable == null) {
            return worldConfig.getGlobalPermissionsFlags();
        }
        final String worldNameLower = worldNameNullable.toLowerCase();
        final LandPermissionsFlags worldPermissionsFlags = worldConfig.getWorldNameToPermissionsFlags()
                .get(worldNameLower);
        if (worldPermissionsFlags != null) {
            // Found world specific
            return worldPermissionsFlags;
        }
        // Return just the global
        return worldConfig.getGlobalPermissionsFlags();
    }

    /**
     * Gets the lands.
     *
     * @param worldName the world name
     * @param x         the x
     * @param z         the z
     * @return the lands
     */
    public Set<Land> getLands(final String worldName, final int x, final int z) {

        final List<Area> areas = getAreas(worldName, x, z);
        final Set<Land> lands = new HashSet<>();

        for (final Area area : areas) {
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
    public Set<Land> getLands(final Location loc) {

        final List<Area> areas = getAreas(loc);
        final Set<Land> lands = new HashSet<>();

        for (final Area area : areas) {
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
    public Set<Land> getLands(final PlayerContainer owner) {

        final Set<Land> lands = new HashSet<>();

        for (final Land land : landList.values()) {
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
    public Set<Land> getLands(final Type type) {

        final Set<Land> lands = new HashSet<>();

        for (final Land land : landList.values()) {
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
     * @return true, if successful
     */
    boolean getPriceFromPlayer(final String worldName, final PlayerContainer pc, final double price) {
        if (price == 0 || !secuboid.getPlayerMoneyOpt().isPresent()
                || pc.getContainerType() != PlayerContainerType.PLAYER) {
            // No transaction
            return true;
        }
        return secuboid.getPlayerMoneyOpt().get().getFromPlayer(((PlayerContainerPlayer) pc).getOfflinePlayer(),
                worldName, price);
    }

    /**
     * Gets the cuboid areas. This method ignore Y value.
     *
     * @param worldName the world name
     * @param x         the x
     * @param z         the z
     * @return the cuboid areas
     */
    public List<Area> getAreas(final String worldName, final int x, final int z) {
        return getAreas(worldName, x, 0, z, false);
    }

    /**
     * Gets the cuboid areas.
     *
     * @param loc the loc
     * @return the cuboid areas
     */
    public List<Area> getAreas(final Location loc) {
        return getAreas(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), true);
    }

    private List<Area> getAreas(final String worldName, final int x, final int y, final int z, final boolean isY) {

        final List<Area> areas = new ArrayList<>();
        final int searchIndex;
        final int nbToFind;
        final boolean forwardSearch;
        final TreeSet<AreaIndex> ais;
        final Iterator<AreaIndex> it;
        AreaIndex ai;

        // First, determinate if what is the highest number between x1, x2, z1 and z2
        if (Math.abs(x) > Math.abs(z)) {
            nbToFind = x;
            if (x < 0) {
                searchIndex = INDEX_X1;
                forwardSearch = true;
            } else {
                searchIndex = INDEX_X2;
                forwardSearch = false;
            }
        } else {
            nbToFind = z;
            if (z < 0) {
                searchIndex = INDEX_Z1;
                forwardSearch = true;
            } else {
                searchIndex = INDEX_Z2;
                forwardSearch = false;
            }
        }

        // Now check for area in location
        ais = areaList[searchIndex].worldToAreaIndex.get(worldName);
        if (ais == null || ais.isEmpty()) {
            return areas;
        }
        if (forwardSearch) {
            it = ais.iterator();
        } else {
            it = ais.descendingIterator();
        }

        // Adds all areas to the list
        while (it.hasNext() && checkContinueSearch((ai = it.next()).getArea(), nbToFind, searchIndex)) {

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
    public Area getArea(final Location loc) {

        int actualPrio = Short.MIN_VALUE;
        int curPrio;
        Area actualArea = null;
        Location resLoc; // Resolved location

        // Give the position from the sky to underbedrock if the Y is greater than 255
        // or lower than 0
        if (loc.getBlockY() >= loc.getWorld().getMaxHeight()) {
            resLoc = new Location(loc.getWorld(), loc.getX(), loc.getWorld().getMaxHeight() - 1d, loc.getZ());
        } else if (loc.getBlockY() < 0) {
            resLoc = new Location(loc.getWorld(), loc.getX(), 0, loc.getZ());
        } else {
            resLoc = loc;
        }

        final Collection<Area> areas = getAreas(resLoc);

        // Compare priorities of parents (or main)
        for (final Area area : areas) {
            curPrio = area.getLand().getPriority();

            if (actualArea == null || actualPrio < curPrio
                    || (actualPrio == curPrio && area.getLand().isParentOrAncestor(actualArea.getLand()))) {
                actualArea = area;
                actualPrio = curPrio;
            }
        }

        return actualArea;
    }

    /**
     * Check continue search.
     *
     * @param area        the area
     * @param nbToFind    the nb to find
     * @param searchIndex the search index
     * @return true, if successful
     */
    private boolean checkContinueSearch(final Area area, final int nbToFind, final int searchIndex) {

        switch (searchIndex) {
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
    void addAreaToList(final Area area) {

        if (!areaList[0].worldToAreaIndex.containsKey(area.getWorldName())) {
            for (int t = 0; t < 4; t++) {
                areaList[t].worldToAreaIndex.put(area.getWorldName(), new TreeSet<AreaIndex>());
            }
        }
        areaList[INDEX_X1].worldToAreaIndex.get(area.getWorldName()).add(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].worldToAreaIndex.get(area.getWorldName()).add(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].worldToAreaIndex.get(area.getWorldName()).add(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].worldToAreaIndex.get(area.getWorldName()).add(new AreaIndex(area.getZ2(), area));
    }

    /**
     * Removes the area from list.
     *
     * @param area the area
     */
    void removeAreaFromList(final Area area) {
        areaList[INDEX_X1].worldToAreaIndex.get(area.getWorldName()).remove(new AreaIndex(area.getX1(), area));
        areaList[INDEX_Z1].worldToAreaIndex.get(area.getWorldName()).remove(new AreaIndex(area.getZ1(), area));
        areaList[INDEX_X2].worldToAreaIndex.get(area.getWorldName()).remove(new AreaIndex(area.getX2(), area));
        areaList[INDEX_Z2].worldToAreaIndex.get(area.getWorldName()).remove(new AreaIndex(area.getZ2(), area));
    }

    /**
     * Adds the land to list.
     *
     * @param land the land
     */
    private void addLandToList(final Land land) {
        landList.put(land.getName(), land);
        landUUIDList.put(land.getUUID(), land);
    }

    /**
     * Removes the land from list.
     *
     * @param land the land
     */
    private void removeLandFromList(final Land land) {
        landList.remove(land.getName());
        landUUIDList.remove(land.getUUID());
        for (final Area area : land.getAreas()) {
            removeAreaFromList(area);
        }
    }

    /**
     * Adds the for sale.
     *
     * @param land the land
     */
    void addForSale(final Land land) {
        forSale.add(land);
    }

    /**
     * Removes the for sale.
     *
     * @param land the land
     */
    void removeForSale(final Land land) {
        forSale.remove(land);
    }

    /**
     * Gets the for sale.
     *
     * @return the for sale
     */
    public Collection<Land> getForSale() {
        return forSale;
    }

    /**
     * Adds the for rent.
     *
     * @param land the land
     */
    void addForRent(final Land land) {
        forRent.add(land);
    }

    /**
     * Removes the for rent.
     *
     * @param land the land
     */
    void removeForRent(final Land land) {
        forRent.remove(land);
    }

    /**
     * Gets the for rent.
     *
     * @return the for rent
     */
    public Set<Land> getForRent() {
        return forRent;
    }
}
