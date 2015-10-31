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
 * The Interface ApiFlagType. Represents a flag type, not a value.
 */
public interface ApiFlagType {

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName();
    
    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public ApiFlagValue getDefaultValue();

    /**
     * Gets the prints format.
     *
     * @return the prints the format
     */
    public String getPrint();
    
    /**
     * Checks if is the flag type is registered from Secuboid or any other
     * plugins.
     *
     * @return true, if is registered
     */
    public boolean isRegistered();
    
}
