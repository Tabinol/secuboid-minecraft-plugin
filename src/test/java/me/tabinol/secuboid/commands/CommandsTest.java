/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2015 Tabinol
 Forked from Factoid (Copyright (C) 2014 Kaz00, Tabinol)

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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.reflections.Reflections;

/**
 * Test for commands annotations
 */
public class CommandsTest {

	@Test
	public void AnnotationsTest() throws IOException, URISyntaxException {

		Reflections reflections = new Reflections("me.tabinol.secuboid.commands.executor");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(InfoCommand.class);

		// Test if annoted classes are in CommandClassList
		for (Class<?> classUnit : classes) {
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

		// TODO Only a test to remove
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources = classLoader.getResources(".");
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			System.out.println(resource.getFile());
			Path path = Paths.get(resource.getFile().replaceFirst("^/", ""));
			Files.find(path, Integer.MAX_VALUE,
					(filePath, fileAttr) -> filePath.toString().endsWith(".class") && fileAttr.isRegularFile())
					.forEach(file -> {
						try {
							System.out.println(Class.forName(file.toString()));
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
		}
	}
}
