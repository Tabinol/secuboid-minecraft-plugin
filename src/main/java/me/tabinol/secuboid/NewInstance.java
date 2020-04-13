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
package me.tabinol.secuboid;

import java.util.ArrayList;
import java.util.UUID;

import me.tabinol.secuboid.lands.areas.*;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.FlagType;
import me.tabinol.secuboid.permissionsflags.FlagValue;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerEverybody;
import me.tabinol.secuboid.playercontainer.PlayerContainerGroup;
import me.tabinol.secuboid.playercontainer.PlayerContainerNobody;
import me.tabinol.secuboid.playercontainer.PlayerContainerOwner;
import me.tabinol.secuboid.playercontainer.PlayerContainerPermission;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayerName;
import me.tabinol.secuboid.playercontainer.PlayerContainerResident;
import me.tabinol.secuboid.playercontainer.PlayerContainerTenant;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.selection.visual.VisualSelection;
import me.tabinol.secuboid.selection.visual.VisualSelectionCuboid;
import me.tabinol.secuboid.selection.visual.VisualSelectionCylinder;
import me.tabinol.secuboid.selection.visual.VisualSelectionRoad;
import me.tabinol.secuboid.utilities.StringChanges;
import org.bukkit.entity.Player;

/**
 * Creates new instance of some classes.
 *
 * @author tabinol
 */
public class NewInstance {

    private final Secuboid secuboid;

    public NewInstance(final Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    /**
     * Creates the player container
     *
     * @param pct  the pct
     * @param name the name
     * @return the player container
     */
    public PlayerContainer createPlayerContainer(final PlayerContainerType pct, final String name) {

        if (null != pct) {
            switch (pct) {
            case GROUP:
                return new PlayerContainerGroup(secuboid, name);
            case RESIDENT:
                return PlayerContainerResident.getInstance();
            case OWNER:
                return PlayerContainerOwner.getInstance();
            case EVERYBODY:
                return PlayerContainerEverybody.getInstance();
            case NOBODY:
                return PlayerContainerNobody.getInstance();
            case PLAYER:
            case PLAYERNAME:
                UUID minecraftUUID;

                // First check if the ID is valid or was connected to the server
                try {
                    minecraftUUID = UUID.fromString(name.replaceFirst("ID-", ""));
                } catch (final IllegalArgumentException ex) {

                    // If there is an error, just return a temporary PlayerName
                    return new PlayerContainerPlayerName(name);
                }

                // If not null, assign the value to a new PlayerContainer
                return new PlayerContainerPlayer(secuboid, minecraftUUID);
            case PERMISSION:
                return new PlayerContainerPermission(name);
            case TENANT:
                return PlayerContainerTenant.getInstance();
            default:
                break;
            }
        }
        return null;
    }

    /**
     * Gets the player container from string.
     *
     * @param string the player container from string
     * @return the string
     */
    public PlayerContainer getPlayerContainerFromFileFormat(final String string) {

        final String strs[] = StringChanges.splitAddVoid(string, ":");
        final PlayerContainerType type = PlayerContainerType.getFromString(strs[0]);
        return createPlayerContainer(type, strs[1]);
    }

    /**
     * Gets the flag from file format.
     *
     * @param str the str
     * @return the from string
     */
    public Flag getFlagFromFileFormat(final String str) {

        final String[] multiStr = StringChanges.splitKeepQuote(str, ":");
        final FlagType ft = secuboid.getPermissionsFlags().getFlagTypeNoValid(multiStr[0]);
        final Object value = getFlagValueFromFileFormat(multiStr[1], ft);

        return secuboid.getPermissionsFlags().newFlag(ft, value, Boolean.parseBoolean(multiStr[2]));
    }

    /**
     * Gets the flag value from file format.
     *
     * @param str the string
     * @param ft  the flag type
     * @return the flag value
     */
    public FlagValue getFlagValueFromFileFormat(final String str, final FlagType ft) {

        FlagValue value;

        if (ft.isRegistered()) {
            if (ft.getDefaultValue().getValue() instanceof Boolean) {
                final String[] strs = str.split(" ");
                value = new FlagValue(Boolean.parseBoolean(strs[0]));
            } else if (ft.getDefaultValue().getValue() instanceof Double) {
                final String[] strs = str.split(" ");
                value = new FlagValue(Double.parseDouble(strs[0]));
            } else if (ft.getDefaultValue().getValue() instanceof String) {
                value = new FlagValue(StringChanges.fromQuote(str));
            } else if (ft.getDefaultValue().getValue() instanceof String[]) {
                final ArrayList<String> result = new ArrayList<String>();
                final String[] strs = StringChanges.splitKeepQuote(str, ";");
                for (final String st : strs) {
                    result.add(StringChanges.fromQuote(st));
                }
                value = new FlagValue(result.toArray(new String[result.size()]));
            } else {
                value = null;
            }
        } else {

            // not registered save raw information
            value = new FlagValue(str);
        }

        return value;
    }

    /**
     * Create a new visual selection from default
     *
     * @param areaType   areaType
     * @param isFromLand is from land or must be false
     * @param player     the player
     * @return visual selection
     */
    public VisualSelection createVisualSelection(final AreaType areaType, final boolean isFromLand,
            final Player player) {

        switch (areaType) {
        case CUBOID:
            return new VisualSelectionCuboid(secuboid, null, null, isFromLand, player);
        case CYLINDER:
            return new VisualSelectionCylinder(secuboid, null, null, isFromLand, player);
        case ROAD:
            return new VisualSelectionRoad(secuboid, null, null, isFromLand, player);
        default:
            return null;
        }
    }

    /**
     * Create a visual selection from an area
     *
     * @param area         area
     * @param originalArea the original area from a land for expand (in this case,
     *                     area must be a copy of)
     * @param isFromLand   is from land or must be false
     * @param player       the player
     * @return visual selection
     */
    public VisualSelection createVisualSelection(final Area area, final Area originalArea, final boolean isFromLand,
            final Player player) {

        switch (area.getAreaType()) {
        case CUBOID:
            return new VisualSelectionCuboid(secuboid, (CuboidArea) area, (CuboidArea) originalArea, isFromLand,
                    player);
        case CYLINDER:
            return new VisualSelectionCylinder(secuboid, (CylinderArea) area, (CylinderArea) originalArea, isFromLand,
                    player);
        case ROAD:
            return new VisualSelectionRoad(secuboid, (RoadArea) area, (RoadArea) originalArea, isFromLand, player);
        default:
            return null;
        }
    }
}
