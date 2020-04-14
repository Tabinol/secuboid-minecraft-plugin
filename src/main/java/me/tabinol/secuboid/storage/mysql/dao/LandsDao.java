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
import java.util.Optional;
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
                    final Optional<Integer> typeIdOpt = DbUtils.getOpt(rs, "type_id", rs::getInt);
                    final int ownerId = rs.getInt("owner_id");
                    final Optional<UUID> parentUUIDOpt = DbUtils.getOpt(rs, "parent_uuid", c -> DbUtils.getUUID(rs, c));
                    final short priority = rs.getShort("priority");
                    final double money = rs.getDouble("money");
                    final boolean forSale = rs.getBoolean("for_sale");
                    final Optional<String> forSaleSignLocationOpt = DbUtils.getOpt(rs, "for_sale_sign_location",
                            rs::getString);
                    final Optional<Double> salePriceOpt = DbUtils.getOpt(rs, "sale_price", rs::getDouble);
                    final boolean forRent = rs.getBoolean("for_rent");
                    final Optional<String> forRentSignLocationOpt = DbUtils.getOpt(rs, "for_rent_sign_location",
                            rs::getString);
                    final Optional<Double> rentPriceOpt = DbUtils.getOpt(rs, "rent_price", rs::getDouble);
                    final Optional<Integer> rentRenewOpt = DbUtils.getOpt(rs, "rent_renew", rs::getInt);
                    final Optional<Boolean> rentAutoRenewOpt = DbUtils.getOpt(rs, "rent_auto_renew", rs::getBoolean);
                    final Optional<UUID> tenantUUIDOpt = DbUtils.getOpt(rs, "tenant_uuid", c -> DbUtils.getUUID(rs, c));
                    final Optional<Long> lastPaymentMillisOpt = DbUtils.getOpt(rs, "last_payment_millis", rs::getLong);

                    results.add(new LandPojo(uuid, name, approved, typeIdOpt, ownerId, parentUUIDOpt, priority, money,
                            forSale, forSaleSignLocationOpt, salePriceOpt, forRent, forRentSignLocationOpt,
                            rentPriceOpt, rentRenewOpt, rentAutoRenewOpt, tenantUUIDOpt, lastPaymentMillisOpt));
                }
                return results;
            }
        }
    }
}