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
import me.tabinol.secuboid.storage.mysql.pojo.LandPojo;
import me.tabinol.secuboid.utilities.DbUtils;

/**
 * LandsDao
 */
public final class LandsDao {

    private final DatabaseConnection dbConn;

    public LandsDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public List<LandPojo> getLands(final Connection conn) throws SQLException {
        final String sql = "SELECT `uuid`, `name`, `approved`, `type_id`, `owner_id`, " //
                + "`parent_uuid`, `priority`, `money`, `for_sale`, `for_sale_sign_location`, " //
                + "`sale_price`, `for_rent`, `for_rent_sign_location`, `rent_price`, " //
                + "`rent_renew`, `rent_auto_renew`, `tenant_uuid`, `last_payment_millis` " //
                + "FROM `{{TP}}lands`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final List<LandPojo> results = new ArrayList<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final UUID uuid = DbUtils.getUUID(rs, "uuid");
                    final String name = rs.getString("name");
                    final boolean approved = rs.getBoolean("approved");
                    final Long typeIdNullable = DbUtils.getNullable(rs, "type_id", rs::getLong);
                    final long ownerId = rs.getLong("owner_id");
                    final UUID parentUUIDNullable = DbUtils.getNullable(rs, "parent_uuid", c -> DbUtils.getUUID(rs, c));
                    final short priority = rs.getShort("priority");
                    final double money = rs.getDouble("money");
                    final boolean forSale = rs.getBoolean("for_sale");
                    final String forSaleSignLocationNullable = DbUtils.getNullable(rs, "for_sale_sign_location",
                            rs::getString);
                    final Double salePriceNullable = DbUtils.getNullable(rs, "sale_price", rs::getDouble);
                    final boolean forRent = rs.getBoolean("for_rent");
                    final String forRentSignLocationNullable = DbUtils.getNullable(rs, "for_rent_sign_location",
                            rs::getString);
                    final Double rentPriceNullable = DbUtils.getNullable(rs, "rent_price", rs::getDouble);
                    final Integer rentRenewNullable = DbUtils.getNullable(rs, "rent_renew", rs::getInt);
                    final Boolean rentAutoRenewNullable = DbUtils.getNullable(rs, "rent_auto_renew", rs::getBoolean);
                    final UUID tenantUUIDNullable = DbUtils.getNullable(rs, "tenant_uuid", c -> DbUtils.getUUID(rs, c));
                    final Long lastPaymentMillisNullable = DbUtils.getNullable(rs, "last_payment_millis", rs::getLong);

                    results.add(new LandPojo(uuid, name, approved, typeIdNullable, ownerId, parentUUIDNullable, priority, money,
                            forSale, forSaleSignLocationNullable, salePriceNullable, forRent, forRentSignLocationNullable,
                            rentPriceNullable, rentRenewNullable, rentAutoRenewNullable, tenantUUIDNullable, lastPaymentMillisNullable));
                }
                return results;
            }
        }
    }

    public void insertOrUpdateLand(final Connection conn, final LandPojo landPojo) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}lands`(" //
                + "`uuid`, `name`, `approved`, `type_id`, `owner_id`, " //
                + "`parent_uuid`, `priority`, `money`, `for_sale`, `for_sale_sign_location`, " //
                + "`sale_price`, `for_rent`, `for_rent_sign_location`, `rent_price`, " //
                + "`rent_renew`, `rent_auto_renew`, `tenant_uuid`, `last_payment_millis`) " //
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`name`=?, `approved`=?, `type_id`=?, `owner_id`=?, " //
                + "`parent_uuid`=?, `priority`=?, `money`=?, `for_sale`=?, `for_sale_sign_location`=?, " //
                + "`sale_price`=?, `for_rent`=?, `for_rent_sign_location`=?, `rent_price`=?, " //
                + "`rent_renew`=?, `rent_auto_renew`=?, `tenant_uuid`=?, `last_payment_millis`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, landPojo.getUUID());
            stmt.setString(2, landPojo.getName());
            stmt.setBoolean(3, landPojo.isApproved());
            DbUtils.setNullable(stmt, 4, landPojo.getTypeIdNullable(), stmt::setLong);
            stmt.setLong(5, landPojo.getOwnerId());
            DbUtils.setNullable(stmt, 6, landPojo.getParentUUIDNullable(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            stmt.setShort(7, landPojo.getPriority());
            stmt.setDouble(8, landPojo.getMoney());
            stmt.setBoolean(9, landPojo.isForSale());
            DbUtils.setNullable(stmt, 10, landPojo.getForSaleSignLocationNullable(), stmt::setString);
            DbUtils.setNullable(stmt, 11, landPojo.getSalePriceNullable(), stmt::setDouble);
            stmt.setBoolean(12, landPojo.isForRent());
            DbUtils.setNullable(stmt, 13, landPojo.getForRentSignLocationNullable(), stmt::setString);
            DbUtils.setNullable(stmt, 14, landPojo.getRentPriceNullable(), stmt::setDouble);
            DbUtils.setNullable(stmt, 15, landPojo.getRentRenewNullable(), stmt::setInt);
            DbUtils.setNullable(stmt, 16, landPojo.getRentAutoRenewNullable(), stmt::setBoolean);
            DbUtils.setNullable(stmt, 17, landPojo.getTenantUUIDNullable(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            DbUtils.setNullable(stmt, 18, landPojo.getLastPaymentMillisNullable(), stmt::setLong);

            stmt.setString(19, landPojo.getName());
            stmt.setBoolean(20, landPojo.isApproved());
            DbUtils.setNullable(stmt, 21, landPojo.getTypeIdNullable(), stmt::setLong);
            stmt.setLong(22, landPojo.getOwnerId());
            DbUtils.setNullable(stmt, 23, landPojo.getParentUUIDNullable(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            stmt.setShort(24, landPojo.getPriority());
            stmt.setDouble(25, landPojo.getMoney());
            stmt.setBoolean(26, landPojo.isForSale());
            DbUtils.setNullable(stmt, 27, landPojo.getForSaleSignLocationNullable(), stmt::setString);
            DbUtils.setNullable(stmt, 28, landPojo.getSalePriceNullable(), stmt::setDouble);
            stmt.setBoolean(29, landPojo.isForRent());
            DbUtils.setNullable(stmt, 30, landPojo.getForRentSignLocationNullable(), stmt::setString);
            DbUtils.setNullable(stmt, 31, landPojo.getRentPriceNullable(), stmt::setDouble);
            DbUtils.setNullable(stmt, 32, landPojo.getRentRenewNullable(), stmt::setInt);
            DbUtils.setNullable(stmt, 33, landPojo.getRentAutoRenewNullable(), stmt::setBoolean);
            DbUtils.setNullable(stmt, 34, landPojo.getTenantUUIDNullable(), (i, u) -> DbUtils.setUUID(stmt, i, u));
            DbUtils.setNullable(stmt, 35, landPojo.getLastPaymentMillisNullable(), stmt::setLong);

            stmt.executeUpdate();
        }
    }

    public void deleteLand(final Connection conn, final UUID uuid) throws SQLException {
        final String sql = "DELETE FROM `{{TP}}lands` WHERE `uuid`=?";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            DbUtils.setUUID(stmt, 1, uuid);
            stmt.executeUpdate();
        }
    }
}