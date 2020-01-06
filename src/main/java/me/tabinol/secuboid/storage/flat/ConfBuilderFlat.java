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
package me.tabinol.secuboid.storage.flat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * The Class ConfBuilder.
 */
final class ConfBuilderFlat {

    /**
     * The br.
     */
    private final BufferedWriter br;

    /**
     * Instantiates a new conf builder.
     *
     * @param name    the name
     * @param uuid    the uuid
     * @param file    the file
     * @param version the version
     * @throws IOException Signals that an I/O exception has occurred.
     */
    ConfBuilderFlat(final String name, final UUID uuid, final File file, final int version) throws IOException {

        final FileWriter fr = new FileWriter(file, false);
        br = new BufferedWriter(fr);
        writeVersion(version);
        writeUUID(uuid);
        writeName(name);
    }

    /**
     * Write version.
     *
     * @param version the version
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeVersion(final int version) throws IOException {

        writeParam("Version", version);
    }

    /**
     * Write uuid.
     *
     * @param uuid the uuid
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeUUID(final UUID uuid) throws IOException {

        writeParam("UUID", uuid.toString());
    }

    /**
     * Write name.
     *
     * @param name the name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeName(final String name) throws IOException {

        writeParam("Name", name);
    }

    /**
     * Writeln.
     *
     * @param string the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeln(final String string) throws IOException {

        br.write(string);
        br.newLine();
    }

    /**
     * Write param.
     *
     * @param paramName the param name
     * @param param     the param
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void writeParam(final String paramName, final String param) throws IOException {

        if (param == null) {
            writeln(paramName + ":-null-");
        } else {
            writeln(paramName + ":" + param);
        }
    }

    /**
     * Write param.
     *
     * @param paramName the param name
     * @param param     the param
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void writeParam(final String paramName, final int param) throws IOException {

        writeln(paramName + ":" + param);
    }

    /**
     * Write param.
     *
     * @param paramName the param name
     * @param param     the param
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void writeParam(final String paramName, final short param) throws IOException {

        writeln(paramName + ":" + param);
    }

    /**
     * Write param.
     *
     * @param paramName the param name
     * @param param     the param
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void writeParam(final String paramName, final double param) throws IOException {

        writeln(paramName + ":" + param);
    }

    /**
     * Write param.
     *
     * @param paramName the param name
     * @param param     the param
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void writeParam(final String paramName, final boolean param) throws IOException {

        writeln(paramName + ":" + param);
    }

    /**
     * Write param.
     *
     * @param ParamName the param name
     * @param params    the params
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void writeParam(final String ParamName, final String[] params) throws IOException {

        if (params == null) {
            return;
        }
        writeln(ParamName + "{");
        for (final String param : params) {
            writeln("  " + param);
        }
        writeln("}");
    }

    /**
     * Close.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void close() throws IOException {

        br.close();
    }
}
