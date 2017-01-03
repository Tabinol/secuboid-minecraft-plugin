/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tabinol.secuboid.lands;

import me.tabinol.secuboid.Secuboid;
import org.bukkit.entity.Player;

/**
 * Represents global, entire world.
 *
 * @author tabinol
 */
public class WorldLand implements Land {

    private final LandPermissionsFlags landPermissionsFlags;
    private final String worldName;

    public WorldLand(Secuboid secuboid, String worldName) {
        this.worldName = worldName;
        landPermissionsFlags = new LandPermissionsFlags(secuboid, this);
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public LandType getLandType() {
        return LandType.WORLD;
    }

    @Override
    public LandPermissionsFlags getPermissionsFlags() {
        return landPermissionsFlags;
    }

    @Override
    public boolean isBanned(Player player) {
        return false;
    }
}
