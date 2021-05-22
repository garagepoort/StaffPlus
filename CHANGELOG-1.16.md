# Changelog Staff++ | v1.16

# V1.16.65
### Features
- Add multiple staff chat channels
- Reports can now have fixed reasons (optionally linked to report types)

### Bugs
- Invalid yaml configuration caused configuration file to reset

# V1.16.64
### Breaking change
- Added permission `see-vanished`. Option `staff-see-staff-in-mode` has been removed

### features
- Add reports support for web-ui
- Update upgrade notifier to stop using the spiget api
- All commands can now be disabled by configuring an empty command name using '' 

# V1.16.62
### features
- Mutes/unmutes are now no longer broadcasted by default. A permission node is needed to receive mute notifications
- Web UI registration
- Allow vanish command to be executed by console
- Add command hooks for reports

### bugs
- Issue with NONE vanish strategy
- Fix warn without options threw internal exception
- Fix usage description of commands
- Fix enable-on-login staff mode
- Fix for staff getting stuck in vanish

# V1.16.60

### bugs
- When no prefixed used, messages had an extra space.
- staff-see-staff-in-mode was no longer working
- Fix bug in luckperms context mode calculator
- Fix bug, vanish message shown every time a new player joins
- Fix session sync issues

# V1.16.58

### Features
- Added support for multiple staffmodes
- Investigations can now be started without needing a player to investigate.
- Enforce a certain staff mode when starting/resuming an investigation.
- Permissions have been moved from config.yml to configuration/permissions.yml
- Commands have been moved from config.yml to configuration/commands.yml
- Staff modes have been moved from config.yml to configuration/staffmode/modes.yml
- Staff modules have been moved from config.yml to configuration/staffmode/modules.yml
- Custom Staff modules have been moved from config.yml to configuration/staffmode/custom-modules.yml

### bugs
- Fix synchronisation on namechange of player
- Fix session synchronisation issues. (Vanish desync bug)
- Staffchat message was incorrect when using a multi char handle.
- Fix discord staffchat duplicate prefix

# V1.16.55

### Features
- Added staff toggle fly command
- Added staffchat mute command.
- Added investigations
- Fix reload command. Reload should work for everything now.

### bugs
- Fix staff chat sending in regular chat when using venturechat
- Fixed namechangeEvent failing

# V1.16.54

### bugs
- Luckperms prefix placeholder in staffchat was not using correct player.

# V1.16.53

### Bugs
- lang files were not copied when clean installing the plugin
- The data.yml file writing was not thread safe

# V1.16.52

### Features
- freeze module now allows for a title & subtitle message to be displayed when a player is frozen.
- Custom staff modules can now have a confirmation dialog (chat or gui)
- Custom staff modules can now have an input prompt
- Report changes are notified across the bungee network to staff members and players.
- New chat detection option to notify staff members when players type certain things in chat. Through the alerts system or discord notifications.
- Xray alert notifications are now supported by the discord integration plugin
- Warning Severity levels can now set a default reason, which takes away the need for staff to provide a reason.
- the manage-warnings command can now be used without the playername parameter. This will give you an overview of all warnings
- Added the ability to use text files as ban templates for an easier way of changing the layout of the ban message.
- Bans can now be configured to have configured prefefined reasons.
- Add ban/mute duration to infractions gui.
- Clickable messages now support bukkit color codes (&6, &9, ...)

### Bugs
- Fix bugs when creating reports in a multi server setup having different servers using different world names.
- Fireticks are now stored and restored when exiting/entering staff mode

# V1.16.51

### Features
- Added ability to configure custom report types
- Teleport to report location now works across bungee network
- Reports can now be unassigned by other staff members, not only the assignee.
- Added find-reports command to search reports by specified criteria
- Staff mode enable/disable commands can now be permission based.
- No actions glass panes removed
- Added support for discord integration template packs
- New discord template pack added by Videndum
- Staff chat Discord integration added. (DiscordSRV required)

### Bugs
- Blindness was not removed after unfreeze with freeze prompt enabled
- Fixed Task Terminated exception on disabling S++

### Extras
- Extracted S++ API to https://github.com/garagepoort/staffplusplus-api

# V1.16.49

### Features
- Added warning expiration
- Some small performance improvements
- Add staff mode discord notifications.

# V1.16.48

### Features
- Added appeals to discord integration
- Added discord plugin reload command
- Added the ability to customize discord templates
- Reports now save the location where they were created.
- Report GUI has teleport to button

# V1.16.47

### Features
- Add warning appeal system
- Add warning actions + rollback system
- Staffmode item-change property is now split up into item-drop & item-pickup

### Bugs
- Staff item Endereye no longer gets dropped when spammed

# V1.16.45

### Features
- Timestamp format can now be configured
- Add ID to the guis more consistently
- Add serverName to the guis (only if server sync enabled)
- infractions-top command can be passed an optional type parameter.
- GUI item materials for the infractions GUI can now be configured

### Bugs
- Fix luckperms context error when new players join
- Fix luckperms context not being set correctly

# V1.16.44
### Features
- Rework miner GUI to be more consistent with the other GUIs, pagination + back button
- Add notify warnings message to the lang files
- Add infractions-top command to list players with most infractions.

### Bugs
- Adding notes through the examine gui was broken.
- Adding warnings through the examine gui was broken.
- Custom-modules got broken in the previous release.

### V1.16.43
Migration guide:
https://app.gitbook.com/@staffplusplus-minecraft/s/staffplusplus/migration-guides/migrating-to-greater-than-v1.16.43

### Features
- Added LuckPerms integration for staffmode context
- Reload command now also reloads lang files
- Multi server support 
  - Keep staff mode enabled when switching servers
  - Keep vanish enabled when switching servers
  - ban/mute/warn players across the network
- No join/leave message is displayed when a player is in vanish mode

### Bugs
- Using a non staff item while in staff mode was throwing an exception.
- One of the lang files had a formatting error.

### V1.16.42
### Features
- Reworked chest interaction https://staffplusplus-minecraft.gitbook.io/staffplusplus/features/chest-inventory-interaction
- Added inventory & enderchest interaction to examine GUI
- Added permission for using the vanish command

### Bugs
- inventory interaction was not working even when correct permission were given
- Location inside the gui was showing a question mark character.

### V1.16.40
### Features
- mute/unmute permission split up into 2 different permissions
- ban/unban permission split up into 2 different permissions
- Examine mode now available for offline players
- Implemented enderchest interaction online and offline
- Added custom GUI for Staff Mode. https://staffplusplus-minecraft.gitbook.io/staffplusplus/features/staff-mode/custom-gui

#### Bugs
- Permission for the /reports command was not working properly

### V1.16.39
### Features
- Added mute/temp mute functionality
- Added infractions gui menu.
- Added kick command.
- Update discord integration plugin for kicks and mutes.

#### Bugs
- Time unit four HOUR/h was not working
- Fix duration if expired

### V1.16.37
#### Breaking
- Implemented fine-grained permissions for reports. Permissions have changed, check [reports wiki](https://github.com/garagepoort/StaffPlusPlus/wiki/Reporting#report-permission)! 

#### Features
- Added manage-reports command to open the GUI without being in staff mode
- Add Xray configuration to allow amount/time based notifications. Check [wiki](https://github.com/garagepoort/StaffPlusPlus/wiki/Alerting-System#x-ray) 

#### Bugs
- Added missing "clear inventory bypass" permission to the default config file.
- Fix reports sometimes not showing last line from reason
- Fix `/help staffplus` command

### V1.16.36
#### Bugs
- Nullpointer fix on alt detection
- Create a new permission to ensure not everyone gets report updates

### V1.16.35
#### Features
- Severity levels now working for warnings issued through examine mode
- Ability to interact with the players inventory in examine mode.
- Add reload configuration ability (experimental, see wiki)

#### Bugs
- Unban command logged the kick issuer instead of the unban issuer
- Fix nullpointer OnInventoryOpen
- Fix alt account detection, do not detect same player
- Fix Inventory item duplication on rejoin

### V1.16.33
#### Features
- 1.16.4 support

### V1.16.32
#### Features
- rename 'teleport' command to 'teleportToLocation'
- Add 'teleportHere' command
- Add vanish indicator in the staff list gui
- Add kick reason placeholder to use for the kick message

#### Bugs
- StaffChat not turning off when using the handle
- Staff items triggered by pressure plates
- Better error messages for tempban

### V1.16.30
#### Features
- Teleport players back to their original location

#### Bugs
- Freezing not working through the gui or staff item
- Custom items enchantments not working
- Staff items only worked when clicking on air

### V1.16.29
#### Features
- Notify users when they have open reports
- Notify users when a report of them changes status
- User Reports overview ui
- Deletion of reports
- Notify users when they have warnings
- User Warnings overview ui

#### Bugs
- Fix reset of armor when exiting Staff mode
- Fix commands plugin prefix

### V1.16.28
#### Bugs
- Wrong titles for reports ui
- Closing reason message displayed even when closing-reason-disabled
- Closing reason not working if other plugins intercept the event

### V1.16.27
#### Features
- Implement kick & tempban system (https://github.com/garagepoort/StaffPlusPlus/issues/106)
- Implement kick discord integration
- Implement Alt account detection system
- Implement Alt account detection discord integration
- Add option to disable bungee network sync for staffchat (https://github.com/garagepoort/StaffPlusPlus/issues/111)

#### Bugs
- Fix miner gui
- Fix prevent inventory open in protected areas
- Fix ui color choosing
- Fix performance when autocompleting offline players (https://github.com/garagepoort/StaffPlusPlus/issues/111)

### V1.16.26
#### Features
- Trello integration added for reports
#### Bugs
- Report resolved event was sent when a report was actually rejected

### V1.16.25
#### Features
- Protect player added
- Protect area added
- "Vanish enabled" action bar displayed for staff members
- Now able to add worlds to the location configuration

### V1.16.24
#### Features
- Broadcast messages over the bungee network
#### Bugs
- Older version of 1.16.x were not working

### V1.16.22
#### Features
- Report closing reason support
- Authme integration for supporting staff login
- Trace log feature added

### V1.16.21
#### Features
- Warning discord integration

### V1.16.20
#### Features
- BungeeCord support. StaffChat will now be synced over all servers inside the bungee network.
- Update notifier implemented

### V1.16.19
#### Bugs
- Players could always bypass clear, freeze and teleport commands
- Resolved issue: https://github.com/garagepoort/StaffPlusPlus/issues/91
Items got removed when inspecting a chest. 

### V1.16.18
#### Features
- Add clear inventory bypass permission

#### Bugs
- Plugins directory was wrongly named which made it crash on Linux machines 

### V1.16.16
#### Features
- Delay argument has been added
- Implemented automatic update for the config file 

#### Bugs
- teleport command was not present in the configuration file

### V1.16.15
#### Bugs
- DELAY Action type not working when users are online

### V1.16.14
#### Features
- Teleport command added

#### Breaking
- Added new warning system

### V1.16.12
#### Features
- Implement new reportEvents
- Added Discord Integration for reporting events

#### Bugs
- Use ZonedDateTimeFor Reporting
- Remove silent sql failure

### V1.16.11
#### Features
- Added HEALTH argument
- New reporting system implemented

#### Bugs
- Fix bug when opening different inventories (BrewingStand, Furnace, Dispenser, ...)

#### Breaking
- Dropped support for flatfiles
- Implemented Sqlite, Mysql databases

### V1.16.10
#### Features
- New arguments system implemented
- Added strip argument. Example: `/freeze player -S`
- Added wiki to github
- Autocompletion for offline players
- Autocompletion for arguments

### V1.16.9
#### Features
- Locations implemented
- Freeze command now has the ability to take a teleportLocation parameter `/freeze myplayer -Tspawn`

### V1.16.8
#### Features
- Allow reporting/warning offline users

### V1.16.6
#### Bugfix
- Logout commands on freeze can be disabled configuring: `logout-commands: ''`


