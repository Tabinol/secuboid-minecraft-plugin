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
package me.tabinol.secuboid.lands.approve;

import java.util.Calendar;
import java.util.UUID;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.storage.Savable;

/**
 * The Class Approve.
 */
public class Approve implements Savable {

    /**
     * The action.
     */
    private final LandAction action;

    /**
     * The land.
     */
    private final Land land;

    /**
     * The removed area id.
     */
    private final Integer removedAreaIdNullable;

    /**
     * The new area.
     */
    private final Integer newAreaIdNullable;

    /**
     * The owner.
     */
    private final PlayerContainer owner;

    /**
     * The parent.
     */
    private final Land parentNullable;

    /**
     * The price.
     */
    private final double price;

    /**
     * The date time.
     */
    private final Calendar dateTime;

    /**
     * Instantiates a new approve.
     *
     * @param land                  the land (new or modified)
     * @param action                the action
     * @param removedAreaIdNullable the removed area id (optional)
     * @param newAreaIdNullable     the new area id (optional)
     * @param owner                 the requestor
     * @param parentNullable        the parent (optional)
     * @param price                 the price
     * @param dateTime              the date time
     */
    public Approve(final Land land, final LandAction action, final Integer removedAreaIdNullable,
                   final Integer newAreaIdNullable, final PlayerContainer owner, final Land parentNullable,
                   final double price, final Calendar dateTime) {

        this.action = action;
        this.land = land;
        this.removedAreaIdNullable = removedAreaIdNullable;
        this.newAreaIdNullable = newAreaIdNullable;
        this.owner = owner;
        this.parentNullable = parentNullable;
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
     * Gets the land.
     *
     * @return the land
     */
    public Land getLand() {
        return land;
    }

    /**
     * Gets the removed area id.
     *
     * @return the removed area id (optional)
     */
    public Integer getRemovedAreaIdNullable() {
        return removedAreaIdNullable;
    }

    /**
     * Gets the new area.
     *
     * @return the new area (optional)
     */
    public Integer getNewAreaIdNullable() {
        return newAreaIdNullable;
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public PlayerContainer getOwner() {
        return owner;
    }

    /**
     * Gets the parent.
     *
     * @return the parent land (optional)
     */
    public Land getParentNullable() {
        return parentNullable;
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

    @Override
    public String getName() {
        return land.getName();
    }

    @Override
    public UUID getUUID() {
        return land.getUUID();
    }
}
