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
package me.tabinol.secuboid.economy;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.events.LandEconomyEvent;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;

/**
 * Economy scheduler.
 */
public class EcoScheduler extends BukkitRunnable {

    private final Secuboid secuboid;

    public EcoScheduler(final Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    @Override
    public void run() {
        if (secuboid.getConf().useEconomy()) {
            runEcoTask();
        }
    }

    private void runEcoTask() {
        final PlayerMoney playerMoney = secuboid.getPlayerMoneyOpt().get();
        final PluginManager pm = secuboid.getServer().getPluginManager();
        final long now = System.currentTimeMillis();

        // Check for rent renew
        for (final Land land : secuboid.getLands().getForRent()) {

            final long nextPaymentTime = land.getLastPaymentTime() + (86400000L * land.getRentRenew());

            if (land.isRented() && nextPaymentTime < now) {
                final PlayerContainerPlayer tenant = land.getTenant();
                final OfflinePlayer offlineTenant = tenant.getOfflinePlayer();

                if (offlineTenant == null || !offlineTenant.hasPlayedBefore()) {
                    secuboid.getLogger()
                            .warning("Player " + tenant.getMinecraftUUID()
                                    + " not found in this server and cannot pay for the land " + land.getName()
                                    + ", UUID: " + land.getUUID() + ".");
                    continue;
                }

                // Check if the tenant has enough money or time limit whit no auto renew
                if (playerMoney.getPlayerBalance(offlineTenant, land.getWorldName()) < land.getRentPrice()
                        || !land.getRentAutoRenew()) {

                    // Unrent
                    land.unSetRented();
                    secuboid.getLogger().info(
                            offlineTenant.getName() + " lost land '" + land.getName() + "' rent. (Not enough money)");
                    try {
                        new EcoSign(secuboid, land, land.getRentSignLoc()).createSignForRent(land.getRentPrice(),
                                land.getRentRenew(), land.getRentAutoRenew(), null);
                    } catch (final SignException e) {
                        secuboid.getLogger().severe("Sign exception in location: " + land.getSaleSignLoc());
                    }
                    pm.callEvent(new LandEconomyEvent(land, LandEconomyEvent.LandEconomyReason.UNRENT, land.getOwner(),
                            tenant));
                } else {

                    // renew rent
                    playerMoney.getFromPlayer(offlineTenant, land.getWorldName(), land.getRentPrice());
                    if (offlineTenant.isOnline()) {
                        offlineTenant.getPlayer()
                                .sendMessage(ChatColor.YELLOW + "[Secuboid] "
                                        + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.LOCATIONGIVE",
                                                playerMoney.toFormat(land.getRentPrice()), land.getName()));
                    }
                    if (land.getOwner() instanceof PlayerContainerPlayer) {
                        final OfflinePlayer offlineOwner = ((PlayerContainerPlayer) land.getOwner()).getOfflinePlayer();
                        playerMoney.giveToPlayer(offlineOwner, land.getWorldName(), land.getRentPrice());
                        if (offlineOwner.isOnline()) {
                            offlineOwner.getPlayer()
                                    .sendMessage(ChatColor.YELLOW + "[Secuboid] "
                                            + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.LOCATIONRECEIVE",
                                                    playerMoney.toFormat(land.getRentPrice()), land.getName()));
                        }
                    }
                    secuboid.getLogger().info(offlineTenant.getName() + " gave "
                            + playerMoney.toFormat(land.getRentPrice()) + " for land '" + land.getName() + "'.");
                    land.setLastPaymentTime(now);
                    pm.callEvent(new LandEconomyEvent(land, LandEconomyEvent.LandEconomyReason.RENT_RENEW,
                            land.getOwner(), tenant));
                }
            }
        }
    }
}
