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

package me.tabinol.secuboid.lands.areas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.tabinol.secuboid.lands.areas.lines.LineLine;
import me.tabinol.secuboid.utilities.Calculate;

public final class LinesArea extends Area {
    
    private final List<LineLine> lines;
    
    public LinesArea(String worldName, Collection<LineLine> lines) {
        
        super(AreaType.LINES, worldName, 0, 0, 0, 0, 0, 0);
        x1 = Integer.MAX_VALUE;
        y1 = Integer.MAX_VALUE;
        z1 = Integer.MAX_VALUE;
        x2 = Integer.MIN_VALUE;
        y2 = Integer.MIN_VALUE;
        z2 = Integer.MIN_VALUE;
        this.lines = new ArrayList<LineLine>();

        // Know the minimal x/z and maximal x/z
        if(lines != null) {
            for (LineLine line : lines) {
                addLine(line);
            }
        }
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(areaType).append(":").append(worldName);
        boolean isFirst = true;
        for(LineLine line : getLines()) {
            sb.append(":").append(line.toString(isFirst));
            isFirst = false;
        }

        return sb.toString();
    }

    /**
     * Gets the prints (visual format).
     *
     * @return the prints
     */
    @Override
    public String getPrint() {

        StringBuffer sb = new StringBuffer();
        sb.append(areaType.toString().substring(0, 3).toLowerCase()).append(":");
        boolean isFirst = true;
        for(LineLine line : getLines()) {
            if(!isFirst) {
                sb.append(", ");
            }
            sb.append(line.getPrint(isFirst));
            isFirst = false;
        }

        return sb.toString();
    }

    public void addLine(LineLine line) {

        // Modify the previous line x2/y2/z2
        if(!lines.isEmpty()) {
            lines.get(lines.size() - 1).resolveIntersection(line);
        }

        this.lines.add(line);
        x1 = Calculate.lowerInt(Calculate.lowerInt(line.getLeftX1(), line.getLeftX2()),
                Calculate.lowerInt(line.getRightX1(), line.getRightX2()));
        y1 = line.getY1();
        z1 = Calculate.lowerInt(Calculate.lowerInt(line.getLeftZ1(), line.getLeftZ2()),
                Calculate.lowerInt(line.getRightZ1(), line.getRightZ2()));
        x2 = Calculate.greaterInt(Calculate.greaterInt(line.getLeftX1(), line.getLeftX2()),
                Calculate.greaterInt(line.getRightX1(), line.getRightX2()));
        y2 = line.gety2();
        z2 = Calculate.greaterInt(Calculate.greaterInt(line.getLeftZ1(), line.getLeftZ2()),
                Calculate.greaterInt(line.getRightZ1(), line.getRightZ2()));
    }

    public Collection<LineLine> getLines() {

        return lines;
    }

    public Area copyOf() {
        
        return new LinesArea(worldName, lines);
    }

    public long getVolume() {
        
        long volume = 0;

        for(LineLine line : lines) {
            volume += ((LineLine) line).getVolume();
        }

        return volume;
    }

    public boolean isLocationInside(String worldName, int x, int y, int z) {

        if(worldName.equals(worldName)) {
            for(LineLine line : lines) {
                if(((LineLine) line).isLocationInside(x, y, z)) {
                    return true;
                }
            }
        }

        return false;
    }
}
