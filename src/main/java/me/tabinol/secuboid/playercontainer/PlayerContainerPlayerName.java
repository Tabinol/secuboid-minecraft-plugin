package me.tabinol.secuboid.playercontainer;

import org.bukkit.entity.Player;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * Represents a player from his name. Used only before UUID discover.
 *
 * @author tabinol
 */
public final class PlayerContainerPlayerName implements PlayerContainer {

    private final String name;

    public PlayerContainerPlayerName(String name) {
        this.name = name;
    }

    @Override
    public boolean hasAccess(Player player, Land pcLandNullable, LandPermissionsFlags testLandPermissionsFlags) {
        return false;
    }

    @Override
    public String getPrint() {
        return "P:" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PlayerContainerType getContainerType() {
        return PlayerContainerType.PLAYERNAME;
    }

    @Override
    public String toFileFormat() {
        return PlayerContainerType.PLAYERNAME + ":" + name;
    }

    @Override
    public int compareTo(PlayerContainer t) {
        int result = PlayerContainerType.PLAYERNAME.compareTo(t.getContainerType());
        if (result != 0) {
            return result;
        }
        return name.compareTo(t.getName());
    }
}
