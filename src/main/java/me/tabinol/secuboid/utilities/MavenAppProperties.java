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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.Bukkit;

/**
 * Load app.properties from Maven properties
 *
 * @author Tabinol
 */
public final class MavenAppProperties {

    private static final String APP_PROPERTIES_FILENAME = "app.properties";

    /**
     * The properties.
     */
    private static final Properties properties = new Properties();

    /**
     * Load properties.
     */
    public void loadProperties() {
        try {
            final File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getCanonicalFile();
            try (final JarFile jar = new JarFile(jarloc)) {
                final JarEntry entry = jar.getJarEntry(APP_PROPERTIES_FILENAME);
                try (final InputStream resource = jar.getInputStream(entry)) {
                    properties.load(resource);
                }
            }
        } catch (final URISyntaxException | IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to load the Secuboid properties file inside de .jar", e);
        }
    }

    /**
     * Load properties from resources when the file is not inside a jar (ex: tests).
     *
     * @throws IOException IOException
     */
    public void loadPropertiesFromResources() throws IOException {
        try (final InputStream resource = getClass().getClassLoader().getResourceAsStream(APP_PROPERTIES_FILENAME)) {
            properties.load(resource);
        }
    }

    /**
     * Gets the property string.
     *
     * @param path         the path
     * @param defaultValue the default value
     * @return the property string
     */
    public static String getPropertyString(final String path, final String defaultValue) {
        return properties.getProperty(path, defaultValue);
    }

    /**
     * Gets the property int.
     *
     * @param path            the path
     * @param defaultValueInt the default value integer
     * @return the property int
     */
    public static int getPropertyInt(final String path, final int defaultValueInt) {
        final String value = properties.getProperty(path);
        if (value == null) {
            return defaultValueInt;
        }
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            return defaultValueInt;
        }
    }
}
