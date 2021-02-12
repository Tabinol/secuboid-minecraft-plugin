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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.util.Strings;

public final class DatabaseConnectionSqlite extends DatabaseConnection {

    private static final String REGEX_TYPEVARCHAR_REPLACE = "VARCHAR($1)";
    
    private static final String PATERN_TYPEUUID_REPLACE = "BLOB";
    private static final String PATERN_TYPEBIGINT_REPLACE = "INTEGER";
    private static final String PATERN_TYPEINT_REPLACE = "INTEGER";
    private static final String PATERN_TYPESMALLINT_REPLACE = "INTEGER";
    private static final String PATERN_TYPEBOOLEAN_REPLACE = "NUMERIC";
    private static final String PATERN_TYPEDOUBLE_REPLACE = "REAL";
    private static final String PATERN_TYPEFLOAT_REPLACE = "REAL";
    private static final String PATERN_TYPEMEDIUMTEXT_REPLACE = "MEDIUMTEXT";
    private static final String PATERN_TYPEDATETIME_REPLACE = "NUMERIC";

    private static final String PATERN_ROWID_REPLACE = "rowid";
    private static final String REGEX_ONDUPLICATEKEY_REPLACE = "ON CONFLICT($1) DO UPDATE SET";

    private final String url;

    public DatabaseConnectionSqlite(String filePath) {
        this.url = "jdbc:sqlite:" + filePath;
    }

    @Override
    public boolean isFunctionCompatible() {
        return false;
    }

    @Override
    public String getRowIdName() {
        return PATERN_ROWID_REPLACE;
    }

    @Override
    void loadDriver() throws ClassNotFoundException {
        // No driver to load
    }

    @Override
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public String convertStmtStrTagsCreate(String stmtStr) {
        return stmtStr //
                .replace(PATERN_LS_SEARCH, PATERN_LS_REPLACE) //
                .replace(PATERN_TP_SEARCH, Strings.EMPTY) //
                .replace(PATERN_CHARENGINE_SEARCH, Strings.EMPTY) //
                .replace(PATERN_CREATEID_SEARCH, Strings.EMPTY) //
                .replace(PATERN_PRIMARYID_SEARCH, Strings.EMPTY) //
                .replaceAll(REGEX_TYPEVARCHAR_SEARCH, REGEX_TYPEVARCHAR_REPLACE) //
                .replace(PATERN_TYPEUUID_SEARCH, PATERN_TYPEUUID_REPLACE) //
                .replace(PATERN_TYPEBIGINT_SEARCH, PATERN_TYPEBIGINT_REPLACE) //
                .replace(PATERN_TYPEINT_SEARCH, PATERN_TYPEINT_REPLACE) //
                .replace(PATERN_TYPESMALLINT_SEARCH, PATERN_TYPESMALLINT_REPLACE) //
                .replace(PATERN_TYPEBOOLEAN_SEARCH, PATERN_TYPEBOOLEAN_REPLACE) //
                .replace(PATERN_TYPEDOUBLE_SEARCH, PATERN_TYPEDOUBLE_REPLACE) //
                .replace(PATERN_TYPEFLOAT_SEARCH, PATERN_TYPEFLOAT_REPLACE) //
                .replace(PATERN_TYPEMEDIUMTEXT_SEARCH, PATERN_TYPEMEDIUMTEXT_REPLACE) //
                .replace(PATERN_TYPEDATETIME_SEARCH, PATERN_TYPEDATETIME_REPLACE);
    }

    @Override
    public String convertStmtStrTags(String stmtStr) {
        return stmtStr //
                .replace(PATERN_LS_SEARCH, PATERN_LS_REPLACE) //
                .replace(PATERN_TP_SEARCH, Strings.EMPTY) //
                .replace(PATERN_ROWID_SEARCH, PATERN_ROWID_REPLACE) //
                .replaceAll(REGEX_ONDUPLICATEKEY_SEARCH, REGEX_ONDUPLICATEKEY_REPLACE);
    }

    @Override
    public PreparedStatement preparedStatementWithTagsAndGeneratedKey(Connection conn, String sqlWithTags)
            throws SQLException {
        return conn.prepareStatement(convertStmtStrTags(sqlWithTags));
    }
}
