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
import me.tabinol.secuboid.events.LandModifyEvent;
import me.tabinol.secuboid.events.LandModifyEvent.LandModifyReason;
import me.tabinol.secuboid.events.PlayerContainerAddNoEnterEvent;
import me.tabinol.secuboid.parameters.FlagType;
import me.tabinol.secuboid.parameters.FlagValue;
import me.tabinol.secuboid.parameters.LandFlag;
import me.tabinol.secuboid.parameters.Permission;
import me.tabinol.secuboid.parameters.PermissionType;

import org.bukkit.World;
import org.bukkit.entity.Player;


/**
 * The Class DummyLand.
 */
public class DummyLand {

    /** The permissions. */
    protected TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>> permissions; // String for playerName
    
    /** The flags. */
    protected TreeMap<FlagType, LandFlag> flags;
    
    /** The world name. */
    protected String worldName;

    /**
     * Instantiates a new dummy land.
     *
     * @param worldName the world name
     */
    public DummyLand(String worldName) {

        permissions = new TreeMap<PlayerContainer, TreeMap<PermissionType, Permission>>();
        flags = new TreeMap<FlagType, LandFlag>();
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

    public void copyPermsFlagsTo(DummyLand desLand) {
        
        // copy permissions
        for(Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> pcEntry : permissions.entrySet()) {
            
            TreeMap<PermissionType, Permission> perms = new TreeMap<PermissionType, Permission>();
            for(Map.Entry<PermissionType, Permission> permEntry : pcEntry.getValue().entrySet()) {
                perms.put(permEntry.getKey(), permEntry.getValue().copyOf());
            }
            ((DummyLand) desLand).permissions.put(pcEntry.getKey(), perms);
        }

        // copy flags
        for(Map.Entry<FlagType, LandFlag> flagEntry : flags.entrySet()) {
            
            ((DummyLand) desLand).flags.put(flagEntry.getKey(), flagEntry.getValue().copyOf());
        }
    }
    
    /**
     * Adds the permission.
     *
     * @param pc the pc
     * @param perm the perm
     */
    public void addPermission(PlayerContainer pc, Permission perm) {

        TreeMap<PermissionType, Permission> permPlayer;

        if (this instanceof Land) {
            ((PlayerContainer)pc).setLand((Land) this);
        }
        
        if (!permissions.containsKey(pc)) {
            permPlayer = new TreeMap<PermissionType, Permission>();
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
    public boolean removePermission(PlayerContainer pc,
            PermissionType permType) {

        TreeMap<PermissionType, Permission> permPlayer;
        Permission perm;

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
    public final Set<PlayerContainer> getSetPCHavePermission() {

        return permissions.keySet();
    }

    /**
     * Gets the permissions for pc.
     *
     * @param pc the pc
     * @return the permissions for pc
     */
    public final Collection<Permission> getPermissionsForPC(PlayerContainer pc) {

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
            PermissionType pt) {

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
            PermissionType pt) {

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
            PermissionType pt, boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).checkLandPermissionAndInherit(player, pt, onlyInherit);
        }
        return Secuboid.getThisPlugin().getLands().getPermissionInWorld(worldName, player, pt, onlyInherit);
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
            PermissionType pt, boolean onlyInherit) {
        
        return getPermission(player, pt, onlyInherit, null);
    }
    
    // Land parameter is only to paste to default parameters for a land
    private Boolean getPermission(Player player, 
            PermissionType pt, boolean onlyInherit, Land land) {

        for (Map.Entry<PlayerContainer, TreeMap<PermissionType, Permission>> permissionEntry : permissions.entrySet()) {
            boolean value;
            if(land != null) {
                value = permissionEntry.getKey().hasAccess(player, land);
            } else {
                value = permissionEntry.getKey().hasAccess(player);
            }
            if (value) {
                Permission perm = permissionEntry.getValue().get(pt);

                // take the parent if the permission does not exist
                if(perm == null && pt.hasParent()) {
                    perm = permissionEntry.getValue().get(pt.getParent());
                }

                if (perm != null) {
                    Secuboid.getThisPlugin().getLog().write("Container: " + permissionEntry.getKey().toString() + ", PermissionType: " + perm.getPermType() + ", Value: " + perm.getValue() + ", Heritable: " + perm.isHeritable());
                    if ((onlyInherit && perm.isHeritable()) || !onlyInherit) {
                        return perm.getValue();
                    }
                }
            }
        }

        // Check in default permissions
        if(!onlyInherit && this instanceof Land) {

            return ((Lands) Secuboid.getThisPlugin().getLands()).getDefaultConf(((Land) this).getType()).getPermission(
                    player, pt, false, (Land) this);
        }
        
        return null;
    }

    /**
     * Adds the flag.
     *
     * @param flag the flag
     */
    public void addFlag(LandFlag flag) {

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
    public boolean removeFlag(FlagType flagType) {

        LandFlag flag = flags.remove(flagType);
        
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
    public Collection<LandFlag> getFlags() {

        return flags.values();
    }

    public FlagValue getFlagAndInherit(FlagType ft) {

        return getFlagAndInherit(ft, false);
    }

    /**
     * Gets the flag no inherit.
     *
     * @param ft the ft
     * @return the flag value or default
     */
    public FlagValue getFlagNoInherit(FlagType ft) {

        FlagValue value = getFlag(ft, false);
        
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
    protected FlagValue getFlagAndInherit(FlagType ft,
            boolean onlyInherit) {

        if (this instanceof Land) {
            return ((Land) this).getLandFlagAndInherit(ft, onlyInherit);
        }
        return Secuboid.getThisPlugin().getLands().getFlagInWorld(worldName, ft, onlyInherit);
    }

    /**
     * Gets the flag.
     *
     * @param ft the ft
     * @param onlyInherit the only inherit
     * @return the flag value
     */
    protected FlagValue getFlag(FlagType ft, boolean onlyInherit) {

        LandFlag flag = flags.get(ft);
        if (flag != null) {
            Secuboid.getThisPlugin().getLog().write("Flag: " + flag.toString());

            if ((onlyInherit && flag.isHeritable()) || !onlyInherit) {
                return flag.getValue();
            }
        }

        // Check in default flags
        if(!onlyInherit && this instanceof Land) {

            return ((Lands) Secuboid.getThisPlugin().getLands()).getDefaultConf(((Land) this).getType()).getFlag(ft, false);
        }

        return null;
    }

    /**
     * Do save.
     */
    protected void doSave() {

        if (this instanceof Land) {
            this.doSave();
        }
    }
}
