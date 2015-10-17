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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.parameters.FlagType;
import me.tabinol.secuboid.parameters.LandFlag;
import me.tabinol.secuboid.parameters.Permission;
import me.tabinol.secuboid.parameters.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboidapi.event.LandModifyEvent;
import me.tabinol.secuboidapi.event.LandModifyEvent.LandModifyReason;
import me.tabinol.secuboidapi.event.PlayerContainerLandBanEvent;
import me.tabinol.secuboidapi.factions.IFaction;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboidapi.lands.types.IType;
import me.tabinol.secuboidapi.parameters.IFlagType;
import me.tabinol.secuboidapi.parameters.IFlagValue;
import me.tabinol.secuboidapi.parameters.ILandFlag;
import me.tabinol.secuboidapi.parameters.IPermission;
import me.tabinol.secuboidapi.parameters.IPermissionType;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainerPlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * The Class Land.
 */
public class Land extends DummyLand implements ILand {

    /** The Constant DEFAULT_PRIORITY. */
    public static final short DEFAULT_PRIORITY = 10;
    
    /** The Constant MINIM_PRIORITY. */
    public static final short MINIM_PRIORITY = 0;
    
    /** The Constant MAXIM_PRIORITY. */
    public static final short MAXIM_PRIORITY = 100;
    
    /** The uuid. */
    private final UUID uuid;
    
    /** The name. */
    private String name;
    
    /** The type. */
    private IType type = null;
    
    /** The areas. */
    private Map<Integer, ICuboidArea> areas = new TreeMap<Integer, ICuboidArea>();
    
    /** The children. */
    private Map<UUID, ILand> 
    	children = new TreeMap<UUID, ILand>();
    
    /** The priority. */
    private short priority = DEFAULT_PRIORITY; // Do not put more then 100000!!!!
    
    /** The genealogy. */
    private int genealogy = 0; // 0 = first, 1 = child, 2 = child of child, ...
    
    /** The parent. */
    private me.tabinol.secuboidapi.lands.ILand parent = null;
    
    /** The owner. */
    private IPlayerContainer owner;
    
    /** The residents. */
    private Set<IPlayerContainer> residents = new TreeSet<IPlayerContainer>();
    
    /** The banneds. */
    private Set<IPlayerContainer> banneds = new TreeSet<IPlayerContainer>();
    
    /** The auto save. */
    private boolean autoSave = true;
    
    /** The faction territory. */
    private IFaction factionTerritory = null;
    
    /** The money. */
    private double money = 0L;
    
    /** The player notify. */
    private Set<IPlayerContainerPlayer> playerNotify = new TreeSet<IPlayerContainerPlayer>();
    
    /** The players in land. */
    private final Set<Player> playersInLand = new HashSet<Player>();
    // Economy
    /** The for sale. */
    private boolean forSale = false;
    
    /** The for sale sign location */
    private Location forSaleSignLoc = null;
    
    /** The sale price. */
    private double salePrice = 0;
    
    /** The for rent. */
    private boolean forRent = false;
    
    /** The for rent sign location */
    private Location forRentSignLoc = null;
    
    /** The rent price. */
    private double rentPrice = 0;
    
    /** The rent renew. */
    private int rentRenew = 0; // How many days before renew?
    
    /** The rent auto renew. */
    private boolean rentAutoRenew = false;
    
    /** The rented. */
    private boolean rented = false;
    
    /** The tenant. */
    private IPlayerContainerPlayer tenant = null;
    
    /** The last payment. */
    private Timestamp lastPayment = new Timestamp(0);

    // Please use createLand in Lands class to create a Land
    /**
     * Instantiates a new land.
     *
     * @param landName the land name
     * @param uuid the uuid
     * @param owner the owner
     * @param area the area
     * @param genealogy the genealogy
     * @param parent the parent
     * @param areaId the area id
     * @param type the type
     */
	protected Land(String landName, UUID uuid, IPlayerContainer owner,
            ICuboidArea area, int genealogy, Land parent, int areaId, IType type) {

        super(area.getWorldName().toLowerCase());
        this.uuid = uuid;
        name = landName.toLowerCase();
        this.type = type;
        if (parent != null) {
            this.parent = parent;
            parent.addChild(this);
            this.factionTerritory = parent.factionTerritory;
        }
        this.owner = owner;
        this.genealogy = genealogy;
        addArea(area, areaId);
    }

    /**
     * Sets the default.
     */
	public void setDefault() {
        owner = new PlayerContainerNobody();
        residents = new TreeSet<IPlayerContainer>();
        playerNotify = new TreeSet<IPlayerContainerPlayer>();
        permissions = new TreeMap<IPlayerContainer, TreeMap<IPermissionType, IPermission>>();
        flags = new TreeMap<IFlagType, ILandFlag>();
        doSave();
    }


    /**
     * Adds the area.
     *
     * @param area the area
     */
    public void addArea(ICuboidArea area) {

        int nextKey = 0;

        if (areas.isEmpty()) {
            nextKey = 1;
        } else {
            for (int key : areas.keySet()) {

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
     * @param area the area
     * @param price the price
     */
    public void addArea(ICuboidArea area, double price) {

        if(price > 0) {
        	Secuboid.getThisPlugin().iLands().getPriceFromPlayer(worldName, owner, price);
        }
        addArea(area);
    }

    /**
     * Adds the area.
     *
     * @param area the area
     * @param key the key
     */
    public void addArea(ICuboidArea area, int key) {

        ((CuboidArea) area).setLand(this);
        areas.put(key, area);
        Secuboid.getThisPlugin().iLands().addAreaToList(area);
        doSave();
        
        // Start Event
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new LandModifyEvent(this, LandModifyReason.AREA_ADD, area));
    }

    /**
     * Removes the area.
     *
     * @param key the key
     * @return true, if successful
     */
    public boolean removeArea(int key) {

        ICuboidArea area;

        if ((area = areas.remove(key)) != null) {
            Secuboid.getThisPlugin().iLands().removeAreaFromList(area);
            doSave();
            
            // Start Event
            Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                    new LandModifyEvent(this, LandModifyReason.AREA_REMOVE, area));

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
    public boolean removeArea(ICuboidArea area) {

        Integer key = getAreaKey(area);

        if (key != null) {
            return removeArea(key);
        }

        return false;
    }

    /**
     * Replace area.
     *
     * @param key the key
     * @param newArea the new area
     * @param price the price
     * @return true, if successful
     */
    public boolean replaceArea(int key, ICuboidArea newArea, double price) {

        if (price > 0) {
        	Secuboid.getThisPlugin().iLands().getPriceFromPlayer(worldName, owner, price);
        }

        return replaceArea(key, newArea);
    }

    /**
     * Replace area.
     *
     * @param key the key
     * @param newArea the new area
     * @return true, if successful
     */
    public boolean replaceArea(int key, ICuboidArea newArea) {

        ICuboidArea area;

        if ((area = areas.remove(key)) != null) {
            Secuboid.getThisPlugin().iLands().removeAreaFromList(area);
            ((CuboidArea) newArea).setLand(this);
            areas.put(key, newArea);
            Secuboid.getThisPlugin().iLands().addAreaToList(newArea);
            doSave();

            // Start Event
            Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                    new LandModifyEvent(this, LandModifyReason.AREA_REPLACE, area));

            return true;
        }

        return false;
    }

    /**
     * Gets the area.
     *
     * @param key the key
     * @return the area
     */
    public ICuboidArea getArea(int key) {

        return areas.get(key);
    }

    /**
     * Gets the area key.
     *
     * @param area the area
     * @return the area key
     */
    public Integer getAreaKey(ICuboidArea area) {

        for (Map.Entry<Integer, ICuboidArea> entry : areas.entrySet()) {
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
    public Map<Integer, ICuboidArea> getIdsAndAreas() {

        return areas;
    }

    /**
     * Gets the areas.
     *
     * @return the areas
     */
    public Collection<ICuboidArea> getAreas() {

        return areas.values();
    }

    /**
     * Checks if is location inside.
     *
     * @param loc the loc
     * @return true, if is location inside
     */
    public boolean isLocationInside(Location loc) {

        for (ICuboidArea area1 : areas.values()) {
            if (area1.isLocationInside(loc)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the nb blocks outside.
     *
     * @param areaComp the area comp
     * @return the nb blocks outside
     */
    public long getNbBlocksOutside(ICuboidArea areaComp) {

        // Get the Volume of the area
        long volume = areaComp.getTotalBlock();

        // Put the list of areas in the land to an array
        ICuboidArea[] areaAr = areas.values().toArray(new ICuboidArea[0]);

        for (int t = 0; t < areaAr.length; t++) {

            // Get the result collision cuboid
            ICuboidArea colArea = areaAr[t].getCollisionArea(areaComp);

            if (colArea != null) {

                // Substract the volume of collision
                volume -= colArea.getTotalBlock();

                // Compare each next areas to the collision area and add
                // the collision of the collision to cancel multiple subtracts
                for (int a = t + 1; a < areaAr.length; a++) {

                    ICuboidArea colAreaToNextArea = areaAr[a].getCollisionArea(colArea);

                    if (colAreaToNextArea != null) {
                        volume += colAreaToNextArea.getTotalBlock();
                    }
                }
            }
        }

        return volume;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUUID() {

        return uuid;
    }

    /**
     * Sets the name.
     *
     * @param newName the new name
     */
    protected void setName(String newName) {

        setAutoSave(false);
        Secuboid.getThisPlugin().iStorageThread().removeLand(this);
        this.name = newName;
        setAutoSave(true);
        doSave();

        // Start Event
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new LandModifyEvent(this, LandModifyReason.RENAME, name));
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public IPlayerContainer getOwner() {

        return owner;
    }

    /**
     * Checks if is owner.
     *
     * @param player the player
     * @return true, if is owner
     */
    public boolean isOwner(Player player) {

        return owner.hasAccess(player);
    }

    /**
     * Gets the faction territory.
     *
     * @return the faction territory
     */
    public IFaction getFactionTerritory() {

        return factionTerritory;
    }

    /**
     * Sets the faction territory.
     *
     * @param faction the new faction territory
     */
    public void setFactionTerritory(IFaction faction) {

        this.factionTerritory = faction;
        for (ILand child : children.values()) {
            child.setFactionTerritory(faction);
        }
        doSave();

        // Start Event
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new LandModifyEvent(this, LandModifyReason.FACTION_TERRITORY_CHANGE, faction));
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(IPlayerContainer owner) {

        this.owner = owner;
        doSave();

        // Start Event
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new LandModifyEvent(this, LandModifyReason.OWNER_CHANGE, owner));
    }

    /**
     * Adds the resident.
     *
     * @param resident the resident
     */
    public void addResident(IPlayerContainer resident) {

        ((PlayerContainer) resident).setLand(this);
        residents.add(resident);
        doSave();

        // Start Event
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new LandModifyEvent(this, LandModifyReason.RESIDENT_ADD, resident));
    }

    /**
     * Removes the resident.
     *
     * @param resident the resident
     * @return true, if successful
     */
    public boolean removeResident(IPlayerContainer resident) {

        if (residents.remove(resident)) {
            doSave();

            // Start Event
            Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                    new LandModifyEvent(this, LandModifyReason.RESIDENT_REMOVE, resident));
            
            return true;
        }

        return false;
    }

    /**
     * Gets the residents.
     *
     * @return the residents
     */
    public final Set<IPlayerContainer> getResidents() {

        return residents;
    }

    /**
     * Checks if is resident.
     *
     * @param player the player
     * @return true, if is resident
     */
    public boolean isResident(Player player) {

        for (IPlayerContainer resident : residents) {
            if (resident.hasAccess(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the banned.
     *
     * @param banned the banned
     */
    @SuppressWarnings("deprecation")
	public void addBanned(IPlayerContainer banned) {

        ((PlayerContainer) banned).setLand(this);
        banneds.add(banned);
        doSave();

        // Start Event
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new PlayerContainerLandBanEvent(this, banned));
    
        // Deprecated to remove
        Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
                new me.tabinol.secuboid.event.PlayerContainerLandBanEvent((Land) this, (PlayerContainer) banned));
    }

    /**
     * Removes the banned.
     *
     * @param banned the banned
     * @return true, if successful
     */
    public boolean removeBanned(IPlayerContainer banned) {

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
    public final Set<IPlayerContainer> getBanneds() {

        return banneds;
    }

    /**
     * Checks if is banned.
     *
     * @param player the player
     * @return true, if is banned
     */
    public boolean isBanned(Player player) {

        for (IPlayerContainer banned : banneds) {
            if (banned.hasAccess(player)) {
                return true;
            }
        }
        return false;
    }

    // Note : a child get the parent priority
    /**
     * Gets the priority.
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
     * Gets the genealogy.
     *
     * @return the genealogy
     */
    public int getGenealogy() {

        return genealogy;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(short priority) {

        this.priority = priority;
        doSave();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Land getParent() {

        return (Land) parent;
    }

    public void setParent(ILand newParent) {
    	
    	// Remove files
    	removeChildFiles();
		Secuboid.getThisPlugin().iStorageThread().removeLand(name, genealogy);
    	
    	// remove parent (if needed)
    	if(parent != null) {
            ((Land)parent).removeChild(uuid);
            parent = null;
            genealogy = 0;
            Secuboid.getThisPlugin().iLog().write("remove parent from land: " + name);
    	}
    	
    	// Add parent
    	if(newParent != null) {
    		((Land)newParent).addChild(this);
    		parent = newParent;
    		priority = parent.getPriority();
    		genealogy = parent.getGenealogy() + 1;
            Secuboid.getThisPlugin().iLog().write("add parent " + parent.getName() + " to land: " + name);
    	}
    	
    	// Save
    	doSave();
    	
    	// Save children files
    	saveChildFiles();
    }
    
    private void removeChildFiles() {
    	
    	for(ILand child : children.values()) {
    		child.setAutoSave(false);
    		Secuboid.getThisPlugin().iStorageThread().removeLand((Land)child);
    		((Land)child).removeChildFiles();
    	}
    }

    private void saveChildFiles() {
    	
    	for(ILand child : children.values()) {
    		child.setPriority(priority);
    		((Land)child).genealogy = genealogy + 1;
    		child.setAutoSave(true);
    		child.forceSave();
    		((Land)child).saveChildFiles();
    	}
    }

    /**
     * Gets the ancestor.
     *
     * @param gen the gen
     * @return the ancestor
     */
    public Land getAncestor(int gen) { // 1 parent, 2 grand-parent, 3 ...

        Land ancestor = this;

        for (int t = 0; t < gen; t++) {
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    /**
     * Checks if is descendants.
     *
     * @param land the land
     * @return true, if is descendants
     */
    public boolean isDescendants(ILand land) {

        if (land == this) {
            return true;
        }

        for (ILand landT : children.values()) {
            if (landT.isDescendants(land) == true) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds the child.
     *
     * @param land the land
     */
    private void addChild(Land land) {

        children.put(land.uuid, land);
        doSave();
    }

    /**
     * Removes the child.
     *
     * @param uuid the uuid
     */
    protected void removeChild(UUID uuid) {

        children.remove(uuid);
        doSave();
    }

    /**
     * Gets the child.
     *
     * @param uuid the uuid
     * @return the child
     */
    public Land getChild(UUID uuid) {

        return (Land) children.get(uuid);
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Collection<ILand> getChildren() {

        return children.values();
    }

    /**
     * Sets the auto save.
     *
     * @param autoSave the new auto save
     */
    public void setAutoSave(boolean autoSave) {

        this.autoSave = autoSave;
    }

    /**
     * Force save.
     */
    public void forceSave() {

        Secuboid.getThisPlugin().iStorageThread().saveLand(this);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.lands.DummyLand#doSave()
     */
    @Override
    protected void doSave() {

        if (autoSave) {
            forceSave();
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.lands.ILand#addPermission(me.tabinol.secuboidapi.playercontainer.IPlayerContainer, me.tabinol.secuboidapi.parameters.IPermissionType, boolean, boolean)
     */
    public void addPermission(IPlayerContainer pc, IPermissionType permType,
    		boolean value, boolean inheritance) {
    	
    	addPermission(pc, new Permission((PermissionType) permType, value, inheritance));
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboidapi.lands.ILand#addFlag(me.tabinol.secuboidapi.parameters.IFlagType, java.lang.Object, boolean)
     */
    public void addFlag(IFlagType flagType, Object value, boolean inheritance) {

    	addFlag(new LandFlag((FlagType) flagType, value, inheritance));
    }
    
    /**
     * Check land permission and inherit.
     *
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the boolean
     */
    protected Boolean checkLandPermissionAndInherit(Player player, IPermissionType pt, boolean onlyInherit) {

        Boolean permValue;

        if ((permValue = getPermission(player, pt, onlyInherit)) != null) {
            return permValue;
        } else if (parent != null) {
            return ((Land) parent).checkPermissionAndInherit(player, pt, true);
        }

        return Secuboid.getThisPlugin().iLands().getPermissionInWorld(worldName, player, pt, true);
    }

    /**
     * Gets the land flag and inherit.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the land flag value
     */
    protected IFlagValue getLandFlagAndInherit(IFlagType ft, boolean onlyInherit) {

        IFlagValue flagValue;

        if ((flagValue = getFlag(ft, onlyInherit)) != null) {
            return flagValue;
        } else if (parent != null) {
            return ((Land) parent).getFlagAndInherit(ft, true);
        }

        return Secuboid.getThisPlugin().iLands().getFlagInWorld(worldName, ft, true);
    }

    /**
     * Adds the money.
     *
     * @param money the money
     */
    public void addMoney(double money) {

        this.money += money;
        doSave();
    }

    /**
     * Substract money.
     *
     * @param money the money
     */
    public void substractMoney(double money) {

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
    public void addPlayerNotify(IPlayerContainerPlayer player) {

        playerNotify.add(player);
        doSave();
    }

    /**
     * Removes the player notify.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerNotify(IPlayerContainerPlayer player) {

        boolean ret = playerNotify.remove(player);
        doSave();

        return ret;
    }

    /**
     * Checks if is player notify.
     *
     * @param player the player
     * @return true, if is player notify
     */
    public boolean isPlayerNotify(IPlayerContainerPlayer player) {

        return playerNotify.contains(player);
    }

    /**
     * Gets the players notify.
     *
     * @return the players notify
     */
    public Set<IPlayerContainerPlayer> getPlayersNotify() {

        return playerNotify;
    }

    /**
     * Adds the player in land.
     *
     * @param player the player
     */
    public void addPlayerInLand(Player player) {

        playersInLand.add(player);
    }

    /**
     * Removes the player in land.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerInLand(Player player) {

        return playersInLand.remove(player);
    }

    // No parent verify
    /**
     * Checks if is player in land.
     *
     * @param player the player
     * @return true, if is player in land
     */
    public boolean isPlayerInLand(Player player) {

        return playersInLand.contains(player);
    }

    /**
     * Checks if is playerin land no vanish.
     *
     * @param player the player
     * @param fromPlayer the from player
     * @return true, if is playerin land no vanish
     */
    public boolean isPlayerinLandNoVanish(Player player, Player fromPlayer) {

        if (playersInLand.contains(player)
                && (!Secuboid.getThisPlugin().iPlayerConf().isVanished(player) 
                		|| Secuboid.getThisPlugin().iPlayerConf().get(fromPlayer).isAdminMod())) {
            return true;
        }

        // Check Chidren
        for (ILand landChild : children.values()) {
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
    	
    	Set<Player> playLandChild = new HashSet<Player>();
    	
    	playLandChild.addAll(playersInLand);
    	
    	for(ILand child : children.values()) {
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
    public Set<Player> getPlayersInLandNoVanish(Player fromPlayer) {

        Set<Player> playerList = new HashSet<Player>();

        for (Player player : playersInLand) {
            if (!Secuboid.getThisPlugin().iPlayerConf().isVanished(player) || Secuboid.getThisPlugin().iPlayerConf().get(fromPlayer).isAdminMod()) {
                playerList.add(player);
            }
        }
        for (ILand landChild : children.values()) {
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
     * @param signLoc the sign location
     */
    public void setForSale(boolean isForSale, double salePrice, Location signLoc) {

        forSale = isForSale;
        if (forSale) {
            this.salePrice = salePrice;
            this.forSaleSignLoc = signLoc;
            Secuboid.getThisPlugin().iLands().addForSale(this);
        } else {
            this.salePrice = 0;
            this.forSaleSignLoc = null;
            Secuboid.getThisPlugin().iLands().removeForSale(this);
        }
        doSave();
    }

    public Location getSaleSignLoc() {
    	
    	return forSaleSignLoc;
    }

    public void setSaleSignLoc(Location forSaleSignLoc) {
    	
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
     * @param rentPrice the rent price
     * @param rentRenew the rent renew
     * @param rentAutoRenew the rent auto renew
     * @param signLoc the sign location
     */
    public void setForRent(double rentPrice, int rentRenew, boolean rentAutoRenew, Location signLoc) {

        forRent = true;
        this.rentPrice = rentPrice;
        this.rentRenew = rentRenew;
        this.rentAutoRenew = rentAutoRenew;
        this.forRentSignLoc = signLoc;
        Secuboid.getThisPlugin().iLands().addForRent(this);
        doSave();
    }

    public Location getRentSignLoc() {
    	
    	return forRentSignLoc;
    }
    
    public void setRentSignLoc(Location forRentSignLoc) {
    	
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
        Secuboid.getThisPlugin().iLands().removeForRent(this);
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
    public void setRented(IPlayerContainerPlayer tenant) {

        rented = true;
        this.tenant = tenant;
        updateRentedPayment(); // doSave() done in this method
    }

    /**
     * Update rented payment.
     */
    public void updateRentedPayment() {

        lastPayment = new Timestamp(new Date().getTime());
        doSave();
    }

    /**
     * Un set rented.
     */
    public void unSetRented() {

        rented = false;
        tenant = null;
        lastPayment = new Timestamp(0);
        doSave();
    }

    /**
     * Gets the tenant.
     *
     * @return the tenant
     */
    public IPlayerContainerPlayer getTenant() {

        return tenant;
    }

    /**
     * Checks if is tenant.
     *
     * @param player the player
     * @return true, if is tenant
     */
    public boolean isTenant(Player player) {

        return rented && tenant.hasAccess(player);
    }

    /**
     * Sets the last payment time.
     *
     * @param lastPayment the new last payment time
     */
    public void setLastPaymentTime(Timestamp lastPayment) {
    	
    	this.lastPayment = lastPayment;
    	doSave();
    }
    
    /**
     * Gets the last payment time.
     *
     * @return the last payment time
     */
    public Timestamp getLastPaymentTime() {

        return lastPayment;
    }

	@Override
	public IType getType() {

		return type;
	}

	@Override
	public void setType(IType arg0) {
		
		type = arg0;
		doSave();
	}
}
