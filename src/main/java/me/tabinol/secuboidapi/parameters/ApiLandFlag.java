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
 * The Interface ApiLandFlag. Represent a configured flag.
 */
public interface ApiLandFlag {

    /**
     * Copy of.
     *
     * @return a copy of this object
     */
    public ApiLandFlag copyOf();

    /**
     * Equals.
     *
     * @param lf2 the lf2
     * @return true, if successful
     */
    public boolean equals(ApiLandFlag lf2);
    
    /**
     * Gets the flag type.
     *
     * @return the flag type
     */
    public ApiFlagType getFlagType();
    
    /**
     * Gets the value.
     *
     * @return the value
     */
    public ApiFlagValue getValue();
    
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
