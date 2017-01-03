/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.tabinol.secuboid.lands;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.types.Type;
import org.bukkit.entity.Player;

/**
 * Represents default value for a all lands or specific type.
 * Created by Tabinol on 17-01-02.
 */
public class DefaultLand implements Land {

    private final LandPermissionsFlags landPermissionsFlags;
    private final Type type;

    public DefaultLand(Secuboid secuboid, Type type) {
        this.type = type;
        landPermissionsFlags = new LandPermissionsFlags(secuboid, this);
    }

    /**
     * Gets the world name.
     *
     * @return the world name
     */
    @Override
    public String getWorldName() {
        return null;
    }

    @Override
    public LandType getLandType() {
        return LandType.DEFAULT;
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
