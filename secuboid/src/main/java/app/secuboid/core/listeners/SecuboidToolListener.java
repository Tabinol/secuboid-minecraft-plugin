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
package app.secuboid.core.listeners;

import app.secuboid.api.players.PlayerInfo;
import app.secuboid.api.players.PlayerInfoService;
import app.secuboid.api.selection.PlayerSelection;
import app.secuboid.core.items.SecuboidToolService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static org.bukkit.event.EventPriority.NORMAL;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class SecuboidToolListener implements Listener {

    private final PlayerInfoService playerInfoService;
    private final SecuboidToolService secuboidToolService;

    public SecuboidToolListener(PlayerInfoService playerInfoService, SecuboidToolService secuboidToolService) {
        this.playerInfoService = playerInfoService;
        this.secuboidToolService = secuboidToolService;
    }

    @EventHandler(priority = NORMAL)
    public void onPlayerInteractNormal(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (!isSecuboidTool(itemStack)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerInfo playerInfo = playerInfoService.getPlayerInfo(player);
        PlayerSelection playerSelection = playerInfo.getPlayerSelection();
        Action action = event.getAction();

        if (action == LEFT_CLICK_BLOCK) {
            leftClick(playerInfo, playerSelection);
        } else if (action == RIGHT_CLICK_BLOCK) {
            rightClick(playerInfo, playerSelection);
        }
    }

    @EventHandler(priority = NORMAL)
    public void onEntityDamageByEntityEventNormal(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();

        if (entity instanceof Player player) {

            PlayerInventory inventory = player.getInventory();

            ItemStack itemStack = inventory.getItemInMainHand();

            if (isSecuboidTool(itemStack)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isSecuboidTool(ItemStack itemStack) {
        return secuboidToolService.isSecuboidTool(itemStack);
    }

    private void leftClick(PlayerInfo playerInfo, PlayerSelection playerSelection) {
        if (!playerSelection.hasSelection()) {
            // todo select here
        }
    }

    private void rightClick(PlayerInfo playerInfo, PlayerSelection playerSelection) {

    }
}
