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
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.Config;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboidapi.SecuboidAPI;
import me.tabinol.secuboidapi.lands.IDummyLand;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.parameters.IFlagType;
import me.tabinol.secuboidapi.parameters.ILandFlag;

import org.bukkit.ChatColor;


/**
 * The Class CommandFlag.
 */
@InfoCommand(name="flag", forceParameter=true)
public class CommandFlag extends CommandExec {
	
	private LinkedList<IDummyLand> precDL; // Listed Precedent lands (no duplicates)
	private StringBuilder stList;

    /**
     * Instantiates a new command flag.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandFlag(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        checkSelections(true, null);
                String curArg = entity.argList.getNext();

        /*
        if (entity.argList.length() < 2) {

            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
            Secuboid.getThisPlugin().iLog().write("PlayerSetFlagUI for " + entity.playerName);
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.HINT"));
            CuboidArea area = Secuboid.getThisPlugin().iLands().getCuboidArea(entity.player.getLocation());
            LandSetFlag setting = new LandSetFlag(entity.player, area);
            entity.playerConf.setSetFlagUI(setting);
            
                    
        } else 
        */ 
        
        if (curArg.equalsIgnoreCase("set")) {

            // Permission check is on getFlagFromArg
            
            ILandFlag landFlag = entity.argList.getFlagFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            
            if(!landFlag.getFlagType().isRegistered()) {
            	throw new SecuboidCommandException("Flag not registered", entity.player, "COMMAND.FLAGS.FLAGNULL");
            }
            
            ((Land)land).addFlag(landFlag);
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + 
            Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), 
                    landFlag.getValue().getValuePrint() + ChatColor.YELLOW));
            Secuboid.getThisPlugin().iLog().write("Flag set: " + landFlag.getFlagType().toString() + ", value: " + 
                    landFlag.getValue().getValue().toString());

        } else if (curArg.equalsIgnoreCase("unset")) {
        
            IFlagType flagType = entity.argList.getFlagTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            if (!land.removeFlag(flagType)) {
                throw new SecuboidCommandException("Flags", entity.player, "COMMAND.FLAGS.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
            Secuboid.getThisPlugin().iLog().write("Flag unset: " + flagType.toString());
        
        } else if (curArg.equalsIgnoreCase("list")) {

        	precDL = new LinkedList<IDummyLand>();
        	stList = new StringBuilder();
        	
        	// For the actual land
        	importDisplayFlagsFrom(land, false);
        	
        	// For default Type
        	if(land.getType() != null) {
            	stList.append(ChatColor.DARK_GRAY + Secuboid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMDEFAULTTYPE",
        				land.getType().getName())).append(Config.NEWLINE);
            	importDisplayFlagsFrom(((Lands) SecuboidAPI.iLands()).getDefaultConf(land.getType()), false);
        	}
        	
        	// For parent (if exist)
        	ILand parLand = land;
        	while((parLand = parLand.getParent()) != null) {
        		stList.append(ChatColor.DARK_GRAY + Secuboid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMPARENT",
        				ChatColor.GREEN + parLand.getName() + ChatColor.DARK_GRAY)).append(Config.NEWLINE);
        		importDisplayFlagsFrom(parLand, true);
        	}
        	
        	// For world
        	stList.append(ChatColor.DARK_GRAY + Secuboid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMWORLD",
    				land.getWorldName())).append(Config.NEWLINE);
        	importDisplayFlagsFrom(((Lands) SecuboidAPI.iLands()).getOutsideArea(land.getWorldName()), true);
                
            new ChatPage("COMMAND.FLAGS.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

        } else {
            throw new SecuboidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
    
    private void importDisplayFlagsFrom(IDummyLand land, boolean onlyInherit) {
    	
    	StringBuilder stSubList = new StringBuilder();
    	for (ILandFlag flag : land.getFlags()) {
            if (stSubList.length() != 0 && !stSubList.toString().endsWith(" ")) {
                stSubList.append(" ");
            }
            if((!onlyInherit || flag.isHeritable()) && !flagInList(flag)) {
                stSubList.append(flag.getFlagType().getPrint()).append(":").append(flag.getValue().getValuePrint());
            }
        }
        
    	if(stSubList.length() > 0) {
        	stList.append(stSubList).append(Config.NEWLINE);
        	precDL.add(land);
    	}
    }
    
    private boolean flagInList(ILandFlag flag) {
    	
    	for(IDummyLand listLand : precDL) {
    		for(ILandFlag listFlag : listLand.getFlags()) {
    			if(flag.getFlagType() == listFlag.getFlagType()) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
}
