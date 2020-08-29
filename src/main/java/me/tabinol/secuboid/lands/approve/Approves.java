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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.storage.StorageThread.SaveOn;
import org.bukkit.entity.Player;

/**
 * Approves
 */
public class Approves {

    private final Secuboid secuboid;
    private final Map<String, Approve> landNameToApprove;

    public Approves(final Secuboid secuboid) {
        this.secuboid = secuboid;
        // Must be in land order
        landNameToApprove = new TreeMap<>();
    }

    /**
     * Load all approves (Internal).
     *
     * @param approves approve list
     */
    public void loadApproves(final List<Approve> approves) {
        landNameToApprove.clear();
        approves.forEach(approve -> addApprove(approve, null));
    }

    /**
     * Adds the approve.
     *
     * @param approve        the approve
     * @param playerNullable the player for notification
     */
    public void addApprove(final Approve approve, final Player playerNullable) {
        final Land land = approve.getLand();

        // Verify if the areas still exist
        if ((approve.getRemovedAreaIdNullable() != null && land.getArea(approve.getRemovedAreaIdNullable()) == null)
                || (approve.getNewAreaIdNullable() != null && land.getArea(approve.getNewAreaIdNullable()) == null)) {
            secuboid.getLogger().log(Level.WARNING, () ->
                    "Skipping the approve because it referes to a non existing area [name=" + approve.getName() + "]");
            return;
        }

        landNameToApprove.put(approve.getName(), approve);

        if (playerNullable != null) {
            secuboid.getApproveNotif().notifyForApprove(approve.getLand().getName(), playerNullable.getDisplayName());
        }

        secuboid.getStorageThread().addSaveAction(SaveActionEnum.APPROVE_SAVE, SaveOn.BOTH, approve);
    }

    /**
     * Gets the approve from name.
     *
     * @param name the name
     * @return the approve instance
     */
    public Approve getApprove(final String name) {
        return landNameToApprove.get(name);
    }

    /**
     * Gets the approve list.
     *
     * @return the approve list
     */
    public Map<String, Approve> getApproveList() {
        return Collections.unmodifiableMap(landNameToApprove);
    }

    /**
     * Checks if is in approve.
     *
     * @param landName the land name
     * @return true, if is in approve
     */
    public boolean isInApprove(final String landName) {
        return landNameToApprove.containsKey(landName.toLowerCase());
    }

    /**
     * Removes the approve.
     *
     * @param approve the approve
     */
    public void removeApprove(final Approve approve) {
        removeApprove(approve, true);
    }

    public void removeApprove(final Approve approve, final boolean removeLandAndArea) {
        landNameToApprove.remove(approve.getName());
        if (removeLandAndArea) {
            removeLandAndAreaIfNeeded(approve);
        }
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.APPROVE_REMOVE, SaveOn.BOTH, approve);
    }

    /**
     * Removes the all approve.
     */
    public void removeAll() {
        for (final Approve approve : landNameToApprove.values()) {
            removeLandAndAreaIfNeeded(approve);
        }
        landNameToApprove.clear();
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.APPROVE_REMOVE_ALL, SaveOn.BOTH, null);
    }

    private void removeLandAndAreaIfNeeded(final Approve approve) {
        final Land land = approve.getLand();
        if (!land.isApproved()) {
            try {
                secuboid.getLands().removeLand(land);
            } catch (final SecuboidLandException e) {
                secuboid.getLogger().log(Level.WARNING, String.format(
                        "Unable to delete non approved land \"%s\", \"%s\"", land.getName(), land.getUUID()), e);
            }
        } else {
            if (approve.getNewAreaIdNullable() != null) {
                land.removeArea(approve.getNewAreaIdNullable());
            }
        }
    }

    /**
     * Creates the action (approve and execute).
     *
     * @param approve the approve
     * @throws SecuboidLandException SecuboidLandException
     */
    public void createAction(final Approve approve) throws SecuboidLandException {
        final LandAction action = approve.getAction();
        final Land land = approve.getLand();

        if (action != null) {
            // Remove approve before to prevent a foreign key constraint fails
            removeApprove(approve, false);

            switch (action) {
                case AREA_ADD:
                    land.approveArea(approve.getNewAreaIdNullable(), approve.getPrice());
                    break;
                case AREA_REMOVE:
                    land.removeArea(approve.getRemovedAreaIdNullable());
                    break;
                case AREA_MODIFY:
                    land.approveReplaceArea(approve.getRemovedAreaIdNullable(), approve.getNewAreaIdNullable(),
                            approve.getPrice());
                    break;
                case LAND_ADD:
                    land.setApproved(approve.getPrice());
                    break;
                case LAND_REMOVE:
                    secuboid.getLands().removeLand(approve.getLand());
                    break;
                case LAND_PARENT:
                    land.setParent(approve.getParentNullable());
                    break;
                default:
                    break;
            }
        }
    }
}