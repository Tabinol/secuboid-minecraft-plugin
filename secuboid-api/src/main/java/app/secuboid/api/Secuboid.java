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
package app.secuboid.api;

import app.secuboid.api.commands.CommandService;
import app.secuboid.api.flagtypes.FlagTypeService;
import app.secuboid.api.lands.LandService;
import app.secuboid.api.messages.MessageService;
import app.secuboid.api.players.PlayerInfoService;
import app.secuboid.api.recipients.RecipientService;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.api.services.ServiceService;

/**
 * Secuboid main api interface.
 */
public interface Secuboid {

    /**
     * Gets the command service.
     *
     * @return the command service
     */
    CommandService getCommandService();

    /**
     * Gets the flag types service.
     *
     * @return the flag types service
     */
    FlagTypeService getFlagTypeService();

    /**
     * Gets the message service.
     *
     * @return the message service
     */
    MessageService getMessageService();

    /**
     * Gets land  service.
     *
     * @return the lands service
     */
    LandService getLandService();

    /**
     * Gets online player information service.
     *
     * @return the player information service
     */
    PlayerInfoService getPlayerInfoService();

    /**
     * Gets the recipients service.
     *
     * @return the recipients service
     */
    RecipientService getRecipientService();

    /**
     * Gets the registration service.
     *
     * @return the registration service
     */
    RegistrationService getRegistrationService();

    /**
     * Gets the service manager.
     *
     * @return the service manager
     */
    ServiceService getServiceService();
}