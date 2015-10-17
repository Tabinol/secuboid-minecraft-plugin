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
package me.tabinol.secuboidapi.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


/**
 * The Class StringChanges. This is for static methods for String conversion
 * or modification.
 */
public class StringChanges {

    /**
     * To lower. Convert a String list to lower case.
     *
     * @param list the String list
     * @return the converted String list
     */
    public static List<String> toLower(List<String> list) {

        if (list == null) {
            return null;
        }

        ArrayList<String> listLower = new ArrayList<String>();

        for (String str : list) {
            listLower.add(str.toLowerCase());
        }

        return listLower;
    }

    /**
     * To quote. Adds quote to a String
     *
     * @param str the String without quote
     * @return the quoted String
     */
    public static String toQuote(String str) {

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
    public static String fromQuote(String str) {

        if (isStartQuote(str) && isEndQuote(str)) {
            return (new StringBuffer(str).deleteCharAt(str.length() - 1).deleteCharAt(0)).toString().replaceAll("''", "'");
        } else {
            return str;
        }
    }

    /**
     * Split a String and make caution with quotes.
     *
     * @param str the string to split
     * @param split the split character
     * @return the multiple String split
     */
    public static String[] splitKeepQuote(String str, String split) {

        String[] strs = str.split(split);
        ArrayList<String> strl = new ArrayList<String>();
        StringBuffer sb = null;

        for (String strv : strs) {
            if (sb == null) {
                if (isStartQuote(strv)) {
                    sb = new StringBuffer(strv);
                } else {
                    strl.add(strv);
                }
            } else {
                sb.append(split).append(strv);
            }
            if(sb != null && isEndQuote(strv)) {
                strl.add(sb.toString());
                sb = null;
            }
        }
        
        return strl.toArray(new String[0]);
    }

    /**
     * Checks if the string starts with a quote.
     *
     * @param str the String
     * @return true, if the String starts with a quote
     */
    private static boolean isStartQuote(String str) {

        if (str.startsWith("'") || str.startsWith("\"")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the String ends with a quote.
     *
     * @param str the String
     * @return true, if the String end with a quote
     */
    private static boolean isEndQuote(String str) {

        if (str.endsWith("'") || str.endsWith("\"") || str.endsWith(";") /* Fix String list */) {
            return true;
        }
        return false;
    }
    
    /**
     * Split the String and adds a empty at the end of the array.
     *
     * @param string the string
     * @param split the split character
     * @return the string[]
     */
    public static String[] splitAddVoid(String string, String split) {
        
        String[] tlist = string.split(split);
        String[] result = new String[tlist.length + 1];
        for(int t = 0; t < tlist.length; t ++) {
            result[t] = tlist[t];
        }
        result[tlist.length] = "";
        
        return result;
    }
    
    /**
     * To integer.
     *
     * @param n the string
     * @return the int
     */
    public static int toInteger(String n){
        try{
           return Integer.parseInt(n);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    /**
     * Checks if is int.
     *
     * @param n the string
     * @return true, if is int
     */
    public static boolean isInt(String n)
    {
        try{
            Integer.parseInt(n);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    /**
     * To double.
     *
     * @param n the string
     * @return the double
     */
    public static double toDouble(String n){
        try{
           return Double.parseDouble(n);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    /**
     * Checks if is double.
     *
     * @param n the string
     * @return true, if is double
     */
    public static boolean isDouble(String n)
    {
        try{
            Double.parseDouble(n);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    /**
     * To long.
     *
     * @param n the long
     * @return the long
     */
    public static long toLong(String n){
        try{
           return Long.parseLong(n);
        }catch(NumberFormatException e){
            return 0;
        }
    }
    
    /**
     * Checks if is long.
     *
     * @param n the long
     * @return true, if is long
     */
    public static boolean isLong(String n)
    {
        try{
            Long.parseLong(n);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    /**
     * Convert the string. The first character will be upper case and the rest
     * will be lower case.
     *
     * @param str the string
     * @return the string converted
     */
    public static String FirstUpperThenLower(String str) {
        
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Location to string.
     *
     * @param location the location
     * @return the string
     */
    public static String locationToString(Location location) {
    	
    	return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() 
    			+ ";" + location.getYaw() + ";" + location.getPitch();
    }
    
    /**
     * String to location.
     *
     * @param locStr the string
     * @return the location
     */
    public static Location stringToLocation(String locStr) {
    	
    	String[] strs = locStr.split("\\;");
    	
    	// Wrong parameter
    	if(strs.length != 6) {
    		return null;
    	}
    	
    	World world = Bukkit.getWorld(strs[0]);
    	
    	if(world == null) {
    		return null;
    	}
    	
    	// Get the location
    	Location location;
    	
    	try {
    		location = new Location(world, Double.parseDouble(strs[1]), Double.parseDouble(strs[2]),
    				Double.parseDouble(strs[3]), Float.parseFloat(strs[4]), Float.parseFloat(strs[5]));
    	} catch(NumberFormatException ex) {
    		
    		// if location is wrong, set null
    		location = null;
    	}
    	
    	return location;
    }
}
