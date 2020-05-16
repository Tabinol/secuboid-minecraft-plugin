/*
 Secuboid: Lands and Protection plugin for Minecraft server
 Copyright (C) 2014 Tabinol

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
package me.tabinol.secuboid.storage.flat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.exceptions.SecuboidLandException;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.Lands;
import me.tabinol.secuboid.lands.approve.Approve;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * ApprovesFlat
 */
public final class ApprovesFlat {

    private final String PARAM_NAME_ACTION = "approveAction";
    private final String PARAM_NAME_REMOVED_AREA_ID = "removedAreaId";
    private final String PARAM_NAME_NEW_AREA_ID = "newAreaId";
    private final String PARAM_NAME_OWNER = "owner";
    private final String PARAM_NAME_PARENT_UUID = "parentUUID";
    private final String PARAM_NAME_PRICE = "price";
    private final String PARAM_NAME_DATETIME = "dateTime";
    // Just for verbose
    private final String PARAM_NAME_LAND_NAME = "landName";

    private final Secuboid secuboid;

    /**
     * The approve file.
     */
    private final File approveFile;

    /**
     * The approve config.
     */
    private FileConfiguration approveConfig;

    public ApprovesFlat(final Secuboid secuboid) {
        this.secuboid = secuboid;
        approveFile = new File(secuboid.getDataFolder() + "/approvelist.yml");
    }

    public void loadApproves() {
        loadApproveYml();
        secuboid.getLands().getApproves().loadApproves(getApproveList());
    }

    private void loadApproveYml() {
        approveConfig = new YamlConfiguration();

        if (!approveFile.exists()) {
            try {
                approveFile.createNewFile();
            } catch (final IOException ex) {
                secuboid.getLogger().severe("Error on approve file creation: " + ex.getLocalizedMessage());
            }
        }
        try {
            approveConfig.load(approveFile);
        } catch (final IOException ex) {
            secuboid.getLogger().severe("Error on approve file load: " + ex.getLocalizedMessage());
        } catch (final InvalidConfigurationException ex) {
            secuboid.getLogger().severe("Error on approve file load" + ex.getLocalizedMessage());
        }
    }

    private List<Approve> getApproveList() {
        final List<Approve> landNameToApprove = new ArrayList<>();

        for (final String key : approveConfig.getKeys(false)) {
            Approve app;
            try {
                final UUID landUUID = UUID.fromString(key);
                try {
                    app = getApproveNullable(landUUID);
                } catch (final RuntimeException ex) {
                    secuboid.getLogger().log(Level.SEVERE,
                            String.format("In approvelist.yml, error loading \"%s\"", landUUID), ex);
                    app = null;
                }
            } catch (final IllegalArgumentException e) {
                // Legacy: key is land name
                app = getApproveNullable(key);
            }
            if (app != null) {
                // Approve ok, put in list
                landNameToApprove.add(app);
            }
        }

        return Collections.unmodifiableList(landNameToApprove);
    }

    private Approve getApproveNullable(final UUID landUUID) {

        final ConfigurationSection section = approveConfig.getConfigurationSection(landUUID.toString());

        if (section == null) {
            return null;
        }

        final Land land = secuboid.getLands().getLand(landUUID);
        final String landNameInFile = section.getString(PARAM_NAME_LAND_NAME);
        if (land == null) {
            secuboid.getLogger().log(Level.WARNING, String
                    .format("In approvelist.yml, \"%s\", \"%s\" refere to an invalid land", landNameInFile, landUUID));
            return null;
        }

        final String[] ownerS = StringChanges.splitAddVoid(section.getString(PARAM_NAME_OWNER), ":");
        final PlayerContainer pc = secuboid.getPlayerContainers().getOrAddPlayerContainer(
                PlayerContainerType.getFromString(ownerS[0]), Optional.ofNullable(ownerS[1]), Optional.empty());
        Optional<Land> parentOpt = Optional.empty();
        Optional<Integer> newAreaIdOpt = Optional.empty();
        Optional<Integer> removedAreaIdOpt = Optional.empty();

        if (section.contains(PARAM_NAME_PARENT_UUID)) {
            final UUID parentUUID = UUID.fromString(section.getString(PARAM_NAME_PARENT_UUID));
            parentOpt = Optional.ofNullable(secuboid.getLands().getLand(parentUUID));

            // If the parent does not exist
            if (!parentOpt.isPresent()) {
                secuboid.getLogger().log(Level.WARNING,
                        String.format("In approvelist.yml, \"%s\", \"%s\" refere to an invalid parent \"%s\"",
                                land.getName(), landUUID, parentUUID));
                return null;
            }
        }

        if (section.contains(PARAM_NAME_REMOVED_AREA_ID)) {
            removedAreaIdOpt = Optional.of(Integer.parseInt(section.getString(PARAM_NAME_REMOVED_AREA_ID)));
            if (land.getArea(removedAreaIdOpt.get()) == null) {
                secuboid.getLogger().log(Level.WARNING,
                        String.format("In approvelist.yml, \"%s\", \"%s\" refere to an invalid removed area id \"%s\"",
                                land.getName(), landUUID, removedAreaIdOpt.get()));
                return null;
            }
        }

        if (section.contains(PARAM_NAME_NEW_AREA_ID)) {
            newAreaIdOpt = Optional.of(Integer.parseInt(section.getString(PARAM_NAME_NEW_AREA_ID)));
            if (land.getArea(newAreaIdOpt.get()) == null) {
                secuboid.getLogger().log(Level.WARNING,
                        String.format("In approvelist.yml, \"%s\", \"%s\" refere to an invalid new area id \"%s\"",
                                land.getName(), landUUID, newAreaIdOpt.get()));
                return null;
            }
        }

        final LandAction action = LandAction.valueOf(section.getString(PARAM_NAME_ACTION));

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(section.getLong(PARAM_NAME_DATETIME));

        return new Approve(land, action, removedAreaIdOpt, newAreaIdOpt, pc, parentOpt,
                section.getDouble(PARAM_NAME_PRICE), cal);
    }

    // Legacy, to be removed in version 1.6.0+
    private Approve getApproveNullable(final String landName) {
        final Lands lands = secuboid.getLands();

        final ConfigurationSection section = approveConfig.getConfigurationSection(landName);

        if (section == null) {
            return null;
        }

        final String typeName = section.getString("Type");
        Type type = null;
        if (typeName != null) {
            type = secuboid.getTypes().addOrGetType(typeName);
        }

        final String[] ownerS = StringChanges.splitAddVoid(section.getString("Owner"), ":");
        final PlayerContainer pc = secuboid.getPlayerContainers().getOrAddPlayerContainer(
                PlayerContainerType.getFromString(ownerS[0]), Optional.ofNullable(ownerS[1]), Optional.empty());
        Land parent = null;
        Area newArea = null;

        if (section.contains("Parent")) {
            parent = lands.getLand(section.getString("Parent"));

            // If the parent does not exist
            if (parent == null) {
                return null;
            }
        }

        if (section.contains("NewArea")) {
            newArea = Area.getFromFileFormat(section.getString("NewArea"));
        }

        final LandAction action = LandAction.valueOf(section.getString("action"));

        // If the land was deleted
        if (action != LandAction.LAND_ADD && lands.getLand(landName) == null) {
            return null;
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(section.getLong("DateTime"));

        // Legacy compatibility: create land if not exists
        Land land = lands.getLand(landName);
        if (land == null) {
            try {
                land = lands.createLand(landName, false, pc, newArea, parent, 1, UUID.randomUUID(), type);
            } catch (final SecuboidLandException e) {
                secuboid.getLogger().log(Level.SEVERE,
                        String.format("Unable to create non approved land \"%s\"", landName), e);
            }
        }

        final Optional<Integer> newAreaIdOpt = Optional
                .of(Optional.ofNullable(newArea).map(area -> area.getKey()).orElse(0));
        return new Approve(land, action, Optional.of(section.getInt("RemovedAreaId")), newAreaIdOpt, pc,
                Optional.ofNullable(parent), section.getDouble("Price"), cal);
    }

    void saveApprove(final Approve approve) {
        final Land land = approve.getLand();
        final UUID landUUID = land.getUUID();
        final ConfigurationSection section = approveConfig.createSection(landUUID.toString());
        section.set(PARAM_NAME_LAND_NAME, land.getName());
        section.set(PARAM_NAME_ACTION, approve.getAction().name());
        if (approve.getRemovedAreaIdOpt().isPresent()) {
            section.set(PARAM_NAME_REMOVED_AREA_ID, approve.getRemovedAreaIdOpt().get());
        }
        if (approve.getNewAreaIdOpt().isPresent()) {
            section.set(PARAM_NAME_NEW_AREA_ID, approve.getNewAreaIdOpt().get());
        }
        section.set(PARAM_NAME_OWNER, approve.getOwner().toFileFormat());
        if (approve.getParentOpt().isPresent()) {
            section.set(PARAM_NAME_PARENT_UUID, approve.getParentOpt().get().getUUID().toString());
        }
        section.set(PARAM_NAME_PRICE, approve.getPrice());
        section.set(PARAM_NAME_DATETIME, approve.getDateTime().getTimeInMillis());
        saveFile();
    }

    void removeApprove(final Approve approve) {
        approveConfig.set(approve.getLand().getUUID().toString(), null);
        saveFile();
    }

    /**
     * Removes the all.
     */
    public void removeAll() {

        // Delete file
        if (approveFile.exists()) {
            if (!approveFile.delete()) {
                secuboid.getLogger().severe(String.format("Unable to delete the file %s.", approveFile.getPath()));
            }
        }

        // Reload file
        loadApproveYml();
    }

    private void saveFile() {
        try {
            approveConfig.save(approveFile);
        } catch (final IOException ex) {
            secuboid.getLogger().severe(String.format("Error on approve file save: %s", ex.getLocalizedMessage()));
        }
    }
}