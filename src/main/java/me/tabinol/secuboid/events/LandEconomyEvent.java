/*
 Secuboid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

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
package me.tabinol.secuboid.events;

import me.tabinol.secuboid.lands.RealLand;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import org.bukkit.event.HandlerList;

/**
 * The Class LandEconomyEvent. When a lend is rented/unrented or sel.
 * Created by Tabinol on 17-02-19.
 */
public class LandEconomyEvent extends LandEvent {

    public enum LandEconomyReason {
        /**
         * Rent a land
         */
        RENT,
        /**
         * Renew a rented land
         */
        RENT_RENEW,
        /**
         * Unrent a land
         */
        UNRENT,
        /**
         * Sell a land
         */
        SELL
    }

    /**
     * The Constant handlers.
     */
    private static final HandlerList handlers = new HandlerList();

    private final LandEconomyReason landEconomyReason;
    private final PlayerContainer fromPC;
    private final PlayerContainer toPC;

    public LandEconomyEvent(final RealLand land, final LandEconomyReason landEconomyReason,
                            final PlayerContainer fromPC, PlayerContainer toPC) {
        super(land);
        this.landEconomyReason = landEconomyReason;
        this.fromPC = fromPC;
        this.toPC = toPC;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the economy reason (action) : rent, rent renew, unrent or sell land.
     *
     * @return the land economy reason
     */
    public LandEconomyReason getLandEconomyReason() {
        return landEconomyReason;
    }

    /**
     * Gets the owner or the land seller.
     *
     * @return the seller
     */
    public PlayerContainer getFromPlayer() {
        return fromPC;
    }

    /**
     * Gets the tenant or the land buyer.
     *
     * @return the seller
     */
    public PlayerContainer getToPlayer() {
        return toPC;
    }
}
