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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.PlayerContainerLandBanEvent;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.approve.Approvable;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.FlagList;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboid.playercontainer.PlayerContainerOwner;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerResident;
import me.tabinol.secuboid.playercontainer.PlayerContainerTenant;
import me.tabinol.secuboid.storage.Savable;
import me.tabinol.secuboid.storage.SavableParameter;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;

/**
 * Represents a land with a number of areas.
 *
 * @author tabinol
 */
public final class Land implements Savable, Approvable {

    /**
     * The Constant DEFAULT_PRIORITY.
     */
    private static final short DEFAULT_PRIORITY = 10;

    /**
     * The Constant MINIM_PRIORITY.
     */
    public static final short MINIM_PRIORITY = 0;

    /**
     * The Constant MAXIM_PRIORITY.
     */
    public static final short MAXIM_PRIORITY = 100;

    private final Secuboid secuboid;

    /**
     * The uuid.
     */
    private final UUID uuid;

    /**
     * The name.
     */
    private String name;

    private boolean isApproved;

    /**
     * The type.
     */
    private Type type = null;

    /**
     * The areas. TreeMap because the order must be respected.
     */
    private final Map<Integer, Area> areas = new TreeMap<>();

    /**
     * The children.
     */
    private final Map<UUID, Land> children = new HashMap<>();

    /**
     * The priority.
     */
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!

    /**
     * The parent.
     */
    private Land parent = null;

    /**
     * The world name. <br>
     * Why using the world string name, but not World instance or UUID? <br>
     * The uuid or the World instance can be changed if there is a land regen.
     */
    private final String worldName;

    /**
     * The owner.
     */
    private PlayerContainer owner;

    private final LandPermissionsFlags landPermissionsFlags;

    /**
     * The residents. TreeSet because it is ordered.
     */
    private Set<PlayerContainer> residents = new TreeSet<>();

    /**
     * The banneds. TreeSet because it is ordered.
     */
    private final Set<PlayerContainer> banneds = new TreeSet<>();

    /**
     * The auto save.
     */
    private boolean autoSave = true;

    /**
     * The money.
     */
    private double money = 0L;

    /**
     * The player notify. TreeSet because it is ordered.
     */
    private Set<PlayerContainerPlayer> playerNotify = new TreeSet<>();

    /**
     * The players in land.
     */
    private final Set<Player> playersInLand = new HashSet<>();
    // Economy
    /**
     * The for sale.
     */
    private boolean forSale = false;

    /**
     * The for sale sign location
     */
    private Location forSaleSignLoc = null;

    /**
     * The sale price.
     */
    private double salePrice = 0;

    /**
     * The for rent.
     */
    private boolean forRent = false;

    /**
     * The for rent sign location
     */
    private Location forRentSignLoc = null;

    /**
     * The rent price.
     */
    private double rentPrice = 0;

    /**
     * The rent renew.
     */
    private int rentRenew = 0; // How many days before renew?

    /**
     * The rent auto renew.
     */
    private boolean rentAutoRenew = false;

    /**
     * The rented.
     */
    private boolean rented = false;

    /**
     * The tenant.
     */
    private PlayerContainerPlayer tenant = null;

    /**
     * The last payment.
     */
    private long lastPayment = 0;

    /**
     * Instantiates a new real land. <br>
     * IMPORTANT: Please use createLand in Lands class to create a Land or it will
     * not be accessible.
     *
     * @param secuboid   secuboid instance
     * @param landName   the land name
     * @param uuid       the uuid
     * @param isApproved is this land is approved or in approve list
     * @param owner      the owner
     * @param area       the area
     * @param parent     the parent
     * @param areaId     the area id
     * @param type       the type
     */
    Land(final Secuboid secuboid, final String landName, final UUID uuid, final boolean isApproved,
            final PlayerContainer owner, final Area area, final Land parent, final int areaId, final Type type) {

        // TODO Add Skip flat save where it is needed
        this.secuboid = secuboid;
        this.uuid = uuid;
        this.isApproved = isApproved;
        name = landName.toLowerCase();
        this.type = type;
        if (parent != null && isApproved) {
            this.parent = parent;
            parent.addChild(this);
        }
        this.owner = owner;
        worldName = area.getWorldName();
        landPermissionsFlags = new LandPermissionsFlags(secuboid, this, worldName);
        if (isApproved) {
            // A approved should have only an approved area.
            area.setApproved();
        }
        addArea(area, areaId);
    }

    public LandPermissionsFlags getPermissionsFlags() {
        return landPermissionsFlags;
    }

    @Override
    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public void setApproved() {
        // Set approved
        isApproved = true;

        // Add to parent
        if (parent != null) {
            parent.addChild(this);
        }

        // Activate new area (should has only one)
        for (final Area area : areas.values()) {
            area.setApproved();
            activateArea(area);
        }
    }

    public void setApproved(final double price) {
        secuboid.getLands().getPriceFromPlayer(worldName, owner, price);
        setApproved();
    }

    /**
     * Sets the land default values.
     */
    public void setDefault() {
        owner = PlayerContainerNobody.getInstance();
        residents = new TreeSet<>();
        playerNotify = new TreeSet<>();
        landPermissionsFlags.setDefault(true);
        doSave(SaveActionEnum.LAND_SAVE, false);
    }

    /**
     * Adds the area.
     *
     * @param area the area
     */
    public void addArea(final Area area) {

        int nextKey = 0;

        if (areas.isEmpty()) {
            nextKey = 1;
        } else {
            for (final int key : areas.keySet()) {

                if (nextKey < key) {
                    nextKey = key;
                }
            }
            nextKey++;
        }

        addArea(area, nextKey);
    }

    /**
     * Adds the area.
     *
     * @param area  the area
     * @param price the price
     */
    public void addArea(final Area area, final double price) {

        secuboid.getLands().getPriceFromPlayer(worldName, owner, price);
        addArea(area);
    }

    /**
     * Adds the area.
     *
     * @param area the area
     * @param key  the key
     */
    public void addArea(final Area area, final int key) {

        area.setLand(this);
        areas.put(key, area);
        activateArea(area);
    }

    /**
     * Approves the area.
     *
     * @param areaId the area id
     * @param price  the price
     * @throws SecuboidLandException
     */
    public void approveArea(final int areaId, final double price) throws SecuboidLandException {
        final Area area = getArea(areaId);
        if (area == null) {
            throw new SecuboidLandException(
                    String.format("The area %d does not exist in land \"%s\", \"%s\"", areaId, name, uuid));
        }
        secuboid.getLands().getPriceFromPlayer(worldName, owner, price);
        area.setApproved();
        activateArea(area);
    }

    private void activateArea(final Area area) {
        if (isApproved && area.isApproved()) {
            // Add area for get location only if the land and the area are approved.
            secuboid.getLands().addAreaToList(area);
        }
        doSave(SaveActionEnum.LAND_AREA_SAVE, false, area);

        // Start Event
        secuboid.getServer().getPluginManager()
                .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.AREA_ADD, area));
    }

    /**
     * Removes the area.
     *
     * @param key the key
     * @return true, if successful
     */
    public boolean removeArea(final int key) {

        Area area;

        if ((area = areas.remove(key)) != null) {
            secuboid.getLands().removeAreaFromList(area);
            doSave(SaveActionEnum.LAND_AREA_REMOVE, false, area);

            // Start Event
            secuboid.getServer().getPluginManager()
                    .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.AREA_REMOVE, area));

            return true;
        }

        return false;
    }

    /**
     * Removes the area.
     *
     * @param area the area
     * @return true, if successful
     */
    public boolean removeArea(final Area area) {
        final Integer key = getAreaKey(area);
        return key != null && removeArea(key);
    }

    /**
     * Replace area.
     *
     * @param key     the key
     * @param newArea the new area
     * @param price   the price
     * @return true, if successful
     */
    public boolean replaceArea(final int key, final Area newArea, final double price) {
        secuboid.getLands().getPriceFromPlayer(worldName, owner, price);
        return replaceArea(key, newArea);
    }

    /**
     * Replace area.
     *
     * @param key     the key
     * @param newArea the new area
     * @return true, if successful
     */
    public boolean replaceArea(final int key, final Area newArea) {
        Area area;

        if ((area = areas.remove(key)) != null) {
            secuboid.getLands().removeAreaFromList(area);
            newArea.setLand(this);
            areas.put(key, newArea);
            newArea.setApproved();
            secuboid.getLands().addAreaToList(newArea);
            doSave(SaveActionEnum.LAND_AREA_REMOVE, true, area);
            doSave(SaveActionEnum.LAND_AREA_SAVE, false, newArea);

            // Start Event
            secuboid.getServer().getPluginManager()
                    .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.AREA_REPLACE, area));

            return true;
        }

        return false;
    }

    /**
     * Replace approve replace area.
     *
     * @param key        the key
     * @param newAreaKey the new area id
     * @param price      the price
     * @return true, if successful
     * @throws SecuboidLandException
     */
    public boolean approveReplaceArea(final int key, final int newAreaId, final double price)
            throws SecuboidLandException {
        if (getArea(key) == null) {
            throw new SecuboidLandException(
                    String.format("The area to replace %d does not exist in land \"%s\", \"%s\"", key, name, uuid));
        }
        final Area newArea = getArea(newAreaId);
        if (newArea == null) {
            throw new SecuboidLandException(
                    String.format("The new area %d does not exist in land \"%s\", \"%s\"", newAreaId, name, uuid));
        }

        // Remove the "new" area to replace the actual area
        setAutoSave(false);
        removeArea(newArea);
        setAutoSave(true);
        return replaceArea(key, newArea, price);
    }

    /**
     * Gets the area.
     *
     * @param key the key
     * @return the area
     */
    public Area getArea(final int key) {
        return areas.get(key);
    }

    /**
     * Gets the area key.
     *
     * @param area the area
     * @return the area key
     */
    public Integer getAreaKey(final Area area) {
        for (final Map.Entry<Integer, Area> entry : areas.entrySet()) {
            if (entry.getValue() == area) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Gets the areas key.
     *
     * @return the areas key
     */
    public Set<Integer> getAreasKey() {
        return areas.keySet();
    }

    /**
     * Gets the ids and areas.
     *
     * @return the ids and areas
     */
    public Map<Integer, Area> getIdsAndAreas() {
        return areas;
    }

    /**
     * Gets the areas.
     *
     * @return the areas
     */
    public Collection<Area> getAreas() {
        return areas.values();
    }

    /**
     * Gets the world.
     *
     * @return the world
     */
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Check if the location is inside the land. This method does not check for Y
     * value.
     *
     * @param world the string world
     * @param x     the x
     * @param z     the z
     * @return true if inside the land
     */
    public boolean isLocationInside(final String world, final int x, final int z) {
        for (final Area area1 : areas.values()) {
            if (area1.isLocationInside(world, x, z)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the location is inside the land.
     *
     * @param loc the location
     * @return true if inside the land
     */
    public boolean isLocationInside(final Location loc) {
        return isLocationInside(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Check if the location is inside the land.
     *
     * @param world the string world
     * @param x     the x
     * @param y     the y
     * @param z     the z
     * @return true if inside the land
     */
    public boolean isLocationInside(final String world, final int x, final int y, final int z) {
        for (final Area area1 : areas.values()) {
            if (area1.isLocationInside(world, x, y, z)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    @Override
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Sets the name.
     *
     * @param newName the new name
     */
    protected void setName(final String newName) {
        this.name = newName;
        doSave(SaveActionEnum.LAND_SAVE, false);

        // Start Event
        secuboid.getServer().getPluginManager()
                .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.RENAME, name));
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public PlayerContainer getOwner() {
        return owner;
    }

    /**
     * Checks if the player is owner with inheritance.
     *
     * @param player the player
     * @return true, if is owner
     */
    public boolean isOwner(final Player player) {
        if (PlayerContainerOwner.getInstance().hasAccess(player, landPermissionsFlags)) {
            return true;
        }
        return parent != null
                && landPermissionsFlags.getFlagAndInherit(FlagList.INHERIT_OWNER.getFlagType()).getValueBoolean()
                && parent.isOwner(player);
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(final PlayerContainer owner) {
        this.owner = owner;

        // Reset all owner permissions and resident managers for safety
        // TODO skip flat for each remove perm, skip database for final save
        landPermissionsFlags.removeAllPermissionsType(PermissionList.LAND_OWNER.getPermissionType());
        landPermissionsFlags.removeAllPermissionsType(PermissionList.RESIDENT_MANAGER.getPermissionType());

        doSave();

        // Start Event
        secuboid.getServer().getPluginManager()
                .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.OWNER_CHANGE, owner));
    }

    /**
     * Adds the resident.
     *
     * @param resident the resident
     */
    public void addResident(final PlayerContainer resident) {
        residents.add(resident);
        doSave();

        // Start Event
        secuboid.getServer().getPluginManager()
                .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.RESIDENT_ADD, resident));
    }

    /**
     * Removes the resident.
     *
     * @param resident the resident
     * @return true, if successful
     */
    public boolean removeResident(final PlayerContainer resident) {
        if (residents.remove(resident)) {
            doSave();

            // Start Event
            secuboid.getServer().getPluginManager()
                    .callEvent(new LandModifyEvent(this, LandModifyEvent.LandModifyReason.RESIDENT_REMOVE, resident));

            return true;
        }

        return false;
    }

    /**
     * Gets the residents.
     *
     * @return the residents
     */
    public final Set<PlayerContainer> getResidents() {
        return residents;
    }

    /**
     * Checks if the player is resident with inheritance.
     *
     * @param player the player
     * @return true, if is resident
     */
    public boolean isResident(final Player player) {
        if (PlayerContainerResident.getInstance().hasAccess(player, landPermissionsFlags)) {
            return true;
        }
        return parent != null
                && landPermissionsFlags.getFlagAndInherit(FlagList.INHERIT_RESIDENTS.getFlagType()).getValueBoolean()
                && parent.isResident(player);
    }

    /**
     * Adds the banned.
     *
     * @param banned the banned
     */
    public void addBanned(final PlayerContainer banned) {
        banneds.add(banned);
        doSave();

        // Start Event
        secuboid.getServer().getPluginManager().callEvent(new PlayerContainerLandBanEvent(this, banned));
    }

    /**
     * Removes the banned.
     *
     * @param banned the banned
     * @return true, if successful
     */
    public boolean removeBanned(final PlayerContainer banned) {
        if (banneds.remove(banned)) {
            doSave();
            return true;
        }

        return false;
    }

    /**
     * Gets the banneds.
     *
     * @return the banneds
     */
    public final Set<PlayerContainer> getBanneds() {
        return banneds;
    }

    /**
     * Checks if is banned with inheritance.
     *
     * @param player the player
     * @return true, if is banned
     */
    public boolean isBanned(final Player player) {
        for (final PlayerContainer banned : banneds) {
            if (banned.hasAccess(player, landPermissionsFlags)) {
                return true;
            }
        }
        return parent != null && parent.isBanned(player);
    }

    /**
     * Gets the priority. A child get the parent priority.
     *
     * @return the priority
     */
    public short getPriority() {
        if (parent != null) {
            return parent.getPriority();
        }

        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(final short priority) {
        this.priority = priority;
        doSave();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Land getParent() {
        return parent;
    }

    /**
     * Gets the first parent in the line (or this land).
     * 
     * @return the first parent
     */
    public Land getFirstParent() {
        Land firstParent = this;
        Land nextParent;
        while ((nextParent = firstParent.parent) != null) {
            firstParent = nextParent;
        }
        return firstParent;
    }

    /**
     * Sets the land parent.
     *
     * @param newParent the land parent
     */
    public void setParent(final Land newParent) {
        // remove parent (if needed)
        if (parent != null) {
            parent.removeChild(uuid);
            parent = null;
        }

        // Add parent
        if (newParent != null) {
            newParent.addChild(this);
            parent = newParent;
            priority = parent.getPriority();
        }

        // Save
        doSave();

        // Save children files
        changeChildrenPriority();
    }

    private void changeChildrenPriority() {
        for (final Land child : children.values()) {
            child.setPriority(priority);
            child.changeChildrenPriority();
        }
    }

    /**
     * Checks if is descendants.
     *
     * @param land the land
     * @return true, if is descendants
     */
    public boolean isDescendants(final Land land) {

        if (land.equals(this)) {
            return true;
        }

        for (final Land landT : children.values()) {
            if (landT.isDescendants(land)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the land is parent, grand-parent or ancestor.
     *
     * @param land the land
     * @return true, if is ancestor
     */
    public boolean isParentOrAncestor(final Land land) {
        if (parent == null) {
            return false;
        }

        if (parent.equals(land)) {
            return true;
        }

        return parent.isParentOrAncestor(land);
    }

    /**
     * Adds the child.
     *
     * @param land the land
     */
    private void addChild(final Land land) {
        children.put(land.uuid, land);
        doSave();
    }

    /**
     * Removes the child.
     *
     * @param uuid the uuid
     */
    void removeChild(final UUID uuid) {
        children.remove(uuid);
        doSave();
    }

    /**
     * Gets the child.
     *
     * @param uuid the uuid
     * @return the child
     */
    public Land getChild(final UUID uuid) {
        return children.get(uuid);
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Collection<Land> getChildren() {
        return children.values();
    }

    /**
     * Gets the auto save.
     *
     * @return true if the auto save is on
     */
    boolean getAutoSave() {
        return autoSave;
    }

    /**
     * Sets the auto save.
     *
     * @param autoSave the new auto save
     */
    private void setAutoSave(final boolean autoSave) {
        this.autoSave = autoSave;
    }

    private void doSave(final SaveActionEnum SaveActionEnum, final boolean skipFlatSave, final SavableParameter... savableParameters) {
        if (autoSave) {
            secuboid.getStorageThread().addSaveAction(SaveActionEnum, skipFlatSave, Optional.of(this),
                    savableParameters);
        }
    }

    /**
     * Adds the money.
     *
     * @param money the money
     */
    public void addMoney(final double money) {
        this.money += money;
        doSave();
    }

    /**
     * Subtracts money.
     *
     * @param money the money
     */
    public void subtractMoney(final double money) {
        this.money -= money;
        doSave();
    }

    /**
     * Gets the money.
     *
     * @return the money
     */
    public double getMoney() {
        return money;
    }

    /**
     * Adds the player notify.
     *
     * @param player the player
     */
    public void addPlayerNotify(final PlayerContainerPlayer player) {
        playerNotify.add(player);
        doSave();
    }

    /**
     * Removes the player notify.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerNotify(final PlayerContainerPlayer player) {
        final boolean ret = playerNotify.remove(player);
        doSave();

        return ret;
    }

    /**
     * Checks if is player notify.
     *
     * @param player the player
     * @return true, if is player notify
     */
    public boolean isPlayerNotify(final PlayerContainerPlayer player) {
        return playerNotify.contains(player);
    }

    /**
     * Gets the players notify.
     *
     * @return the players notify
     */
    public Set<PlayerContainerPlayer> getPlayersNotify() {
        return playerNotify;
    }

    /**
     * Adds the player in land.
     *
     * @param player the player
     */
    public void addPlayerInLand(final Player player) {
        playersInLand.add(player);
    }

    /**
     * Removes the player in land.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerInLand(final Player player) {
        return playersInLand.remove(player);
    }

    /**
     * Checks if is player in land. No parent verify.
     *
     * @param player the player
     * @return true, if is player in land
     */
    public boolean isPlayerInLand(final Player player) {
        return playersInLand.contains(player);
    }

    /**
     * Checks if is playerin land no vanish.
     *
     * @param player     the player
     * @param fromPlayer the from player
     * @return true, if is playerin land no vanish
     */
    public boolean isPlayerinLandNoVanish(final Player player, final Player fromPlayer) {

        if (playersInLand.contains(player) && (!secuboid.getPlayerConf().isVanished(player)
                || secuboid.getPlayerConf().get(fromPlayer).isAdminMode())) {
            return true;
        }

        // Check Chidren
        for (final Land landChild : children.values()) {
            if (landChild.isPlayerinLandNoVanish(player, fromPlayer)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the players in land.
     *
     * @return the players in land
     */
    public Set<Player> getPlayersInLand() {
        return playersInLand;
    }

    /**
     * Gets the players in land and children.
     *
     * @return the players in land and children
     */
    public Set<Player> getPlayersInLandAndChildren() {
        final Set<Player> playLandChild = new HashSet<>();

        playLandChild.addAll(playersInLand);

        for (final Land child : children.values()) {
            playLandChild.addAll(child.getPlayersInLandAndChildren());
        }

        return playLandChild;
    }

    /**
     * Gets the players in land no vanish.
     *
     * @param fromPlayer the from player
     * @return the players in land no vanish
     */
    public Set<Player> getPlayersInLandNoVanish(final Player fromPlayer) {
        final Set<Player> playerList = new HashSet<>();

        for (final Player player : playersInLand) {
            if (!secuboid.getPlayerConf().isVanished(player)
                    || secuboid.getPlayerConf().get(fromPlayer).isAdminMode()) {
                playerList.add(player);
            }
        }
        for (final Land landChild : children.values()) {
            playerList.addAll(landChild.getPlayersInLandNoVanish(fromPlayer));
        }

        return playerList;
    }

    /**
     * Checks if is for sale.
     *
     * @return true, if is for sale
     */
    public boolean isForSale() {
        return forSale;
    }

    /**
     * Sets the for sale.
     *
     * @param isForSale the is for sale
     * @param salePrice the sale price
     * @param signLoc   the sign location
     */
    public void setForSale(final boolean isForSale, final double salePrice, final Location signLoc) {
        forSale = isForSale;
        if (forSale) {
            this.salePrice = salePrice;
            this.forSaleSignLoc = signLoc;
            secuboid.getLands().addForSale(this);
        } else {
            this.salePrice = 0;
            this.forSaleSignLoc = null;
            secuboid.getLands().removeForSale(this);
        }
        doSave();
    }

    /**
     * Gets the location of sale sign.
     *
     * @return the location
     */
    public Location getSaleSignLoc() {
        return forSaleSignLoc;
    }

    /**
     * Sets the location of sale sign. Do not use if the sign is not here.
     *
     * @param forSaleSignLoc the sale sign location
     */
    public void setSaleSignLoc(final Location forSaleSignLoc) {
        this.forSaleSignLoc = forSaleSignLoc;
        doSave();
    }

    /**
     * Gets the sale price.
     *
     * @return the sale price
     */
    public double getSalePrice() {
        return salePrice;
    }

    /**
     * Checks if is for rent.
     *
     * @return true, if is for rent
     */
    public boolean isForRent() {
        return forRent;
    }

    /**
     * Sets the for rent.
     *
     * @param rentPrice     the rent price
     * @param rentRenew     the rent renew
     * @param rentAutoRenew the rent auto renew
     * @param signLoc       the sign location
     */
    public void setForRent(final double rentPrice, final int rentRenew, final boolean rentAutoRenew,
            final Location signLoc) {
        forRent = true;
        this.rentPrice = rentPrice;
        this.rentRenew = rentRenew;
        this.rentAutoRenew = rentAutoRenew;
        this.forRentSignLoc = signLoc;
        secuboid.getLands().addForRent(this);
        doSave();
    }

    /**
     * Gets the location of rent sign.
     *
     * @return the location
     */
    public Location getRentSignLoc() {
        return forRentSignLoc;
    }

    /**
     * Sets the location of rent sign. Do not use if the sign is not here.
     *
     * @param forRentSignLoc the rent sign location
     */
    public void setRentSignLoc(final Location forRentSignLoc) {
        this.forRentSignLoc = forRentSignLoc;
        doSave();
    }

    /**
     * Un set for rent.
     */
    public void unSetForRent() {
        forRent = false;
        rentPrice = 0;
        rentRenew = 0;
        rentAutoRenew = false;
        forRentSignLoc = null;
        secuboid.getLands().removeForRent(this);
        doSave();
    }

    /**
     * Gets the rent price.
     *
     * @return the rent price
     */
    public double getRentPrice() {
        return rentPrice;
    }

    /**
     * Gets the rent renew.
     *
     * @return the rent renew
     */
    public int getRentRenew() {
        return rentRenew;
    }

    /**
     * Gets the rent auto renew.
     *
     * @return the rent auto renew
     */
    public boolean getRentAutoRenew() {
        return rentAutoRenew;
    }

    /**
     * Checks if is rented.
     *
     * @return true, if is rented
     */
    public boolean isRented() {

        return rented;
    }

    /**
     * Sets the rented.
     *
     * @param tenant the new rented
     */
    public void setRented(final PlayerContainerPlayer tenant) {
        rented = true;
        this.tenant = tenant;
        lastPayment = System.currentTimeMillis();
        landPermissionsFlags.removeAllPermissionsType(PermissionList.LAND_TENANT.getPermissionType());
        doSave();
    }

    /**
     * Un set rented.
     */
    public void unSetRented() {

        rented = false;
        tenant = null;
        lastPayment = 0;
        landPermissionsFlags.removeAllPermissionsType(PermissionList.LAND_TENANT.getPermissionType());
        doSave();
    }

    /**
     * Gets the tenant.
     *
     * @return the tenant
     */
    public PlayerContainerPlayer getTenant() {
        return tenant;
    }

    /**
     * Checks if is tenant with inheritance.
     *
     * @param player the player
     * @return true, if is tenant
     */
    public boolean isTenant(final Player player) {
        if (tenant != null && PlayerContainerTenant.getInstance().hasAccess(player, landPermissionsFlags)) {
            return true;
        }
        return parent != null
                && landPermissionsFlags.getFlagAndInherit(FlagList.INHERIT_TENANT.getFlagType()).getValueBoolean()
                && parent.isTenant(player);
    }

    /**
     * Sets the last payment time.
     *
     * @param lastPayment the new last payment time
     */
    public void setLastPaymentTime(final long lastPayment) {
        this.lastPayment = lastPayment;
        doSave();
    }

    /**
     * Gets the last payment time.
     *
     * @return the last payment time
     */
    public long getLastPaymentTime() {
        return lastPayment;
    }

    /**
     * Gets the land type.
     *
     * @return the land type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the land type.
     *
     * @param arg0 the land type
     */
    public void setType(final Type arg0) {
        type = arg0;
        doSave();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Land)) {
            return false;
        }
        final Land land = (Land) o;
        return Objects.equals(uuid, land.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
