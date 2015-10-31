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
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboidapi.config.players.ApiPlayerConfEntry;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainerPlayer;
import me.tabinol.secuboid.lands.Land;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class CommandEcosign extends CommandExec {

    public enum SignType {
        SALE, RENT
    }

    /** The player. */
    private final Player player;

    /** The player conf. */
    private final ApiPlayerConfEntry playerConf;
    private final Action action;
    private final SignType signType;

    // Called from FlyCreativeListener (right or leftclick)
    public CommandEcosign(ApiPlayerConfEntry entry, ApiLand land, Action action,
            SignType signType) throws SecuboidCommandException {

        super(null);
        this.player = entry.getPlayer();
        playerConf = entry;
        this.land = land;
        this.action = action;
        this.signType = signType;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    public void commandExecute() throws SecuboidCommandException {

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (signType == SignType.SALE) {

                // Buy a land
                if (!land.checkPermissionAndInherit(player,
                        PermissionList.ECO_LAND_BUY.getPermissionType())) {
                    throw new SecuboidCommandException("No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
                }
                if (Secuboid.getThisPlugin().getPlayerMoney().getPlayerBalance(player,
                        land.getWorldName()) < land.getSalePrice()) {
                    throw new SecuboidCommandException("Not enough money to buy a land", player, "COMMAND.ECONOMY.NOTENOUGHMONEY");
                }
                Secuboid.getThisPlugin().getPlayerMoney().getFromPlayer(player,
                        land.getWorldName(), land.getSalePrice());
                if (land.getOwner() instanceof ApiPlayerContainerPlayer) {
                    Secuboid.getThisPlugin().getPlayerMoney()
                            .giveToPlayer(
                                    ((ApiPlayerContainerPlayer) land.getOwner())
                                            .getOfflinePlayer(),
                                    land.getWorldName(), land.getSalePrice());
                }
                try {
                    new EcoSign(land, land.getSaleSignLoc()).removeSign();
                } catch (SignException e) {
                    // Real Error
                    e.printStackTrace();
                }
                ((Land) land).setForSale(false, 0, null);
                land.setOwner(playerConf.getPlayerContainer());
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.BUYLAND",
                        land.getName()));
                Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is purchased by : " + player.getName());
            } else {

                // Rent and unrent
                if (land.isRented()
                        && (land.getTenant().hasAccess(player) || land.getOwner().hasAccess(player)
                                || playerConf.isAdminMod())) {

                    // Unrent
                    ((Land) land).unSetRented();
                    try {
                        new EcoSign(land, land.getRentSignLoc()).createSignForRent(
                                land.getRentPrice(), land.getRentRenew(),
                                land.getRentAutoRenew(), null);
                    } catch (SignException e) {
                        // Real Error
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.UNRENTLAND",
                            land.getName()));
                    Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is unrented by : " + player.getName());
                
                } else if (!land.isRented()) {

                    // Rent
                    if (!land.checkPermissionAndInherit(player,
                            PermissionList.ECO_LAND_RENT.getPermissionType())) {
                        throw new SecuboidCommandException("No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (Secuboid.getThisPlugin().getPlayerMoney().getPlayerBalance(player,
                            land.getWorldName()) < land.getRentPrice()) {
                        throw new SecuboidCommandException("Not enough money to rent a land", player, "COMMAND.ECONOMY.NOTENOUGHMONEY");
                    }
                    Secuboid.getThisPlugin().getPlayerMoney().getFromPlayer(player,
                            land.getWorldName(), land.getRentPrice());
                    if (land.getOwner() instanceof ApiPlayerContainerPlayer) {
                        Secuboid.getThisPlugin().getPlayerMoney()
                                .giveToPlayer(
                                        ((ApiPlayerContainerPlayer) land
                                                .getOwner()).getOfflinePlayer(),
                                        land.getWorldName(),
                                        land.getRentPrice());
                    }
                    ((Land) land).setRented(playerConf.getPlayerContainer());
                    try {
                        new EcoSign(land, land.getRentSignLoc()).createSignForRent(
                                land.getRentPrice(), land.getRentRenew(),
                                land.getRentAutoRenew(), player.getName());
                    } catch (SignException e) {
                        // Real Error
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.RENTLAND",
                            land.getName()));
                    Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is rented by : " + player.getName());
                }
            }
        } else {

            // Left Click, destroy the sign
            if (land.getOwner().hasAccess(player) || playerConf.isAdminMod()) {
                
                if (signType == SignType.SALE) {

                    // Destroy sale sign
                    try {
                        new EcoSign(land, land.getSaleSignLoc()).removeSign();
                    } catch (SignException e) {
                        // Real Error
                        e.printStackTrace();
                    }
                    ((Land) land).setForSale(false, 0, null);
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.UNFORSALE",
                            land.getName()));
                    Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is no longer for sale by : " + player.getName());
                } else {

                    // Destroy rent sign
                    try {
                        new EcoSign(land, land.getRentSignLoc()).removeSign();
                    } catch (SignException e) {
                        // Real Error
                        e.printStackTrace();
                    }
                    ((Land) land).unSetRented();
                    ((Land) land).unSetForRent();
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.UNFORRENT",
                            land.getName()));
                    Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is no longer for rent by : " + player.getName());
                }
            }
        }
    }
}
