/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.approve.Approves;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.playercontainer.PlayerContainer;

/**
 * The Class CommandApprove.
 */
@InfoCommand(name = "approve", allowConsole = true, forceParameter = true, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "clear", "list", "info", "confirm", "cancel" }), //
                @CompletionMap(regex = "^(info|confirm|cancel)$", completions = { "@approveLandList" }) //
        })
public final class CommandApprove extends CommandCollisionsThreadExec {

    private final Approves approves;
    private boolean confirm = false;
    Approve approve = null;

    /**
     * Instantiates a new command approve.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandApprove(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
        approves = secuboid.getLands().getApproves();
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        final String curArg = argList.getNext();
        final boolean isApprover = sender.hasPermission("secuboid.collisionapprove");
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final LandPermissionsFlags landPermissionsFlagsSelectNullable = landSelectNullable != null
                ? landSelectNullable.getPermissionsFlags()
                : null;

        if (curArg.equalsIgnoreCase("clear")) {

            if (!isApprover) {
                throw new SecuboidCommandException(secuboid, "Approve", sender, "GENERAL.MISSINGPERMISSION");
            }
            approves.removeAll();
            sender.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COLLISION.GENERAL.CLEAR"));

        } else if (curArg.equalsIgnoreCase("list")) {

            // List of Approve
            final StringBuilder stList = new StringBuilder();
            int t = 0;
            final TreeMap<Date, Approve> approveTree = new TreeMap<Date, Approve>();

            // create list (short by date/time)
            for (final Approve app : approves.getApproveList().values()) {
                approveTree.put(app.getDateTime().getTime(), app);
            }

            // show Approve List
            for (final Map.Entry<Date, Approve> approveEntry : approveTree.descendingMap().entrySet()) {
                final Approve app = approveEntry.getValue();
                if (app != null
                        && (isApprover || app.getOwner().hasAccess(player, landPermissionsFlagsSelectNullable))) {
                    stList.append(ChatColor.WHITE)
                            .append(secuboid.getLanguage().getMessage("COLLISION.SHOW.LIST",
                                    ChatColor.BLUE + df.format(app.getDateTime().getTime()) + ChatColor.WHITE,
                                    ChatColor.BLUE + app.getLand().getName() + ChatColor.WHITE,
                                    app.getOwner().getPrint() + ChatColor.WHITE,
                                    ChatColor.BLUE + app.getAction().toString() + ChatColor.WHITE));
                    stList.append(Config.NEWLINE);
                    t++;
                }
            }
            if (t == 0) {

                // List empty
                sender.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                        + secuboid.getLanguage().getMessage("COLLISION.SHOW.LISTROWNULL"));
            } else {

                // List not empty
                new ChatPage(secuboid, "COLLISION.SHOW.LISTSTART", stList.toString(), sender, null).getPage(1);
            }
        } else if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")
                || curArg.equalsIgnoreCase("cancel")) {

            final String param = argList.getNext();

            if (param == null) {
                throw new SecuboidCommandException(secuboid, "Approve", sender, "COLLISION.SHOW.PARAMNULL");
            }

            approve = approves.getApprove(param);

            if (approve == null) {
                throw new SecuboidCommandException(secuboid, "Approve", sender, "COLLISION.SHOW.PARAMNULL");
            }

            // Check permission
            if ((curArg.equalsIgnoreCase("confirm") && !isApprover) || ((curArg.equalsIgnoreCase("cancel")
                    || curArg.equalsIgnoreCase("info"))
                    && !(isApprover || approve.getOwner().hasAccess(player, landPermissionsFlagsSelectNullable)))) {
                throw new SecuboidCommandException(secuboid, "Approve", sender, "GENERAL.MISSINGPERMISSION");
            }

            final Land landLocal = approve.getLand();
            final Collisions.LandAction actionLocal = approve.getAction();
            final Optional<Integer> removeAreaIdOptLocal = approve.getRemovedAreaIdOpt();
            final Optional<Integer> newAreaIdOptLocal = approve.getNewAreaIdOpt();
            final Optional<Land> parentOptLocal = approve.getParentOpt();
            final PlayerContainer ownerLocal = approve.getOwner();

            if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")) {
                String worldNameLocal;

                // Print area and get world
                final Area newAreaLocalNullable;
                if (newAreaIdOptLocal.isPresent()) {
                    newAreaLocalNullable = landLocal.getArea(newAreaIdOptLocal.get());
                    worldNameLocal = newAreaLocalNullable.getWorldName();
                    sender.sendMessage(newAreaLocalNullable.getPrint());
                } else {
                    newAreaLocalNullable = null;
                    worldNameLocal = landSelectNullable.getWorldName();
                }

                if (curArg.equalsIgnoreCase("confirm")) {
                    // Paste to the After Thread
                    confirm = true;
                }
                // Info on the specified land (Collision)
                checkCollision(worldNameLocal, param, landLocal, null, actionLocal, removeAreaIdOptLocal.orElse(0),
                        newAreaLocalNullable, parentOptLocal.orElse(null), ownerLocal, false);

            } else if (curArg.equalsIgnoreCase("cancel")) {

                // Remove in approve list
                approves.removeApprove(approve);
                sender.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                        + secuboid.getLanguage().getMessage("COLLISION.GENERAL.REMOVE"));
            } else {
                throw new SecuboidCommandException(secuboid, "Approve", sender, "GENERAL.MISSINGPERMISSION");
            }
        } else {
            throw new SecuboidCommandException(secuboid, "Missing information command", sender, "GENERAL.MISSINGINFO");
        }
    }

    @Override
    public void commandThreadExecute(final Collisions collisions) throws SecuboidCommandException {

        if (confirm) {
            // Create the action (if it is possible)
            try {
                approves.createAction(approve);
            } catch (final SecuboidLandException e) {
                throw new SecuboidCommandException(secuboid, sender, "Error in land approve command", e);
            }
            sender.sendMessage(
                    ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COLLISION.GENERAL.DONE"));
        }
    }
}
