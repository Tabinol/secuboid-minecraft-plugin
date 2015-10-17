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
package me.tabinol.secuboidapi.parameters;

/**
 * The Interface IFlagValue. Represent a flag value (Boolean, Double, String or
 * String[])
 */
public interface IFlagValue {

    /**
     * Copy of.
     *
     * @return a copy of this object
     */
    public IFlagValue copyOf();

    /**
	 * Sets the value. IMPORTANT: You must use the same type has the flag type:
	 * Boolean, Double, String or String[].
	 *
	 * @param value the new value
	 */
	public void setValue(Object value);

    /**
     * Gets the value.
     *
     * @return the value
     */
    public Object getValue();
    
    /**
     * Gets the value boolean.
     *
     * @return the value boolean
     */
    public boolean getValueBoolean();

    /**
     * Gets the value double.
     *
     * @return the value double
     */
    public double getValueDouble();

    /**
     * Gets the value string.
     *
     * @return the value string
     */
    public String getValueString();
    
    /**
     * Gets the value string list.
     *
     * @return the value string list
     */
    public String[] getValueStringList();

    /**
     * Gets the value in print format.
     *
     * @return the value in print format
     */
    public String getValuePrint();
}
