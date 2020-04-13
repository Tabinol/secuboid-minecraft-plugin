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

public final class FlagsPojo {

    private final UUID landUUID;
    private final int flagId;
    private final Optional<String> valueStringOpt;
    private final Optional<Double> valueDoubleOpt;
    private final Optional<Boolean> valueBooleanOpt;
    private final boolean inheritance;

    public FlagsPojo(final UUID landUUID, final int flagId, final Optional<String> valueStringOpt,
            final Optional<Double> valueDoubleOpt, final Optional<Boolean> valueBooleanOpt, final boolean inheritance) {
        this.landUUID = landUUID;
        this.flagId = flagId;
        this.valueStringOpt = valueStringOpt;
        this.valueDoubleOpt = valueDoubleOpt;
        this.valueBooleanOpt = valueBooleanOpt;
        this.inheritance = inheritance;
    }

    public UUID getLandUUID() {
        return this.landUUID;
    }

    public int getFlagId() {
        return this.flagId;
    }

    public Optional<String> getValueStringOpt() {
        return this.valueStringOpt;
    }

    public Optional<Double> getValueDoubleOpt() {
        return this.valueDoubleOpt;
    }

    public Optional<Boolean> getValueBooleanOpt() {
        return this.valueBooleanOpt;
    }

    public boolean getInheritance() {
        return this.inheritance;
    }

    public boolean isInheritance() {
        return this.inheritance;
    }
}