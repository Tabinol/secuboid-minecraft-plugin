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

import java.io.File;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.InventoryConfig;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.inventories.InventorySpec;
import me.tabinol.secuboid.inventories.InventoryStorage;
import me.tabinol.secuboid.parameters.PermissionList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@InfoCommand(name="inv", aliases={"inventory"}, allowConsole=true, forceParameter=true)
public class CommandInv extends CommandExec {

    public CommandInv(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        // Verify if Multiple inventories is active
        if(Secuboid.getThisPlugin().getInventoryConf() == null) {
            throw new SecuboidCommandException("Multiple Inventories is not active", entity.player, "COMMAND.INV.NOTACTIVE");
        }

        checkPermission(false, false, PermissionList.RESIDENT_MANAGER.getPermissionType(), null);

        String function = entity.argList.getNext();

        if (function.equalsIgnoreCase("default")) {
            checkPermission(false, false, null, InventoryConfig.PERM_DEFAULT);
            saveDefault();
        } else if (function.equalsIgnoreCase("loaddeath")) {
            checkPermission(false, false, null, InventoryConfig.PERM_LOADDEATH);
            loadDeath();
        } else if (function.equalsIgnoreCase("forcesave")) {
            checkPermission(false, false, null, InventoryConfig.PERM_FORCESAVE);
            forceSave();
        } else {
            throw new SecuboidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }

    private void saveDefault() throws SecuboidCommandException {

        if (entity.player == null) {
            throw new SecuboidCommandException("Impossible to do from console", Bukkit.getConsoleSender(), "CONSOLE");
        }
        
        // Get the land name
        InventorySpec invSpec = Secuboid.getThisPlugin().getInventoryListener().getPlayerInvEntry(entity.player).getActualInv();
        String subCom = entity.argList.getNext();

        if(subCom != null && subCom.equalsIgnoreCase("save")) {

            // Save the inventory
            Secuboid.getThisPlugin().getInventoryListener().saveDefaultInventory(entity.player, invSpec);
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.INV.DEFAULTSAVE"));
            
        } else if(subCom != null && subCom.equalsIgnoreCase("remove")) {
            
            // Remove inventory
            new File(Secuboid.getThisPlugin().getDataFolder()
                    + "/" + InventoryStorage.INV_DIR + "/" + invSpec.getInventoryName() + "/" + InventoryStorage.DEFAULT_INV + ".yml").delete();
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.INV.DEFAULTREMOVE"));
        
        } else {
            
            // Bad parameter
            entity.player.sendMessage(ChatColor.RED + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.INV.ERRORSAVEREMOVE"));
        }
    }

    private void loadDeath() throws SecuboidCommandException {

        String playerName = entity.argList.getNext();

        if (playerName == null) {
            throw new SecuboidCommandException("No player!", entity.sender, "COMMAND.INV.ERRORNOPLAYER");
        }

        // Check for player
        
        @SuppressWarnings("deprecation")
		Player player = Bukkit.getPlayer(playerName);
        
        if (player == null) {
            throw new SecuboidCommandException("Player offline!", entity.sender, "COMMAND.INV.ERRORPLAYEROFFLINE");
        }

        // Check for the last time version
        int lastTime;
        String stNumber = entity.argList.getNext();
        if (stNumber != null) {
            try {
                lastTime = Integer.parseInt(stNumber);
            } catch (NumberFormatException ex) {
                // Number unreadable
                lastTime = 1;
            }
        } else {
            lastTime = 1;
        }

        // Execute
        if (!Secuboid.getThisPlugin().getInventoryListener().loadDeathInventory(player, lastTime)) {
            throw new SecuboidCommandException("Death save not found!", entity.sender, "COMMAND.INV.ERRORDEATHNOTFOUND");
        }

        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.INV.DEATHDONE",
                player.getName()));
    }
    
    private void forceSave() {
        Secuboid.getThisPlugin().getInventoryListener().forceSave();
        entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.INV.SAVEDONE"));
    }
}
