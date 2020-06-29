# Tables

## Player containers

A player container can be the player name, or any other containers listed here. If the player container needs a parameter in a command line, you need to write it like this table. Example: "g:newbies". in a YML, you need to do for example:

        :::YML
            playercontainers: [ g:newbies ]



Player containers list in priority order:

Name       | Parameter         | Description
-----------|-------------------|------------------------------------------------------------------------------
Owner      | O                 | Land owner
Player     | P:`<Player name>` | You can write directly the player name without the word "player" (except .yml)
Resident   | R                 | Land resident
Tenant     | T                 | Land tenant
Group      | G:`<Group name>`  | Permission system group
Permission | B:`<Permission>`  | Bukkit permission (xxx.yyy. ... or group.xxx)
Everybody  | E                 | Everybody
Nobody     | N                 | Nobody (You really want to give access to nobody?)

## Flags

A flag is a configuration that effects the land/world and is attributed to all players.

Flag                  | Type            | Default value | Description
----------------------|-----------------|---------------|------------------------------------------------------------------------------------------------------------------------------------
INHERIT_OWNER         | boolean         | true          | Inherits permissions of parent land owner
INHERIT_RESIDENTS     | boolean         | true          | Inherits permissions of parent residents
INHERIT_TENANT        | boolean         | true          | Inherits permissions of parent tenant
FIRESPREAD            | boolean         | true          | Fire spreads
FIRE                  | boolean         | true          | Allow fire
EXPLOSION             | boolean         | true          | Allow explosions
CREEPER_EXPLOSION     | boolean         | true          | Creepers explosions
TNT_EXPLOSION         | boolean         | true          | TNT explosions
FIREWORK_EXPLOSION    | boolean         | true          | Firework explosions
END_CRYSTAL_EXPLOSION | boolean         | true          | End crystal explosions
CREEPER_DAMAGE        | boolean         | true          | Creepers damage
ENDERMAN_DAMAGE       | boolean         | true          | Endermans damage
WITHER_DAMAGE         | boolean         | true          | Whiters damage
GHAST_DAMAGE          | boolean         | true          | Ghasts damage
ENDERDRAGON_DAMAGE    | boolean         | true          | Ender dragons damage
TNT_DAMAGE            | boolean         | true          | TNT damage
END_CRYSTAL_DAMAGE    | boolean         | true          | End crystal damage
MOB_SPAWN             | boolean         | true          | Hostile monsters spawn
ANIMAL_SPAWN          | boolean         | true          | Animals spawn
VILLAGER_SPAWN        | boolean         | true          | Villagers spawn
FULL_PVP              | boolean         | true          | Players versus players
MESSAGE_ENTER         | text            |               | Message on land enter
MESSAGE_EXIT          | text            |               | Message on land exit
ECO_BLOCK_PRICE       | double (123.45) | 0             | Price per block for a land
EXCLUDE_COMMANDS      | string list     |               | Banned commands list
SPAWN                 | text            |               | Land spawn point (created with /secuboid setspawn)
LEAF_DECAY            | boolean         | true          | Leafs decay
CROP_TRAMPLE          | boolean         | true          | Crop trample from every source (animal, player)
WATER_FLOW            | boolean         | true          | Water flow
LAVA_FLOW             | boolean         | true          | Lava flow
PORTAL_WORLD          | text            |               | Teleport the player to a specific world
PORTAL_LAND           | text            |               | Teleport the player to a specific land (the land must have a spawn point)
PORTAL_WORLD_RANDOM   | text            |               | Teleport the player to a random point and outside a land to a specific world (Be sure to adjust "/worldborder" on the destination!)
PORTAL_LAND_RANDOM    | text            |               | Teleport the player randomly inside a specific land (the land must include ground level)
INVENTORY             | text            | Default       | Name of the inventory: MultipleInventories must be "true" in config.yml and the inventory name must be defined in inventory.yml

## Permissions

A permission is what a player, or a group (player container) can do or not do in a land or a world. A permission is always boolean (true/false).

Permission                                                                          | Default | Description
------------------------------------------------------------------------------------|---------|------------------------------------------------------------------------
BUILD                                                                               | true    | Place or destroy a block
BUILD_PLACE                                                                         | true    | Place a block
BUILD_DESTROY                                                                       | true    | Destroy a block
DROP                                                                                | true    | Drop item on the floor
PICKUP                                                                              | true    | Pickup an item
SLEEP                                                                               | true    | Sleep zzzzz!
OPEN                                                                                | true    | Open a container (does not affect doors!)
OPEN_CRAFT                                                                          | true    | Open a craft table
OPEN_BREW                                                                           | true    | Open a brew
OPEN_FURNACE                                                                        | true    | Open a furnace
OPEN_CHEST                                                                          | true    | Open a chest
OPEN_ENDERCHEST                                                                     | true    | Open an ender chest
OPEN_BEACON                                                                         | true    | Open a beacon
OPEN_HOPPER                                                                         | true    | Open a hopper
OPEN_DROPPER                                                                        | true    | Open a dropper
OPEN_DISPENSER                                                                      | true    | Open a dispenser
OPEN_JUKEBOX                                                                        | true    | Open a jukebox
OPEN_SHULKER_BOX                                                                    | true    | Open a shulker box
OPEN_LECTERN                                                                        | true    | Open a lectern
OPEN_BARREL                                                                         | true    | Open a barrel
OPEN_BEEHIVE                                                                        | true    | Open a beehive
USE                                                                                 | true    | Use doors, buttons, etc.
USE_DOOR                                                                            | true    | Use doors and traps
USE_BUTTON                                                                          | true    | Use buttons
USE_LEVER                                                                           | true    | Use levers
USE_PRESSUREPLATE                                                                   | true    | Use pressure plates
USE_TRAPPEDCHEST                                                                    | true    | Use trapped chests
USE_STRING                                                                          | true    | Use strings
USE_ENCHANTTABLE                                                                    | true    | Use enchant tables
USE_ANVIL                                                                           | true    | Use anvils
USE_MOBSPAWNER                                                                      | true    | Use mob spawners
USE_LIGHTDETECTOR                                                                   | true    | Use light detectors
USE_COMPARATOR                                                                      | true    | Use comparator
USE_REPEATER                                                                        | true    | Use repeater
USE_NOTEBLOCK                                                                       | true    | Use note block
USE_VEHICLE                                                                         | true    | Use vehicle (boat, minecart, ...)
USE_BELL                                                                            | true    | Use a bell
USE_GRINDSTONE                                                                      | true    | Use a grindstone
USE_STONECUTTER                                                                     | true    | Use a stone cutter
ANIMAL_KILL                                                                         | true    | Kill animals
TAMED_KILL                                                                          | true    | Kill tamed animals
MOB_KILL                                                                            | true    | Kill mobs
VILLAGER_KILL                                                                       | true    | Kill villagers hein!
VILLAGER_GOLEM_KILL                                                                 | true    | Kill villager golems
HORSE_KILL                                                                          | true    | Kill horses
WATERMOB_KILL                                                                       | true    | Kill watermobs (fishes, dolphins, ...)
BUCKET_WATER                                                                        | true    | Deposit water
BUCKET_LAVA                                                                         | true    | Deposit lava
FIRE                                                                                | true    | Put fire
AUTO_HEAL                                                                           | false   | Automatically heal
FOOD_HEAL                                                                           | true    | Automatically feed
EAT                                                                                 | true    | Can eat
EAT_CHORUS_FRUIT                                                                    | true    | Can eat chorus fruit
POTION_SPLASH                                                                       | true    | Potion splashes
TAME                                                                                | true    | Tame a pet
TRADE                                                                               | true    | Trade with villagers
FROST_WATER                                                                         | true    | Frost water
RESIDENT_MANAGER                                                                    | false   | Add or remove a resident
LAND_CREATE                                                                         | false   | Create a sub-land
LAND_OWNER                                                                          | false   | Owner access (land admin, removed on owner change)
LAND_TENANT                                                                         | false   | Tenant access (removed on unrent/tenant change)
LAND_ENTER                                                                          | true    | Enter on the land
LAND_REMOVE                                                                         | false   | Remove a land
LAND_KICK                                                                           | false   | Kick players from a land
LAND_BAN                                                                            | false   | Ban a player from a land
LAND_WHO                                                                            | false   | List player on a land
LAND_NOTIFY                                                                         | false   | Activate player enter/exit notification
MONEY_DEPOSIT                                                                       | false   | Deposit money
MONEY_WITHDRAW                                                                      | false   | Withdraw money
MONEY_BALANCE                                                                       | false   | Show money balance
ECO_LAND_FOR_SALE                                                                   | false   | Sell a land
ECO_LAND_BUY                                                                        | false   | Buy a land
ECO_LAND_FOR_RENT                                                                   | false   | Put for rent a land
ECO_LAND_RENT                                                                       | false   | Rent a land
ENDERPEARL_TP                                                                       | true    | Use ender pearls
TP                                                                                  | false   | Teleport to the land spawn point
TP_DEATH                                                                            | false   | On death, return to the land spawn point
LAND_DEATH                                                                          | false   | Kill player on land entrance (ouch!)
CROP_TRAMPLE                                                                        | true    | Crop trample
GOD                                                                                 | false   | No damage
END_PORTAL_TP                                                                       | true    | Use ender portals
NETHER_PORTAL_TP                                                                    | true    | Use nether portals
CREATIVE                                                                            | false   | Creative mode automatically activated (must be activated in config.yml)
FLY                                                                                 | false   | Fly automatically activated (must be activated in config.yml)
PLACE_[item](https:///hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)    | false   | Place a specific item ex: PLACE_WOOL (where «build» is disabled)
NOPLACE_[item](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)   | false   | Prevent to place a specific item (where «build» is enabled)
DESTROY_[item](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)   | false   | Destroy a specific item (where «build» is disabled)
NODESTROY_[item](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html) | false   | Prevent to destroy a specific item (where «build» is enabled)
PORTAL_TP                                                                           | true    | Teleport if PORTAL_XXX flag is activated
