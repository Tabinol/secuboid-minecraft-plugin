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
import java.util.List;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.utilities.DbUtils;

public final class InventoriesDeathsDao {

    private final DatabaseConnection dbConn;

    public InventoriesDeathsDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Long getEntryIdNullable(final Connection conn, final UUID playerUUID, final long inventoryId,
                                   final long gameModeId, final int deathNumber) throws SQLException {
        final String sql = "SELECT `inventories_entries_id` FROM `{{TP}}inventories_deaths` " //
                + "WHERE `player_uuid`=? AND `inventory_id`=? AND `game_mode_id`=? AND `death_number`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, playerUUID);
            stmt.setLong(2, inventoryId);
            stmt.setLong(3, gameModeId);
            stmt.setInt(4, deathNumber);

            try (final ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("inventories_entries_id");
                }
                return null;
            }
        }
    }

    public void insertInventoryDeath(final Connection conn, final UUID playerUUID, final long inventoryId,
                                     final long gameModeId, final int deathNumber, final long inventoryEntryId) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}inventories_deaths` " //
                + "(`player_uuid`, `inventory_id`, `game_mode_id`, `death_number`, `inventories_entries_id`) " //
                + "VALUES (?, ?, ?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, playerUUID);
            stmt.setLong(2, inventoryId);
            stmt.setLong(3, gameModeId);
            stmt.setInt(4, deathNumber);
            stmt.setLong(5, inventoryEntryId);
            stmt.executeUpdate();
        }
    }

    public void deleteNinth(final Connection conn, final UUID playerUUID, final long inventoryId, final long gameModeId)
            throws SQLException {
        final String sql = "DELETE FROM `{{TP}}inventories_deaths` " //
                + "WHERE `player_uuid`=? AND `inventory_id`=? AND `game_mode_id`=? AND `death_number`=9";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, playerUUID);
            stmt.setLong(2, inventoryId);
            stmt.setLong(3, gameModeId);
            stmt.executeUpdate();
        }
    }

    public List<Long> getEntryIdsFromInventoryId(Connection conn, long inventoryId) throws SQLException {
        final String sql = "SELECT `inventories_entries_id` FROM `{{TP}}inventories_deaths` " //
                + "WHERE `inventory_id`=?";

        try (PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setLong(1, inventoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Long> results = new ArrayList<>();
                if (rs.next()) {
                    results.add(rs.getLong("inventories_entries_id"));
                }
                return results;
            }
        }
    }

    public void deleteFromInventoryId(Connection conn, long inventoryId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}inventories_deaths` " //
                + "WHERE `inventory_id`=?";

        try (PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setLong(1, inventoryId);
            stmt.executeUpdate();
        }
    }

    public void incrementDeathNumber(final Connection conn, final UUID playerUUID, final long inventoryId,
                                     final long gameModeId, final int deathNumber) throws SQLException {
        final String sql = "UPDATE IGNORE `{{TP}}inventories_deaths` " //
                + "SET  `death_number`=? " //
                + "WHERE `player_uuid`=? AND `inventory_id`=? AND `game_mode_id`=? AND `death_number`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, deathNumber + 1);
            DbUtils.setUUID(stmt, 2, playerUUID);
            stmt.setLong(3, inventoryId);
            stmt.setLong(4, gameModeId);
            stmt.setInt(5, deathNumber);
            stmt.executeUpdate();
        }
    }
}