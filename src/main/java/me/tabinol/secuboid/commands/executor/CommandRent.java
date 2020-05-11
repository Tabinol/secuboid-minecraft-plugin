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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.data.type.Sign;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.LandLocation;
import me.tabinol.secuboid.permissionsflags.PermissionList;

/**
 * The rent command class.
 */
@InfoCommand(name = "rent", forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "@number", "recreate" }), //
                @CompletionMap(regex = "^([^\\s]+)$", completions = { "@number" }), //
                @CompletionMap(regex = "^([^\\s]+) ([^\\s]+)$", completions = { "@boolean" }) //
        })
public final class CommandRent extends CommandExec {

    /**
     * Instantiates a rent command.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandRent(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
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
            checkPermission(false, false, PermissionList.ECO_LAND_FOR_RENT.getPermissionType(), null);
        }

        String curArg = argList.getNext();
        double rentPrice;
        int rentRenew;
        boolean rentAutoRenew = true;
        EcoSign ecoSign;

        // Check for sign in hand
        if (player.getGameMode() != GameMode.CREATIVE
                && !Sign.class.isAssignableFrom(player.getEquipment().getItemInMainHand().getType().data)) {
            throw new SecuboidCommandException(secuboid, "Must have a sign in hand", player,
                    "COMMAND.ECONOMY.MUSTHAVEISIGN");
        }

        // If 'recreate'
        if (curArg.equalsIgnoreCase("recreate")) {
            if (!landSelectNullable.isForRent()) {
                throw new SecuboidCommandException(secuboid, "The land is not for rent", player,
                        "COMMAND.ECONOMY.ERRORCREATESIGN");
            }
            try {
                ecoSign = new EcoSign(secuboid, landSelectNullable, player);
                ecoSign.createSignForRent(landSelectNullable.getRentPrice(), landSelectNullable.getRentRenew(),
                        landSelectNullable.getRentAutoRenew(),
                        landSelectNullable.isRented() ? landSelectNullable.getTenant().getPlayerName() : null); // Tenant
                                                                                                                // name
                                                                                                                // if
                                                                                                                // the
                                                                                                                // land
                                                                                                                // is
                                                                                                                // rented
                removeSignFromHand();
                final Location location = landSelectNullable.getRentSignLoc().toLocation();
                if (!ecoSign.getLocation().getBlock().equals(location.getBlock())) {
                    ecoSign.removeSign(location);
                    landSelectNullable.setRentSignLoc(LandLocation.fromLocation(ecoSign.getLocation()));
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
            rentPrice = Double.parseDouble(curArg);
        } catch (final NumberFormatException ex) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player, "GENERAL.MISSINGINFO");
        }

        // get renew
        curArg = argList.getNext();
        try {
            rentRenew = Integer.parseInt(curArg);
        } catch (final NumberFormatException ex) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player, "GENERAL.MISSINGINFO");
        }

        // get auto renew
        curArg = argList.getNext();
        if (curArg != null) {
            try {
                rentAutoRenew = Boolean.parseBoolean(curArg);
            } catch (final NumberFormatException ex) {
                // Default value
                rentAutoRenew = true;
            }
        }

        // Land already for rent?
        if (landSelectNullable.isForRent()) {
            throw new SecuboidCommandException(secuboid, "Land already for rent", player,
                    "COMMAND.ECONOMY.ALREADYRENT");
        }

        // Create Sign
        try {
            ecoSign = new EcoSign(secuboid, landSelectNullable, player);
            ecoSign.createSignForRent(rentPrice, rentRenew, rentAutoRenew, null);
            removeSignFromHand();
        } catch (final SignException e) {
            throw new SecuboidCommandException(secuboid, "Error in the command", player,
                    "COMMAND.ECONOMY.ERRORCREATESIGN");
        }
        landSelectNullable.setForRent(rentPrice, rentRenew, rentAutoRenew,
                LandLocation.fromLocation(ecoSign.getLocation()));
        player.sendMessage(
                ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
    }
}
