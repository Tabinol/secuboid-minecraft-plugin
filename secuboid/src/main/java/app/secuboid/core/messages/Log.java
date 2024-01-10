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
package app.secuboid.core.messages;

import java.util.logging.Logger;

public class Log {

    private static final String LOGGER_PROPERTY_NAME = "java.util.logging.SimpleFormatter.format";
    private static final String LOGGER_PROPERTY_VALUE = "[%1$tF %1$tT] [%4$-7s] %5$s %n";

    private static Logger logger = null;

    private Log() {
    }

    public static void setLog(Logger logger) {
        Log.logger = logger;
    }

    public static Logger log() {
        if (logger == null) {
            System.setProperty(LOGGER_PROPERTY_NAME, LOGGER_PROPERTY_VALUE);
            logger = Logger.getGlobal();
        }

        return logger;
    }
}
