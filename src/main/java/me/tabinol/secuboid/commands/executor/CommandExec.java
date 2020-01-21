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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.permissionsflags.PermissionType;
import me.tabinol.secuboid.players.PlayerConfEntry;

/**
 * The Class CommandExec.
 */
public abstract class CommandExec {

    /**
     * Secuboid instance.
     */
    final Secuboid secuboid;

    /**
     * The command.
     */
    final InfoCommand infoCommand;

    /**
     * The sender.
     */
    final CommandSender sender;

    /**
     * The arg list.
     */
    final ArgList argList;

    /**
     * The player.
     */
    final Player player;

    /**
     * The player conf.
     */
    final PlayerConfEntry playerConf;

    /**
     * The selected land.
     */
    protected Land landSelectNullable;

    /**
     * The is executable.
     */
    private boolean isExecutable = true;

    /**
     * The reset select cancel.
     */
    private boolean resetSelectCancel = false; // If reset select cancel is done (1 time only)

    /**
     * Instantiates a new command exec.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected CommandExec(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        this.secuboid = secuboid;
        this.infoCommand = infoCommand;
        this.sender = sender;
        if (argList != null) {
            this.argList = argList;
        } else {
            this.argList = new ArgList(secuboid, new String[0], sender);
        }

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }

        playerConf = secuboid.getPlayerConf().get(sender);

        if (player != null) {
            // get the land Selected or null
            landSelectNullable = playerConf.getSelection().getLand();
        } else {
            landSelectNullable = null;
        }

        if (infoCommand != null) {
            if (player == null && !infoCommand.allowConsole()) {

                // Send a message if this command is player only
                throw new SecuboidCommandException(secuboid, "Impossible to do from console", Bukkit.getConsoleSender(),
                        "CONSOLE");
            }

            // Show help if there is no more parameter and the command needs one
            if (infoCommand.forceParameter() && argList != null && argList.isLast()) {
                new CommandHelp(secuboid, null, sender,
                        new ArgList(secuboid, new String[] { infoCommand.name() }, sender)).commandExecute();
                isExecutable = false;
            }
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
    public final boolean isExecutable() {
        return isExecutable;
    }

    /**
     * Check for needed selection and not needed (null for no verification).
     *
     * @param mustBeSelectMode   the must be select mode
     * @param mustBeAreaSelected the must be area selected
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected final void checkSelections(final Boolean mustBeSelectMode, final Boolean mustBeAreaSelected)
            throws SecuboidCommandException {

        if (mustBeSelectMode != null) {
            // Pasted to variable land, can take direcly
            checkSelection(landSelectNullable != null, mustBeSelectMode, "GENERAL.JOIN.SELECTMODE",
                    playerConf.getSelection().getLand() != null);
        }
        if (mustBeAreaSelected != null) {
            checkSelection(playerConf.getSelection().getArea() != null, mustBeAreaSelected, "GENERAL.JOIN.SELECTAREA",
                    true);
        }
    }

    /**
     * Check selection for per type.
     *
     * @param result            the result
     * @param neededResult      the needed result
     * @param messageFalse      the message false
     * @param startSelectCancel the start select cancel
     * @throws SecuboidCommandException the secuboid command exception
     */
    private final void checkSelection(final boolean result, final boolean neededResult, final String messageFalse,
            final boolean startSelectCancel) throws SecuboidCommandException {

        if (result != neededResult) {
            if (!result) {
                throw new SecuboidCommandException(secuboid, "Player Select", player, messageFalse);
            }
        } else if (startSelectCancel && !resetSelectCancel && result) {

            // Reset autocancel if there is a command executed that need it
            playerConf.setAutoCancelSelect(true);
            resetSelectCancel = true;
        }
    }

    // Check if the player has permission

    /**
     * Check permission.
     *
     * @param mustBeAdminMode  the must be admin mod
     * @param mustBeOwner      the must be owner
     * @param neededPerm       the needed perm
     * @param bukkitPermission the bukkit permission
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected final void checkPermission(final boolean mustBeAdminMode, final boolean mustBeOwner,
            final PermissionType neededPerm, final String bukkitPermission) throws SecuboidCommandException {

        boolean canDo = false;

        if (mustBeAdminMode && playerConf.isAdminMode()) {
            canDo = true;
        } else if (mustBeOwner && (landSelectNullable == null || landSelectNullable.isOwner(player))) {
            canDo = true;
        } else if (neededPerm != null
                && landSelectNullable.getPermissionsFlags().checkPermissionAndInherit(player, neededPerm)) {
            canDo = true;
        } else if (bukkitPermission != null && sender.hasPermission(bukkitPermission)) {
            canDo = true;
        }

        // No permission, this is an exception
        if (!canDo) {
            throw new SecuboidCommandException(secuboid, "No permission to do this action", player,
                    "GENERAL.MISSINGPERMISSION");
        }
    }

    // The name says what it does!!!

    /**
     * Gets the land from command if no land selected.
     */
    final void getLandFromCommandIfNoLandSelected() {

        if (landSelectNullable == null && !argList.isLast()) {
            landSelectNullable = secuboid.getLands().getLand(argList.getNext());
        }
    }

    /**
     * Removes the sign from hand.
     */
    final void removeSignFromHand() {
        if (player.getGameMode() != GameMode.CREATIVE) {
            final EntityEquipment equipment = player.getEquipment();
            if (equipment.getItemInMainHand().getAmount() == 1) {
                equipment.setItemInMainHand(new ItemStack(Material.AIR));
            } else {
                equipment.getItemInMainHand().setAmount(equipment.getItemInMainHand().getAmount() - 1);
            }
        }
    }
}
