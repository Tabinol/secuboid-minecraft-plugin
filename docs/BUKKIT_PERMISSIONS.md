# Permissions (Bukkit/Spigot)

Here is the list of Bukkit permissons. The * are accepted.

 Secuboid part        |Permission                                    |Défaut|Description
 ---------------------|----------------------------------------------|------|-------------------------------------------------
 General              |secuboid.use                                  |op    |Can use /secuboid command
 &nbsp;               |secuboid.adminmode                            |op    |Can use admin mode
 &nbsp;               |secuboid.adminmode.auto                       |op    |Admin mode on connect
 &nbsp;               |secuboid.collisionapprove                     |op    |Can approve land conflicts
 &nbsp;               |secuboid.socialspy                            |op    |Can read chat form all lands
 &nbsp;               |secuboid.bypassban                            |op    |Exempt of land ban
 &nbsp;               |secuboid.reload                               |op    |Can use "/secuboid reload"
 Inventory (if active)|secuboid.inv.forcesave                        |op    |Can use "/secuboid inv forcesave"
 &nbsp;               |secuboid.inv.default                          |op    |Can use "/secuboid inv default"
 &nbsp;               |secuboid.inv.loaddeath                        |op    |Can use "/secuboid inv loaddeath"
 &nbsp;               |secuboid.inv.list *v1.6.0+*                   |op    |Can use "/secuboid inv list"
 &nbsp;               |secuboid.inv.purge *v1.6.0+*                  |op    |Can use "/secuboid inv purge"
 &nbsp;               |secuboid.inv.ignorecreativeinv                |false |Ignore creative inventory (ignored by secuboid.*)
 &nbsp;               |secuboid.inv.ignoreinv                        |false |Ignore inventory changes (ignored by secuboid.*)
 &nbsp;               |secuboid.inv.ignoredisabledcommands           |op    |Ignore disabled commands
 Creative (if active) |secuboid.flycreative.ignorefly                |op    |Ignore «fly» flag
 &nbsp;               |secuboid.flycreative.ignorecreative           |op    |Ignore «creative» flag
 &nbsp;               |secuboid.flycreative.override.nodrop          |op    |Can deposit in creative
 &nbsp;               |secuboid.flycreative.override.noopenchest     |op    |Can open chests in créative
 &nbsp;               |secuboid.flycreative.override.nobuildoutside  |op    |Can build outside the land
 &nbsp;               |secuboid.flycreative.override.allowbanneditems|op    |Can use banned items

![warning](https://tabinol.github.io/secuboid-minecraft-plugin/images/26A0-50x.png) Some permision systems override "false" default values. Do not use "secuboid.*" or set explicitly "secuboid.inv.ignorecreativeinv" and "secuboid.inv.ignoreinv" to "false"