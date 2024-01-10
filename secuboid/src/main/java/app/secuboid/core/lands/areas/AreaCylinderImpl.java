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
import app.secuboid.api.lands.areas.AreaCylinder;
import app.secuboid.api.lands.areas.AreaType;
import app.secuboid.api.messages.MessagePath;
import app.secuboid.core.messages.MessagePaths;
import app.secuboid.core.persistence.jpa.AreaJPA;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AreaCylinderImpl extends AreaImpl implements AreaCylinder {

    @EqualsAndHashCode.Exclude
    private boolean isSetValues = false;
    @EqualsAndHashCode.Exclude
    private double rX = 0;
    @EqualsAndHashCode.Exclude
    private double rZ = 0;
    @EqualsAndHashCode.Exclude
    private double originH = 0;
    @EqualsAndHashCode.Exclude
    private double originK = 0;

    public AreaCylinderImpl(AreaJPA areaJPA, Land land) {
        super(areaJPA, land);

        rX = (double) (getX2() - getX1()) / 2;
        rZ = (double) (getZ2() - getZ1()) / 2;
        originH = getX1() + rX;
        originK = getZ1() + rZ;
        isSetValues = true;
    }

    @Override
    public AreaType getType() {
        return AreaType.CYLINDER;
    }

    @Override
    public int getZPosFromX(int x) {
        return (int) Math.round(originK + (rZ * Math.sqrt((rX + x - originH) * (rX - x + originH))) / getRX());
    }

    @Override
    public int getZNegFromX(int x) {
        return (int) Math.round(originK - (rZ * Math.sqrt((rX + x - originH) * (rX - x + originH))) / getRX());
    }

    @Override
    public int getXPosFromZ(int z) {
        return (int) Math.round(originH + (rX * Math.sqrt((rZ + z - originK) * (rZ - z + originK))) / getRZ());
    }

    @Override
    public int getXNegFromZ(int z) {
        return (int) Math.round(originH - (rX * Math.sqrt((rZ + z - originK) * (rZ - z + originK))) / getRZ());
    }

    @Override
    public long getArea() {
        return Math.round(rX * rZ * Math.PI);
    }

    @Override
    public long getVolume() {
        return Math.round(rX * rZ * Math.PI * (getY2() - getY1() + 1));
    }

    @Override
    public boolean isLocationInside(int x, int z) {
        return ((Math.pow((x - originH), 2) / Math.pow(rX, 2))
                + (Math.pow((z - originK), 2) / Math.pow(rZ, 2))) < 1;
    }

    @Override
    public MessagePath getMessagePath() {
        return MessagePaths.areaCylinder(originH, originK, rX, getRZ());
    }
}
