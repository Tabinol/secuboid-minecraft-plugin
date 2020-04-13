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
package me.tabinol.secuboid.permissionsflags;

/**
 * The Enum FlagList.
 *
 * @author Tabinol
 */
public enum FlagList {

    /**
     * The undefined.
     */
    UNDEFINED(""),
    /**
     * The firespread.
     */
    FIRESPREAD(true),
    /**
     * The fire.
     */
    FIRE(true),
    /**
     * The explosion.
     */
    EXPLOSION(true),
    /**
     * The creeper explosion.
     */
    CREEPER_EXPLOSION(true),
    /**
     * The tnt explosion.
     */
    TNT_EXPLOSION(true),
    /**
     * The end crystal explosion.
     */
    END_CRYSTAL_EXPLOSION(true),
    /**
     * The firework explosion.
     */
    FIREWORK_EXPLOSION(true),
    /**
     * The creeper damage.
     */
    CREEPER_DAMAGE(true),
    /**
     * The enderman damage.
     */
    ENDERMAN_DAMAGE(true),
    /**
     * The wither damage.
     */
    WITHER_DAMAGE(true),
    /**
     * The ghast damage.
     */
    GHAST_DAMAGE(true),
    /**
     * The enderdragon damage.
     */
    ENDERDRAGON_DAMAGE(true),
    /**
     * The ender crystal damage.
     */
    END_CRYSTAL_DAMAGE(true),
    /**
     * The tnt damage.
     */
    TNT_DAMAGE(true),
    /**
     * The mob spawn.
     */
    MOB_SPAWN(true),
    /**
     * The animal spawn.
     */
    ANIMAL_SPAWN(true),
    /**
     * The villager spawn.
     */
    VILLAGER_SPAWN(true),
    /**
     * The leaves decay
     */
    LEAF_DECAY(true),
    /**
     * The crop trample
     */
    CROP_TRAMPLE(true),
    /**
     * The lava flow
     */
    LAVA_FLOW(true),
    /**
     * The water flow
     */
    WATER_FLOW(true),
    /**
     * The full pvp.
     */
    FULL_PVP(true),
    /**
     * The message enter.
     */
    MESSAGE_ENTER(""),
    /**
     * The message exit.
     */
    MESSAGE_EXIT(""),
    /**
     * The eco block price.
     */
    ECO_BLOCK_PRICE(0d),
    /**
     * The exclude commands.
     */
    EXCLUDE_COMMANDS(new String[]{}),
    /**
     * The spawn and teleport point.
     */
    SPAWN(""),
    /**
     * Inherit from parent owner
     */
    INHERIT_OWNER(true),
    /**
     * Inherit from parent residents
     */
    INHERIT_RESIDENTS(true),
    /**
     * Inherit from parent tenant
     */
    INHERIT_TENANT(true),
    /**
     * Is a portal for a world (spawn)
     */
    PORTAL_WORLD(""),
    /**
     * Is a portal for a world (random)
     */
    PORTAL_WORLD_RANDOM(""),
    /**
     * Is a portal for a land (spawn)
     */
    PORTAL_LAND(""),
    /**
     * Is a portal for a world (random)
     */
    PORTAL_LAND_RANDOM("");

    /**
     * The base value.
     */
    final FlagValue baseValue;

    /**
     * The flag type.
     */
    private FlagType flagType;

    /**
     * Instantiates a new flag list.
     *
     * @param baseValue the base value
     */
    FlagList(Object baseValue) {
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
