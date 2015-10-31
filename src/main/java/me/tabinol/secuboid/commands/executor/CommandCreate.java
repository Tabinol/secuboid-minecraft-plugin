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

import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.BannedWords;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiType;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;

import org.bukkit.ChatColor;


/**
 * The Class CommandCreate.
 */
@InfoCommand(name="create", forceParameter=true)
public class CommandCreate extends CommandExec {

    /**
     * Instantiates a new command create.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCreate(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(null, true);
        // checkPermission(false, false, null, null);

        AreaSelection select = (AreaSelection) entity.playerConf.getSelection().getSelection(SelectionType.AREA);

        ApiCuboidArea area = select.getCuboidArea();
        Double price = entity.playerConf.getSelection().getLandCreatePrice();
        ApiLand parent;

        // Quit select mod
        // entity.playerConf.setAreaSelection(null);
        // entity.playerConf.setLandSelected(null);
        // select.resetSelection();

        String curArg = entity.argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new SecuboidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.HINTUSE");
        }

        // Check for parent
        if (!entity.argList.isLast()) {

            String curString = entity.argList.getNext();
            
            if(curString.equalsIgnoreCase("noparent")) {
                
                parent = null;
            }
            
            else {
            
                parent = Secuboid.getThisPlugin().getLands().getLand(curString);

                if (parent == null) {
                    throw new SecuboidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.PARENTNOTEXIST");
                }
            }
        } else {

            // Autodetect parent
            parent = select.getParentDetected();
        }

        // Not complicated! The player must be AdminMod, or access to create (in world) 
        // or access to create in parent if it is a subland.
        if (!entity.playerConf.isAdminMod()
                && ((parent == null && !Secuboid.getThisPlugin().getLands().getOutsideArea(area.getWorldName()).checkPermissionAndInherit(entity.player, PermissionList.LAND_CREATE.getPermissionType()))
                || (parent != null && !parent.checkPermissionAndInherit(entity.player, PermissionList.LAND_CREATE.getPermissionType())))) {
            throw new SecuboidCommandException("CommandCreate", entity.player, "GENERAL.MISSINGPERMISSION");
        }

        // If the player is adminmod, the owner is nobody, and set type
        ApiPlayerContainer owner;
        ApiType type;
        if(entity.playerConf.isAdminMod()) {
            owner = new PlayerContainerNobody();
            type = Secuboid.getThisPlugin().getConf().getTypeAdminMod();
        } else {
            owner = entity.playerConf.getPlayerContainer();
            type = Secuboid.getThisPlugin().getConf().getTypeNoneAdminMod();
        }

        // Check for collision
        if (checkCollision(curArg, null, type, LandAction.LAND_ADD, 0, area, parent, owner, price, true)) {
            new CommandCancel(entity.playerConf, true).commandExecute();
            return;
        }

        // Create Land
        ApiLand land = null;
        
        try {
            land = Secuboid.getThisPlugin().getLands().createLand(curArg, owner, area, parent, price, type);
        } catch (SecuboidLandException ex) {
            Logger.getLogger(CommandCreate.class.getName()).log(Level.SEVERE, "On land create", ex);
        }

        entity.player.sendMessage(ChatColor.GREEN + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.CREATE.DONE"));
        Secuboid.getThisPlugin().getLog().write(entity.playerName + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
        
        // Cancel and select the land
        new CommandCancel(entity.playerConf, true).commandExecute();
        new CommandSelect(entity.player, new ArgList(new String[] {land.getName()}, 
                entity.player), null).commandExecute();
    }
}
