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
package me.tabinol.secuboid.storage.flat;

import java.util.List;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.storage.Storage;

/**
 * The Class StorageFlat.
 */
public class StorageFlat implements Storage {

    private final LandsFlat landsFlat;
    private final ApprovesFlat approvesFlat;

    public StorageFlat(final LandsFlat landsFlat, final ApprovesFlat approvesFlat) {
        this.landsFlat = landsFlat;
        this.approvesFlat = approvesFlat;
    }

    @Override
    public void loadAll() {
        loadLands();
        loadApproves();
    }

    @Override
    public void loadLands() {
        landsFlat.loadLands();
    }

    @Override
    public void saveLand(final Land land) {
        landsFlat.saveLand(land);
    }

    @Override
    public void removeLand(final Land land) {
        landsFlat.removeLand(land);
    }

    @Override
    public List<Approve> loadApproves() {
        return approvesFlat.loadApproves();
    }

    @Override
    public void saveApprove(final Approve approve) {
        approvesFlat.saveApprove(approve);
    }

    @Override
    public void removeApprove(final Approve approve) {
        approvesFlat.removeApprove(approve);
    }

    @Override
    public void removeAllApproves() {
        approvesFlat.removeAll();
    }
}
