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
package me.tabinol.secuboid.parameters;

import me.tabinol.secuboid.lands.areas.CuboidArea;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/**
 * The Class LandSetFlag.
 */
public class LandSetFlag extends Thread implements Listener{
    
    /** The player. */
    private Player player;
    
    /** The area. */
    @SuppressWarnings("unused")
    private CuboidArea area;
    
    /** The Redstone torch off. */
    @SuppressWarnings("unused")
    private ItemStack RedstoneTorchOff = new ItemStack(Material.REDSTONE_TORCH_OFF);
    
    /** The Redstone torch on. */
    @SuppressWarnings("unused")
    private ItemStack RedstoneTorchOn = new ItemStack(Material.REDSTONE_TORCH_ON);
    
    /** The inventory. */
    @SuppressWarnings("unused")
    private Inventory inventory;
    
    /**
     * Instantiates a new land set flag.
     *
     * @param player the player
     * @param area the area
     */
    public LandSetFlag(Player player,CuboidArea area){
        this.player = player;
        this.area = area;
        makeMenu();
        //player.openInventory(inventory);
    }
    
    /**
     * Make menu.
     */
    private void makeMenu(){
        
        inventory = player.getServer().createInventory(null,8,"Flag Setting");
        @SuppressWarnings("unused")
        int i = 0;
       /* for(){
            if(){
                ItemStack Torch = RedstoneTorchOn.clone();
                ItemMeta meta = Torch.getItemMeta();
                List<String> lore = new ArrayList<String>();
                
                meta.setDisplayName("Flag");
                lore.add("BLABLABLA");
                meta.setLore(lore);
                
                Torch.setItemMeta(meta);
                inventory.setItem(i, Torch);
            }else{
                ItemStack Torch = RedstoneTorchOff.clone();
                ItemMeta meta = Torch.getItemMeta();
                List<String> lore = new ArrayList<String>();
                
                meta.setDisplayName("Flag");
                lore.add("BLABLABLA");
                meta.setLore(lore);
                
                Torch.setItemMeta(meta);
                inventory.setItem(i, Torch);
            }
                i++;
        }*/
        
    }
    
}
