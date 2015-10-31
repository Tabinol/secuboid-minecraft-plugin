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
package me.tabinol.secuboidapi.lands;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiType;
import me.tabinol.secuboidapi.parameters.ApiFlagType;
import me.tabinol.secuboidapi.parameters.ApiLandFlag;
import me.tabinol.secuboidapi.parameters.ApiPermissionType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerPlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * The Interface ApiLand. Represent a land.
 */
public interface ApiLand extends ApiDummyLand {

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(ApiType type);
    
    /**
     * Gets the type.
     *
     * @return the type
     */
    public ApiType getType();
    
    /**
     * Restore the land to default configuration. 
     * WARNING: The owner will be "nobody" and
     * all permissions and flags will be restored to default!
     */
    public void setDefault();

    /**
     * Adds an area.
     *
     * @param area the area
     */
    public void addArea(ApiCuboidArea area);
    
    /**
     * Removes an area.
     *
     * @param key the area key
     * @return true, if successful
     */
    public boolean removeArea(int key);
    
    /**
     * Removes an area.
     *
     * @param area the area
     * @return true, if successful
     */
    public boolean removeArea(ApiCuboidArea area);
    
    /**
     * Replace an area.
     *
     * @param key the area key
     * @param newArea the new area
     * @return true, if successful
     */
    public boolean replaceArea(int key, ApiCuboidArea newArea);
    
    /**
     * Gets an area from a key.
     *
     * @param key the key
     * @return the area
     */
    public ApiCuboidArea getArea(int key);
    
    /**
     * Gets a key from an area.
     *
     * @param area the area
     * @return the area key
     */
    public Integer getAreaKey(ApiCuboidArea area);
    
    /**
     * Gets all areas key.
     *
     * @return areas key
     */
    public Set<Integer> getAreasKey();
    
    /**
     * Gets all keys and areas.
     *
     * @return keys and areas
     */
    public Map<Integer, ApiCuboidArea> getIdsAndAreas();
    
    /**
     * Gets all areas.
     *
     * @return areas
     */
    public Collection<ApiCuboidArea> getAreas();
    
    /**
     * Checks if is location inside this land.
     *
     * @param loc the location
     * @return true, if is location inside
     */
    public boolean isLocationInside(Location loc);
    
    /**
     * Gets the number of blocks of the area areaComp outside of this land 
     *
     * @param areaComp the cuboid area
     * @return the number of blocks outside
     */
    public long getNbBlocksOutside(ApiCuboidArea areaComp);
    
    /**
     * Gets the land name.
     *
     * @return the name
     */
    public String getName();
    
    /**
     * Gets the land uuid.
     *
     * @return the uuid
     */
    public UUID getUUID();
    
    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public ApiPlayerContainer getOwner();
    
    /**
     * Checks if is owner.
     *
     * @param player the player
     * @return true, if is owner
     */
    public boolean isOwner(Player player);
    
    /**
     * Adds a permission.
     *
     * @param pc the pc
     * @param permType the permission type
     * @param value the value
     * @param inheritance the inheritance (normally true)
     */
    public void addPermission(ApiPlayerContainer pc, ApiPermissionType permType,
            boolean value, boolean inheritance);
    
    /**
     * Removes a permission.
     *
     * @param pc the player container
     * @param permType the permission type
     * @return true, if successful
     */
    public boolean removePermission(ApiPlayerContainer pc, ApiPermissionType permType);

    /**
     * Adds a flag.
     *
     * @param flagType the flag type
     * @param value the value must be the same type has the value declared
     * in flag type : Double, Boolean, String or String[]
     * @param inheritance the inheritance (normally true)
     */
    public void addFlag(ApiFlagType flagType, Object value, boolean inheritance);
    
    /**
     * Adds a flag.
     *
     * @param LandFlag the flag type
     */
    public void addFlag(ApiLandFlag LandFlag);
    
    /**
     * Removes a flag.
     *
     * @param flagType the flag type
     * @return true, if successful
     */
    public boolean removeFlag(ApiFlagType flagType);

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(ApiPlayerContainer owner);
    
    /**
     * Adds a resident.
     *
     * @param resident the resident
     */
    public void addResident(ApiPlayerContainer resident);
    
    /**
     * Removes a resident.
     *
     * @param resident the resident
     * @return true, if successful
     */
    public boolean removeResident(ApiPlayerContainer resident);
    
    /**
     * Gets all residents.
     *
     * @return the residents
     */
    public Set<ApiPlayerContainer> getResidents();
    
    /**
     * Checks if this player is resident.
     *
     * @param player the player
     * @return true, if is resident
     */
    public boolean isResident(Player player);
    
    /**
     * Adds a banned.
     *
     * @param banned the banned
     */
    public void addBanned(ApiPlayerContainer banned);
    
    /**
     * Removes a banned.
     *
     * @param banned the banned
     * @return true, if successful
     */
    public boolean removeBanned(ApiPlayerContainer banned);
    
    /**
     * Gets all banneds.
     *
     * @return the banneds
     */
    public Set<ApiPlayerContainer> getBanneds();
    
    /**
     * Checks if this player is banned.
     *
     * @param player the player
     * @return true, if is banned
     */
    public boolean isBanned(Player player);
    
    // Note : a child get the parent priority
    /**
     * Gets the land priority.
     *
     * @return the priority
     */
    public short getPriority();
    
    /**
     * Gets the land genealogy.
     *
     * @return the genealogy
     */
    public int getGenealogy();
    
    /**
     * Sets land priority.
     *
     * @param priority the new priority
     */
    public void setPriority(short priority);
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public ApiLand getParent();
    
    /**
     * Sets the parent land
     * 
     * @param parent the parent land (or null for no parent)
     */
    public void setParent(ApiLand parent);
    
    /**
     * Gets a ancestor.
     *
     * @param gen the generation upside (1 = parent, 2 = grand-parent, ...)
     * @return the ancestor
     */
    public ApiLand getAncestor(int gen);
    
    /**
     * Checks if the land in parameter is a descendant of this land
     *
     * @param land the land
     * @return true, if is descendants
     */
    public boolean isDescendants(ApiLand land);
    
    /**
     * Gets a land child from the UUID.
     *
     * @param uuid the uuid
     * @return the child
     */
    public ApiLand getChild(UUID uuid);
    
    /**
     * Gets all children.
     *
     * @return children
     */
    public Collection<ApiLand> getChildren();
    
    /**
     * Enable or disable auto save. PLEASE USE WITH CAUTION!
     *
     * @param autoSave if auto save or not
     */
    public void setAutoSave(boolean autoSave);
    
    /**
     * Force save.
     */
    public void forceSave();

    /**
     * Adds money to the land.
     *
     * @param money the money
     */
    public void addMoney(double money);
    
    /**
     * Substract money from the land.
     *
     * @param money the money
     */
    public void substractMoney(double money);
    
    /**
     * Gets the money balance.
     *
     * @return the money
     */
    public double getMoney();
    
    /**
     * Adds a player notify to notify list.
     *
     * @param player the player
     */
    public void addPlayerNotify(ApiPlayerContainerPlayer player);
    
    /**
     * Removes a player from notify list.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayerNotify(ApiPlayerContainerPlayer player);
    
    /**
     * Checks if is player in notify list.
     *
     * @param player the player
     * @return true, if is player notify
     */
    public boolean isPlayerNotify(ApiPlayerContainerPlayer player);
    
    /**
     * Gets all players notify list.
     *
     * @return the players notify
     */
    public Set<ApiPlayerContainerPlayer> getPlayersNotify();
    
    /**
     * Checks if the player is inside the land. Does not verify in children
     * lands.
     *
     * @param player the player
     * @return true, if is player in land
     */
    public boolean isPlayerInLand(Player player);
    
    /**
     * Checks if the player is in land and can be visible from the other player.
     *
     * @param player the player
     * @param fromPlayer the player to know if he has permission to see
     * @return true, if player is in land
     */
    public boolean isPlayerinLandNoVanish(Player player, Player fromPlayer);
    
    /**
     * Gets the players in land. This method does not look in children lands
     *
     * @return the players in land
     */
    public Set<Player> getPlayersInLand();
    
    /**
     * Gets the players in land and children.
     *
     * @return the players in land and children
     */
    public Set<Player> getPlayersInLandAndChildren();
    
    /**
     * Gets all players in land and can be visible from the other player.
     *
     * @param fromPlayer the player to know if he has permission to see
     * @return the players in land
     */
    public Set<Player> getPlayersInLandNoVanish(Player fromPlayer);
    
    /**
     * Checks if the land is for sale.
     *
     * @return true, if is for sale
     */
    public boolean isForSale();
    
    /**
     * Gets the sale sign location.
     *
     * @return the sale sign location
     */
    public Location getSaleSignLoc();
    
    /**
     * Gets the sale price.
     *
     * @return the sale price
     */
    public double getSalePrice();
    
    /**
     * Checks if the land is for rent.
     *
     * @return true, if is for rent
     */
    public boolean isForRent();
    
    /**
     * Gets the rent sign location.
     *
     * @return the rent sign location
     */
    public Location getRentSignLoc();
    
    /**
     * Gets the rent price.
     *
     * @return the rent price
     */
    public double getRentPrice();
    
    /**
     * Gets the rent renew.
     *
     * @return the rent renew
     */
    public int getRentRenew();
    
    /**
     * Gets the rent auto renew.
     *
     * @return the rent auto renew
     */
    public boolean getRentAutoRenew();
    
    /**
     * Checks if is rented.
     *
     * @return true, if is rented
     */
    public boolean isRented();
    
    /**
     * Gets the tenant.
     *
     * @return the tenant
     */
    public ApiPlayerContainerPlayer getTenant();
    
    /**
     * Checks if the player is tenant.
     *
     * @param player the player
     * @return true, if is tenant
     */
    public boolean isTenant(Player player);
    
    /**
     * Gets the last payment time.
     *
     * @return the last payment time
     */
    public Timestamp getLastPaymentTime();
}
