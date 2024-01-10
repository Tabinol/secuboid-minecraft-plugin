/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.api.events;

import app.secuboid.api.lands.Land;
import org.bukkit.event.HandlerList;

/**
 * When a land is modified. When there is a change in a land.
 */
public class LandModifyEvent extends LandEvent {

    private static final HandlerList handlers = new HandlerList();
    private final LandModifyReason landModifyReason;
    private final Object newObject;

    /**
     * Instantiates a new land modify events.
     *
     * @param land             the land
     * @param landModifyReason the land modify reason
     * @param newObject        the new object
     */
    public LandModifyEvent(Land land, LandModifyReason landModifyReason, Object newObject) {
        super(land);
        this.newObject = newObject;
        this.landModifyReason = landModifyReason;
    }

    @SuppressWarnings("java:S4144")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the land modify reason.
     *
     * @return the land modify reason
     */
    public LandModifyReason getLandModifyReason() {
        return landModifyReason;
    }

    /**
     * Gets the new object. This object can be an Area, a playerContainer (for
     * RESIDENT or owner change), a flag, a permission, a String for a name change.
     *
     * @return the new object
     */

    public Object getNewObject() {
        return newObject;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * The land modify reason.
     */
    public enum LandModifyReason {

        /**
         * The area add.
         */
        AREA_ADD,
        /**
         * The area remove.
         */
        AREA_REMOVE,
        /**
         * The area replace.
         */
        AREA_REPLACE,
        /**
         * The resident add.
         */
        RESIDENT_ADD,
        /**
         * The resident remove.
         */
        RESIDENT_REMOVE,
        /**
         * The permission set.
         */
        PERMISSION_SET,
        /**
         * The permission unset.
         */
        PERMISSION_UNSET,
        /**
         * The flag set.
         */
        FLAG_SET,
        /**
         * The flag unset.
         */
        FLAG_UNSET,
        /**
         * The owner change.
         */
        OWNER_CHANGE,
        /**
         * The land rename.
         */
        RENAME
    }
}
