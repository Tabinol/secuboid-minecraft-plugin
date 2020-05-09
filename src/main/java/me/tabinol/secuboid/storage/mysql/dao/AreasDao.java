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
import me.tabinol.secuboid.storage.mysql.pojo.AreaPojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class AreasDao {

    private final DatabaseConnection dbConn;

    public AreasDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Map<UUID, List<AreaPojo>> getLandUUIDToAreas(final Connection conn) throws SQLException {
        final String sql = "SELECT `land_uuid`, `area_id`, `approved`, `world_name`, `area_type_id`, " //
                + "`x1`, `y1`, `z1`, `x2`, `y2`, `z2` " //
                + "FROM `{{TP}}lands_areas`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<UUID, List<AreaPojo>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final UUID landUUID = DbUtils.getUUID(rs, "land_uuid");
                    final int areaId = rs.getInt("area_id");
                    final boolean approved = rs.getBoolean("approved");
                    final String worldName = rs.getString("world_name");
                    final int areaTypeId = rs.getInt("area_type_id");
                    final int x1 = rs.getInt("x1");
                    final int y1 = rs.getInt("y1");
                    final int z1 = rs.getInt("z1");
                    final int x2 = rs.getInt("x2");
                    final int y2 = rs.getInt("y2");
                    final int z2 = rs.getInt("z2");
                    final AreaPojo areaPojo = new AreaPojo(landUUID, areaId, approved, worldName, areaTypeId, x1, y1,
                            z1, x2, y2, z2);

                    results.computeIfAbsent(landUUID, k -> new ArrayList<>()).add(areaPojo);
                }
                return results;
            }
        }
    }

    public void insertOrUpdateArea(final Connection conn, final AreaPojo areaPojo) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}lands_areas`(" //
                + "`land_uuid`, `area_id`, `approved`, `world_name`, `area_type_id`, " //
                + "`x1`, `y1`, `z1`, `x2`, `y2`, `z2`) " //
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`approved`=?, `world_name`=?, `area_type_id`=?, " //
                + "`x1`=?, `y1`=?, `z1`=?, `x2`=?, `y2`=?, `z2`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, areaPojo.getLandUUID());
            stmt.setInt(2, areaPojo.getAreaId());
            stmt.setBoolean(3, areaPojo.getApproved());
            stmt.setString(4, areaPojo.getWorldName());
            stmt.setInt(5, areaPojo.getAreaTypeId());
            stmt.setInt(6, areaPojo.getX1());
            stmt.setInt(7, areaPojo.getY1());
            stmt.setInt(8, areaPojo.getZ1());
            stmt.setInt(9, areaPojo.getX2());
            stmt.setInt(10, areaPojo.getY2());
            stmt.setInt(11, areaPojo.getZ2());

            stmt.setBoolean(12, areaPojo.getApproved());
            stmt.setString(13, areaPojo.getWorldName());
            stmt.setInt(14, areaPojo.getAreaTypeId());
            stmt.setInt(15, areaPojo.getX1());
            stmt.setInt(16, areaPojo.getY1());
            stmt.setInt(17, areaPojo.getZ1());
            stmt.setInt(18, areaPojo.getX2());
            stmt.setInt(19, areaPojo.getY2());
            stmt.setInt(20, areaPojo.getZ2());

            stmt.executeUpdate();
        }
    }

    public void deleteArea(final Connection conn, final UUID landUUID, final int areaId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands_areas` WHERE `land_uuid`=? AND `area_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.setInt(2, areaId);
            stmt.executeUpdate();
        }
    }
}