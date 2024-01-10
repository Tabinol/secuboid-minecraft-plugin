/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.core.players;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.LocationPath;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.selection.PlayerSelection;
import app.secuboid.api.selection.SenderSelection;
import app.secuboid.core.selection.PlayerSelectionImpl;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class PlayerInfoImpl extends CommandSenderInfoImpl implements PlayerInfo {

    private final Player player;
    private final PlayerSelection playerSelection;

    @Setter
    private boolean adminMode;

    @Setter
    private long lastUpdateTimeMillis;
    private Location lastLocation;
    private Area area;
    private Land land;
    private LocationPath locationPath;
    @Setter
    private boolean tpCancel;

    PlayerInfoImpl(Player player, Location lastLocation, Area area, Land land, LocationPath locationPath) {
        super(player);
        this.player = player;
        playerSelection = new PlayerSelectionImpl(this);

        adminMode = false;
        lastUpdateTimeMillis = 0L;
        this.lastLocation = lastLocation;
        this.area = area;
        this.land = land;
        this.locationPath = locationPath;
        tpCancel = false;
    }

    public void updatePlayerPosition(Location lastLocation, Area area, Land land, LocationPath locationPath) {
        this.lastLocation = lastLocation;
        this.area = area;
        this.land = land;
        this.locationPath = locationPath;

        ((PlayerSelectionImpl) playerSelection).updateSelectionFromLocation();
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public boolean isAdminMode() {

        // Security for adminmode
        if (adminMode && !player.hasPermission("secuboid.adminmode")) {
            adminMode = false;
            return false;
        }

        return adminMode;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public Land getLand() {
        return land;
    }

    @Override
    public LocationPath getLocationPath() {
        return locationPath;
    }

    @Override
    public Land getWorldLand() {
        return land.getWorldLand();
    }

    @Override
    public SenderSelection getSelection() {
        return playerSelection;
    }
}
