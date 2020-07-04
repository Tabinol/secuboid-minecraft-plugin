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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ConfirmEntry;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;

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

        final ConfirmEntry confirmEntry;

        if ((confirmEntry = playerConf.getConfirm()) != null) {
            if (confirmEntry.confirmType != null) {
                switch (confirmEntry.confirmType) {

                    case REMOVE_LAND:
                        removeLand(confirmEntry);
                        break;

                    case REMOVE_AREA:
                        // Remove area
                        if (!confirmEntry.land.removeArea(confirmEntry.areaNb)) {
                            throw new SecuboidCommandException(secuboid, "Area", player, "COMMAND.REMOVE.AREA.INVALID");
                        }
                        playerConf.getSelection().refreshLand();
                        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage()
                                .getMessage("COMMAND.REMOVE.DONE.AREA", confirmEntry.land.getName()));
                        break;

                    case LAND_DEFAULT:
                        // Set to default
                        confirmEntry.land.setDefault();
                        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage()
                                .getMessage("COMMAND.SETDEFAULT.ISDONE", confirmEntry.land.getName()));
                        break;

                    default:
                        break;
                }
            }

            // Remove confirm
            playerConf.setConfirm(null);

        } else {

            throw new SecuboidCommandException(secuboid, "Nothing to confirm", player, "COMMAND.NOCONFIRM");
        }
    }

    private void removeLand(final ConfirmEntry confirmEntry) throws SecuboidCommandException {
        final int i = confirmEntry.land.getAreas().size();
        final LandAction landAction = confirmEntry.landActionNullable;
        try {
            switch (landAction) {
                case LAND_REMOVE:
                    secuboid.getLands().removeLand(confirmEntry.land);
                    break;
                case LAND_REMOVE_FORCE:
                    secuboid.getLands().removeLandForce(confirmEntry.land);
                    break;
                case LAND_REMOVE_RECURSIVE:
                    secuboid.getLands().removeLandRecursive(confirmEntry.land);
                    break;
                default:
                    // Never done
            }
        } catch (final SecuboidLandException ex) {
            ex.printStackTrace();
            throw new SecuboidCommandException(secuboid, "On land remove", player, "GENERAL.ERROR");
        }
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                + secuboid.getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND", confirmEntry.land.getName(), i + ""));
    }
}
