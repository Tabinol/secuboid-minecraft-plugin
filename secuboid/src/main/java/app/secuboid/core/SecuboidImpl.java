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
package app.secuboid.core;

import app.secuboid.api.Secuboid;
import app.secuboid.api.SecuboidComponent;
import app.secuboid.api.SecuboidPlugin;
import app.secuboid.api.commands.CommandService;
import app.secuboid.api.flagtypes.FlagTypeService;
import app.secuboid.api.lands.LandService;
import app.secuboid.api.lands.areas.AreaService;
import app.secuboid.api.messages.MessageManagerService;
import app.secuboid.api.messages.MessageService;
import app.secuboid.api.players.ChatPageService;
import app.secuboid.api.players.PlayerInfoService;
import app.secuboid.api.recipients.RecipientService;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.api.services.ServiceService;
import app.secuboid.core.commands.CommandListenerService;
import app.secuboid.core.commands.CommandServiceImpl;
import app.secuboid.core.commands.exec.*;
import app.secuboid.core.config.ConfigService;
import app.secuboid.core.flags.FlagDeclarations;
import app.secuboid.core.flagtypes.FlagTypeServiceImpl;
import app.secuboid.core.items.SecuboidToolService;
import app.secuboid.core.lands.LandServiceImpl;
import app.secuboid.core.lands.areas.AreaServiceImpl;
import app.secuboid.core.listeners.*;
import app.secuboid.core.messages.ChatGetterService;
import app.secuboid.core.messages.Log;
import app.secuboid.core.messages.MessageServiceImpl;
import app.secuboid.core.persistence.PersistenceService;
import app.secuboid.core.persistence.PersistenceSessionService;
import app.secuboid.core.persistence.jpa.AreaJPA;
import app.secuboid.core.persistence.jpa.LandJPA;
import app.secuboid.core.persistence.jpa.RecipientJPA;
import app.secuboid.core.persistence.jpa.ResidentJPA;
import app.secuboid.core.players.ChatPageServiceImpl;
import app.secuboid.core.players.PlayerInfoServiceImpl;
import app.secuboid.core.recipients.RecipientServiceImpl;
import app.secuboid.core.registration.RegistrationServiceImpl;
import app.secuboid.core.scoreboard.ScoreboardService;
import app.secuboid.core.services.ServiceServiceImpl;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

@Getter
public class SecuboidImpl implements Secuboid, SecuboidComponent {

    private final SecuboidPlugin secuboidPlugin;

    // Bukkit instances in alphabetical order
    private final PluginManager pluginManager;

    // Secuboid instances in alphabetical order
    private final AreaService areaService;
    private final ChatGetterService chatGetterService;
    private final ChatPageService chatPageService;
    private final CommandService commandService;
    private final CommandListenerService commandListenerService;
    private final ConfigService configService;
    private final FlagTypeService flagTypeService;
    private final LandService landService;
    private final MessageManagerService messageManagerService;
    private final MessageService messageService;
    private final PersistenceSessionService persistenceSessionService;
    private final PersistenceService persistenceService;
    private final PlayerInfoService playerInfoService;
    private final RecipientService recipientService;
    private final RegistrationService registrationService;
    private final ScoreboardService scoreboardService;
    private final SecuboidToolService secuboidToolService;
    private final ServiceService serviceService;

    public SecuboidImpl(SecuboidPlugin secuboidPlugin) {
        this.secuboidPlugin = secuboidPlugin;

        // Bukkit/Spigot instances
        Server server = secuboidPlugin.getServer();
        pluginManager = server.getPluginManager();
        BukkitScheduler scheduler = server.getScheduler();
        ScoreboardManager scoreboardManager = server.getScoreboardManager();

        // Secuboid instances
        // phase 1 in alphabetical order
        configService = new ConfigService(secuboidPlugin);
        areaService = new AreaServiceImpl();
        chatGetterService = new ChatGetterService(secuboidPlugin, scheduler);
        registrationService = new RegistrationServiceImpl();

        // phase 2 in alphabetical order
        messageService = new MessageServiceImpl(configService);
        persistenceSessionService = new PersistenceSessionService(configService, registrationService);

        // phase 3 in alphabetical order
        persistenceService = new PersistenceService(secuboidPlugin, scheduler, persistenceSessionService);

        // phase 4 in alphabetical order
        flagTypeService = new FlagTypeServiceImpl(registrationService);
        landService = new LandServiceImpl(server, areaService, persistenceService);
        messageManagerService = messageService.grab(secuboidPlugin);
        recipientService = new RecipientServiceImpl(registrationService);
        serviceService = new ServiceServiceImpl(registrationService);

        // phase 5 in alphabetical order
        playerInfoService = new PlayerInfoServiceImpl(server, areaService, landService);
        chatPageService = new ChatPageServiceImpl(messageManagerService);
        scoreboardService = new ScoreboardService(scoreboardManager, messageManagerService);
        secuboidToolService = new SecuboidToolService(secuboidPlugin, messageManagerService);

        // phase 6 in alphabetical order
        commandService = new CommandServiceImpl(chatPageService, registrationService);

        // phase 7 in alphabetical order
        commandListenerService = new CommandListenerService(secuboidPlugin, commandService, playerInfoService);
    }

    @Override
    public void onLoad() {
        Log.setLog(secuboidPlugin.getLogger());
        registerJPAs();
        registerServices();
        registerCommands();
        registerFlagTypes();
        registerRecipients();
    }

    @Override
    public void onEnable(boolean isServerBoot) {
        serviceService.onEnable(secuboidPlugin);

        if (isServerBoot) {
            registerListeners();
        }
    }

    @Override
    public void onDisable() {
        serviceService.onDisable(secuboidPlugin);
    }

    @Override
    public void reload() {
        ((ServiceServiceImpl) serviceService).onDisableReload();
        ((ServiceServiceImpl) serviceService).onEnableReload();
    }

    private void registerJPAs() {

        // Register JPAs

        // phase 1 in alphabetical order
        registrationService.registerJPA(RecipientJPA.class);
        registrationService.registerJPA(LandJPA.class);

        // phase 2 in alphabetical order
        registrationService.registerJPA(AreaJPA.class);
        registrationService.registerJPA(ResidentJPA.class);
    }

    private void registerServices() {

        // Register services
        // phase 1 in alphabetical order
        registrationService.registerService(secuboidPlugin, registrationService);
        registrationService.registerService(secuboidPlugin, serviceService);

        // phase 2 in alphabetical order
        registrationService.registerService(secuboidPlugin, configService);

        // phase 3 in alphabetical order
        registrationService.registerService(secuboidPlugin, persistenceSessionService);

        // phase 4 in alphabetical order
        registrationService.registerService(secuboidPlugin, persistenceService);

        // phase 5 in alphabetical order
        registrationService.registerService(secuboidPlugin, areaService);
        registrationService.registerService(secuboidPlugin, chatGetterService);
        registrationService.registerService(secuboidPlugin, commandListenerService);
        registrationService.registerService(secuboidPlugin, commandService);
        registrationService.registerService(secuboidPlugin, flagTypeService);
        registrationService.registerService(secuboidPlugin, messageManagerService);
        registrationService.registerService(secuboidPlugin, messageService);
        registrationService.registerService(secuboidPlugin, recipientService);
        registrationService.registerService(secuboidPlugin, secuboidToolService);

        // Phase 6 in alphabetical order
        registrationService.registerService(secuboidPlugin, scoreboardService);

        // Phase 7 in alphabetical order
        registrationService.registerService(secuboidPlugin, landService);

        // Phase 8 in alphabetical order
        registrationService.registerService(secuboidPlugin, playerInfoService);

        // Phase 9 in alphabetical order
        registrationService.registerService(secuboidPlugin, chatPageService);

    }

    private void registerCommands() {

        // Register commands in alphabetical order
        registrationService.registerCommand(new CommandCancel(messageManagerService));
        registrationService.registerCommand(new CommandCreate(chatGetterService, landService, messageManagerService,
                recipientService));
        registrationService.registerCommand(new CommandInfo(messageManagerService));
        registrationService.registerCommand(new CommandPage(chatPageService, messageManagerService));
        registrationService.registerCommand(new CommandReload(this, messageManagerService));
        registrationService.registerCommand(new CommandSelect(commandService));
        registrationService.registerCommand(new CommandSelectCuboid(configService, scoreboardService));
        registrationService.registerCommand(new CommandSelectHere(scoreboardService));
        registrationService.registerCommand(new CommandTool(secuboidToolService));
    }

    private void registerRecipients() {
        // TODO
    }

    private void registerFlagTypes() {
        registrationService.registerFlagType(FlagDeclarations.class);
    }

    private void registerListeners() {
        pluginManager.registerEvents(new AsyncPlayerChatListener(chatGetterService, playerInfoService), secuboidPlugin);
        pluginManager.registerEvents(new PlayerConnectionListener(chatGetterService, playerInfoService), secuboidPlugin);
        pluginManager.registerEvents(new PlayerMoveListener(chatGetterService, messageManagerService,
                playerInfoService), secuboidPlugin);
        pluginManager.registerEvents(new SecuboidToolListener(playerInfoService, secuboidToolService), secuboidPlugin);
        pluginManager.registerEvents(new WorldListener(landService), secuboidPlugin);
    }
}
