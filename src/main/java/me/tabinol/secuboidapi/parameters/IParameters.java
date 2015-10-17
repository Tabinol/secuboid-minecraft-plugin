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
package me.tabinol.secuboidapi.parameters;

import org.bukkit.Material;

/**
 * The Interface IParameters. It is where you can register or get permissions
 * or flags.
 */
public interface IParameters {

    /**
     *  Prefix of specials permissions (ex: PLACE_SIGN).
     */
	public enum SpecialPermPrefix {
    	
	    /** Place a block */
	    PLACE,
    	
	    /** Prevent place a block */
	    NOPLACE,
    	
	    /** Destroy a block */
	    DESTROY,
    	
	    /** Prevent destroy a block */
	    NODESTROY;
    }

    /**
     * Register permission type. Keep the returned value. a Permission type is
     * unique.
     *
     * @param permissionName the permission name (UPPER CASE ONLY!)
     * @param defaultValue the default value
     * @return the permission type
     */
	public IPermissionType registerPermissionType(String permissionName, boolean defaultValue);
    
	/**
     * Register flag type. The default value gives the value type and can not
     * be null, but can be empty. you can put for example: (new Boolean(false),
     * new Double(0D), new String() or new String[] {}).
     * Keep the returned value. a Flag type is
     * unique.
     *
     * @param flagName the flag name (UPPER CASE ONLY!)
     * @param defaultValue the default value
     * @return the flag type
     */
    public IFlagType registerFlagType(String flagName, Object defaultValue);
    
    /**
     * Gets the permission type from the permission name.
     *
     * @param permissionName the permission name (UPPER CASE ONLY!)
     * @return the permission type
     */
    public IPermissionType getPermissionType(String permissionName);
    
    /**
     * Gets the flag type from flag name.
     *
     * @param flagName the flag name (UPPER CASE ONLY!)
     * @return the flag type
     */
    public IFlagType getFlagType(String flagName);
    
    /**
     * Gets the special permission (prefix_mat). This method is
     * here to reduce CPU usage.
     *
     * @param prefix the prefix ([NO]PLACE, [NO]DESTROY)
     * @param mat the material (Bukkit)
     * @return the permission type
     */
    public IPermissionType getSpecialPermission(SpecialPermPrefix prefix, Material mat);
}
