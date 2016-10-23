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
package me.tabinol.secuboid.playerscache.minecraftapi;

/**
 *
 * @author michel
 */
public class Profile {

    private String id;
    private String name;

    /**
     *
     * @param id
     * @param name
     */
    public Profile(String id, String name) {

        this.id = id;
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getId() {

        return id;
    }

    /**
     *
     * @return
     */
    public String getName() {

        return name;
    }
}