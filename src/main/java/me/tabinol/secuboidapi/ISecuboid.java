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

import me.tabinol.secuboidapi.config.players.IPlayerStaticConfig;
import me.tabinol.secuboidapi.factions.IFactions;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.lands.ILands;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboidapi.lands.types.ITypes;
import me.tabinol.secuboidapi.parameters.IParameters;
import me.tabinol.secuboidapi.playercontainer.EPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;

/**
 * The ISecuboid. To use this API, be sure to put this line in your plugin.yml: <br>
 * depend: [Secuboid]<br>
 * How to access the Secuboid API?<br>
 * ISecuboid secuboidAPI = (ISecuboid) Bukkit.getPluginManager().getPlugin("Secuboid");
 */
public interface ISecuboid {
	
    /*
     * Normal gets
     */
	
    /**
     * Gets all faction informations
     *
     * @return the factions
     */
    public IFactions iFactions();

    /**
     * Gets all faction informations
     *
     * @return the lands
     */
    public ILands iLands();
    
    /**
     * Gets all land types informations
     *
     * @return the types
     */
    public ITypes iTypes();

    /**
     * Gets all parameters (flags and permissions). If you want to register
     * a new flag or a new permission, it is here!
     *
     * @return the parameters
     */
    public IParameters iParameters();
    
    /**
     * Obtains informations about an online player
     *
     * @return the player config
     */
    public IPlayerStaticConfig iPlayerConf();
    
    /*
     * Creators
     */

    /**
     * Creates a player container.
     *
     * @param land the parameter land is needed for Container type RESIDENT,
     *  VISITOR, FACTION_TERRITORY, OWNER and TENANT, otherwise, put null
     * @param pct the container type (EPlayerContainerType.XXXX)
     * @param name the name is needed for a GROUP (name of the group from
     * your permission system), PERMISSION (xxx.yyy from your permission system)
     * and for a PLAYER (ID-110e8400-e29b-11d4-a716-446655440000) without
     * parenthesis. You must begin with "ID-" or it will not works. If the player
     * is online, you may prefer to take it from iPlayerConf().
     * @return the player container
     */
    public IPlayerContainer createPlayerContainer(ILand land, 
    		EPlayerContainerType pct, String name);
    
    /**
     * Creates a cuboid area.
     *
     * @param worldName the world name
     * @param x1 the x1
     * @param y1 the y1
     * @param z1 the z1
     * @param x2 the x2
     * @param y2 the y2
     * @param z2 the z2
     * @return the cuboid area
     */
    public ICuboidArea createCuboidArea(String worldName, int x1, int y1, 
    		int z1, int x2, int y2, int z2);

}