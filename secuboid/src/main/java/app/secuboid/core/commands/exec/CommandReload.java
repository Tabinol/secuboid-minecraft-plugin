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

import app.secuboid.api.Secuboid;
import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.messages.MessageManagerService;
import app.secuboid.api.messages.MessageType;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.core.SecuboidImpl;
import app.secuboid.core.messages.MessagePaths;

@CommandRegistered(
        name = "reload",
        isAdminModeByPass = false,
        permissions = "secuboid.core.reload"
)
public class CommandReload implements CommandExec {

    private final Secuboid secuboid;
    private final MessageManagerService messageManagerService;

    public CommandReload(Secuboid secuboid, MessageManagerService messageManagerService) {
        this.secuboid = secuboid;
        this.messageManagerService = messageManagerService;
    }

    @Override
    public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
        messageManagerService.broadcastMessage(MessageType.NORMAL, MessagePaths.generalPreReload());
        ((SecuboidImpl) secuboid).reload();
        messageManagerService.broadcastMessage(MessageType.NORMAL, MessagePaths.generalReload());
    }
}
