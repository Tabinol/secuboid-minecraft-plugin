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

package app.secuboid.core.lands.areas;

import app.secuboid.api.lands.Land;
import app.secuboid.api.lands.LocationPath;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.lands.areas.AreaType;
import app.secuboid.api.messages.MessagePath;
import app.secuboid.core.persistence.jpa.AreaJPA;
import app.secuboid.core.utilities.LocalMath;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Location;

import java.util.Optional;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public abstract class AreaImpl implements Area {

    protected final AreaJPA jPA;
    protected final Land land;

    public static Area newInstance(AreaType type, AreaJPA areaJPA, Land land) {
        return switch (type) {
            case CUBOID -> new AreaCuboidImpl(areaJPA, land);
            case CYLINDER -> new AreaCylinderImpl(areaJPA, land);
        };
    }

    public abstract MessagePath getMessagePath();

    @Override
    public int getX1() {
        return jPA.getX1();
    }

    @Override
    public int getY1() {
        return jPA.getY1();
    }

    @Override
    public int getZ1() {
        return jPA.getZ1();
    }

    @Override
    public int getX2() {
        return jPA.getX2();
    }

    @Override
    public int getY2() {
        return jPA.getY2();
    }

    @Override
    public int getZ2() {
        return jPA.getZ2();
    }

    @Override
    public final String getPathName() {
        return Optional.ofNullable(getLand()).map(LocationPath::getPathName).orElse("") + SEPARATOR_AREA + getId();
    }

    @Override
    public String getName() {
        return String.valueOf(getId());
    }

    @Override
    public boolean isLocationInside(int x, int y, int z) {
        return isLocationInside(x, z) && LocalMath.isInRange(y, getY1(), getY2());
    }

    @Override
    public boolean isLocationInside(Location loc) {
        return isLocationInside(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Override
    public final boolean isLocationInsideSquare(int x, int z) {
        return LocalMath.isInRange(x, getX1(), getX2()) && LocalMath.isInRange(z, getZ1(), getZ2());
    }
}
