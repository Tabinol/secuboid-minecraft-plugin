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
import java.util.Optional;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.FlagPojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class FlagsDao {

    private final DatabaseConnection dbConn;

    public FlagsDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Map<UUID, List<FlagPojo>> getLandUUIDToFlags(final Connection conn) throws SQLException {
        final String sql = "SELECT `land_uuid`, `flag_id`, `value_string`, `value_double`, "
                + "`value_boolean`, `inheritance` " //
                + "FROM `{{TP}}lands_flags`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<UUID, List<FlagPojo>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final UUID landUUID = DbUtils.getUUID(rs, "land_uuid");
                    final int flagId = rs.getInt("flag_id");
                    final Optional<String> valueStringOpt = DbUtils.getOpt(rs, "value_string", rs::getString);
                    final Optional<Double> valueDoubleOpt = DbUtils.getOpt(rs, "value_double", rs::getDouble);
                    final Optional<Boolean> valueBooleanOpt = DbUtils.getOpt(rs, "value_boolean", rs::getBoolean);
                    final boolean inheritance = rs.getBoolean("inheritance");
                    final FlagPojo flagPojo = new FlagPojo(landUUID, flagId, valueStringOpt, valueDoubleOpt,
                            valueBooleanOpt, inheritance);

                    results.computeIfAbsent(landUUID, k -> new ArrayList<>()).add(flagPojo);
                }
                return results;
            }
        }
    }

    public void insertOrUpdateFlag(final Connection conn, final FlagPojo flagPojo) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}lands_flags`(" //
                + "`land_uuid`, `flag_id`, `value_string`, `value_double`, " //
                + "`value_boolean`, `inheritance`) " //
                + "VALUES(?, ?, ?, ?, ?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`value_string`=?, `value_double`=?, " //
                + "`value_boolean`=?, `inheritance`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, flagPojo.getLandUUID());
            stmt.setInt(2, flagPojo.getFlagId());
            DbUtils.setOpt(stmt, 3, flagPojo.getValueStringOpt(), (i, u) -> stmt.setString(i, u));
            DbUtils.setOpt(stmt, 4, flagPojo.getValueDoubleOpt(), (i, u) -> stmt.setDouble(i, u));
            DbUtils.setOpt(stmt, 5, flagPojo.getValueBooleanOpt(), (i, u) -> stmt.setBoolean(i, u));
            stmt.setBoolean(6, flagPojo.getInheritance());

            DbUtils.setOpt(stmt, 7, flagPojo.getValueStringOpt(), (i, u) -> stmt.setString(i, u));
            DbUtils.setOpt(stmt, 8, flagPojo.getValueDoubleOpt(), (i, u) -> stmt.setDouble(i, u));
            DbUtils.setOpt(stmt, 9, flagPojo.getValueBooleanOpt(), (i, u) -> stmt.setBoolean(i, u));
            stmt.setBoolean(10, flagPojo.getInheritance());

            stmt.executeUpdate();
        }
    }

    public void deleteFlag(final Connection conn, final UUID landUUID, final int flagId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands_flags` WHERE `land_uuid`=? AND `flag_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.setInt(2, flagId);
            stmt.executeUpdate();
        }
    }

    public void deleteAllLandFlags(final Connection conn, final UUID landUUID) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands_flags` WHERE `land_uuid`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.executeUpdate();
        }
    }
}