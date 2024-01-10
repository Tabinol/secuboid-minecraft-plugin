/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.secuboid.core.lands;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.LandType;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.core.persistence.jpa.LandJPA;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Data
@Builder
public class LandImpl implements Land {

    private final LandJPA jPA;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private final Set<Area> areas = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Land parent = null;

    @Override
    public String getName() {
        return jPA.getName();
    }

    @Override
    public LandType getType() {
        return jPA.getType();
    }

    @Override
    public String getPathName() {
        return switch (getType()) {
            case LAND -> Optional.ofNullable(parent).map(Land::getPathName).orElse("") + SEPARATOR_LAND + getName();
            case CONFIGURATION_SET -> PREFIX_CONFIGURATION_SET + getName();
            case WORLD -> SEPARATOR_LAND + getName();
        };
    }

    @Override
    public Set<Land> getChildren() {
        // TODO Implements
        return null;
    }

    @Override
    public Land getWorldLand() {
        if (getType() == LandType.WORLD) {
            return this;
        }

        return Optional.ofNullable(parent).map(Land::getWorldLand).orElse(null);
    }

    @Override
    public boolean isLocationInside(int x, int z) {
        for (Area area : areas) {
            if (area.isLocationInside(x, z)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isLocationInside(int x, int y, int z) {
        for (Area area : areas) {
            if (area.isLocationInside(x, y, z)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isLocationInside(Location loc) {
        if (!getWorldLand().isLocationInside(loc)) {
            return false;
        }

        for (Area area : areas) {
            if (area.isLocationInside(loc)) {
                return true;
            }
        }

        return false;
    }
}
