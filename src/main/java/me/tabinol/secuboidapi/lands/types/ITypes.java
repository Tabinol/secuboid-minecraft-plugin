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
package me.tabinol.secuboidapi.lands.types;

import java.util.Collection;

/**
 * The Interface ITypes. Contains categories for lands
 */
public interface ITypes {

	/**
	 * Adds add or get type. If the type does not exist, it will be created.
	 *
	 * @param typeName the type name
	 * @return the i type
	 */
	public IType addOrGetType(String typeName);
	
	/**
	 * Gets the type from the name. Return null if the type does not exist.
	 *
	 * @param typeName the type name
	 * @return the type
	 */
	public IType getType(String typeName);
	
	/**
	 * Gets all the types.
	 *
	 * @return the types
	 */
	public Collection<IType> getTypes();
}
