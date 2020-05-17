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
import java.util.Optional;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.utilities.DbUtils;

public final class InventoriesSavesDao {

    private final DatabaseConnection dbConn;

    public InventoriesSavesDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Optional<Long> getEntryIdOpt(final Connection conn, final UUID playerUUID, final long inventoryId,
            final long gameModeId) throws SQLException {
        final String sql = "SELECT `inventories_entries_id` FROM `{{TP}}inventories_saves` " //
                + "WHERE `player_uuid`=? AND `inventory_id`=? AND `game_mode_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, playerUUID);
            stmt.setLong(2, inventoryId);
            stmt.setLong(3, gameModeId);

            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return Optional.of(rs.getLong("inventories_entries_id"));
                }
                return Optional.empty();
            }
        }
    }

    public void insertInventorySave(final Connection conn, final UUID playerUUID, final long inventoryId,
            final long gameModeId, final long inventoryEntryId) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}inventories_saves` " //
                + "(`player_uuid`, `inventory_id`, `game_mode_id`, `inventories_entries_id`) " //
                + "VALUES (?, ?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, playerUUID);
            stmt.setLong(2, inventoryId);
            stmt.setLong(3, gameModeId);
            stmt.setLong(4, inventoryEntryId);
            stmt.executeUpdate();
        }
    }
}