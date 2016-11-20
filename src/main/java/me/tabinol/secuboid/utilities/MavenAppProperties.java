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
package me.tabinol.secuboid.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Load app.properties from Maven properties
 *
 * @author Tabinol
 */
public class MavenAppProperties {

    /**
     * The properties.
     */
    private Properties properties;

    /**
     * Instantiates a new maven app properties.
     */
    public MavenAppProperties() {
        this.properties = new Properties();
    }

    /**
     * Load properties.
     */
    public void loadProperties() {

        try {

            File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            JarFile jar = new JarFile(jarloc);
            JarEntry entry = jar.getJarEntry("app.properties");
            InputStream resource = jar.getInputStream(entry);
            properties.load(resource);
            resource.close();
            jar.close();

        } catch (URISyntaxException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the property string.
     *
     * @param path the path
     * @return the property string
     */
    public String getPropertyString(String path) {

        return properties.getProperty(path);
    }

    /**
     * Gets the property int.
     *
     * @param path the path
     * @return the property int
     */
    public int getPropertyInt(String path) {

        return Integer.parseInt(properties.getProperty(path));
    }
}
