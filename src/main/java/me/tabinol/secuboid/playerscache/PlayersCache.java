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
package me.tabinol.secuboid.playerscache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.executor.CommandPlayerThreadExec;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayerName;
import me.tabinol.secuboid.playerscache.minecraftapi.HttpProfileRepository;
import me.tabinol.secuboid.playerscache.minecraftapi.Profile;
import me.tabinol.secuboid.storage.StorageThread.SaveActionEnum;
import me.tabinol.secuboid.utilities.SecuboidQueueThread;

/**
 * The Class PlayersCache.
 */
public final class PlayersCache extends SecuboidQueueThread<PlayersCache.PlayerCacheable> {

    /**
     * The players cache list.
     */
    private final Map<String, PlayerCacheEntry> playersCacheList;

    /**
     * The players rev cache list.
     */
    private final Map<UUID, PlayerCacheEntry> playersRevCacheList;

    /**
     * The http profile repository.
     */
    private final HttpProfileRepository httpProfileRepository;

    /**
     * This interface is here to represent objects in queue.
     */
    protected interface PlayerCacheable {
        // Empty interface
    }

    /**
     * The Class OutputRequest.
     */
    private static class OutputRequest implements PlayerCacheable {

        /**
         * The command exec.
         */
        CommandPlayerThreadExec commandExec;

        /**
         * The player names.
         */
        String[] playerNames;

        /**
         * Instantiates a new output request.
         *
         * @param commandExec the command exec
         * @param playerNames the player names
         */
        OutputRequest(final CommandPlayerThreadExec commandExec, final String[] playerNames) {
            this.commandExec = commandExec;
            this.playerNames = playerNames;
        }
    }

    /**
     * Instantiates a new players cache.
     *
     * @param secuboid secuboid instance
     */
    public PlayersCache(final Secuboid secuboid) {
        super(secuboid, "Secuboid Players cache");
        playersCacheList = new HashMap<>();
        playersRevCacheList = new HashMap<>();
        httpProfileRepository = new HttpProfileRepository("minecraft");
    }

    /**
     * Load players cache (Internal).
     * 
     * @param playerCacheEntries the player cache entries
     */
    public void loadPlayerscache(final List<PlayerCacheEntry> playerCacheEntries) {
        playersCacheList.clear();
        playersRevCacheList.clear();
        playerCacheEntries.forEach(playerCacheEntry -> {
            playersCacheList.put(playerCacheEntry.getName(), playerCacheEntry);
            playersRevCacheList.put(playerCacheEntry.getUUID(), playerCacheEntry);
        });
    }

    /**
     * Update player.
     *
     * @param uuid       the uuid
     * @param playerName the player name
     */
    public void updatePlayer(final UUID uuid, final String playerName) {
        addElement(new PlayerCacheEntry(uuid, playerName));
    }

    /**
     * Gets all player names in cache.
     * 
     * @return a set of player names
     */
    public Set<String> getPlayerNames() {
        return playersCacheList.keySet();
    }

    /**
     * Gets the player cache entries.
     * 
     * @return an unmodifiable collection
     */
    public Collection<PlayerCacheEntry> getPlayerCacheEntries() {
        return Collections.unmodifiableCollection(playersCacheList.values());
    }

    public String getNameFromUUID(final UUID uuid) {
        final PlayerCacheEntry entry = playersRevCacheList.get(uuid);

        if (entry != null) {
            return entry.getName();
        }

        return null;
    }

    /**
     * Gets the UUID with names.
     *
     * @param commandExec the command exec
     * @param pc          the pc
     */
    public void getUUIDWithNames(final CommandPlayerThreadExec commandExec, final PlayerContainer pc) {

        if (pc != null && pc instanceof PlayerContainerPlayerName) {
            getUUIDWithNames(commandExec, pc.getName());
        } else {
            // Start a null player name, nothing to resolve
            getUUIDWithNames(commandExec);
        }
    }

    /**
     * Gets the UUID with names.
     *
     * @param commandExec the command exec
     * @param playerNames the player names
     */
    private void getUUIDWithNames(final CommandPlayerThreadExec commandExec, final String... playerNames) {
        addElement(new OutputRequest(commandExec, playerNames));
    }

    @Override
    protected void doElement(final PlayerCacheable outputRequestObj) {
        // Check if the object is an output request
        if (outputRequestObj instanceof OutputRequest) {
            final OutputRequest outputRequest = (OutputRequest) outputRequestObj;
            final int length = outputRequest.playerNames.length;

            final PlayerCacheEntry[] entries = new PlayerCacheEntry[length];

            // Pass 1 check in playersCache or null
            final ArrayList<String> names = new ArrayList<String>(); // Pass 2 list
            for (int t = 0; t < length; t++) {
                entries[t] = playersCacheList.get(outputRequest.playerNames[t].toLowerCase());
                if (entries[t] == null) {
                    // Add in a list for pass 2
                    names.add(outputRequest.playerNames[t]);
                }
            }

            // Pass 2 check in Minecraft website
            if (!names.isEmpty()) {
                final Profile[] profiles = httpProfileRepository
                        .findProfilesByNames(names.toArray(new String[names.size()]));
                for (final Profile profile : profiles) {
                    // Put in the correct position
                    int compt = 0;

                    while (compt != length) {
                        if (entries[compt] == null) {
                            final UUID uuid = stringToUUID(profile.getId());
                            if (uuid != null) {
                                entries[compt] = new PlayerCacheEntry(uuid, profile.getName());
                                // Update now
                                updatePlayerInlist(entries[compt]);
                            }
                        }
                        compt++;
                    }
                }
            }
            // Return the output of the request on the main thread
            final ReturnPlayerToCommand returnToCommand = new ReturnPlayerToCommand(secuboid, outputRequest.commandExec,
                    entries);
            Bukkit.getScheduler().callSyncMethod(secuboid, returnToCommand);
        } else if (outputRequestObj instanceof PlayerCacheEntry) {
            // Update playerList
            updatePlayerInlist((PlayerCacheEntry) outputRequestObj);
        }
    }

    private void updatePlayerInlist(final PlayerCacheEntry entry) {

        final String nameLower = entry.getName().toLowerCase();
        if (playersCacheList.get(nameLower) == null) {

            // Update to do
            if (playersRevCacheList.get(entry.getUUID()) != null) {
                // Player exist, but name changed
                playersCacheList.remove(nameLower);
            }

            // update name
            playersCacheList.put(nameLower, entry);
            playersRevCacheList.put(entry.getUUID(), entry);

            // Request save
            secuboid.getStorageThread().addSaveAction(SaveActionEnum.PLAYERS_CACHE_SAVE, Optional.of(entry));
        }
    }

    private UUID stringToUUID(final String stId) {
        final String convId = stId.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
        UUID uuid;
        try {
            uuid = UUID.fromString(convId);
        } catch (final IllegalArgumentException ex) {
            // error? return null
            return null;
        }

        return uuid;
    }
}
