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
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.economy.PlayerMoney;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.parameters.PermissionList;

import org.bukkit.ChatColor;


/**
 * The Class CommandMoney.
 */
@InfoCommand(name="money", forceParameter=true)
public class CommandMoney extends CommandExec {

    /** The player money. */
    private final PlayerMoney playerMoney;

    /**
     * Instantiates a new command money.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandMoney(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
        playerMoney = Secuboid.getThisPlugin().getPlayerMoney();
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    public void commandExecute() throws SecuboidCommandException {

        if (playerMoney == null) {

            throw new SecuboidCommandException("Economy not avalaible", entity.player, "COMMAND.ECONOMY.NOTAVAILABLE");
        }

        checkSelections(true, null);

        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("balance")) {
            balance();
        } else if (curArg.equalsIgnoreCase("deposit")) {
            deposit();
        } else if (curArg.equalsIgnoreCase("withdraw")) {
            withdraw();
        } else {
            throw new SecuboidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    /**
     * Balance.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void balance() throws SecuboidCommandException {

        checkPermission(true, false, PermissionList.MONEY_BALANCE.getPermissionType(), null);
        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.LANDBALANCE",
                land.getName(), playerMoney.toFormat(land.getMoney())));
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
        if (amount > playerMoney.getPlayerBalance(entity.player.getPlayer(), land.getWorldName())) {
            throw new SecuboidCommandException("Invalid amount", entity.player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        playerMoney.getFromPlayer(entity.player.getPlayer(), land.getWorldName(), amount);
        land.addMoney(amount);
        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.LANDDEPOSIT",
                playerMoney.toFormat(land.getMoney()), land.getName()));
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
            throw new SecuboidCommandException("Invalid amount", entity.player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        land.substractMoney(amount);
        playerMoney.giveToPlayer(entity.player.getPlayer(), land.getWorldName(), amount);
        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.LANDWITHDRAW",
                playerMoney.toFormat(land.getMoney()), land.getName()));
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
            ret = Double.parseDouble(entity.argList.getNext());
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
            throw new SecuboidCommandException("Invalid amount", entity.player, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        return ret;
    }
}
