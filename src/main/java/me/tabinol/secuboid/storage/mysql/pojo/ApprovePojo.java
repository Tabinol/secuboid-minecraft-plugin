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
package me.tabinol.secuboid.storage.mysql.pojo;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

public final class ApprovePojo {

    private final UUID landUUID;
    private final long approveActionId;
    private final Optional<Integer> removedAreaIdOpt;
    private final Optional<Integer> newAreaIdOpt;
    private final long ownerId;
    private final Optional<UUID> parentUUIDOpt;
    private final double price;
    private final Calendar transactionDatetime;

    public ApprovePojo(final UUID landUUID, final long approveActionId, final Optional<Integer> removedAreaIdOpt,
            final Optional<Integer> newAreaIdOpt, final long ownerId, final Optional<UUID> parentUUIDOpt,
            final double price, final Calendar transactionDatetime) {
        this.approveActionId = approveActionId;
        this.landUUID = landUUID;
        this.removedAreaIdOpt = removedAreaIdOpt;
        this.newAreaIdOpt = newAreaIdOpt;
        this.ownerId = ownerId;
        this.parentUUIDOpt = parentUUIDOpt;
        this.price = price;
        this.transactionDatetime = transactionDatetime;
    }

    public UUID getLandUUID() {
        return this.landUUID;
    }

    public long getApproveActionId() {
        return this.approveActionId;
    }

    public Optional<Integer> getRemovedAreaIdOpt() {
        return this.removedAreaIdOpt;
    }

    public Optional<Integer> getNewAreaIdOpt() {
        return this.newAreaIdOpt;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public Optional<UUID> getParentUUIDOpt() {
        return this.parentUUIDOpt;
    }

    public double getPrice() {
        return this.price;
    }

    public Calendar getTransactionDatetime() {
        return this.transactionDatetime;
    }
}