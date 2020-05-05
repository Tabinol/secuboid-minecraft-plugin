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
package me.tabinol.secuboid.utilities;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

public final class DbUtils {

    @FunctionalInterface
    public static interface SqlBiConsumer<T, U> {
        void accept(T t, U u) throws SQLException;
    }

    @FunctionalInterface
    public static interface SqlFunction<T, R> {
        R apply(T t) throws SQLException;
    }

    private DbUtils() {
    }

    public static void setCalendar(final PreparedStatement stmt, final int parameterIndex, final Calendar calendar)
            throws SQLException {
        final Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        stmt.setTimestamp(parameterIndex, timestamp);
    }

    public static Calendar getCalendar(final ResultSet rs, final String columnLabel) throws SQLException {
        final Timestamp timestamp = rs.getTimestamp(columnLabel);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar;
    }

    public static void setUUID(final PreparedStatement stmt, final int parameterIndex, final UUID uuid)
            throws SQLException {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        stmt.setBytes(parameterIndex, byteBuffer.array());
    }

    public static UUID getUUID(final ResultSet rs, final String columnLabel) throws SQLException {
        final byte[] bytes = rs.getBytes(columnLabel);
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final Long high = byteBuffer.getLong();
        final Long low = byteBuffer.getLong();
        return new UUID(high, low);
    }

    public static void setMatrixs16(final PreparedStatement stmt, final int parameterIndex, final short[] matrix)
            throws SQLException {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        for (final short elem : matrix) {
            byteBuffer.putShort(elem);
        }
        stmt.setBytes(parameterIndex, byteBuffer.array());
    }

    public static short[] getMatrix16(final ResultSet rs, final String columnLabel) throws SQLException {
        final byte[] bytes = rs.getBytes(columnLabel);
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final short[] matrix = new short[16];
        for (int i = 0; i < 16; i++) {
            matrix[i] = byteBuffer.getShort();
        }
        return matrix;
    }

    public static <U> void setOpt(final PreparedStatement stmt, final int parameterIndex, final Optional<U> uOpt,
            final SqlBiConsumer<Integer, U> consumer) throws SQLException {
        if (uOpt.isPresent()) {
            consumer.accept(parameterIndex, uOpt.get());
        } else {
            stmt.setNull(parameterIndex, Types.NULL);
        }
    }

    public static <R> Optional<R> getOpt(final ResultSet rs, final String columnLabel,
            final SqlFunction<String, R> function) throws SQLException {
        final R r = function.apply(columnLabel);
        if (rs.wasNull()) {
            return null;
        }
        return Optional.of(r);
    }
}