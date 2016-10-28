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
package me.tabinol.secuboid.lands.collisions;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.collisions.Collisions.LandError;

/**
 * The Class CollisionsEntry.
 */
public class CollisionsEntry {

    /**
     * The error.
     */
    private final LandError error;

    /**
     * The land.
     */
    private final RealLand land;

    /**
     * The area id.
     */
    private final int areaId;

    /**
     * Instantiates a new collisions entry.
     *
     * @param error the error
     * @param land the land
     * @param areaId the area id
     */
    public CollisionsEntry(LandError error, RealLand land, int areaId) {

	this.error = error;
	this.land = land;
	this.areaId = areaId;
    }

    /**
     * Gets the error.
     *
     * @return the error
     */
    public LandError getError() {

	return error;
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public RealLand getLand() {

	return land;
    }

    /**
     * Gets the area id.
     *
     * @return the area id
     */
    public int getAreaId() {

	return areaId;
    }

    /**
     * Gets the prints the.
     *
     * @return the prints the
     */
    public String getPrint() {

	if (error != null) {
	    switch (error) {
		case COLLISION:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.COLLISION", land.getName(), areaId + "");
		case OUT_OF_PARENT:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.OUT_OF_PARENT", land.getName());
		case CHILD_OUT_OF_BORDER:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.CHILD_OUT_OF_BORDER", land.getName());
		case HAS_CHILDREN:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.HAS_CHILDREN", land.getName());
		case NAME_IN_USE:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.NAME_IN_USE");
		case IN_APPROVE_LIST:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.IN_APPROVE_LIST");
		case NOT_ENOUGH_MONEY:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.NOT_ENOUGH_MONEY");
		case MAX_AREA_FOR_LAND:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.MAX_AREA_FOR_LAND", land.getName());
		case MAX_LAND_FOR_PLAYER:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.MAX_LAND_FOR_PLAYER");
		case MUST_HAVE_AT_LEAST_ONE_AREA:
		    return Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.SHOW.MUST_HAVE_AT_LEAST_ONE_AREA");
		default:
		    break;
	    }
	}

	return null;
    }
}
