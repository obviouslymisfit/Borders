# Borders – Progressive World Border Mod (Fabric)

Borders is a lightweight, server-side Fabric mod that turns Minecraft survival into a progressive exploration challenge.
The world begins inside a tiny border and expands only as players make progress.

## Core Features

### World Border Progression
- World border starts small (16×16).
- Border expands when players discover **new unique items**.
- Optional shrink on player death.
- Automatic border expansion after long periods of inactivity.
- Border size synchronized across Overworld, Nether, and End.

### Config Book (In‑Game Admin UI)
Operators can run:
```
/borders book
```
This opens an interactive written book containing:
- Game control actions
- Growth/shrink configuration
- Inactivity timer settings
- Manual border adjustments
- Full info and help pages
- Clickable buttons and page navigation
- “Back to Home” links on all pages

### Scoreboard
Displays:
- Current border size
- Player discovery scores

### Commands Overview

**Core**
- `/borders start`
- `/borders stop`
- `/borders reset`
- `/borders info`
- `/borders reload`

**Configuration**
- `/borders setgrowth <blocksPerSide>`
- `/borders setdeathshrink <blocksPerSide>`
- `/borders toggledeathshrink`
- `/borders settimer <seconds>`

**Manual Control**
- `/borders grow <blocksPerSide>`
- `/borders shrink <blocksPerSide>`

**Utility**
- `/borders help`
- `/borders book`

## Architecture Overview
- BorderManager
- CommandManager
- DiscoveryManager
- DeathManager
- FailsafeManager
- ScoreboardManager
- MessageManager
- InventoryTracker
- BookManager
- Global state container: `GameState`

## Installation
Server‑side only. Requires:
- Fabric Loader
- Fabric API

Place the mod JAR into your server's `mods` folder.

## License
See `LICENSE`.
