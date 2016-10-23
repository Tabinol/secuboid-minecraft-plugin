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

package me.tabinol.secuboid.inventories;

/**
 *
 * @author michel
 */
public class PlayerInvEntry {
    
    private InventorySpec actualInv;
    private boolean isCreativeInv;
    
    /**
     *
     * @param actualInv
     * @param isCreativeInv
     */
    public PlayerInvEntry(InventorySpec actualInv, boolean isCreativeInv) {
        
        this.actualInv = actualInv;
        this.isCreativeInv = isCreativeInv;
    }
    
    /**
     *
     * @return
     */
    public InventorySpec getActualInv() {
        
        return actualInv;
    }
    
    /**
     *
     * @param actualInv
     */
    public void setActualInv(InventorySpec actualInv) {
        
        this.actualInv = actualInv;
    }
    
    /**
     *
     * @return
     */
    public boolean isCreativeInv() {
        
        return isCreativeInv;
    }
    
    /**
     *
     * @param isCreativeInv
     */
    public void setCreativeInv(boolean isCreativeInv) {
        
        this.isCreativeInv = isCreativeInv;
    }
}
