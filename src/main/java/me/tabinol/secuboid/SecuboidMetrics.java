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
package me.tabinol.secuboid;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;

import me.tabinol.secuboid.lands.Land;
import me.tabinol.secuboid.lands.areas.Area;
import me.tabinol.secuboid.lands.areas.CuboidArea;
import me.tabinol.secuboid.lands.areas.CylinderArea;
import me.tabinol.secuboid.lands.areas.RoadArea;

public final class SecuboidMetrics {

    private static final int BSTATS_PLUGIN_ID = 10990;
    private static final String YES = "Yes";
    private static final String NO = "No";

    private final Secuboid secuboid;

    SecuboidMetrics(Secuboid secuboid) {
        this.secuboid = secuboid;
    }

    void start() {
        Metrics metrics = new Metrics(secuboid, BSTATS_PLUGIN_ID);

        // Storage
        String storage = Objects.equal(secuboid.getConf().getStorage().toLowerCase(), "mysql") ? "MySql" : "Flat";
        metrics.addCustomChart(new SimplePie("storage", () -> storage));

        // Lands
        metrics.addCustomChart(new SingleLineChart("lands", () -> secuboid.getLands().getLands().size()));

        // Areas
        metrics.addCustomChart(new SingleLineChart("areas", () -> {
            int areaNb = 0;
            for (Land land : secuboid.getLands().getLands()) {
                areaNb += land.getAreas().size();
            }
            return areaNb;
        }));

        // Areas types
        metrics.addCustomChart(new AdvancedPie("area_types", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            int cuboidAreaNb = 0;
            int cylinderAreaNb = 0;
            int roadAreaNb = 0;
            for (Land land : secuboid.getLands().getLands()) {
                for (Area area : land.getAreas()) {
                    if (area instanceof CuboidArea) {
                        cuboidAreaNb++;
                    } else if (area instanceof CylinderArea) {
                        cylinderAreaNb++;
                    } else if (area instanceof RoadArea) {
                        roadAreaNb++;
                    }
                }
            }
            valueMap.put("CuboidArea", cuboidAreaNb);
            valueMap.put("CylinderArea", cylinderAreaNb);
            valueMap.put("RoadArea", roadAreaNb);
            return valueMap;
        }));

        // Inventories enabled
        metrics.addCustomChart(
                new SimplePie("inventories_enabled", () -> secuboid.getConf().isMultipleInventories() ? YES : NO));

        // Creative and Fly enabled
        metrics.addCustomChart(
                new SimplePie("creative_and_fly_enabled", () -> secuboid.getConf().isFlyAndCreative() ? YES : NO));

        // Economy enabled
        metrics.addCustomChart(new SimplePie("economy_enabled", () -> secuboid.getConf().useEconomy() ? YES : NO));
    }
}
