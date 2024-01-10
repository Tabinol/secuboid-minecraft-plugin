/*
 *  Secuboid: LandService and Protection plugin for Minecraft server
 *  Copyright (C) 2014 Tabinol
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.secuboid.api.thread;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * This is a class to create a waiting queue thread.
 */
public interface QueueThread<T, R> {

    /**
     * Starts the thread.
     */
    void start();

    /**
     * Checks if the queue is alive.
     *
     * @return true if the queue is alive
     */
    boolean isAlive();

    /**
     * Adds an element without callback.
     *
     * @param t the element to send
     */
    void addElement(T t);

    /**
     * Adds an element and blocking queue for sync.
     *
     * @param t           the element to send
     * @param resultQueue the blocking queue for result
     * @param isSet       is only one or a set?
     */
    void addElement(T t, BlockingQueue<Object> resultQueue, boolean isSet);

    /**
     * Adds an element with callback.
     *
     * @param t        the element to send
     * @param callback the callback or null
     */
    void addElement(T t, Consumer<R> callback);

    /**
     * Waits for the last element and stop.
     */
    void stop();
}
