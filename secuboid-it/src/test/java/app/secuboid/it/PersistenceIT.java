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

package app.secuboid.it;

import app.secuboid.api.registration.RegistrationService;
import app.secuboid.core.config.ConfigService;
import app.secuboid.core.persistence.PersistenceSessionService;
import app.secuboid.core.persistence.jpa.RecipientJPA;
import app.secuboid.core.registration.RegistrationServiceImpl;
import org.junit.jupiter.api.*;

import static app.secuboid.it.DatabaseContainer.mariaDBContainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersistenceIT {

    PersistenceSessionService persistenceSessionService;

    @BeforeAll
    static void beforeAll() {
        mariaDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mariaDBContainer.stop();
    }

    @BeforeEach
    void beforeEach() {
        ConfigService configService = mock(ConfigService.class);
        when(configService.getDatabaseHost()).thenReturn(mariaDBContainer.getHost());
        when(configService.getDatabasePort()).thenReturn(mariaDBContainer.getFirstMappedPort());
        when(configService.getDatabaseDatabase()).thenReturn(mariaDBContainer.getDatabaseName());
        when(configService.getDatabaseUser()).thenReturn(mariaDBContainer.getUsername());
        when(configService.getDatabasePassword()).thenReturn(mariaDBContainer.getPassword());

        RegistrationService registrationService = new RegistrationServiceImpl();
        registrationService.registerJPA(RecipientJPA.class);

        persistenceSessionService = new PersistenceSessionService(configService, registrationService);
        persistenceSessionService.onEnable(true);
    }

    @AfterEach
    void afterEach() {
        persistenceSessionService.onDisable(true);
    }

    @Test
    void when_start_database_then_database_reachable() {
        var session = persistenceSessionService.getSession();
        RecipientJPA recipientJPA = RecipientJPA.builder()
                .shortName("TST1")
                .value("TEST1")
                .uuid(null)
                .build();

        var transaction = session.beginTransaction();
        session.persist(recipientJPA);
        session.flush();
        transaction.commit();

        RecipientJPA recipientInserted = session.createQuery("SELECT a FROM RecipientJPA a WHERE a.shortName = 'TST1'",
                        RecipientJPA.class)
                .getResultList()
                .iterator()
                .next();

        session.close();

        assertEquals(recipientJPA, recipientInserted);
    }

    @Test
    void when_connection_restart_then_data_persist() {
        var session = persistenceSessionService.getSession();
        RecipientJPA recipientJPA = RecipientJPA.builder()
                .shortName("TST2")
                .value("TEST2")
                .uuid(null)
                .build();

        var transaction = session.beginTransaction();
        session.persist(recipientJPA);
        session.flush();
        transaction.commit();
        session.close();

        persistenceSessionService.onDisable(true);
        persistenceSessionService.onEnable(true);

        session = persistenceSessionService.getSession();

        RecipientJPA recipientInserted = session.createQuery("SELECT a FROM RecipientJPA a WHERE a.shortName = 'TST2'",
                        RecipientJPA.class)
                .getResultList()
                .iterator()
                .next();

        session.close();

        assertEquals(recipientJPA.getId(), recipientInserted.getId());
        assertEquals(recipientJPA.getShortName(), recipientInserted.getShortName());
        assertEquals(recipientJPA.getValue(), recipientInserted.getValue());
    }
}
