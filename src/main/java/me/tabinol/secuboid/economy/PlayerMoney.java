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
package me.tabinol.secuboid.economy;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;

/**
 * Money from players.
 *
 * @author Tabinol
 */
public class PlayerMoney {

    /**
     * The economy.
     */
    private final Economy economy;

    /**
     * Instantiates a new player money.
     *
     * @param economy the secuboid economy instance
     */
    public PlayerMoney(final Economy economy) {
        this.economy = economy;
    }

    /**
     * Gets the player balance.
     *
     * @param offlinePlayer the offline player
     * @param worldName     the world name
     * @return the player balance
     */
    public Double getPlayerBalance(final OfflinePlayer offlinePlayer, final String worldName) {
        return economy.getBalance(offlinePlayer, worldName);
    }

    /**
     * Give to player.
     *
     * @param offlinePlayer the offline player
     * @param worldName     the world name
     * @param amount        the amount
     * @return true, if successful
     */
    public boolean giveToPlayer(final OfflinePlayer offlinePlayer, final String worldName, final Double amount) {
        return economy.depositPlayer(offlinePlayer, worldName, amount).transactionSuccess();
    }

    /**
     * Gets the from player.
     *
     * @param offlinePlayer the offline player
     * @param worldName     the world name
     * @param amount        the amount
     * @return true, if successful
     */
    public boolean getFromPlayer(final OfflinePlayer offlinePlayer, final String worldName, final Double amount) {
        return economy.withdrawPlayer(offlinePlayer, worldName, amount).transactionSuccess();
    }

    /**
     * To format.
     *
     * @param amount the amount
     * @return the string
     */
    public String toFormat(final Double amount) {
        return economy.format(amount);
    }
}
