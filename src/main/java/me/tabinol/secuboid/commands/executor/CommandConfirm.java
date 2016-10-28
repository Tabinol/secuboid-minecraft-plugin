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
package me.tabinol.secuboid.commands.executor;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.ConfirmEntry;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import org.bukkit.ChatColor;

/**
 * The Class CommandConfirm.
 */
@InfoCommand(name = "confirm")
public class CommandConfirm extends CommandExec {

    /**
     * Instantiates a new command confirm.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandConfirm(CommandEntities entity) throws SecuboidCommandException {

	super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

	ConfirmEntry confirmEntry;

	if ((confirmEntry = entity.playerConf.getConfirm()) != null) {
	    if (confirmEntry.confirmType != null) {
		switch (confirmEntry.confirmType) {

		    case REMOVE_LAND:
			// Remove land
			int i = confirmEntry.land.getAreas().size();
			try {
			    Secuboid.getThisPlugin().getLands().removeLand(confirmEntry.land);
			} catch (SecuboidLandException ex) {
			    Logger.getLogger(CommandConfirm.class.getName()).log(Level.SEVERE, "On land remove", ex);
			    throw new SecuboidCommandException("On land remove", entity.player, "GENERAL.ERROR");
			}
			entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND", confirmEntry.land.getName(), i + ""));
			Secuboid.getThisPlugin().getLog().write(entity.playerName + " confirm for removing " + confirmEntry.land.getName());
			break;

		    case REMOVE_AREA:
			// Remove area
			if (!confirmEntry.land.removeArea(confirmEntry.areaNb)) {
			    throw new SecuboidCommandException("Area", entity.player, "COMMAND.REMOVE.AREA.INVALID");
			}
			entity.playerConf.getSelection().refreshLand();
			entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA", confirmEntry.land.getName()));
			Secuboid.getThisPlugin().getLog().write("area " + confirmEntry.areaNb + " for land " + confirmEntry.land.getName() + " is removed by " + entity.playerName);
			break;

		    case LAND_DEFAULT:
			// Set to default
			confirmEntry.land.setDefault();
			entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SETDEFAULT.ISDONE", confirmEntry.land.getName()));
			Secuboid.getThisPlugin().getLog().write("The land " + confirmEntry.land.getName() + "is set to default configuration by " + entity.playerName);
			break;

		    default:
			break;
		}
	    }

	    // Remove confirm
	    entity.playerConf.setConfirm(null);

	} else {

	    throw new SecuboidCommandException("Nothing to confirm", entity.player, "COMMAND.NOCONFIRM");
	}
    }
}
