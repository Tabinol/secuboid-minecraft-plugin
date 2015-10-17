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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.parameters.PermissionList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class CommandWho.
 */
@InfoCommand(name="who")
public class CommandWho extends CommandExec {

    /**
     * Instantiates a new command who.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandWho(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        getLandFromCommandIfNoLandSelected();
        checkSelections(true, null);
        checkPermission(true, true, PermissionList.LAND_WHO.getPermissionType(), null);

        // Create list
        StringBuilder stList = new StringBuilder();
        for (Player player : land.getPlayersInLandNoVanish(entity.player)) {
            stList.append(player.getDisplayName()).append(Config.NEWLINE);
        }

        if (stList.length() != 0) {
            new ChatPage("COMMAND.WHO.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        } else {
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.WHO.LISTNULL", land.getName()));
        }
    }
}
