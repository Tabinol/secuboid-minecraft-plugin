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
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.ApprovePojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class ApprovesDao {

    private final DatabaseConnection dbConn;

    public ApprovesDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public List<ApprovePojo> getApproves(final Connection conn) throws SQLException {
        final String sql = "SELECT  `land_uuid`, `approve_action_id`, `removed_area_id`, `new_area_id`, " //
                + "`owner_id`, `parent_uuid`, `price`, `transaction_datetime` " //
                + "FROM `{{TP}}approves`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final List<ApprovePojo> results = new ArrayList<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final UUID landUUID = DbUtils.getUUID(rs, "land_uuid");
                    final long approveActionId = rs.getLong("approve_action_id");
                    final Integer removedAreaIdNullable = DbUtils.getNullable(rs, "removed_area_id", rs::getInt);
                    final Integer newAreaIdNullable = DbUtils.getNullable(rs, "new_area_id", rs::getInt);
                    final long ownerId = rs.getLong("owner_id");
                    final UUID parentUUIDNullable = DbUtils.getNullable(rs, "parent_uuid", c -> DbUtils.getUUID(rs, c));
                    final double price = rs.getDouble("price");
                    final Calendar transactionDatetime = DbUtils.getCalendar(rs, "transaction_datetime");

                    results.add(new ApprovePojo(landUUID, approveActionId, removedAreaIdNullable, newAreaIdNullable, ownerId,
                            parentUUIDNullable, price, transactionDatetime));
                }
                return results;
            }
        }
    }

    public void insertOrUpdateApprove(final Connection conn, final ApprovePojo approvePojo) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}approves` (" //
                + "`land_uuid`, `approve_action_id`, `removed_area_id`, `new_area_id`, " //
                + "`owner_id`, `parent_uuid`, `price`, `transaction_datetime`) " //
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`approve_action_id`=?, `removed_area_id`=?, `new_area_id`=?, " //
                + "`owner_id`=?, `parent_uuid`=?, `price`=?, `transaction_datetime`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, approvePojo.getLandUUID());
            stmt.setLong(2, approvePojo.getApproveActionId());
            DbUtils.setNullable(stmt, 3, approvePojo.getRemovedAreaIdNullable(), stmt::setInt);
            DbUtils.setNullable(stmt, 4, approvePojo.getNewAreaIdNullable(), stmt::setInt);
            stmt.setLong(5, approvePojo.getOwnerId());
            DbUtils.setNullable(stmt, 6, approvePojo.getParentUUIDNullable(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            stmt.setDouble(7, approvePojo.getPrice());
            DbUtils.setCalendar(stmt, 8, approvePojo.getTransactionDatetime());

            stmt.setLong(9, approvePojo.getApproveActionId());
            DbUtils.setNullable(stmt, 10, approvePojo.getRemovedAreaIdNullable(), stmt::setInt);
            DbUtils.setNullable(stmt, 11, approvePojo.getNewAreaIdNullable(), stmt::setInt);
            stmt.setLong(12, approvePojo.getOwnerId());
            DbUtils.setNullable(stmt, 13, approvePojo.getParentUUIDNullable(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            stmt.setDouble(14, approvePojo.getPrice());
            DbUtils.setCalendar(stmt, 15, approvePojo.getTransactionDatetime());

            stmt.executeUpdate();
        }
    }

    public void deleteApprove(final Connection conn, final UUID landUUID) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}approves` WHERE `land_uuid`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landUUID);
            stmt.executeUpdate();
        }
    }

    public void deleteAllApproves(final Connection conn) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}approves`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.executeUpdate();
        }
    }
}