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
package me.tabinol.secuboid.lands.approve;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.utilities.SecuboidRunnable;

/**
 * The Class ApproveNotif.
 */
public class ApproveNotif extends SecuboidRunnable {

    /**
     * The Constant PERM_APPROVE.
     */
    private static final String PERM_APPROVE = "secuboid.collisionapprove";

    /**
     * Instantiates a new approve notif.
     *
     * @param secuboid secuboid instance
     */
    public ApproveNotif(final Secuboid secuboid) {
        super(secuboid);
    }

    /**
     * Run approve notif later.
     */
    public void runApproveNotifLater() {

        final long notifyTime = secuboid.getConf().getApproveNotifyTime();

        // Start only if notification is activated in configuration
        if (notifyTime != 0) {
            this.runLater(notifyTime, true);
        }

    }

    @Override
    public void run() {

        int lstCount;

        if ((lstCount = secuboid.getLands().getApproves().getApproveList().size()) != 0) {

            // If there is some notification to done
            notifyListApprove(lstCount);
        }
    }

    /**
     * Notify for approve.
     *
     * @param landName   the land name
     * @param playerName the player name
     */
    void notifyForApprove(final String landName, final String playerName) {
        notifyPlayer(
                secuboid.getLanguage().getMessage("COLLISION.SHOW.NOTIFYLAND", landName, playerName + ChatColor.GREEN));
    }

    /**
     * Notify list approve.
     *
     * @param lstCount the lst count
     */
    private void notifyListApprove(final int lstCount) {
        notifyPlayer(secuboid.getLanguage().getMessage("COLLISION.SHOW.NOTIFY", lstCount + ""));
    }

    /**
     * Notify the player with a message.
     *
     * @param message the message
     */
    private void notifyPlayer(final String message) {
        for (final Player players : secuboid.getServer().getOnlinePlayers()) {
            if (players.hasPermission(PERM_APPROVE)) {
                players.sendMessage(ChatColor.GREEN + "[Secuboid] " + message);
            }
        }
        secuboid.getLogger().log(Level.INFO, "[Secuboid] {0}", message);
    }
}
