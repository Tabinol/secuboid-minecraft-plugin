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

/**
 * The Class Calculate.
 */
public class Calculate {

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
     * @param nb1 the nb1
     * @param nb2 the nb2
     * @return true, if is in interval
     */
    public static boolean isInInterval(int nbSource, int nb1, int nb2) {

	return nbSource >= lowerInt(nb1, nb2) && nbSource <= greaterInt(nb1, nb2);
    }

    // -1 before, 0 inside, +1 after
    /**
     * Compare position.
     *
     * @param nbSource the nb source
     * @param nb1 the nb1
     * @param nb2 the nb2
     * @return the int
     */
    public static int comparePosition(int nbSource, int nb1, int nb2) {

	if (nbSource < nb1) {
	    return -1;
	}
	if (nbSource > nb2) {
	    return 1;
	}
	return 0;
    }

    /**
     * Addition double.
     *
     * @param a the a
     * @param b the b
     * @return the double
     */
    public static Double AdditionDouble(Double a, Double b) {
	Double t;
	if (a < 0) {
	    t = a - b;
	} else {
	    t = a + b;
	}
	return t;
    }

    /**
     * Addition int.
     *
     * @param a the a
     * @param b the b
     * @return the int
     */
    public static int AdditionInt(int a, int b) {
	int t;
	if (a < 0) {
	    t = a - b;
	} else {
	    t = a + b;
	}
	return t;
    }
}
