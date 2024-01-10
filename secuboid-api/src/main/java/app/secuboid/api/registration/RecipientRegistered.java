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
package app.secuboid.api.registration;

import app.secuboid.api.utilities.CharacterCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static app.secuboid.api.utilities.CharacterCase.LOWERCASE;

/**
 * The Interface RecipientRegistered. Annotation used for custom parameter
 * values. If you make your own value types, your class should implement
 * RecipientExec.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RecipientRegistered {

    /**
     * The parameter name (not the value). Ex: player, everybody, etc.
     *
     * @return the name
     */
    String name();

    /**
     * The short name for chat and save. Must be unique. Ex: p, everybody, etc.
     *
     * @return the short name
     */
    String shortName();

    /**
     * The chat color code.
     *
     * @return the chat color code
     */
    String chatColor();

    /**
     * Value (additional parameter) needed or not?
     *
     * @return has additional parameter
     */
    boolean needsValue() default false;

    /**
     * With {@link #needsValue()} value case sensitive?
     *
     * @return the case
     */
    CharacterCase characterCase() default LOWERCASE;

    /**
     * The priority, higher is returned before the lowest if there is a match.
     *
     * @return the priority
     */
    int priority() default 50;
}
