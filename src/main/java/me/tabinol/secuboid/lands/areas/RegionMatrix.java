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

import java.util.*;

/**
 * Represents a gride of point for roads.
 */
public class RegionMatrix {

    /**
     * points is represented: RegionX: RegionZ: chunkMatrix
     */
    private final Map<Integer, Map<Integer, ChunkMatrix>> points;

    /**
     * Creates a new region matrix for roads.
     */
    public RegionMatrix() {
        points = new HashMap<Integer, Map<Integer, ChunkMatrix>>();
    }

    /**
     * Creates a new region. Only for copyOf() and from save files.
     */
    RegionMatrix(Map<Integer, Map<Integer, ChunkMatrix>> points) {
        this.points = points;
    }

    /**
     * Adds a point in chunk matrix.
     *
     * @param x the x position
     * @param z the z position
     */
    public void addPoint(int x, int z) {

        // From region X
        int chunkX = x / 16;
        Map<Integer, ChunkMatrix> pRegionZ = points.get(chunkX);
        if (pRegionZ == null) {
            pRegionZ = new HashMap<Integer, ChunkMatrix>();
            points.put(chunkX, pRegionZ);
        }

        // From region Z
        int chunkZ = z / 16;
        ChunkMatrix matrix = pRegionZ.get(chunkZ);
        if (matrix == null) {
            matrix = new ChunkMatrix();
            pRegionZ.put(chunkZ, matrix);
        }

        // Add to matrix
        byte posX = (byte) Math.abs((x % 512) % 16);
        byte posZ = (byte) Math.abs((z % 512) % 16);
        matrix.addPoint(posX, posZ);
    }

    /**
     * Gets de point value.
     *
     * @param x the x position
     * @param z the z position
     * @return boolean point value
     */
    public boolean getPoint(int x, int z) {

        // From region X
        int chunkX = x / 16;
        Map<Integer, ChunkMatrix> pRegionZ = points.get(chunkX);
        if (pRegionZ == null) {
            return false;
        }

        // From region Z
        int chunkZ = z / 16;
        ChunkMatrix matrix = pRegionZ.get(chunkZ);
        if (matrix == null) {
            return false;
        }

        // Add to matrix
        byte posX = (byte) Math.abs((x % 512) % 16);
        byte posZ = (byte) Math.abs((z % 512) % 16);
        return matrix.getPoint(posX, posZ);
    }

    /**
     * Count the number of points.
     *
     * @return the number of points
     */
    public long countPoints() {
        long nbPoints = 0;
        for (Map<Integer, ChunkMatrix> pRegionZ : points.values()) {
            for (ChunkMatrix matrix : pRegionZ.values()) {
                nbPoints += matrix.countPoints();
            }
        }
        return nbPoints;
    }

    public String toFileFormat() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Map<Integer, ChunkMatrix>> entryX : points.entrySet()) {
            for (Map.Entry<Integer, ChunkMatrix> entryZ : entryX.getValue().entrySet()) {
                sb.append(':').append(entryX.getKey()).append(':').append(entryZ.getKey()).append(':')
                        .append(entryZ.getValue().toFileFormat());
            }
        }
        return sb.toString();
    }

    public RegionMatrix copyOf() {
        Map<Integer, Map<Integer, ChunkMatrix>> newPoints = new HashMap<Integer, Map<Integer, ChunkMatrix>>();
        for (Map.Entry<Integer, Map<Integer, ChunkMatrix>> entryX : points.entrySet()) {
            Map<Integer, ChunkMatrix> newPointsZ = new HashMap<Integer, ChunkMatrix>();
            for (Map.Entry<Integer, ChunkMatrix> entryZ : entryX.getValue().entrySet()) {
                if (!entryZ.getValue().isEmpty()) {
                    newPointsZ.put(entryZ.getKey(), entryZ.getValue().copyOf());
                }
            }
            if (!newPointsZ.isEmpty()) {
                newPoints.put(entryX.getKey(), newPointsZ);
            }
        }
        return new RegionMatrix(newPoints);
    }
}
