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
package me.tabinol.secuboid.storage.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection
 */
public final class DatabaseConnection {

    private static final String PATERN_LS_SEARCH = "{{LS}}";
    private static final String PATERN_LS_REPLACE = System.lineSeparator();
    private static final String PATERN_TP_SEARCH = "{{TP}}";

    private final String hostName;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private final String prefix;

    public DatabaseConnection(final String hostName, final int port, final String database, final String user,
            final String password, final String prefix) {
        this.hostName = hostName;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.prefix = prefix;
    }

    void loadDriver() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }

    public Connection openConnection() throws SQLException {
        final String url = String.format("jdbc:mysql://%s:%s/%s", hostName, port, database);
        return DriverManager.getConnection(url, user, password);
    }

    public String convertStmtStrTags(final String stmtStr) {
        return stmtStr //
                .replace(PATERN_LS_SEARCH, PATERN_LS_REPLACE) //
                .replace(PATERN_TP_SEARCH, prefix);
    }
}