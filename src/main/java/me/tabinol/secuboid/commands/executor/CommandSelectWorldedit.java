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

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.selection.region.AreaSelection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


// WorldEdit is in a separate class from CommandSelect because if WorldEdit
// is not installed, we don't want to makes error.

/**
 * The Class CommandSelectWorldedit.
 */
public class CommandSelectWorldedit {
    
    /** The player. */
    Player player;
    
    /** The entry. */
    PlayerConfEntry entry;
    
    /**
     * Instantiates a new command select worldedit.
     *
     * @param player the player
     * @param entry the entry
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelectWorldedit(Player player, PlayerConfEntry entry) throws SecuboidCommandException{
        
        this.player = player;
        this.entry = entry;
    }
    
    /**
     * Make select.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected void MakeSelect() throws SecuboidCommandException {
        
        if (Secuboid.getThisPlugin().iDependPlugin().getWorldEdit() == null) {
            throw new SecuboidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
        }
        LocalSession session = ((WorldEditPlugin) Secuboid.getThisPlugin().iDependPlugin().getWorldEdit()).getSession(player);
        
        try {
            Region sel;
            if (session.getSelectionWorld() == null
                    || !((sel = session.getSelection(session.getSelectionWorld())) != null && sel instanceof CuboidRegion)) {
                throw new SecuboidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED");
            }

            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            Secuboid.getThisPlugin().iLog().write(Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            
            AreaSelection select = new AreaSelection(player, new CuboidArea(player.getWorld().getName(), 
                    sel.getMinimumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(),
                    sel.getMinimumPoint().getBlockZ(), sel.getMaximumPoint().getBlockX(), 
                    sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ()));
            
            entry.getSelection().addSelection(select);
            entry.setAutoCancelSelect(true);

        } catch (IncompleteRegionException ex) {
            throw new SecuboidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET");
        }
    }
}
