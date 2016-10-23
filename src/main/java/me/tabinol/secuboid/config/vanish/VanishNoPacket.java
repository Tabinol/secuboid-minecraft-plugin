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
package me.tabinol.secuboid.config.vanish;

import me.tabinol.secuboid.BKVersion;
import me.tabinol.secuboid.Secuboid;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

// import org.kitteh.vanish.VanishPlugin;

/**
 * VanishNoPacket Function.
 *
 * @author michel
 */
public class VanishNoPacket implements Vanish {

    // private final VanishPlugin vanishNoPacket;

    /**
     * Instantiates a new vanish no packet.
     */
    public VanishNoPacket() {

        // vanishNoPacket = (VanishPlugin) Secuboid.getThisPlugin().getDependPlugin().getVanishNoPacket();
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.config.vanish.Vanish#isVanished(org.bukkit.entity.Player)
     */
    @Override
    public boolean isVanished(Player player) {

        if((Secuboid.getThisPlugin().getConf().isSpectatorIsVanish()
                && BKVersion.isSpectatorMode(player))) {
            return true;
        }
        
        // return vanishNoPacket.getManager().isVanished(player);
        for(MetadataValue value : player.getMetadata("vanished")) {
            return value.asBoolean();
        }
        
        return false;
    }
}
