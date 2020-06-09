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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.tabinol.secuboid.Secuboid;

/**
 * Connector to Essentials.
 */
public final class EssentialsCommon {

    private static final String ESSENTIALS_API_CLASS_NAME = "com.earth2me.essentials.Essentials";
    private static final String GET_USER_METHOD = "getUser";

    private static final String USER_CLASS_NAME = "com.earth2me.essentials.User";
    private static final String IS_SOCIAL_SPY_ENABLED_METHOD = "isSocialSpyEnabled";
    private static final String IS_MUTED_METHOD = "isMuted";
    private static final String IS_VANISHED_METHOD = "isVanished";

    private final Secuboid secuboid;
    private final Plugin essentialsPlugin;
    private final Class<?> essentialsAPIClass;
    private final Class<?> userClass;

    public EssentialsCommon(final Secuboid secuboid, final Plugin essentialsPlugin) {
        this.secuboid = secuboid;
        this.essentialsPlugin = essentialsPlugin;

        Class<?> essentialsAPIClassTmp;
        Class<?> userClassTmp;
        try {
            essentialsAPIClassTmp = Class.forName(ESSENTIALS_API_CLASS_NAME);
            userClassTmp = Class.forName(USER_CLASS_NAME);
        } catch (final ClassNotFoundException e) {
            secuboid.getLogger().log(Level.SEVERE,
                    "Class not found: Is it an incompatible Essentials version? [" + ESSENTIALS_API_CLASS_NAME + "]",
                    e);
            essentialsAPIClassTmp = null;
            userClassTmp = null;
        }
        essentialsAPIClass = essentialsAPIClassTmp;
        userClass = userClassTmp;
    }

    public boolean isSocialSpyEnabled(final Player player) {
        try {
            final Object user = getUser(player);
            return ((Boolean) userClass.getDeclaredMethod(IS_SOCIAL_SPY_ENABLED_METHOD).invoke(user)).booleanValue();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | NullPointerException e) {
            secuboid.getLogger().log(Level.SEVERE, "Method not found: Is it an incompatible Essentials version? ["
                    + IS_SOCIAL_SPY_ENABLED_METHOD + "]", e);
            return false;
        }
    }

    public boolean isMuted(final Player player) {
        try {
            final Object user = getUser(player);
            return ((Boolean) userClass.getDeclaredMethod(IS_MUTED_METHOD).invoke(user)).booleanValue();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | NullPointerException e) {
            secuboid.getLogger().log(Level.SEVERE,
                    "Method not found: Is it an incompatible Essentials version? [" + IS_MUTED_METHOD + "]", e);
            return false;
        }
    }

    public boolean isVanished(final Player player) {
        try {
            final Object user = getUser(player);
            return ((Boolean) userClass.getDeclaredMethod(IS_VANISHED_METHOD).invoke(user)).booleanValue();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | NullPointerException e) {
            secuboid.getLogger().log(Level.SEVERE,
                    "Method not found: Is it an incompatible Essentials version? [" + IS_VANISHED_METHOD + "]", e);
            return false;
        }
    }

    private Object getUser(final Player player) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        return essentialsAPIClass.getDeclaredMethod(GET_USER_METHOD, Player.class).invoke(essentialsPlugin, player);
    }

}