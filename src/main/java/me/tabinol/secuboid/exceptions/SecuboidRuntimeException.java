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

/**
 * Catch all "should not happen errors".
 */
public class SecuboidRuntimeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SecuboidRuntimeException() {
        super();
    }

    public SecuboidRuntimeException(final String message) {
        super(message);
    }

    public SecuboidRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SecuboidRuntimeException(final Throwable cause) {
        super(cause);
    }
}