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
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.utilities.StringChanges;

public class PlayerContainerUtil {
    
    /**
     * Creates the player container
     *
     * @param land the land
     * @param pct the pct
     * @param name the name
     * @return the player container
     */
    public static PlayerContainer create(Land land, PlayerContainerType pct, String name) {

        if (null != pct) switch (pct) {
	    case GROUP:
		return new PlayerContainerGroup(name);
	    case RESIDENT:
		return new PlayerContainerResident(land);
	    case VISITOR:
		return new PlayerContainerVisitor(land);
	    case OWNER:
		return new PlayerContainerOwner(land);
	    case EVERYBODY:
		return new PlayerContainerEverybody();
	    case NOBODY:
		return new PlayerContainerNobody();
	    case PLAYER:
	    case PLAYERNAME:
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
	    case PERMISSION:
		return new PlayerContainerPermission(name);
	    case TENANT:
		return new PlayerContainerTenant(land);
	    default:
		break;
	}
        return null;
    }

    /**
     * Gets the player container from string.
     *
     * @param string the player container from string
     * @return the string
     */
    public static PlayerContainer getFromFileFormat(String string) {

        String strs[] = StringChanges.splitAddVoid(string, ":");
        PlayerContainerType type = PlayerContainerType.getFromString(strs[0]);
        return create(null, type, strs[1]);
    }
}
