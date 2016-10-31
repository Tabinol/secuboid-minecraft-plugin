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
package me.tabinol.secuboid.commands;

import me.tabinol.secuboid.commands.executor.*;

/**
 * Each command classes must be added in this list. The goal is to remove Reflection library
 */
public enum CommandClassList {

    /**
     *
     */
    CommandAdminmode(CommandAdminmode.class),
    /**
     *
     */
    CommandApprove(CommandApprove.class),
    /**
     *
     */
    CommandArea(CommandArea.class),
    /**
     *
     */
    CommandKick(CommandKick.class),
    /**
     *
     */
    CommandRent(CommandRent.class),
    /**
     *
     */
    CommandBan(CommandBan.class),
    /**
     *
     */
    CommandList(CommandList.class),
    /**
     *
     */
    CommandResident(CommandResident.class),
    /**
     *
     */
    CommandCancel(CommandCancel.class),
    /**
     *
     */
    CommandMoney(CommandMoney.class),
    /**
     *
     */
    CommandSale(CommandSale.class),
    /**
     *
     */
    CommandConfirm(CommandConfirm.class),
    /**
     *
     */
    CommandNotify(CommandNotify.class),
    /**
     *
     */
    CommandSelect(CommandSelect.class),
    /**
     *
     */
    CommandCreate(CommandCreate.class),
    /**
     *
     */
    CommandOwner(CommandOwner.class),
    /**
     *
     */
    CommandDefault(CommandDefault.class),
    /**
     *
     */
    CommandPage(CommandPage.class),
    /**
     *
     */
    CommandSetspawn(CommandSetspawn.class),
    /**
     *
     */
    CommandParent(CommandParent.class),
    /**
     *
     */
    CommandTp(CommandTp.class),
    /**
     *
     */
    CommandExpand(CommandExpand.class),
    /**
     *
     */
    CommandPermission(CommandPermission.class),
    /**
     *
     */
    CommandType(CommandType.class),
    /**
     *
     */
    CommandFlag(CommandFlag.class),
    /**
     *
     */
    CommandPriority(CommandPriority.class),
    /**
     *
     */
    CommandWho(CommandWho.class),
    /**
     *
     */
    CommandHelp(CommandHelp.class),
    /**
     *
     */
    CommandReload(CommandReload.class);

    private final Class<?> commandClass;

    private CommandClassList(Class<?> commandClass) {

	this.commandClass = commandClass;
    }

    /**
     *
     * @return
     */
    public Class<?> getCommandClass() {

	return commandClass;
    }
}
