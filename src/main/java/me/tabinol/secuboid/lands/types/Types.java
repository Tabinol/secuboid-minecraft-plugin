/*
 Secuboid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

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
package me.tabinol.secuboid.lands.types;

import java.util.Collection;
import java.util.TreeMap;

/**
 *
 * @author michel
 */
public class Types {

    final private TreeMap<String, Type> types;
    
    /**
     *
     */
    public Types() {
        
        types = new TreeMap<String, Type>();
    }
    
    /**
     *
     * @param arg0
     * @return
     */
    public Type addOrGetType(String arg0) {
        
        if(arg0 == null || arg0.isEmpty()) {
            return null;
        }
        
        String typeName = arg0.toLowerCase();
        
        Type type = types.get(typeName);
        
        if(type != null) {
            return type;
        }
        
        // not found, create it
        type = new Type(typeName);
        types.put(typeName, type);
        
        return type;
    }

    /**
     *
     * @param arg0
     * @return
     */
    public Type getType(String arg0) {

        return types.get(arg0.toLowerCase());
    }

    /**
     *
     * @return
     */
    public Collection<Type> getTypes() {

        return types.values();
    }
}
