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
@Table(name = "secuboid_resident")
@CreateTable({"""
        CREATE TABLE `secuboid_resident` (
          `level` int(11) NOT NULL,
          `id` bigint(20) NOT NULL AUTO_INCREMENT,
          `recipient_id` bigint(20) NOT NULL,
          `land_id` bigint(20) NOT NULL,
          PRIMARY KEY (`id`,`level`,`recipient_id`),
          KEY `FK_resident_recipient_id` (`recipient_id`),
          KEY `FK_resident_land_if` (`land_id`),
          CONSTRAINT `FK_resident_land_if` FOREIGN KEY (`land_id`) REFERENCES `secuboid_land` (`id`),
          CONSTRAINT `FK_resident_recipient_id` FOREIGN KEY (`recipient_id`) REFERENCES `secuboid_recipient` (`id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
        """})
public class ResidentJPA implements JPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Builder.Default
    private long id = NON_EXISTING_ID;

    @ManyToOne
    @JoinColumn(name = "land_id", nullable = false)
    private LandJPA landJPA;

    @Id
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private RecipientJPA recipientJPA;

    @Id
    @Column(name = "level", nullable = false)
    private int level;
}
