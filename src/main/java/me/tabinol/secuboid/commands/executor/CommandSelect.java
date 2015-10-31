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

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.CommandEntities;
import me.tabinol.secuboid.commands.CommandExec;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.config.players.PlayerConfEntry;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboidapi.lands.ApiLand;
import me.tabinol.secuboidapi.lands.areas.ApiCuboidArea;
import me.tabinol.secuboid.parameters.PermissionList;
import me.tabinol.secuboidapi.playercontainer.ApiPlayerContainer;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.ActiveAreaSelection;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.LandSelection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * The Class CommandSelect.
 */
@InfoCommand(name="select")
public class CommandSelect extends CommandExec {

    /** The player. */
    private final Player player;
    
    /** The location. */
    private final Location location;
    
    /** The player conf. */
    private final PlayerConfEntry playerConf;
    
    /** The arg list. */
    private final ArgList argList;

    /**
     * Instantiates a new command select.
     *
     * @param entity the entity
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelect(CommandEntities entity) throws SecuboidCommandException {

        super(entity);
        player = entity.player;
        location = null;
        playerConf = entity.playerConf;
        argList = entity.argList;
    }

    // Called from player action, not a command
    /**
     * Instantiates a new command select.
     *
     * @param player the player
     * @param argList the arg list
     * @param location the location
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelect(Player player, ArgList argList, Location location) throws SecuboidCommandException {

        super(null);
        this.player = player;
        this.location = location;
        playerConf = Secuboid.getThisPlugin().getPlayerConf().get(player);
        this.argList = argList;
    }

    /* (non-Javadoc)
     * @see me.tabinol.secuboid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws SecuboidCommandException {

        // Done nothing but for future use
        checkSelections(null, null);

        String curArg;

        if (playerConf.getSelection().getCuboidArea() == null) {
            Secuboid.getThisPlugin().getLog().write(player.getName() + " join select mode");

            if (!argList.isLast()) {

                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (Secuboid.getThisPlugin().getDependPlugin().getWorldEdit() == null) {
                        throw new SecuboidCommandException("CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new CommandSelectWorldedit(player, playerConf).MakeSelect();

                } else {

                    ApiLand landtest;
                    if (curArg.equalsIgnoreCase("here")) {

                        // add select Here to select the the cuboid
                        if (location != null) {

                            // With an item
                            landtest = Secuboid.getThisPlugin().getLands().getLand(location);
                        } else {

                            // Player location
                            landtest = Secuboid.getThisPlugin().getLands().getLand(player.getLocation());
                        }

                    } else {

                        landtest = Secuboid.getThisPlugin().getLands().getLand(curArg);
                    }

                    if (landtest == null) {
                        throw new SecuboidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");

                    }
                    ApiPlayerContainer owner = landtest.getOwner();

                    if (!owner.hasAccess(player) && !playerConf.isAdminMod()
                            && !(landtest.checkPermissionAndInherit(player, PermissionList.RESIDENT_MANAGER.getPermissionType())
                                    && (landtest.isResident(player) || landtest.isOwner(player)))) {
                        throw new SecuboidCommandException("CommandSelect", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (playerConf.getSelection().getLand() == null) {

                        playerConf.getSelection().addSelection(new LandSelection(player, landtest));

                        player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.SELECTEDLAND", landtest.getName()));
                        playerConf.setAutoCancelSelect(true);
                    } else {

                        player.sendMessage(ChatColor.RED + "[Secuboid] " + ChatColor.DARK_GRAY + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
                    }
                }
            } else {

                player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.sendMessage(ChatColor.DARK_GRAY + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
                ActiveAreaSelection select = new ActiveAreaSelection(player);
                playerConf.getSelection().addSelection(select);
                playerConf.setAutoCancelSelect(true);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {

            //if (playerConf.getSelection().getLand() != null) {
            //    throw new SecuboidCommandException("CommandSelect", player, "COMMAND.SELECT.CANTDONE");
            //}

            //if (playerConf.getSelection().getCuboidArea() != null) {
                doSelectAreaDone();
            //}

        } else if (curArg != null && curArg.equalsIgnoreCase("info")) {

            doSelectAreaInfo();

        } else {
            throw new SecuboidCommandException("CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    /**
     * Do select area done.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void doSelectAreaDone() throws SecuboidCommandException {

        checkSelections(null, true);

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        playerConf.getSelection().addSelection(new AreaSelection(player, select.getCuboidArea()));
        playerConf.setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
                    + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
        } else {
            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.RED
                    + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
        }
    }

    /**
     * Do select area info.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void doSelectAreaInfo() throws SecuboidCommandException {

        checkSelections(null, true);

        double price;

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        ApiCuboidArea area = select.getCuboidArea();

        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.INFO.INFO1",
                area.getPrint()));
        player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.INFO.INFO2",
                area.getTotalBlock() + ""));

        // Price (economy)
        price = playerConf.getSelection().getLandCreatePrice();
        if (price != 0L) {
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.INFO.INFO3",
                    Secuboid.getThisPlugin().getPlayerMoney().toFormat(price)));
        }
        price = playerConf.getSelection().getAreaAddPrice();
        if (price != 0L) {
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + Secuboid.getThisPlugin().getLanguage().getMessage("COMMAND.SELECT.INFO.INFO4",
                    Secuboid.getThisPlugin().getPlayerMoney().toFormat(price)));
        }
    }
}
