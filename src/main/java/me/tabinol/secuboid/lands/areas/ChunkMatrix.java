/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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

import java.util.Arrays;

/**
 * Represents a matrix in a chunk.
 */
public final class ChunkMatrix {

    private final short[] matrix;

    /**
     * Creates an empty chunk matrix.
     */
    public ChunkMatrix() {
        matrix = new short[16];
    }

    /**
     * Creates an empty matrix. Only for copyOf() and from save files.
     *
     * @param matrix the matrix
     */
    public ChunkMatrix(final short[] matrix) {
        this.matrix = matrix;
    }

    /**
     * Adds a point in chunk matrix.
     *
     * @param chunkX the chunk x position
     * @param chunkZ the chunk z position
     */
    public void addPoint(final byte chunkX, final byte chunkZ) {
        matrix[chunkX] |= (1 << chunkZ);
    }

    /**
     * Removes a point in chunk matrix.
     *
     * @param chunkX the chunk x position
     * @param chunkZ the chunk z position
     */
    public void removePoint(final byte chunkX, final byte chunkZ) {
        matrix[chunkX] &= ~(1 << chunkZ);
    }

    /**
     * Gets de point value.
     *
     * @param chunkX the chunk x position
     * @param chunkZ the chunk z position
     * @return boolean point value
     */
    public boolean getPoint(final byte chunkX, final byte chunkZ) {
        return (matrix[chunkX] & (1 << chunkZ)) != 0;
    }

    /**
     * Is the matrix empty?
     *
     * @return true or false
     */
    public boolean isEmpty() {
        for (final short slice : matrix) {
            if (slice != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Count the number of points.
     *
     * @return the number of points
     */
    public int countPoints() {
        int nbPoints = 0;
        for (final short slice : matrix) {
            for (int i = 0; i < 16; i++) {
                if ((slice & (1 << i)) > 0) {
                    nbPoints++;
                }
            }
        }
        return nbPoints;
    }

    public String toFileFormat() {
        final StringBuilder sb = new StringBuilder();
        for (final short slice : matrix) {
            final String hexString = Integer.toHexString(slice & 0xffff);
            sb.append("0000".substring(hexString.length())).append(hexString);
        }
        return sb.toString();
    }

    public ChunkMatrix copyOf() {
        return new ChunkMatrix(Arrays.copyOf(matrix, matrix.length));
    }
}
