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
package me.tabinol.secuboid.storage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import me.tabinol.secuboid.utilities.DbUtils.SqlBiConsumer;

/**
 * DatabaseConnection
 */
public abstract class DatabaseConnection {

    protected static final String PATERN_LS_SEARCH = "{{LS}}";
    protected static final String PATERN_LS_REPLACE = System.lineSeparator();
    protected static final String PATERN_TP_SEARCH = "{{TP}}";
    protected static final String PATERN_CHARENGINE_SEARCH = "{{CHARENGINE}}";
    protected static final String PATERN_CREATEID_SEARCH = "{{CREATEID}}";
    protected static final String PATERN_PRIMARYID_SEARCH = "{{PRIMARYID}}";
    
    protected static final String REGEX_TYPEVARCHAR_SEARCH = "\\{\\{TYPEVARCHAR\\(([0-9]+)\\)\\}\\}";
    protected static final String PATERN_TYPEUUID_SEARCH = "{{TYPEUUID}}";
    protected static final String PATERN_TYPEBIGINT_SEARCH = "{{TYPEBIGINT}}";
    protected static final String PATERN_TYPEINT_SEARCH = "{{TYPEINT}}";
    protected static final String PATERN_TYPESMALLINT_SEARCH = "{{TYPESMALLINT}}";
    protected static final String PATERN_TYPEBOOLEAN_SEARCH = "{{TYPEBOOLEAN}}";
    protected static final String PATERN_TYPEDOUBLE_SEARCH = "{{TYPEDOUBLE}}";
    protected static final String PATERN_TYPEFLOAT_SEARCH = "{{TYPEFLOAT}}";
    protected static final String PATERN_TYPEMEDIUMTEXT_SEARCH = "{{TYPEMEDIUMTEXT}}";
    protected static final String PATERN_TYPEDATETIME_SEARCH = "{{TYPEDATETIME}}";
    
    public static final String PATERN_ROWID_SEARCH = "{{ROWID}}";
    protected static final String REGEX_ONDUPLICATEKEY_SEARCH = "\\{\\{ONDUPLICATEKEY\\((.*)\\)\\}\\}";

    private static final int MAX_BATCH_SIZE = 1_000;

    public abstract boolean isFunctionCompatible();

    public abstract String getRowIdName();

    abstract void loadDriver() throws ClassNotFoundException;

    public abstract Connection openConnection() throws SQLException;

    public abstract String convertStmtStrTagsCreate(String stmtStr);

    public abstract String convertStmtStrTags(String stmtStr);

    public final PreparedStatement preparedStatementWithTagsCreate(Connection conn, String sqlWithTags)
            throws SQLException {
        return conn.prepareStatement(convertStmtStrTagsCreate(sqlWithTags));
    }

    public final PreparedStatement preparedStatementWithTags(Connection conn, String sqlWithTags) throws SQLException {
        return conn.prepareStatement(convertStmtStrTags(sqlWithTags));
    }

    public abstract PreparedStatement preparedStatementWithTagsAndGeneratedKey(Connection conn, String sqlWithTags)
            throws SQLException;

    public <I> void prepareStatementAndExecuteBatch(Connection conn, String sqlWithTags, Collection<I> items,
            SqlBiConsumer<PreparedStatement, I> consumer) throws SQLException {
        try (PreparedStatement stmt = preparedStatementWithTags(conn, sqlWithTags)) {
            int it = 0;
            for (I item : items) {
                // Exec lamda expression
                consumer.accept(stmt, item);
                stmt.addBatch();
                it++;
                if (it % MAX_BATCH_SIZE == 0 || it == items.size()) {
                    stmt.executeBatch();
                }
            }
        }
    }
}