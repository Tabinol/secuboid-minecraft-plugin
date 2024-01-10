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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;

import static app.secuboid.core.messages.Log.log;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.logging.Level.SEVERE;

class QueueThreadRun<T, R> extends Thread {

    private final Plugin plugin;
    private final QueueProcessor<T, R> queueProcessor;
    private final BlockingQueue<QueueThreadElement<T, R>> taskQueue;

    QueueThreadRun(Plugin plugin, QueueProcessor<T, R> queueProcessor,
                   BlockingQueue<QueueThreadElement<T, R>> taskQueue) {
        this.plugin = plugin;
        this.queueProcessor = queueProcessor;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        try {
            loopQueue();
        } catch (InterruptedException e) {
            log().log(Level.WARNING, e, () -> format("Interruption requested for thread \"%s\".", getName()));
            Thread.currentThread().interrupt();
        }
    }

    private void loopQueue() throws InterruptedException {
        QueueThreadElement<T, R> element;

        // Loop unit there is no empty (stop thread request) queueProcessor.
        while ((element = taskQueue.take()).getT() != null) {

            Object result;
            if (element.isSet()) {
                result = processElements(element);
            } else {
                result = processElement(element);
            }


            if (result != null && element.getResultQueue() != null) {
                element.getResultQueue().put(result);
            }
        }
    }

    private R processElement(QueueThreadElement<T, R> element) {
        T t = element.getT();
        Consumer<R> callback = element.getCallback();

        if (t == null) {
            log().log(SEVERE, "An element in thread has a 't' null: {}", element);
            return null;
        }

        R r = queueProcessor.process(t);

        if (callback != null) {

            Callable<Void> callableCallback = () -> {
                try {
                    callback.accept(r);
                } catch (RuntimeException e) {
                    log().log(SEVERE, "Exception in main thread callback", e);
                }

                return null;
            };

            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.callSyncMethod(plugin, callableCallback);
        }

        return r;
    }

    private Set<R> processElements(QueueThreadElement<T, R> element) {
        T t = element.getT();

        if (t == null) {
            log().log(SEVERE, "An element in thread has a 't' null: {}", element);
            return emptySet();
        }

        return queueProcessor.processMultiple(t);
    }
}
