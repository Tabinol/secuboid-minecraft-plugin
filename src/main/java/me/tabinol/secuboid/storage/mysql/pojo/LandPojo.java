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
    private final Optional<Integer> typeIdOpt;
    private final int ownerId;
    private final Optional<UUID> parentUuidOpt;
    private final int priority;
    private final double money;
    private final boolean forSale;
    private final Optional<Integer> forSaleSignXOpt;
    private final Optional<Integer> forSaleSignYOpt;
    private final Optional<Integer> forSaleSignZOpt;
    private final Optional<Double> salePriceOpt;
    private final boolean forRent;
    private final Optional<Integer> forRentSignXOpt;
    private final Optional<Integer> forRentSignYOpt;
    private final Optional<Integer> forRentSignZOpt;
    private final Optional<Double> rentPriceOpt;
    private final Optional<Boolean> rentRenewOpt;
    private final Optional<Boolean> rentAutoRenewOpt;
    private final Optional<Integer> tenantIdOpt;
    private final Optional<Long> lastPaymentMillisOpt;

    public LandPojo(final UUID uuid, final String name, final boolean approved, final Optional<Integer> typeIdOpt,
            final int ownerId, final Optional<UUID> parentUuidOpt, final int priority, final double money,
            final boolean forSale, final Optional<Integer> forSaleSignXOpt, final Optional<Integer> forSaleSignYOpt,
            final Optional<Integer> forSaleSignZOpt, final Optional<Double> salePriceOpt, final boolean forRent,
            final Optional<Integer> forRentSignXOpt, final Optional<Integer> forRentSignYOpt,
            final Optional<Integer> forRentSignZOpt, final Optional<Double> rentPriceOpt,
            final Optional<Boolean> rentRenewOpt, final Optional<Boolean> rentAutoRenewOpt,
            final Optional<Integer> tenantIdOpt, final Optional<Long> lastPaymentMillisOpt) {
        this.uuid = uuid;
        this.name = name;
        this.approved = approved;
        this.typeIdOpt = typeIdOpt;
        this.ownerId = ownerId;
        this.parentUuidOpt = parentUuidOpt;
        this.priority = priority;
        this.money = money;
        this.forSale = forSale;
        this.forSaleSignXOpt = forSaleSignXOpt;
        this.forSaleSignYOpt = forSaleSignYOpt;
        this.forSaleSignZOpt = forSaleSignZOpt;
        this.salePriceOpt = salePriceOpt;
        this.forRent = forRent;
        this.forRentSignXOpt = forRentSignXOpt;
        this.forRentSignYOpt = forRentSignYOpt;
        this.forRentSignZOpt = forRentSignZOpt;
        this.rentPriceOpt = rentPriceOpt;
        this.rentRenewOpt = rentRenewOpt;
        this.rentAutoRenewOpt = rentAutoRenewOpt;
        this.tenantIdOpt = tenantIdOpt;
        this.lastPaymentMillisOpt = lastPaymentMillisOpt;
    }

    public UUID getUuid() {
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

    public Optional<Integer> getTypeIdOpt() {
        return this.typeIdOpt;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public Optional<UUID> getParentUuidOpt() {
        return this.parentUuidOpt;
    }

    public int getPriority() {
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

    public Optional<Integer> getForSaleSignXOpt() {
        return this.forSaleSignXOpt;
    }

    public Optional<Integer> getForSaleSignYOpt() {
        return this.forSaleSignYOpt;
    }

    public Optional<Integer> getForSaleSignZOpt() {
        return this.forSaleSignZOpt;
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

    public Optional<Integer> getForRentSignXOpt() {
        return this.forRentSignXOpt;
    }

    public Optional<Integer> getForRentSignYOpt() {
        return this.forRentSignYOpt;
    }

    public Optional<Integer> getForRentSignZOpt() {
        return this.forRentSignZOpt;
    }

    public Optional<Double> getRentPriceOpt() {
        return this.rentPriceOpt;
    }

    public Optional<Boolean> getRentRenewOpt() {
        return this.rentRenewOpt;
    }

    public Optional<Boolean> getRentAutoRenewOpt() {
        return this.rentAutoRenewOpt;
    }

    public Optional<Integer> getTenantIdOpt() {
        return this.tenantIdOpt;
    }

    public Optional<Long> getLastPaymentMillisOpt() {
        return this.lastPaymentMillisOpt;
    }
}