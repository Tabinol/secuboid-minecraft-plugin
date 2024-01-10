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
import app.secuboid.api.lands.areas.AreaCuboid;
import app.secuboid.api.lands.areas.AreaType;
import app.secuboid.api.messages.MessagePath;
import app.secuboid.core.messages.MessagePaths;
import app.secuboid.core.persistence.jpa.AreaJPA;
import app.secuboid.core.utilities.LocalMath;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AreaCuboidImpl extends AreaImpl implements AreaCuboid {

    public AreaCuboidImpl(AreaJPA areaJPA, Land land) {
        super(areaJPA, land);
    }

    @Override
    public AreaType getType() {
        return AreaType.CUBOID;
    }

    @Override
    public long getArea() {
        return (getX2() - getX1() + 1L) * (getZ2() - getZ1() + 1L);
    }

    @Override
    public long getVolume() {
        return getArea() * (getY2() - getY1() + 1);
    }

    @Override
    public boolean isLocationInside(int x, int z) {
        return LocalMath.isInRange(x, getX1(), getX2())
                && LocalMath.isInRange(z, getZ1(), getZ2());
    }

    @Override
    public MessagePath getMessagePath() {
        return MessagePaths.areaCuboid(getX1(), getY1(), getZ1(), getX2(), getY2(), getZ2());
    }
}
