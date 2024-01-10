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
package app.secuboid.core.commands;

import app.secuboid.api.commands.CommandService;
import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.players.PlayerInfoService;
import app.secuboid.api.services.Service;
import app.secuboid.core.messages.Log;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CommandListenerService implements CommandExecutor, Service {

    private static final String COMMAND_NAME = "secuboid";

    private final JavaPlugin javaPlugin;
    private final CommandService commandService;
    private final PlayerInfoService playerInfoService;

    public CommandListenerService(JavaPlugin javaPlugin, CommandService commandService,
                                  PlayerInfoService playerInfoService) {
        this.javaPlugin = javaPlugin;
        this.commandService = commandService;
        this.playerInfoService = playerInfoService;
    }

    @Override
    public void onEnable(boolean isServerBoot) {
        if (!isServerBoot) {
            return;
        }

        PluginCommand pluginCommand = javaPlugin.getCommand(COMMAND_NAME);
        pluginCommand.setExecutor(this);
    }

    @Override
    @SuppressWarnings("java:S3516")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandSenderInfo commandSenderInfo = playerInfoService.get(sender);

        if (commandSenderInfo == null) {
            Log.log().log(Level.WARNING, "The player sent a command and is not available. [sender={}]",
                    sender.getName());
            return true;
        }

        ((CommandServiceImpl) commandService).executeCommandName(commandSenderInfo, args);

        return true;
    }

}