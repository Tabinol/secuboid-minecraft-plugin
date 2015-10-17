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
import me.tabinol.secuboidapi.factions.IFaction;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.playercontainer.EPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainerFaction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerFaction.
 */
public class PlayerContainerFaction extends PlayerContainer implements IPlayerContainerFaction {

    /** The faction. */
    private IFaction faction;

    /**
     * Instantiates a new player container faction.
     *
     * @param faction the faction
     */
    public PlayerContainerFaction(IFaction faction) {

        super(faction.getName(), EPlayerContainerType.FACTION, true);
        this.faction = faction;
    }

    /**
     * Gets the faction.
     *
     * @return the faction
     */
    public IFaction getFaction() {

        return faction;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#equals(me.tabinol.secuboid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(IPlayerContainer container2) {

        return container2 instanceof PlayerContainerFaction
                && name.equalsIgnoreCase(container2.getName());
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {

        return new PlayerContainerFaction(faction);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {

        return faction.isPlayerInList(Secuboid.getThisPlugin().iPlayerConf().get(player).getPlayerContainer());
    }

    @Override
    public boolean hasAccess(Player player, ILand land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        return ChatColor.GOLD + "F:" + ChatColor.WHITE + name;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void setLand(ILand land) {

    }
}
