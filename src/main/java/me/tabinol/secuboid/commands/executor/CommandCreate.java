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
import me.tabinol.secuboid.commands.*;
import me.tabinol.secuboid.config.BannedWords;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The Class CommandCreate.
 */
@InfoCommand(name = "create", forceParameter = true)
public class CommandCreate extends CommandCollisionsThreadExec {

    /**
     * Instantiates a new command create.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCreate(Secuboid secuboid, InfoCommand infoCommand, CommandSender sender, ArgList argList)
            throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(null, true);
        // checkPermission(false, false, null, null);

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);

        Area area = select.getVisualSelection().getArea();
        RealLand localParent;

        // Quit select mod
        // playerConf.setAreaSelection(null);
        // playerConf.setLandSelected(null);
        // select.resetSelection();
        String curArg = argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new SecuboidCommandException(secuboid, "CommandCreate", player, "COMMAND.CREATE.HINTUSE");
        }

        // Check for parent
        if (!argList.isLast()) {

            String curString = argList.getNext();

            if (curString.equalsIgnoreCase("noparent")) {

                localParent = null;
            } else {

                localParent = secuboid.getLands().getLand(curString);

                if (localParent == null) {
                    throw new SecuboidCommandException(secuboid, "CommandCreate", player, "COMMAND.CREATE.PARENTNOTEXIST");
                }
            }
        } else {

            // Autodetect parent
            localParent = select.getVisualSelection().getParentDetected();
        }

        // Not complicated! The player must be AdminMode, or access to create (in world)
        // or access to create in parent if it is a subland.
        if (!playerConf.isAdminMode() && (localParent == null
                || !localParent.getPermissionsFlags().checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType()))) {
            throw new SecuboidCommandException(secuboid, "CommandCreate", player, "GENERAL.MISSINGPERMISSION");
        }

        // If the player is adminmode, the owner is nobody, and set type
        PlayerContainer localOwner;
        Type localType;
        if (playerConf.isAdminMode()) {
            localOwner = new PlayerContainerNobody();
            localType = secuboid.getConf().getTypeAdminMode();
        } else {
            localOwner = playerConf.getPlayerContainer();
            localType = secuboid.getConf().getTypeNoneAdminMode();
        }

        checkCollision(curArg, null, localType, LandAction.LAND_ADD, 0, area, localParent, localOwner, true);
    }

    @Override
    public void commandThreadExecute(Collisions collisions) throws SecuboidCommandException {

        // Check for collision
        if (collisions.hasCollisions()) {
            new CommandCancel(secuboid, infoCommand, sender, argList).commandExecute();
            return;
        }

        // Create Land
        try {
            RealLand cLand = secuboid.getLands().createLand(collisions.getLandName(), owner, newArea, parent, collisions.getPrice(), type);

            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
            secuboid.getLog().debug(playerName + " has create a land named " + cLand.getName() + ".");

            // Cancel and select the land
            new CommandCancel(secuboid, infoCommand, sender, argList).commandExecute();
            new CommandSelect(secuboid, infoCommand, player, new ArgList(secuboid, new String[]{cLand.getName()},
                    player)).commandExecute();

        } catch (SecuboidLandException ex) {
            secuboid.getLog().severe("On land create: " + ex.getLocalizedMessage());
        }

    }
}
