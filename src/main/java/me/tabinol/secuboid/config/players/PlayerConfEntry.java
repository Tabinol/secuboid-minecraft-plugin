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
package me.tabinol.secuboid.config.players;

// Entries for each player
import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.commands.ChatPage;
import me.tabinol.secuboid.commands.ConfirmEntry;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.playercontainer.PlayerContainerPlayer;
import me.tabinol.secuboid.selection.PlayerSelection;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The Class PlayerConfEntry.
 */
public class PlayerConfEntry {

    /**
     * The sender.
     */
    private final CommandSender sender; // The player (or sender)

    /**
     * The player.
     */
    private final Player player; // The player (if is not console)

    /**
     * The player selection.
     */
    private final PlayerSelection playerSelection; // Player Lands, areas and visual selections

    /**
     * The admin mod.
     */
    private boolean adminMod = false; // If the player is in Admin Mod

    /**
     * The confirm.
     */
    private ConfirmEntry confirm = null; // "/secuboid confirm" command

    /**
     * The chat page.
     */
    private ChatPage chatPage = null; // pages for "/secuboid page" command

    /**
     * The last move update.
     */
    private long lastMoveUpdate = 0; // Time of lastupdate for PlayerEvents

    /**
     * The last land.
     */
    private Land lastLand = null; // Last Land for player

    /**
     * The last loc.
     */
    private Location lastLoc = null; // Present location

    /**
     * The tp cancel.
     */
    private boolean tpCancel = false; // If the player has a teleportation cacelled

    /**
     * The cancel select.
     */
    private PlayerAutoCancelSelect cancelSelect = null; // Auto cancel selection system

    /**
     * The pcp.
     */
    private PlayerContainerPlayer pcp; // PlayerContainerPlayer for this player

    /**
     * Instantiates a new player conf entry.
     *
     * @param sender the sender
     */
    PlayerConfEntry(CommandSender sender) {

	this.sender = sender;
	if (sender instanceof Player) {
	    player = (Player) sender;
	    playerSelection = new PlayerSelection(this);
	    pcp = new PlayerContainerPlayer(player.getUniqueId());
	} else {
	    player = null;
	    playerSelection = null;
	    pcp = null;
	}
    }

    /**
     *
     * @return
     */
    public PlayerContainerPlayer getPlayerContainer() {

	return pcp;
    }

    /**
     *
     * @return
     */
    public CommandSender getSender() {

	return sender;
    }

    /**
     *
     * @return
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
     *
     * @return
     */
    public boolean isAdminMod() {

	// Security for adminmod
	if (adminMod == true && !sender.hasPermission("secuboid.adminmod")) {
	    adminMod = false;
	    return false;
	}

	return adminMod;
    }

    /**
     * Sets the admin mod.
     *
     * @param value the new admin mod
     */
    public void setAdminMod(boolean value) {

	adminMod = value;
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
    public void setConfirm(ConfirmEntry entry) {

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
    public void setChatPage(ChatPage page) {

	chatPage = page;
    }

    /**
     *
     * @return
     */
    public long getLastMoveUpdate() {

	return lastMoveUpdate;
    }

    /**
     * Sets the last move update.
     *
     * @param lastMove the new last move update
     */
    public void setLastMoveUpdate(Long lastMove) {

	lastMoveUpdate = lastMove;
    }

    /**
     *
     * @return
     */
    public Land getLastLand() {

	return lastLand;
    }

    /**
     * Sets the last land.
     *
     * @param land the new last land
     */
    public void setLastLand(Land land) {

	lastLand = land;
    }

    /**
     *
     * @return
     */
    public Location getLastLoc() {

	return lastLoc;
    }

    /**
     * Sets the last loc.
     *
     * @param loc the new last loc
     */
    public void setLastLoc(Location loc) {

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
    public void setTpCancel(boolean tpCancel) {

	this.tpCancel = tpCancel;
    }

    // Set auto cancel select
    /**
     * Sets the auto cancel select.
     *
     * @param value the new auto cancel select
     */
    public void setAutoCancelSelect(boolean value) {

	Long timeTick = Secuboid.getThisPlugin().getConf().getSelectAutoCancel();

	if (timeTick == 0) {
	    return;
	}

	if (cancelSelect == null && value == true) {
	    cancelSelect = new PlayerAutoCancelSelect(this);
	}

	if (cancelSelect == null) {
	    return;
	}

	if (value == true) {

	    // Schedule task
	    cancelSelect.runLater(timeTick, false);
	} else {

	    // Stop!
	    cancelSelect.stopNextRun();
	}
    }
}
