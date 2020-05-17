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
package me.tabinol.secuboid.playerscache.minecraftapi;

import org.junit.Test;

/**
 * Test profile requests
 */
public class ProfilesTest {

    private final String[] names = {"Notch", "Tabinol"};

    @Test
    public void testHtmlNamesRequest() {

        HttpProfileRepository httpProfileRepository = new HttpProfileRepository("minecraft");
        Profile[] profiles = httpProfileRepository.findProfilesByNames(names);

        for (int t = 0; t < names.length; t++) {

            // Write result
            System.out.println(names[t] + "-->" + profiles[t].getId());
        }
    }
}
