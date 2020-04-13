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
package me.tabinol.secuboid.permissionsflags;

import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.ChatColor;

/**
 * The Class FlagValue. Represent a flag value : Boolean, Double, String and String[]
 */
public class FlagValue {

    /**
     * The value.
     */
    private Object value;

    /**
     * Instantiates a new flag value.
     *
     * @param value the value
     */
    public FlagValue(Object value) {
        this.value = value;
    }

    public FlagValue copyOf() {

        if (value instanceof Boolean || value instanceof Double) {
            return new FlagValue(value);
        } else if (value instanceof String) {
            return new FlagValue(String.valueOf(value));
        } else if (value instanceof String[]) {
            String[] newStr = new String[((String[]) value).length];
            for (int t = 0; t < ((String[]) value).length; t++) {
                newStr[t] = String.valueOf(((String[]) value)[t]);
            }
            return new FlagValue(newStr);
        }
        return new FlagValue(value);
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public final Object getValue() {
        return value;
    }

    /**
     * Gets the value boolean.
     *
     * @return the value boolean
     */
    public final boolean getValueBoolean() {
        return (Boolean) value;
    }

    /**
     * Gets the value double.
     *
     * @return the value double
     */
    public final double getValueDouble() {
        return (Double) value;
    }

    /**
     * Gets the value string.
     *
     * @return the value string
     */
    public final String getValueString() {
        return (String) value;
    }

    /**
     * Gets the value string list.
     *
     * @return the value string list
     */
    public final String[] getValueStringList() {
        return (String[]) value;
    }

    /**
     * Gets the value print.
     *
     * @return the value print
     */
    public final String getValuePrint() {

        if (value instanceof Boolean) {
            if ((Boolean) value) {
                return "" + ChatColor.GREEN + true;
            } else {
                return "" + ChatColor.RED + false;
            }
        }

        if (value instanceof Double) {
            return value.toString();
        }

        if (value instanceof String) {
            return (String) value;
        }

        if (value instanceof String[]) {
            StringBuilder sb = new StringBuilder();
            for (String st : (String[]) value) {
                if (sb.length() != 0) {
                    sb.append("; ");
                }
                sb.append(StringChanges.toQuote(st));
            }
            return sb.toString();
        }

        return null;
    }
}
