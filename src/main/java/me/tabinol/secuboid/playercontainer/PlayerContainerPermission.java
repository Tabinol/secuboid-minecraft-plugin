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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


/**
 * The Class PlayerContainerPermission.
 */
public class PlayerContainerPermission extends PlayerContainer {

    /** The perm. */
    private Permission perm;
    
    /**
     * Instantiates a new player container permission.
     *
     * @param bukkitPermission the bukkit permission
     */
    public PlayerContainerPermission(String bukkitPermission) {

        super(bukkitPermission, PlayerContainerType.PERMISSION, true);
        if(Secuboid.getThisPlugin() != null){
            perm = new Permission(bukkitPermission);
        } else {
            perm = null;
        }
    }

    public boolean equals(PlayerContainer container2) {

        return container2 instanceof PlayerContainerPermission
                && name.equalsIgnoreCase(container2.getName());
    }

    public PlayerContainer copyOf() {

        return new PlayerContainerPermission(name);
    }

    public boolean hasAccess(Player player) {

        return player.hasPermission(perm);
    }

    public boolean hasAccess(Player player, Land land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        return ChatColor.GRAY + "B:" + ChatColor.WHITE + name;
    }

    public void setLand(Land land) {

    }
}
