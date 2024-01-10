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
package app.secuboid.core.flagtypes;

import app.secuboid.api.flagtypes.FlagType;
import app.secuboid.api.flagtypes.FlagTypeService;
import app.secuboid.api.registration.RegistrationService;
import app.secuboid.core.registration.RegistrationServiceImpl;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class FlagTypeServiceImpl implements FlagTypeService {

    private final RegistrationService registrationService;

    private final Map<String, FlagType> nameToFlagType = new HashMap<>();

    @Override
    public void onEnable(boolean isServerBoot) {
        if (isServerBoot) {
            Set<FlagType> flagTypes = ((RegistrationServiceImpl) registrationService).getFlagTypes();
            flagTypes.forEach(f -> nameToFlagType.put(f.getName(), f));
        }
    }

    @Override
    public FlagType getFlagType(String flagName) {
        return nameToFlagType.get(flagName);
    }

    @Override
    public Set<String> getFlagTypeNames() {
        return nameToFlagType.keySet();
    }
}
