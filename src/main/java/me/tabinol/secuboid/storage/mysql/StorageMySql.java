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
package me.tabinol.secuboid.storage.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.inventories.PlayerInvEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.permissionsflags.Flag;
import me.tabinol.secuboid.permissionsflags.Permission;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.storage.Storage;
import me.tabinol.secuboid.storage.mysql.dao.SecuboidDao;

/**
 * StorageMySql
 */
public class StorageMySql implements Storage {

    private final Secuboid secuboid;
    private final Logger log;
    private final DatabaseConnection dbConn;

    public StorageMySql(final Secuboid secuboid, final DatabaseConnection dbConn) {
        this.secuboid = secuboid;
        this.dbConn = dbConn;
        log = secuboid.getLogger();
    }

    @Override
    public void loadAll() {
        try {
            initDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            log.log(Level.SEVERE, "Unable to create or access the database", e);
        }
        loadLands();
        loadApproves();
        loadPlayersCache();
        loadInventories();
    }

    private void initDatabase() throws ClassNotFoundException, SQLException {
        dbConn.loadDriver();
        try (final Connection conn = dbConn.openConnection()) {
            // Verify if the database is empty
            final SecuboidDao secuboidDao = new SecuboidDao(dbConn);
            secuboidDao.createVarables(conn);
            final Optional<Integer> versionOpt = secuboidDao.getVersionOpt(conn);
            if (!versionOpt.isPresent() || versionOpt.get() < 1) {
                // Create database
                secuboidDao.initDatabase(conn);
                secuboidDao.setVersion(conn);
            }
        }
    }

    @Override
    public void loadLands() {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLand(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLand(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandArea(final Land land, final Area area) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandArea(final Land land, final Area area) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandBanned(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandBanned(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandFlag(final Land land, final Flag flag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandFlags(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandFlag(final Land land, final Flag flag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandPermissions(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandPermission(final Land land, final PlayerContainer playerContainer,
            final Permission permission) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandPlayerNotify(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandPlayerNotify(final Land land, final PlayerContainerPlayer pcp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLandResident(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllLandResidents(final Land land) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveLandResident(final Land land, final PlayerContainer playerContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadApproves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveApprove(final Approve approve) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeApprove(final Approve approve) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAllApproves() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadInventories() {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryDefault(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeInventoryDefault(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadInventoriesPlayer(final PlayerInventoryCache playerInventoryCache) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryPlayer(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryPlayerDeath(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveInventoryPlayerDeathHistory(final PlayerInvEntry playerInvEntry) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadPlayersCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void savePlayerCacheEntry(final PlayerCacheEntry playerCacheEntry) {
        // TODO Auto-generated method stub

    }

}