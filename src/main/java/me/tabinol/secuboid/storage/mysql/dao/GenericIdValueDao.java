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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;
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

    public Map<I, V> getIdToValue(final Connection conn) throws SQLException {
        final String sql = String.format("SELECT `%s`, `%s` FROM `{{TP}}%s`", idColumnLabel, valueColumnLabel,
                tableSuffix);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<I, V> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final I i = getFromClass(idClazz, rs, idColumnLabel);
                    final V v = getFromClass(valueClazz, rs, valueColumnLabel);

                    results.put(i, v);
                }
                return results;
            }
        }
    }

    public Map<I, List<V>> getIdToValues(final Connection conn) throws SQLException {
        final String sql = String.format("SELECT `%s`, `%s` FROM `{{TP}}%s`", idColumnLabel, valueColumnLabel,
                tableSuffix);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<I, List<V>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final I i = getFromClass(idClazz, rs, idColumnLabel);
                    final V v = getFromClass(valueClazz, rs, valueColumnLabel);

                    results.computeIfAbsent(i, k -> new ArrayList<>()).add(v);
                }
                return results;
            }
        }
    }

    public void insert(final Connection conn, final I i, final V v) throws SQLException {
        final String sql = String.format("INSERT INTO `{{TP}}%s` (`%s`, `%s`) " //
                + "VALUES (?, ?)", tableSuffix, idColumnLabel, valueColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            setFromClass(idClazz, stmt, 1, i);
            setFromClass(valueClazz, stmt, 2, v);
            stmt.executeUpdate();
        }
    }

    public void insertOrUpdate(final Connection conn, final I i, final V v) throws SQLException {
        final String sql = String.format("INSERT INTO `{{TP}}%s` (`%s`, `%s`) " //
                + "VALUES (?, ?) " //
                + "ON DUPLICATE KEY UPDATE " //
                + "`%s`=?", tableSuffix, idColumnLabel, valueColumnLabel, valueColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            setFromClass(idClazz, stmt, 1, i);
            setFromClass(valueClazz, stmt, 2, v);
            setFromClass(valueClazz, stmt, 3, v);
            stmt.executeUpdate();
        }
    }

    public Optional<I> getIdOpt(final Connection conn, final V v) throws SQLException {
        final String sql = String.format("SELECT `%s` FROM `{{TP}}%s` WHERE `%s`=?", idColumnLabel, tableSuffix,
                valueColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            setFromClass(valueClazz, stmt, 1, v);
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return Optional.of(getFromClass(idClazz, rs, idColumnLabel));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<V> getValueOpt(final Connection conn, final I i) throws SQLException {
        final String sql = String.format("SELECT `%s` FROM `{{TP}}%s` WHERE `%s`=?", valueColumnLabel, tableSuffix,
                idColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            setFromClass(idClazz, stmt, 1, i);
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return Optional.of(getFromClass(valueClazz, rs, valueColumnLabel));
                }
                return Optional.empty();
            }
        }
    }

    public long insertOrGetId(final Connection conn, final V v) throws SQLException {
        assert idClazz.isAssignableFrom(Long.class);

        final Optional<I> idOpt = getIdOpt(conn, v);
        if (idOpt.isPresent()) {
            return ((Long) idOpt.get()).longValue();
        }

        final String sql = String.format("INSERT INTO `{{TP}}%s` SET `%s`=?", tableSuffix, valueColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql,
                Statement.RETURN_GENERATED_KEYS)) {
            setFromClass(valueClazz, stmt, 1, v);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public void delete(final Connection conn, final I i, final V v) throws SQLException {
        final String sql = String.format("DELETE FROM `{{TP}}%s` WHERE `%s`=? AND `%s`=?", tableSuffix, idColumnLabel,
                valueColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            setFromClass(idClazz, stmt, 1, i);
            setFromClass(valueClazz, stmt, 2, v);
            stmt.executeUpdate();
        }
    }

    public void delete(final Connection conn, final I i) throws SQLException {
        final String sql = String.format("DELETE FROM `{{TP}}%s` WHERE `%s`=?", tableSuffix, idColumnLabel);

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            setFromClass(idClazz, stmt, 1, i);
            stmt.executeUpdate();
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
        if (clazz.isAssignableFrom(Boolean.class)) {
            return (C) new Boolean(rs.getBoolean(columnLabel));
        }

        classNotSupported(clazz);
        return null;
    }

    private <C> void setFromClass(final Class<C> clazz, final PreparedStatement stmt, final int parameterIndex,
            final C c) throws SQLException {
        if (clazz.isAssignableFrom(Integer.class)) {
            stmt.setInt(parameterIndex, (Integer) c);
        } else if (clazz.isAssignableFrom(Long.class)) {
            stmt.setLong(parameterIndex, (Long) c);
        } else if (clazz.isAssignableFrom(Double.class)) {
            stmt.setDouble(parameterIndex, (Double) c);
        } else if (clazz.isAssignableFrom(UUID.class)) {
            DbUtils.setUUID(stmt, parameterIndex, (UUID) c);
        } else if (clazz.isAssignableFrom(String.class)) {
            stmt.setString(parameterIndex, (String) c);
        } else if (clazz.isAssignableFrom(Boolean.class)) {
            stmt.setBoolean(parameterIndex, (Boolean) c);
        } else {
            classNotSupported(clazz);
        }
    }

    private <C> void classNotSupported(final Class<C> clazz) {
        throw new SecuboidRuntimeException(
                String.format("Class %s not supported by Generic id/value dao", clazz.getName()));
    }
}