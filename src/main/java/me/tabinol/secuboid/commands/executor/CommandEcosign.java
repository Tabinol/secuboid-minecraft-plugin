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
package me.tabinol.secuboid.commands.executor;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/**
 * Represents an action on an economy sign.
 */
public class CommandEcosign extends CommandExec {

    public enum SignType {

        SALE,
        RENT
    }

    private final Action action;
    private final SignType signType;

    /**
     * Click on economy sign.
     *
     * @param secuboid secuboid instance
     * @param player   the player
     * @param land     the land
     * @param action   the action
     * @param signType the sign type
     * @throws SecuboidCommandException wrong command execution
     */
    public CommandEcosign(Secuboid secuboid, Player player, RealLand land, Action action,
                          SignType signType) throws SecuboidCommandException {

        super(secuboid, null, player, null);
        this.land = land;
        this.action = action;
        this.signType = signType;
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (signType == SignType.SALE) {

                // Buy a land
                if (!land.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.ECO_LAND_BUY.getPermissionType())) {
                    throw new SecuboidCommandException(secuboid, "No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
                }
                if (secuboid.getPlayerMoney().getPlayerBalance(player, land.getWorldName()) < land.getSalePrice()) {
                    throw new SecuboidCommandException(secuboid, "Not enough money to buy a land", player, "COMMAND.ECONOMY.NOTENOUGHMONEY");
                }
                secuboid.getPlayerMoney().getFromPlayer(player, land.getWorldName(), land.getSalePrice());
                if (land.getOwner().getContainerType() == PlayerContainerType.PLAYER) {
                    secuboid.getPlayerMoney().giveToPlayer(((PlayerContainerPlayer) land.getOwner()).getOfflinePlayer(),
                            land.getWorldName(), land.getSalePrice());
                }
                try {
                    new EcoSign(secuboid, land, land.getSaleSignLoc()).removeSign();
                } catch (SignException e) {
                    // Real Error
                    secuboid.getLog().severe("Sign exception in location: " + land.getSaleSignLoc());
                }
                land.setForSale(false, 0, null);
                land.setOwner(playerConf.getPlayerContainer());
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.BUYLAND",
                        land.getName()));
            } else // Rent and unrent
                if (land.isRented() && (land.getTenant().hasAccess(player, land, land) || land.getOwner().hasAccess(player, land, land)
                        || playerConf.isAdminMode())) {

                    // Unrent
                    land.unSetRented();
                    try {
                        new EcoSign(secuboid, land, land.getRentSignLoc()).createSignForRent(land.getRentPrice(), land.getRentRenew(),
                                land.getRentAutoRenew(), null);
                    } catch (SignException e) {
                        // Real Error
                        secuboid.getLog().severe("Sign exception in location: " + land.getSaleSignLoc());
                    }
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.UNRENTLAND",
                            land.getName()));

                } else if (!land.isRented()) {

                    // Rent
                    if (!land.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.ECO_LAND_RENT.getPermissionType())) {
                        throw new SecuboidCommandException(secuboid, "No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (secuboid.getPlayerMoney().getPlayerBalance(player,
                            land.getWorldName()) < land.getRentPrice()) {
                        throw new SecuboidCommandException(secuboid, "Not enough money to rent a land", player, "COMMAND.ECONOMY.NOTENOUGHMONEY");
                    }
                    secuboid.getPlayerMoney().getFromPlayer(player,
                            land.getWorldName(), land.getRentPrice());
                    if (land.getOwner() instanceof PlayerContainerPlayer) {
                        secuboid.getPlayerMoney().giveToPlayer(((PlayerContainerPlayer) land.getOwner()).getOfflinePlayer(),
                                land.getWorldName(), land.getRentPrice());
                    }
                    land.setRented(playerConf.getPlayerContainer());
                    try {
                        new EcoSign(secuboid, land, land.getRentSignLoc()).createSignForRent(land.getRentPrice(),
                                land.getRentRenew(), land.getRentAutoRenew(), player.getName());
                    } catch (SignException e) {
                        // Real Error
                        secuboid.getLog().severe("Sign exception in location: " + land.getSaleSignLoc());
                    }
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.RENTLAND",
                            land.getName()));
                }
        } else if (land.getOwner().hasAccess(player, land, land) || playerConf.isAdminMode()) {

            // Left Click, destroy the sign
            if (signType == SignType.SALE) {

                // Destroy sale sign
                try {
                    new EcoSign(secuboid, land, land.getSaleSignLoc()).removeSign();
                } catch (SignException e) {
                    // Real Error
                    secuboid.getLog().severe("Sign exception in location: " + land.getSaleSignLoc());
                }
                land.setForSale(false, 0, null);
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.UNFORSALE",
                        land.getName()));
            } else {

                // Destroy rent sign
                try {
                    new EcoSign(secuboid, land, land.getRentSignLoc()).removeSign();
                } catch (SignException e) {
                    // Real Error
                    secuboid.getLog().severe("Sign exception in location: " + land.getSaleSignLoc());
                }
                land.unSetRented();
                land.unSetForRent();
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.UNFORRENT",
                        land.getName()));
            }
        }
    }
}
