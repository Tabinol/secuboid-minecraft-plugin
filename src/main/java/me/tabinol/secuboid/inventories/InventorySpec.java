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

package me.tabinol.secuboid.inventories;

import java.util.List;

/**
 * The class for inventory specifications.
 */
public class InventorySpec {

    private final String inventoryName;
    private final boolean isCreativeChange;
    private final boolean isSaveInventory;
    private final boolean isAllowDrop;
    private final List<String> disabledCommands;

    public InventorySpec(final String inventoryName, final boolean isCreativeChange, final boolean isSaveInventory,
            final boolean isAllowDrop, final List<String> disabledCommands) {

        this.inventoryName = inventoryName;
        this.isCreativeChange = isCreativeChange;
        this.isSaveInventory = isSaveInventory;
        this.isAllowDrop = isAllowDrop;
        this.disabledCommands = disabledCommands;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public boolean isCreativeChange() {
        return isCreativeChange;
    }

    public boolean isSaveInventory() {
        return isSaveInventory;
    }

    public boolean isAllowDrop() {
        return isAllowDrop;
    }

    public boolean isDisabledCommand(final String command) {

        // We have to check for no cas sensitive
        if (disabledCommands != null) {
            for (final String cItem : disabledCommands) {
                if (cItem.equalsIgnoreCase(command)) {
                    return true;
                }
            }
        }

        return false;
    }
}
