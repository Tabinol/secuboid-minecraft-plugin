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
package me.tabinol.secuboid.config;

import java.util.logging.Level;
import me.tabinol.secuboid.Secuboid;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The Class DependPlugin.
 */
public class DependPlugin {

    /**
     * The world edit.
     */
    private Plugin worldEdit = null;

    /**
     * The essentials.
     */
    private Plugin essentials = null;

    /**
     * The vanish no packet.
     */
    private Plugin vanishNoPacket = null;

    /**
     * The permission.
     */
    private Permission permission = null;

    /**
     * The economy.
     */
    private Economy economy = null;

    /**
     * The chat.
     */
    private Chat chat = null;

    /**
     * Instantiates a new depend plugin.
     */
    public DependPlugin() {

	worldEdit = getPlugin("WorldEdit");
	essentials = getPlugin("Essentials");
	vanishNoPacket = getPlugin("VanishNoPacket");
	setupPermissions();
	setupChat();
	setupEconomy();
    }

    /**
     * Gets the plugin.
     *
     * @param pluginName the plugin name
     * @return the plugin
     */
    private Plugin getPlugin(String pluginName) {

	Plugin plugin = Secuboid.getThisPlugin().getServer().getPluginManager().getPlugin(pluginName);

	if (plugin != null) {
	    Secuboid.getThisPlugin().getServer().getPluginManager().enablePlugin(plugin);
	    Secuboid.getThisPlugin().getLog().write(pluginName + " detected!");
	    Secuboid.getThisPlugin().getLogger().log(Level.INFO, "{0} detected!", pluginName);
	} else {
	    Secuboid.getThisPlugin().getLog().write(pluginName + " NOT detected!");
	    Secuboid.getThisPlugin().getLogger().log(Level.INFO, "{0} IS NOT Detected!", pluginName);
	}

	return plugin;
    }

    /**
     * Gets the world edit.
     *
     * @return the world edit
     */
    public Plugin getWorldEdit() {

	return worldEdit;
    }

    /**
     * Gets the essentials.
     *
     * @return the essentials
     */
    public Plugin getEssentials() {

	return essentials;
    }

    /**
     * Gets the vanish no packet.
     *
     * @return the vanish no packet
     */
    public Plugin getVanishNoPacket() {

	return vanishNoPacket;
    }

    /**
     * Setup permissions.
     *
     * @return true, if successful
     */
    private boolean setupPermissions() {
	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	if (permissionProvider != null) {
	    permission = permissionProvider.getProvider();
	}
	return (permission != null);
    }

    /**
     * Setup chat.
     *
     * @return true, if successful
     */
    private boolean setupChat() {
	RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
	if (chatProvider != null) {
	    chat = chatProvider.getProvider();
	}

	return (chat != null);
    }

    /**
     * Setup economy.
     *
     * @return true, if successful
     */
    private boolean setupEconomy() {
	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	if (economyProvider != null) {
	    economy = economyProvider.getProvider();
	}

	return (economy != null);
    }

    /**
     * Gets the permission.
     *
     * @return the permission
     */
    public Permission getPermission() {

	return permission;
    }

    /**
     * Gets the economy.
     *
     * @return the economy
     */
    public Economy getEconomy() {

	return economy;
    }

    /**
     * Gets the chat.
     *
     * @return the chat
     */
    public Chat getChat() {

	return chat;
    }
}
