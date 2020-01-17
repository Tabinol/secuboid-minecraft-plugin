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

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import me.tabinol.secuboid.Secuboid;

/**
 * Abstract class for task queue in a thread.
 */
public abstract class SecuboidQueueThread<T> extends Thread {

    private static final long TIME_WAITING_THREAD_MILLIS = Duration.ofSeconds(10).toMillis();

    protected final Secuboid secuboid;

    /**
     * The blocking queue for requests.
     */
    private final BlockingQueue<Optional<T>> taskQueue;

    /**
     * Activate or disactivate the queue. If false, there is no item added to te
     * queue.
     */
    protected boolean isQueueActive;

    /**
     * Instantiates a new Secuboid queue thread.
     * 
     * @param secuboid   the secuboid instance
     * @param threadName the name of the thread
     */
    protected SecuboidQueueThread(final Secuboid secuboid, final String threadName) {
        this.secuboid = secuboid;
        taskQueue = new LinkedBlockingQueue<>();
        isQueueActive = true;
        setName(threadName);
    }

    @Override
    public final void run() {
        try {
            loopQueue();
        } catch (final InterruptedException e) {
            secuboid.getLogger().log(Level.SEVERE, String.format("Interruption requested for thread \"%s\"", getName()),
                    e);
        }
    }

    private void loopQueue() throws InterruptedException {
        Optional<T> tOpt;
        // Loop unit there is no empty (stop thread request) element.
        while ((tOpt = taskQueue.take()).isPresent()) {
            doElement(tOpt.get());
        }
    }

    public final void addElement(final T t) {
        if (isQueueActive) {
            taskQueue.add(Optional.of(t));
        }
    }

    /**
     * Execute what it should execute inside the loop.
     * 
     * @param t the element
     */
    protected abstract void doElement(T t);

    /**
     * Stop next run.
     */
    public final void stopNextRun() {
        // Check if the thread is not death
        if (!isAlive()) {
            secuboid.getLogger().severe(String.format("Thread \"%s\" is already stopped and it shouldn't!", getName()));
            return;
        }

        // Send shutdown thread request
        taskQueue.add(Optional.empty());

        // Waiting for thread
        try {
            join(TIME_WAITING_THREAD_MILLIS);
        } catch (final InterruptedException e) {
            secuboid.getLogger().severe(String.format("Thread \"%s\" interrupted!", getName()));
            return;
        }

        // If the thread is not stopped
        if (isAlive()) {
            secuboid.getLogger().severe(String.format("Unable to stop gracefully the thread \"%s\"!", getName()));
            interrupt();
        }
    }
}