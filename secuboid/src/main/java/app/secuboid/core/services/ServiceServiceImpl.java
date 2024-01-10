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

package app.secuboid.core.services;

import app.secuboid.api.registration.RegistrationService;
import app.secuboid.api.services.Service;
import app.secuboid.api.services.ServiceService;
import app.secuboid.core.registration.RegistrationServiceImpl;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static java.util.Collections.reverse;

public class ServiceServiceImpl implements ServiceService {

    private final RegistrationService registrationService;

    public ServiceServiceImpl(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public void onDisableReload() {
        Map<Plugin, List<Service>> pluginToServices = ((RegistrationServiceImpl) registrationService).getPluginToServices();
        ArrayList<Entry<Plugin, List<Service>>> pluginToServicesReverse = new ArrayList<>(pluginToServices.entrySet());
        reverse(pluginToServicesReverse);
        pluginToServicesReverse.forEach(e -> {
            List<Service> servicesReverse = new ArrayList<>(e.getValue());
            reverse(servicesReverse);
            servicesReverse.forEach(s -> s.onDisable(false));
        });
    }

    public void onEnableReload() {
        Map<Plugin, List<Service>> pluginToServices = ((RegistrationServiceImpl) registrationService).getPluginToServices();
        pluginToServices.forEach((plugin, services) -> services.forEach(s -> s.onEnable(false)));
    }

    @Override
    public void onLoad(Plugin plugin) {
        Map<Plugin, List<Service>> pluginToServices = ((RegistrationServiceImpl) registrationService).getPluginToServices();
        List<Service> services = pluginToServices.get(plugin);
        executeMethods(services, Service::onLoad);
    }

    @Override
    public void onEnable(Plugin plugin) {
        Map<Plugin, List<Service>> pluginToServices = ((RegistrationServiceImpl) registrationService).getPluginToServices();
        List<Service> services = pluginToServices.get(plugin);
        executeMethods(services, s -> s.onEnable(true));
    }

    @Override
    public void onDisable(Plugin plugin) {
        Map<Plugin, List<Service>> pluginToServices = ((RegistrationServiceImpl) registrationService).getPluginToServices();
        List<Service> services = pluginToServices.get(plugin);
        List<Service> servicesReverse = new ArrayList<>(services);
        reverse(servicesReverse);
        executeMethods(servicesReverse, s -> s.onDisable(false));
    }

    private void executeMethods(List<Service> services, Consumer<Service> consumer) {
        services.forEach(consumer);
    }
}
