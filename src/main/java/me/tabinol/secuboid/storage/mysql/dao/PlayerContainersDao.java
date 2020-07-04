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
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.PlayerContainerPojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class PlayerContainersDao {

    private final DatabaseConnection dbConn;

    public PlayerContainersDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Map<Long, PlayerContainerPojo> getIdToPlayerContainer(final Connection conn) throws SQLException {
        final String sql = "SELECT `id`, `player_container_type_id`, `player_uuid`, `parameter` " //
                + "FROM `{{TP}}player_containers`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<Long, PlayerContainerPojo> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final long id = rs.getLong("id");
                    final long playerContainerTypeId = rs.getLong("player_container_type_id");
                    final UUID playerUUIDNullable = DbUtils.getNullable(rs, "player_uuid", c -> DbUtils.getUUID(rs, c));
                    final String parameterNullable = DbUtils.getNullable(rs, "parameter", rs::getString);

                    results.put(id, new PlayerContainerPojo(id, playerContainerTypeId, playerUUIDNullable, parameterNullable));
                }
                return results;
            }
        }
    }

    public Long getIdNullable(final Connection conn, final long playerContainerTypeId,
                              final UUID playerUUIDNullable, final String parameterNullable) throws SQLException {
        final String sql = String.format("SELECT `id` FROM `{{TP}}player_containers` " //
                        + "WHERE `player_container_type_id`=? AND `player_uuid`%s AND `parameter`%s",
                DbUtils.isNullOrEquals(playerUUIDNullable), DbUtils.isNullOrEquals(parameterNullable));

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            int parameterIndex = 1;
            stmt.setLong(parameterIndex++, playerContainerTypeId);
            if (playerUUIDNullable != null) {
                DbUtils.setNullable(stmt, parameterIndex++, playerUUIDNullable, (i, u) -> DbUtils.setUUID(stmt, i, u));
            }
            if (parameterNullable != null) {
                DbUtils.setNullable(stmt, parameterIndex, parameterNullable, stmt::setString);
            }
            try (final ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                return null;
            }
        }
    }

    public long insertOrGetPlayerContainer(final Connection conn, final long playerContainerTypeId,
                                           final UUID playerUUIDNullable, final String parameterNullable) throws SQLException {
        final Long idNullable = getIdNullable(conn, playerContainerTypeId, playerUUIDNullable, parameterNullable);
        if (idNullable != null) {
            return idNullable;
        }

        final String sql = "INSERT INTO `{{TP}}player_containers` " //
                + "(`player_container_type_id`, `player_uuid`, `parameter`) VALUES (?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql,
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, playerContainerTypeId);
            DbUtils.setNullable(stmt, 2, playerUUIDNullable, (i, u) -> DbUtils.setUUID(stmt, i, u));
            DbUtils.setNullable(stmt, 3, parameterNullable, stmt::setString);
            stmt.executeUpdate();
            try (final ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }
}