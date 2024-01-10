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
package app.secuboid.api.flagtypes;

import app.secuboid.api.exceptions.SecuboidRuntimeException;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents a flag type. Hidden "true" is only for a flag that contains sensitive or not understandable information
 * for a player.
 **/
public interface FlagType {

    /**
     * Creates a new instance of flag type.
     *
     * @param name         the name of the flag lowercase with "-" if more than one word
     * @param description  the flag description
     * @param needSource   is this flag need a source parameter (ex: the player who take the action, the exploded creeper)?
     * @param needTarget   is this flag need a target parameter (ex: to mob targeted with an arrow)?
     * @param needMetadata is this flag need extra data?
     * @param isHidden     hidden "true" is only for a flag that contains sensitive or not understandable information for a player
     * @return a new instance
     */
    static FlagType newInstance(String name, String description, boolean needSource, boolean needTarget,
                                boolean needMetadata, boolean isHidden) {
        try {
            Class<?> clazz = Class.forName("app.secuboid.core.flagtypes.FlagTypeImpl");
            return (FlagType) clazz.getDeclaredConstructor(String.class, String.class, boolean.class, boolean.class,
                            boolean.class, boolean.class)
                    .newInstance(name, description, needSource, needTarget, needMetadata, isHidden);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new SecuboidRuntimeException("Problem with \"app.secuboid.core.flagtypes.FlagTypeImpl\" new instance", e);
        }
    }

    /**
     * Gets the name of the flag lowercase with "-" if more than one word.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the flag description.
     *
     * @return the flag description
     */
    String getDescription();

    /**
     * Is this flag need a source parameter (ex: the player who take the action, the exploded creeper)?
     *
     * @return true if the flag need a source parameter
     */
    boolean isNeedSource();

    /**
     * Is this flag need a target parameter (ex: to mob targeted with an arrow)?
     *
     * @return true if this flag need a target parameter
     */
    boolean isNeedTarget();

    /**
     * Is this flag need extra data?
     *
     * @return true if this flag need extra data?
     */
    boolean isNeedMetadata();

    /**
     * Hidden "true" is only for a flag that contains sensitive or not understandable information for a player.
     *
     * @return true if the flag is hidden
     */
    boolean isHidden();
}
