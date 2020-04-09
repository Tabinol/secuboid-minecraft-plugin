/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2015 Tabinol
 Forked from Factoid (Copyright (C) 2014 Kaz00, Tabinol)

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
                + "`parent_uuid`, `priority`, `money`, `for_sale`, `for_sale_sign_x`, " //
                + "`for_sale_sign_y`, `for_sale_sign_z`, `sale_price`, `for_rent`, " //
                + "`for_rent_sign_x`, `for_rent_sign_y`, `for_rent_sign_z`, `rent_price`, " //
                + "`rent_renew`, `rent_auto_renew`, `tenant_id`, `last_payment_millis` " //
                + "FROM `{{TP}}lands`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final List<LandPojo> results = new ArrayList<>();
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                final UUID uuid = DbUtils.getUUID(rs, "uuid");
                final String name = rs.getString("name");
                final boolean approved = rs.getBoolean("approved");
                final Optional<Integer> typeIdOpt = DbUtils.getOpt(rs, "type_id", rs::getInt);
                final int ownerId = rs.getInt("owner_id");
                final Optional<UUID> parentUuidOpt = DbUtils.getOpt(rs, "parent_uuid", c -> DbUtils.getUUID(rs, c));
                final int priority = rs.getInt("priority");
                final double money = rs.getDouble("money");
                final boolean forSale = rs.getBoolean("for_sale");
                final Optional<Integer> forSaleSignXOpt = DbUtils.getOpt(rs, "for_sale_sign_x", rs::getInt);
                final Optional<Integer> forSaleSignYOpt = DbUtils.getOpt(rs, "for_sale_sign_y", rs::getInt);
                final Optional<Integer> forSaleSignZOpt = DbUtils.getOpt(rs, "for_sale_sign_z", rs::getInt);
                final Optional<Double> salePriceOpt = DbUtils.getOpt(rs, "sale_price", rs::getDouble);
                final boolean forRent = rs.getBoolean("for_rent");
                final Optional<Integer> forRentSignXOpt = DbUtils.getOpt(rs, "for_rent_sign_x", rs::getInt);
                final Optional<Integer> forRentSignYOpt = DbUtils.getOpt(rs, "for_rent_sign_y", rs::getInt);
                final Optional<Integer> forRentSignZOpt = DbUtils.getOpt(rs, "for_rent_sign_z", rs::getInt);
                final Optional<Double> rentPriceOpt = DbUtils.getOpt(rs, "rent_price", rs::getDouble);
                final Optional<Boolean> rentRenewOpt = DbUtils.getOpt(rs, "rent_renew", rs::getBoolean);
                final Optional<Boolean> rentAutoRenewOpt = DbUtils.getOpt(rs, "rent_auto_renew", rs::getBoolean);
                final Optional<Integer> tenantIdOpt = DbUtils.getOpt(rs, "tenant_id", rs::getInt);
                final Optional<Long> lastPaymentMillisOpt = DbUtils.getOpt(rs, "last_payment_millis", rs::getLong);

                results.add(new LandPojo(uuid, name, approved, typeIdOpt, ownerId, parentUuidOpt, priority, money,
                        forSale, forSaleSignXOpt, forSaleSignYOpt, forSaleSignZOpt, salePriceOpt, forRent,
                        forRentSignXOpt, forRentSignYOpt, forRentSignZOpt, rentPriceOpt, rentRenewOpt, rentAutoRenewOpt,
                        tenantIdOpt, lastPaymentMillisOpt));
            }
            return results;
        }
    }
}