package com.borders.state;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Central container for all mutable game state of the Borders mod.
 *
 * This is the single source of truth for:
 *  - Border configuration and size
 *  - Spawn / border center position
 *  - Game flags (active, failsafe enabled)
 *  - Global timing (ticks, last discovery tick)
 *  - Failsafe delay configuration
 *  - Discovered items and per-player inventory snapshots
 *
 * BordersMod and manager classes should always read/write state through this object.
 */
public class GameState {

    // ------------------------------------------------------------------------
    // World border state
    // ------------------------------------------------------------------------

    /** Current border size (diameter, in blocks). */
    public double currentBorderSize = 16.0;

    /** Whether the border has been initialized around the first player's chunk. */
    public boolean BORDER_INITIALIZED = false;

    /** Initial spawn position used for border centering and /borders reset. */
    public BlockPos initialSpawnPos = null;

    /** Cached border center X coordinate (used for joins and reset). */
    public double borderCenterX = 0.0;

    /** Cached border center Z coordinate (used for joins and reset). */
    public double borderCenterZ = 0.0;

    // ------------------------------------------------------------------------
    // Game flags and timing
    // ------------------------------------------------------------------------

    /** Whether the Borders game is currently active (item discoveries expand the border). */
    public boolean gameActive = false;

    /** Whether the failsafe (auto-expansion after inactivity) is enabled. */
    public boolean failsafeEnabled = true;

    /** Global tick counter (incremented every server tick). */
    public long globalTick = 0L;

    /** Tick value at which the last new item was discovered. */
    public long lastDiscoveryTick = 0L;

    /**
     * Delay (in ticks) before the failsafe triggers.
     * Default: 6000 ticks = 5 minutes at 20 TPS.
     * Can be changed via the /borders settimer command.
     */
    public long borderFailsafeDelayTicks = 6000L;

    // ------------------------------------------------------------------------
    // Item discovery and inventory tracking
    // ------------------------------------------------------------------------

    /**
     * Pool of items that have already been obtained at least once
     * during the current game session.
     */
    public final Set<Item> OBTAINED_ITEMS = new HashSet<>();

    /**
     * Per-player snapshot of their inventory from the last scan.
     * Used to detect which items increased between ticks.
     */
    public final Map<UUID, Map<Item, Integer>> LAST_INVENTORIES = new HashMap<>();
}
