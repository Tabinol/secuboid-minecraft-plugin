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
package me.tabinol.secuboid.it.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class WebUtils {

    public static void wget(final String url, final File targetFile) throws IOException {
        try (final InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
            try (final OutputStream fileOutput = new FileOutputStream(targetFile)) {
                try (final OutputStream bufferedOut = new BufferedOutputStream(fileOutput, 1024)) {
                    final byte data[] = new byte[1024];
                    int bytesNb;
                    while ((bytesNb = inputStream.read(data, 0, 1024)) > 0) {
                        bufferedOut.write(data, 0, bytesNb);
                    }
                }
            }
        }
    }
}