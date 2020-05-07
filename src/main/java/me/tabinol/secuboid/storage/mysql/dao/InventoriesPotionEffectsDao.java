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

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.InventoryPotionEffectPojo;

public final class InventoriesPotionEffectsDao {

    private final DatabaseConnection dbConn;

    public InventoriesPotionEffectsDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public List<InventoryPotionEffectPojo> getPotionEffectsFromEntryId(final Connection conn,
            final int inventoryEntryId) throws SQLException {
        final String sql = "SELECT `name`, `duration`, `amplifier`, `ambient` " //
                + "FROM `{{TP}}inventories_potion_effects` " //
                + "WHERE `inventories_entries_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, inventoryEntryId);
            try (final ResultSet rs = stmt.executeQuery()) {
                final List<InventoryPotionEffectPojo> results = new ArrayList<>();
                while (rs.next()) {
                    final String name = rs.getString("name");
                    final int duration = rs.getInt("duration");
                    final int amplifier = rs.getInt("amplifier");
                    final boolean ambient = rs.getBoolean("ambient");
                    results.add(new InventoryPotionEffectPojo(inventoryEntryId, name, duration, amplifier, ambient));
                }
                return results;
            }
        }
    }

    public void deletePotionEffectsFromEntryId(final Connection conn, final int inventoryEntryId) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}inventories_potion_effects` " //
                + "WHERE `inventories_entries_id`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, inventoryEntryId);
            stmt.executeUpdate();
        }
    }

    public void addPotionEffectsFromEntryId(final Connection conn,
            final InventoryPotionEffectPojo inventoryPotionEffectPojo) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}inventories_potion_effects` " //
                + "(`inventories_entries_id`, `name`, `duration`, `amplifier`, `ambient`)" //
                + "VALUES (?, ?, ?, ?, ?)";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.setInt(1, inventoryPotionEffectPojo.getInventoryEntryId());
            stmt.setString(2, inventoryPotionEffectPojo.getName());
            stmt.setInt(3, inventoryPotionEffectPojo.getDuration());
            stmt.setInt(4, inventoryPotionEffectPojo.getAmplifier());
            stmt.setBoolean(5, inventoryPotionEffectPojo.getAmbient());
            stmt.executeUpdate();
        }
    }
}