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

import java.util.LinkedList;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandThreadExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboidapi.ApiSecuboidSta;
import me.tabinol.secuboidapi.lands.ApiDummyLand;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.parameters.ApiPermission;
import me.tabinol.secuboidapi.parameters.ApiPermissionType;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;

import org.bukkit.ChatColor;


/**
 * The Class CommandPermission.
 */
@InfoCommand(name="permission", forceParameter=true)
public class CommandPermission extends CommandThreadExec {

    private LinkedList<ApiDummyLand> precDL; // Listed Precedent lands (no duplicates)
    private StringBuilder stList;

    private String fonction;

    /**
     * Instantiates a new command permission.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandPermission(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);

        fonction = entity.argList.getNext();
        
        if (fonction.equalsIgnoreCase("set")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            
            Secuboid.getThisPlugin().getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("unset")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            Secuboid.getThisPlugin().getPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

            precDL = new LinkedList<ApiDummyLand>();
            stList = new StringBuilder();

            // For the actual land
            importDisplayPermsFrom(land, false);
            
            // For default Type
            if(land.getType() != null) {
                stList.append(ChatColor.DARK_GRAY + Secuboid.getThisPlugin().getLanguage().getMessage("GENERAL.FROMDEFAULTTYPE",
                        land.getType().getName())).append(Config.NEWLINE);
                importDisplayPermsFrom(((Lands) ApiSecuboidSta.getLands()).getDefaultConf(land.getType()), false);
            }
            
            // For parent (if exist)
            ApiLand parLand = land;
            while((parLand = parLand.getParent()) != null) {
                stList.append(ChatColor.DARK_GRAY + Secuboid.getThisPlugin().getLanguage().getMessage("GENERAL.FROMPARENT",
                        ChatColor.GREEN + parLand.getName() + ChatColor.DARK_GRAY)).append(Config.NEWLINE);
                importDisplayPermsFrom(parLand, true);
            }
            
            // For world
            stList.append(ChatColor.DARK_GRAY + Secuboid.getThisPlugin().getLanguage().getMessage("GENERAL.FROMWORLD",
                    land.getWorldName())).append(Config.NEWLINE);
            importDisplayPermsFrom(((Lands) ApiSecuboidSta.getLands()).getOutsideArea(land.getWorldName()), true);
            
            new ChatPage("COMMAND.PERMISSION.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

        } else {
            throw new SecuboidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    private void importDisplayPermsFrom(ApiDummyLand land, boolean onlyInherit) {
        
        boolean addToList = false;
        
        for (ApiPlayerContainer pc : land.getSetPCHavePermission()) {
            StringBuilder stSubList = new StringBuilder();
            
            for (ApiPermission perm : land.getPermissionsForPC(pc)) {
                if((!onlyInherit || perm.isHeritable()) && !permInList(pc, perm)) {
                    addToList = true;
                    stSubList.append(" ").append(perm.getPermType().getPrint()).append(":").append(perm.getValuePrint());
                }
            }
            
            // Append to list
            if(stSubList.length() > 0) {
                stList.append(ChatColor.WHITE).append(pc.getPrint()).append(":");
                stList.append(stSubList).append(Config.NEWLINE);
            }
            
        }
        
        if(addToList) {
            precDL.add(land);
        }
    }
    
    private boolean permInList(ApiPlayerContainer pc, ApiPermission perm) {
        
        for(ApiDummyLand listLand : precDL) {
            
            if(listLand.getSetPCHavePermission().contains(pc)) {
                for(ApiPermission listPerm : listLand.getPermissionsForPC(pc)) {
                    if(perm.getPermType() == listPerm.getPermType()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.secuboid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
            throws SecuboidCommandException {
        
        convertPcIfNeeded(playerCacheEntry);

        if (fonction.equalsIgnoreCase("set")) {

            ApiPermission perm = entity.argList.getPermissionFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));

            if(!perm.getPermType().isRegistered()) {
                throw new SecuboidCommandException("Permission not registered", entity.player, "COMMAND.PERMISSIONTYPE.TYPENULL");
            }
            
            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                    && perm.getValue() != perm.getPermType().getDefaultValue()
                    && land.isLocationInside(land.getWorld().getSpawnLocation())) {
                throw new SecuboidCommandException("Permission", entity.player, "COMMAND.PERMISSION.NOENTERNOTINSPAWN");
            }
            ((Land) land).addPermission(pc, perm);
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getPermType().getPrint(),
                    pc.getPrint() + ChatColor.YELLOW, land.getName()));
            Secuboid.getThisPlugin().getLog().write("Permission set: " + perm.getPermType().toString() + ", value: " + perm.getValue());

        } else if (fonction.equalsIgnoreCase("unset")) {

            ApiPermissionType pt = entity.argList.getPermissionTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            if (!land.removePermission(pc, pt)) {
                throw new SecuboidCommandException("Permission", entity.player, "COMMAND.PERMISSION.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
            Secuboid.getThisPlugin().getLog().write("Permission unset: " + pt.toString());
        }
    }
}
