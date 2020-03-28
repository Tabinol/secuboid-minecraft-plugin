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
package me.tabinol.secuboid.storage.flat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.playerscache.PlayerCacheEntry;
import me.tabinol.secuboid.utilities.MavenAppProperties;

/**
 * PlayersCacheFlat
 */
public class PlayersCacheFlat {

    private final Secuboid secuboid;

    /**
     * The player cache version.
     */
    private final int playerCacheVersion;

    /**
     * The file.
     */
    private final File file;

    public PlayersCacheFlat(final Secuboid secuboid) {
        this.secuboid = secuboid;
        playerCacheVersion = MavenAppProperties.getPropertyInt("playersCacheVersion", 1);
        final String fileName = secuboid.getDataFolder() + "/" + "playerscache.conf";
        file = new File(fileName);
    }

    /**
     * Load all.
     */
    void loadPlayersCache() {
        final List<PlayerCacheEntry> playerCacheEntries = new ArrayList<>();

        try {
            BufferedReader br;

            try {
                br = new BufferedReader(new FileReader(file));
            } catch (final FileNotFoundException ex) {
                // The file is just not yet created
                return;
            }

            @SuppressWarnings("unused")
            final int version = Integer.parseInt(br.readLine().split(":")[1]);
            br.readLine(); // Read remark line

            String str;

            while ((str = br.readLine()) != null && !str.isEmpty()) {
                // Read from String "PlayerUUID:LastInventoryName:isCreative"
                final String[] strs = str.split(":");

                final String name = strs[0];
                final UUID uuid = UUID.fromString(strs[1]);
                playerCacheEntries.add(new PlayerCacheEntry(uuid, name));
            }
            br.close();

        } catch (final IOException ex) {
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Unable to load players cache file %s", file.getAbsolutePath()), ex);
        }
        secuboid.getPlayersCache().loadPlayerscache(Collections.unmodifiableList(playerCacheEntries));
    }

    /**
     * Save all.
     */
    void savePlayersCache() {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            bw.write("Version:" + playerCacheVersion);
            bw.newLine();
            bw.write("# Name:PlayerUUID");
            bw.newLine();

            for (final PlayerCacheEntry playerCacheEntry : secuboid.getPlayersCache().getPlayerCacheEntries()) {
                // Write to String "Name:PlayerUUID"
                bw.write(playerCacheEntry.getName() + ":" + playerCacheEntry.getUUID());
                bw.newLine();
            }
            bw.close();

        } catch (final IOException ex) {
            secuboid.getLogger().log(Level.SEVERE,
                    String.format("Unable to save players cache file %s", file.getAbsolutePath()), ex);
        }
    }
}