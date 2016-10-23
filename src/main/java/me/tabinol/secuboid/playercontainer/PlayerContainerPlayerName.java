package me.tabinol.secuboid.playercontainer;

import me.tabinol.secuboid.lands.Land;
import org.bukkit.entity.Player;

public class PlayerContainerPlayerName implements PlayerContainer {

    private final String name;

    public PlayerContainerPlayerName(String name) {
	this.name = name;
    }

    @Override
    public boolean hasAccess(Player player) {
	return false;
    }

    @Override
    public boolean hasAccess(Player player, Land land) {
	return hasAccess(player);
    }

    @Override
    public String getPrint() {
	return "P:" + name;
    }

    @Override
    public void setLand(Land land) {
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
    public Land getLand() {
	return null;
    }

    @Override
    public int compareTo(PlayerContainer t) {
	int result = PlayerContainerType.PLAYERNAME.compareTo(t.getContainerType());
	if (result == 0) {
	    return result;
	}
	return name.compareTo(t.getName());
    }
}
