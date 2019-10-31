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
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandMoney.
 */
@InfoCommand(name = "money", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "balance", "deposit", "withdraw" }) //
        })
public class CommandMoney extends CommandExec {

    /**
     * The player money.
     */
    private final PlayerMoney playerMoney;

    /**
     * Instantiates a new command money.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandMoney(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
        playerMoney = secuboid.getPlayerMoney();
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        if (playerMoney == null) {

            throw new SecuboidCommandException(secuboid, "Economy not avalaible", player,
                    "COMMAND.ECONOMY.NOTAVAILABLE");
        }

        checkSelections(true, null);

        String curArg = argList.getNext();

        if (curArg.equalsIgnoreCase("balance")) {
            balance();
        } else if (curArg.equalsIgnoreCase("deposit")) {
            deposit();
        } else if (curArg.equalsIgnoreCase("withdraw")) {
            withdraw();
        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", player, "GENERAL.MISSINGINFO");
        }
    }

    /**
     * Balance.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void balance() throws SecuboidCommandException {

        checkPermission(true, false, PermissionList.MONEY_BALANCE.getPermissionType(), null);
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage()
                .getMessage("COMMAND.ECONOMY.LANDBALANCE", land.getName(), playerMoney.toFormat(land.getMoney())));
    }

    /**
     * Deposit.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void deposit() throws SecuboidCommandException {

        checkPermission(true, false, PermissionList.MONEY_DEPOSIT.getPermissionType(), null);

        double amount = getAmountFromCommandLine();

        // Amount is valid?
        if (amount > playerMoney.getPlayerBalance(player.getPlayer(), land.getWorldName())) {
            throw new SecuboidCommandException(secuboid, "Invalid amount", player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        playerMoney.getFromPlayer(player.getPlayer(), land.getWorldName(), amount);
        land.addMoney(amount);
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage()
                .getMessage("COMMAND.ECONOMY.LANDDEPOSIT", playerMoney.toFormat(land.getMoney()), land.getName()));
    }

    /**
     * Withdraw.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void withdraw() throws SecuboidCommandException {

        checkPermission(true, false, PermissionList.MONEY_WITHDRAW.getPermissionType(), null);

        double amount = getAmountFromCommandLine();

        // Amount is valid?
        if (amount > land.getMoney()) {
            throw new SecuboidCommandException(secuboid, "Invalid amount", player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        land.subtractMoney(amount);
        playerMoney.giveToPlayer(player.getPlayer(), land.getWorldName(), amount);
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage()
                .getMessage("COMMAND.ECONOMY.LANDWITHDRAW", playerMoney.toFormat(land.getMoney()), land.getName()));
    }

    /**
     * Gets the amount from command line.
     *
     * @return the amount from command line
     * @throws SecuboidCommandException the secuboid command exception
     */
    private double getAmountFromCommandLine() throws SecuboidCommandException {

        double ret = 0;
        boolean err = false;

        try {
            ret = Double.parseDouble(argList.getNext());
            if (ret <= 0) {
                // Amount is 0 or less
                err = true;
            }
        } catch (NullPointerException ex) {
            // Amount is null
            err = true;
        } catch (NumberFormatException ex) {
            // Amount is unreadable
            err = true;
        }

        if (err) {
            throw new SecuboidCommandException(secuboid, "Invalid amount", player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        return ret;
    }
}
