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
package me.tabinol.secuboid.config;

/**
 * Banned words for a land.
 */
public enum BannedWords {

    DONE,
    WORLDEDIT,
    EXPAND,
    SELECT,
    REMOVE,
    HERE,
    CURRENT,
    ADMINMODE,
    FACTOID,
    SECUBOID,
    CONSOLE,
    CLAIM,
    PAGE,
    CONFIG,
    AREA,
    SET,
    UNSET,
    LIST,
    DEFAULT,
    PRIORITY,
    NULL,
    APPROVE,
    RENAME;

    /**
     * The Constant INVALID_CHARACTERS.
     */
    private static final String[] INVALID_CHARACTERS = new String[]{
            ":", ";", "#", ",", ".", "*", "(", ")", "{", "}", "[", "]",
            "|", "\\", "/", "!", "?", "*", "\"", "'", "+", "-", "="};

    /**
     * Checks if is banned word or invalid.
     *
     * @param name The name to check if is invalid
     * @return true, if banned word or invalid character
     */
    public static boolean isBannedWord(String name) {

        // Pass 1 check for an invalid word
        try {
            valueOf(name.toUpperCase());
            // No catch, the name is in ban list
            return true;

        } catch (IllegalArgumentException ex) {
            // The word is not in ban list
        }

        // Pass 2 check for an invalid character
        for (String invalidChar : INVALID_CHARACTERS) {
            if (name.contains(invalidChar)) {
                return true;
            }
        }

        return false;
    }
}
