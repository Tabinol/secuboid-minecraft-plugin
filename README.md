# Secuboid

**Hello, I have to announce that the Secuboid project is unfortunately no longer in development. It's still available on GitHub if someone ever wants to pick it up. I'm sorry but I had to come to this conclusion due to lack of time and because I'm working on other projects.**

Secuboid is a complete land (region) protection plugin. It has a lot of functionalities. You can make cuboid lands, cylinder lands and road (beta) protection. The player can make his own land with economy, rent or sell his land. There is also a very complete list of flags and permissions.

## Help needed
The help is welcome for development and language typos.

## Documentation

* [configuration](https://tabinol.github.io/secuboid-minecraft-plugin/CONFIG)
* [Bukkit permissions](https://tabinol.github.io/secuboid-minecraft-plugin/BUKKIT_PERMISSIONS)
* [How to create a land quick guide](https://tabinol.github.io/secuboid-minecraft-plugin/CREATE_LAND)
* [Tables (Player containers, Permissions, Flags)](https://tabinol.github.io/secuboid-minecraft-plugin/TABLES)
* [Commands](https://tabinol.github.io/secuboid-minecraft-plugin/COMMANDS)
* [Issues](https://github.com/Tabinol/secuboid-minecraft-plugin/issues)
* [For developers](https://tabinol.github.io/secuboid-minecraft-plugin/DEVELOPERS)

## Support
* [Open an issue](https://github.com/Tabinol/secuboid-minecraft-plugin/issues)
* [Secuboid on Discord](https://discord.gg/37PAVevWut)


# Features

## Protection
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F512-100x.png)

* No one can place, destroy blocks, or drop items in your region if you don’t want
* Moderation for every region
* You can ban or kick any player of the region
* You can accept or deny any land when a player creates it
* Pre-visualize your claim with sponges

## Region chat
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/E246-100x.png)

* You can speak in the region just putting =, < or > before your message
* You can spy region chat by giving a permission to your moderators

## Land Management
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F3E1-100x.png)

* Flags ([list here](https://tabinol.github.io/secuboid-minecraft-plugin/TABLES))
* Permissions ([list here](https://tabinol.github.io/secuboid-minecraft-plugin/TABLES))
* You can decide what your players can set themselves in config.yml
* Different roles (owner, resident manager, resident, visitor…)

## Multi Inventories
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/E260-100x.png)

* Inventory by world (shared or no by multiple worlds)
* Inventory by region (can be share by multiple regions)
* The last 9 death inventories are saved and can be load

## Parent and children claims
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F468-200D-1F467-200D-1F466-100x.png)

* The children regions can have flags and permissions themselves

## Multilanguage
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/E24A-100x.png)

* English ![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F1EC-1F1E7-50x.png) ![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F1FA-1F1F8-50x.png)
* Français ![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F1EB-1F1F7-50x.png)
* Add your language by editing the .yml file in /lang folder

## Persistence
![](https://tabinol.github.io/secuboid-minecraft-plugin/images/1F4BE-100x.png)

* Flat files (default)
* MySQL/Maria (since 1.5.0)

## Dependencies

* **Needed**: Vault (groups, economy)
* **Optional**: WorldEdit, Essentials(-X) (chat, vanish), VanishNoPacket (vanish), SuperVanish/PremiumVanish (vanish)

## Metrics

This plugin utilises bStats plugin metrics system, which means that the following information is collected and sent to bstats.org:

* A unique identifier
* The server's version of Java
* Whether the server is in offline or online mode
* The plugin's version
* The server's version
* The OS version/name and architecture
* The core count for the CPU
* The number of players online
* The Metrics version
* The number and type of Secuboid lands/areas and some basic configurations

Opting out of this service can be done by editing plugins/Plugin Metrics/config.yml and changing opt-out to true.

# History

The Secuboid project began in 2014 on the "Factoid" name. The goal was to have a complete protection plugin with an unlimited possibilities of configuration for lands and worlds. We also wanted to add an inventory system for lands, worlds and game modes.

## Former developers

Kaz00

## Actual developers

Ouaou, Tabinol

## Thanks to

Breston (Documentation), Modulmonde staff (Tests)

# License

* Secuboid license is [GPL3](http://fsf.org/)
* OpenMoji icons is [CC BY-SA 4.0](https://openmoji.org/)
