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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class BufferedWriterArray implements Closeable {

    private final BufferedWriter[] bwTargets;

    public BufferedWriterArray(Writer[] wTargets) {
        bwTargets = new BufferedWriter[wTargets.length];

        for (int i = 0; i < wTargets.length; i++) {
            bwTargets[i] = new BufferedWriter(wTargets[i]);
        }
    }

    public BufferedWriter[] getBwTargets() {
        return bwTargets;
    }

    @Override
    public void close() throws IOException {
        for (BufferedWriter bwTarget : bwTargets) {
            if (bwTarget != null) {
                bwTarget.close();
            }
        }
    }
}
