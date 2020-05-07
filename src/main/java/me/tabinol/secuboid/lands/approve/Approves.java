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
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.storage.StorageThread.SaveOn;

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
        approves.stream().forEach(this::addApprove);
    }

    /**
     * Adds the approve.
     *
     * @param approve the approve
     */
    public void addApprove(final Approve approve) {
        landNameToApprove.put(approve.getName(), approve);
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.APPROVE_SAVE, SaveOn.BOTH, Optional.of(approve));
    }

    /**
     * Gets the approve from name.
     *
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
        landNameToApprove.remove(approve.getName());
        removeLandIfNeeded(approve);
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.APPROVE_REMOVE, SaveOn.BOTH, Optional.of(approve));
    }

    /**
     * Removes the all approve.
     */
    public void removeAll() {
        for (final Approve approve : landNameToApprove.values()) {
            removeLandIfNeeded(approve);
        }
        landNameToApprove.clear();
        secuboid.getStorageThread().addSaveAction(SaveActionEnum.APPROVE_REMOVE_ALL, SaveOn.BOTH, Optional.empty());
    }

    private final void removeLandIfNeeded(final Approve approve) {
        final Land land = approve.getLand();
        if (!land.isApproved()) {
            try {
                secuboid.getLands().removeLand(land);
            } catch (final SecuboidLandException e) {
                secuboid.getLogger().log(Level.WARNING, String.format(
                        "Unable to delete non approved land \"%s\", \"%s\"", land.getName(), land.getUUID()), e);
            }
        }
    }

    /**
     * Creates the action (approve and execute).
     */
    public void createAction(final String landName) throws SecuboidLandException {
        final String landNameLower = landName.toLowerCase();
        final Approve approve = landNameToApprove.get(landNameLower);
        final LandAction action = approve.getAction();
        final Land land = approve.getLand();

        if (action != null) {
            switch (action) {
                case AREA_ADD:
                    land.approveArea(approve.getNewAreaIdOpt().get(), approve.getPrice());
                    break;
                case AREA_REMOVE:
                    land.removeArea(approve.getRemovedAreaIdOpt().get());
                    break;
                case AREA_MODIFY:
                    land.approveReplaceArea(approve.getRemovedAreaIdOpt().get(), approve.getNewAreaIdOpt().get(),
                            approve.getPrice());
                    break;
                case LAND_ADD:
                    land.setApproved(approve.getPrice());
                    break;
                case LAND_REMOVE:
                    secuboid.getLands().removeLand(landNameLower);
                    break;
                case LAND_PARENT:
                    land.setParent(approve.getParentOpt().get());
                    break;
                default:
                    break;
            }
        }
    }
}