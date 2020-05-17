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
package me.tabinol.secuboid.lands.collisions;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.collisions.Collisions.LandError;

/**
 * The Class CollisionsEntry.
 */
public class CollisionsEntry {

    private final Secuboid secuboid;

    /**
     * The error.
     */
    private final LandError error;

    /**
     * The land.
     */
    private final Land land;

    /**
     * The area id.
     */
    private final int areaId;

    /**
     * Instantiates a new collisions entry.
     *
     * @param secuboid secuboid instance
     * @param error the error
     * @param land the land
     * @param areaId the area id
     */
    public CollisionsEntry(Secuboid secuboid, LandError error, Land land, int areaId) {

	this.secuboid = secuboid;
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
    public Land getLand() {

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
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.COLLISION", land.getName(), areaId + "");
		case OUT_OF_PARENT:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.OUT_OF_PARENT", land.getName());
		case CHILD_OUT_OF_BORDER:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.CHILD_OUT_OF_BORDER", land.getName());
		case HAS_CHILDREN:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.HAS_CHILDREN", land.getName());
		case NAME_IN_USE:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.NAME_IN_USE");
		case IN_APPROVE_LIST:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.IN_APPROVE_LIST");
		case NOT_ENOUGH_MONEY:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.NOT_ENOUGH_MONEY");
		case MAX_AREA_FOR_LAND:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.MAX_AREA_FOR_LAND", land.getName());
		case MAX_LAND_FOR_PLAYER:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.MAX_LAND_FOR_PLAYER");
		case MUST_HAVE_AT_LEAST_ONE_AREA:
		    return secuboid.getLanguage().getMessage("COLLISION.SHOW.MUST_HAVE_AT_LEAST_ONE_AREA");
		default:
		    break;
	    }
	}

	return null;
    }
}
