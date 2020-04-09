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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.utilities.DbUtils;

/**
 * Generic DAO for tables with id and name.
 */
public final class GenericIdValueDao<I, V> {

    public enum IdNameTableSuffix {
        LANDS_TYPES, player_containers_types, areas_types
    }

    private final Class<I> idClazz;
    private final Class<V> valueClazz;
    private final DatabaseConnection dbConn;
    private final String tableSuffix;
    private final String idColumnLabel;
    private final String valueColumnLabel;

    public GenericIdValueDao(final DatabaseConnection dbConn, final Class<I> idClazz, final Class<V> valueClazz,
            final String tableSuffix, final String idColumnLabel, final String valueColumnLabel) {
        this.dbConn = dbConn;
        this.idClazz = idClazz;
        this.valueClazz = valueClazz;
        this.tableSuffix = tableSuffix;
        this.idColumnLabel = idColumnLabel;
        this.valueColumnLabel = valueColumnLabel;
    }

    public Map<I, V> getIdNames(final Connection conn) throws SQLException {
        final String sql = String.format("SELECT `%s`, `%s` FROM `{{TP}}%s`", idColumnLabel, valueColumnLabel,
                tableSuffix);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<I, V> results = new HashMap<>();
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                final I i = getFromClass(idClazz, rs, idColumnLabel);
                final V v = getFromClass(valueClazz, rs, valueColumnLabel);

                results.put(i, v);
            }
            return results;
        }
    }

    @SuppressWarnings("unchecked")
    private <C> C getFromClass(final Class<C> clazz, final ResultSet rs, final String columnLabel) throws SQLException {
        if (clazz.isAssignableFrom(Integer.class)) {
            return (C) new Integer(rs.getInt(columnLabel));
        }
        if (clazz.isAssignableFrom(Long.class)) {
            return (C) new Long(rs.getLong(columnLabel));
        }
        if (clazz.isAssignableFrom(Double.class)) {
            return (C) new Double(rs.getDouble(columnLabel));
        }
        if (clazz.isAssignableFrom(UUID.class)) {
            return (C) DbUtils.getUUID(rs, columnLabel);
        }
        if (clazz.isAssignableFrom(String.class)) {
            return (C) rs.getString(columnLabel);
        }
        return rs.getObject(columnLabel, clazz);
    }

    private <C> void setFromClass(final Class<C> clazz, final PreparedStatement stmt, final int parameterIndex,
            final C c) throws SQLException {
        if (clazz.isAssignableFrom(Integer.class)) {
            stmt.setInt(parameterIndex, (int) c);
        }
        if (clazz.isAssignableFrom(Long.class)) {
            stmt.setLong(parameterIndex, (long) c);
        }
        if (clazz.isAssignableFrom(Double.class)) {
            stmt.setDouble(parameterIndex, (double) c);
        }
        if (clazz.isAssignableFrom(UUID.class)) {
            DbUtils.setUUID(stmt, parameterIndex, (UUID) c);
        }
        if (clazz.isAssignableFrom(String.class)) {
            stmt.setString(parameterIndex, (String) c);
        }
        stmt.setObject(parameterIndex, c);
    }
}