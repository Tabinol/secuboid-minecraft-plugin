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

import app.secuboid.api.exceptions.SecuboidRuntimeException;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.api.services.Service;
import app.secuboid.core.config.ConfigService;
import app.secuboid.core.registration.RegistrationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@RequiredArgsConstructor
public class PersistenceSessionService implements Service {

    public static final String DRIVER_CLASS = "org.mariadb.jdbc.Driver";
    public static final String CONNECTION_PROVIDER_CLASS = "org.hibernate.hikaricp.internal.HikariCPConnectionProvider";

    private final ConfigService configService;
    private final RegistrationService registrationService;

    private SessionFactory sessionFactory = null;

    @Override
    public void onEnable(boolean isServerBoot) {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new SecuboidRuntimeException("MariaDB driver not found: " + DRIVER_CLASS, e);
        }
        
        String url = String.format("jdbc:mariadb://%s:%s/%s", configService.getDatabaseHost(), configService.getDatabasePort(),
                configService.getDatabaseDatabase());

        new CreateDatabase(registrationService, url, configService.getDatabaseUser(), configService.getDatabasePassword()).createDatabases();

        Properties properties = getProperties(url);
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        ((RegistrationServiceImpl) registrationService).getJpaClassToCreateTable().keySet().forEach(configuration::addAnnotatedClass);
        sessionFactory = configuration.buildSessionFactory();
    }

    @Override
    public void onDisable(boolean isServerShutdown) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    private Properties getProperties(String url) {
        Properties properties = new Properties();

        properties.setProperty("hibernate.connection.driver_class", DRIVER_CLASS);
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", configService.getDatabaseUser());
        properties.setProperty("hibernate.connection.password", configService.getDatabasePassword());
        properties.setProperty("hibernate.connection.provider_class", CONNECTION_PROVIDER_CLASS);
        return properties;
    }
}
