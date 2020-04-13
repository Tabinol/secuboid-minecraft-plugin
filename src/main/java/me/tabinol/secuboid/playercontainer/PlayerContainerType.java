/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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
 * The Enum PlayerContainerType. This is the enum list of the possible player
 * containers. <br>
 * Order is important here The first is the permission checked first
 */
public enum PlayerContainerType {

    /**
     * The land owner.
     */
    OWNER("O", "Owner", false),
    /**
     * The player.
     */
    PLAYER("P", "Player", true),
    /**
     * The land resident.
     */
    RESIDENT("R", "Resident", false),
    /**
     * The land tenant.
     */
    TENANT("T", "Tenant", false),
    /**
     * The group from permision system.
     */
    GROUP("G", "Group", true),
    /**
     * The Bukkit permission.
     */
    PERMISSION("B", "Permission", true),
    /**
     * Everybody.
     */
    EVERYBODY("E", "Everybody", false),
    /**
     * Nobody.
     */
    NOBODY("N", "Nobody", false),
    /**
     * Player Name, Only for UUID resolve and replace to a true player. (INTERNAL)
     */
    PLAYERNAME("Z", "PlayerName", false);

    /**
     * The player container name.
     */
    private final String pcName;
    private final String oneLetterCode;

    /**
     * Has parameter.
     */
    private final boolean hasParameter;

    /**
     * Instantiates a new player container type.
     *
     * @param pcName        the pc name
     * @param hasParameter  the has parameter
     * @param oneLetterCode One letter code
     */
    PlayerContainerType(String oneLetterCode, String pcName, boolean hasParameter) {

        this.pcName = pcName;
        this.hasParameter = hasParameter;
        this.oneLetterCode = oneLetterCode;
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
     * Gets one letter code
     *
     * @return one letter code
     */
    public String getOneLetterCode() {
        return oneLetterCode;
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
            if (pct.oneLetterCode.equalsIgnoreCase(pcName) || pct.toString().equalsIgnoreCase(pcName)) {
                return pct;
            }
        }
        return null;
    }
}
