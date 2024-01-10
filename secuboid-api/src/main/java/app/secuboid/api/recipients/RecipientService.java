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

package app.secuboid.api.recipients;

import app.secuboid.api.services.Service;

import java.util.function.Consumer;

/**
 * Access to recipients.
 */
public interface RecipientService extends Service {

    public static final String ENTITY_TYPE = "et";
    public static final String ENTITY_CLASS = "ec";
    public static final String PLAYER = "p";
    public static final String RESIDENT = "res";
    public static final String NOBODY = "nobody";
    public static final String EVERYTHING = "everything";
    public static final String EVERYBODY = "everybody";

    /**
     * Grabs a recipientExec.
     *
     * @param name     the name or short name
     * @param value    the value or null if the recipientExec doesn't accept values
     * @param callback the returned answer in callback
     */
    void grab(String name, String value, Consumer<RecipientResult> callback);
}
