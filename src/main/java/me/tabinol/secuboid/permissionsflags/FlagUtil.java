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
package me.tabinol.secuboid.permissionsflags;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * Class for flag creation.
 *
 * @author tabinol
 */
public class FlagUtil {

    /**
     * Gets the from file format.
     *
     * @param str the str
     * @return the from string
     */
    public static Flag getFromFileFormat(String str) {

	String[] multiStr = StringChanges.splitKeepQuote(str, ":");
	FlagType ft = Secuboid.getThisPlugin().getPermissionsFlags().getFlagTypeNoValid(multiStr[0]);
	Object value = FlagValueUtil.getFromFileFormat(multiStr[1], ft);

	return new Flag(ft, value, Boolean.parseBoolean(multiStr[2]));
    }
}
