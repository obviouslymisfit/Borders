# Changelog
All notable changes to this project will be documented in this file.
Dates intentionally omitted.

---

## [2.1]
### Added
- **Borders Configuration Book** (`/borders book`):
  - Fully interactive written book UI for server operators.
  - Section-based navigation with clickable page jumps.
  - Dedicated pages for:
    - Game Control
    - Discovery expansion settings
    - Death-shrink settings
    - Inactivity timer configuration
    - Manual border grow/shrink tools
    - Info & utility commands
  - Clickable buttons for:
    - `/borders start`, `/borders stop`, `/borders reset`, `/borders reload`
    - `/borders setgrowth`, `/borders setdeathshrink`, `/borders settimer`
    - `/borders grow`, `/borders shrink`
    - `/borders info`, `/borders help`
  - “Back to Home” navigation links on all subpages.
- Greatly expanded broadcast message pools for:
  - Item discovery
  - Inactivity expansions
  - Player death shrink events

### Changed
- BookManager now builds a fully resolved written book using the
  `WRITTEN_BOOK_CONTENT` data component.
- Page layout, colors, and formatting applied consistently across all book pages.
- MessageManager updated with expanded flavor text for broadcasts.

---


## [1.4]
### Added
- Full `/borders help` command with categorized sections:
  - Core commands
  - Configuration commands
  - Manual border control
- Colored, formatted help panel with gold command labels and gray descriptions.

### Changed
- Improved scoreboard formatting and help/info message structures.
- Ensured scoreboard border-size line updates reliably during reload/grow/shrink operations.
- Improved command grouping and documentation clarity.

---

## [1.3]
### Added
- Player-death shrink mechanic:
  - Border shrinks by configurable amount per side when any player dies.
  - Snarky multi-line death messages with randomized flavor text.
- New state fields:
  - `deathShrinkEnabled`
  - `deathShrinkBlocksPerSide`
- New commands:
  - `/borders setdeathshrink <blocksPerSide>`
  - `/borders toggledeathshrink`
- New `DeathManager` responsible for detecting player deaths and applying shrink logic.

### Changed
- `/borders info` updated to include:
  - Death-shrink enabled/disabled indicator
  - Death-shrink blocks per side
  - New formatting and color-coding improvements

### Fixed
- Scoreboard formatting issues where info lines rendered too close to the header.
- Ensured scoreboard maintains consistent ordering of title → border size → player scores.

---

## [1.2]
### Added
- Nether and End border synchronization:
  - Both dimensions mirror Overworld border diameter.
  - Centers based on Overworld spawn initialization logic.
- Configurable discovery growth:
  - `/borders setgrowth <blocksPerSide>`
  - Replaces hardcoded +1-per-side expansion.
- Major expansion to `/borders info`:
  - Full status block (active/inactive)
  - Border size, center, discovered item count
  - Growth settings
  - Death-shrink settings (added later)
  - Inactivity timer status
- Inactivity expansion flavor message system:
  - Multi-line themed messages when failsafe triggers.
- Updated scoreboard sidebar:
  - Added decorative separator
  - Added “Border size:” informational line
  - Color improvements

### Changed
- DiscoveryManager expansion uses new configurable growth amount.
- BorderManager updated to apply changes to all dimensions.
- Improved logging and debugging output.
- Refactored inactivity logic into cleaner fail-safe system.

### Fixed
- First-join border initialization stability.
- Failsafe timer resetting correctly on new discoveries.
- Rare edge cases with inventory scanning during item detection.

---

## [1.1]
### Refactor (complete rewrite)
- Introduced modular architecture:
  - `BorderManager`
  - `CommandManager`
  - `DiscoveryManager`
  - `FailsafeManager`
  - `InventoryTracker`
  - `ScoreboardManager`
  - `MessageManager`
- Added unified `GameState` container for all mutable mod state:
  - Border size & position
  - Spawn reference
  - Discovered items set
  - Inventory snapshots
  - Global tick timers
  - Inactivity timer configuration
- Fully separated concerns across modules.
- Cleaned and simplified main `BordersMod.java` entrypoint.

