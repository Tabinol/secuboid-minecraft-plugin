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

package app.secuboid.core.persistence.jpa;

import app.secuboid.api.lands.areas.AreaType;
import app.secuboid.api.persistence.CreateTable;
import app.secuboid.api.persistence.JPA;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static app.secuboid.api.persistence.WithId.NON_EXISTING_ID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "secuboid_area")
@CreateTable({"""
        CREATE TABLE `secuboid_area` (
          `id` bigint(20) NOT NULL AUTO_INCREMENT,
          `type` varchar(1) NOT NULL,
          `x1` int(11) NOT NULL,
          `x2` int(11) NOT NULL,
          `y1` int(11) NOT NULL,
          `y2` int(11) NOT NULL,
          `z1` int(11) NOT NULL,
          `z2` int(11) NOT NULL,
          `land_id` bigint(20) NOT NULL,
          PRIMARY KEY (`id`),
          KEY `FK_area_land_id` (`land_id`),
          CONSTRAINT `FK_area_land_id` FOREIGN KEY (`land_id`) REFERENCES `secuboid_land` (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
        """})
public class AreaJPA implements JPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Builder.Default
    private long id = NON_EXISTING_ID;

    @Column(name = "type", length = 1, nullable = false)
    private String typeValue;

    @Transient
    private AreaType type;

    @ManyToOne
    @JoinColumn(name = "land_id", nullable = false)
    private LandJPA landJPA;

    @Column(name = "x1", nullable = false)
    @Builder.Default
    private int x1 = Integer.MIN_VALUE;

    @Column(name = "y1", nullable = false)
    @Builder.Default
    private int y1 = Integer.MIN_VALUE;

    @Column(name = "z1", nullable = false)
    @Builder.Default
    private int z1 = Integer.MIN_VALUE;

    @Column(name = "x2", nullable = false)
    @Builder.Default
    private int x2 = Integer.MAX_VALUE;

    @Column(name = "y2", nullable = false)
    @Builder.Default
    private int y2 = Integer.MAX_VALUE;

    @Column(name = "z2", nullable = false)
    @Builder.Default
    private int z2 = Integer.MAX_VALUE;

    @PostLoad
    void fillTransient() {
        type = AreaType.of(typeValue);
    }

    @PrePersist
    void fillPersistent() {
        typeValue = type.getValue();
    }
}
