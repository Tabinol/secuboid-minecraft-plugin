/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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

package app.secuboid.generator.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterArray implements Closeable {

    private final FileWriter[] fwTargets;
    private final File[] fileTargets;

    public FileWriterArray(String[] targets) throws IOException {
        fwTargets = new FileWriter[targets.length];
        fileTargets = new File[targets.length];

        for (int i = 0; i < targets.length; i++) {
            //noinspection resource
            fwTargets[i] = new FileWriter(targets[i]);
            fileTargets[i] = new File(targets[i]);
        }
    }

    public FileWriter[] getFwTargets() {
        return fwTargets;
    }

    public File[] getFileTargets() {
        return fileTargets;
    }

    @Override
    public void close() throws IOException {
        for (FileWriter fwTarget : fwTargets) {
            if (fwTarget != null) {
                fwTarget.close();
            }
        }
    }
}
