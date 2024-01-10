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

package app.secuboid.core.messages;

import app.secuboid.api.players.CommandSenderInfo;
import app.secuboid.api.services.Service;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static app.secuboid.core.messages.Log.log;
import static java.util.logging.Level.SEVERE;

public class ChatGetterService implements Service {

    private final JavaPlugin javaPlugin;
    private final BukkitScheduler scheduler;
    private final ConcurrentMap<CommandSenderInfo, Consumer<String>> commandSenderInfoToCallback;

    public ChatGetterService(JavaPlugin javaPlugin, BukkitScheduler scheduler) {
        this.javaPlugin = javaPlugin;
        this.scheduler = scheduler;
        commandSenderInfoToCallback = new ConcurrentHashMap<>();
    }

    @Override
    public void onEnable(boolean isServerBoot) {
        if (!isServerBoot) {
            commandSenderInfoToCallback.clear();
        }
    }

    public void remove(CommandSenderInfo commandSenderInfo) {
        commandSenderInfoToCallback.remove(commandSenderInfo);
    }


    public void put(CommandSenderInfo commandSenderInfo, Consumer<String> callback) {
        this.commandSenderInfoToCallback.put(commandSenderInfo, callback);
    }

    public boolean checkAnswerAndCallBackIfNeeded(CommandSenderInfo commandSenderInfo, String message) {
        Consumer<String> callback = this.commandSenderInfoToCallback.remove(commandSenderInfo);

        if (callback != null) {
            scheduler.callSyncMethod(javaPlugin, () -> {
                try {
                    callback.accept(message);
                } catch (RuntimeException e) {
                    log().log(SEVERE, "Exception in chat answer callback", e);
                }
                return null;
            });
            return true;
        }

        return false;
    }
}
