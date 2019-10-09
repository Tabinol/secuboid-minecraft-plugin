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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.utilities.Log;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Init lands database and methods.
 */
public class InitLands {

    public static final String WORLD = "world";

    private Secuboid secuboid;
    private Lands lands;
    private DefaultLand defaultConfNoType;
    private WorldLand worldLand;

    public InitLands() {
        // Prepare Mock
        // PowerMockito.mockStatic(Secuboid.class);
        secuboid = mock(Secuboid.class);
        doNothing().when(secuboid).saveResource(anyString(), anyBoolean());
        doNothing().when((Secuboid) secuboid).saveResource(anyString(), anyBoolean());

        // log
        Log log = mock(Log.class);
        doNothing().when(log).info(anyString());
        doNothing().when(log).warning(anyString());
        doNothing().when(log).severe(anyString());
        when(secuboid.getLog()).thenReturn(log);

        // Permissions Flags
        PermissionsFlags permissionsFlags = new PermissionsFlags(secuboid);
        when(secuboid.getPermissionsFlags()).thenReturn(permissionsFlags);

        // Types
        Types types = new Types();
        when(secuboid.getTypes()).thenReturn(types);

        // Server
        PluginManager pm = mock(PluginManager.class);
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(pm);
        doReturn(server).when(secuboid).getServer();

        // Lands
        lands = spy(new Lands(secuboid));
        when(secuboid.getLands()).thenReturn(lands);

        // Outside areas
        TreeMap<String, WorldLand> outsideArea = new TreeMap<String, WorldLand>();
        worldLand = new WorldLand(secuboid, Config.GLOBAL);
        outsideArea.put(Config.GLOBAL, worldLand);
        // Whitebox.setInternalState(lands, "outsideArea", outsideArea);

        // defaultConfNoType
        defaultConfNoType = new DefaultLand(secuboid);
        // Whitebox.setInternalState(lands, "defaultConfNoType", defaultConfNoType);

        // Storage
        StorageThread storageThread = mock(StorageThread.class);
        doNothing().when(storageThread).saveLand(any(RealLand.class));
        when(secuboid.getStorageThread()).thenReturn(storageThread);
    }

    public Secuboid getSecuboid() {
        return secuboid;
    }

    public Lands getLands() {
        return lands;
    }

    public DefaultLand getDefaultConfNoType() {
        return defaultConfNoType;
    }

    public WorldLand getWorldLand() {
        return worldLand;
    }
}
