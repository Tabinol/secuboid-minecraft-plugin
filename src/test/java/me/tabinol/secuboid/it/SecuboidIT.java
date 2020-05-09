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
package me.tabinol.secuboid.it;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import me.tabinol.secuboid.it.utilities.WebUtils;
import me.tabinol.secuboid.utilities.MavenAppProperties;

/**
 * Integration tests class
 */
public final class SecuboidIT {

    private static final String BUILDTOOLS_URL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
    private static final String VAULT_URL = "https://jitpack.io/com/github/MilkBowl/Vault/{{VERSION}}/Vault-{{VERSION}}.jar";

    private static final File IT_CACHE_DIR_FILE = new File("itcache");
    private static final File WORK_DIR_FILE = new File("target/itwork");
    private static final File SERVER_DIR_FILE = new File("target/itwork/server");
    private static final File PLUGINS_DIR_FILE = new File("target/itwork/server/plugins");
    private static final File EULA_FILE = new File("target/itwork/server/eula.txt");

    private static final String SECUBOID_FILENAME = "secuboid-{{VERSION}}.jar";
    private static final String SPIGOT_FILENAME = "spigot-{{VERSION}}.jar";
    private static final String BUILDTOOLS_FILENAME = "BuildTools.jar";
    private static final String BUILDTOOLS_WORK_DIRNAME = "buildtoolswork";
    private static final String VAULT_FILENAME = "Vault-{{VERSION}}.jar";

    @Test
    public void executeIT() throws FileNotFoundException, IOException, InterruptedException {
        new MavenAppProperties().loadPropertiesFromResources();
        final String secuboidVersion = MavenAppProperties.getPropertyString("application.version", "1.5.0");
        final File secuboidSourceFile = new File("target/" + SECUBOID_FILENAME.replace("{{VERSION}}", secuboidVersion));
        if (!secuboidSourceFile.exists()) {
            fail("Please make the secuboid jar file before running this test (mvn package)");
        }

        final String spigotVersion = MavenAppProperties.getPropertyString("spigot.version", "1.15.2");
        final File spigotFile = new File(IT_CACHE_DIR_FILE, SPIGOT_FILENAME.replace("{{VERSION}}", spigotVersion));

        // Remove old data and create new dirs
        if (!IT_CACHE_DIR_FILE.exists()) {
            IT_CACHE_DIR_FILE.mkdirs();
        }

        if (WORK_DIR_FILE.exists()) {
            FileUtils.deleteDirectory(WORK_DIR_FILE);
        }
        WORK_DIR_FILE.mkdirs();

        if (!spigotFile.exists()) {
            prepareSpigot(spigotFile, spigotVersion);
        }

        // Vault
        final String vaultVersion = MavenAppProperties.getPropertyString("vault.version", "1.7.2");
        final File vaultFile = new File(IT_CACHE_DIR_FILE, VAULT_FILENAME.replace("{{VERSION}}", vaultVersion));
        if (!vaultFile.exists()) {
            WebUtils.wget(VAULT_URL.replace("{{VERSION}}", vaultVersion), vaultFile);
        }

        // Prepare server dir
        prepareServerDir(spigotFile, vaultFile, secuboidSourceFile);

        // TODO Exec Spigot and test it
    }

    private void prepareSpigot(final File spigotFile, final String spigotVersion)
            throws IOException, InterruptedException {

        final File buildtoolsWorkDir = new File(WORK_DIR_FILE, BUILDTOOLS_WORK_DIRNAME);
        final File buildtoolsFile = new File(buildtoolsWorkDir, BUILDTOOLS_FILENAME);
        buildtoolsWorkDir.mkdirs();

        // Get file
        if (!buildtoolsFile.exists()) {
            WebUtils.wget(BUILDTOOLS_URL, buildtoolsFile);
        }

        // Build Spigot
        buildtoolsWorkDir.mkdirs();
        final Process proc = Runtime.getRuntime().exec(
                new String[] { "java", "-jar", buildtoolsFile.getAbsolutePath(), "--rev", spigotVersion }, null,
                buildtoolsWorkDir);
        final InputStream inputStream = proc.getInputStream();
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        proc.waitFor();

        // Copy Spigot file to cache
        FileUtils.copyFile(new File(buildtoolsWorkDir, spigotFile.getName()), spigotFile);
    }

    private void prepareServerDir(final File spigotFile, final File vaultFile, final File secuboidSourceFile)
            throws IOException {
        SERVER_DIR_FILE.mkdirs();
        PLUGINS_DIR_FILE.mkdir();
        FileUtils.copyFile(spigotFile, new File(SERVER_DIR_FILE, spigotFile.getName()));
        FileUtils.copyFile(secuboidSourceFile, new File(PLUGINS_DIR_FILE, secuboidSourceFile.getName()));
        FileUtils.copyFile(vaultFile, new File(PLUGINS_DIR_FILE, vaultFile.getName()));

        // Write EULA
        FileUtils.writeStringToFile(EULA_FILE, "eula=true", Charset.defaultCharset());
    }
}