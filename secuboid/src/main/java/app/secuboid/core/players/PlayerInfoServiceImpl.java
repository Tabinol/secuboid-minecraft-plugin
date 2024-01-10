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
import app.secuboid.api.lands.LandService;
import app.secuboid.api.lands.LocationPath;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.lands.areas.AreaService;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.players.ConsoleCommandSenderInfo;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.players.PlayerInfoService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerInfoServiceImpl implements PlayerInfoService {

    private final Server server;
    private final AreaService areaService;
    private final LandService landService;

    private final Map<CommandSender, CommandSenderInfo> senderToInfo = new HashMap<>();

    public void addPlayer(Player player) {
        Location lastLocation = player.getLocation();
        Area area = areaService.getArea(lastLocation);
        Land land = landService.get(lastLocation);
        LocationPath locationPath = landService.getLocationPath(lastLocation);
        PlayerInfo playerInfo = new PlayerInfoImpl(player, lastLocation, area, land, locationPath);
        senderToInfo.put(player, playerInfo);
    }

    public void removePlayer(Player player) {
        PlayerInfo playerInfo = (PlayerInfo) senderToInfo.get(player);

        // First, remove AutoCancelSelect
        // TODO reactivate
        // ((PlayerInfoImpl) playerInfo).setAutoCancelSelect(false);

        senderToInfo.remove(player);
    }

    public void updatePlayerPosition(PlayerInfo playerInfo, Location toLocation) {

        // TODO land change Events
        Area area = areaService.getArea(toLocation);
        Land land = landService.get(toLocation);
        LocationPath locationPath = landService.getLocationPath(toLocation);
        ((PlayerInfoImpl) playerInfo).updatePlayerPosition(toLocation, area, land, locationPath);
    }

    @Override
    public void onEnable(boolean isServerBoot) {
        if (isServerBoot) {
            ConsoleCommandSender consoleCommandSender = server.getConsoleSender();
            ConsoleCommandSenderInfo consoleCommandSenderInfo = new ConsoleCommandSenderInfoImpl(consoleCommandSender);
            senderToInfo.put(consoleCommandSender, consoleCommandSenderInfo);
        }

        server.getOnlinePlayers().forEach(this::addPlayer);
    }

    @Override
    public void onDisable(boolean isServerShutdown) {
        server.getOnlinePlayers().forEach(this::removePlayer);
    }

    @Override
    public CommandSenderInfo get(CommandSender sender) {
        return senderToInfo.get(sender);
    }

    @Override
    public PlayerInfo getPlayerInfo(Player player) {
        return (PlayerInfo) senderToInfo.get(player);
    }

    @Override
    public Collection<CommandSenderInfo> getAll() {
        return senderToInfo.values();
    }
}
