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
package app.secuboid.core.messages;

import app.secuboid.api.messages.MessageColor;
import app.secuboid.api.messages.MessagePath;
import app.secuboid.api.messages.MessageType;
import app.secuboid.core.config.ConfigService;
import app.secuboid.core.flags.FlagDeclarations;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageManagerServiceTest {

    private MessageManagerServiceImpl messageManagerService;

    @BeforeEach
    void beforeEach() {
        Plugin plugin = mock(Plugin.class);
        ConfigService configService = mock(ConfigService.class);
        when(configService.getLang()).thenReturn("en");
        messageManagerService = new MessageManagerServiceImpl(plugin, configService);
        messageManagerService.onEnable(true);
    }

    @Test
    void when_send_two_parameters_return_it() {
        MessagePath messagePath = MessagePaths.generalTest("p1", "p2");
        String message = messageManagerService.get(MessageType.NORMAL, messagePath);

        String expected = String.format("%sThis is a test message [%sp1%s,%sp2%s]", MessageColor.NORMAL,
                MessageColor.NAME, MessageColor.NORMAL, MessageColor.NAME, MessageColor.NORMAL);
        assertEquals(expected, message);
    }

    @Test
    void when_send_number_format_it() {
        MessagePath messagePath = MessagePaths.generalTest(1.23, "p2");
        String message = messageManagerService.get(MessageType.NORMAL, messagePath);

        String expected = String.format("%sThis is a test message [%s1%s.%s23%s,%sp2%s]", MessageColor.NORMAL,
                MessageColor.NUMBER, MessageColor.NORMAL, MessageColor.NUMBER, MessageColor.NORMAL, MessageColor.NAME,
                MessageColor.NORMAL);
        assertEquals(expected, message);
    }

    @Test
    void when_no_color_do_not_format() {
        MessagePath messagePath = MessagePaths.generalTest(1.23, "p2");
        String message = messageManagerService.get(MessageType.NO_COLOR, messagePath);

        String expected = "This is a test message [1.23,p2]";
        assertEquals(expected, message);
    }

    @Test
    void when_send_error_prefix_it() {
        MessagePath messagePath = MessagePaths.generalTest(1.23, "p2");
        String message = messageManagerService.get(MessageType.ERROR, messagePath);

        String expected = String.format("%s%sThis is a test message [%s1%s.%s23%s,%sp2%s]", MessageType.ERROR.prefix,
                MessageColor.ERROR, MessageColor.NUMBER, MessageColor.ERROR, MessageColor.NUMBER, MessageColor.ERROR,
                MessageColor.NAME, MessageColor.ERROR);
        assertEquals(expected, message);
    }

    @Test
    void when_get_flag_description_return_it() {
        String message = messageManagerService.getFlagDescription(FlagDeclarations.FLAG_BUILD);

        assertEquals("Build and destroy", message);
    }
}
