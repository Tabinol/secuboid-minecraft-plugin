package me.tabinol.secuboid.playercontainer;

import java.util.Objects;

import org.bukkit.entity.Player;

import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * Represents a player from his name. Used only before UUID discover.
 *
 * @author tabinol
 */
public final class PlayerContainerPlayerName implements PlayerContainer {

    private final String name;

    public PlayerContainerPlayerName(final String name) {
        this.name = name;
    }

    @Override
    public boolean hasAccess(final Player player, final LandPermissionsFlags testLandPermissionsFlags) {
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
    public boolean isLandRelative() {
        return false;
    }

    @Override
    public int compareTo(final PlayerContainer t) {
        final int result = PlayerContainerType.PLAYERNAME.compareTo(t.getContainerType());
        if (result != 0) {
            return result;
        }
        return name.compareTo(t.getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PlayerContainerPlayerName)) {
            return false;
        }
        final PlayerContainerPlayerName playerContainerPlayerName = (PlayerContainerPlayerName) o;
        return Objects.equals(name, playerContainerPlayerName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
