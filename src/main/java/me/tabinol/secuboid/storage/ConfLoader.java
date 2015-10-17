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
package me.tabinol.secuboid.storage;

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
public class ConfLoader {

    /** The version. */
    private int version;
    
    /** The uuid. */
    private UUID uuid;
    
    /** The name. */
    private String name;
    
    /** The param. */
    private String param = null;
    
    /** The value. */
    private String value = null;
    
    /** The file. */
    private final File file;
    
    /** The br. */
    private final BufferedReader br;
    
    /** The act line. */
    private String actLine = null; // Line read
    
    /** The act line nb. */
    private int actLineNb = 0; // Line nb

    /**
     * Instantiates a new conf loader.
     *
     * @param file the file
     * @throws FileLoadException the file load exception
     */
    public ConfLoader(File file) throws FileLoadException {

        this.file = file;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            // Impossible
        }
        br = new BufferedReader(fr);
        readVersion();
        if(version >= 2) {
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
        } catch(IllegalArgumentException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read UUID.");
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
    public String readln() throws FileLoadException {

        String lrt;

        actLineNb++;

        try {
            actLine = br.readLine();
        } catch (IOException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the next line.");
        }

        if (actLine == null) {
            return null;
        }
        lrt = actLine.trim();
        if (lrt.equals("") || lrt.equals("}")) {
            return null;
        }
        Secuboid.getThisPlugin().iLog().write("Readline: " + lrt);
        return lrt;
    }

    /**
     * Read param.
     *
     * @return true, if successful
     * @throws FileLoadException the file load exception
     */
    public boolean readParam() throws FileLoadException {

        String str = readln();

        if (str == null) {
            return false;
        }
        if (str.endsWith("\\{")) {
            param = str.replaceAll("\\{", "");
            value = null;
        } else if (str.contains(":")) {
            String[] chn = str.split(":", 2);
            param = chn[0];
            if (chn[1].equals("-null-")) {
                value = null;
            } else {
                value = chn[1];
            }
        }

        return true;
    }

    /**
     * Gets the param name.
     *
     * @return the param name
     */
    public String getParamName() {

        return param;
    }

    /**
     * Gets the value string.
     *
     * @return the value string
     */
    public String getValueString() {

        return value;
    }

    /**
     * Gets the value int.
     *
     * @return the value int
     * @throws FileLoadException the file load exception
     */
    public int getValueInt() throws FileLoadException {

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the Integer parameter.");
        }
    }

    /**
     * Gets the value short.
     *
     * @return the value short
     * @throws FileLoadException the file load exception
     */
    public short getValueShort() throws FileLoadException {

        try {
        return Short.parseShort(value);
        } catch (NumberFormatException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the Short parameter.");
        }
    }

    /**
     * Gets the value double.
     *
     * @return the value double
     * @throws FileLoadException the file load exception
     */
    public double getValueDouble() throws FileLoadException {

        try {
        return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Can't read the Double parameter.");
        }
    }

    /**
     * Gets the next string.
     *
     * @return the next string
     * @throws FileLoadException the file load exception
     */
    public String getNextString() throws FileLoadException {

        return readln();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public int getVersion() {

        return version;
    }
    
    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUUID() {
        
        return uuid;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {

        return file.getName();
    }

    // Used for errors
    /**
     * Gets the line nb.
     *
     * @return the line nb
     */
    public int getLineNb() {

        return actLineNb;
    }

    // Used for errors
    /**
     * Gets the line.
     *
     * @return the line
     */
    public String getLine() {

        return actLine;
    }

    /**
     * Close.
     *
     * @throws FileLoadException the file load exception
     */
    public void close() throws FileLoadException {
        try {
            br.close();
        } catch (IOException ex) {
            throw new FileLoadException(file.getName(), actLine, actLineNb, "Impossible to close the file.");
        }
    }
}
