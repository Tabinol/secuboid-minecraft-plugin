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
package me.tabinol.secuboid.storage.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import me.tabinol.secuboid.storage.mysql.DatabaseConnection;
import me.tabinol.secuboid.utilities.MavenAppProperties;

/**
 * Creates database.
 */
public final class SecuboidDao {

    private final static String VARIABLE_VERSION = "VERSION";

    // @formatter:off
    private static final String[] CREATE_TABLE_STMS_V1 = new String[] {
        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_types`({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`)){{LS}}" +
        "ENGINE = InnoDB",
        
        "CREATE TABLE IF NOT EXISTS `{{TP}}player_containers_types` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}players` ({{LS}}" +
        "  `uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`uuid`),{{LS}}" +
        "  UNIQUE KEY `uuid` (`uuid`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}player_containers` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `player_container_type_id` INT NOT NULL,{{LS}}" +
        "  `player_uuid` BINARY(16) NULL,{{LS}}" +
        "  `parameter` VARCHAR(200) NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `player_container_type_id` (`player_container_type_id`, `player_uuid`, `parameter`),{{LS}}" +
        "  INDEX `fk_player_containers_name_id_idx` (`player_container_type_id`),{{LS}}" +
        "  INDEX `fk_player_containers_player_uuid_idx` (`player_uuid`),{{LS}}" +
        "  CONSTRAINT `fk_player_containers_type_id`{{LS}}" +
        "    FOREIGN KEY (`player_container_type_id`){{LS}}" +
        "    REFERENCES `{{TP}}player_containers_types` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_player_containers_player_uuid`{{LS}}" +
        "    FOREIGN KEY (`player_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}players` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}lands` ({{LS}}" +
        "  `uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `name` VARCHAR(200) NOT NULL,{{LS}}" +
        "  `approved` TINYINT NOT NULL,{{LS}}" +
        "  `type_id` INT NULL,{{LS}}" +
        "  `owner_id` INT NOT NULL,{{LS}}" +
        "  `parent_uuid` BINARY(16) NULL,{{LS}}" +
        "  `priority` SMALLINT NOT NULL,{{LS}}" +
        "  `money` DOUBLE NOT NULL,{{LS}}" +
        "  `for_sale` TINYINT NOT NULL,{{LS}}" +
        "  `for_sale_sign_location` VARCHAR(200) NULL,{{LS}}" +
        "  `sale_price` DOUBLE NULL,{{LS}}" +
        "  `for_rent` TINYINT NOT NULL,{{LS}}" +
        "  `for_rent_sign_location` VARCHAR(200) NULL,{{LS}}" +
        "  `rent_price` DOUBLE NULL,{{LS}}" +
        "  `rent_renew` INT NULL,{{LS}}" +
        "  `rent_auto_renew` TINYINT NULL,{{LS}}" +
        "  `tenant_uuid` BINARY(16) NULL,{{LS}}" +
        "  `last_payment_millis` BIGINT NULL,{{LS}}" +
        "  PRIMARY KEY (`uuid`),{{LS}}" +
        "  UNIQUE KEY `uuid` (`uuid`),{{LS}}" +
        "  INDEX `fk_lands_type_id_idx` (`type_id`),{{LS}}" +
        "  INDEX `fk_lands_owner_id_idx` (`owner_id`),{{LS}}" +
        "  INDEX `fk_lands_parent_uuid_idx` (`parent_uuid`),{{LS}}" +
        "  INDEX `fk_lands_tenant_id_idx` (`tenant_id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`),{{LS}}" +
        "  CONSTRAINT `fk_lands_type_id`{{LS}}" +
        "    FOREIGN KEY (`type_id`){{LS}}" +
        "    REFERENCES `{{TP}}lands_types` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_owner_id`{{LS}}" +
        "    FOREIGN KEY (`owner_id`){{LS}}" +
        "    REFERENCES `{{TP}}player_containers` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_parent_uuid`{{LS}}" +
        "    FOREIGN KEY (`parent_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "ENGINE = InnoDB{{LS}}",
        
        "CREATE TABLE IF NOT EXISTS `{{TP}}areas_types` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",
        
        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_areas` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `area_id` INT NOT NULL,{{LS}}" +
        "  `approved` TINYINT NOT NULL,{{LS}}" +
        "  `world_name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  `area_type_id` INT NOT NULL,{{LS}}" +
        "  `x1` INT NOT NULL,{{LS}}" +
        "  `y1` INT NOT NULL,{{LS}}" +
        "  `z1` INT NOT NULL,{{LS}}" +
        "  `x2` INT NOT NULL,{{LS}}" +
        "  `y2` INT NOT NULL,{{LS}}" +
        "  `z2` INT NOT NULL,{{LS}}" +
        "  INDEX `fk_areas_type_id_idx` (`area_type_id`),{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `area_id`),{{LS}}" +
        "  CONSTRAINT `fk_areas_type_id`{{LS}}" +
        "    FOREIGN KEY (`area_type_id`){{LS}}" +
        "    REFERENCES `{{TP}}areas_types` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_areas_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",
        
        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_areas_roads_matrices` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `area_id` INT NOT NULL,{{LS}}" +
        "  `chunk_x` INT NOT NULL,{{LS}}" +
        "  `chunk_z` INT NOT NULL,{{LS}}" +
        "  `matrix` BINARY(16) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `area_id`, `chunk_x`, `chunk_z`),{{LS}}" +
        "  INDEX `fk_areas_roads_matrices_land_uuid_idx` (`land_uuid`),{{LS}}" +
        "  CONSTRAINT `fk_areas_roads_matrices_area_id`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`, `area_id`){{LS}}" +
        "    REFERENCES `{{TP}}lands_areas` (`land_uuid`, `area_id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_areas_roads_matrices_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_residents` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `player_container_id` INT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `player_container_id`),{{LS}}" +
        "  INDEX `fk_lands_residents_player_container_id_idx` (`player_container_id`),{{LS}}" +
        "  CONSTRAINT `fk_lands_residents_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_residents_player_container_id`{{LS}}" +
        "    FOREIGN KEY (`player_container_id`){{LS}}" +
        "    REFERENCES `{{TP}}player_containers` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_banneds` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `player_container_id` INT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `player_container_id`),{{LS}}" +
        "  INDEX `fk_lands_banneds_player_container_id_idx` (`player_container_id`),{{LS}}" +
        "  CONSTRAINT `fk_lands_banneds_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_banneds_player_container_id`{{LS}}" +
        "    FOREIGN KEY (`player_container_id`){{LS}}" +
        "    REFERENCES `{{TP}}player_containers` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}permissions` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `name` VARCHAR(200) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_permissions` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `player_container_id` INT NOT NULL,{{LS}}" +
        "  `permission_id` INT NOT NULL,{{LS}}" +
        "  `value` TINYINT NOT NULL,{{LS}}" +
        "  `inheritance` TINYINT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `player_container_id`, `permission_id`),{{LS}}" +
        "  INDEX `fk_lands_permissions_player_container_id_idx` (`player_container_id`),{{LS}}" +
        "  INDEX `fk_lands_permissions_permission_id_idx` (`permission_id`),{{LS}}" +
        "  CONSTRAINT `fk_lands_permissions_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_permissions_player_container_id`{{LS}}" +
        "    FOREIGN KEY (`player_container_id`){{LS}}" +
        "    REFERENCES `{{TP}}player_containers` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_permissions_permission_id`{{LS}}" +
        "    FOREIGN KEY (`permission_id`){{LS}}" +
        "    REFERENCES `{{TP}}permissions` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}flags` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `name` VARCHAR(200) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_flags` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `flag_id` INT NOT NULL,{{LS}}" +
        "  `value_string` VARCHAR(200) NULL,{{LS}}" +
        "  `value_double` DOUBLE NULL,{{LS}}" +
        "  `value_boolean` TINYINT NULL,{{LS}}" +
        "  `inheritance` TINYINT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `flag_id`),{{LS}}" +
        "  INDEX `fk_lands_flags_flag_id_idx` (`flag_id`),{{LS}}" +
        "  CONSTRAINT `fk_lands_flags_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_flags_flag_id`{{LS}}" +
        "    FOREIGN KEY (`flag_id`){{LS}}" +
        "    REFERENCES `{{TP}}flags` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}lands_players_notifies` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `player_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`, `player_uuid`),{{LS}}" +
        "  INDEX `fk_lands_players_notifies_player_uuid_idx` (`player_uuid`),{{LS}}" +
        "  CONSTRAINT `fk_lands_players_notifies_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_lands_players_notifies_player_uuid`{{LS}}" +
        "    FOREIGN KEY (`player_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}players` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}approves_actions` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}approves` ({{LS}}" +
        "  `land_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `approve_action_id` INT NOT NULL,{{LS}}" +
        "  `removed_area_id` INT NULL,{{LS}}" +
        "  `new_area_id` INT NULL,{{LS}}" +
        "  `owner_id` INT NOT NULL,{{LS}}" +
        "  `parent_uuid` BINARY(16) NULL,{{LS}}" +
        "  `price` DOUBLE NOT NULL,{{LS}}" +
        "  `transaction_datetime` DATETIME NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`land_uuid`),{{LS}}" +
        "  UNIQUE KEY `land_uuid` (`land_uuid`),{{LS}}" +
        "  INDEX `fk_approves_land_uuid_idx` (`land_uuid`),{{LS}}" +
        "  INDEX `fk_approves_approve_action_id_idx` (`approve_action_id`),{{LS}}" +
        "  INDEX `fk_approves_removed_area_id_idx` (`id` ASC, `land_uuid`),{{LS}}" +
        "  INDEX `fk_approves_new_area_id_idx` (`new_area_id`),{{LS}}" +
        "  INDEX `fk_approves_owner_id_idx` (`owner_id`),{{LS}}" +
        "  INDEX `fk_approves_parent_id_idx` (`parent_uuid`),{{LS}}" +
        "  CONSTRAINT `fk_approves_land_uuid`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_approves_approve_action_id`{{LS}}" +
        "    FOREIGN KEY (`approve_action_id`){{LS}}" +
        "    REFERENCES `{{TP}}approves_actions` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_approves_removed_area_id`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`, `removed_area_id`){{LS}}" +
        "    REFERENCES `{{TP}}lands_areas` (`land_uuid`, `area_id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_approves_new_area_id`{{LS}}" +
        "    FOREIGN KEY (`land_uuid`, `new_area_id`){{LS}}" +
        "    REFERENCES `{{TP}}lands_areas` (`land_uuid`, `area_id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_approves_owner_id`{{LS}}" +
        "    FOREIGN KEY (`owner_id`){{LS}}" +
        "    REFERENCES `{{TP}}player_containers` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_approves_parent_id`{{LS}}" +
        "    FOREIGN KEY (`parent_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}lands` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}inventories` ({{LS}}" +
        "  `id` INT NOT NULL,{{LS}}" +
        "  `name` VARCHAR(200) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}inventories_entries` ({{LS}}" +
        "  `id` INT NOT NULL AUTO_INCREMENT,{{LS}}" +
        "  `level` INT NOT NULL,{{LS}}" +
        "  `exp` FLOAT NOT NULL,{{LS}}" +
        "  `health` DOUBLE NOT NULL,{{LS}}" +
        "  `food_level` INT NOT NULL,{{LS}}" +
        "  `item_stacks` TEXT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}inventories_defaults` ({{LS}}" +
        "  `inventory_id` INT NOT NULL,{{LS}}" +
        "  `inventories_entries_id` INT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`inventory_id`),{{LS}}" +
        "  INDEX `fk_inventories_defaults_inventories_id_idx` (`inventory_id`),{{LS}}" +
        "  INDEX `fk_inventories_defaults_entries_id_idx` (`inventories_entries_id`),{{LS}}" +
        "  CONSTRAINT `fk_inventories_defaults_inventories_id`{{LS}}" +
        "    FOREIGN KEY (`inventory_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_defaults_entries_id`{{LS}}" +
        "    FOREIGN KEY (`inventories_entries_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories_entries` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}game_modes` ({{LS}}" +
        "  `id` INT NOT NULL,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`id`),{{LS}}" +
        "  UNIQUE KEY `id` (`id`),{{LS}}" +
        "  UNIQUE KEY `name` (`name`)){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}inventories_saves` ({{LS}}" +
        "  `player_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `inventory_id` INT NOT NULL,{{LS}}" +
        "  `game_mode_id` INT NOT NULL,{{LS}}" +
        "  `inventories_entries_id` INT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`player_uuid`, `inventory_id`, `game_mode_id`),{{LS}}" +
        "  INDEX `fk_inventories_saves_game_mode_id_idx` (`game_mode_id`),{{LS}}" +
        "  INDEX `fk_inventories_saves_inventory_id_idx` (`inventory_id`),{{LS}}" +
        "  INDEX `fk_inventories_saves_player_uuid_idx` (`player_uuid`),{{LS}}" +
        "  INDEX `fk_inventories_saves_entries_id_idx` (`inventories_entries_id`),{{LS}}" +
        "  CONSTRAINT `fk_inventories_saves_inventory_id`{{LS}}" +
        "    FOREIGN KEY (`inventory_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_saves_game_mode_id`{{LS}}" +
        "    FOREIGN KEY (`game_mode_id`){{LS}}" +
        "    REFERENCES `{{TP}}game_modes` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_saves_player_uuid`{{LS}}" +
        "    FOREIGN KEY (`player_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}players` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_saves_entries_id`{{LS}}" +
        "    FOREIGN KEY (`inventories_entries_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories_entries` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}inventories_deaths` ({{LS}}" +
        "  `player_uuid` BINARY(16) NOT NULL,{{LS}}" +
        "  `inventory_id` INT NOT NULL,{{LS}}" +
        "  `game_mode_id` INT NOT NULL,{{LS}}" +
        "  `death_number` INT NOT NULL,{{LS}}" +
        "  `inventories_entries_id` INT NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`player_uuid`, `inventory_id`, `game_mode_id`, `death_number`),{{LS}}" +
        "  INDEX `fk_inventories_deaths_game_mode_id_idx` (`game_mode_id`),{{LS}}" +
        "  INDEX `fk_inventories_deaths_inventory_id_idx` (`inventory_id`),{{LS}}" +
        "  INDEX `fk_inventories_deaths_player_uuid_idx` (`player_uuid`),{{LS}}" +
        "  INDEX `fk_inventories_deaths_entries_id_idx` (`inventories_entries_id`),{{LS}}" +
        "  CONSTRAINT `fk_inventories_deaths_inventory_id`{{LS}}" +
        "    FOREIGN KEY (`inventory_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_deaths_game_mode_id`{{LS}}" +
        "    FOREIGN KEY (`game_mode_id`){{LS}}" +
        "    REFERENCES `{{TP}}game_modes` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_deaths_player_uuid`{{LS}}" +
        "    FOREIGN KEY (`player_uuid`){{LS}}" +
        "    REFERENCES `{{TP}}players` (`uuid`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION,{{LS}}" +
        "  CONSTRAINT `fk_inventories_deaths_entries_id`{{LS}}" +
        "    FOREIGN KEY (`inventories_entries_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories_entries` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB",

        "CREATE TABLE IF NOT EXISTS `{{TP}}inventories_potion_effects` ({{LS}}" +
        "  `inventories_entries_id` INT NOT NULL,{{LS}}" +
        "  `name` VARCHAR(45) NOT NULL,{{LS}}" +
        "  `duration` INT NOT NULL,{{LS}}" +
        "  `amplifier` INT NOT NULL,{{LS}}" +
        "  `ambient` TINYINT(1) NOT NULL,{{LS}}" +
        "  PRIMARY KEY (`inventories_entries_id`, `name`),{{LS}}" +
        "  CONSTRAINT `fk_inventories_potion_effects_entries_id`{{LS}}" +
        "    FOREIGN KEY (`inventories_entries_id`){{LS}}" +
        "    REFERENCES `{{TP}}inventories_entries` (`id`){{LS}}" +
        "    ON DELETE NO ACTION{{LS}}" +
        "    ON UPDATE NO ACTION){{LS}}" +
        "ENGINE = InnoDB" };
// @formatter:on

    private final DatabaseConnection dbConn;
    private final int currentVersion;

    public SecuboidDao(final DatabaseConnection dbConn) {
        this.dbConn = dbConn;
        currentVersion = MavenAppProperties.getPropertyInt("mySqlStorageVersion", 1);
    }

    public void createVarables(final Connection conn) throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS `{{TP}}variables` ({{LS}}" //
                + "  `name` VARCHAR(45) NOT NULL,{{LS}}" //
                + "  `value` VARCHAR(45) NOT NULL,{{LS}}" //
                + "  PRIMARY KEY (`name`),{{LS}}" //
                + "  UNIQUE KEY `name` (`name`)){{LS}}" //
                + "ENGINE = InnoDB";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.executeUpdate();
        }
    }

    public Optional<Integer> getVersionOpt(final Connection conn) throws SQLException {
        final String sql = "SELECT `value` FROM `{{TP}}variables` " //
                + "  WHERE `name` = '" + VARIABLE_VERSION + "'";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                final String valueStr = rs.getString("value");
                try {
                    return Optional.of(Integer.parseInt(valueStr));
                } catch (final NumberFormatException e) {
                    // Non number should be used has empty
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    public void initDatabase(final Connection conn) throws SQLException {
        for (final String sql : CREATE_TABLE_STMS_V1) {
            try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
                stmt.executeUpdate();
            }
        }
    }

    public void setVersion(final Connection conn) throws SQLException {
        final String sql = "INSERT INTO `{{TP}}variables` (`name`, `value`) " //
                + "VALUES('" + VARIABLE_VERSION + "', '" + currentVersion + "') " //
                + "ON DUPLICATE KEY UPDATE `value` = '" + currentVersion + "'";

        try (final PreparedStatement stmt = dbConn.preparedStatementWithTags(conn, sql)) {
            stmt.executeUpdate();
        }
    }
}