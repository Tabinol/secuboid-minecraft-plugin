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
package me.tabinol.secuboid;

import java.util.EnumSet;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * This class is for compatibility to BK 1.7.x 
 *
 */
public class BKVersion {
    
    private static boolean existPlayerInteractAtEntityEvent = false;
    
    private static GameMode spectatorMode = null;
    
    private static final EnumSet<Material> doors = EnumSet.noneOf(Material.class);
    
    private static Material armorStand = null;
    
    private static EntityType armorStandEntity = null;
    
    /**
     *
     */
    protected static void initVersion() {
        
        // org.bukkit.events.player.PlayerInteractAtEntityEvent (for ArmorStand)
        try {
            Class<?> plInAtEnEv = Class.forName("org.bukkit.events.player.PlayerInteractAtEntityEvent");
            if(plInAtEnEv != null) {
                existPlayerInteractAtEntityEvent = true;
            }
            
        } catch (ClassNotFoundException ex) {
            // This is 1.7 version
        }
        
        // Spectator mode
        try {
            spectatorMode = GameMode.valueOf("SPECTATOR");
        } catch (IllegalArgumentException ex) {
            // This is 1.7 version 
        }
        
        // Doors
        doors.add(Material.WOODEN_DOOR);
        doors.add(Material.TRAP_DOOR);
        doors.add(Material.FENCE_GATE);
        try {
            doors.add(Material.valueOf("SPRUCE_DOOR"));
            doors.add(Material.valueOf("SPRUCE_FENCE_GATE"));
            doors.add(Material.valueOf("BIRCH_DOOR"));
            doors.add(Material.valueOf("BIRCH_FENCE_GATE"));
            doors.add(Material.valueOf("JUNGLE_DOOR"));
            doors.add(Material.valueOf("JUNGLE_FENCE_GATE"));
            doors.add(Material.valueOf("ACACIA_DOOR"));
            doors.add(Material.valueOf("ACACIA_FENCE_GATE"));
            doors.add(Material.valueOf("DARK_OAK_DOOR"));
            doors.add(Material.valueOf("DARK_OAK_FENCE_GATE"));
        } catch (IllegalArgumentException ex) {
            // This is 1.7 version 
        }
        
        // ArmorStand
        try {
            armorStand = Material.valueOf("ARMOR_STAND");
            armorStandEntity = EntityType.valueOf("ARMOR_STAND");
        } catch (IllegalArgumentException ex) {
            // This is 1.7 version 
        }
        
    }
    
    /**
     *
     * @return
     */
    public static boolean isPlayerInteractAtEntityEventExist() {
        
        return existPlayerInteractAtEntityEvent;
    }
    
    /**
     *
     * @param player
     * @return
     */
    public static boolean isSpectatorMode(Player player) {
        
        return player.getGameMode() == spectatorMode;
    }
    
    /**
     *
     * @param material
     * @return
     */
    public static boolean isDoor(Material material) {
        
        return doors.contains(material);
    }
    
    /**
     *
     * @param material
     * @return
     */
    public static boolean isArmorStand(Material material) {
        
        return armorStand != null ? material == armorStand : false;
    }

    /**
     *
     * @param entityType
     * @return
     */
    public static boolean isArmorStand(EntityType entityType) {
        
        return armorStandEntity != null ? entityType == armorStandEntity : false;
    }
}
