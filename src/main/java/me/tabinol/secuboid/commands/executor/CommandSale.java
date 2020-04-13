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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.data.type.Sign;
import org.bukkit.command.CommandSender;

/**
 * The command sale class.
 *
 * @author tabinol
 */
@InfoCommand(name = "sale", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@number", "recreate" }) //
        })
public final class CommandSale extends CommandExec {

    /**
     * Instantiates a new command sale.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSale(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        // Economy activated in configuration?
        if (!secuboid.getConf().useEconomy()) {
            throw new SecuboidCommandException(secuboid, "Economy not available.", player,
                    "COMMAND.ECONOMY.NOTAVAILABLE");
        }

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        if (!playerConf.isAdminMode()) {
            // If the player not adminmode, he must be owner && permission true
            checkPermission(false, false, PermissionList.ECO_LAND_FOR_SALE.getPermissionType(), null);
        }

        final String curArg = argList.getNext();
        double salePrice;
        EcoSign ecoSign;

        // Check for sign in hand
        if (player.getGameMode() != GameMode.CREATIVE
                && !Sign.class.isAssignableFrom(player.getEquipment().getItemInMainHand().getType().data)) {
            throw new SecuboidCommandException(secuboid, "Must have a sign in hand", player,
                    "COMMAND.ECONOMY.MUSTHAVEISIGN");
        }

        // If 'recreate'
        if (curArg.equalsIgnoreCase("recreate")) {
            if (!landSelectNullable.isForSale()) {
                throw new SecuboidCommandException(secuboid, "The land is not for sale", player,
                        "COMMAND.ECONOMY.ERRORCREATESIGN");
            }
            try {
                ecoSign = new EcoSign(secuboid, landSelectNullable, player);
                ecoSign.createSignForSale(landSelectNullable.getSalePrice());
                removeSignFromHand();
                if (!ecoSign.getLocation().getBlock().equals(landSelectNullable.getSaleSignLoc().getBlock())) {
                    ecoSign.removeSign(landSelectNullable.getSaleSignLoc());
                    landSelectNullable.setSaleSignLoc(ecoSign.getLocation());
                }
            } catch (final SignException e) {
                throw new SecuboidCommandException(secuboid, "Error in the command", player,
                        "COMMAND.ECONOMY.ERRORCREATESIGN");
            }

            player.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));

            return;
        }

        // get price
        try {
            salePrice = Double.parseDouble(curArg);
        } catch (final NumberFormatException ex) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player, "GENERAL.MISSINGINFO");
        }

        // Land already for sale?
        if (landSelectNullable.isForSale()) {
            throw new SecuboidCommandException(secuboid, "Land already for sale", player,
                    "COMMAND.ECONOMY.ALREADYSALE");
        }

        // Create Sign
        try {
            ecoSign = new EcoSign(secuboid, landSelectNullable, player);
            ecoSign.createSignForSale(salePrice);
            removeSignFromHand();
        } catch (final SignException e) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player,
                    "COMMAND.ECONOMY.ERRORCREATESIGN");
        }
        landSelectNullable.setForSale(true, salePrice, ecoSign.getLocation());
        player.sendMessage(
                ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
    }
}
