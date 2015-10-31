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
package me.tabinol.secuboidapi;

import java.lang.reflect.InvocationTargetException;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboidapi.config.players.ApiPlayerStaticConfig;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.ApiLands;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiTypes;
import me.tabinol.secuboidapi.parameters.ApiParameters;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;

/**
 * The Secuboid API. This is the static way to access the Secuboid API.
 * To use this API, be sure to put this line in your plugin.yml: <br>
 * depend: [Secuboid]<br>
 */
public final class ApiSecuboidSta {

    /**  The Secuboid Interface. */
    private static ApiSecuboid apiSecuboid;
    
    /**
     * Inits the secuboid plugin access. NOTE: It is not useful to run it.
     */
    public static void initSecuboidPluginAccess() {
        
        apiSecuboid = Secuboid.getThisPlugin();
    }
    
    /*
     * Normal gets
     */
    
    /**
     * Get lands.
     *
     * @return the lands
     * @see ApiSecuboid#getLands()
     */
    public static ApiLands getLands() {
        
        return apiSecuboid.getLands();
    }

    /**
     * Get types.
     *
     * @return the types
     * @see ApiSecuboid#getTypes()
     */
    public static ApiTypes getTypes() {
        
        return apiSecuboid.getTypes();
    }
    
    /**
     * Get parameters.
     *
     * @return the parameters
     * @see ApiSecuboid#getParameters()
     */
    public static ApiParameters getParameters() {
        
        return apiSecuboid.getParameters();
    }
    
    /**
     * Get player conf.
     *
     * @return the  player static config
     * @see ApiSecuboid#getPlayerConf()
     */
    public static ApiPlayerStaticConfig getPlayerConf() {
        
        return apiSecuboid.getPlayerConf();
    }
    
    /*
     * Creators
     */

    /**
     * Creates the player container.
     *
     * @param land the land
     * @param pct the pct
     * @param name the name
     * @return the i player container
     * @see ApiSecuboid#createPlayerContainer(ApiLand, ApiPlayerContainerType, String)
     */
    public static ApiPlayerContainer createPlayerContainer(ApiLand land,
            ApiPlayerContainerType pct, String name) {
        
        return apiSecuboid.createPlayerContainer(land, pct, name);
    }
    
    /**
     * Creates the cuboid area.
     *
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     * @return the i cuboid area
     * @see ApiSecuboid#createCuboidArea(String, int, int, int, int, int, int)
     */
    public static ApiCuboidArea createCuboidArea(String worldName, int x1, int y1,
            int z1, int x2, int y2, int z2) {
        
        return apiSecuboid.createCuboidArea(worldName, x1, y1, z1, x2, y2, z2);
    }

}
