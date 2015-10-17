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

import me.tabinol.secuboidapi.config.players.IPlayerStaticConfig;
import me.tabinol.secuboidapi.factions.IFactions;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.lands.ILands;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboidapi.lands.types.ITypes;
import me.tabinol.secuboidapi.parameters.IParameters;
import me.tabinol.secuboidapi.playercontainer.EPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;

// TODO: Auto-generated Javadoc
/**
 * The Secuboid API. This is the static way to access the Secuboid API.
 * To use this API, be sure to put this line in your plugin.yml: <br>
 * depend: [Secuboid]<br>
 */
public final class SecuboidAPI {

    /**  The Secuboid Interface. */
	private static ISecuboid iSecuboid;
	
    /**
     * Inits the secuboid plugin access. NOTE: It is not useful to run it.
     */
    public static void initSecuboidPluginAccess() {
    	
    	try {
			iSecuboid = (ISecuboid) Class.forName("me.tabinol.secuboid.Secuboid")
					.getDeclaredMethod("getThisPlugin").invoke(null);
		
    	} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
	
	/*
     * Normal gets
     */
	
    /**
	 * I factions.
	 *
	 * @return the i factions
	 * @see ISecuboid#iFactions()
	 */
    public static IFactions iFactions() {
    	
    	return iSecuboid.iFactions();
    }

    /**
     * I lands.
     *
     * @return the i lands
     * @see ISecuboid#iLands()
     */
    public static ILands iLands() {
    	
    	return iSecuboid.iLands();
    }

    /**
     * I types.
     *
     * @return the i types
     * @see ISecuboid#iTypes()
     */
    public static ITypes iTypes() {
    	
    	return iSecuboid.iTypes();
    }
    
    /**
     * I parameters.
     *
     * @return the i parameters
     * @see ISecuboid#iParameters()
     */
    public static IParameters iParameters() {
    	
    	return iSecuboid.iParameters();
    }
    
    /**
     * I player conf.
     *
     * @return the i player static config
     * @see ISecuboid#iPlayerConf()
     */
    public static IPlayerStaticConfig iPlayerConf() {
    	
    	return iSecuboid.iPlayerConf();
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
     * @see ISecuboid#createPlayerContainer(ILand, EPlayerContainerType, String)
     */
    public static IPlayerContainer createPlayerContainer(ILand land, 
    		EPlayerContainerType pct, String name) {
    	
    	return iSecuboid.createPlayerContainer(land, pct, name);
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
     * @see ISecuboid#createCuboidArea(String, int, int, int, int, int, int)
     */
    public static ICuboidArea createCuboidArea(String worldName, int x1, int y1, 
    		int z1, int x2, int y2, int z2) {
    	
    	return iSecuboid.createCuboidArea(worldName, x1, y1, z1, x2, y2, z2);
    }

}
