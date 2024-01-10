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
package app.secuboid.api.recipients;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.flags.Flag;
import org.bukkit.entity.Entity;

/**
 * Represents a recipientExec (ex: entity, mob, players, ...) If you implements a new recipientExec, you need to add the
 * annotation RecipientRegistered and add it to
 * {@link app.secuboid.api.registration.RegistrationService#registerRecipient(RecipientExec)}.
 */
public interface RecipientExec {

    /**
     * Return if the player has access. This command does not look for inheritance. If this is land relative, it will
     * return false.
     *
     * @param flag   the flag
     * @param entity the entity
     * @return true if the entity has access
     */
    boolean hasAccess(Flag flag, Entity entity);

    /**
     * Return if the player has access from a land.
     *
     * @param flag       the flag
     * @param entity     the entity
     * @param originLand the land where we are looking from
     * @return true if the entity has access
     */
    boolean hasAccess(Flag flag, Entity entity, Land originLand);
}
