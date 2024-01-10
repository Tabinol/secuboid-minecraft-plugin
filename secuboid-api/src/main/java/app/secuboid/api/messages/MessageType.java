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
 * Message type for message manager.
 */
public enum MessageType {

    /**
     * Title, header or footer
     */
    TITLE("", MessageColor.TITLE),

    /**
     * Normal text
     */
    NORMAL("", MessageColor.NORMAL),

    /**
     * Error
     */
    ERROR(MessageColor.ERROR_PREFIX + "!> ", MessageColor.ERROR),

    /**
     * No color
     */
    NO_COLOR("", ""),

    /**
     * Clickable
     */
    CLICKABLE("", MessageColor.CLICKABLE);

    public final String prefix;
    public final String color;

    private MessageType(String prefix, String color) {
        this.prefix = prefix;
        this.color = color;
    }
}
