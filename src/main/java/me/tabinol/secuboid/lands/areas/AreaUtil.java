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

import java.util.HashMap;
import java.util.Map;

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
        // Create road area
        if (multiStr[0].equals(AreaType.ROAD.toString())) {
            Map<Integer, Map<Integer, ChunkMatrix>> points = new HashMap<Integer, Map<Integer, ChunkMatrix>>();
            for (int i = 4; i < multiStr.length; i += 3) {
                int regionX = Integer.parseInt(multiStr[i]);
                int regionZ = Integer.parseInt(multiStr[i + 1]);
                String hex = multiStr[i + 2];
                short[] matrix = new short[16];
                for (int j = 0; j < hex.length(); j += 4) {
                    matrix[j] = Short.parseShort(hex.substring(j, j + 4));
                }
                ChunkMatrix chunkMatrix = new ChunkMatrix(matrix);
                Map<Integer, ChunkMatrix> pointX = points.get(regionX);
                if (pointX == null) {
                    pointX = new HashMap<Integer, ChunkMatrix>();
                    points.put(regionX, pointX);
                }
                pointX.put(regionZ, chunkMatrix);
            }
            return new RoadArea(multiStr[1],
                    Integer.parseInt(multiStr[2]),
                    Integer.parseInt(multiStr[3]),
                    new RegionMatrix(points));
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
