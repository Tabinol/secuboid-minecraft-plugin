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

package app.secuboid.api.players;

import app.secuboid.api.services.Service;

/**
 * Chat page service for a command output.
 */
public interface ChatPageService extends Service {

    /**
     * Shows a text in the chat and creates other pages if needed.
     *
     * @param commandSenderInfo the command sender info
     * @param subject           the subject
     * @param text              the text
     */
    void show(CommandSenderInfo commandSenderInfo, String subject, String text);

    /**
     * Shows a page to the player or the console sender. If the page does not exist, an error message is sent to the
     * user.
     *
     * @param commandSenderInfo the command sender info
     * @param pageNumber        the page number to show or an error message
     */
    void show(CommandSenderInfo commandSenderInfo, int pageNumber);

    /**
     * Gets the total number of pages for the last command or 0 if there is no page stored.
     *
     * @param commandSenderInfo the command sender info
     * @return the total number of pages or 0 if no page is stored
     */
    int getTotalPages(CommandSenderInfo commandSenderInfo);

    /**
     * Removes any page for this command sender.
     *
     * @param commandSenderInfo the command sender info
     */
    void remove(CommandSenderInfo commandSenderInfo);
}
