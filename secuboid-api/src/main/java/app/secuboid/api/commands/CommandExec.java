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
package app.secuboid.api.commands;

import app.secuboid.api.players.CommandSenderInfo;

/**
 * If you create a command for Secuboid, you should implement this interface and add CommandRegistered annotation.
 * Don't forget to add your class in
 * {@link app.secuboid.api.registration.RegistrationService#registerCommand(CommandExec)}.
 */
public interface CommandExec {

    /**
     * Where the command is executed.
     *
     * @param commandSenderInfo the command sender secuboid information (player or
     *                          console)
     * @param subArgs           the argument array (exclude the command itself)
     */
    void commandExec(CommandSenderInfo commandSenderInfo, String[] subArgs);
}
