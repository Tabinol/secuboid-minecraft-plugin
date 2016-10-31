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
package me.tabinol.secuboid.utilities;

import java.util.HashMap;
import me.tabinol.secuboid.Secuboid;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The Class ExpirableTreeMap.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class ExpirableHashMap<K, V> extends HashMap<K, V> {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -8255110767996977825L;

    /**
     * The delay in tick.
     */
    private final long delay;

    /**
     * Instantiates a new expirable tree map.
     *
     * @param delay the delay (in ticks)
     */
    public ExpirableHashMap(long delay) {

	super();
	this.delay = delay;
    }

    @Override
    public Object clone() {
	return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * The Class BestBefored.
     */
    private class BestBefored extends BukkitRunnable {

	/**
	 * The key.
	 */
	K key;

	/**
	 * Instantiates a new best befored.
	 *
	 * @param key the key
	 */
	private BestBefored(K key) {

	    this.key = key;
	}

	/* (non-Javadoc)
         * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    remove(key);
	}
    }

    /* (non-Javadoc)
     * @see java.util.TreeMap#put(java.lang.Object, java.lang.Object)
     */
    /**
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public V put(K key, V value) {

	new BestBefored(key).runTaskLater(Secuboid.getThisPlugin(), delay);
	return super.put(key, value);
    }
}
