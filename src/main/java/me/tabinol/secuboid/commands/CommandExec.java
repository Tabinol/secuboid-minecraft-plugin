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

import java.util.Calendar;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandCancel;
import me.tabinol.secuboid.commands.executor.CommandHelp;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboidapi.lands.types.ApiType;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.playercontainer.PlayerContainerOwner;
import me.tabinol.secuboidapi.parameters.ApiPermissionType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    protected ApiLand land;
    
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
            checkSelection(entity.playerConf.getSelection().getCuboidArea() != null, mustBeAreaSelected, null, "GENERAL.JOIN.SELECTAREA", true);
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
            ApiPermissionType neededPerm, String bukkitPermission) throws SecuboidCommandException {

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

    // Why Land paramater? The land can be an other land, not the land stored here.
    /**
     * Check collision.
     *
     * @param landName the land name
     * @param land the land
     * @param type the type
     * @param action the action
     * @param removeId the remove id
     * @param newArea the new area
     * @param parent the parent
     * @param owner the owner of the land (PlayerContainer)
     * @param price the price
     * @param addForApprove the add for approve
     * @return true, if successful
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected boolean checkCollision(String landName, ApiLand land, ApiType type, Collisions.LandAction action,
            int removeId, ApiCuboidArea newArea, ApiLand parent, ApiPlayerContainer owner,
            double price, boolean addForApprove) throws SecuboidCommandException {

        // allowApprove: false: The command can absolutely not be done if there is error!
        Collisions coll = new Collisions(landName, land, action, removeId, newArea, parent,
                owner, price, !addForApprove);
        boolean allowApprove = coll.getAllowApprove();

        if (coll.hasCollisions()) {
            entity.sender.sendMessage(coll.getPrints());

            if (addForApprove) {
                if (Secuboid.getThisPlugin().getConf().getAllowCollision() == Config.AllowCollisionType.APPROVE && allowApprove == true) {
                    entity.sender.sendMessage(ChatColor.RED + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.GENERAL.NEEDAPPROVE", landName));
                    Secuboid.getThisPlugin().getLog().write("land " + landName + " has collision and needs approval.");
                    Secuboid.getThisPlugin().getLands().getApproveList().addApprove(new Approve(landName, type, action, removeId, newArea,
                            owner, parent, price, Calendar.getInstance()));
                    new CommandCancel(entity.playerConf, true).commandExecute();
                    return true;
                } else if (Secuboid.getThisPlugin().getConf().getAllowCollision() == Config.AllowCollisionType.FALSE || allowApprove == false) {
                    throw new SecuboidCommandException("Land collision", entity.sender, "COLLISION.GENERAL.CANNOTDONE");
                }
            }
        }
        return false;
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
