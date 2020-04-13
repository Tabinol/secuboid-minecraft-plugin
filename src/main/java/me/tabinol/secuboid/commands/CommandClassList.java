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
package me.tabinol.secuboid.commands;

import me.tabinol.secuboid.commands.executor.*;

/**
 * Each command classes must be added in this list. The goal is to remove Reflection library.
 */
enum CommandClassList {

    ADMINMODE(CommandAdminmode.class),
    APPROVE(CommandApprove.class),
    AREA(CommandArea.class),
    BAN(CommandBan.class),
    BOTTOM(CommandBottom.class),
    CANCEL(CommandCancel.class),
    CONFIRM(CommandConfirm.class),
    CREATE(CommandCreate.class),
    DEFAULT(CommandDefault.class),
    FLAG(CommandFlag.class),
    HELP(CommandHelp.class),
    INFO(CommandInfo.class),
    INV(CommandInv.class),
    KICK(CommandKick.class),
    LIST(CommandList.class),
    MONEY(CommandMoney.class),
    NOTIFY(CommandNotify.class),
    OWNER(CommandOwner.class),
    PAGE(CommandPage.class),
    PARENT(CommandParent.class),
    PERMISSION(CommandPermission.class),
    PRIORITY(CommandPriority.class),
    RADIUS(CommandRadius.class),
    RELOAD(CommandReload.class),
    REMOVE(CommandRemove.class),
    RENAME(CommandRename.class),
    RENT(CommandRent.class),
    RESIDENT(CommandResident.class),
    SALE(CommandSale.class),
    SELECT(CommandSelect.class),
    SETSPAWN(CommandSetspawn.class),
    TOP(CommandTop.class),
    TP(CommandTp.class),
    TYPE(CommandType.class),
    WHO(CommandWho.class);

    private final Class<? extends CommandExec> commandClass;

    CommandClassList(Class<? extends CommandExec> commandClass) {
        this.commandClass = commandClass;
    }

    public Class<? extends CommandExec> getCommandClass() {
        return commandClass;
    }
}
