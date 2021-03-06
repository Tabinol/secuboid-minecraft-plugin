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

import me.tabinol.secuboid.Secuboid;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Only return false if there is no Vanish plugin.
 */
public class DummyVanish implements Vanish {

    private final Secuboid secuboid;

    public DummyVanish(Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    @Override
    public boolean isVanished(Player player) {
        return secuboid.getConf().isSpectatorIsVanish()
                && player.getGameMode() == GameMode.SPECTATOR;
    }
}
