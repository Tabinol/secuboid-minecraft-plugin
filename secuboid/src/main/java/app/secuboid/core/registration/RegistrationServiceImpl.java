/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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

package app.secuboid.core.registration;

import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.flagtypes.FlagType;
import app.secuboid.api.persistence.CreateTable;
import app.secuboid.api.persistence.JPA;
import app.secuboid.api.recipients.RecipientExec;
import app.secuboid.api.registration.CommandRegistered;
import app.secuboid.api.registration.RecipientRegistered;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.api.services.Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static app.secuboid.core.messages.Log.log;
import static java.util.logging.Level.SEVERE;

@NoArgsConstructor
@Getter
public class RegistrationServiceImpl implements RegistrationService {

    private final Map<Class<? extends JPA>, CreateTable> jpaClassToCreateTable = new LinkedHashMap<>();
    private final Map<Plugin, List<Service>> pluginToServices = new LinkedHashMap<>();
    private final Map<CommandExec, CommandRegistered> commandExecToCommandRegistered = new HashMap<>();
    private final Set<FlagType> flagTypes = new HashSet<>();
    private final Map<RecipientExec, RecipientRegistered> recipientExecToRecipientRegistered = new HashMap<>();
    private boolean isRegistrationClosed = false;

    @Override
    public void onEnable(boolean isServerBoot) {
        if (isServerBoot) {
            isRegistrationClosed = true;
        }
    }

    @Override
    public void registerJPA(Class<? extends JPA> jpaClass) {
        if (isRegistrationClosedMessage("JPA class", jpaClass.getName())) {
            return;
        }

        CreateTable createTable = jpaClass.getAnnotation(CreateTable.class);

        if (createTable != null) {
            jpaClassToCreateTable.put(jpaClass, createTable);
        } else {
            log().log(SEVERE, "The class {} needs to have the \"@CreateTable\" annotation",
                    jpaClass.getName());
        }
    }

    @Override
    public void registerService(Plugin plugin, Service service) {
        if (isRegistrationClosedMessage("service", service.getClass().getName())) {
            return;
        }

        pluginToServices.computeIfAbsent(plugin, p -> new ArrayList<>()).add(service);
    }

    @Override
    public void registerCommand(CommandExec commandExec) {
        Class<? extends CommandExec> clazz = commandExec.getClass();
        if (isRegistrationClosedMessage("command exec class", clazz.getName())) {
            return;
        }

        CommandRegistered commandRegistered = clazz.getAnnotation(CommandRegistered.class);

        if (commandRegistered != null) {
            commandExecToCommandRegistered.put(commandExec, commandRegistered);
        } else {
            log().log(SEVERE, "The class {} needs to have the \"@CommandRegistered\" annotation",
                    commandExec.getClass().getName());
        }
    }

    @Override
    public void registerFlagType(Class<?> clazz) {
        if (isRegistrationClosedMessage("flag types class", clazz.getName())) {
            return;
        }

        Arrays.stream(clazz.getFields()).forEach(field -> {
            if (field.getDeclaringClass().isAssignableFrom(FlagType.class)) {
                try {
                    registerFlagType((FlagType) field.get(null));
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log().log(SEVERE, e, () -> String.format("The field %s form class %s cannot be registered", field,
                            clazz.getName()));
                }
            }
        });
    }

    @Override
    public void registerFlagType(FlagType flagType) {
        if (isRegistrationClosedMessage("flag type", flagType.getName())) {
            return;
        }

        flagTypes.add(flagType);
    }

    @Override
    public void registerRecipient(RecipientExec recipientExec) {
        Class<? extends RecipientExec> clazz = recipientExec.getClass();
        if (isRegistrationClosedMessage("recipient exec class", clazz.getName())) {
            return;
        }

        RecipientRegistered recipientRegistered = clazz.getAnnotation(RecipientRegistered.class);

        if (recipientRegistered != null) {
            recipientExecToRecipientRegistered.put(recipientExec, recipientRegistered);
        } else {
            log().log(SEVERE, "The class {} needs to have the \"@CommandRegistered\" annotation",
                    recipientExec.getClass().getName());
        }
    }

    private boolean isRegistrationClosedMessage(String componentName, String name) {
        if (isRegistrationClosed) {
            log().log(SEVERE, "The {} \"{}\" needs to be registered in \"onLoad()\" method or Secuboid plugin not " +
                    "declared as dependence", new Object[]{componentName, name});
            return true;
        }

        return false;
    }
}
