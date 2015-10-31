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
package me.tabinol.secuboid.playercontainer;

import java.util.UUID;

import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.utilities.StringChanges;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;


/**
 * The Class PlayerContainer.
 */
public abstract class PlayerContainer implements ApiPlayerContainer, Comparable<PlayerContainer> {

    /** The name. */
    protected String name;
    
    /** The container type. */
    protected ApiPlayerContainerType containerType;

    /**
     * Instantiates a new player container.
     *
     * @param name the name
     * @param containerType the container type
     * @param toLowerCase the to lower case
     */
    protected PlayerContainer(String name, ApiPlayerContainerType containerType, boolean toLowerCase) {

        if (toLowerCase) {
            this.name = name.toLowerCase();
        } else {
            this.name = name;
        }
        this.containerType = containerType;
    }

    /**
     * Creates the.
     *
     * @param land the land
     * @param pct the pct
     * @param name the name
     * @return the player container
     */
    public static PlayerContainer create(ApiLand land, ApiPlayerContainerType pct, String name) {

        if (pct == ApiPlayerContainerType.GROUP) {
            return new PlayerContainerGroup(name);
        } else if (pct == ApiPlayerContainerType.RESIDENT) {
            return new PlayerContainerResident(land);
        } else if (pct == ApiPlayerContainerType.VISITOR) {
            return new PlayerContainerVisitor(land);
        } else if (pct == ApiPlayerContainerType.OWNER) {
            return new PlayerContainerOwner(land);
        } else if (pct == ApiPlayerContainerType.EVERYBODY) {
            return new PlayerContainerEverybody();
        } else if (pct == ApiPlayerContainerType.NOBODY) {
            return new PlayerContainerNobody();
        } else if (pct == ApiPlayerContainerType.PLAYER || pct == ApiPlayerContainerType.PLAYERNAME) {
            UUID minecraftUUID;

            // First check if the ID is valid or was connected to the server
            try {
                minecraftUUID = UUID.fromString(name.replaceFirst("ID-", ""));
            } catch (IllegalArgumentException ex) {

                // If there is an error, just return a temporary PlayerName
                return new PlayerContainerPlayerName(name);
            }

            // If not null, assign the value to a new PlayerContainer
            return new PlayerContainerPlayer(minecraftUUID);
        } else if (pct == ApiPlayerContainerType.PERMISSION) {
            return new PlayerContainerPermission(name);
        } else if (pct == ApiPlayerContainerType.TENANT) {
            return new PlayerContainerTenant(land);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#getName()
     */
    @Override
    public String getName() {

        return name;
    }

    /**
     * Gets the container type.
     *
     * @return the container type
     */
    public ApiPlayerContainerType getContainerType() {

        return containerType;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#compareTo(me.tabinol.secuboid.playercontainer.PlayerContainer)
     */
    @Override
    public int compareTo(PlayerContainer t) {

        if (containerType.getValue() < t.containerType.getValue()) {
            return -1;
        }
        if (containerType.getValue() > t.containerType.getValue()) {
            return 1;
        }

        // No ignorecase (Already Lower, except UUID)
        return name.compareTo(t.name);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return containerType.toString() + ":" + name;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#getPrint()
     */
    @Override
    public String getPrint() {

        return containerType.toString();
    }

    /**
     * Gets the from string.
     *
     * @param string the string
     * @return the from string
     */
    public static PlayerContainer getFromString(String string) {

        String strs[] = StringChanges.splitAddVoid(string, ":");
        ApiPlayerContainerType type = ApiPlayerContainerType.getFromString(strs[0]);
        return create(null, type, strs[1]);
    }
    
    /**
     * Sets the land. Not in Common API for security.
     *
     * @param land the new land
     */
    public abstract void setLand(ApiLand land);
    
}
