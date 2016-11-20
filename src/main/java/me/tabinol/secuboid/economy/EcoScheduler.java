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

import java.sql.Timestamp;
import java.util.Calendar;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Economy scheduler.
 */
public class EcoScheduler extends BukkitRunnable {

    private final Secuboid secuboid;

    public EcoScheduler(Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    @Override
    public void run() {

        Calendar now = Calendar.getInstance();

        // Check for rent renew
        for (RealLand land : secuboid.getLands().getForRent()) {

            long nextPaymentTime = land.getLastPaymentTime().getTime() + (86400000 * land.getRentRenew());

            if (land.isRented() && nextPaymentTime < now.getTimeInMillis()) {

                //Check if the tenant has enough money or time limit whit no auto renew
                if (secuboid.getPlayerMoney().getPlayerBalance(land.getTenant().getOfflinePlayer(), land.getWorldName()) < land.getRentPrice()
                        || !land.getRentAutoRenew()) {

                    // Unrent
                    land.unSetRented();
                    try {
                        new EcoSign(secuboid, land, land.getRentSignLoc()).createSignForRent(
                                land.getRentPrice(), land.getRentRenew(),
                                land.getRentAutoRenew(), null);
                    } catch (SignException e) {
                        secuboid.getLog().severe("Sign exception in location: " + land.getSaleSignLoc());
                    }
                } else {

                    // renew rent
                    secuboid.getPlayerMoney().getFromPlayer(land.getTenant().getOfflinePlayer(),
                            land.getWorldName(), land.getRentPrice());
                    if (land.getOwner() instanceof PlayerContainerPlayer) {
                        secuboid.getPlayerMoney().giveToPlayer(((PlayerContainerPlayer) land.getOwner()).getOfflinePlayer(),
                                land.getWorldName(), land.getRentPrice());
                    }
                    land.setLastPaymentTime(new Timestamp(now.getTime().getTime()));
                }
            }
        }
    }
}
