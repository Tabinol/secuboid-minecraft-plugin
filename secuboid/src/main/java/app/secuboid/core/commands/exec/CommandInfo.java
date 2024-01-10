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
package app.secuboid.core.commands.exec;

import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.lands.LocationPath;
import app.secuboid.api.messages.MessageManagerService;
import app.secuboid.api.messages.MessageType;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.core.messages.MessagePaths;
import org.bukkit.command.CommandSender;

@CommandRegistered(
        name = "info"
)
public class CommandInfo implements CommandExec {

    private final MessageManagerService messageManagerService;

    public CommandInfo(MessageManagerService messageManagerService) {
        this.messageManagerService = messageManagerService;
    }

    @Override
    public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
        CommandSender sender = commandSenderInfo.sender();

        LocationPath locationPath;
        if (subArgs.length == 0) {
            if (commandSenderInfo instanceof PlayerInfo playerInfo) {
                locationPath = playerInfo.getLocationPath();
            } else {
                messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.generalNeedParameter());
                return;
            }
        } else {
            // TODO with parameter
            return;
        }

        messageManagerService.sendMessage(sender, MessageType.NORMAL, MessagePaths.infoLocationPath(locationPath));
    }
}
