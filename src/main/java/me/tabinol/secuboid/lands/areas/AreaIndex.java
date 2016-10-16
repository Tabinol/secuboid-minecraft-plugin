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

/**
 * The Class AreaIndex.
 */
public class AreaIndex implements Comparable<AreaIndex> {
    
    /** The index nb. */
    private final int indexNb;
    
    /** The area. */
    private final Area area;
    
    /**
     * Instantiates a new area index.
     *
     * @param indexNb the index nb
     * @param area the area
     */
    public AreaIndex(int indexNb, Area area) {
        
        this.indexNb = indexNb;
        this.area = area;
    }

    /**
     * Equals.
     *
     * @param index2 the index2
     * @return true, if successful
     */
    public boolean equals(AreaIndex index2) {
        
        return indexNb == index2.indexNb && area == index2.area;
    }
    
    /**
     * Copy of.
     *
     * @return the area index
     */
    public AreaIndex copyOf() {
        
        return new AreaIndex(indexNb, area);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AreaIndex t) {
        if(indexNb < t.indexNb) {
            return -1;
        }
        if(indexNb > t.indexNb) {
            return 1;
        }
        return ((Area) area).compareTo((Area) t.area);
    }
    
    /**
     * Gets the index nb.
     *
     * @return the index nb
     */
    public int getIndexNb() {
        
        return indexNb;
    }
    
    /**
     * Gets the area.
     *
     * @return the area
     */
    public Area getArea() {
        
        return (Area) area;
    }
}
