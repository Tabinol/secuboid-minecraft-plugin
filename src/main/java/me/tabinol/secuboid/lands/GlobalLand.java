/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tabinol.secuboid.lands;

import me.tabinol.secuboid.Secuboid;
import org.bukkit.entity.Player;

/**
 * Represents global, entire world or defaults.
 *
 * @author tabinol
 */
public class GlobalLand implements Land {

    private final LandPermissionsFlags landPermissionsFlags;
    private final String worldName;

    public GlobalLand(Secuboid secuboid, String worldName) {
        this.worldName = worldName;
        landPermissionsFlags = new LandPermissionsFlags(secuboid, this);
    }

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean isRealLand() {
        return false;
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
