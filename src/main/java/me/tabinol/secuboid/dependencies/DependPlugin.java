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
package me.tabinol.secuboid.dependencies;

import com.earth2me.essentials.Essentials;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.dependencies.chat.ChatEssentials;
import me.tabinol.secuboid.dependencies.chat.ChatSecuboid;
import me.tabinol.secuboid.dependencies.vanish.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import static org.bukkit.Bukkit.getServer;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * The Class for plugin dependencies.
 */
public class DependPlugin {

    private final Secuboid secuboid;

    /**
     * The world edit.
     */
    private Plugin worldEdit = null;

    /**
     * The essentials.
     */
    private Plugin essentials = null;

    /**
     * The vanish
     */
    private Vanish vanish;

    /**
     * The chat.
     */
    private me.tabinol.secuboid.dependencies.chat.Chat chat;

    /**
     * The vanish no packet.
     */
    private Plugin vanishNoPacket = null;

    /**
     * The super/premium vanish no packet.
     */
    private Plugin superVanish = null;

    /**
     * The vault permission.
     */
    private Permission vaultPermission = null;

    /**
     * The vault economy.
     */
    private Economy vaultEconomy = null;

    /**
     * The vault chat.
     */
    private Chat vaultChat = null;

    /**
     * Instantiates a new depend plugin.
     *
     * @param secuboid secuboid instance
     */
    public DependPlugin(Secuboid secuboid) {

        this.secuboid = secuboid;

        worldEdit = getPlugin("WorldEdit");
        essentials = getPlugin("Essentials");
        vanishNoPacket = getPlugin("VanishNoPacket");
        superVanish = getPlugin("PremiumVanish");
        if (superVanish == null) {
            superVanish = getPlugin("SuperVanish");
        }
        setupPermissions();
        setupChat();
        setupEconomy();
        setupVanish();
    }

    /**
     * Gets the plugin.
     *
     * @param pluginName the plugin name
     * @return the plugin
     */
    private Plugin getPlugin(String pluginName) {

        Plugin plugin = secuboid.getServer().getPluginManager().getPlugin(pluginName);

        if (plugin != null) {
            secuboid.getServer().getPluginManager().enablePlugin(plugin);
            secuboid.getLogger().info(pluginName + " detected!");
        } else {
            secuboid.getLogger().info(pluginName + " IS NOT Detected!");
        }

        return plugin;
    }

    /**
     * Setup permissions.
     */
    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            vaultPermission = permissionProvider.getProvider();
        }
    }

    /**
     * Setup chat.
     */
    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            vaultChat = chatProvider.getProvider();
        }

        // Get chat for social spy.
        if (essentials != null) {
            chat = new ChatEssentials((Essentials) essentials);
        } else {
            chat = new ChatSecuboid();
        }
    }

    /**
     * Setup vaultEconomy.
     */
    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            vaultEconomy = economyProvider.getProvider();
        }
    }

    /**
     * Setup vanish.
     */
    private void setupVanish() {
        if (superVanish != null) {
            vanish = new SuperVanish(secuboid);

        } else if (vanishNoPacket != null) {
            vanish = new VanishNoPacket(secuboid);

        } else if (essentials != null) {
            vanish = new VanishEssentials(secuboid, (Essentials) essentials);

            // Dummy Vanish if no plugins
        } else {
            vanish = new DummyVanish(secuboid);
        }
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
     * Gets the vault permission.
     *
     * @return the vault permission
     */
    public Permission getVaultPermission() {

        return vaultPermission;
    }

    /**
     * Gets the vault economy.
     *
     * @return the vault economy
     */
    public Economy getVaultEconomy() {

        return vaultEconomy;
    }

    /**
     * Gets the vault chat.
     *
     * @return the vault chat
     */
    public Chat getVaultChat() {

        return vaultChat;
    }

    public Vanish getVanish() {
        return vanish;
    }

    public me.tabinol.secuboid.dependencies.chat.Chat getChat() {
        return chat;
    }
}
