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

import java.util.ArrayList;

/**
 * The Class StringChanges. This is for static methods for String conversion or
 * modification.
 */
public class StringChanges {

    /**
     * To quote. Adds quote to a String
     *
     * @param str the String without quote
     * @return the quoted String
     */
    public static String toQuote(final String str) {

        String strRet;

        if (isStartQuote(str) && isEndQuote(str)) {
            strRet = (new StringBuffer(str).deleteCharAt(str.length() - 1).deleteCharAt(0)).toString();
        } else {
            strRet = str;
        }

        return "'" + strRet.replaceAll("'", "''") + "'";
    }

    /**
     * From quote. Remove quote from a String.
     *
     * @param str the quoted String
     * @return the string removed from quote
     */
    public static String fromQuote(final String str) {

        if (isStartQuote(str) && isEndQuote(str)) {
            return (new StringBuffer(str).deleteCharAt(str.length() - 1).deleteCharAt(0)).toString().replaceAll("''",
                    "'");
        } else {
            return str;
        }
    }

    /**
     * Split a String and make caution with quotes.
     *
     * @param str   the string to split
     * @param split the split character
     * @return the multiple String split
     */
    public static String[] splitKeepQuote(final String str, final String split) {

        final String[] strs = str.split(split);
        final ArrayList<String> strl = new ArrayList<String>();
        StringBuffer sb = null;

        for (final String strv : strs) {
            if (sb == null) {
                if (isStartQuote(strv)) {
                    sb = new StringBuffer(strv);
                } else {
                    strl.add(strv);
                }
            } else {
                sb.append(split).append(strv);
            }
            if (sb != null && isEndQuote(strv)) {
                strl.add(sb.toString());
                sb = null;
            }
        }

        return strl.toArray(new String[strl.size()]);
    }

    /**
     * Checks if the string starts with a quote.
     *
     * @param str the String
     * @return true, if the String starts with a quote
     */
    private static boolean isStartQuote(final String str) {

        return str.startsWith("'") || str.startsWith("\"");
    }

    /**
     * Checks if the String ends with a quote.
     *
     * @param str the String
     * @return true, if the String end with a quote
     */
    private static boolean isEndQuote(final String str) {

        return str.endsWith("'") || str.endsWith("\"") || str.endsWith(";") /* Fix String list */;
    }

    /**
     * Split the String and adds a empty at the end of the array.
     *
     * @param string the string
     * @param split  the split character
     * @return the string[]
     */
    public static String[] splitAddVoid(final String string, final String split) {

        final String[] tlist = string.split(split);
        final String[] result = new String[tlist.length + 1];
        System.arraycopy(tlist, 0, result, 0, tlist.length);
        result[tlist.length] = "";

        return result;
    }

    /**
     * Convert a array to a String with spaces.
     * 
     * @param arrayStr the string array
     * @param firstIdx the first index from 0
     * @param endIdx   the last index
     * @return a string
     */
    public static String arrayToString(final String[] arrayStr, final int firstIdx, final int endIdx) {
        final StringBuilder bf = new StringBuilder();
        int i = firstIdx;
        while (i < arrayStr.length && i <= endIdx) {
            if (i != firstIdx) {
                bf.append(" ");
            }
            bf.append(arrayStr[i]);
            i++;
        }
        return bf.toString();
    }
}
