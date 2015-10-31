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
package me.tabinol.secuboidapi.playercontainer;


/**
 * The Enum PlayerContainerType. This is the enum list of the possible player
 * containers.
 */
public enum ApiPlayerContainerType {
    
    // Order is important here The first is the permission checked first
    /** The undefined (do not use). */
    UNDEFINED(0,"UNDEFINED", false),
    
    /** The land owner. */
    OWNER(1,"Owner", false),
    
    /** The player. */
    PLAYER(2,"Player", true),
    
    /** The land resident. */
    RESIDENT(3,"Resident", false),
    
    /** The land tenant. */
    TENANT(4, "Tenant", false),
    
    /** The land visitor. */
    VISITOR(5, "Visitor", false),
    
    /** The group from permision system. */
    GROUP(6,"Group", true),
    
    /** The Bukkit permission. */
    PERMISSION(7, "Permission", true),
    
    /** Everybody. */
    EVERYBODY(8,"Everybody", false),
    
    /** Nobody. */
    NOBODY(9,"Nobody", false),
    
    /** Player Name, Only for UUID resolve and replace to a true player. (INTERNAL) */
    PLAYERNAME(10, "PlayerName", false);
    
    /** The value. */
    private final int value;
    
    /** The player container name. */
    private final String pcName;
    
    /** Has parameter. */
    private final boolean hasParameter;
    
    /**
     * Instantiates a new player container type.
     *
     * @param value the value
     * @param pcName the pc name
     * @param hasParameter the has parameter
     */
    private ApiPlayerContainerType(final int value, final String pcName, final boolean hasParameter) {
        
        this.value = value;
        this.pcName = pcName;
        this.hasParameter = hasParameter;
    }
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        
        return value;
    }
    
    /**
     * Checks for parameter.
     *
     * @return true, if successful
     */
    public boolean hasParameter() {
        
        return hasParameter;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        
        return pcName;
    }
    
    /**
     * Gets the player container from string.
     *
     * @param pcName the player container name (UPPER CASE)
     * @return the player container type
     */
    public static ApiPlayerContainerType getFromString(String pcName) {
        
        for(ApiPlayerContainerType pct : values()) {
            if(pct.toString().equalsIgnoreCase(pcName)) {
                return pct;
            }
        }
        return null;
    }
}
