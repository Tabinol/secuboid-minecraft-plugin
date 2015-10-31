package me.tabinol.secuboid.playercontainer;

import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;

import org.bukkit.ChatColor;
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

        super(name, ApiPlayerContainerType.PLAYERNAME, false);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#equals(me.tabinol.secuboid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(ApiPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayerName &&
                name.equals(((PlayerContainerPlayer) container2).name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayerName(name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
         return false;
    }

    @Override
    public boolean hasAccess(Player player, ApiLand land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        return "P:" + name;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.secuboid.lands.Land)
     */
    @Override
    public void setLand(ApiLand land) {

    }
}
