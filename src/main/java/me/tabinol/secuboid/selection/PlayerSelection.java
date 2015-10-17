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
package me.tabinol.secuboid.selection;

import java.util.Collection;
import java.util.EnumMap;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboidapi.config.players.IPlayerConfEntry;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboid.parameters.FlagList;
import me.tabinol.secuboidapi.parameters.IFlagType;
import me.tabinol.secuboid.selection.region.AreaSelection;
import me.tabinol.secuboid.selection.region.LandSelection;
import me.tabinol.secuboid.selection.region.RegionSelection;


/**
 * The Class PlayerSelection.
 */
public class PlayerSelection {

    /**
     * Selection Type.
     */
    public enum SelectionType { // ACTIVE = move with the player, PASSIVE = fixed

        /** The land. */
    	LAND,
        
        /** The area. */
        AREA;
    }

    /** The player conf entry. */
    private final IPlayerConfEntry playerConfEntry;
    
    /** The selection list. */
    private final EnumMap<SelectionType, RegionSelection> selectionList; // SelectionList for the player
    
    /** The area to replace. */
    ICuboidArea areaToReplace; // If it is an areaToReplace with an expand

    /**
     * Instantiates a new player selection.
     *
     * @param playerConfEntry the player conf entry
     */
    public PlayerSelection(IPlayerConfEntry playerConfEntry) {

        this.playerConfEntry = playerConfEntry;
        selectionList = new EnumMap<SelectionType, RegionSelection>(SelectionType.class);
        areaToReplace = null;
    }

    /**
     * Checks for selection.
     *
     * @return true, if successful
     */
    public boolean hasSelection() {

        return !selectionList.isEmpty();
    }

    /**
     * Gets the selections.
     *
     * @return the selections
     */
    public Collection<RegionSelection> getSelections() {

        return selectionList.values();
    }

    /**
     * Adds the selection.
     *
     * @param sel the sel
     */
    public void addSelection(RegionSelection sel) {

        selectionList.put(sel.getSelectionType(), sel);
    }

    /**
     * Gets the selection.
     *
     * @param type the type
     * @return the selection
     */
    public RegionSelection getSelection(SelectionType type) {

        return selectionList.get(type);
    }

    /**
     * Removes the selection.
     *
     * @param type the type
     * @return the region selection
     */
    public RegionSelection removeSelection(SelectionType type) {

        RegionSelection select = selectionList.remove(type);

        if (select != null) {

            // reset AreaToReplace if exist
            areaToReplace = null;

            select.removeSelection();
        }

        return select;
    }
    
    /**
     * Refresh land selection.
     */
    public void refreshLand() {
    	
    	ILand land = getLand();
    	
    	if(land !=null) {
    		removeSelection(SelectionType.LAND);
    		addSelection(new LandSelection(playerConfEntry.getPlayer(), land));
    	}
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public ILand getLand() {

        LandSelection sel = (LandSelection) selectionList.get(SelectionType.LAND);
        if (sel != null) {
            return sel.getLand();
        } else {
            return null;
        }
    }

    /**
     * Gets the cuboid area.
     *
     * @return the cuboid area
     */
    public ICuboidArea getCuboidArea() {

        AreaSelection sel = (AreaSelection) selectionList.get(SelectionType.AREA);
        if (sel != null) {
            return sel.getCuboidArea();
        } else {
            return null;
        }
    }

    /**
     * Sets the area to replace.
     *
     * @param areaToReplace the new area to replace
     */
    public void setAreaToReplace(ICuboidArea areaToReplace) {

        this.areaToReplace = areaToReplace;
    }

    /**
     * Gets the area to replace.
     *
     * @return the area to replace
     */
    public ICuboidArea getAreaToReplace() {

        return areaToReplace;
    }

    /**
     * Gets the land create price.
     *
     * @return the land create price
     */
    public double getLandCreatePrice() {

        if(!isPlayerMustPay()) {
            return 0;
        }

        ILand land = getLand();
        ICuboidArea area = getCuboidArea();
        Double priceFlag;
        IFlagType flagType = FlagList.ECO_BLOCK_PRICE.getFlagType();
        
        // Get land price
        if (land == null) {
            priceFlag = Secuboid.getThisPlugin().iLands().getOutsideArea(area.getWorldName())
            		.getFlagAndInherit(flagType).getValueDouble();
                    
        } else {
            priceFlag = land.getFlagAndInherit(flagType).getValueDouble();
        }

        // Not set, return 0
        if (priceFlag == 0D) {
            return 0;
        }

        return priceFlag * area.getTotalBlock();
    }

    /**
     * Gets the area add price.
     *
     * @return the area add price
     */
    public double getAreaAddPrice() {

        if(!isPlayerMustPay()) {
            return 0;
        }

        ILand land = getLand();
        ICuboidArea area = getCuboidArea();
        double priceFlag;
        IFlagType flagType = FlagList.ECO_BLOCK_PRICE.getFlagType();

        if(land == null) {
            return 0;
        }

        // The area is from parent ask parent
        if (land.getParent() == null) {
            priceFlag = Secuboid.getThisPlugin().iLands().getOutsideArea(area.getWorldName())
            		.getFlagAndInherit(flagType).getValueDouble();
        } else {
            priceFlag = land.getParent().getFlagAndInherit(flagType).getValueDouble();
        }

        // Not set, return 0
        if (priceFlag == 0D) {
            return 0;
        }

        // get total areas cube
        long nbCube = land.getNbBlocksOutside(area);

        return priceFlag * nbCube;
    }

    /**
     * Gets the area replace price.
     *
     * @param areaId the area id
     * @return the area replace price
     */
    public double getAreaReplacePrice(int areaId) {

        if(!isPlayerMustPay()) {
            return 0;
        }

        // Check only with Area add. No refound for reduced area.
        return getAreaAddPrice();
    }
    
    /**
     * Checks if is player must pay.
     *
     * @return true, if is player must pay
     */
    private boolean isPlayerMustPay() {
        
        // Is Economy?
        if (Secuboid.getThisPlugin().iPlayerMoney() == null
        		|| !Secuboid.getThisPlugin().iConf().useEconomy()
        		|| playerConfEntry.isAdminMod()) {
            return false;
        }

        return true;
    }
}
