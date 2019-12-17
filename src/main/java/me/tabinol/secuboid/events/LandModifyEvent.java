/*
 Secuboid: Lands plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

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
package me.tabinol.secuboid.events;

import org.bukkit.event.HandlerList;

import me.tabinol.secuboid.lands.Land;

/**
 * The Class LandModifyEvent. When there is a change in a land.
 */
public class LandModifyEvent extends LandInsideEvent {

    /**
     * The Enum LandModifyReason.
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

    /**
     * The Constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * The land modify reason.
     */
    private final LandModifyReason landModifyReason;

    /**
     * The new object.
     */
    private final Object newObject;

    /**
     * Instantiates a new land modify events.
     *
     * @param land             the land
     * @param landModifyReason the land modify reason
     * @param newObject        the new object
     */
    public LandModifyEvent(final Land land, final LandModifyReason landModifyReason, final Object newObject) {

        super(land);
        this.newObject = newObject;
        this.landModifyReason = landModifyReason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
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
     * Gets the new object. This object can be an Area, a playerContainer (for RESIDENT or owner change), a flag, a
     * permission, a String for a name change.
     *
     * @return the new object
     */
    public Object getNewObject() {
        return newObject;
    }
}
