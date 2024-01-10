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

package app.secuboid.core.listeners;

import app.secuboid.api.lands.LandService;
import app.secuboid.core.lands.LandServiceImpl;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import static org.bukkit.event.EventPriority.MONITOR;

public class WorldListener implements Listener {

    private final LandService landService;

    public WorldListener(LandService landService) {
        this.landService = landService;
    }

    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    public void onWorldLoadMonitor(WorldLoadEvent event) {
        World world = event.getWorld();

        // This is synced because the Minecraft world load is not async (with lags) and we cannot take the risk to have
        // world damages (ex: fire, explosion or player destroy)
        ((LandServiceImpl) landService).loadWorldSync(world);
    }
}
