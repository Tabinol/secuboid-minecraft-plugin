package me.tabinol.secuboid.lands;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.lands.types.Types;
import me.tabinol.secuboid.permissionsflags.PermissionsFlags;
import me.tabinol.secuboid.storage.StorageThread;
import me.tabinol.secuboid.utilities.Log;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.util.TreeMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Init lands database and methods.
 */
public class InitLands {

    public static final String WORLD = "world";

    private Secuboid secuboid;
    private Lands lands;

    public InitLands() {
        // Prepare Mock
        PowerMockito.mockStatic(Secuboid.class);
        secuboid = mock(Secuboid.class);

        // log
        Log log = mock(Log.class);
        doNothing().when(log).info(anyString());
        doNothing().when(log).warning(anyString());
        doNothing().when(log).severe(anyString());
        when(secuboid.getLog()).thenReturn(log);

        // Permissions Flags
        PermissionsFlags permissionsFlags = new PermissionsFlags(secuboid);
        when(secuboid.getPermissionsFlags()).thenReturn(permissionsFlags);

        // Tyles
        Types types = new Types();
        when(secuboid.getTypes()).thenReturn(types);

        // Server
        PluginManager pm = mock(PluginManager.class);
        Server server = mock(Server.class);
        when(server.getPluginManager()).thenReturn(pm);
        when(secuboid.getServer()).thenReturn(server);

        // Lands
        lands = spy(new Lands(secuboid));
        when(secuboid.getLands()).thenReturn(lands);

        // Outside areas
        TreeMap<String, WorldLand> outsideArea = new TreeMap<String, WorldLand>();
        outsideArea.put(Config.GLOBAL, new WorldLand(secuboid, Config.GLOBAL));
        Whitebox.setInternalState(lands, "outsideArea", outsideArea);

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
}
