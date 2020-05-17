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
package me.tabinol.secuboid.exceptions;

import me.tabinol.secuboid.Secuboid;

/**
 * The Class FileLoadException.
 */
public class FileLoadException extends Exception {

    private static final long serialVersionUID = 1116607352612567604L;

    /**
     * Instantiates a new file load exception.
     *
     * @param secuboid the scuboid instance
     * @param FileName the file name
     * @param Line     the line
     * @param LineNum  the line num
     * @param message  the message
     */
    public FileLoadException(Secuboid secuboid, String FileName, String Line, Integer LineNum, String message) {

        super("File Load Exception in:" + FileName);
        secuboid.getLogger().severe("There is an error in file: " + FileName);
        secuboid.getLogger().severe("Line: " + LineNum);
        secuboid.getLogger().severe(Line);
        secuboid.getLogger().severe("Error Message: " + message);
    }
}
