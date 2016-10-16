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
package me.tabinol.secuboid.playercontainer;

import me.tabinol.secuboid.lands.Land;
import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerEverybody.
 */
public class PlayerContainerEverybody extends PlayerContainer {

    /**
     * Instantiates a new player container everybody.
     */
    public PlayerContainerEverybody() {
        
        super("", PlayerContainerType.EVERYBODY, false);
    }
    
    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerEverybody;
    }

    public PlayerContainer copyOf() {
        
        return new PlayerContainerEverybody();
    }

    public boolean hasAccess(Player player) {
        
        return true;
    }
    
    public boolean hasAccess(Player player, Land land) {
        
        return true;
    }

    public void setLand(Land land) {
        
    }
}
