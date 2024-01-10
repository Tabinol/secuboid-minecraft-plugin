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
package app.secuboid.core.thread;

import app.secuboid.api.thread.QueueProcessor;
import app.secuboid.api.thread.QueueThread;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;

import static app.secuboid.core.messages.Log.log;
import static java.lang.String.format;

public class QueueThreadImpl<T, R> implements QueueThread<T, R> {

    private static final long TIME_WAITING_THREAD_MILLIS = Duration.ofSeconds(10).toMillis();

    private final Plugin plugin;
    private final String threadName;
    private final QueueProcessor<T, R> queueProcessor;
    private final BlockingQueue<QueueThreadElement<T, R>> taskQueue;


    private QueueThreadRun<T, R> thread;

    public QueueThreadImpl(Plugin plugin, String threadName,
                           QueueProcessor<T, R> queueProcessor) {
        this.plugin = plugin;
        this.threadName = threadName;
        this.queueProcessor = queueProcessor;
        taskQueue = new LinkedBlockingQueue<>();
        thread = null;
    }

    @Override
    public void start() {

        if (isAlive()) {
            log().log(Level.WARNING,
                    () -> format("Thread \"%s\" is already started and it shouldn't!", threadName));
        }

        thread = new QueueThreadRun<>(plugin, queueProcessor, taskQueue);
        thread.setName(threadName);

        thread.start();
    }

    @Override
    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    @Override
    public void addElement(T t) {
        QueueThreadElement<T, R> element = new QueueThreadElement<>(t, null, null, false);
        taskQueue.add(element);
    }

    @Override
    public void addElement(T t, BlockingQueue<Object> resultQueue, boolean isSet) {
        QueueThreadElement<T, R> element = new QueueThreadElement<>(t, resultQueue, null, isSet);
        taskQueue.add(element);
    }

    @Override
    public void addElement(T t, Consumer<R> callback) {
        QueueThreadElement<T, R> element = new QueueThreadElement<>(t, null, callback, false);
        taskQueue.add(element);
    }

    @Override
    public void stop() {
        // Check if the thread is not death
        if (thread == null || !thread.isAlive()) {
            log().log(Level.SEVERE, () -> format("Thread \"%s\" is already stopped!", threadName));
            return;
        }

        // Send shutdown thread request
        QueueThreadElement<T, R> element = new QueueThreadElement<>(null, null, null, false);
        taskQueue.add(element);

        // Waiting for thread
        waitForThread();

        // If the thread is not stopped
        if (thread.isAlive()) {
            log().log(Level.WARNING, () -> format("Unable to stop gracefully the thread \"%s\"!", threadName));
            thread.interrupt();
            waitForThread();
        }

        thread = null;
    }

    private void waitForThread() {
        try {
            thread.join(TIME_WAITING_THREAD_MILLIS);
        } catch (InterruptedException e) {
            log().log(Level.WARNING, e, () -> format("Thread \"%s\" interrupted!", threadName));
            Thread.currentThread().interrupt();
        }
    }
}
