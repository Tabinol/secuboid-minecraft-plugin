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
package app.secuboid.core.config;

import app.secuboid.api.services.Service;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
public class ConfigService implements Service {

    private final JavaPlugin javaPlugin;

    private String lang;
    private String databaseHost;
    private int databasePort;
    private String databaseDatabase;
    private String databaseUser;
    private String databasePassword;
    private int selectionDefaultStartDiameter;

    @Override
    public void onEnable(boolean isServerBoot) {
        if (isServerBoot) {
            javaPlugin.saveDefaultConfig();
        } else {
            javaPlugin.reloadConfig();
        }

        load(javaPlugin.getConfig());
    }

    private void load(FileConfiguration fileConfiguration) {
        fileConfiguration.addDefault("lang", "en");
        lang = fileConfiguration.getString("lang");
        fileConfiguration.addDefault("database.host", "localhost");
        databaseHost = fileConfiguration.getString("database.host");
        fileConfiguration.addDefault("database.port", 3306);
        databasePort = fileConfiguration.getInt("database.port");
        fileConfiguration.addDefault("database.database", "secuboid");
        databaseDatabase = fileConfiguration.getString("database.database");
        fileConfiguration.addDefault("database.user", "secuboid");
        databaseUser = fileConfiguration.getString("database.user");
        fileConfiguration.addDefault("database.password", "secuboid");
        databasePassword = fileConfiguration.getString("database.password");
        fileConfiguration.addDefault("selection.default-start-diameter", 3);
        selectionDefaultStartDiameter = fileConfiguration.getInt("selection.default-start-diameter");
    }
}
