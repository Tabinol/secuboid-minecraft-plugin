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
package me.tabinol.secuboid.lands.approve;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboidapi.lands.ILand;
import me.tabinol.secuboidapi.lands.areas.ICuboidArea;
import me.tabinol.secuboidapi.lands.types.IType;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboidapi.playercontainer.IPlayerContainer;


/**
 * The Class Approve.
 */
public class Approve {
    
    /** The action. */
    private final LandAction action;
    
    /** The land name. */
    private final String landName;
    
    /** The type */
    private final IType type;
    
    /** The removed area id. */
    private final int removedAreaId;
    
    /** The new area. */
    private final ICuboidArea newArea;
    
    /** The owner. */
    private final IPlayerContainer owner;
    
    /** The parent. */
    private final ILand parent;
    
    /** The price. */
    private final double price;
    
    /** The date time. */
    private final Calendar dateTime;
    
    /**
     * Instantiates a new approve.
     *
     * @param landName the land name
     * @param type the type
     * @param action the action
     * @param removedAreaId the removed area id
     * @param newArea the new area
     * @param owner the owner
     * @param parent the parent
     * @param price the price
     * @param dateTime the date time
     */
    public Approve(String landName, IType type, LandAction action, int removedAreaId, 
            ICuboidArea newArea, IPlayerContainer owner, ILand parent, double price,
            Calendar dateTime) {
        
        this.action = action;
        this.landName = landName.toLowerCase();
        this.type = type;
        this.removedAreaId = removedAreaId;
        this.newArea = newArea;
        this.owner = owner;
        this.parent = parent;
        this.price = price;
        this.dateTime = dateTime;
    }

    /**
     * Gets the action.
     *
     * @return the action
     */
    public LandAction getAction() {
        
        return action;
    }
    
    /**
     * Gets the land name.
     *
     * @return the land name
     */
    public String getLandName() {
        
        return landName;
    }
    
    /**
     * Gets the type.
     *
     * @return the type
     */
    public IType getType() {
    	
    	return type;
    }
    
    /**
     * Gets the removed area id.
     *
     * @return the removed area id
     */
    public int getRemovedAreaId() {
        
        return removedAreaId;
    }
    
    /**
     * Gets the new area.
     *
     * @return the new area
     */
    public ICuboidArea getNewArea() {
        
        return newArea;
    }
    
    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public IPlayerContainer getOwner() {
        
        return owner;
    }
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public ILand getParent() {
        
        return parent;
    }
    
    /**
     * Gets the price.
     *
     * @return the price
     */
    public double getPrice() {
        
        return price;
    }
    
    /**
     * Gets the date time.
     *
     * @return the date time
     */
    public Calendar getDateTime() {
        
        return dateTime;
    }
    
    /**
     * Creates the action.
     */
    public void createAction() {
        
        if(action == LandAction.AREA_ADD) {
            Secuboid.getThisPlugin().iLands().getLand(landName).addArea(newArea, price);
        } else if(action == LandAction.AREA_REMOVE) {
            Secuboid.getThisPlugin().iLands().getLand(landName).removeArea(removedAreaId);
        } else if(action == LandAction.AREA_MODIFY) {
            Secuboid.getThisPlugin().iLands().getLand(landName).replaceArea(removedAreaId, newArea, price);
        } else if(action == LandAction.LAND_ADD) {
            try {
                Secuboid.getThisPlugin().iLands().createLand(landName, owner, newArea, parent, price, type);
            } catch (SecuboidLandException ex) {
                Logger.getLogger(Approve.class.getName()).log(Level.SEVERE, "On land create", ex);
            }
        } else if(action == LandAction.LAND_REMOVE) {
            try {
                Secuboid.getThisPlugin().iLands().removeLand(landName);
            } catch (SecuboidLandException ex) {
                Logger.getLogger(Approve.class.getName()).log(Level.SEVERE, "On land remove", ex);
            }
        } else if(action == LandAction.LAND_PARENT) {
        	Secuboid.getThisPlugin().iLands().getLand(landName).setParent(parent);
        }
    }
}
