---
title: FancyDialogs [WIP] 
icon: browser
order: 90
---

#

![](../static/logos-and-banners/fancydialogs-banner.png)

!!!
Simple and lightweight dialogs plugin for Paper servers and other plugin developers.
!!!

## Features

With this plugin you can create fancy dialogs for your server or plugin. 
FancyDialogs uses the new dialog feature, Minecraft introduced in **1.21.6** and combines it with the power of packets.

!!!warning
The plugin will only for **Paper** servers on **1.21.6** or newer!
!!!

!!!warning
FancyDialogs is still in development and some features are not yet implemented.
See the [TODO](#todo) section for more information.
!!!

**Core advantages of FancyDialogs:**
- Simple custom dialog creation (in JSON format or in code)
- A lot of different dialog components (text, buttons, input fields, etc.)
- Custom button actions (e.g. run commands, give items, open other dialogs)
- MiniMessages and PlaceholderAPI support

### For servers

If you use FancyDialogs as a plugin, you can take advantage of the following features:
- Dialog as welcome-screen for players joining the first time
- Custom tutorial dialogs
- FancyNpcs 'open_dialog' action for NPCs

### For plugin developers

If you are a plugin developer and want to spice up your plugin with dialogs, you can use FancyDialogs as a library.

You can define default dialogs, which will be persisted in the `plugins/FancyDialogs/data/dialogs` folder.
This allows server administrators to customize the dialogs for their server.
You can then use the dialogs in your plugin and show them to the players whenever you want.

Common use cases are:
- Help dialogs (e.g. for commands or features)
- Confirmation dialogs for critical actions
- Shop UIs (replacing inventories UIs)
- Dialogs for quests

## TODO

- [X] Create dialogs in JSON format
- [X] Create dialogs in code
- [X] Customize dialog content and buttons
- [X] Open dialogs for players (via commands or code)
- [X] Welcome dialog for new players
- [X] FancyNpcs integration (open_dialog action)
- [X] Confirmation and notice dialogs (API)
- [ ] Add support for the quick action hotkey
- [ ] Add tutorial features
- [ ] Add input controls
- [ ] Add button action system
