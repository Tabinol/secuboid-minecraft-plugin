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
package me.tabinol.secuboid.parameters;

import org.bukkit.ChatColor;


/**
 * The Class ParameterType.
 *
 * @author michel
 */
public class ParameterType implements Comparable<ParameterType> {
    
    /** The name. */
    private final String name;
    
    /** The is registered. */
    private boolean isRegistered = false;
    
    /**
     * Instantiates a new parameter type.
     *
     * @param name the name
     */
    ParameterType(String name) {
        
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ParameterType t) {
        
        return name.compareTo(t.name);
    }
    
    /**
     * Equals.
     *
     * @param t the t
     * @return true, if successful
     */
    public boolean equals(ParameterType t) {
        
        return name.equals(t.name);
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        
        return name;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        
        return name;
    }
    
    /**
     * Gets the prints the.
     *
     * @return the prints the
     */
    public String getPrint() {
        
        if(isRegistered) {
            return ChatColor.YELLOW + name;
        } else {
            return ChatColor.DARK_GRAY + name;
        }
    }
    
    /**
     * Checks if is registered.
     *
     * @return true, if is registered
     */
    public boolean isRegistered() {
        
        return isRegistered;
    }
    
    /**
     * Sets the registered.
     */
    void setRegistered() {
        
        isRegistered = true;
    }
}
