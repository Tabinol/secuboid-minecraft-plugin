# Commands

Note : The commands «/secuboid» can be changed with «/sd»

## Generals

### /secuboid help [COMMAND]
Show help for a Secuboid command. Without an argument, the general help will be shown.

## Lands

**Note: You have to select the land (right click with selection tool) before using commands.**

### /secuboid area

Create or modify an area.

`/secuboid area add`

Adds an area to the selected land.

`/secuboid area remove [NUMBER]`

Removes an area from the land. It takes the selected area or specify an area number.

`/secuboid area replace [NUMBER]`

Replace an area from the land. It takes the selected area or specify an area number.

### /secuboid ban

Bans a player from a land.

`/secuboid ban add PLAYER_CONTAINER`

Adds a player to the ban list.

`/secuboid ban remove PLAYER_CONTAINER`

Removes a player from the ban list.

`/secuboid ban list`

Show the ban list.

### /secuboid bottom [NUMBER]

Defines the bottom of the land for land selection. Zero or positive is an absolute high (y). A negative number is from the player position.

### /secuboid cancel

Cancel the selection. You have to do it twice to cancel the selected land modification. Equivalent: Right click with selection item (default: Rotten flesh).

### /secuboid create LAND_NAME

Creates a new land. The area must be selected.

### /secuboid default

Put back parameters, permissions and flags from default for the land.

### /secuboid flag

Land flag.

`/secuboid flag add FLAG VALUE`

Adds a flag with the specified value.

`/secuboid flag remove FLAG`

Remove a flag. The flag will have the default value or the value from the parent.

`/secuboid flag list`

Flag list from the land.

### /secuboid info [LAND_NAME]

Alias: */sd current,/sd here*

Show general information from the land. If no land is specified, the land from the player position (if exists) will be selected. Equivalent: Left click with information tool (default: bone).

### /secuboid kick PLAYER_NAME [LAND_NAME]

Kick a player from the land. If no land is selected, you can specify one at the and of the command.

###  /secuboid list

Show land list. By default, show lands owned by the command sender.

`/secuboid list PLAYER_NAME`

Show land list owned by the player.

`/secuboid list type TYPE_NAME`

Show land list from this type.

`/secuboid list world WORLD_NAME`

Show land list from this world.

###  /secuboid money

Adds or removes money from a land. The money is added or removed from the command sender. The economy must be activated.

`/secuboid money deposit AMOUNT`

Add the amount to the land.

`/secuboid money withdraw AMOUNT`

Remove the amount from the land.

`/secuboid money balance`

Show the balance from the land.

### /secuboid notify [LAND_NAME]

Activate or deactivate the land notification. If it is activated, the command sender will be notified when a player enters or exists from a land. If the land is not selected, you can specify the land name.

### /secuboid owner PLAYER_CONTAINER

Change the land owner.

### /secuboid parent LAND_NAME/unset

Adds a parent to the land. «unset» removes the parent.

### /secuboid permission

Alias: */sd perm*

Land permissions.

`/secuboid perm add PERMISSION PLAYER_CONTAINER TRUE/FALSE`

Adds a permission with the specified value.

`/secuboid perm remove PERMISSION PLAYER_CONTAINER`

Remove a permission for the player container. The permission will have the default value or the value from the parent.

`/secuboid perm list`

Show permission list from the land.

### /secuboid priority NUMBER

Sets the land priority. The child land takes his parent priority. If two lands or more make collision, the highest priority land will be active. Do not make collision with lands at the same priority, exempt for parents and childs.

### /secuboid radius RAY

Sets the ray distance (square) when a road land is selected.

### /secuboid remove [force/recursive]

Destroys the selected land. "force" permits to delete a land with children. "recursive" deletes also the children.

### /secuboid rename NEW_NAME

Rename the selected land.

###  /secuboid rent PRICE NUMBER_OF_DAYS [auto renew: TRUE/FALSE]

Puts a land for rent at a price per days. Add "true" or "false" if you want to automatically renew after the and of the times. It will puts a clickable sign where you are looking. You must look inside the land and have a sign in your hand.

### /secuboid resident

Alias: */sd res*

Land residents.

`/secuboid res add PLAYER_CONTAINER`

Adds a resident (or resident group).

`/secuboid res remove PLAYER_CONTAINER`

Remove a resident (or resident group).

`/secuboid res list`

Show resident list.

### /secuboid sale PRICE

Puts a land for sale. It will puts a clickable sign where you are looking. You must look inside the land and have a sign in your hand.

### /secuboid select

Alias: */sd sel*

Selection command. Bye default, a new "cuboid" area is selected in "expand" mode.

`/secuboid sel LAND_NAME (or land LAND_NAME)`

Select a land for modifications. Equivalent : Left click with selection tool (default: rotten flesh).

`/secuboid sel cub (or cuboid)`

Creates a "cuboid" type selection.

![select_cuboid.png](https://tabinol.github.io/secuboid-minecraft-plugin/images/select-cuboid.png)

`/secuboid sel cyl (or cylinder)`

Creates a "cylinder" type selection.

![select_cylinder.png](https://tabinol.github.io/secuboid-minecraft-plugin/images/select-cylinder.png)

`/secuboid sel roa (or road)`

Creates a "road" type selection.

![select_road.png](https://tabinol.github.io/secuboid-minecraft-plugin/images/select-road.png)

`/secuboid sel exp (or expand)`

The selection is in expansion mode (by default).

`/secuboid sel ret (or retract)`

The selection is in retraction mode.

![select_retract.png](https://tabinol.github.io/secuboid-minecraft-plugin/images/select-retract.png)

`/secuboid sel mov (or move)`

The selection is in move mode.

`/secuboid sel done`

Froze the selection.

`/secuboid sel worldedit`

Gets selection from WorldEdit.

`/secuboid sel are (or area) [NUMBER]`

Select an area in expand mode. Equivalent : Left click two times with selection tool (default: rotten flesh). When done, use: /sd area replace

### /secuboid setspawn

Takes the actual player position for the teleportation zone to the land.

### /secuboid top [NUMBER]

Defines the top of the land for land selection. Zero or positive is an absolute high (y). A negative number is from the player position.

### /secuboid tp LAND_NAME

Teleports the player to the land spawn point defined with the "/sd spawn" command.

### /secuboid type

Land types (categories).

`/secuboid type list`

List available types.

`/secuboid type TYPE_NAME`

Change land type.

`/secuboid type remove`

Remove land type (allowed but not recommended).

### /secuboid who [LAND_NAME]

Show the player list from the land. If the land is not selected, you can specify the land name.
## Administrators

### /secuboid adminmode

Alias: */sd am*

Bukkit permission: *secuboid.adminmode, op*

Activates or deactivates admin mode. If active, you can do everything and the created land will be "admin" type. When it is deactivated, you are like a normal player and you can not pass over land permissions.

### /secuboid reload

Reload the configuration and the land list.

### /secuboid approve

Bukkit permission: *secuboid.collisionapprove, op*

Approve land conflicts.

`/secuboid approve list`

Show conflict land list.

`/secuboid approve info LAND_NAME`

Show conflict information from the land.

`/Secuboid approve confirm LAND_NAME`

Accept land.

`/secuboid approve cancel LAND_NAME`

Reject land.

`/secuboid approve clear`

Removes **all the land requests** in conflict list.

## Inventory

Note: Inventory must be activated to use those commands.

### /secuboid inventory

Alias: */sd inv*

Inventory command.

`/secuboid inventory default`

Bukkit permission: *secuboid.inv.default, op*

Save player inventory has default global.

`/secuboid inventory loaddeath JOUEUR [1-9]`

Bukkit permission: *secuboid.inv.loaddeath, op*

Give back an inventory before player death. You can specify a number from 1 to 9 to specify from how many death you went to give back (1 for the last death).

`/secuboid inventory forcesave`

Bukkit permission: *secuboid.inv.forcesave, op*

Force inventory save from all players.

`/secuboid inventory list` *v1.6.0+*

Bukkit permission: *secuboid.inv.list, op*

List available inventories.

`/secuboid inventory purge INVENTORY_NAME` *v1.6.0+*

Bukkit permission: *secuboid.inv.purge, op*

Purge (completely remove) an inventory.

