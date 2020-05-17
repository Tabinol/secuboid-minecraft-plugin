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
package me.tabinol.secuboid.storage.flat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.FileLoadException;

/**
 * The Class ConfLoader.
 */
final class ConfLoaderFlat {

    private final Secuboid secuboid;

    /**
     * The version.
     */
    private int version;

    /**
     * The uuid.
     */
    private UUID uuid;

    /**
     * The name.
     */
    private String name;

    /**
     * The value.
     */
    private String value = null;

    /**
     * The file.
     */
    private final File file;

    /**
     * The br.
     */
    private final BufferedReader br;

    /**
     * The act line.
     */
    private String actLine = null; // Line read

    /**
     * The act line nb.
     */
    private int actLineNb = 0; // Line nb

    /**
     * Instantiates a new conf loader.
     *
     * @param secuboid secuboid instance
     * @param file     the file
     * @throws FileLoadException the file load exception
     */
    ConfLoaderFlat(final Secuboid secuboid, final File file) throws FileLoadException {

        this.secuboid = secuboid;
        this.file = file;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (final FileNotFoundException ex) {
            // Impossible
        }
        assert fr != null;
        br = new BufferedReader(fr);
        readVersion();
        if (version >= 2) {
            readUUID();
        } else {
            uuid = null;
        }
        readName();
    }

    /**
     * Read version.
     *
     * @throws FileLoadException the file load exception
     */
    private void readVersion() throws FileLoadException {

        readParam();
        version = getValueInt();
    }

    /**
     * Read uuid.
     *
     * @throws FileLoadException the file load exception
     */
    private void readUUID() throws FileLoadException {

        readParam();
        try {
            uuid = UUID.fromString(getValueString());
        } catch (final IllegalArgumentException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb, "Can't read UUID.");
        }
    }

    /**
     * Read name.
     *
     * @throws FileLoadException the file load exception
     */
    private void readName() throws FileLoadException {

        readParam();
        name = value;

    }

    /**
     * Readln.
     *
     * @return the string
     * @throws FileLoadException the file load exception
     */
    private String readln() throws FileLoadException {

        String lrt;

        actLineNb++;

        try {
            actLine = br.readLine();
        } catch (final IOException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb, "Can't read the next line.");
        }

        if (actLine == null) {
            return null;
        }
        lrt = actLine.trim();
        if (lrt.isEmpty() || lrt.equals("}")) {
            return null;
        }
        return lrt;
    }

    /**
     * Read param.
     *
     * @return true, if successful
     * @throws FileLoadException the file load exception
     */
    boolean readParam() throws FileLoadException {

        final String str = readln();

        if (str == null) {
            return false;
        }
        if (str.endsWith("\\{")) {
            value = null;
        } else if (str.contains(":")) {
            final String[] chn = str.split(":", 2);
            if (chn[1].equals("-null-")) {
                value = null;
            } else {
                value = chn[1];
            }
        }

        return true;
    }

    /**
     * Gets the value string.
     *
     * @return the value string
     */
    String getValueString() {

        return value;
    }

    /**
     * Gets the value int.
     *
     * @return the value int
     * @throws FileLoadException the file load exception
     */
    int getValueInt() throws FileLoadException {

        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb,
                    "Can't read the Integer parameter.");
        }
    }

    /**
     * Gets the value long.
     *
     * @return the value long
     * @throws FileLoadException the file load exception
     */
    long getValueLong() throws FileLoadException {

        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb,
                    "Can't read the Integer parameter.");
        }
    }

    /**
     * Gets the value short.
     *
     * @return the value short
     * @throws FileLoadException the file load exception
     */
    short getValueShort() throws FileLoadException {

        try {
            return Short.parseShort(value);
        } catch (final NumberFormatException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb,
                    "Can't read the Short parameter.");
        }
    }

    /**
     * Gets the value double.
     *
     * @return the value double
     * @throws FileLoadException the file load exception
     */
    double getValueDouble() throws FileLoadException {

        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb,
                    "Can't read the Double parameter.");
        }
    }

    /**
     * Gets the value boolean.
     *
     * @return the value boolean
     */
    boolean getValueBoolean() {
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets the next string.
     *
     * @return the next string
     * @throws FileLoadException the file load exception
     */
    String getNextString() throws FileLoadException {
        return readln();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName() {
        return name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    int getVersion() {
        return version;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    UUID getUUID() {

        return uuid;
    }

    /**
     * Gets the line nb.
     *
     * @return the line nb
     */
    int getLineNb() {
        return actLineNb;
    }

    // Used for errors

    /**
     * Gets the line.
     *
     * @return the line
     */
    String getLine() {
        return actLine;
    }

    /**
     * Close.
     *
     * @throws FileLoadException the file load exception
     */
    void close() throws FileLoadException {
        try {
            br.close();
        } catch (final IOException ex) {
            throw new FileLoadException(secuboid, file.getName(), actLine, actLineNb, "Impossible to close the file.");
        }
    }
}
