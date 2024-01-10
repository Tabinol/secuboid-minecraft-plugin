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
package app.secuboid.core.commands;

import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.commands.CommandService;
import app.secuboid.api.players.ChatPageService;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.core.commands.exec.CommandPage;
import app.secuboid.core.messages.Log;
import app.secuboid.core.registration.RegistrationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.logging.Level.SEVERE;

@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {

    private static final int COMMANDS_SEPARATOR = ' ';

    private final ChatPageService chatPageService;
    private final RegistrationService registrationService;

    private final Map<String, CommandContainer> nameToCommandContainer = new HashMap<>();
    private final Map<Class<? extends CommandExec>, CommandContainer> classToCommandContainer = new HashMap<>();

    @Override
    public void onEnable(boolean isServerBoot) {
        if (!isServerBoot) {
            return;
        }

        Map<CommandExec, CommandRegistered> commandExecToCommandRegistered =
                ((RegistrationServiceImpl) registrationService).getCommandExecToCommandRegistered();

        AtomicInteger commandLevelAt = new AtomicInteger(0);
        AtomicBoolean foundAt = new AtomicBoolean();
        do {
            foundAt.set(false);

            commandExecToCommandRegistered.forEach((commandExec, commandRegistered) -> {
                long curLevel = commandRegistered.name().chars().filter(c -> c == COMMANDS_SEPARATOR).count();
                if (curLevel == commandLevelAt.get()) {
                    foundAt.set(true);
                    registerCommand(commandExec, commandRegistered);
                }
            });
            commandLevelAt.incrementAndGet();
        }
        while (foundAt.get());
    }

    @Override
    public void executeCommandClass(Class<? extends CommandExec> clazz,
                                    CommandSenderInfo commandSenderInfo, String[] subArgs) {

        CommandContainer commandContainer = classToCommandContainer.get(clazz);
        executeCommand(commandContainer, commandSenderInfo, subArgs);
    }

    void executeCommandName(CommandSenderInfo commandSenderInfo, String[] args) {
        CommandSender sender = commandSenderInfo.sender();

        if (args.length == 0) {
            // TODO help with click
            sender.sendMessage("todo");
            return;
        }

        Map<String, CommandContainer> curNameToCommand = nameToCommandContainer;
        int commandPathCounter = 0;
        CommandContainer commandContainer = null;
        CommandContainer curCommandContainer;
        do {
            String arg = args[commandPathCounter];
            String argLower = arg.toLowerCase();
            curCommandContainer = curNameToCommand.get(argLower);

            if (curCommandContainer != null) {
                commandContainer = curCommandContainer;
                curNameToCommand = commandContainer.getNameToSubCommand();
            }

            commandPathCounter++;
        } while (curCommandContainer != null && commandPathCounter < args.length && !curNameToCommand.isEmpty());

        if (commandContainer == null) {
            // TODO help with null
            sender.sendMessage("todo");
            return;
        }

        String[] subArgs = Arrays.copyOfRange(args, commandPathCounter, args.length);
        executeCommand(commandContainer, commandSenderInfo, subArgs);
    }

    private void executeCommand(CommandContainer commandContainer,
                                CommandSenderInfo commandSenderInfo, String[] subArgs) {
        CommandExec commandExec = commandContainer.getCommandExec();

        if (!(commandExec instanceof CommandPage)) {
            chatPageService.remove(commandSenderInfo);
        }

        // TODO check permissions

        // TODO subcommands

        commandExec.commandExec(commandSenderInfo, subArgs);
    }

    private void registerCommand(CommandExec commandExec, CommandRegistered commandRegistered) {
        // TODO Retreve flags

        String commandName = commandRegistered.name();
        String[] nameSplit = commandName.split(Character.toString(COMMANDS_SEPARATOR));
        int nameSplitLengthMinusOne = nameSplit.length - 1;
        Map<String, CommandContainer> curNameToCommand = nameToCommandContainer;

        for (int i = 0; i < nameSplitLengthMinusOne; i++) {
            CommandContainer curCommandContainer = curNameToCommand.get(nameSplit[i]);
            if (curCommandContainer == null) {
                Log.log().log(SEVERE, () -> "There is an error on registered command parent name: " + commandName);
                return;
            }
            curNameToCommand = curCommandContainer.getNameToSubCommand();
        }

        String subName = nameSplit[nameSplitLengthMinusOne];
        CommandContainer commandContainer = new CommandContainer(commandExec, commandRegistered, Collections.emptySet(),
                new HashMap<>());
        classToCommandContainer.put(commandExec.getClass(), commandContainer);
        curNameToCommand.put(subName, commandContainer);
        for (String alias : commandRegistered.aliases()) {
            curNameToCommand.put(alias, commandContainer);
        }
    }
}
