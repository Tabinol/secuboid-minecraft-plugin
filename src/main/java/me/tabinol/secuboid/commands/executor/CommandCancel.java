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
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class CommandCancel.
 */
@InfoCommand(name="cancel")
public class CommandCancel extends CommandExec {

    /** The player. */
    private final Player player;
    
    /** The player conf. */
    private final PlayerConfEntry playerConf;
    
    /** The from auto cancel. */
    private final boolean fromAutoCancel; // true: launched from autoCancel

    /**
     * Instantiates a new command cancel.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCancel(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
        player = entity.player;
        playerConf = entity.playerConf;
        fromAutoCancel = false;
    }

    // Called from PlayerListener
    /**
     * Instantiates a new command cancel.
     *
     * @param entry the entry
     * @param fromAutoCancel the from auto cancel
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCancel(PlayerConfEntry entry, boolean fromAutoCancel) throws SecuboidCommandException {

        super(null);
        this.player = entry.getPlayer();
        playerConf = entry;
        this.fromAutoCancel = fromAutoCancel;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        if (playerConf.getConfirm() != null) {
            playerConf.setConfirm(null);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.CANCEL.ACTION"));
            Secuboid.getThisPlugin().iLog().write(player.getName() + " cancel for action");
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        if (playerConf.getSelection().getSelection(SelectionType.AREA) != null) {

            playerConf.getSelection().removeSelection(SelectionType.AREA);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Secuboid.getThisPlugin().iLog().write(player.getName() + ": Select cancel");

            if(!fromAutoCancel) {
                return;
            }
        }

/*
        if (playerConf.getSetFlagUI() != null) {

            playerConf.setSetFlagUI(null);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.CANCEL.FLAGS"));

            if(!fromAutoCancel) {
                return;
            }
        }
  
*/
        if (playerConf.getSelection().getSelection(SelectionType.LAND) != null) {

            playerConf.getSelection().removeSelection(SelectionType.LAND);
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.CANCEL.SELECT"));

            // Cancel selection (it is the last think selected)
            playerConf.setAutoCancelSelect(false);
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        // No cancel done
        if(!fromAutoCancel) {
            throw new SecuboidCommandException("Nothing to confirm", player, "COMMAND.CANCEL.NOCANCEL");
        }
    }
}
