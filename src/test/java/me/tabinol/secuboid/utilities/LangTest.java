/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.secuboid.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mock;

import me.tabinol.secuboid.Secuboid;

public class LangTest {

    @Mock
    Secuboid secuboid;

    @Test
    public void testLegacyMessage() {
        Lang lang = new Lang(secuboid);
        lang.langconfig.set("testPath", "1 % 2 % 3 %");
        String message = lang.getMessage("testPath", "1", "2", "3");
        assertEquals("1 1 2 2 3 3", message);
    }
        
    @Test
    public void testMessage() {
        Lang lang = new Lang(secuboid);
        lang.langconfig.set("testPath", "1 %1% 2 %3% 3 %2%");
        String message = lang.getMessage("testPath", "1", "3", "2");
        assertEquals("1 1 2 2 3 3", message);
    }

    @Test
    public void testMessageNoParam() {
        Lang lang = new Lang(secuboid);
        lang.langconfig.set("testPath", "1 2 3 4");
        String message = lang.getMessage("testPath");
        assertEquals("1 2 3 4", message);
    }

    @Test
    public void testMessageEscapeChar() {
        Lang lang = new Lang(secuboid);
        lang.langconfig.set("testPath", "%1% entre sur le terrain %2%.");
        String message = lang.getMessage("testPath", "[Admin] Breston $", "toto");
        assertEquals("[Admin] Breston $ entre sur le terrain toto.", message);
    }
}
