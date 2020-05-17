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
import java.util.Optional;
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
                    final Optional<UUID> playerUUIDOpt = DbUtils.getOpt(rs, "player_uuid", c -> DbUtils.getUUID(rs, c));
                    final Optional<String> parameterOpt = DbUtils.getOpt(rs, "parameter", rs::getString);

                    results.put(id, new PlayerContainerPojo(id, playerContainerTypeId, playerUUIDOpt, parameterOpt));
                }
                return results;
            }
        }
    }

    public Optional<Long> getIdOpt(final Connection conn, final long playerContainerTypeId,
            final Optional<UUID> playerUUIDOpt, final Optional<String> parameterOpt) throws SQLException {
        final String sql = String.format("SELECT `id` FROM `{{TP}}player_containers` " //
                + "WHERE `player_container_type_id`=? AND `player_uuid`%s AND `parameter`%s",
                DbUtils.isNullOrEquals(playerUUIDOpt), DbUtils.isNullOrEquals(parameterOpt));

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            int parameterIndex = 1;
            stmt.setLong(parameterIndex++, playerContainerTypeId);
            if (playerUUIDOpt.isPresent()) {
                DbUtils.setOpt(stmt, parameterIndex++, playerUUIDOpt, (i, u) -> DbUtils.setUUID(stmt, i, u));
            }
            if (parameterOpt.isPresent()) {
                DbUtils.setOpt(stmt, parameterIndex++, parameterOpt, (i, u) -> stmt.setString(i, u));
            }
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
                return Optional.empty();
            }
        }
    }

    public long insertOrGetPlayerContainer(final Connection conn, final long playerContainerTypeId,
            final Optional<UUID> playerUUIDOpt, final Optional<String> parameterOpt) throws SQLException {
        final Optional<Long> idOpt = getIdOpt(conn, playerContainerTypeId, playerUUIDOpt, parameterOpt);
        if (idOpt.isPresent()) {
            return idOpt.get();
        }

        final String sql = "INSERT INTO `{{TP}}player_containers` " //
                + "(`player_container_type_id`, `player_uuid`, `parameter`) VALUES (?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql,
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, playerContainerTypeId);
            DbUtils.setOpt(stmt, 2, playerUUIDOpt, (i, u) -> DbUtils.setUUID(stmt, i, u));
            DbUtils.setOpt(stmt, 3, parameterOpt, (i, u) -> stmt.setString(i, u));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }
}