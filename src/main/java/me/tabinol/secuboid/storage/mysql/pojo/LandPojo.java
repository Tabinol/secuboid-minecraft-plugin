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

import java.util.Optional;
import java.util.UUID;

/**
 * LandPojo
 */
public final class LandPojo {

    private final UUID uuid;
    private final String name;
    private final boolean approved;
    private final Optional<Long> typeIdOpt;
    private final long ownerId;
    private final Optional<UUID> parentUUIDOpt;
    private final short priority;
    private final double money;
    private final boolean forSale;
    private final Optional<String> forSaleSignLocationOpt;
    private final Optional<Double> salePriceOpt;
    private final boolean forRent;
    private final Optional<String> forRentSignLocationOpt;
    private final Optional<Double> rentPriceOpt;
    private final Optional<Integer> rentRenewOpt;
    private final Optional<Boolean> rentAutoRenewOpt;
    private final Optional<UUID> tenantUUIDOpt;
    private final Optional<Long> lastPaymentMillisOpt;

    public LandPojo(final UUID uuid, final String name, final boolean approved, final Optional<Long> typeIdOpt,
            final long ownerId, final Optional<UUID> parentUUIDOpt, final short priority, final double money,
            final boolean forSale, final Optional<String> forSaleSignLocationOpt, final Optional<Double> salePriceOpt,
            final boolean forRent, final Optional<String> forRentSignLocationOpt, final Optional<Double> rentPriceOpt,
            final Optional<Integer> rentRenewOpt, final Optional<Boolean> rentAutoRenewOpt,
            final Optional<UUID> tenantUUIDOpt, final Optional<Long> lastPaymentMillisOpt) {
        this.uuid = uuid;
        this.name = name;
        this.approved = approved;
        this.typeIdOpt = typeIdOpt;
        this.ownerId = ownerId;
        this.parentUUIDOpt = parentUUIDOpt;
        this.priority = priority;
        this.money = money;
        this.forSale = forSale;
        this.forSaleSignLocationOpt = forSaleSignLocationOpt;
        this.salePriceOpt = salePriceOpt;
        this.forRent = forRent;
        this.forRentSignLocationOpt = forRentSignLocationOpt;
        this.rentPriceOpt = rentPriceOpt;
        this.rentRenewOpt = rentRenewOpt;
        this.rentAutoRenewOpt = rentAutoRenewOpt;
        this.tenantUUIDOpt = tenantUUIDOpt;
        this.lastPaymentMillisOpt = lastPaymentMillisOpt;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public boolean getApproved() {
        return this.approved;
    }

    public boolean isApproved() {
        return this.approved;
    }

    public Optional<Long> getTypeIdOpt() {
        return this.typeIdOpt;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public Optional<UUID> getParentUUIDOpt() {
        return this.parentUUIDOpt;
    }

    public short getPriority() {
        return this.priority;
    }

    public double getMoney() {
        return this.money;
    }

    public boolean getForSale() {
        return this.forSale;
    }

    public boolean isForSale() {
        return this.forSale;
    }

    public Optional<String> getForSaleSignLocationOpt() {
        return this.forSaleSignLocationOpt;
    }

    public Optional<Double> getSalePriceOpt() {
        return this.salePriceOpt;
    }

    public boolean getForRent() {
        return this.forRent;
    }

    public boolean isForRent() {
        return this.forRent;
    }

    public Optional<String> getForRentSignLocationOpt() {
        return this.forRentSignLocationOpt;
    }

    public Optional<Double> getRentPriceOpt() {
        return this.rentPriceOpt;
    }

    public Optional<Integer> getRentRenewOpt() {
        return this.rentRenewOpt;
    }

    public Optional<Boolean> getRentAutoRenewOpt() {
        return this.rentAutoRenewOpt;
    }

    public Optional<UUID> getTenantUUIDOpt() {
        return this.tenantUUIDOpt;
    }

    public Optional<Long> getLastPaymentMillisOpt() {
        return this.lastPaymentMillisOpt;
    }
}