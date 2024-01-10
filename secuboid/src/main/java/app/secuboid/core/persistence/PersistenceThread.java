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

package app.secuboid.core.persistence;

import app.secuboid.api.persistence.JPA;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import static app.secuboid.core.messages.Log.log;
import static java.util.logging.Level.SEVERE;

@AllArgsConstructor
public class PersistenceThread extends Thread {

    private final JavaPlugin javaPlugin;
    private final BukkitScheduler scheduler;
    private final PersistenceSessionService persistenceSessionService;

    private final BlockingQueue<PersistenceElement<?>> queue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Object> threadSyncQueue = new LinkedBlockingQueue<>(1);

    void shutdown() {
        interrupt();
        try {
            join();
        } catch (InterruptedException e) {
            log().severe("Unwanted interruption on persistenceSessionService shutdown. Possible data loss!");
            Thread.currentThread().interrupt();
        }
    }

    <R extends JPA> void offer(PersistenceElement<R> element) {
        if (!queue.offer(element)) {
            log().severe("The persistenceSessionService queue is full. Possible data loss!");
        }
    }

    void take() {
        try {
            threadSyncQueue.take();
        } catch (InterruptedException e) {
            log().log(SEVERE, "Interrupted! Possible data loss!", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (!(Thread.currentThread().isInterrupted())) {
            try {
                PersistenceElement<?> element = queue.take();
                process(element);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log().info("Flushing the persistenceSessionService queue...");
        List<PersistenceElement<?>> elements = new ArrayList<>();
        queue.drainTo(elements);

        for (PersistenceElement<?> element : elements) {
            process(element);
        }

        log().info("Flushing the persistenceSessionService queue done!");
    }

    private <R extends JPA> void process(PersistenceElement<R> element) {
        Function<Session, R> sessionFunction = element.getSessionFunction();

        R result;
        try (Session session = persistenceSessionService.getSession()) {
            result = sessionFunction.apply(session);
        }

        Consumer<R> callback = element.getCallback();
        if (callback != null) {
            callMainThread(callback, result);
        }

        if (element.isSync() && !threadSyncQueue.offer(result)) {
            log().severe(() -> "Unable to send the persistenceSessionService result: " + result);
        }
    }

    private <R> void callMainThread(Consumer<R> callback, R result) {
        scheduler.callSyncMethod(javaPlugin, () -> {
            try {
                callback.accept(result);
            } catch (RuntimeException e) {
                log().log(SEVERE, "Exception while call back main thread", e);
            }
            return null;
        });
    }
}
