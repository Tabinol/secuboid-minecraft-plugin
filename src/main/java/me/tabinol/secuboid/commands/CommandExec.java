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
package me.tabinol.secuboid.commands;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandHelp;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.parameters.PermissionType;
import me.tabinol.secuboid.playercontainer.PlayerContainerOwner;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


/**
 * The Class CommandExec.
 */
public abstract class CommandExec {

    /** The entity. */
    protected final CommandEntities entity;
    
    /** The land. */
    protected Land land;
    
    /** The is executable. */
    private boolean isExecutable = true;
    
    /** The reset select cancel. */
    public boolean resetSelectCancel = false; // If reset select cancel is done (1 time only)

    /**
     * Instantiates a new command exec.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected CommandExec(CommandEntities entity) throws SecuboidCommandException {

        this.entity = entity;

        // Null Entity for an action without command, but don't ask to have information!
        if (entity == null) {
            land = null;
            return;
        }

        if (entity.player != null) {
            // get the land Selected or null
            land = entity.playerConf.getSelection().getLand();
        }

        if (entity.player == null && !entity.infoCommand.allowConsole()) {

            // Send a message if this command is player only
            throw new SecuboidCommandException("Impossible to do from console", Bukkit.getConsoleSender(), "CONSOLE");
        }

        // Show help if there is no more parameter and the command needs one
        if (entity.infoCommand.forceParameter() && entity.argList != null && entity.argList.isLast()) {
            new CommandHelp(entity.onCommand, entity.sender, 
                    entity.infoCommand.name()).commandExecute();
            isExecutable = false;
        }
    }

    /**
     * Command execute.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    public abstract void commandExecute() throws SecuboidCommandException;
    
    
    /**
     * Checks if is executable.
     *
     * @return true, if is executable
     */
    public boolean isExecutable() {

        return isExecutable;
    }

    // Check for needed selection and not needed (null for no verification)
    /**
     * Check selections.
     *
     * @param mustBeSelectMode the must be select mode
     * @param mustBeAreaSelected the must be area selected
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected void checkSelections(Boolean mustBeSelectMode, Boolean mustBeAreaSelected) throws SecuboidCommandException {

        // No check if entity is null (if it is not from a command)
        if (entity == null) {
            return;
        }

        if (mustBeSelectMode != null) {
            // Pasted to variable land, can take direcly
            checkSelection(land != null, mustBeSelectMode, null, "GENERAL.JOIN.SELECTMODE",
                    entity != null && entity.playerConf.getSelection().getLand() != null);
        }
        if (mustBeAreaSelected != null) {
            checkSelection(entity.playerConf.getSelection().getArea() != null, mustBeAreaSelected, null, "GENERAL.JOIN.SELECTAREA", true);
        }
    }

    // Check selection for per type
    /**
     * Check selection.
     *
     * @param result the result
     * @param neededResult the needed result
     * @param messageTrue the message true
     * @param messageFalse the message false
     * @param startSelectCancel the start select cancel
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void checkSelection(boolean result, boolean neededResult, String messageTrue, String messageFalse,
            boolean startSelectCancel) throws SecuboidCommandException {

        if (result != neededResult) {
            if (result == true) {
                throw new SecuboidCommandException("Player Select", entity.player, messageTrue);
            } else {
                throw new SecuboidCommandException("Player Select", entity.player, messageFalse);
            }
        } else {
            if (startSelectCancel && !resetSelectCancel && result == true) {

                // Reset autocancel if there is a command executed that need it
                entity.playerConf.setAutoCancelSelect(true);
                resetSelectCancel = true;
            }
        }
    }

    // Check if the player has permission
    /**
     * Check permission.
     *
     * @param mustBeAdminMod the must be admin mod
     * @param mustBeOwner the must be owner
     * @param neededPerm the needed perm
     * @param bukkitPermission the bukkit permission
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected void checkPermission(boolean mustBeAdminMod, boolean mustBeOwner,
            PermissionType neededPerm, String bukkitPermission) throws SecuboidCommandException {

        boolean canDo = false;

        if (mustBeAdminMod && entity.playerConf.isAdminMod()) {
            canDo = true;
        }
        if (mustBeOwner && (land == null || (land !=null && new PlayerContainerOwner(land).hasAccess(entity.player)))) {
            canDo = true;
        }
        if (neededPerm != null && land.checkPermissionAndInherit(entity.player, neededPerm)) {
            canDo = true;
        }
        if (bukkitPermission != null && entity.sender.hasPermission(bukkitPermission)) {
            canDo = true;
        }

        // No permission, this is an exception
        if (canDo == false) {
            throw new SecuboidCommandException("No permission to do this action", entity.player, "GENERAL.MISSINGPERMISSION");
        }
    }

    // The name says what it does!!!
    /**
     * Gets the land from command if no land selected.
     */
    protected void getLandFromCommandIfNoLandSelected() {

        if (land == null && !entity.argList.isLast()) {
            land = Secuboid.getThisPlugin().getLands().getLand(entity.argList.getNext());
        }
    }
    
    /**
     * Removes the sign from hand.
     */
    protected void removeSignFromHand() {
        
        if(entity.player.getGameMode() != GameMode.CREATIVE) {
            if(entity.player.getItemInHand().getAmount() == 1) {
                entity.player.setItemInHand(new ItemStack(Material.AIR));
            } else {
                entity.player.getItemInHand().setAmount(entity.player.getItemInHand().getAmount() - 1);
            }
        }
    }
}
