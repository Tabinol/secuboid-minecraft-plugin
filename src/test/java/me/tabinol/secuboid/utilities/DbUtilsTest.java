package me.tabinol.secuboid.utilities;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.Test;

public final class DbUtilsTest {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    @Test
    public void setUUIDTest() throws SQLException {
        final PreparedStatement stmt = mock(PreparedStatement.class);
        final UUID uuid = UUID.fromString("6b0fd9b6-4ff2-413d-9158-6f9f8b930e5b");

        final byte[] bytes = DbUtils.setUUID(stmt, 1, uuid);

        assertEquals("413D4FF26B0FD9B691586F9F8B930E5B", bytesToHexString(bytes));
    }

    @Test
    public void getUUIDTest() throws SQLException {
        final byte[] bytes = hexStringToByteArray("413D4FF26B0FD9B691586F9F8B930E5B");
        final ResultSet rs = mock(ResultSet.class);
        when(rs.getBytes(anyString())).thenReturn(bytes);

        final UUID uuid = DbUtils.getUUID(rs, "test");

        assertEquals("6b0fd9b6-4ff2-413d-9158-6f9f8b930e5b", uuid.toString());
    }

    private String bytesToHexString(final byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            final int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(final String hexStr) {
        final int hexStrLength = hexStr.length();
        final byte[] bytes = new byte[hexStrLength / 2];
        for (int i = 0; i < hexStrLength; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                    + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return bytes;
    }
}