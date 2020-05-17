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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.exceptions.SecuboidRuntimeException;

public final class DbUtils {

    @FunctionalInterface
    public static interface SqlBiConsumer<T, U> {
        void accept(T t, U u) throws SQLException;
    }

    @FunctionalInterface
    public static interface SqlFunction<T, R> {
        R apply(T t) throws SQLException;
    }

    @FunctionalInterface
    public interface SqlConsumer<T> {
        void accept(T t) throws SQLException;
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

        if (timestamp == null) {
            return null;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar;
    }

    public static byte[] setUUID(final PreparedStatement stmt, final int parameterIndex, final UUID uuid)
            throws SQLException {
        final long high = uuid.getMostSignificantBits();
        final long low = uuid.getLeastSignificantBits();

        final byte[] bytes = new byte[] { //
                (byte) (high >> 8), //
                (byte) high, //
                (byte) (high >> 24), //
                (byte) (high >> 16), //
                (byte) (high >> 56), //
                (byte) (high >> 48), //
                (byte) (high >> 40), //
                (byte) (high >> 32), //
                (byte) (low >> 56), //
                (byte) (low >> 48), //
                (byte) (low >> 40), //
                (byte) (low >> 32), //
                (byte) (low >> 24), //
                (byte) (low >> 16), //
                (byte) (low >> 8), //
                (byte) low //
        };

        stmt.setBytes(parameterIndex, bytes);
        return bytes;
    }

    public static UUID getUUID(final ResultSet rs, final String columnLabel) throws SQLException {
        final byte[] bytes = rs.getBytes(columnLabel);

        if (bytes == null) {
            return null;
        }

        final Long high = ((long) bytes[4] << 56) //
                | ((long) bytes[5] & 0xff) << 48 //
                | ((long) bytes[6] & 0xff) << 40 //
                | ((long) bytes[7] & 0xff) << 32 //
                | ((long) bytes[2] & 0xff) << 24 //
                | ((long) bytes[3] & 0xff) << 16 //
                | ((long) bytes[0] & 0xff) << 8 //
                | ((long) bytes[1] & 0xff);

        final Long low = ((long) bytes[8] << 56) //
                | ((long) bytes[9] & 0xff) << 48 //
                | ((long) bytes[10] & 0xff) << 40 //
                | ((long) bytes[11] & 0xff) << 32 //
                | ((long) bytes[12] & 0xff) << 24 //
                | ((long) bytes[13] & 0xff) << 16 //
                | ((long) bytes[14] & 0xff) << 8 //
                | ((long) bytes[15] & 0xff);

        return new UUID(high, low);
    }

    public static void setMatrix16(final PreparedStatement stmt, final int parameterIndex, final short[] matrix)
            throws SQLException {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[32]);
        for (final short elem : matrix) {
            byteBuffer.putShort(elem);
        }
        stmt.setBytes(parameterIndex, byteBuffer.array());
    }

    public static short[] getMatrix16(final ResultSet rs, final String columnLabel) throws SQLException {
        final byte[] bytes = rs.getBytes(columnLabel);

        if (bytes == null) {
            return null;
        }

        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final short[] matrix = new short[16];
        for (int i = 0; i < 16; i++) {
            matrix[i] = byteBuffer.getShort();
        }
        return matrix;
    }

    public static void setItemStacks(final PreparedStatement stmt, final int parameterIndex,
            final ItemStack[] itemStacks) throws SQLException {
        final YamlConfiguration itemStackYaml = new YamlConfiguration();
        for (int t = 0; t < itemStacks.length; t++) {
            itemStackYaml.set(Integer.toString(t), itemStacks[t]);
        }
        stmt.setString(parameterIndex, itemStackYaml.saveToString());
    }

    public static ItemStack[] getItemStacks(final ResultSet rs, final String columnLabel, final int length)
            throws SQLException {
        final String itemStackStr = rs.getString(columnLabel);

        if (itemStackStr == null) {
            return null;
        }

        final YamlConfiguration itemStackYaml;
        try (final Reader reader = new StringReader(itemStackStr)) {
            itemStackYaml = YamlConfiguration.loadConfiguration(reader);
        } catch (final IOException e) {
            // This error should never happend!
            throw new SecuboidRuntimeException(e);
        }

        final ItemStack[] itemStacks = new ItemStack[length];
        for (int t = 0; t < length; t++) {
            itemStacks[t] = itemStackYaml.getItemStack(Integer.toString(t), null);
        }
        return itemStacks;
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
            return Optional.empty();
        }
        return Optional.of(r);
    }

    public static <V> String isNullOrEquals(Optional<V> valueOpt) {
        return valueOpt.map(v -> "=?").orElse(" IS NULL");
    }
}