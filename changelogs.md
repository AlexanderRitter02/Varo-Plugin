# Version 2.1.0

#### Added
- **Kill tracker:** Kills are now saved and counted ([#26][i26])
- **Strike tracker:**  Use `/varo.strike <add|remove|list> <player>` to give out strikes (punishment is still manual) ([#27][i27])
- **Varo winmessage:**  Broadcast on Discord includes kill and strike stats as well as armor and weapon info. ([#13][i13])

#### Changed
- You can craft ender eyes now
- Offline coordinate posts use whole numbers

#### Fixed
- Worldborder not getting appropriately shrunken by missed time after server was down
- Worldborder in the nether/end never getting missed time shrunken
- Shrink console message appears for each world instead of just one for all borders ([#25][i25])
- Temporary admin position is overwritten when `/reload`-ing the server ([#8][i8])

#### Internals
- Create utility class `AdvancedOfflinePlayer.java`
- Remove code duplication in coordinate posts

[i8]: https://github.com/AlexanderRitter02/Varo-Plugin/issues/8
[i13]: https://github.com/AlexanderRitter02/Varo-Plugin/issues/13
[i25]: https://github.com/AlexanderRitter02/Varo-Plugin/issues/25
[i26]: https://github.com/AlexanderRitter02/Varo-Plugin/issues/26
[i27]: https://github.com/AlexanderRitter02/Varo-Plugin/issues/27


# Version 2.0.0

#### Added
- **HUD options**:  
Change where varo info gets displayed per player: `/varo.hud <scoreboard | actionbar | tab | chat>`  
Set a default in the config with `hud-default`.
- Display **spawn coordinates** with `/border`
- Coordinates can be posted **more than one time** per week
- Discord messages for `/varo.start` and `/varo.stop`
- Your config.yml will update automatically now, no need to change values manually


#### Changed
- Gamerules and difficulty won't be overwritten
- Border center is now being set to worldspawn
- Border config options are more readable now, renamed `projecttime` to `border.shrink-time`

#### Fixed
- No remaining time being broadcast to player if session length is over 15 minutes
- Adding admin being broadcast to all online players
- Command syntax is sent in `/varo.register` when registering fails even if the command is right


#### Internals
- Use BorderMode and HUDoption enums

# Version 1.8.0

#### Added
- **ADMIN role, either temporary or forever**
  It excludes the player from all Varo events and allows him to fix something or watch over the game
  Commands: `/maintainence <player>` (temporary) & `/maintainence <player> forever` (forever admin)
- Configuration option `allow-prevent-coordinate-post` to allow/deny players to use /coordinates, so their coordinates don't get posted later

#### Fixed
- `spawn-teams-together` option not working sometimes
- /varo.spawn does not display "Spawnpoint wurde erfolgreich überschrieben." for spawnpoints lower than the last registered spawnpoint
- If a spectator is online, the time for all players stops

#### Internals
- Removed now unused original Calendar class


# Version 1.7

#### Added
- `spawn-teams-together` config option for player start distribution:
   true: teams are spawned side by side
   false: all players are spawned randomly

#### Fixed
- `/kill` doesn't kill players with spawn protection or before the start of Varo


# Version 0.9.2

#### Added
- Coordinate post time can now be set to: DAY, HOUR

#### Fixed
- Teampartners' "postedcoords" does not get set when using /coordinates
- Stop command doesn't reset "postedcoords" and "dead"
- When player is online while week gets updated his data will get overwritten with old data
- Sysout of current day and configured day for coordinate post in Calendar.checkCoordinatePost()
- Dependence of various events on a player joining the server
- Getting teleported above lava or into blocks when logging in outside the worlborder

#### Internals
- Added method getTeamMembers() in Registration, simplified some cases where it would be needed
- Removed Calendar in favour of PerHourChecker
- Improved Worlborder.getHighestBlock(), shorter


# Version 0.9.1

#### Fixed
- Coordinates are not rounded on post
- NullPointerException if coordinates are attempted to post for a player who never joined the server


# Version 0.9

#### Added
- Config option for Discord text channel
- Config option for default varo world
- Config option for coordinate post day
- Automagically post coordinates on a specific day, if team hasn't posted coordinates with /coordinates before

#### Changed
- Coordinates now get posted for a team, not for the player

#### Fixed
- Team deletion not working
- Some plugin messages not getting sent into the #ingame Discord channel
- Crashes when trying to change lobby name in the config
- NullPointerException when using /coordinates when VaroPlayer not loaded
- Conflict with Multiverse plugin: Multiverse overrides world spawn to lobby -> Fix: plugin now changes Multiverse option 'firstspawnoverride' to false on load
- Player being teleported to lobby world after revival
- Config now adds new keys on plugin update
- Not able to fly after gamemode 1 or 3 while in LoginProtection
- Strange coloring of /coordinates message
- Worldborder may shrink when it shouldn't because hour not saved on shutdown
- Successful load of lobby&world is not logged

#### Internals
- Fixed plugin sometimes not using getLobby() but instead Bukkit.getWorld("lobby")
- Fixed Registration.getAllTeams() not returning toLowerCase() values
- Removed getColor(), setUUID(UUID) and setEnemyNear(boolean)in VaroPlayer
- Moved Discord channels from hardcoded to config, new Setting getDiscordChannelId()
- Moved "world" name from hardcoded to config, new Setting getVaroWorld()
- Removed unnecessary comment ("// TODO TESTs") at Settings.setRunning(boolean) line 107
- Removed unnecessary setSessionsPerWeek(int), setSessionLength(int), setMinLogoutDistance(int), setFriendlyFire(boolean), setStartProtection(int) and setLoginProtection(int) in Settings.java
- Removed one strange "double" semicolon (;) at TeamChest.onBreak() at line 115
- Removed unnecessary lines setting world properties in CMDstart
- Improved testEnemyNear() method in type VaroPlayer


# Version 0.8

#### Added
- New message (_"Dein Team ist tot. Bald wird ein Spectator-Mode implementiert"_) when whole team is dead

#### Fixed
- World border shrinks 120x faster
- ConcurrentModificationException at de.lordquadrato.varo.timemanagment.Gametime.run(Gametime.java:32), happening when more players leave at the same time
- NullPointerException when player leaves while the start countdown is started (at Varo.reloadIngamePlayers()

#### Internals
- Removed unnecessary ban in Countdown.start() for players not registered
- Differenciate between dead player and dead team


# Version 0.7

#### Added
- Config option for how long the project should last (projecttime)
- Remove any withers and enderdragons on start too

#### Changed
- Worldborder size is now given as radius in config option borderradius (instead of bordersize)

#### Internals
- Made Wordborder.class non-static
- Replaced Worldborder.decreaseby() with void startShrinking()


# Version 0.5 & 0.6

#### Added
- Basic world border getting smaller (Option for bordersize in config.yml)
- Three mode if player is outside of the border: 0 - Kill player; 1 - Put player back near worldborder edge; 2 - Teleport player to spawn (Option bordermode in config.yml)
- Success message on team deletion
- Proper warning message when illegal potion is brewed
- Sound (Villager.NO) when crafting illegal items
- Destroy all arrows on start
- Error message if there are more players than spawns
- Error message when no spawns are registered

#### Fixed
- NullPointerException if there are more players than spawns
- NullPointerException using /varo.start when no spawns are registered
- NullPointerExections in LoginProtectionListener after player was killed
- Player's inventory and stats don't get reset when they are revived
- Player being teleported on top of the nether roof when outside the border
- Warning message when no lobby world present not shown on first start

#### Changed
- (Start-) Protection time now also shows in the action bar
- Renamed /varo.setspawn to /varo.spawn

#### Removed:
- bad and unfinished spectator implementation
- /session command
- Ability to hide scoreboard
- Negative Effects when trying to eat and enchanted golden apple
- Unnecessary sys.out() after starting the game
- Unnecessary debugs with illegal potions


#### Internals
- Added unregistering in LoginProtectionListener.unregister()
- Added ActionBar API utility
- Removed static constant storage Var.class
- Removed unnecessary null checks in LoginProtectionListener
- Removed unnecessary containsKey check in PlayerManager.removeIngamePlayer(Player)
- Removed unused boolean argument in VaroPlayer.save()
- Removed unused PlayerManager.addIngamePlayer(UUID, VaroPlayer) and PlayerManager.removeIngamePlayer(UUID) and PlayerManager.getIngamePlayer(UUID)
- Moved permission handling (in CMD* classes) to plugin.yml & Removed no.perm constant
- Moved command usage handling into command registering
- Moved config default handling into separate file (config.yml)
- Moved from Init.class to Main
- Rewritten yml file handling
- Renamed Ingame_Player to VaroPlayer
- Used ENUM names for saving team colors in file
- Commands now properly `return true` when successfull
- Calendar now updates every time, not only when Varo is started
- On /varo.start worldborders get set back to original size














