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
package me.tabinol.secuboid.lands;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.config.WorldConfig;
import me.tabinol.secuboid.lands.approve.Approves;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;

/**
 * Init lands database and methods.
 */
public final class InitLands {

    public static final String WORLD = "world";

    private final Secuboid secuboid;
    private final Lands lands;

    public InitLands() {
        // Prepare Mock
        secuboid = mock(Secuboid.class);

        // conf
        final Config config = mock(Config.class);
        when(config.useEconomy()).thenReturn(false);
        when(secuboid.getConf()).thenReturn(config);

        // log
        final Logger log = Logger.getLogger("Secuboid");
        when(secuboid.getLogger()).thenReturn(log);

        // Permissions Flags
        final PermissionsFlags permissionsFlags = new PermissionsFlags(secuboid);
        when(secuboid.getPermissionsFlags()).thenReturn(permissionsFlags);

        // Types
        final Types types = new Types();
        when(secuboid.getTypes()).thenReturn(types);

        // Server
        final PluginManager pm = mock(PluginManager.class);
        final Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(pm);
        when(secuboid.getServer()).thenReturn(server);

        // WorldConfig

        // WorldConfig
        final WorldConfig worldConfig = mock(WorldConfig.class);
        final Map<String, LandPermissionsFlags> worldNameToPermissionsFlags = new HashMap<>();
        final LandPermissionsFlags globalPermissionsFlags = new LandPermissionsFlags(secuboid, null);
        final Map<Type, LandPermissionsFlags> typeToDefaultPermissionsFlags = new HashMap<>();
        final LandPermissionsFlags defaultPermissionsFlags = new LandPermissionsFlags(secuboid);
        when(worldConfig.getWorldNameToPermissionsFlags()).thenReturn(worldNameToPermissionsFlags);
        when(worldConfig.getGlobalPermissionsFlags()).thenReturn(globalPermissionsFlags);
        when(worldConfig.getTypeToDefaultPermissionsFlags()).thenReturn(typeToDefaultPermissionsFlags);
        when(worldConfig.getDefaultPermissionsFlags()).thenReturn(defaultPermissionsFlags);

        // ApproveList
        final Approves approves = mock(Approves.class);

        // Lands
        lands = new Lands(secuboid, worldConfig, approves);
        when(secuboid.getLands()).thenReturn(lands);

        // Storage
        final StorageThread storageThread = mock(StorageThread.class);
        doNothing().when(storageThread).addSaveAction(any(SaveActionEnum.class), any());
        when(secuboid.getStorageThread()).thenReturn(storageThread);
    }

    public Secuboid getSecuboid() {
        return secuboid;
    }

    public Lands getLands() {
        return lands;
    }
}
