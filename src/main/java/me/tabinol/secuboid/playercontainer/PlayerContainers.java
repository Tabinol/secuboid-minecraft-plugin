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
package me.tabinol.secuboid.playercontainer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.utilities.StringChanges;

public final class PlayerContainers {

    private final Secuboid secuboid;

    /**
     * Contains Player Containers from Type. The object can be a player container
     * instance without parameter, a map of group/permission player containers from
     * a String or a map of players from a UUID.
     */
    private final Map<PlayerContainerType, Object> typeToPlayerContainerObj;

    public PlayerContainers(final Secuboid secuboid) {
        this.secuboid = secuboid;

        typeToPlayerContainerObj = new EnumMap<>(PlayerContainerType.class);
        for (final PlayerContainerType playerContainerType : PlayerContainerType.values()) {
            if (!playerContainerType.hasParameter()) {
                // The object is only an instance
                typeToPlayerContainerObj.put(playerContainerType,
                        playerContainerType.createPCBiFunction.apply(secuboid, null));
            } else if (playerContainerType == PlayerContainerType.PLAYER) {
                // The object is a set of player container players from UUID
                typeToPlayerContainerObj.put(playerContainerType, new HashMap<UUID, PlayerContainerPlayer>());
            } else {
                // The object is a set of player container from a String parameter
                typeToPlayerContainerObj.put(playerContainerType, new HashMap<String, PlayerContainer>());
            }
        }
    }

    /**
     * Gets a player container. Only available for a player container with no
     * paramater (no group, permission or player).
     *
     * @param playerContainerType the player container type
     * @return the player container
     */
    public PlayerContainer getPlayerContainer(final PlayerContainerType playerContainerType) {
        if (!playerContainerType.hasParameter()) {
            return (PlayerContainer) typeToPlayerContainerObj.get(playerContainerType);
        }
        return null;
    }

    /**
     * Gets or adds a player container player only. You must add a UUID.
     *
     * @param playerUUID the player uuid
     * @return the player container player
     */
    public PlayerContainerPlayer getOrAddPlayerContainerPlayer(UUID playerUUID) {
        return (PlayerContainerPlayer) getOrAddPlayerContainer(PlayerContainerType.PLAYER, Optional.empty(),
                Optional.of(playerUUID));
    }

    /**
     * Gets or adds a player container. Group and permission types must have a
     * parameter. Player type checks first for a UUID then a UUID in String format.
     *
     * @param playerContainerType the player container type
     * @param parameterOpt        the parameter optional
     * @param uuidOpt             the uuid optional
     * @return the player container
     */
    public PlayerContainer getOrAddPlayerContainer(final PlayerContainerType playerContainerType,
            final Optional<String> parameterOpt, final Optional<UUID> uuidOpt) {

        if (playerContainerType == null) {
            return null;
        }

        // Return singleton without parameter first
        if (!playerContainerType.hasParameter()) {
            return (PlayerContainer) typeToPlayerContainerObj.get(playerContainerType);
        }

        switch (playerContainerType) {
            case PLAYER:
            case PLAYERNAME:
                @SuppressWarnings("unchecked")
                final Map<UUID, PlayerContainerPlayer> uuidToPCP = (Map<UUID, PlayerContainerPlayer>) typeToPlayerContainerObj
                        .get(PlayerContainerType.PLAYER);
                final UUID playerUUID;
                if (uuidOpt.isPresent()) {
                    playerUUID = uuidOpt.get();
                } else {
                    // First check if the ID is valid or was connected to the server
                    final String parameter = parameterOpt.get();
                    try {
                        playerUUID = UUID.fromString(parameter.replaceFirst("ID-", ""));
                    } catch (final IllegalArgumentException ex) {
                        // If there is an error, just return a temporary PlayerName without adding it in
                        // the map
                        return new PlayerContainerPlayerName(parameter);
                    }
                }
                // If not null, assign the value to a new PlayerContainer
                return uuidToPCP.computeIfAbsent(playerUUID, u -> new PlayerContainerPlayer(secuboid, u));
            default:
                // With String parameter
                @SuppressWarnings("unchecked")
                final Map<String, PlayerContainer> parameterToPC = (Map<String, PlayerContainer>) typeToPlayerContainerObj
                        .get(playerContainerType);
                return parameterToPC.computeIfAbsent(parameterOpt.get(),
                        p -> playerContainerType.createPCBiFunction.apply(secuboid, p));
        }
    }

    /**
     * Gets the player container from string.
     *
     * @param string the player container from string
     * @return the string
     */
    public PlayerContainer getPlayerContainerFromFileFormat(final String string) {
        final String strs[] = StringChanges.splitAddVoid(string, ":");
        final PlayerContainerType type = PlayerContainerType.getFromString(strs[0]);
        final Optional<String> parameterOpt = Optional.ofNullable(strs[1].isEmpty() ? null : strs[1]);
        return getOrAddPlayerContainer(type, parameterOpt, Optional.empty());
    }

}