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
        final String sql = "SELECT `id`, `land_uuid`, `flag_id`, `inheritance` " //
                + "FROM `{{TP}}lands_flags`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<UUID, List<FlagPojo>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final long id = rs.getLong("id");
                    final UUID landUUID = DbUtils.getUUID(rs, "land_uuid");
                    final long flagId = rs.getLong("flag_id");
                    final boolean inheritance = rs.getBoolean("inheritance");
                    final FlagPojo flagPojo = new FlagPojo(id, landUUID, flagId, inheritance);

                    results.computeIfAbsent(landUUID, k -> new ArrayList<>()).add(flagPojo);
                }
                return results;
            }
        }
    }

    public Optional<Long> getLandFlagIdOpt(final Connection conn, final UUID landUUID, final long flagId)
            throws SQLException {
        final String sql = "SELECT `id` FROM `{{TP}}lands_flags` " //
                + "WHERE `land_uuid`=?, `flag_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.setLong(2, flagId);
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
                return Optional.empty();
            }
        }
    }

    public void updateLandFlagIdInheritance(final Connection conn, final UUID landUUID, final long flagId,
            boolean inheritance) throws SQLException {
        final String sql = "UPDATE `{{TP}}lands_flags` SET `inheritance`=?" //
                + "WHERE `land_uuid`=?, `flag_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setBoolean(1, inheritance);
            DbUtils.setUUID(stmt, 2, landUUID);
            stmt.setLong(3, flagId);
            stmt.executeUpdate();
        }
    }

    public long insertFlagOrUpdateGetId(final Connection conn, final UUID landUUID, final long flagId,
            final boolean inheritance) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}lands_flags`(" //
                + "`land_uuid`, `flag_id`, `inheritance`) " //
                + "VALUES(?, ?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`inheritance`=?";

        final Optional<Long> idOpt = getLandFlagIdOpt(conn, landUUID, flagId);
        if (idOpt.isPresent()) {
            updateLandFlagIdInheritance(conn, landUUID, flagId, inheritance);
            return idOpt.get();
        }

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.setLong(2, flagId);
            stmt.setBoolean(3, inheritance);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public void deleteLandFlag(final Connection conn, final UUID landUUID, final long flagId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands_flags` WHERE `land_uuid`=? AND `flag_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.setLong(2, flagId);
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