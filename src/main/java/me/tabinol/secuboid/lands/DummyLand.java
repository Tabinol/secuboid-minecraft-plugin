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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboidapi.SecuboidAPI;
import me.tabinol.secuboidapi.event.LandModifyEvent;
import me.tabinol.secuboidapi.event.LandModifyEvent.LandModifyReason;
import me.tabinol.secuboidapi.event.PlayerContainerAddNoEnterEvent;
import me.tabinol.secuboidapi.lands.IDummyLand;
import me.tabinol.secuboidapi.parameters.IFlagType;
import me.tabinol.secuboidapi.parameters.IFlagValue;
import me.tabinol.secuboidapi.parameters.ILandFlag;
import me.tabinol.secuboidapi.parameters.IPermission;
import me.tabinol.secuboidapi.parameters.IPermissionType;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;

import org.bukkit.World;
import org.bukkit.entity.Player;


/**
 * The Class DummyLand.
 */
public class DummyLand implements IDummyLand {

    /** The permissions. */
    protected TreeMap<IPlayerContainer, TreeMap<IPermissionType, IPermission>> permissions; // String for playerName
    
    /** The flags. */
    protected TreeMap<IFlagType, ILandFlag> flags;
    
    /** The world name. */
    protected String worldName;

    /**
     * Instantiates a new dummy land.
     *
     * @param worldName the world name
     */
    public DummyLand(String worldName) {

        permissions = new TreeMap<IPlayerContainer, TreeMap<IPermissionType, IPermission>>();
        flags = new TreeMap<IFlagType, ILandFlag>();
        this.worldName = worldName;
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
     * Gets the world.
     *
     * @return the world
     */
    public World getWorld() {

        return Secuboid.getThisPlugin().getServer().getWorld(worldName);
    }

    public void copyPermsFlagsTo(IDummyLand desLand) {
    	
    	// copy permissions
    	for(Map.Entry<IPlayerContainer, TreeMap<IPermissionType, IPermission>> pcEntry : permissions.entrySet()) {
    		
    		TreeMap<IPermissionType, IPermission> perms = new TreeMap<IPermissionType, IPermission>(); 
    		for(Map.Entry<IPermissionType, IPermission> permEntry : pcEntry.getValue().entrySet()) {
    			perms.put(permEntry.getKey(), permEntry.getValue().copyOf());
    		}
    		((DummyLand) desLand).permissions.put(pcEntry.getKey(), perms);
    	}

    	// copy flags
    	for(Map.Entry<IFlagType, ILandFlag> flagEntry : flags.entrySet()) {
    		
    		((DummyLand) desLand).flags.put(flagEntry.getKey(), flagEntry.getValue().copyOf());
    	}
    }
    
    /**
     * Adds the permission.
     *
     * @param pc the pc
     * @param perm the perm
     */
	public void addPermission(IPlayerContainer pc, IPermission perm) {

        TreeMap<IPermissionType, IPermission> permPlayer;

        if (this instanceof Land) {
            ((PlayerContainer)pc).setLand((Land) this);
        }
        
        if (!permissions.containsKey(pc)) {
            permPlayer = new TreeMap<IPermissionType, IPermission>();
            permissions.put(pc, permPlayer);
        } else {
            permPlayer = permissions.get(pc);
        }
        permPlayer.put(perm.getPermType(), perm);
        doSave();

        if (this instanceof Land) {
        	if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
        			&& perm.getValue() != perm.getPermType().getDefaultValue()) {
                
        		// Start Event for kick
        		Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
        				new PlayerContainerAddNoEnterEvent((Land) this, pc));
        	}

        	// Start Event
        	Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
        			new LandModifyEvent((Land) this, LandModifyReason.PERMISSION_SET, perm));
        }
    }

    /**
     * Removes the permission.
     *
     * @param pc the pc
     * @param permType the perm type
     * @return true, if successful
     */
    public boolean removePermission(IPlayerContainer pc, 
    		me.tabinol.secuboidapi.parameters.IPermissionType permType) {

        TreeMap<IPermissionType, IPermission> permPlayer;
        IPermission perm;

        if (!permissions.containsKey(pc)) {
            return false;
        }
        permPlayer = permissions.get(pc);
        perm = permPlayer.remove(permType);
        if (perm == null) {
            return false;
        }

        // remove key for PC if it is empty
        if (permPlayer.isEmpty()) {
            permissions.remove(pc);
        }

        doSave();

    	if(this instanceof Land) {
    		// Start Event
    		Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
    				new LandModifyEvent((Land) this, LandModifyReason.PERMISSION_UNSET, perm));
    	}

    	return true;
    }

    /**
     * Gets the sets the pc have permission.
     *
     * @return the sets the pc have permission
     */
    public final Set<IPlayerContainer> getSetPCHavePermission() {

        return permissions.keySet();
    }

    /**
     * Gets the permissions for pc.
     *
     * @param pc the pc
     * @return the permissions for pc
     */
    public final Collection<IPermission> getPermissionsForPC(IPlayerContainer pc) {

        return permissions.get(pc).values();
    }

    /**
     * Check permission and inherit.
     *
     * @param player the player
     * @param pt the pt
     * @return the boolean
     */
    public boolean checkPermissionAndInherit(Player player, 
    		me.tabinol.secuboidapi.parameters.IPermissionType pt) {

        return checkPermissionAndInherit(player, pt, false);
    }

    /**
     * Check permission no inherit.
     *
     * @param player the player
     * @param pt the pt
     * @return the boolean
     */
    public boolean checkPermissionNoInherit(Player player, 
    		me.tabinol.secuboidapi.parameters.IPermissionType pt) {

        Boolean value = getPermission(player, pt, false);
        
        if(value != null) {
        	return value;
        } else {
        	return pt.getDefaultValue();
        }
    }

    /**
     * Check permission and inherit.
     *
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the boolean
     */
    protected Boolean checkPermissionAndInherit(Player player, 
    		me.tabinol.secuboidapi.parameters.IPermissionType pt, boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).checkLandPermissionAndInherit(player, pt, onlyInherit);
        }
        return Secuboid.getThisPlugin().iLands().getPermissionInWorld(worldName, player, pt, onlyInherit);
    }

    /**
     * Gets the permission.
     *
     * @param player the player
     * @param pt the pt
     * @param onlyInherit the only inherit
     * @return the permission
     */
    protected Boolean getPermission(Player player, 
    		me.tabinol.secuboidapi.parameters.IPermissionType pt, boolean onlyInherit) {
    	
    	return getPermission(player, pt, onlyInherit, null);
    }
    
    // Land parameter is only to paste to default parameters for a land
    private Boolean getPermission(Player player, 
    		me.tabinol.secuboidapi.parameters.IPermissionType pt, boolean onlyInherit, Land land) {

        for (Map.Entry<IPlayerContainer, TreeMap<IPermissionType, IPermission>> permissionEntry : permissions.entrySet()) {
            boolean value;
        	if(land != null) {
            	value = permissionEntry.getKey().hasAccess(player, land);
            } else {
            	value = permissionEntry.getKey().hasAccess(player);
            }
        	if (value) {
                IPermission perm = permissionEntry.getValue().get(pt);
                if (perm != null) {
                    Secuboid.getThisPlugin().iLog().write("Container: " + permissionEntry.getKey().toString() + ", PermissionType: " + perm.getPermType() + ", Value: " + perm.getValue() + ", Heritable: " + perm.isHeritable());
                    if ((onlyInherit && perm.isHeritable()) || !onlyInherit) {
                        return perm.getValue();
                    }
                }
            }
        }

        // Check in default permissions
        if(!onlyInherit && this instanceof Land) {

            return ((Lands) SecuboidAPI.iLands()).getDefaultConf(((Land) this).getType()).getPermission(
            		player, pt, onlyInherit, (Land) this);
        }
        
        return null;
    }

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(ILandFlag flag) {

        flags.put(flag.getFlagType(), flag);
        doSave();

    	if(this instanceof Land) {
    		// Start Event
    		Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
    				new LandModifyEvent((Land) this, LandModifyReason.FLAG_SET, flag));
    	}
    }

    /**
     * Removes the flag.
     *
     * @param flagType the flag type
     * @return true, if successful
     */
    public boolean removeFlag(IFlagType flagType) {

        ILandFlag flag = flags.remove(flagType);
    	
    	if (flag == null) {
            return false;
        }
        doSave();
    	
        if(this instanceof Land) {
    		// Start Event
    		Secuboid.getThisPlugin().getServer().getPluginManager().callEvent(
    				new LandModifyEvent((Land) this, LandModifyReason.FLAG_UNSET, flag));
    	}

    	return true;
    }

    /**
     * Gets the flags.
     *
     * @return the flags value or default
     */
    public Collection<ILandFlag> getFlags() {

        return flags.values();
    }

    public IFlagValue getFlagAndInherit(IFlagType ft) {

        return getFlagAndInherit(ft, false);
    }

    /**
     * Gets the flag no inherit.
     *
     * @param ft the ft
     * @return the flag value or default
     */
    public IFlagValue getFlagNoInherit(IFlagType ft) {

        IFlagValue value = getFlag(ft, false);
        
        if(value != null) {
        	return value;
        } else {
        	return ft.getDefaultValue();
        }
    }

    /**
     * Gets the flag and inherit.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag and inherit
     */
    protected IFlagValue getFlagAndInherit(IFlagType ft, 
    		boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).getLandFlagAndInherit(ft, onlyInherit);
        }
        return Secuboid.getThisPlugin().iLands().getFlagInWorld(worldName, ft, onlyInherit);
    }

    /**
     * Gets the flag.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag value
     */
    protected IFlagValue getFlag(IFlagType ft, boolean onlyInherit) {

    	ILandFlag flag = flags.get(ft);
        if (flag != null) {
            Secuboid.getThisPlugin().iLog().write("Flag: " + flag.toString());

            if ((onlyInherit && flag.isHeritable()) || !onlyInherit) {
                return flag.getValue();
            }
        }

        // Check in default flags
        if(!onlyInherit && this instanceof Land) {

        	return ((Lands) SecuboidAPI.iLands()).getDefaultConf(((Land) this).getType()).getFlag(ft, onlyInherit);
        }

        return null;
    }

    /**
     * Do save.
     */
    protected void doSave() {

        if (this instanceof Land) {
            ((Land) this).doSave();
        }
    }
}
