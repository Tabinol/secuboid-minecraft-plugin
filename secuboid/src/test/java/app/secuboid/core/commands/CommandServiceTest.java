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

import app.secuboid.api.Secuboid;
import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.players.ChatPageService;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.core.registration.RegistrationServiceImpl;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.mockito.Mockito.*;

class CommandServiceTest {

    private CommandServiceImpl commandService;
    private CommandSender sender;
    private CommandSenderInfo commandSenderInfo;

    @BeforeEach
    void beforeEach() {
        Secuboid secuboid = mock(Secuboid.class);
        ChatPageService chatPageService = mock(ChatPageService.class);
        RegistrationServiceImpl registrationService = mock(RegistrationServiceImpl.class);

        Map<CommandExec, CommandRegistered> commandExecToAnnotation =
                Map.ofEntries(
                        entry(new CommandTest(secuboid), CommandTest.class.getAnnotation(CommandRegistered.class)),
                        entry(new CommandTestSub(secuboid), CommandTestSub.class.getAnnotation(CommandRegistered.class))
                );
        when(registrationService.getCommandExecToCommandRegistered()).thenReturn(commandExecToAnnotation);

        commandService = spy(new CommandServiceImpl(chatPageService, registrationService));
        commandService.onEnable(true);

        sender = mock(CommandSender.class);
        commandSenderInfo = mock(CommandSenderInfo.class);
        when(commandSenderInfo.sender()).thenReturn(sender);
    }

    @Test
    void when_call_test_command_name_then_execute_it() {
        commandService.executeCommandName(commandSenderInfo, new String[]{"test"});

        verify(sender, times(1)).sendMessage("done!");
    }

    @Test
    void when_call_test_command_with_parameter_then_execute_it() {
        commandService.executeCommandName(commandSenderInfo, new String[]{"test", "parameter"});

        verify(sender, times(1)).sendMessage("done!");
    }

    @Test
    void when_call_test_sub_command_name_then_execute_it() {
        commandService.executeCommandName(commandSenderInfo, new String[]{"test", "sub"});

        verify(sender, times(1)).sendMessage("done sub!");
    }

    @Test
    void when_call_test_command_class_then_execute_it() {
        commandService.executeCommandClass(CommandTest.class, commandSenderInfo, new String[]{});

        verify(sender, times(1)).sendMessage("done!");
    }

    @CommandRegistered(
            name = "test"
    )
    private static class CommandTest implements CommandExec {

        @SuppressWarnings("unused")
        CommandTest(Secuboid secuboid) {
        }

        @Override
        public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
            CommandSender sender = commandSenderInfo.sender();
            sender.sendMessage("done!");
        }
    }

    @CommandRegistered(
            name = "test sub"
    )
    private static class CommandTestSub implements CommandExec {

        @SuppressWarnings("unused")
        CommandTestSub(Secuboid secuboid) {
        }

        @Override
        public void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs) {
            CommandSender sender = commandSenderInfo.sender();
            sender.sendMessage("done sub!");
        }
    }
}
