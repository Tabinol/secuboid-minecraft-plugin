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

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboid.selection.region.AreaSelection;

/**
 * Can create a command and calculate the collisions in a thread.
 */
public abstract class CommandCollisionsThreadExec extends CommandExec {

    private static final long STATUS_FIRST_NB_TICKS = 40;
    private static final long STATUS_NEXT_NB_TICKS = 400;

    private boolean addForApprove = false;
    private Collisions.LandAction action = null;
    private BukkitTask statusTask = null;
    Type type = null;
    int removeId = 0;
    Area newArea = null;
    PlayerContainer owner = null;
    Land parent = null;

    private class CollisionThreadStatus extends BukkitRunnable {

        private final Player player;
        private final Collisions collisions;
        private long nbTick;

        CollisionThreadStatus(final Player player, final Collisions collisions) {
            this.player = player;
            this.collisions = collisions;
            nbTick = STATUS_FIRST_NB_TICKS;
        }

        @Override
        public void run() {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.DARK_GRAY + "[Secuboid] " + secuboid.getLanguage()
                        .getMessage("COLLISION.GENERAL.PERCENT", collisions.getPercentDone() + ""));
            }
            secuboid.getLogger().info("Collision manger is running and takes " + nbTick + " ticks.");
            nbTick += STATUS_NEXT_NB_TICKS;
        }
    }

    /**
     * Instantiates a new command collisions thread exec.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCollisionsThreadExec(final Secuboid secuboid, final InfoCommand infoCommand,
            final CommandSender sender, final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
    }

    /**
     * Command thread execute.
     *
     * @param collisions collisions
     * @throws SecuboidCommandException the secuboid command exception
     */
    public abstract void commandThreadExecute(Collisions collisions) throws SecuboidCommandException;

    /**
     * Check collision. Why Land parameter? The land can be an other land, not the
     * land stored here.
     * 
     * @param worldName     the world name
     * @param landName      the land name
     * @param land          the land
     * @param type          the type
     * @param action        the action
     * @param removeId      the remove id
     * @param newArea       the new area
     * @param parent        the parent
     * @param owner         the owner of the land (PlayerContainer)
     * @param addForApprove the add for approve
     * @throws SecuboidCommandException the secuboid command exception
     */
    final void checkCollision(final String worldName, final String landName, final Land land, final Type type,
            final Collisions.LandAction action, final int removeId, final Area newArea, final Land parent,
            final PlayerContainer owner, final boolean addForApprove) {

        // allowApprove: false: The command can absolutely not be done if there is
        // error!
        this.addForApprove = addForApprove;
        this.type = type;
        this.action = action;
        this.removeId = removeId;
        this.newArea = newArea;
        this.owner = owner;
        this.parent = parent;
        final boolean isFree = !isPlayerMustPay();
        final Collisions coll = new Collisions(secuboid, worldName, landName, land, action, removeId, newArea, parent,
                owner, isFree, !addForApprove);
        secuboid.getCollisionsManagerThread().lookForCollisions(this, coll);
        final CollisionThreadStatus collisionThreadStatus = new CollisionThreadStatus(player, coll);
        statusTask = Bukkit.getScheduler().runTaskTimer(secuboid, (Runnable) collisionThreadStatus,
                STATUS_FIRST_NB_TICKS, STATUS_NEXT_NB_TICKS);
    }

    /**
     * The returned method from thread
     *
     * @param collisions collisions
     * @throws SecuboidCommandException the secuboid command exception
     */
    public final void commandThreadParentExecute(final Collisions collisions) throws SecuboidCommandException {

        final boolean allowApprove = collisions.getAllowApprove();

        if (statusTask != null) {
            statusTask.cancel();
        }

        if (collisions.hasCollisions()) {
            sender.sendMessage(collisions.getPrints());

            if (addForApprove) {
                if (secuboid.getConf().getAllowCollision() == Config.AllowCollisionType.APPROVE && allowApprove) {
                    Land landLocal;
                    if ((landLocal = collisions.getLand()) == null) {
                        // Land create? create a non approved land
                        final UUID uuid = UUID.randomUUID();
                        try {
                            landLocal = secuboid.getLands().createLand(collisions.getLandName(), false, owner, newArea,
                                    parent, 1, uuid, type);
                        } catch (final SecuboidLandException e) {
                            throw new SecuboidCommandException(secuboid, sender,
                                    String.format("Error creating the non approved land \"%s\" for \"%s\"",
                                            collisions.getLandName(), sender.getName()),
                                    e);
                        }
                    } else if (newArea != null) {
                        if (action == Collisions.LandAction.AREA_MODIFY) {
                            // Area replace
                            landLocal.addArea(newArea, -1);
                        } else {
                            // new area
                            landLocal.addArea(newArea);
                        }
                    }

                    sender.sendMessage(ChatColor.RED + "[Secuboid] " + secuboid.getLanguage()
                            .getMessage("COLLISION.GENERAL.NEEDAPPROVE", collisions.getLandName()));
                    final Optional<Integer> newAreaIdOpt = Optional
                            .ofNullable(Optional.ofNullable(newArea).map(newArea -> newArea.getKey()).orElse(null));
                    secuboid.getLands().getApproves()
                            .addApprove(new Approve(landLocal, action, Optional.of(removeId), newAreaIdOpt, owner,
                                    Optional.ofNullable(parent), collisions.getPrice(), Calendar.getInstance()));
                    // new Approve(land, action, removedAreaIdOpt, newAreaIdOpt, owner, parentOpt,
                    // price, dateTime)
                    new CommandCancel(secuboid, null, sender, argList).commandExecute();

                } else if (secuboid.getConf().getAllowCollision() == Config.AllowCollisionType.FALSE || !allowApprove) {

                    throw new SecuboidCommandException(secuboid, "Land collision", sender,
                            "COLLISION.GENERAL.CANNOTDONE");
                }
            }
        }

        commandThreadExecute(collisions);
    }

    /**
     * Checks if is player must pay.
     *
     * @return true, if is player must pay
     */
    private boolean isPlayerMustPay() {
        // Is Economy?

        return !(!secuboid.getPlayerMoneyOpt().isPresent() || !secuboid.getConf().useEconomy()
                || playerConf.isAdminMode());
    }

    /**
     * Class for multiple return for landCheckForCreate. It is just for land
     * create/check.
     */
    final class LandCheckValues {
        LandPermissionsFlags localPermissionsFlagsParent;
        Land realLocalParent;
        PlayerContainer localOwner;
        Type localType;
    }

    /**
     * This is common check for land create and land select info commands.
     * 
     * @param select the player selection
     * @return multiple the value for area create
     * @throws SecuboidCommandException no permission/command error
     */
    final LandCheckValues landCheckForCreate(final AreaSelection select) throws SecuboidCommandException {
        final LandCheckValues landCheckValues = new LandCheckValues();

        // Check for parent
        if (!argList.isLast()) {

            final String curString = argList.getNext();

            if (curString.equalsIgnoreCase("noparent")) {

                landCheckValues.localPermissionsFlagsParent = null;
            } else {
                final Land localParent = secuboid.getLands().getLand(curString);
                if (localParent == null) {
                    throw new SecuboidCommandException(secuboid, "CommandCreate", player,
                            "COMMAND.CREATE.PARENTNOTEXIST");
                }
                landCheckValues.localPermissionsFlagsParent = localParent.getPermissionsFlags();
            }
        } else {

            // Autodetect parent
            landCheckValues.localPermissionsFlagsParent = select.getVisualSelection().getParentPermsFlagsDetected();
        }

        // Not complicated! The player must be AdminMode, or access to create (in world)
        // or access to create in parent if it is a subland.
        if (!playerConf.isAdminMode()
                && (landCheckValues.localPermissionsFlagsParent == null || !landCheckValues.localPermissionsFlagsParent
                        .checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType()))) {
            throw new SecuboidCommandException(secuboid, "CommandCreate", player, "GENERAL.MISSINGPERMISSION");
        }

        // If the player is adminmode, the owner is nobody, and set type
        if (playerConf.isAdminMode()) {
            landCheckValues.localOwner = PlayerContainerNobody.getInstance();
            landCheckValues.localType = secuboid.getConf().getTypeAdminMode();
        } else {
            landCheckValues.localOwner = playerConf.getPlayerContainer();
            landCheckValues.localType = secuboid.getConf().getTypeNoneAdminMode();
        }
        if (landCheckValues.localPermissionsFlagsParent.getLandNullable() != null) {
            landCheckValues.realLocalParent = landCheckValues.localPermissionsFlagsParent.getLandNullable();
        } else {
            landCheckValues.realLocalParent = null;
        }

        return landCheckValues;
    }
}
