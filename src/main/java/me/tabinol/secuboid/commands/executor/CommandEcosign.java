/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.events.LandEconomyEvent;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;

/**
 * Represents an action on an economy sign.
 */
public final class CommandEcosign extends CommandExec {

    public enum SignType {
        SALE, RENT
    }

    private final Action action;
    private final SignType signType;
    private final PlayerMoney playerMoney;

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
    public CommandEcosign(final Secuboid secuboid, final Player player, final Land land, final Action action,
            final SignType signType) throws SecuboidCommandException {

        super(secuboid, null, player, null);
        this.landSelectNullable = land;
        this.action = action;
        this.signType = signType;
        playerMoney = secuboid.getPlayerMoneyOpt().orElse(null);
    }

    @Override
    public final void commandExecute() throws SecuboidCommandException {

        final PluginManager pm = secuboid.getServer().getPluginManager();

        // Economy activated in configuration?
        if (playerMoney == null) {
            throw new SecuboidCommandException(secuboid, "Economy not available.", player,
                    "COMMAND.ECONOMY.NOTAVAILABLE");
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (signType == SignType.SALE) {

                // Buy a land
                if (!landSelectNullable.getPermissionsFlags().checkPermissionAndInherit(player,
                        PermissionList.ECO_LAND_BUY.getPermissionType())) {
                    throw new SecuboidCommandException(secuboid, "No permission to do this action", player,
                            "GENERAL.MISSINGPERMISSION");
                }
                if (playerMoney.getPlayerBalance(player, landSelectNullable.getWorldName()) < landSelectNullable
                        .getSalePrice()) {
                    throw new SecuboidCommandException(secuboid, "Not enough money to buy a land", player,
                            "COMMAND.ECONOMY.NOTENOUGHMONEY");
                }
                playerMoney.getFromPlayer(player, landSelectNullable.getWorldName(), landSelectNullable.getSalePrice());
                if (landSelectNullable.getOwner().getContainerType() == PlayerContainerType.PLAYER) {
                    final OfflinePlayer offlineOwner = ((PlayerContainerPlayer) landSelectNullable.getOwner())
                            .getOfflinePlayer();
                    playerMoney.giveToPlayer(offlineOwner, landSelectNullable.getWorldName(),
                            landSelectNullable.getRentPrice());
                    if (offlineOwner.isOnline()) {
                        offlineOwner.getPlayer()
                                .sendMessage(ChatColor.YELLOW + "[Secuboid] "
                                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.SALERECEIVE",
                                                String.valueOf(landSelectNullable.getRentPrice()),
                                                landSelectNullable.getName()));
                    }
                }
                try {
                    new EcoSign(secuboid, landSelectNullable, landSelectNullable.getSaleSignLoc()).removeSign();
                } catch (final SignException e) {
                    // Real Error
                    secuboid.getLogger().severe("Sign exception in location: " + landSelectNullable.getSaleSignLoc());
                }
                landSelectNullable.setForSale(false, 0, null);
                final PlayerContainer oldOwner = landSelectNullable.getOwner();
                landSelectNullable.setOwner(playerConf.getPlayerContainer());
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.BUYLAND", landSelectNullable.getName()));
                secuboid.getLogger()
                        .info(player.getName() + " gave " + playerMoney.toFormat(landSelectNullable.getRentPrice())
                                + " for land '" + landSelectNullable.getName() + "'.");
                pm.callEvent(new LandEconomyEvent(landSelectNullable, LandEconomyEvent.LandEconomyReason.SELL, oldOwner,
                        playerConf.getPlayerContainer()));
            } else // Rent and unrent
            if (landSelectNullable.isRented() && (landSelectNullable.isTenant(player)
                    || landSelectNullable.isOwner(player) || playerConf.isAdminMode())) {

                // Unrent
                landSelectNullable.unSetRented();
                try {
                    new EcoSign(secuboid, landSelectNullable, landSelectNullable.getRentSignLoc()).createSignForRent(
                            landSelectNullable.getRentPrice(), landSelectNullable.getRentRenew(),
                            landSelectNullable.getRentAutoRenew(), null);
                } catch (final SignException e) {
                    // Real Error
                    secuboid.getLogger().severe("Sign exception in location: " + landSelectNullable.getSaleSignLoc());
                }
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage()
                        .getMessage("COMMAND.ECONOMY.UNRENTLAND", landSelectNullable.getName()));
                pm.callEvent(new LandEconomyEvent(landSelectNullable, LandEconomyEvent.LandEconomyReason.UNRENT,
                        landSelectNullable.getOwner(), playerConf.getPlayerContainer()));

            } else if (!landSelectNullable.isRented()) {

                // Rent
                if (!landSelectNullable.getPermissionsFlags().checkPermissionAndInherit(player,
                        PermissionList.ECO_LAND_RENT.getPermissionType())) {
                    throw new SecuboidCommandException(secuboid, "No permission to do this action", player,
                            "GENERAL.MISSINGPERMISSION");
                }
                if (playerMoney.getPlayerBalance(player, landSelectNullable.getWorldName()) < landSelectNullable
                        .getRentPrice()) {
                    throw new SecuboidCommandException(secuboid, "Not enough money to rent a land", player,
                            "COMMAND.ECONOMY.NOTENOUGHMONEY");
                }
                playerMoney.getFromPlayer(player, landSelectNullable.getWorldName(), landSelectNullable.getRentPrice());
                if (landSelectNullable.getOwner() instanceof PlayerContainerPlayer) {
                    final OfflinePlayer offlineOwner = ((PlayerContainerPlayer) landSelectNullable.getOwner())
                            .getOfflinePlayer();
                    playerMoney.giveToPlayer(offlineOwner, landSelectNullable.getWorldName(),
                            landSelectNullable.getRentPrice());
                    if (offlineOwner.isOnline()) {
                        offlineOwner.getPlayer()
                                .sendMessage(ChatColor.YELLOW + "[Secuboid] "
                                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.LOCATIONRECEIVE",
                                                playerMoney.toFormat(landSelectNullable.getRentPrice()),
                                                landSelectNullable.getName()));
                    }
                }
                landSelectNullable.setRented(playerConf.getPlayerContainer());
                try {
                    new EcoSign(secuboid, landSelectNullable, landSelectNullable.getRentSignLoc()).createSignForRent(
                            landSelectNullable.getRentPrice(), landSelectNullable.getRentRenew(),
                            landSelectNullable.getRentAutoRenew(), player.getName());
                } catch (final SignException e) {
                    // Real Error
                    secuboid.getLogger().severe("Sign exception in location: " + landSelectNullable.getSaleSignLoc());
                }
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.RENTLAND", landSelectNullable.getName()));
                secuboid.getLogger()
                        .info(player.getName() + " gave " + playerMoney.toFormat(landSelectNullable.getRentPrice())
                                + " for land '" + landSelectNullable.getName() + "'.");
                pm.callEvent(new LandEconomyEvent(landSelectNullable, LandEconomyEvent.LandEconomyReason.RENT,
                        landSelectNullable.getOwner(), playerConf.getPlayerContainer()));
            }
        } else if (landSelectNullable.isOwner(player) || playerConf.isAdminMode()) {

            // Left Click, destroy the sign
            if (signType == SignType.SALE) {

                // Destroy sale sign
                try {
                    new EcoSign(secuboid, landSelectNullable, landSelectNullable.getSaleSignLoc()).removeSign();
                } catch (final SignException e) {
                    // Real Error
                    secuboid.getLogger().severe("Sign exception in location: " + landSelectNullable.getSaleSignLoc());
                }
                landSelectNullable.setForSale(false, 0, null);
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.UNFORSALE", landSelectNullable.getName()));
            } else {

                // Destroy rent sign
                try {
                    new EcoSign(secuboid, landSelectNullable, landSelectNullable.getRentSignLoc()).removeSign();
                } catch (final SignException e) {
                    // Real Error
                    secuboid.getLogger().severe("Sign exception in location: " + landSelectNullable.getSaleSignLoc());
                }
                final boolean wasRented = landSelectNullable.isRented();
                PlayerContainer tenant = null;
                if (wasRented) {
                    tenant = landSelectNullable.getTenant();
                }
                landSelectNullable.unSetRented();
                landSelectNullable.unSetForRent();
                player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.UNFORRENT", landSelectNullable.getName()));
                if (wasRented) {
                    pm.callEvent(new LandEconomyEvent(landSelectNullable, LandEconomyEvent.LandEconomyReason.UNRENT,
                            landSelectNullable.getOwner(), tenant));
                }
            }
        }
    }
}
