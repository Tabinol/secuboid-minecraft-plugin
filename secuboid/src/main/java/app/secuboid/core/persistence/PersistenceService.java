/*
 *  Secuboid: Lands and Protection plugin for Minecraft server
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
import app.secuboid.api.services.Service;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class PersistenceService implements Service {

    private static final String THREAD_NAME = "Secuboid PersistenceSessionService";

    private final JavaPlugin javaPlugin;
    private final BukkitScheduler scheduler;
    private final PersistenceSessionService persistenceSessionService;

    private PersistenceThread persistenceThread = null;

    @Override
    public void onEnable(boolean isServerBoot) {
        persistenceThread = new PersistenceThread(javaPlugin, scheduler, persistenceSessionService);
        persistenceThread.setName(THREAD_NAME);
        persistenceThread.start();
    }

    @Override
    public void onDisable(boolean isServerShutdown) {
        if (persistenceThread != null) {
            persistenceThread.shutdown();
            persistenceThread = null;
        }
    }

    public <R extends JPA> void exec(Function<Session, R> sessionFunction, Consumer<R> callback) {
        PersistenceElement<R> element = new PersistenceElement<>(sessionFunction, callback, false);
        persistenceThread.offer(element);
    }

    public void execSync(Consumer<Session> sessionConsumer) {
        Function<Session, JPA> sessionFunction = session -> {
            sessionConsumer.accept(session);
            return null;
        };

        PersistenceElement<JPA> element = new PersistenceElement<>(sessionFunction, null, true);
        persistenceThread.offer(element);

        persistenceThread.take();
    }
}
