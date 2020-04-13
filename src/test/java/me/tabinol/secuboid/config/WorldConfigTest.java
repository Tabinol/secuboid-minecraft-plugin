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
package me.tabinol.secuboid.config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.tabinol.secuboid.NewInstance;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.bukkit.LogHandler;
import me.tabinol.secuboid.config.WorldConfig.FileType;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerEverybody;
import me.tabinol.secuboid.playercontainer.PlayerContainerGroup;

/**
 * WorldConfigTest
 */
public final class WorldConfigTest {

    Secuboid secuboid;
    WorldConfig worldConfig;
    Logger logger;
    LogHandler logHandler;

    @Before
    public void init() {
        secuboid = mock(Secuboid.class);
        final NewInstance newInstance = new NewInstance(secuboid);
        when(secuboid.getNewInstance()).thenReturn(newInstance);

        // Logger
        logHandler = new LogHandler(this.getClass());
        logger = logHandler.createLogger();
        when(secuboid.getLogger()).thenReturn(logger);

        // Permissions flags
        final PermissionsFlags permissionsFlags = new PermissionsFlags(secuboid);
        when(secuboid.getPermissionsFlags()).thenReturn(permissionsFlags);

        worldConfig = new WorldConfig(secuboid);
    }

    @Test
    public void loadDataYmlWrongFormatTest() {
        final String yml = "wrong format";
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(logHandler.isMessageContainsAny("The file format is incorrect"));
    }

    @Test
    public void loadDataYmlMustBeListTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  notList:") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(logHandler.isMessageContainsAny("must be a list"));
    }

    @Test
    public void loadDataYmlMustWrongKeyTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - wrongKey: wrong value") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(logHandler.isMessageContainsAny("invalid key"));
    }

    @Test
    public void loadDataYmlFlagBooleanTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - flags: [FIRE, EXPLOSION]") //
                .append(Config.NEWLINE) //
                .append("    value: false") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(flagValueCheck("FIRE", false));
        assertTrue(flagValueCheck("EXPLOSION", false));
    }

    @Test
    public void loadDataYmlFlagDoubleTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - flags: ECO_BLOCK_PRICE") //
                .append(Config.NEWLINE) //
                .append("    value: 1.00") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(flagValueCheck("ECO_BLOCK_PRICE", 1d));
    }

    @Test
    public void loadDataYmlFlagStringListTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - flags: EXCLUDE_COMMANDS") //
                .append(Config.NEWLINE) //
                .append("    value: [a,b,c,d]") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(flagValueStringArrayCheck("EXCLUDE_COMMANDS", new String[] { "a", "b", "c", "d" }));
    }

    @Test
    public void loadDataYmlFlagStringTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - flags: MESSAGE_ENTER") //
                .append(Config.NEWLINE) //
                .append("    value: Bonjour!") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(flagValueCheck("MESSAGE_ENTER", "Bonjour!"));
    }

    @Test
    public void loadDataYmlWrongValueFormatTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - flags: FULL_PVP") //
                .append(Config.NEWLINE) //
                .append("    value: Bonjour!") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(logHandler.isMessageContainsAny("invalid value"));
    }

    @Test
    public void loadDataNonExistFlagTest() {
        final String yml = new StringBuilder() //
                .append("flags:") //
                .append(Config.NEWLINE) //
                .append("  - flags: NON_EXIST") //
                .append(Config.NEWLINE) //
                .append("    value: true") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(flagValueCheck("NON_EXIST", true));
    }

    @Test
    public void loadDataYmlPermNoPcTest() {
        final String yml = new StringBuilder() //
                .append("permissions:") //
                .append(Config.NEWLINE) //
                .append("  - permissions: [BUILD, OPEN]") //
                .append(Config.NEWLINE) //
                .append("    value: true") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(logHandler.isMessageContainsAny("a permission must have at least a player container"));
    }

    @Test
    public void loadDataYmlPermTest() {
        final String yml = new StringBuilder() //
                .append("permissions:") //
                .append(Config.NEWLINE) //
                .append("  - playercontainers: [G:new, everybody]") //
                .append(Config.NEWLINE) //
                .append("    permissions: [BUILD, OPEN]") //
                .append(Config.NEWLINE) //
                .append("    value: false") //
                .append(Config.NEWLINE) //
                .toString();
        final InputStream inputStream = new ByteArrayInputStream(yml.getBytes());
        worldConfig.loadDataYml(inputStream, FileType.LAND_DEFAULT);

        assertTrue(permissionValueCheck(new PlayerContainerGroup(secuboid, "new"), "BUILD", false));
        assertTrue(permissionValueCheck(PlayerContainerEverybody.getInstance(), "BUILD", false));
        assertTrue(permissionValueCheck(new PlayerContainerGroup(secuboid, "new"), "OPEN", false));
        assertTrue(permissionValueCheck(PlayerContainerEverybody.getInstance(), "OPEN", false));
    }

    @After
    public void end() {
        logger.removeHandler(logHandler);
    }

    private boolean permissionValueCheck(final PlayerContainer pc, final String permName, final boolean value) {
        return worldConfig.getDefaultPermissionsFlags().getPermissionsForPC(pc).stream()
                .anyMatch(p -> p.getPermType().getName().equalsIgnoreCase(permName) && p.getValue() == value);
    }

    private boolean flagValueCheck(final String flagName, final Object value) {
        return worldConfig.getDefaultPermissionsFlags().getFlags().stream().anyMatch(
                f -> f.getFlagType().getName().equalsIgnoreCase(flagName) && f.getValue().getValue().equals(value));
    }

    private boolean flagValueStringArrayCheck(final String flagName, final String[] stringArray) {
        return worldConfig.getDefaultPermissionsFlags().getFlags().stream()
                .anyMatch(f -> f.getFlagType().getName().equalsIgnoreCase(flagName)
                        && Arrays.equals((String[]) f.getValue().getValue(), stringArray));
    }
}