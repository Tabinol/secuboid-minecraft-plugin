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


/**
 * The Interface IPermission. Represent a configured permission.
 */
public interface IPermission {

    /**
     * Copy of.
     *
     * @return a copy of this object
     */
    public IPermission copyOf();
	
	/**
     * Gets the perm type.
     *
     * @return the perm type
     */
    public IPermissionType getPermType();
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public boolean getValue();
    
    /**
     * Gets the value print.
     *
     * @return the value print
     */
    public String getValuePrint();

    /**
     * Checks if is heritable.
     *
     * @return true, if is heritable
     */
    public boolean isHeritable();
    
    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString();
}
