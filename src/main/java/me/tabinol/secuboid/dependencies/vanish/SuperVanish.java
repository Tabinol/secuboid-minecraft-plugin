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
package me.tabinol.secuboid.dependencies.vanish;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;

/**
 * SuperVanish/PremiumVanish dependency. Created by mblanchet on 2017-03-20.
 */
public class SuperVanish implements Vanish {

    private static final String VANISH_API_CLASS_NAME = "de.myzelyam.api.vanish.VanishAPI";
    private static final String IS_INVISIBLE_STATIC_METHOD = "isInvisible";

    private final Secuboid secuboid;
    private final Class<?> vanishAPIClass;

    /**
     * Instantiates a new super vanish.
     *
     * @param secuboid secuboid instance
     */
    public SuperVanish(final Secuboid secuboid) {
        this.secuboid = secuboid;

        Class<?> vanishAPIClassTmp;
        try {
            vanishAPIClassTmp = Class.forName(VANISH_API_CLASS_NAME);
        } catch (final ClassNotFoundException e) {
            secuboid.getLogger().log(Level.SEVERE,
                    "Class not found: Is it an incompatible SuperVanish version? [" + VANISH_API_CLASS_NAME + "]", e);
            vanishAPIClassTmp = null;
        }
        vanishAPIClass = vanishAPIClassTmp;
    }

    @Override
    public boolean isVanished(final Player player) {

        if ((secuboid.getConf().isSpectatorIsVanish() && player.getGameMode() == GameMode.SPECTATOR)) {
            return true;
        }

        // Someting is wrong!
        if (vanishAPIClass == null) {
            return false;
        }

        try {
            return ((Boolean) vanishAPIClass.getDeclaredMethod(IS_INVISIBLE_STATIC_METHOD, Player.class).invoke(null,
                    player)).booleanValue();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            secuboid.getLogger().log(Level.SEVERE,
                    "Method not found: Is it an incompatible SuperVanish version? [" + IS_INVISIBLE_STATIC_METHOD + "]",
                    e);
            return false;
        }
    }
}
