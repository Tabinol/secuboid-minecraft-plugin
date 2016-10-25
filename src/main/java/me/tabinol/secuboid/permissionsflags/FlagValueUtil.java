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

import java.util.ArrayList;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * Class for flag util creation.
 *
 * @author tabinol
 */
public class FlagValueUtil {

    /**
     * Gets the flag value from file format.
     *
     * @param str the string
     * @param ft the flag type
     * @return the flag value
     */
    public static FlagValue getFromFileFormat(String str, FlagType ft) {

	FlagValue value;

	if (ft.isRegistered()) {
	    if (ft.getDefaultValue().getValue() instanceof Boolean) {
		String[] strs = str.split(" ");
		value = new FlagValue(Boolean.parseBoolean(strs[0]));
	    } else if (ft.getDefaultValue().getValue() instanceof Double) {
		String[] strs = str.split(" ");
		value = new FlagValue(Double.parseDouble(strs[0]));
	    } else if (ft.getDefaultValue().getValue() instanceof String) {
		value = new FlagValue(StringChanges.fromQuote(str));
	    } else if (ft.getDefaultValue().getValue() instanceof String[]) {
		ArrayList<String> result = new ArrayList<String>();
		String[] strs = StringChanges.splitKeepQuote(str, ";");
		for (String st : strs) {
		    result.add(StringChanges.fromQuote(st));
		}
		value = new FlagValue(result.toArray(new String[0]));
	    } else {
		value = null;
	    }
	} else {

	    // not registered save raw information
	    value = new FlagValue(str);
	}

	return value;
    }
}
