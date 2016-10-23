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
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import org.bukkit.ChatColor;

/**
 * Can create a command and calculate the collisions in a thread.
 */
public abstract class CommandCollisionsThreadExec extends CommandExec {

    /**
     *
     */
    protected boolean addForApprove = false;

    /**
     *
     */
    protected Type type = null;

    /**
     *
     */
    protected Collisions.LandAction action = null;

    /**
     *
     */
    protected int removeId = 0;

    /**
     *
     */
    protected Area newArea = null;

    /**
     *
     */
    protected PlayerContainer owner = null;

    /**
     *
     */
    protected Land parent = null;

    /**
     * Instantiates a new command collisions thread exec.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandCollisionsThreadExec(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /**
     * Command thread execute.
     *
     * @param collisions collisions
     * @throws SecuboidCommandException the secuboid command exception
     */
    public abstract void commandThreadExecute(Collisions collisions)
            throws SecuboidCommandException;

    /**
     * Check collision. Why Land paramater? The land can be an other land, not the land stored here.
     *
     * @param landName the land name
     * @param land the land
     * @param type the type
     * @param action the action
     * @param removeId the remove id
     * @param newArea the new area
     * @param parent the parent
     * @param owner the owner of the land (PlayerContainer)
     * @param addForApprove the add for approve
     * @throws SecuboidCommandException the secuboid command exception
     */
    protected void checkCollision(String landName, Land land, Type type, Collisions.LandAction action,
                                     int removeId, Area newArea, Land parent, PlayerContainer owner,
                                     boolean addForApprove) throws SecuboidCommandException {

        // allowApprove: false: The command can absolutely not be done if there is error!
        this.addForApprove = addForApprove;
        this.type = type;
        this.action = action;
        this.removeId = removeId;
        this.newArea = newArea;
        this.owner = owner;
        this.parent = parent;
        boolean isFree = !isPlayerMustPay();
        Collisions coll = new Collisions(landName, land, action, removeId, newArea, parent,
                owner, isFree, !addForApprove);
        Secuboid.getThisPlugin().getCollisionsManagerThread().lookForCollisions(this, coll);
    }

    /**
     * The returned method from thread
     * @param collisions collisions
     * @throws SecuboidCommandException the secuboid command exception
     */
    public void commandThreadParentExecute(Collisions collisions) throws SecuboidCommandException {

        boolean allowApprove = collisions.getAllowApprove();

        if (collisions.hasCollisions()) {
            entity.sender.sendMessage(collisions.getPrints());

            if (addForApprove) {
                if (Secuboid.getThisPlugin().getConf().getAllowCollision() == Config.AllowCollisionType.APPROVE && allowApprove == true) {

                    entity.sender.sendMessage(ChatColor.RED + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COLLISION.GENERAL.NEEDAPPROVE", collisions.getLandName()));
                    Secuboid.getThisPlugin().getLog().write("land " + collisions.getLandName() + " has collision and needs approval.");
                    Secuboid.getThisPlugin().getLands().getApproveList().addApprove(new Approve(collisions.getLandName(), type, action, removeId, newArea,
                            owner, parent, collisions.getPrice(), Calendar.getInstance()));
                    new CommandCancel(entity.playerConf, true).commandExecute();

                } else if (Secuboid.getThisPlugin().getConf().getAllowCollision() == Config.AllowCollisionType.FALSE || allowApprove == false) {

                    throw new SecuboidCommandException("Land collision", entity.sender, "COLLISION.GENERAL.CANNOTDONE");
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
    protected boolean isPlayerMustPay() {
        // Is Economy?


        return !(Secuboid.getThisPlugin().getPlayerMoney() == null
                || !Secuboid.getThisPlugin().getConf().useEconomy()
                || entity.playerConf.isAdminMod());
    }
}
