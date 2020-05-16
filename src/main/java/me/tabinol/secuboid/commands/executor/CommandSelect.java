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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ArgList;
import me.tabinol.secuboid.commands.InfoCommand;
import me.tabinol.secuboid.commands.InfoCommand.CompletionMap;
import me.tabinol.secuboid.exceptions.SecuboidCommandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaType;
import me.tabinol.secuboid.lands.collisions.Collisions;
import me.tabinol.secuboid.permissionsflags.PermissionList;
import me.tabinol.secuboid.selection.PlayerSelection.SelectionType;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.LandSelection;

/**
 * The Class CommandSelect.
 */
@InfoCommand(name = "select", aliases = { "sel" }, //
        completion = { //
                @CompletionMap(regex = "^$", completions = { "cub", "cuboid", "cyl", "cylinder", "rea", "read", "exp",
                        "expand", "ret", "retract", "mov", "move", "lan", "land", "are", "area", "done", "info", "we",
                        "worldedit", "here", "@land" }) //
        })
public final class CommandSelect extends CommandCollisionsThreadExec {

    /**
     * The location.
     */
    private final Location location;

    /**
     * Instantiates a new command select.
     *
     * @param secuboid    secuboid instance
     * @param infoCommand the info command
     * @param sender      the sender
     * @param argList     the arg list
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelect(final Secuboid secuboid, final InfoCommand infoCommand, final CommandSender sender,
            final ArgList argList) throws SecuboidCommandException {

        super(secuboid, infoCommand, sender, argList);
        location = null;
    }

    /**
     * Instantiates a new command select (from a player action).
     *
     * @param secuboid secuboid instance
     * @param player   the player
     * @param argList  the arg list
     * @param location the location
     * @throws SecuboidCommandException the secuboid command exception
     */
    public CommandSelect(final Secuboid secuboid, final Player player, final ArgList argList, final Location location)
            throws SecuboidCommandException {

        super(secuboid, null, player, argList);
        this.location = location;
    }

    @Override
    public void commandExecute() throws SecuboidCommandException {

        // Done nothing but for future use
        checkSelections(null, null);

        String curArg;

        if (playerConf.getSelection().getArea() == null) {

            if (!argList.isLast()) {

                curArg = argList.getNext();
                final String curArg2 = argList.isLast() ? curArg : argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit") || curArg.equalsIgnoreCase("we")) {
                    if (secuboid.getDependPlugin().getWorldEdit() == null) {
                        throw new SecuboidCommandException(secuboid, "CommandSelect", player,
                                "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new CommandSelectWorldedit(secuboid, player, playerConf).MakeSelect();

                } else if (curArg.toLowerCase().matches("^cub(oid)?")) {
                    doVisualActiveSelect(AreaType.CUBOID, AreaSelection.MoveType.EXPAND);
                } else if (curArg.toLowerCase().matches("^cyl(inder)?")) {
                    doVisualActiveSelect(AreaType.CYLINDER, AreaSelection.MoveType.EXPAND);
                } else if (curArg.toLowerCase().matches("^roa(d)?")) {
                    doVisualActiveSelect(AreaType.ROAD, AreaSelection.MoveType.EXPAND);
                } else if (curArg.toLowerCase().matches("^exp(and)?")) {
                    doVisualActiveSelect(AreaType.CUBOID, AreaSelection.MoveType.EXPAND);
                } else if (curArg.toLowerCase().matches("^ret(ract)?")) {
                    doVisualActiveSelect(AreaType.CUBOID, AreaSelection.MoveType.RETRACT);
                } else if (curArg.toLowerCase().matches("^mov(e)?")) {
                    doVisualActiveSelect(AreaType.CUBOID, AreaSelection.MoveType.MOVE);
                } else if (curArg.toLowerCase().matches("^lan(d)?")) {
                    doSelectLand(curArg2);
                } else if (curArg.toLowerCase().matches("^are(a)?")) {
                    doSelectArea(curArg2);
                } else {
                    doSelectLand(curArg);
                }
            } else {
                doVisualActiveSelect(AreaType.CUBOID, AreaSelection.MoveType.EXPAND);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {
            doSelectAreaDone();
        } else if (curArg != null && curArg.toLowerCase().matches("^exp(and)?")) {
            changeVisualActiveSelect(AreaSelection.MoveType.EXPAND);
        } else if (curArg != null && curArg.toLowerCase().matches("^ret(ract)?")) {
            changeVisualActiveSelect(AreaSelection.MoveType.RETRACT);
        } else if (curArg != null && curArg.toLowerCase().matches("^mov(e)?")) {
            changeVisualActiveSelect(AreaSelection.MoveType.MOVE);
        } else if (curArg != null && curArg.equalsIgnoreCase("info")) {
            doSelectAreaInfo();
        } else {
            throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    private void doSelectLand(final String curArg) throws SecuboidCommandException {
        Land landtest;

        // If land is already selected, select an area, not a land
        if (playerConf.getSelection().getLand() != null) {
            doSelectArea(curArg);
            return;
        }

        if (curArg.equalsIgnoreCase("here")) {
            landtest = doSelectHere();
        } else {
            landtest = secuboid.getLands().getLand(curArg);
        }
        if (landtest == null || !landtest.isApproved()) {
            throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.NOLAND");

        }
        final boolean isResidentManager = landtest.getPermissionsFlags().checkPermissionAndInherit(player,
                PermissionList.RESIDENT_MANAGER.getPermissionType());

        // (isResidentManager && landtest.isResident(player): Bug exploitation found by
        // Geo_Log: Add resident for a non resident
        if (!(landtest.isOwner(player) || playerConf.isAdminMode()
                || (isResidentManager && landtest.isResident(player)))) {
            throw new SecuboidCommandException(secuboid, "CommandSelect", player, "GENERAL.MISSINGPERMISSION");
        }

        if (playerConf.getSelection().getLand() == null) {
            playerConf.getSelection().addSelection(new LandSelection(secuboid, player, landtest));
            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.SELECTEDLAND", landtest.getName()));
            playerConf.setAutoCancelSelect(true);
        } else {
            player.sendMessage(ChatColor.RED + "[Secuboid] " + ChatColor.DARK_GRAY
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
        }
    }

    private void doSelectArea(final String curArg) throws SecuboidCommandException {

        final Land landtest = playerConf.getSelection().getLand();
        Area areaSelect;

        if (landtest == null) {
            throw new SecuboidCommandException(secuboid, "CommandSelect", player, "GENERAL.JOIN.SELECTMODE");
        }

        if (!playerConf.isAdminMode() && !landtest.isOwner(player)) {
            throw new SecuboidCommandException(secuboid, "CommandSelect", player, "GENERAL.MISSINGPERMISSION");
        }

        try {
            final int areaNb = Integer.parseInt(curArg);
            areaSelect = landtest.getArea(areaNb);
        } catch (final NumberFormatException ex) {
            // this not a number, take the areaNb where the player is
            areaSelect = doSelectAreaHere(landtest);
        }

        if (areaSelect == null || !areaSelect.isApproved()) {
            throw new SecuboidCommandException(secuboid, "CommandSelect", player, "COMMAND.SELECT.NOLAND");
        }

        if (playerConf.getSelection().getArea() == null) {
            playerConf.getSelection().addSelection(new AreaSelection(secuboid, player, areaSelect.copyOf(), areaSelect,
                    true, areaSelect.getAreaType(), AreaSelection.MoveType.EXPAND));
            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY + secuboid.getLanguage()
                    .getMessage("COMMAND.SELECT.SELECTEDAREA", String.valueOf(areaSelect.getKey())));
            playerConf.setAutoCancelSelect(true);
        } else {
            player.sendMessage(ChatColor.RED + "[Secuboid] " + ChatColor.DARK_GRAY
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
        }
    }

    private Land doSelectHere() {
        Land landtest;

        // add select Here to select the the cuboid
        if (location != null) {
            // With an item
            landtest = secuboid.getLands().getLand(location);
        } else {
            // Player location
            landtest = secuboid.getLands().getLand(player.getLocation());
        }

        return landtest;
    }

    private Area doSelectAreaHere(final Land landtest) {

        Area areatest;

        // add select Here to select the the cuboid
        if (location != null) {
            // With an item
            areatest = secuboid.getLands().getArea(location);
        } else {
            // Player location
            areatest = secuboid.getLands().getArea(player.getLocation());
        }

        if (areatest == null) {
            return null;
        }

        return areatest.getLand() == landtest ? areatest : null;
    }

    private void doVisualActiveSelect(final AreaType areaType, final AreaSelection.MoveType moveType) {

        player.sendMessage(
                ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
        player.sendMessage(
                ChatColor.DARK_GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.HINT",
                        ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
        final AreaSelection select = new AreaSelection(secuboid, player, null, null, true, areaType, moveType);
        playerConf.getSelection().addSelection(select);
        playerConf.setAutoCancelSelect(true);
    }

    private void changeVisualActiveSelect(final AreaSelection.MoveType moveType) {

        player.sendMessage(
                ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
        player.sendMessage(
                ChatColor.DARK_GRAY + "[Secuboid] " + secuboid.getLanguage().getMessage("COMMAND.SELECT.HINT",
                        ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
        final AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        playerConf.getSelection()
                .addSelection(new AreaSelection(secuboid, player, select.getVisualSelection().getArea(),
                        select.getVisualSelection().getOriginalArea(), true, null, moveType));
        playerConf.setAutoCancelSelect(true);
    }

    /**
     * Do select area done.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void doSelectAreaDone() throws SecuboidCommandException {

        checkSelections(null, true);

        final AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        playerConf.getSelection()
                .addSelection(new AreaSelection(secuboid, player, select.getVisualSelection().getArea(),
                        select.getVisualSelection().getOriginalArea(), true, null, AreaSelection.MoveType.PASSIVE));
        playerConf.setAutoCancelSelect(true);

        if (!select.getVisualSelection().hasCollision()) {

            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.DARK_GRAY
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
        } else {
            player.sendMessage(ChatColor.GREEN + "[Secuboid] " + ChatColor.RED
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
        }
    }

    /**
     * Do select area info.
     *
     * @throws SecuboidCommandException the secuboid command exception
     */
    private void doSelectAreaInfo() throws SecuboidCommandException {

        checkSelections(null, true);

        final AreaSelection areaSelection = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        final Area area = areaSelection.getVisualSelection().getArea();

        player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                + secuboid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO1", area.getPrint()));
        if (area.getVolume() != 0) {
            player.sendMessage(ChatColor.YELLOW + "[Secuboid] "
                    + secuboid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO2", area.getVolume() + ""));
        }

        final LandSelection landSelection = (LandSelection) playerConf.getSelection().getSelection(SelectionType.LAND);

        if (landSelection != null) {
            final Land land = landSelection.getLand();
            final Area originalArea = areaSelection.getVisualSelection().getOriginalArea();
            if (originalArea != null) {
                // Area replace
                checkCollision(area.getWorldName(), land.getName(), land, null, Collisions.LandAction.AREA_MODIFY,
                        originalArea.getKey(), playerConf.getSelection().getArea(), land.getParent(), land.getOwner(),
                        false);
            } else {
                // Area add
                checkCollision(area.getWorldName(), land.getName(), land, null, Collisions.LandAction.AREA_ADD, 0,
                        playerConf.getSelection().getArea(), land.getParent(), land.getOwner(), false);
            }
        } else {
            // Land create
            final LandCheckValues landCheckValues = landCheckForCreate(areaSelection);
            checkCollision(area.getWorldName(), null, null, landCheckValues.localType, Collisions.LandAction.LAND_ADD,
                    0, area, landCheckValues.realLocalParent, landCheckValues.localOwner, true);
        }
    }

    @Override
    public void commandThreadExecute(final Collisions collisions) throws SecuboidCommandException {
        // Info only
        final double price = collisions.getPrice();

        // Price (economy)
        if (price > 0d) {
            switch (collisions.getAction()) {
                case AREA_MODIFY:
                case AREA_ADD:
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage(
                            "COMMAND.SELECT.INFO.INFO4", secuboid.getPlayerMoneyOpt().get().toFormat(price)));
                    break;
                case LAND_ADD:
                    player.sendMessage(ChatColor.YELLOW + "[Secuboid] " + secuboid.getLanguage().getMessage(
                            "COMMAND.SELECT.INFO.INFO3", secuboid.getPlayerMoneyOpt().get().toFormat(price)));
                    break;
                default:
            }
        }
    }
}
