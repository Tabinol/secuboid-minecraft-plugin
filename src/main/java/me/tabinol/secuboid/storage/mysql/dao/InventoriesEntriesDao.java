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

import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.InventoryEntryPojo;

public final class InventoriesEntriesDao {

    private final DatabaseConnection dbConn;

    public InventoriesEntriesDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public InventoryEntryPojo getInventoryEntry(final Connection conn, final int id) throws SQLException {
        final String sql = "SELECT `level`, `exp`, `health`, `food_level`, `item_stacks` " //
                + "FROM `{{TP}}inventories_entries` " //
                + "WHERE `id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, id);
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final int level = rs.getInt("level");
                    final float exp = rs.getFloat("exp");
                    final double health = rs.getFloat("health");
                    final int foodLevel = rs.getInt("food_level");
                    final String itemStackStr = rs.getString("item_stacks");
                    return new InventoryEntryPojo(id, level, exp, health, foodLevel, itemStackStr);
                }
            }
        }
        // Should never happen
        throw new SecuboidRuntimeException();
    }

    public int insertInventoryEntry(final Connection conn, final int level, final float exp, final double health,
            final int foodLevel, final String itemStackStr) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}inventories_entries` " //
                + "(`level`, `exp`, `health`, `food_level`, `item_stacks`) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql,
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, level);
            stmt.setFloat(2, exp);
            stmt.setDouble(3, health);
            stmt.setInt(4, foodLevel);
            stmt.setString(5, itemStackStr);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }

    public void updateInventoryEntry(final Connection conn, final InventoryEntryPojo inventoryEntryPojo)
            throws SQLException {
        final String sql = "UPDATE `{{TP}}inventories_entries` SET " //
                + "`level`=?, `exp`=?, `health`=?, `food_level`=?, `item_stacks`=? " + "WHERE `id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, inventoryEntryPojo.getLevel());
            stmt.setFloat(2, inventoryEntryPojo.getExp());
            stmt.setDouble(3, inventoryEntryPojo.getHealth());
            stmt.setInt(4, inventoryEntryPojo.getFoodLevel());
            stmt.setString(5, inventoryEntryPojo.getItemStackStr());
            stmt.setInt(6, inventoryEntryPojo.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteInventoryEntry(final Connection conn, final int id) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}inventories_entries WHERE `id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}