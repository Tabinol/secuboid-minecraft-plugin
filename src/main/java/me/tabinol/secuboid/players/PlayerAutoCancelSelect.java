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
package me.tabinol.secuboid.players;

import java.util.logging.Level;

import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandCancel;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.utilities.SecuboidRunnable;

/**
 * The Class PlayerAutoCancelSelect. Auto cancel selection.
 */
class PlayerAutoCancelSelect extends SecuboidRunnable {

    /**
     * The entry.
     */
    private final PlayerConfEntry entry;

    /**
     * Instantiates a new player auto cancel select.
     *
     * @param secuboid the secuboid instance
     * @param entry    the entry
     */
    PlayerAutoCancelSelect(final Secuboid secuboid, final PlayerConfEntry entry) {

        super(secuboid);
        this.entry = entry;
    }

    @Override
    public void run() {

        setOneTimeDone();
        try {
            new CommandCancel(secuboid, null, entry.getSender(), null).commandExecute();
        } catch (final SecuboidCommandException ex) {
            secuboid.getLogger().log(Level.SEVERE, "Error in select cancel command", ex);
            ex.notifySender();
        }
    }
}
