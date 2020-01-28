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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.lands.LandPermissionsFlags;

/**
 * The inventory storage class.
 */
public final class InventoryStorage {

    private final Secuboid secuboid;


    public void switchInventory(final Player player, final LandPermissionsFlags landPermissionsFlags,
            boolean toIsCreative, final PlayerAction playerAction) {

        PlayerInvEntry invEntry = null;
        boolean fromIsCreative = false;
        InventorySpec fromInv = null;
        InventorySpec toInv;

        // Check last values
        if (playerAction != PlayerAction.JOIN) {
            invEntry = playerInvList.get(player);
        }

        // invEntry is null if the player is new
        if (invEntry != null) {
            fromIsCreative = invEntry.isCreativeInv();
            fromInv = invEntry.getInventorySpec();
        }

        // Get new inventory
        toInv = secuboid.getInventoryConf().getInvSpec(landPermissionsFlags);

        // check if we have to do this action
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_INV)) {
            return;
        }

        // Force survival value if we do not change to creative inventory
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV)
                || (fromInv != null && !fromInv.isCreativeChange())) {
            fromIsCreative = false;
        }
        if (player.hasPermission(InventoryConfig.PERM_IGNORE_CREATIVE_INV) || !toInv.isCreativeChange()) {
            toIsCreative = false;
        }

        // Update player inventory information
        if (playerAction != PlayerAction.QUIT) {
            // TODO Resolve and Uncomment
            //playerInvList.put(player,
            //        new PlayerInvEntry(Optional.of(player), toInv, toIsCreative));
        }

        // Return if the inventory will be exacly the same
        if (playerAction != PlayerAction.DEATH && playerAction != PlayerAction.QUIT && (fromInv != null
                && fromInv.getInventoryName().equals(toInv.getInventoryName()) && fromIsCreative == toIsCreative)) {
            return;
        }

        // If the player is death, save a renamed file
        if (playerAction == PlayerAction.DEATH && fromInv != null) {
            saveInventory(player, fromInv.getInventoryName(), fromIsCreative, true, fromInv.isSaveInventory(), false,
                    false);
        }

        // Save last inventory (only EnderChest if death)
        if (playerAction != PlayerAction.JOIN && fromInv != null) {
            saveInventory(player, fromInv.getInventoryName(), fromIsCreative, false, fromInv.isSaveInventory(), false,
                    playerAction == PlayerAction.DEATH);
        }

        // Don't load a new inventory if the player quit
        if (playerAction != PlayerAction.QUIT && playerAction != PlayerAction.DEATH) {
            loadInventory(player, toInv.getInventoryName(), toIsCreative, false, 0);
        }

        // If the player quit, update Offline Inventories and remove player
        if (playerAction == PlayerAction.QUIT) {
            playerInvList.remove(player);
        }
    }

    public PlayerInvEntry getPlayerInvEntry(final Player player) {
        return playerInvList.get(player);
    }
}
