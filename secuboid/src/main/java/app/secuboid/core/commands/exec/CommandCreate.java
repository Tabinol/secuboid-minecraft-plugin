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
import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.LandResult;
import app.secuboid.api.lands.LandResultCode;
import app.secuboid.api.lands.LandService;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.messages.MessageManagerService;
import app.secuboid.api.messages.MessageType;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.players.ConsoleCommandSenderInfo;
import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.recipients.RecipientExec;
import app.secuboid.api.recipients.RecipientResult;
import app.secuboid.api.recipients.RecipientResultCode;
import app.secuboid.api.recipients.RecipientService;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.api.selection.SenderSelection;
import app.secuboid.api.selection.active.ActiveSelection;
import app.secuboid.api.selection.active.ActiveSelectionModify;
import app.secuboid.core.messages.ChatGetterService;
import app.secuboid.core.messages.MessagePaths;
import app.secuboid.core.players.CommandSenderInfoImpl;
import app.secuboid.core.selection.active.ActiveSelectionModifyImpl;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static app.secuboid.api.recipients.RecipientService.NOBODY;
import static app.secuboid.api.recipients.RecipientService.PLAYER;

@CommandRegistered(
        name = "create",
        sourceActionFlags = "land-create"
)
@RequiredArgsConstructor
public class CommandCreate implements CommandExec {

    private static final String COMMAND_SELECT = "/sd select";

    private final ChatGetterService chatGetterService;
    private final LandService landService;
    private final MessageManagerService messageManagerService;
    private final RecipientService recipientService;

    @Override
    public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
        SenderSelection selection = ((CommandSenderInfoImpl) commandSenderInfo).getSelection();
        ActiveSelection activeSelection = selection.getActiveSelection();
        CommandSender sender = commandSenderInfo.sender();

        // TODD Remove this check and use annotation check
        if (!(activeSelection instanceof ActiveSelectionModify activeSelectionModify)) {
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.selectionCreateNeedActiveSelection(COMMAND_SELECT));
            return;
        }

        if (subArgs.length == 0) {
            if (commandSenderInfo instanceof ConsoleCommandSenderInfo) {
                messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.generalNeedParameter());
                return;
            }

            messageManagerService.sendMessage(sender, MessageType.NORMAL, MessagePaths.selectionCreateEnterName());
            chatGetterService.put(commandSenderInfo, s -> landNameCallback(commandSenderInfo, activeSelectionModify,
                    s));
            return;
        }

        if (subArgs.length > 1) {
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.selectionCreateNoSpace());
            return;
        }

        landNameCallback(commandSenderInfo, activeSelectionModify, subArgs[0]);
    }

    private void landNameCallback(CommandSenderInfo commandSenderInfo,
                                  ActiveSelectionModify activeSelectionModify, String landName) {
        if (landName.contains(" ")) {
            CommandSender sender = commandSenderInfo.sender();
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.selectionCreateNoSpace());
            return;
        }

        if (!commandSenderInfo.isAdminMode() && commandSenderInfo instanceof PlayerInfo playerInfo) {
            UUID uuid = playerInfo.getUUID();
            recipientService.grab(PLAYER, uuid.toString(), r -> landOwnerCallback(commandSenderInfo,
                    activeSelectionModify, landName, r));
        } else {
            recipientService.grab(NOBODY, null, r -> landOwnerCallback(commandSenderInfo, activeSelectionModify,
                    landName, r));
        }
    }

    private void landOwnerCallback(CommandSenderInfo commandSenderInfo,
                                   ActiveSelectionModify activeSelectionModify, String landName,
                                   RecipientResult result) {
        RecipientExec owner = result.getRecipientExec();
        RecipientResultCode code = result.getCode();

        if (code != RecipientResultCode.SUCCESS || owner == null) {
            CommandSender sender = commandSenderInfo.sender();
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.generalError(code));
            return;
        }

        // TODO get parent
        Land worldLand = activeSelectionModify.getWorldLand();
        Area area = ((ActiveSelectionModifyImpl) activeSelectionModify).getSelectionForm().getArea();
        landService.create(worldLand, landName, owner, area, r -> landCreateCallback(commandSenderInfo, r));
    }

    public void landCreateCallback(CommandSenderInfo commandSenderInfo, LandResult landResult) {
        CommandSender sender = commandSenderInfo.sender();
        LandResultCode code = landResult.getCode();

        if (code != LandResultCode.SUCCESS || landResult.getArea() == null) {
            messageManagerService.sendMessage(sender, MessageType.ERROR, MessagePaths.generalError(code));
            return;
        }

        messageManagerService.sendMessage(sender, MessageType.NORMAL,
                MessagePaths.selectionCreateCreated(landResult.getArea().getId()));
        SenderSelection senderSelection = commandSenderInfo.getSelection();
        senderSelection.removeSelection();
    }
}
