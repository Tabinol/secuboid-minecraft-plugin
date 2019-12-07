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
package me.tabinol.secuboid.lands.approve;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tabinol.secuboid.Secuboid;
import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.AreaUtil;
import me.tabinol.secuboid.lands.collisions.Collisions.LandAction;
import me.tabinol.secuboid.lands.types.Type;
import me.tabinol.secuboid.playercontainer.PlayerContainer;
import me.tabinol.secuboid.playercontainer.PlayerContainerType;
import me.tabinol.secuboid.utilities.StringChanges;

/**
 * The Class ApproveList.
 */
public final class ApproveList {

    private final Secuboid secuboid;

    /**
     * The approve file.
     */
    private final File approveFile;

    /**
     * The approve config.
     */
    private FileConfiguration approveConfig;

    /**
     * The land names.
     */
    private final TreeSet<String> landNames;

    /**
     * Instantiates a new approve list.
     *
     * @param secuboid secuboid instance
     */
    public ApproveList(Secuboid secuboid) {

        this.secuboid = secuboid;
        approveConfig = new YamlConfiguration();
        landNames = new TreeSet<String>();
        if (secuboid.getDataFolder() != null) {
            approveFile = new File(secuboid.getDataFolder() + "/approvelist.yml");
            loadFile();
        } else {
            // Unit tests
            approveFile = null;
        }
    }

    /**
     * Adds the approve.
     *
     * @param approve the approve
     */
    public void addApprove(Approve approve) {

        landNames.add(approve.getLandName());
        ConfigurationSection section = approveConfig.createSection(approve.getLandName());
        if (approve.getType() != null) {
            section.set("Type", approve.getType().getName());
        }
        section.set("Action", approve.getAction().toString());
        section.set("RemovedAreaId", approve.getRemovedAreaId());
        if (approve.getNewArea() != null) {
            section.set("NewArea", approve.getNewArea().toFileFormat());
        }
        section.set("Owner", approve.getOwner().toFileFormat());
        if (approve.getParent() != null) {
            section.set("Parent", approve.getParent().getName());
        }
        section.set("Price", approve.getPrice());
        section.set("DateTime", approve.getDateTime().getTimeInMillis());
        saveFile();
        secuboid.getApproveNotif().notifyForApprove(approve.getLandName(), approve.getOwner().getPrint());
    }

    /**
     * Gets the approve list.
     *
     * @return the approve list
     */
    public TreeMap<String, Approve> getApproveList() {

        TreeMap<String, Approve> approves = new TreeMap<String, Approve>();
        TreeMap<String, Approve> approvesToRemove = new TreeMap<String, Approve>();

        // Check if land names are ok
        for (String landName : landNames) {

            Approve app = getApprove(landName);

            if (app != null) {

                // Approve ok, put in list
                approves.put(landName, app);
            } else {

                // Approve not ok, add it to list
                approvesToRemove.put(landName, null);
            }
        }

        // Remove wrong approves
        for (Map.Entry<String, Approve> appEntry : approvesToRemove.entrySet()) {

            removeApprove(appEntry.getKey());
        }

        return approves;
    }

    /**
     * Checks if is in approve.
     *
     * @param landName the land name
     * @return true, if is in approve
     */
    public boolean isInApprove(String landName) {
        return landNames.contains(landName.toLowerCase());
    }

    /**
     * Gets the approve.
     *
     * @param landName the land name
     * @return the approve
     */
    public Approve getApprove(String landName) {

        ConfigurationSection section = approveConfig.getConfigurationSection(landName);

        if (section == null) {
            return null;
        }

        String typeName = section.getString("Type");
        Type type = null;
        if (typeName != null) {
            type = secuboid.getTypes().addOrGetType(typeName);
        }

        String[] ownerS = StringChanges.splitAddVoid(section.getString("Owner"), ":");
        PlayerContainer pc = secuboid.getNewInstance()
                .createPlayerContainer(PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);
        Land parent = null;
        Area newArea = null;

        if (section.contains("Parent")) {
            parent = secuboid.getLands().getLand(section.getString("Parent"));

            // If the parent does not exist
            if (parent == null) {
                return null;
            }
        }

        if (section.contains("NewArea")) {
            newArea = AreaUtil.getFromFileFormat(section.getString("NewArea"));
        }

        LandAction action = LandAction.valueOf(section.getString("Action"));

        // If the land was deleted
        if (action != LandAction.LAND_ADD && secuboid.getLands().getLand(landName) == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(section.getLong("DateTime"));

        return new Approve(secuboid, landName, type, action, section.getInt("RemovedAreaId"), newArea, pc, parent,
                section.getDouble("Price"), cal);
    }

    /**
     * Removes the approve.
     *
     * @param approve the approve
     */
    public void removeApprove(Approve approve) {
        removeApprove(approve.getLandName());
    }

    /**
     * Removes the approve.
     *
     * @param landName the land name
     */
    private void removeApprove(String landName) {
        approveConfig.set(landName, null);
        landNames.remove(landName);
        saveFile();
    }

    /**
     * Removes the all.
     */
    public void removeAll() {

        // Delete file
        if (approveFile.exists()) {
            if (!approveFile.delete()) {
                secuboid.getLogger().severe("Impossible to delete the file " + approveFile.getPath() + ".");
            }
        }

        // Delete list
        landNames.clear();
        approveConfig = new YamlConfiguration();

        // Reload file
        loadFile();
    }

    /**
     * Load file.
     */
    private void loadFile() {

        if (!approveFile.exists()) {
            try {
                if (!approveFile.createNewFile()) {
                    throw new IOException("Impossible to create the file " + approveFile.getPath() + ".");
                }
            } catch (IOException ex) {
                secuboid.getLogger().severe("Error on approve file creation: " + ex.getLocalizedMessage());
            }
        }
        try {
            approveConfig.load(approveFile);
        } catch (IOException ex) {
            secuboid.getLogger().severe("Error on approve file load: " + ex.getLocalizedMessage());
        } catch (InvalidConfigurationException ex) {
            secuboid.getLogger().severe("Error on approve file load" + ex.getLocalizedMessage());
        }

        // add land names to list
        for (String landName : approveConfig.getKeys(false)) {
            landNames.add(landName);
        }
    }

    /**
     * Save file.
     */
    private void saveFile() {
        try {
            approveConfig.save(approveFile);
        } catch (IOException ex) {
            secuboid.getLogger().severe("Error on approve file save: " + ex.getLocalizedMessage());
        }
    }
}
