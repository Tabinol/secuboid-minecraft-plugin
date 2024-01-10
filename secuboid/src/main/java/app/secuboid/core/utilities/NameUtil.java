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
package app.secuboid.core.utilities;

public class NameUtil {

    private static final String REGEX_NAME_VALIDATOR = "^[a-z0-9à-ÿ][a-z0-9à-ÿ\\-]{0,20}[a-z0-9à-ÿ]$";

    private static final String REGEX_UNALLOWED_NAMES = "^(land|area|secuboid|sd)$";

    private NameUtil() {
    }

    public static boolean validateName(String name) {
        return name.matches(REGEX_NAME_VALIDATOR) && !name.matches(REGEX_UNALLOWED_NAMES);
    }
}
