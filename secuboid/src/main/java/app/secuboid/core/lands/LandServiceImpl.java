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
package app.secuboid.core.lands;

import app.secuboid.api.lands.*;
import app.secuboid.api.lands.areas.Area;
import app.secuboid.api.lands.areas.AreaResult;
import app.secuboid.api.lands.areas.AreaResultCode;
import app.secuboid.api.lands.areas.AreaService;
import app.secuboid.api.recipients.RecipientExec;
import app.secuboid.core.persistence.PersistenceService;
import app.secuboid.core.persistence.jpa.LandJPA;
import app.secuboid.core.utilities.NameUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static app.secuboid.api.lands.LandResultCode.SUCCESS;
import static app.secuboid.api.lands.LandResultCode.UNKNOWN;
import static app.secuboid.core.messages.Log.log;

@RequiredArgsConstructor
public class LandServiceImpl implements LandService {

    private static final String DEFAULT_WORLD_NAME = "world";

    private final Server server;
    private final AreaService areaService;
    private final PersistenceService persistenceService;

    private final Map<String, Land> worldNameToLand = new HashMap<>();
    private final Map<Long, Land> idToLand = new HashMap<>();
    private final Map<String, Set<Land>> nameToLands = new HashMap<>();

    @Override
    public void onEnable(boolean isServerBoot) {
        worldNameToLand.clear();
        idToLand.clear();
        nameToLands.clear();

        loadLands();
        loadAreas();
    }


    public void loadWorldSync(World world) {
        String worldName = world.getName();

        if (worldNameToLand.containsKey(worldName)) {
            return;
        }

        LandJPA landJPA = LandJPA.builder()
                .name(worldName)
                .build();

        persistenceService.execSync(session -> {
            Transaction transaction = session.beginTransaction();
            session.persist(landJPA);
            session.flush();
            transaction.commit();
        });

        Land worldLand = LandImpl.builder()
                .jPA(landJPA)
                .build();
        putLandToMap(worldLand);
    }

    @Override
    public void create(Land parent, String landName, RecipientExec owner, Area area, Consumer<LandResult> callback) {
        String nameLower = landName.toLowerCase();

        LandResultCode code = validateName(parent, nameLower);
        if (code != null) {
            if (callback != null) {
                LandResult landResult = new LandResultImpl(code, null, null);
                callback.accept(landResult);
            }
            return;
        }

        LandJPA landJPA = LandJPA.builder()
                .type(LandType.LAND)
                .name(nameLower)
                .parentLandJPA(((LandImpl) parent).getJPA())
                .build();

        persistenceService.exec(session -> {
            Transaction transaction = session.beginTransaction();
            session.persist(landJPA);
            session.flush();
            transaction.commit();
            return landJPA;
        }, r -> createInsertCallback(landJPA, parent, area, callback));
    }

    @Override
    public void removeForce(Land land, Consumer<LandResult> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeRecursive(Land land, Consumer<LandResult> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(Land land, Consumer<LandResult> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rename(Land land, String newName, Consumer<LandResult> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setParent(Land land, Land newParent, Consumer<LandResult> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public Land get(long id) {
        return idToLand.get(id);
    }

    @Override
    public Land get(Location loc) {
        Area area = areaService.getArea(loc);

        if (area != null) {
            return area.getLand();
        }

        return getWorldLand(loc);
    }

    @Override
    public Set<Land> getLands(World world, int x, int z) {
        Set<Area> areas = areaService.getAreas(world, x, z);

        return getLandsFromAreas(areas);
    }

    @Override
    public Set<Land> getLands(Location loc) {
        Set<Area> areas = areaService.getAreas(loc);

        return getLandsFromAreas(areas);
    }

    @Override
    public LocationPath getLocationPath(Location loc) {
        // TODO return Optional.ofNullable((LocationPath) getArea(loc)).orElse(get(loc));
        return null;
    }

    @Override
    public Land getWorldLand(Location loc) {
        return getWorldLand(getWorldFromLocation(loc));
    }

    @Override
    public Land getWorldLand(World world) {
        return worldNameToLand.get(world.getName());
    }

    private void loadLands() {
//        // TODO Set<LandRow> landRows = getStorageManager().selectAllSync(LandRow.class);
//        Set<LandRow> landRows = Collections.emptySet();
//
//        while (!landRows.isEmpty()) {
//            Set<LandRow> nextLandRows = new HashSet<>();
//
//            for (LandRow landRow : landRows) {
//                if (loadLandComponentAndIsToRetry(landRow)) {
//                    nextLandRows.add(landRow);
//                }
//            }
//            landRows = nextLandRows;
//        }
//
//        for (World world : server.getWorlds()) {
//            loadWorldSync(world);
//        }
    }

//    private boolean loadLandComponentAndIsToRetry(LandRow landRow) {
//        Long parentId = landRow.parentId();
//        Land parent = null;
//
//        if (landRow.type() == AREA_LAND) {
//            if (parentId == null) {
//                log().log(SEVERE, "Unable to create the land \"{}\" because it has no parent", landRow.name());
//                return false;
//            }
//
//            parent = (Land) idToLandComponent.get(parentId);
//
//            if (parent == null) {
//                return true;
//            }
//        }
//
//        LandComponent landComponent = LandType.newLandComponent(landRow, parent);
//        putLandComponentToMap(landComponent);
//
//        return false;
//    }

    private void loadAreas() {
//        // TODO Set<AreaRow> areaRows = getStorageManager().selectAllSync(AreaRow.class);
//        Set<AreaRow> areaRows = Collections.emptySet();
//
//        for (AreaRow areaRow : areaRows) {
//            loadArea(areaRow);
//        }
    }

//    private void loadArea(AreaRow areaRow) {
//        LandComponent landComponent = idToLandComponent.get(areaRow.landId());
//
//        if (!(landComponent instanceof AreaLand)) {
//            String msg = format("Unable to add area id \"%s\" because the associated land is the wrong type [%s]"
//                    , areaRow.id(), landComponent);
//            log().log(SEVERE, msg);
//            return;
//        }
//        ((AreaLandImpl) landComponent).addAreaToLand(areaRow);
//    }

    private LandResultCode validateName(Land parent, String nameLower) {
        if (!NameUtil.validateName(nameLower)) {
            return LandResultCode.NAME_INVALID;
        }

        // TODO validate if name exists
        if (!NameUtil.validateName(nameLower)) {
            return LandResultCode.NAME_EXIST;
        }

        return null;
    }

    private void createInsertCallback(LandJPA landJPA, Land parent, Area area, Consumer<LandResult> callback) {
//        AreaLandImpl areaLand = new AreaLandImpl(landRow.id(), landRow.name(), parent);
//        areaLand.addArea(areaForm, r -> createAddAreaCallback(areaLand, r, callback));
    }

    private void createAddAreaCallback(Land land, AreaResult areaResult, Consumer<LandResult> callback) {
        if (areaResult.getCode() != AreaResultCode.SUCCESS) {
            if (callback != null) {
                callback.accept(new LandResultImpl(UNKNOWN, null, null));
            }
            log().warning(() -> String.format("This land cannot be create because an error with the area [id=%s, " +
                    "name=%s]", land.getId(), land.getName()));
            return;
        }

        putLandToMap(land);
        // TODO ((LandImpl) land.getParent()).setChild(areaLand);

        if (callback != null) {
            callback.accept(new LandResultImpl(SUCCESS, land, areaResult.getArea()));
        }
    }

    private void putLandToMap(Land land) {
        String name = land.getName();

        idToLand.put(land.getId(), land);
        nameToLands.computeIfAbsent(name, k -> new HashSet<>()).add(land);

        if (land.getType() == LandType.WORLD) {
            worldNameToLand.put(name, land);
        }
    }

    private void removeLandFromMap(Land land) {
        String name = land.getName();

        idToLand.remove(land.getId());

        nameToLands.computeIfPresent(name, (k, v) -> {
            v.remove(land);
            return !v.isEmpty() ? v : null;
        });

        if (land.getType() == LandType.WORLD) {
            worldNameToLand.remove(name);
        }
    }

    private Set<Land> getLandsFromAreas(Set<Area> areas) {
        return areas.stream().map(Area::getLand).collect(Collectors.toSet());
    }

    private World getWorldFromLocation(Location loc) {
        World world = loc.getWorld();

        if (world != null)
            return world;

        world = Bukkit.getWorld(DEFAULT_WORLD_NAME);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        log().log(Level.WARNING, "A location is sent without any world. Assuming: {}", world.getName());

        return world;
    }
}
