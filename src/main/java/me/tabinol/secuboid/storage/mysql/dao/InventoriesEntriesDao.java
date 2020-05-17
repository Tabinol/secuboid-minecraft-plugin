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

import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.InventoryEntryPojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class InventoriesEntriesDao {

    private final DatabaseConnection dbConn;

    public InventoriesEntriesDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public InventoryEntryPojo getInventoryEntry(final Connection conn, final long id) throws SQLException {
        final String sql = "SELECT `level`, `exp`, `health`, `food_level`, `contents`, " //
                + "`ender_chest_contents` " //
                + "FROM `{{TP}}inventories_entries` " //
                + "WHERE `id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setLong(1, id);
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final int level = rs.getInt("level");
                    final float exp = rs.getFloat("exp");
                    final double health = rs.getFloat("health");
                    final int foodLevel = rs.getInt("food_level");
                    final ItemStack[] contents = DbUtils.getItemStacks(rs, "contents",
                            PlayerInvEntry.INVENTORY_LIST_SIZE);
                    final ItemStack[] enderChestContents = DbUtils.getItemStacks(rs, "ender_chest_contents",
                            PlayerInvEntry.ENDER_CHEST_SIZE);
                    return new InventoryEntryPojo(id, level, exp, health, foodLevel, contents, enderChestContents);
                }
            }
        }
        // Should never happen
        throw new SecuboidRuntimeException();
    }

    public long insertInventoryEntry(final Connection conn, final int level, final float exp, final double health,
            final int foodLevel, final ItemStack[] contents, final ItemStack[] enderChestContents) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}inventories_entries` " //
                + "(`level`, `exp`, `health`, `food_level`, `contents`, `ender_chest_contents`) " //
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql,
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, level);
            stmt.setFloat(2, exp);
            stmt.setDouble(3, health);
            stmt.setInt(4, foodLevel);
            DbUtils.setItemStacks(stmt, 5, contents);
            DbUtils.setItemStacks(stmt, 6, enderChestContents);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public void updateInventoryEntry(final Connection conn, final InventoryEntryPojo inventoryEntryPojo)
            throws SQLException {
        final String sql = "UPDATE `{{TP}}inventories_entries` SET " //
                + "`level`=?, `exp`=?, `health`=?, `food_level`=?, " //
                + "`contents`=?, `ender_chest_contents`=? WHERE `id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, inventoryEntryPojo.getLevel());
            stmt.setFloat(2, inventoryEntryPojo.getExp());
            stmt.setDouble(3, inventoryEntryPojo.getHealth());
            stmt.setInt(4, inventoryEntryPojo.getFoodLevel());
            DbUtils.setItemStacks(stmt, 5, inventoryEntryPojo.getContents());
            DbUtils.setItemStacks(stmt, 6, inventoryEntryPojo.getEnderChestContents());
            stmt.setLong(7, inventoryEntryPojo.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteInventoryEntry(final Connection conn, final long id) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}inventories_entries WHERE `id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}