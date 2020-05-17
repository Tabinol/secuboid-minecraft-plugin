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

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;

public final class FlagsValuesListDao {

    private final DatabaseConnection dbConn;

    public FlagsValuesListDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Map<Long, List<String>> getLandFlagIdToValueList(final Connection conn) throws SQLException {
        final String sql = "SELECT `land_flag_id`, `value_string` " //
                + "FROM `{{TP}}lands_flags_values_list` ORDER BY `id`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<Long, List<String>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final long landFlagId = rs.getLong("land_flag_id");
                    final String valueString = rs.getString("value_string");

                    results.computeIfAbsent(landFlagId, k -> new ArrayList<>()).add(valueString);
                }
                return results;
            }
        }
    }

    public void insertLandFlagValueListItem(final Connection conn, final long landFlagId, final String[] values)
            throws SQLException {
        final String sql = "INSERT INTO `{{TP}}lands_flags_values_list` (`land_flag_id`, `value_string`) "//
                + " VALUES (?, ?)";

        conn.setAutoCommit(false);
        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            for (final String value : values) {
                stmt.setLong(1, landFlagId);
                stmt.setString(2, value);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        }
        conn.setAutoCommit(true);
    }

    public void deleteLandFlagValueList(final Connection conn, final long landFlagId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands_flags_values_list` WHERE `land_flag_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setLong(1, landFlagId);
            stmt.executeUpdate();
        }
    }
}