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

package me.tabinol.secuboid;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import me.tabinol.secuboid.commands.InfoCommand;
import org.junit.Test;
import org.reflections.Reflections;

/**
 * Test if all commands annotations.
 * @author mblanchet
 */
public class SecuboidTest {

    private String[] names = {"Notch"};
    
    @Test
    public void testCommandAnnotations() {
        
        // Create Command list
        Map<String, Class<?>> commands = new TreeMap<String, Class<?>>();
        
        // Gets all annotations
        Reflections reflections = new Reflections("me.tabinol.secuboid.commands.executor");
        Set<Class<?>> classCommands = 
                reflections.getTypesAnnotatedWith(InfoCommand.class);
        
        for(Class<?> presentClass : classCommands) {

            // Store commands information
            InfoCommand infoCommand = presentClass.getAnnotation(InfoCommand.class);
            commands.put(infoCommand.name().toLowerCase(), presentClass);
            for(String alias : infoCommand.aliases()) {
                commands.put(alias.toLowerCase(), presentClass);
            }
        }
    }
    
    @Test
    public void testHtmlNamesRequest() {
        
        HttpProfileRepository httpProfileRepository = new HttpProfileRepository("minecraft");
        Profile[] profiles = httpProfileRepository.findProfilesByNames(names);
        
        for(int t = 0; t < names.length; t ++) {
            
            // Write result
            System.out.println(names[t] + "-->" + profiles[t].getId());
        }
    }
}
