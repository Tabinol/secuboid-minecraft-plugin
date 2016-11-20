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
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 * The command sale class.
 *
 * @author tabinol
 */
@InfoCommand(name = "sale", forceParameter = true)
public class CommandSale extends CommandExec {

    /**
     * Instantiates a new command sale.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSale(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        if (!playerConf.isAdminMode()) {
            // If the player not adminmode, he must be owner && permission true
            checkPermission(false, false, PermissionList.ECO_LAND_FOR_SALE.getPermissionType(), null);
        }

        String curArg = argList.getNext();
        double salePrice = 0;
        EcoSign ecoSign = null;

        // Check for sign in hand
        if (player.getGameMode() != GameMode.CREATIVE && player.getItemInHand().getType() != Material.SIGN) {
            throw new SecuboidCommandException(secuboid, "Must have a sign in hand", player, "COMMAND.ECONOMY.MUSTHAVEISIGN");
        }

        // If 'recreate'
        if (curArg.equalsIgnoreCase("recreate")) {
            if (!land.isForSale()) {
                throw new SecuboidCommandException(secuboid, "The land is not for sale", player, "COMMAND.ECONOMY.ERRORCREATESIGN");
            }
            try {
                ecoSign = new EcoSign(secuboid, land, player);
                ecoSign.createSignForSale(land.getSalePrice());
                removeSignFromHand();
                if (!ecoSign.getLocation().getBlock().equals(land.getSaleSignLoc().getBlock())) {
                    ecoSign.removeSign(land.getSaleSignLoc());
                    land.setSaleSignLoc(ecoSign.getLocation());
                }
            } catch (SignException e) {
                throw new SecuboidCommandException(secuboid, "Error in the command", player, "COMMAND.ECONOMY.ERRORCREATESIGN");
            }

            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));
            secuboid.getLog().debug("Sign recreated for land " + land.getName() + " by: " + playerName);

            return;
        }

        // get price
        try {
            salePrice = Double.parseDouble(curArg);
        } catch (NumberFormatException ex) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player, "GENERAL.MISSINGINFO");
        }

        // Land already for sale?
        if (land.isForSale()) {
            throw new SecuboidCommandException(secuboid, "Land already for sale", player, "COMMAND.ECONOMY.ALREADYSALE");
        }

        // Create Sign
        try {
            ecoSign = new EcoSign(secuboid, land, player);
            ecoSign.createSignForSale(salePrice);
            removeSignFromHand();
        } catch (SignException e) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player, "COMMAND.ECONOMY.ERRORCREATESIGN");
        }
        land.setForSale(true, salePrice, ecoSign.getLocation());
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
        secuboid.getLog().debug("The land " + land.getName() + " is set to for sale by: " + playerName);
    }
}
