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
package me.tabinol.secuboid.commands.executor;

import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;

/**
 * The Class CommandConfirm.
 */
@InfoCommand(name = "confirm")
public final class CommandConfirm extends CommandExec {

    /**
     * Instantiates a new command confirm.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandConfirm(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {
        CommandConfirmable commandConfirmable = playerConf.getCommandConfirmable();

        try {
            if (commandConfirmable != null) {
                playerConf.getCommandConfirmable().execConfirm();
            } else {
                throw new SecuboidCommandException(secuboid, "Nothing to confirm", player, "COMMAND.NOCONFIRM");
            }
        } finally {
            playerConf.setCommandConfirmable(null);
        }
    }
}
