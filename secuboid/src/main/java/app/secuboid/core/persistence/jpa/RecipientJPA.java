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

package app.secuboid.core.persistence.jpa;

import app.secuboid.api.persistence.CreateTable;
import app.secuboid.api.persistence.JPA;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static app.secuboid.api.persistence.WithId.NON_EXISTING_ID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "secuboid_recipient", uniqueConstraints = @UniqueConstraint(columnNames = {"short_name", "value", "uuid"}))
@CreateTable({"""
        CREATE TABLE `secuboid_recipient` (
          `id` bigint(20) NOT NULL AUTO_INCREMENT,
          `short_name` varchar(10) NOT NULL,
          `uuid` binary(16) DEFAULT NULL,
          `value` varchar(255) DEFAULT NULL,
          PRIMARY KEY (`id`),
          UNIQUE KEY `UK_recipient_001` (`short_name`,`value`,`uuid`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
        """})
public class RecipientJPA implements JPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Builder.Default
    private long id = NON_EXISTING_ID;

    @Column(name = "short_name", nullable = false, length = 10)
    private String shortName;

    @Column(name = "value")
    private String value;

    @Column(name = "uuid")
    private UUID uuid;
}
