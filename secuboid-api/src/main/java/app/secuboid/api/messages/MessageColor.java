/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.api.messages;

/**
 * Class for default message colors.
 */
public class MessageColor {

    /**
     * The color code prefix
     */
    public static final char COLOR_CODE = '\u00A7';

    /**
     * Header, footer and title color
     */
    public static final String TITLE = COLOR_CODE + "6";

    /**
     * Normal text color
     */
    public static final String NORMAL = COLOR_CODE + "6";

    /**
     * Error prefix color
     */
    public static final String ERROR_PREFIX = COLOR_CODE + "c";

    /**
     * Error text color
     */
    public static final String ERROR = COLOR_CODE + "e";

    /**
     * Name color
     */
    public static final String NAME = COLOR_CODE + "3";

    /**
     * Number color
     */
    public static final String NUMBER = COLOR_CODE + "2";

    /**
     * Boolean true color
     */
    public static final String TRUE = COLOR_CODE + "2";

    /**
     * Boolean false color
     */
    public static final String FALSE = COLOR_CODE + "4";

    /**
     * Clickable link
     */
    public static final String CLICKABLE = COLOR_CODE + "6" + COLOR_CODE + "n";

    private MessageColor() {
    }
}
