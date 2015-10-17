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
package me.tabinol.secuboid.parameters;


/**
 * The Enum FlagList.
 *
 * @author Tabinol
 */
public enum FlagList {
    
    /** The undefined. */
    UNDEFINED(new String()),
    
    /** The firespread. */
    FIRESPREAD(new Boolean(true)),
    
    /** The fire. */
    FIRE(new Boolean(true)),
    
    /** The explosion. */
    EXPLOSION(new Boolean(true)),
    
    /** The creeper explosion. */
    CREEPER_EXPLOSION(new Boolean(true)),
    
    /** The tnt explosion. */
    TNT_EXPLOSION(new Boolean(true)),
    
    /** The creeper damage. */
    CREEPER_DAMAGE(new Boolean(true)),
    
    /** The enderman damage. */
    ENDERMAN_DAMAGE(new Boolean(true)),
    
    /** The wither damage. */
    WITHER_DAMAGE(new Boolean(true)),
    
    /** The ghast damage. */
    GHAST_DAMAGE(new Boolean(true)),
    
    /** The enderdragon damage. */
    ENDERDRAGON_DAMAGE(new Boolean(true)),
    
    /** The tnt damage. */
    TNT_DAMAGE(new Boolean(true)),
    
    /** The mob spawn. */
    MOB_SPAWN(new Boolean(true)),
    
    /** The animal spawn. */
    ANIMAL_SPAWN(new Boolean(true)),
    
    /** The leaves decay */
    LEAF_DECAY(new Boolean(true)),
    
    /** The crop trample */
    CROP_TRAMPLE(new Boolean(true)),

    /** The lava flow */
    LAVA_FLOW(new Boolean(true)),

    /** The water flow */
    WATER_FLOW(new Boolean(true)),

    /** The full pvp. */
    FULL_PVP(new Boolean(true)),
    
    /** The faction pvp. */
    FACTION_PVP(new Boolean(true)),

    /** The message join. */
    MESSAGE_JOIN(new String()),
    
    /** The message quit. */
    MESSAGE_QUIT(new String()),
    
    /** The eco block price. */
    ECO_BLOCK_PRICE(new Double(0)),
    
    /** The exclude commands. */
    EXCLUDE_COMMANDS(new String[] {}),
    
    /**  The spawn and teleport point. */
    SPAWN(new String("")),
    
    /** Inherit from parent owner */
    INHERIT_OWNER(new Boolean(true)),
    
    /** Inherit from parent residents */
    INHERIT_RESIDENTS(new Boolean(true)),
    
    /** Inherit from parent tenant */
    INHERIT_TENANT(new Boolean(true));

    /** The base value. */
    final FlagValue baseValue;
    
    /** The flag type. */
    private FlagType flagType;
    
    /**
     * Instantiates a new flag list.
     *
     * @param baseValue the base value
     */
    private FlagList(Object baseValue) {

        this.baseValue = new FlagValue(baseValue);
    }

    /**
     * Sets the flag type.
     *
     * @param flagType the new flag type
     */
    void setFlagType(FlagType flagType) {
        
        this.flagType = flagType;
    }

    /**
     * Gets the flag type.
     *
     * @return the flag type
     */
    public FlagType getFlagType() {
        
        return flagType;
    }
}
