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
package me.tabinol.secuboid.utilities;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * The Class Dates.
 */
public class Dates {
    
    /** The locale. */
    static Locale locale = Locale.getDefault();
    
    /** The actuelle. */
    static Date actuelle = new Date();
    
    /** The date format. */
    static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Date.
     *
     * @return the string
     */
    public static String date()
    {
            String dat = dateFormat.format(actuelle);
            return dat;
    }
    
    /**
     * Time.
     *
     * @return the string
     */
    public static String time()
    {
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String dat = sdf.format(cal.getTime());
            return dat;
    }
}
