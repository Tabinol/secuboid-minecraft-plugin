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
package me.tabinol.secuboid.players;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.ConfirmEntry;
import me.tabinol.secuboid.inventories.PlayerInventoryCache;
import me.tabinol.secuboid.lands.LandPermissionsFlags;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.selection.PlayerSelection;

/**
 * The Class PlayerConfEntry. Entries for each players.
 */
public final class PlayerConfEntry {

    private final Secuboid secuboid;

    /**
     * The player (or sender).
     */
    private final CommandSender sender;

    /**
     * The player (if is not console).
     */
    private final Player player;

    /**
     * Player Lands, areas and visual selections
     */
    private final PlayerSelection playerSelection;

    /**
     * Plauer inventory cache.
     */
    private final PlayerInventoryCache playerInventoryCache;

    /**
     * If the player is in Admin Mod
     */
    private boolean adminMode = false;

    /**
     * secuboid confirm command
     */
    private ConfirmEntry confirm = null;

    /**
     * pages for /secuboid page command
     */
    private ChatPage chatPage = null;

    /**
     * Time of lastupdate for PlayerEvents
     */
    private long lastMoveUpdate = 0;

    /**
     * Last Land for player
     */
    private LandPermissionsFlags lastLandPermissionsFlags = null;

    /**
     * Present location
     */
    private Location lastLoc = null;

    /**
     * If the player has a teleportation cancelled
     */
    private boolean tpCancel = false;

    /**
     * Auto cancel selection system
     */
    private PlayerAutoCancelSelect cancelSelect = null;

    /**
     * PlayerContainerPlayer for this player
     */
    private final PlayerContainerPlayer pcp;

    /**
     * Top selection. Positive is absolute, negative is from player feet.
     */
    private int selectionTop;

    /**
     * Bottom selection. Positive is absolute, negative is from player feet.
     */
    private int selectionBottom;

    /**
     * Radius selection.
     */
    private int selectionRadius;

    /**
     * Instantiates a new player conf entry.
     *
     * @param secuboid secuboid instance
     * @param sender   the sender
     */
    PlayerConfEntry(final Secuboid secuboid, final CommandSender sender,
            final PlayerInventoryCache playerInventoryCache) {
        this.secuboid = secuboid;
        this.sender = sender;
        this.playerInventoryCache = playerInventoryCache;

        if (sender instanceof Player) {
            player = (Player) sender;
            playerSelection = new PlayerSelection(secuboid, this);
            pcp = new PlayerContainerPlayer(secuboid, player.getUniqueId());
        } else {
            player = null;
            playerSelection = null;
            pcp = null;
        }

        selectionTop = secuboid.getConf().getDefaultTop();
        selectionBottom = secuboid.getConf().getDefaultBottom();
        selectionRadius = secuboid.getConf().getDefaultRadius();
    }

    /**
     * Gets the player container for this player.
     *
     * @return the player container
     */
    public PlayerContainerPlayer getPlayerContainer() {
        return pcp;
    }

    /**
     * Gets the command sender for this player.
     *
     * @return the command sender
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the Bukkit player instance.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the selection.
     *
     * @return the selection
     */
    public PlayerSelection getSelection() {
        return playerSelection;
    }

    /**
     * Gets the player inventory cache.
     * 
     * @return the player inventory cache
     */
    public PlayerInventoryCache getPlayerInventoryCache() {
        return playerInventoryCache;
    }

    /**
     * Is the player admin mode?
     *
     * @return true if the player is admin mode
     */
    public boolean isAdminMode() {

        // Security for adminmode
        if (adminMode && !sender.hasPermission("secuboid.adminmode")) {
            adminMode = false;
            return false;
        }

        return adminMode;
    }

    /**
     * Sets the admin mod.
     *
     * @param value the new admin mod
     */
    public void setAdminMode(final boolean value) {
        adminMode = value;
    }

    /**
     * Gets the confirm.
     *
     * @return the confirm
     */
    public ConfirmEntry getConfirm() {
        return confirm;
    }

    /**
     * Sets the confirm.
     *
     * @param entry the new confirm
     */
    public void setConfirm(final ConfirmEntry entry) {
        confirm = entry;
    }

    /**
     * Gets the chat page.
     *
     * @return the chat page
     */
    public ChatPage getChatPage() {
        return chatPage;
    }

    /**
     * Sets the chat page.
     *
     * @param page the new chat page
     */
    public void setChatPage(final ChatPage page) {
        chatPage = page;
    }

    /**
     * Gets the last move update time.
     *
     * @return update time in long format
     */
    public long getLastMoveUpdate() {
        return lastMoveUpdate;
    }

    /**
     * Sets the last move update.
     *
     * @param lastMove the new last move update
     */
    public void setLastMoveUpdate(final Long lastMove) {
        lastMoveUpdate = lastMove;
    }

    /**
     * Gets the last land permissions flags.
     *
     * @return the last land permissions flags
     */
    public LandPermissionsFlags getLastLandPermissionsFlags() {
        return lastLandPermissionsFlags;
    }

    /**
     * Sets the last land permissions flags.
     *
     * @param landPermissionsFlags the new last land permissions flags
     */
    public void setLastLandPermissionsFlags(final LandPermissionsFlags landPermissionsFlags) {
        lastLandPermissionsFlags = landPermissionsFlags;
    }

    /**
     * Gets the last player location.
     *
     * @return the last location
     */
    public Location getLastLoc() {
        return lastLoc;
    }

    /**
     * Sets the last player location.
     *
     * @param loc the new last location
     */
    public void setLastLoc(final Location loc) {
        lastLoc = loc;
    }

    /**
     * Checks for tp cancel.
     *
     * @return true, if successful
     */
    public boolean hasTpCancel() {
        return tpCancel;
    }

    /**
     * Sets the tp cancel.
     *
     * @param tpCancel the new tp cancel
     */
    public void setTpCancel(final boolean tpCancel) {
        this.tpCancel = tpCancel;
    }

    /**
     * Sets the auto cancel select.
     *
     * @param value the new auto cancel select
     */
    public void setAutoCancelSelect(final boolean value) {

        final Long timeTick = secuboid.getConf().getSelectAutoCancel();

        if (timeTick == 0) {
            return;
        }

        if (cancelSelect == null && value) {
            cancelSelect = new PlayerAutoCancelSelect(secuboid, this);
        }

        if (cancelSelect == null) {
            return;
        }

        if (value) {

            // Schedule task
            cancelSelect.runLater(timeTick, false);
        } else {

            // Stop!
            cancelSelect.stopNextRun();
        }
    }

    /**
     * Gets top selection. Positive is absolute, negative is from player feet.
     *
     * @return the top selection
     */
    public int getSelectionTop() {
        return selectionTop;
    }

    /**
     * Sets top selection. Positive is absolute, negative is from player feet.
     *
     * @param selectionTop the top selection
     */
    public void setSelectionTop(final int selectionTop) {
        this.selectionTop = selectionTop;
    }

    /**
     * Gets bottom selection. Positive is absolute, negative is from player feet.
     *
     * @return the bottom selection
     */
    public int getSelectionBottom() {
        return selectionBottom;
    }

    /**
     * Sets bottom selection. Positive is absolute, negative is from player feet.
     *
     * @param selectionBottom the bottom selection
     */
    public void setSelectionBottom(final int selectionBottom) {
        this.selectionBottom = selectionBottom;
    }

    /**
     * Gets radius selection.
     *
     * @return the radius selection
     */
    public int getSelectionRadius() {
        return selectionRadius;
    }

    /**
     * Sets radius selection.
     *
     * @param selectionRadius the radius selection
     */
    public void setSelectionRadius(final int selectionRadius) {
        this.selectionRadius = selectionRadius;
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }
}
