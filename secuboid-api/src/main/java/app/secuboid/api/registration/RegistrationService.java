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

package app.secuboid.api.registration;

import app.secuboid.api.commands.CommandExec;
import app.secuboid.api.flagtypes.FlagType;
import app.secuboid.api.persistence.JPA;
import app.secuboid.api.recipients.RecipientExec;
import app.secuboid.api.services.Service;
import org.bukkit.plugin.Plugin;

/**
 * All service, command, flag and recipient executors must be registered here.
 */
public interface RegistrationService extends Service {

    /**
     * Register a JPA with Hibernate and Jakarta annotation with {@link app.secuboid.api.persistence.JPA}
     * implementation. This is done in call order.
     *
     * @param jpaClass the jpa class
     */
    void registerJPA(Class<? extends JPA> jpaClass);

    /**
     * Register a service with {@link app.secuboid.api.services.Service}. The registration order is important here.
     *
     * @param plugin  the source plugin
     * @param service the service
     */
    void registerService(Plugin plugin, Service service);

    /**
     * Register a command with {@link app.secuboid.api.commands.CommandExec}. The class must be annotated with
     * {@link app.secuboid.api.registration.CommandRegistered}
     *
     * @param commandExec the command execution
     */
    void registerCommand(CommandExec commandExec);

    /**
     * Register all flag types from a class with {@link app.secuboid.api.flagtypes.FlagType}. It takes automatically
     * every {@link FlagType} public constants.
     *
     * @param clazz the class
     */
    void registerFlagType(Class<?> clazz);

    /**
     * Register a flag type instance with {@link app.secuboid.api.flagtypes.FlagType}.
     *
     * @param flagType the flag type
     */
    void registerFlagType(FlagType flagType);

    /**
     * Register a recipient type instance with {@link app.secuboid.api.recipients.RecipientExec}. The class must be
     * annotated with {@link app.secuboid.api.registration.RecipientRegistered}
     *
     * @param recipientExec the recipient execution
     */
    void registerRecipient(RecipientExec recipientExec);
}
