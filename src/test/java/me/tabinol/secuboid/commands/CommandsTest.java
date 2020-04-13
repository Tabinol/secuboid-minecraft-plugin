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
package me.tabinol.secuboid.commands;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.reflections.Reflections;

/**
 * Test for commands annotations
 */
public final class CommandsTest {

	@Test
	public void annotationsTest() throws IOException, URISyntaxException {

		final Reflections reflections = new Reflections("me.tabinol.secuboid.commands.executor");
		final Set<Class<?>> classes = reflections.getTypesAnnotatedWith(InfoCommand.class);

		// Test if annoted classes are in CommandClassList
		for (final Class<?> classUnit : classes) {
			boolean found = false;
			for (CommandClassList commandList : CommandClassList.values()) {
				if (commandList.getCommandClass().equals(classUnit)) {
					found = true;
				}
			}
			if (!found) {
				fail("The class \"" + classUnit.getCanonicalName()
						+ "\" is not declared in class \"CommandClassList\".");
			}
		}
	}
}
