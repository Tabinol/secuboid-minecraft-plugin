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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.storage.mysql.pojo.PermissionPojo;
import me.tabinol.secuboid.utilities.DbUtils;

public final class PermissionsDao {

    private final DatabaseConnection dbConn;

    public PermissionsDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public Map<UUID, List<PermissionPojo>> getLandUUIDToPermissions(final Connection conn) throws SQLException {
        final String sql = "SELECT `land_uuid`, `player_container_id`, `permission_id`, `value`, `inheritance` " //
                + "FROM `{{TP}}lands_permissions`";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final Map<UUID, List<PermissionPojo>> results = new HashMap<>();
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    final UUID landUUID = DbUtils.getUUID(rs, "land_uuid");
                    final int playerContainerId = rs.getInt("player_container_id");
                    final int permissionId = rs.getInt("permission_id");
                    final boolean value = rs.getBoolean("value_boolean");
                    final boolean inheritance = rs.getBoolean("inheritance");
                    final PermissionPojo permissionPojo = new PermissionPojo(landUUID, playerContainerId, permissionId,
                            value, inheritance);

                    results.computeIfAbsent(landUUID, k -> new ArrayList<>()).add(permissionPojo);
                }
                return results;
            }
        }
    }
}