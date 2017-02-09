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

/**
 * The Enum PlayerContainerType. This is the enum list of the possible player containers. <br>
 * Order is important here The first is the permission checked first
 */
public enum PlayerContainerType {

    /**
     * The land owner.
     */
    OWNER("Owner", false),
    /**
     * The player.
     */
    PLAYER("Player", true),
    /**
     * The land resident.
     */
    RESIDENT("Resident", false),
    /**
     * The land tenant.
     */
    TENANT("Tenant", false),
    /**
     * The land visitor.
     */
    VISITOR("Visitor", false),
    /**
     * The group from permision system.
     */
    GROUP("Group", true),
    /**
     * The Bukkit permission.
     */
    PERMISSION("Permission", true),
    /**
     * Everybody.
     */
    EVERYBODY("Everybody", false),
    /**
     * Nobody.
     */
    NOBODY("Nobody", false),
    /**
     * Player Name, Only for UUID resolve and replace to a true player. (INTERNAL)
     */
    PLAYERNAME("PlayerName", false);

    /**
     * The player container name.
     */
    private final String pcName;

    /**
     * Has parameter.
     */
    private final boolean hasParameter;

    /**
     * Instantiates a new player container type.
     *
     * @param pcName       the pc name
     * @param hasParameter the has parameter
     */
    PlayerContainerType(final String pcName, final boolean hasParameter) {

        this.pcName = pcName;
        this.hasParameter = hasParameter;
    }

    /**
     * Checks for parameter.
     *
     * @return true, if successful
     */
    public boolean hasParameter() {
        return hasParameter;
    }

    /**
     * Gets printable name
     *
     * @return printable name
     */
    public String getPrint() {
        return pcName;
    }

    /**
     * Gets the player container from string.
     *
     * @param pcName the player container name (UPPER CASE)
     * @return the player container type
     */
    public static PlayerContainerType getFromString(String pcName) {

        for (PlayerContainerType pct : values()) {
            if (pct.toString().equalsIgnoreCase(pcName)) {
                return pct;
            }
        }
        return null;
    }
}
