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

import me.tabinol.secuboidapi.config.players.ApiPlayerStaticConfig;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.ApiLands;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiTypes;
import me.tabinol.secuboidapi.parameters.ApiParameters;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;

/**
 * The ApiSecuboid. To use this API, be sure to put this line in your plugin.yml: <br>
 * depend: [Secuboid]<br>
 * How to access the Secuboid API?<br>
 * ApiSecuboid secuboidAPI = (ApiSecuboid) Bukkit.getPluginManager().getPlugin("Secuboid");
 */
public interface ApiSecuboid {
    
    /*
     * Normal gets
     */
    
    /**
     * Gets all faction informations
     *
     * @return the lands
     */
    public ApiLands getLands();
    
    /**
     * Gets all land types informations
     *
     * @return the types
     */
    public ApiTypes getTypes();

    /**
     * Gets all parameters (flags and permissions). If you want to register
     * a new flag or a new permission, it is here!
     *
     * @return the parameters
     */
    public ApiParameters getParameters();
    
    /**
     * Obtains informations about an online player
     *
     * @return the player config
     */
    public ApiPlayerStaticConfig getPlayerConf();
    
    /*
     * Creators
     */

    /**
     * Creates a player container.
     *
     * @param land the parameter land is needed for Container type RESIDENT,
     *  VISITOR, FACTION_TERRITORY, OWNER and TENANT, otherwise, put null
     * @param pct the container type (ApiPlayerContainerType.XXXX)
     * @param name the name is needed for a GROUP (name of the group from
     * your permission system), PERMISSION (xxx.yyy from your permission system)
     * and for a PLAYER (ID-110e8400-e29b-11d4-a716-446655440000) without
     * parenthesis. You must begin with "ID-" or it will not works. If the player
     * is online, you may prefer to take it from getPlayerConf().
     * @return the player container
     */
    public ApiPlayerContainer createPlayerContainer(ApiLand land,
            ApiPlayerContainerType pct, String name);
    
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
    public ApiCuboidArea createCuboidArea(String worldName, int x1, int y1,
            int z1, int x2, int y2, int z2);

}