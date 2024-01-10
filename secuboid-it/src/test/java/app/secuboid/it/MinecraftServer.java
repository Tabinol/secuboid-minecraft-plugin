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


package app.secuboid.it;

import app.secuboid.api.SecuboidComponent;
import app.secuboid.api.exceptions.SecuboidRuntimeException;
import app.secuboid.core.SecuboidImpl;
import app.secuboid.core.SecuboidPluginImpl;
import app.secuboid.core.messages.Log;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static app.secuboid.it.DatabaseContainer.mariaDBContainer;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MinecraftServer {

    private static final String PLUGIN_VERSION = "IT_VERSION";

    private final File pluginTempDir;

    private final List<SecuboidComponent> secuboidComponents;
    private final List<JavaPlugin> plugins;

    public MinecraftServer(File pluginTempDir) {
        this.pluginTempDir = pluginTempDir;
        secuboidComponents = new ArrayList<>();
        plugins = new ArrayList<>();

        SecuboidPluginImpl secuboidPlugin = mockSecuboidPluginImpl();
        SecuboidImpl secuboid = new SecuboidImpl(secuboidPlugin);

        secuboidComponents.add(secuboid);
        plugins.add(secuboidPlugin);
    }

    public void load() {
        for (SecuboidComponent secuboidComponent : secuboidComponents) {
            secuboidComponent.onLoad();
        }
    }

    public void enable() {
        for (SecuboidComponent secuboidComponent : secuboidComponents) {
            secuboidComponent.onEnable(true);
        }
    }

    public void disable() {
        List<SecuboidComponent> secuboidComponentsCopy = secuboidComponents.subList(0, secuboidComponents.size());
        Collections.reverse(secuboidComponentsCopy);
        for (SecuboidComponent secuboidComponent : secuboidComponentsCopy) {
            secuboidComponent.onDisable();
        }
    }

    @SuppressWarnings("unchecked")
    public <C> C getSecuboidComponent(Class<C> secuboidComponentImplClasses) {
        for (SecuboidComponent secuboidComponent : secuboidComponents) {
            if (secuboidComponentImplClasses.isAssignableFrom(secuboidComponent.getClass())) {
                return (C) secuboidComponent;
            }
        }

        throw new SecuboidRuntimeException("Invalid class");
    }

    private SecuboidPluginImpl mockSecuboidPluginImpl() {
        String pluginName = "Secuboid";
        String mainClass = "app.secuboid.core.SecuboidPluginImpl";

        return pluginCommonMock(SecuboidPluginImpl.class, pluginName, mainClass);
    }

    public <P extends JavaPlugin> P pluginCommonMock(Class<P> clazz, String pluginName, String mainClass) {
        P javaPlugin = mock(clazz);

        when(javaPlugin.getLogger()).thenReturn(Log.log());

        YamlConfiguration yamlConfiguration = getYamlConfiguration();
        when(javaPlugin.getConfig()).thenReturn(yamlConfiguration);

        when(javaPlugin.getDescription()).thenReturn(new PluginDescriptionFile(pluginName, PLUGIN_VERSION, mainClass));
        when(javaPlugin.getDataFolder()).thenReturn(new File(pluginTempDir, pluginName));
        when(javaPlugin.getName()).thenReturn(pluginName);

        Server server = mockServer();
        when(javaPlugin.getServer()).thenReturn(server);

        PluginCommand pluginCommand = mock(PluginCommand.class);
        when(javaPlugin.getCommand(anyString())).thenReturn(pluginCommand);

        return javaPlugin;
    }

    private YamlConfiguration getYamlConfiguration() {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        ConfigurationSection databaseSection = yamlConfiguration.createSection("database");
        databaseSection.set("host", mariaDBContainer.getHost());
        databaseSection.set("port", mariaDBContainer.getFirstMappedPort());
        databaseSection.set("database", mariaDBContainer.getDatabaseName());
        databaseSection.set("user", mariaDBContainer.getUsername());
        databaseSection.set("password", mariaDBContainer.getPassword());

        return yamlConfiguration;
    }

    private Server mockServer() {
        Server server = mock(Server.class);
        PluginManager pluginManager = mockPluginManager();

        when(server.getPluginManager()).thenReturn(pluginManager);

        when(server.getConsoleSender()).thenReturn(mock(ConsoleCommandSender.class));
        when(server.getServicesManager()).thenReturn(mock(ServicesManager.class));
        when(server.getScheduler()).thenReturn(mock(BukkitScheduler.class));
        when(server.getScoreboardManager()).thenReturn(mock(ScoreboardManager.class));

        return server;
    }

    private PluginManager mockPluginManager() {
        PluginManager pluginManager = mock(PluginManager.class);

        when(pluginManager.getPlugins()).thenAnswer(a -> plugins.toArray(new JavaPlugin[0]));

        return pluginManager;
    }
}
