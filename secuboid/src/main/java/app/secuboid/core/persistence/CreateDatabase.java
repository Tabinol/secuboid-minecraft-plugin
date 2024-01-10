/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.secuboid.core.persistence;

import app.secuboid.api.exceptions.SecuboidRuntimeException;
import app.secuboid.api.persistence.CreateTable;
import app.secuboid.api.persistence.JPA;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.core.registration.RegistrationServiceImpl;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static app.secuboid.api.persistence.WithId.NON_EXISTING_ID;

@RequiredArgsConstructor
public class CreateDatabase {

    private static final String CREATE_TABLE_TABLE_VERSION = """
            CREATE TABLE IF NOT EXISTS `secuboid_table_version` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `clazz` varchar(255) NOT NULL,
              `version` int(11) NOT NULL,
            PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
            """;

    private static final int NOT_YET_CREATED_VERSION = 0;

    private final RegistrationService registrationService;
    private final String url;
    private final String user;
    private final String password;

    public void createDatabases() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            execCreateTable(conn, CREATE_TABLE_TABLE_VERSION);
            Map<String, TableVersion> classNameToTableVersion = loadClassNameToTableVersion(conn);
            Map<Class<? extends JPA>, CreateTable> jpaClassToCreateTable =
                    ((RegistrationServiceImpl) registrationService).getJpaClassToCreateTable();

            for (Map.Entry<Class<? extends JPA>, CreateTable> entry : jpaClassToCreateTable.entrySet()) {
                Class<? extends JPA> jpaClass = entry.getKey();
                CreateTable createTable = entry.getValue();
                String className = jpaClass.getSimpleName();
                TableVersion tableVersion = classNameToTableVersion.getOrDefault(className,
                        new TableVersion(NON_EXISTING_ID, className, NOT_YET_CREATED_VERSION));

                createDatabase(conn, tableVersion, createTable);
            }
        } catch (SQLException e) {
            throw new SecuboidRuntimeException("Unable to connect to the database... Check plugins/Secuboid/config.yml", e);
        }
    }

    private Map<String, TableVersion> loadClassNameToTableVersion(Connection conn) throws SQLException {
        Map<String, TableVersion> classNameToTableVersion = new HashMap<>();

        String sql = "SELECT `id`, `clazz`, `version` FROM `secuboid_table_version`";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String clazz = rs.getString("clazz");
                    int version = rs.getInt("version");
                    TableVersion tableVersion = new TableVersion(id, clazz, version);

                    classNameToTableVersion.put(tableVersion.getClazz(), tableVersion);
                }
            }
        }

        return classNameToTableVersion;
    }

    private void createDatabase(Connection conn, TableVersion tableVersion, CreateTable createTable) throws SQLException {
        String[] createTableQueries = createTable.value();

        int currentVersion = tableVersion.getVersion();
        while (currentVersion < createTableQueries.length) {
            execCreateTable(conn, createTableQueries[currentVersion]);
            tableVersion.setVersion(++currentVersion);
            insertOrUpdateVersion(conn, tableVersion);
        }
    }

    private void execCreateTable(Connection conn, String createTableQuery) throws SQLException {
        try (PreparedStatement stm = conn.prepareStatement(createTableQuery)) {
            stm.executeUpdate();
        }
    }

    private void insertOrUpdateVersion(Connection conn, TableVersion tableVersion) throws SQLException {
        if (tableVersion.getId() == NON_EXISTING_ID) {
            insertVersion(conn, tableVersion);
        } else {
            updateVersion(conn, tableVersion);
        }
    }

    private void insertVersion(Connection conn, TableVersion tableVersion) throws SQLException {
        String sql = "INSERT INTO `secuboid_table_version` (`clazz`, `version`) VALUES (?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, tableVersion.getClazz());
            stm.setInt(2, tableVersion.getVersion());

            stm.executeUpdate();
        }
    }

    private void updateVersion(Connection conn, TableVersion tableVersion) throws SQLException {
        String sql = "UPDATE `secuboid_table_version` SET `clazz` = ?, `version` = ? WHERE `id` = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, tableVersion.getClazz());
            stm.setInt(2, tableVersion.getVersion());
            stm.setLong(3, tableVersion.getId());

            stm.executeUpdate();
        }
    }

}
