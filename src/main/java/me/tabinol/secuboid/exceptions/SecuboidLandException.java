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
package me.tabinol.secuboid.exceptions;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboidapi.exceptions.ASecuboidLandException;


/**
 * The Class SecuboidLandException.
 */
public class SecuboidLandException extends ASecuboidLandException {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -4561559858019587492L;

	/**
     * Instantiates a new secuboid land exception.
     *
     * @param landName the land name
     * @param area the area
     * @param action the action
     * @param error the error
     */
    public SecuboidLandException(String landName, CuboidArea area, Collisions.LandAction action, Collisions.LandError error) {
        
        super("Secuboid Land Exception");
        
        StringBuilder bf = new StringBuilder();
        
        bf.append("Error: Land: ").append(landName);
        if(area != null) {
            bf.append(", area: ").append(area.getPrint());
        }
        bf.append(", Action: ").append(action.toString()).append(", Error: ").append(error.toString());

        Secuboid.getThisPlugin().iLog().write(bf.toString());
    }
}
