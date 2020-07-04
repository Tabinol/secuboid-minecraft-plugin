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

import java.util.UUID;

/**
 * LandPojo
 */
public final class LandPojo {

    private final UUID uuid;
    private final String name;
    private final boolean approved;
    private final Long typeIdNullable;
    private final long ownerId;
    private final UUID parentUUIDNullable;
    private final short priority;
    private final double money;
    private final boolean forSale;
    private final String forSaleSignLocationNullable;
    private final Double salePriceNullable;
    private final boolean forRent;
    private final String forRentSignLocationNullable;
    private final Double rentPriceNullable;
    private final Integer rentRenewNullable;
    private final Boolean rentAutoRenewNullable;
    private final UUID tenantUUIDNullable;
    private final Long lastPaymentMillisNullable;

    public LandPojo(final UUID uuid, final String name, final boolean approved, final Long typeIdNullable,
                    final long ownerId, final UUID parentUUIDNullable, final short priority, final double money,
                    final boolean forSale, final String forSaleSignLocationNullable, final Double salePriceNullable,
                    final boolean forRent, final String forRentSignLocationNullable, final Double rentPriceNullable,
                    final Integer rentRenewNullable, final Boolean rentAutoRenewNullable,
                    final UUID tenantUUIDNullable, final Long lastPaymentMillisNullable) {
        this.uuid = uuid;
        this.name = name;
        this.approved = approved;
        this.typeIdNullable = typeIdNullable;
        this.ownerId = ownerId;
        this.parentUUIDNullable = parentUUIDNullable;
        this.priority = priority;
        this.money = money;
        this.forSale = forSale;
        this.forSaleSignLocationNullable = forSaleSignLocationNullable;
        this.salePriceNullable = salePriceNullable;
        this.forRent = forRent;
        this.forRentSignLocationNullable = forRentSignLocationNullable;
        this.rentPriceNullable = rentPriceNullable;
        this.rentRenewNullable = rentRenewNullable;
        this.rentAutoRenewNullable = rentAutoRenewNullable;
        this.tenantUUIDNullable = tenantUUIDNullable;
        this.lastPaymentMillisNullable = lastPaymentMillisNullable;
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

    public Long getTypeIdNullable() {
        return this.typeIdNullable;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public UUID getParentUUIDNullable() {
        return this.parentUUIDNullable;
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

    public String getForSaleSignLocationNullable() {
        return this.forSaleSignLocationNullable;
    }

    public Double getSalePriceNullable() {
        return this.salePriceNullable;
    }

    public boolean getForRent() {
        return this.forRent;
    }

    public boolean isForRent() {
        return this.forRent;
    }

    public String getForRentSignLocationNullable() {
        return this.forRentSignLocationNullable;
    }

    public Double getRentPriceNullable() {
        return this.rentPriceNullable;
    }

    public Integer getRentRenewNullable() {
        return this.rentRenewNullable;
    }

    public Boolean getRentAutoRenewNullable() {
        return this.rentAutoRenewNullable;
    }

    public UUID getTenantUUIDNullable() {
        return this.tenantUUIDNullable;
    }

    public Long getLastPaymentMillisNullable() {
        return this.lastPaymentMillisNullable;
    }
}