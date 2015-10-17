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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.approve.ApproveList;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;

import org.bukkit.ChatColor;


/**
 * The Class CommandApprove.
 */
@InfoCommand(name="approve", allowConsole=true, forceParameter=true)
public class CommandApprove extends CommandExec {

    /**
     * Instantiates a new command approve.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandApprove(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        String curArg = entity.argList.getNext();
        ApproveList approveList = Secuboid.getThisPlugin().iLands().getApproveList();
        boolean isApprover = entity.sender.hasPermission("secuboid.collisionapprove");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (curArg.equalsIgnoreCase("clear")) {
            
            if(!isApprover) {
            	throw new SecuboidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
            approveList.removeAll();
            entity.sender.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.CLEAR"));
            
        } else if (curArg.equalsIgnoreCase("list")) {

            // List of Approve
            StringBuilder stList = new StringBuilder();
            int t = 0;
            TreeMap<Date,Approve> approveTree = new TreeMap<Date,Approve>();
            
            //create list (short by date/time)
            for (Approve app : approveList.getApproveList().values()) {
                approveTree.put(app.getDateTime().getTime(), app);
            }
            
            // show Approve List
            for(Map.Entry<Date,Approve> approveEntry : approveTree.descendingMap().entrySet()) {
                Approve app = approveEntry.getValue();
                if (app != null && (isApprover || app.getOwner().hasAccess(entity.player))) {
                    stList.append(ChatColor.WHITE + Secuboid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.LIST",
                            ChatColor.BLUE + df.format(app.getDateTime().getTime()) + ChatColor.WHITE,
                            ChatColor.BLUE + app.getLandName() + ChatColor.WHITE,
                            app.getOwner().getPrint() + ChatColor.WHITE,
                            ChatColor.BLUE + app.getAction().toString() + ChatColor.WHITE));
                    stList.append(Config.NEWLINE);
                    t++;
                }
            }
            if (t == 0) {

                // List empty
                entity.sender.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.LISTROWNULL"));
            } else {

                // List not empty
                new ChatPage("COLLISION.SHOW.LISTSTART", stList.toString(), entity.sender, null).getPage(1);
            }
        } else if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm") || curArg.equalsIgnoreCase("cancel")) {

            String param = entity.argList.getNext();

            if (param == null) {
                throw new SecuboidCommandException("Approve", entity.sender, "COLLISION.SHOW.PARAMNULL");
            }

            Approve approve = approveList.getApprove(param);

            if (approve == null) {
                throw new SecuboidCommandException("Approve", entity.sender, "COLLISION.SHOW.PARAMNULL");
            }

            // Check permission
            if ((curArg.equalsIgnoreCase("confirm") && !isApprover)
                    || ((curArg.equalsIgnoreCase("cancel") || curArg.equalsIgnoreCase("info"))
                    && !(isApprover || approve.getOwner().hasAccess(entity.player)))) {
                throw new SecuboidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }

            ILand land = Secuboid.getThisPlugin().iLands().getLand(param);
            Collisions.LandAction action = approve.getAction();
            int removeId = approve.getRemovedAreaId();
            ICuboidArea newArea = approve.getNewArea();
            ILand parent = approve.getParent();
            Double price = approve.getPrice();
            IPlayerContainer owner = approve.getOwner();

            if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")) {

                // Print area
                if(newArea != null) {
                    entity.sender.sendMessage(newArea.getPrint());
                }
                
                // Info on the specified land (Collision)
                checkCollision(param, land, null, action, removeId, newArea, parent, owner, price, false);

                if (curArg.equalsIgnoreCase("confirm")) {

                    // Create the action (if it is possible)
                    approveList.removeApprove(approve);
                    approve.createAction();
                    entity.sender.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.DONE"));
                }
            } else if (curArg.equalsIgnoreCase("cancel")) {

                // Remove in approve list
                approveList.removeApprove(approve);
                entity.sender.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.REMOVE"));
            } else {
                throw new SecuboidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
        } else {
            throw new SecuboidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }
}
