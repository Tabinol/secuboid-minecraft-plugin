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

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.economy.EcoSign;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SignException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.parameters.PermissionList;

@InfoCommand(name="sale", forceParameter=true)
public class CommandSale extends CommandExec {

    public CommandSale(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {
        
        checkSelections(true, null);
        checkPermission(true, true, null, null);
        if(!entity.playerConf.isAdminMod()) {
            // If the player not adminmod, he must be owner && permission true
            checkPermission(false, false, PermissionList.ECO_LAND_FOR_SALE.getPermissionType(), null);
        }
        
        String curArg = entity.argList.getNext();
        double salePrice = 0;
        EcoSign ecoSign = null;
        
        // Check for sign in hand
        if(entity.player.getGameMode() != GameMode.CREATIVE && entity.player.getItemInHand().getType() != Material.SIGN) {
            throw new SecuboidCommandException("Must have a sign in hand", entity.player, "COMMAND.ECONOMY.MUSTHAVEISIGN");
        }
        
        // If 'recreate'
        if(curArg.equalsIgnoreCase("recreate")) {
            if(!land.isForSale()) {
                throw new SecuboidCommandException("The land is not for sale", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
            }
            try {
                ecoSign = new EcoSign(land, entity.player);
                ecoSign.createSignForSale(land.getSalePrice());
                removeSignFromHand();
                if(!ecoSign.getLocation().getBlock().equals(land.getSaleSignLoc().getBlock())) {
                    ecoSign.removeSign(land.getSaleSignLoc());
                    ((Land) land).setSaleSignLoc(ecoSign.getLocation());
                }
            } catch (SignException e) {
                throw new SecuboidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
            }
            
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));
            Secuboid.getThisPlugin().getLog().write("Sign recreated for land " + land.getName() + " by: " + entity.playerName);
            
            return;
        }
        
        // get price
        try {
            salePrice = Double.parseDouble(curArg);
        } catch (NumberFormatException ex) {
            throw new SecuboidCommandException("Error in the command", entity.player, "GENERAL.MISSINGINFO");
        }
        
        // Land already for sale?
        if(land.isForSale()) {
            throw new SecuboidCommandException("Land already for sale", entity.player, "COMMAND.ECONOMY.ALREADYSALE");
        }

        // Create Sign
        try {
            ecoSign = new EcoSign(land, entity.player);
            ecoSign.createSignForSale(salePrice);
            removeSignFromHand();
        } catch (SignException e) {
            throw new SecuboidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
        }
        ((Land) land).setForSale(true, salePrice, ecoSign.getLocation());
        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
        Secuboid.getThisPlugin().getLog().write("The land " + land.getName() + " is set to for sale by: " + entity.playerName);
    }


}
