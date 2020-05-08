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
package me.tabinol.secuboid.storage.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.RoadMatrixPojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class AreasRoadsMatricesDao {

    private final DatabaseConnection dbConn;

    public AreasRoadsMatricesDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Map<UUID, List<RoadMatrixPojo>> getLandUUIDToMatrices(final Connection conn) throws SQLException {
        final String sql = "SELECT `land_uuid`, `area_id`, `chunk_x`, `chunk_z`, `matrix` " //
                + "FROM `{{TP}}lands_areas_roads_matrices`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<UUID, List<RoadMatrixPojo>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final UUID landUUID = DbUtils.getUUID(rs, "land_uuid");
                    final int areaId = rs.getInt("area_id");
                    final int chunkX = rs.getInt("chunk_x");
                    final int chunkZ = rs.getInt("chunk_z");
                    final short[] matrix = DbUtils.getMatrix16(rs, "matrix");
                    final RoadMatrixPojo roadMatrixPojo = new RoadMatrixPojo(landUUID, areaId, chunkX, chunkZ, matrix);

                    results.computeIfAbsent(landUUID, k -> new ArrayList<>()).add(roadMatrixPojo);
                }
                return results;
            }
        }
    }

    public void insertOrUpdateRoadMatrix(final Connection conn, final RoadMatrixPojo roadMatrixPojo)
            throws SQLException {
        final String sql = "INSERT INTO `{{TP}}lands_areas_roads_matrices`(" //
                + "`land_uuid`, `area_id`, `chunk_x`, `chunk_z`, `matrix`) " //
                + "VALUES(?, ?, ?, ?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`matrix`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, roadMatrixPojo.getLandUUID());
            stmt.setInt(2, roadMatrixPojo.getAreaId());
            stmt.setInt(3, roadMatrixPojo.getChunkX());
            stmt.setInt(4, roadMatrixPojo.getChunkZ());
            DbUtils.setMatrix16(stmt, 5, roadMatrixPojo.getMatrix());

            DbUtils.setMatrix16(stmt, 6, roadMatrixPojo.getMatrix());

            stmt.executeUpdate();
        }
    }

    public void deleteRoadMatrix(final Connection conn, final UUID landUUID, final int areaId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands_areas_roads_matrices` WHERE `land_uuid`=? AND `area_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.setInt(2, areaId);
            stmt.executeUpdate();
        }
    }
}