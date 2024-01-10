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

import app.secuboid.api.lands.LandType;
import app.secuboid.api.persistence.CreateTable;
import app.secuboid.api.persistence.JPA;
import jakarta.persistence.*;
import lombok.*;

import static app.secuboid.api.persistence.WithId.NON_EXISTING_ID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "secuboid_land")
@CreateTable({"""
        CREATE TABLE `secuboid_land` (
          `id` bigint(20) NOT NULL AUTO_INCREMENT,
          `name` varchar(45) NOT NULL,
          `type` varchar(1) NOT NULL,
          `parent_id` bigint(20) DEFAULT NULL,
          PRIMARY KEY (`id`),
          KEY `FK_land_parent_id` (`parent_id`),
          CONSTRAINT `FK_land_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `secuboid_land` (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
        """})
public class LandJPA implements JPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Builder.Default
    private long id = NON_EXISTING_ID;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "type", length = 1, nullable = false)
    private String typeValue;

    @Transient
    private LandType type;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @EqualsAndHashCode.Exclude
    private LandJPA parentLandJPA;

    @PostLoad
    void fillTransient() {
        type = LandType.of(typeValue);
    }

    @PrePersist
    void fillPersistent() {
        typeValue = type.getValue();
    }
}
