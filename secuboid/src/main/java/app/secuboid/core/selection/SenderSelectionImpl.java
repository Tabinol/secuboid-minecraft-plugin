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
package app.secuboid.core.selection;

import app.secuboid.api.selection.SenderSelection;
import app.secuboid.api.selection.active.ActiveSelection;
import app.secuboid.api.selection.active.ActiveSelectionNothing;
import app.secuboid.core.selection.active.ActiveSelectionImpl;
import app.secuboid.core.selection.active.ActiveSelectionNothingImpl;
import org.bukkit.command.CommandSender;

public class SenderSelectionImpl implements SenderSelection {

    protected final CommandSender commandSender;
    protected ActiveSelection activeSelection;

    public SenderSelectionImpl(CommandSender commandSender) {
        this.commandSender = commandSender;
        activeSelection = new ActiveSelectionNothingImpl(commandSender);
    }

    @Override
    public final ActiveSelection getActiveSelection() {
        return activeSelection;
    }

    @Override
    public final boolean hasSelection() {
        return !(activeSelection instanceof ActiveSelectionNothing);
    }

    @Override
    public boolean removeSelection() {
        if (hasSelection()) {
            ((ActiveSelectionImpl) activeSelection).removeSelection();
            activeSelection = new ActiveSelectionNothingImpl(commandSender);
            return true;
        }

        return false;
    }
}
