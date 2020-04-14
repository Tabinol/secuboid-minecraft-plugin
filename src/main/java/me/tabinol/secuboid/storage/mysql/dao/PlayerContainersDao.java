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

    public Map<Integer, PlayerContainerPojo> getIdToPlayerContainer(final Connection conn) throws SQLException {
        final String sql = "SELECT `id`, `player_container_type_id`, `player_uuid`, `parameter` " //
                + "FROM `{{TP}}lands`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<Integer, PlayerContainerPojo> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final int playerContainerTypeId = rs.getInt("player_container_type_id");
                    final Optional<UUID> playerUUIDOpt = DbUtils.getOpt(rs, "player_uuid", c -> DbUtils.getUUID(rs, c));
                    final Optional<String> parameterOpt = DbUtils.getOpt(rs, "parameter", rs::getString);

                    results.put(id, new PlayerContainerPojo(id, playerContainerTypeId, playerUUIDOpt, parameterOpt));
                }
                return results;
            }
        }
    }
}