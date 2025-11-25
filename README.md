# Borders – Progressive World Border Mod (Fabric)

**Borders** is a small, server-side Fabric mod that turns Minecraft survival into a **progressive exploration challenge**.

The world starts inside a tiny border.  
As players discover new items, the border expands and more of the world becomes available.  
Deaths, inactivity, and configuration options all influence how the border behaves.

This mod is designed for **multiplayer survival servers** that want a focused, challenge-style progression without adding a ton of new content or complexity.

---

## 🔍 Core Concept

- The **world border starts very small** (16×16 blocks – 1 chunk).
- Whenever **any player obtains a brand-new item** (first time ever in this world):
  - The **world border expands** by a configurable amount.
  - A **global discovery message** is broadcast in chat.
  - The discovering player gains **+1 point** on a dedicated scoreboard.
- If **no new items are discovered** for too long:
  - An **inactivity expansion** automatically expands the border.
  - A themed message is shown to everyone.
- If the **death-shrink mechanic** is enabled:
  - When a player dies, the border **shrinks** by a configurable amount.
  - A snarky, darkly humorous message announces who screwed everyone.

This mod supports **Overworld, Nether, and End** borders in sync, and is fully controlled via `/borders` commands by server operators.

---

## 🧠 Design Goals

- **Simple to understand**
- **No client mods required**
- **Lore-friendly**
- **Minimal but powerful configuration**
- **Clear feedback for players**

---

## ⚙️ Gameplay Mechanics

### World Border
- Starts at **16×16**.
- Expands or shrinks based on discoveries or deaths.
- Synced across **all dimensions**.

### Item Discovery → Border Growth
- Each *unique* item discovered = border expands.
- Expansion per side is configurable.

### Death → Border Shrink (Optional)
- Border shrinks when a player dies (toggleable).
- Snarky message included.

### Inactivity → Automatic Growth
- If no discoveries for X seconds -> border expands automatically.
- Broadcast message included.

---

## 🌍 Dimensions

- **Overworld** – primary logic
- **Nether** – synced border size
- **End** – synced border size

---

## 📊 Scoreboard

Sidebar scoreboard tracks:

- Number of unique items found per player
- Current border size (informational line)

---

## 🧾 Commands

### `/borders help`
Shows all commands, formatted.

### Core Commands
- `/borders start`
- `/borders stop`
- `/borders reset`
- `/borders info`
- `/borders reload`

### Configuration
- `/borders setgrowth <blocksPerSide>`
- `/borders setdeathshrink <blocksPerSide>`
- `/borders toggledeathshrink`
- `/borders settimer <seconds>`

### Manual Border Control
- `/borders grow <blocksPerSide>`
- `/borders shrink <blocksPerSide>`

---

## 🧬 Architecture Overview

Managers:
- `BorderManager`
- `CommandManager`
- `DiscoveryManager`
- `FailsafeManager`
- `DeathManager`
- `InventoryTracker`
- `ScoreboardManager`
- `MessageManager`

One global mutable state container:
- `GameState`

---

## 📦 Installation

Server-side only.  
Requires:
- Fabric Loader
- Fabric API

Drop the JAR into your server's `mods` folder.

---

## 📝 License

See `LICENSE`.

---
