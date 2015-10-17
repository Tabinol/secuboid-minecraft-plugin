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
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.utilities.Calculate;

import org.bukkit.ChatColor;


/**
 * The Class CommandPriority.
 */
@InfoCommand(name="priority", forceParameter=true)
public class CommandPriority extends CommandExec {

    /**
     * Instantiates a new command priority.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandPriority(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, false, null, null);
        String curArg = entity.argList.getNext();
        short newPrio;

        if (land.getParent() != null) {
            throw new SecuboidCommandException("Priority", entity.player, "COMMAND.PRIORITY.NOTCHILD");
        }
        try {
            newPrio = Short.parseShort(curArg);
        } catch (NumberFormatException ex) {
            throw new SecuboidCommandException("Priority", entity.player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        if (!Calculate.isInInterval(newPrio, Land.MINIM_PRIORITY, Land.MAXIM_PRIORITY)) {
            throw new SecuboidCommandException("Priority", entity.player, "COMMAND.PRIORITY.INVALID", Land.MINIM_PRIORITY + "", Land.MAXIM_PRIORITY + "");
        }
        land.setPriority(newPrio);

        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage(
                "COMMAND.PRIORITY.DONE", land.getName(), land.getPriority() + ""));
        Secuboid.getThisPlugin().iLog().write("Priority for land " + land.getName() + " changed for " + land.getPriority());
    }
}
