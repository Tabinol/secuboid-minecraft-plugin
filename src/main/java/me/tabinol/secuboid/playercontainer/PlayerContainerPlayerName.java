package me.tabinol.secuboid.playercontainer;

import me.tabinol.secuboid.lands.Land;
import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerPlayerName.
 */
public class PlayerContainerPlayerName extends PlayerContainer {

    /**
     * Instantiates a new player container player name.
     *
     * @param name the name
     */
    public PlayerContainerPlayerName(String name) {

        super(name, PlayerContainerType.PLAYERNAME, false);
    }

    public boolean equals(PlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayerName &&
                name.equals(((PlayerContainerPlayer) container2).name);
    }

    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayerName(name);
    }

    public boolean hasAccess(Player player) {
        
         return false;
    }

    public boolean hasAccess(Player player, Land land) 
    {
        
        return hasAccess(player);
    }

    public String getPrint() {

        return "P:" + name;
    }

    public void setLand(Land land) {

    }
}
