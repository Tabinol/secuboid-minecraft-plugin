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

/**
 * The Class LocalMath.
 */
public class LocalMath {

    /**
     * Greater int.
     *
     * @param nb1 the nb1
     * @param nb2 the nb2
     * @return the int
     */
    public static int greaterInt(int nb1, int nb2) {

        if (nb1 > nb2) {
            return nb1;
        } else {
            return nb2;
        }
    }

    /**
     * Lower int.
     *
     * @param nb1 the nb1
     * @param nb2 the nb2
     * @return the int
     */
    public static int lowerInt(int nb1, int nb2) {

        if (nb1 < nb2) {
            return nb1;
        } else {
            return nb2;
        }
    }

    /**
     * Checks if is in interval.
     *
     * @param nbSource the nb source
     * @param nb1      the nb1
     * @param nb2      the nb2
     * @return true, if is in interval
     */
    public static boolean isInInterval(int nbSource, int nb1, int nb2) {

        return nbSource >= lowerInt(nb1, nb2) && nbSource <= greaterInt(nb1, nb2);
    }
}
