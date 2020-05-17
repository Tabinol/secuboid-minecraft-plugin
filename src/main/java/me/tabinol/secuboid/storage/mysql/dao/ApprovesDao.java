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
import java.util.Optional;
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
                    final Optional<Integer> removedAreaIdOpt = DbUtils.getOpt(rs, "removed_area_id", rs::getInt);
                    final Optional<Integer> newAreaIdOpt = DbUtils.getOpt(rs, "new_area_id", rs::getInt);
                    final long ownerId = rs.getLong("owner_id");
                    final Optional<UUID> parentUUIDOpt = DbUtils.getOpt(rs, "parent_uuid", c -> DbUtils.getUUID(rs, c));
                    final double price = rs.getDouble("price");
                    Calendar transactionDatetime = DbUtils.getCalendar(rs, "transaction_datetime");

                    results.add(new ApprovePojo(landUUID, approveActionId, removedAreaIdOpt, newAreaIdOpt, ownerId,
                            parentUUIDOpt, price, transactionDatetime));
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
            DbUtils.setOpt(stmt, 3, approvePojo.getRemovedAreaIdOpt(), (i, u) -> stmt.setInt(i, u));
            DbUtils.setOpt(stmt, 4, approvePojo.getNewAreaIdOpt(), (i, u) -> stmt.setInt(i, u));
            stmt.setLong(5, approvePojo.getOwnerId());
            DbUtils.setOpt(stmt, 6, approvePojo.getParentUUIDOpt(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            stmt.setDouble(7, approvePojo.getPrice());
            DbUtils.setCalendar(stmt, 8, approvePojo.getTransactionDatetime());

            stmt.setLong(9, approvePojo.getApproveActionId());
            DbUtils.setOpt(stmt, 10, approvePojo.getRemovedAreaIdOpt(), (i, u) -> stmt.setInt(i, u));
            DbUtils.setOpt(stmt, 11, approvePojo.getNewAreaIdOpt(), (i, u) -> stmt.setInt(i, u));
            stmt.setLong(12, approvePojo.getOwnerId());
            DbUtils.setOpt(stmt, 13, approvePojo.getParentUUIDOpt(), (i, u) -> DbUtils.setUUID(stmt, i, u));
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