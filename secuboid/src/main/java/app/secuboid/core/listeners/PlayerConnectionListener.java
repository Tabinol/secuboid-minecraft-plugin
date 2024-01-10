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

import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.players.PlayerInfoService;
import app.secuboid.core.messages.ChatGetterService;
import app.secuboid.core.players.PlayerInfoServiceImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.event.EventPriority.MONITOR;

public class PlayerConnectionListener implements Listener {

    private final ChatGetterService chatGetterService;
    private final PlayerInfoService playerInfoService;

    public PlayerConnectionListener(ChatGetterService chatGetterService, PlayerInfoService playerInfoService) {
        this.chatGetterService = chatGetterService;
        this.playerInfoService = playerInfoService;
    }

    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    public void onPlayerJoinMonitor(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ((PlayerInfoServiceImpl) playerInfoService).addPlayer(player);
    }

    @EventHandler(priority = MONITOR)
    public void onPlayerQuitMonitor(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo playerInfo = playerInfoService.getPlayerInfo(player);
        chatGetterService.remove(playerInfo);
        ((PlayerInfoServiceImpl) playerInfoService).removePlayer(player);
    }
}
