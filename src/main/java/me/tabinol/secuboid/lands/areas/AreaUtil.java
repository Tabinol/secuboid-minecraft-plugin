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
package me.tabinol.secuboid.lands.areas;

import java.util.ArrayList;
import java.util.List;
import me.tabinol.secuboid.lands.areas.lines.LineLine;

/**
 * Area utilities
 */
public class AreaUtil {

    /**
     * Gets the from string.
     *
     * @param str the str
     * @return the from string
     */
    public static Area getFromFileFormat(String str) {

	String[] multiStr = str.split(":");

	// Create infinite area
	if (multiStr[0].equals(AreaType.INFINITE.toString())) {
	    return new InfiniteArea(multiStr[1]);
	}

	// Create cuboid area
	if (multiStr[0].equals(AreaType.CUBOID.toString())) {
	    return new CuboidArea(multiStr[1],
		    Integer.parseInt(multiStr[2]),
		    Integer.parseInt(multiStr[3]),
		    Integer.parseInt(multiStr[4]),
		    Integer.parseInt(multiStr[5]),
		    Integer.parseInt(multiStr[6]),
		    Integer.parseInt(multiStr[7]));
	}

	// Create cylinder area
	if (multiStr[0].equals(AreaType.CYLINDER.toString())) {
	    return new CylinderArea(multiStr[1],
		    Integer.parseInt(multiStr[2]),
		    Integer.parseInt(multiStr[3]),
		    Integer.parseInt(multiStr[4]),
		    Integer.parseInt(multiStr[5]),
		    Integer.parseInt(multiStr[6]),
		    Integer.parseInt(multiStr[7]));
	}

	// Create lines area
	if (multiStr[0].equals(AreaType.LINES.toString())) {
	    List<LineLine> lines = new ArrayList<LineLine>();

	    // Do the first
	    lines.add(new LineLine(
		    Integer.parseInt(multiStr[2]),
		    Integer.parseInt(multiStr[3]),
		    Integer.parseInt(multiStr[4]),
		    Integer.parseInt(multiStr[5]),
		    Integer.parseInt(multiStr[6]),
		    Integer.parseInt(multiStr[7]),
		    Integer.parseInt(multiStr[8]),
		    Integer.parseInt(multiStr[9]),
		    Integer.parseInt(multiStr[10]),
		    Integer.parseInt(multiStr[11])
	    ));

	    // Do the next lines (if exist)
	    if (multiStr.length > 12) {
		for (int t = 12; t < multiStr.length; t += 10) {
		    lines.add(new LineLine(
			    Integer.parseInt(multiStr[t - 7]),
			    Integer.parseInt(multiStr[t - 6]),
			    Integer.parseInt(multiStr[t - 5]),
			    Integer.parseInt(multiStr[t]),
			    Integer.parseInt(multiStr[t + 1]),
			    Integer.parseInt(multiStr[t + 2]),
			    Integer.parseInt(multiStr[t + 3]),
			    Integer.parseInt(multiStr[t + 4]),
			    Integer.parseInt(multiStr[t + 5]),
			    Integer.parseInt(multiStr[t + 6])
		    ));
		}
	    }
	    return new LinesArea(multiStr[1], lines);
	}

	// Create CuboidArea (old version)
	return new CuboidArea(multiStr[0],
		Integer.parseInt(multiStr[1]),
		Integer.parseInt(multiStr[2]),
		Integer.parseInt(multiStr[3]),
		Integer.parseInt(multiStr[4]),
		Integer.parseInt(multiStr[5]),
		Integer.parseInt(multiStr[6]));
    }
}
