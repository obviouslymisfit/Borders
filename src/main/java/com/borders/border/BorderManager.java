package com.borders.border;

import com.borders.BordersMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;

/**
 * Handles all world-border-related behavior:
 *  - Initial border creation on first join
 *  - Manual border growth/shrink (via commands or discovery)
 *  - Clamping players inside the current border
 *  - Resetting border to original spawn chunk
 *  - Syncing border state across Overworld, Nether and End
 *
 * This class contains NO global state — it only mutates GameState
 * through BordersMod.STATE and operates on the world border instance.
 */
public class BorderManager {

    /**
     * Adjusts the border size by a delta in a single level.
     *
     * @param level       The world (server level)
     * @param currentSize Current border diameter
     * @param delta       Positive or negative change (diameter)
     * @return The updated border size
     */
    public static double changeBorderSize(ServerLevel level, double currentSize, double delta) {
        WorldBorder border = level.getWorldBorder();
        double newSize = currentSize + delta;
        border.setSize(newSize);
        return newSize;
    }

    /**
     * Applies the current border configuration (center + size) from GameState
     * to all main dimensions:
     *
     *  - Overworld
     *  - Nether
     *  - End
     *
     * All three share the same:
     *  - centerX / centerZ
     *  - diameter (currentBorderSize)
     *
     * Nether is NOT scaled by 8x – border size is identical there.
     */
    public static void applyBorderToAllDimensions(MinecraftServer server) {
        double size = BordersMod.STATE.currentBorderSize;
        double centerX = BordersMod.STATE.borderCenterX;
        double centerZ = BordersMod.STATE.borderCenterZ;

        syncBorderForLevel(server.getLevel(Level.OVERWORLD), size, centerX, centerZ);
        syncBorderForLevel(server.getLevel(Level.NETHER), size, centerX, centerZ);
        syncBorderForLevel(server.getLevel(Level.END), size, centerX, centerZ);
    }

    private static void syncBorderForLevel(ServerLevel level, double size, double centerX, double centerZ) {
        if (level == null) return;

        WorldBorder border = level.getWorldBorder();
        border.setCenter(centerX, centerZ);
        border.setSize(size);
    }

    /**
     * Called only once: when the very first player joins the server.
     * This initializes:
     *  - GameState border flags
     *  - The spawn position for resets
     *  - Border center position
     *  - Starting border size (16x16)
     *  - Teleports first player into the center of their chunk
     *
     * It also applies the same border to Nether and End.
     */
    public static void initializeBorderOnFirstJoin(ServerLevel level, ServerPlayer player) {
        BordersMod.STATE.BORDER_INITIALIZED = true;

        // First player’s position becomes the "spawn" for our custom border logic
        BlockPos spawnPos = player.blockPosition();
        BordersMod.STATE.initialSpawnPos = spawnPos;

        // Determine chunk center from spawn position
        ChunkPos chunkPos = new ChunkPos(spawnPos);
        BordersMod.STATE.borderCenterX = chunkPos.getMiddleBlockX();
        BordersMod.STATE.borderCenterZ = chunkPos.getMiddleBlockZ();

        // Starting border size
        BordersMod.STATE.currentBorderSize = 16.0;

        // Apply border center + starting border size to all dimensions
        applyBorderToAllDimensions(level.getServer());

        // Log for debugging
        BordersMod.LOGGER.info(
                "World border initialized around spawn chunk ({}, {}) at center ({}, {}).",
                chunkPos.x, chunkPos.z,
                BordersMod.STATE.borderCenterX, BordersMod.STATE.borderCenterZ
        );

        // Ensure the first player stands inside the center of their chunk
        player.teleportTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5
        );
    }

    /**
     * Used when a player joins after the border is already initialized.
     * If they join outside the border, they are teleported back to the spawn center.
     *
     * This logic is dimension-agnostic; it uses the cached center and size
     * from GameState and always teleports to the stored spawn position
     * (which is in the Overworld).
     */
    public static void clampPlayerInsideBorder(ServerLevel level, ServerPlayer player) {
        if (BordersMod.STATE.initialSpawnPos == null) return;

        double half = BordersMod.STATE.currentBorderSize / 2.0;
        double px = player.getX();
        double pz = player.getZ();

        double centerX = BordersMod.STATE.borderCenterX;
        double centerZ = BordersMod.STATE.borderCenterZ;

        // Check if position is outside our manually tracked border
        boolean outside =
                px < centerX - half || px > centerX + half ||
                        pz < centerZ - half || pz > centerZ + half;

        if (outside) {
            BlockPos spawn = BordersMod.STATE.initialSpawnPos;

            player.teleportTo(
                    spawn.getX() + 0.5,
                    spawn.getY(),
                    spawn.getZ() + 0.5
            );
        }
    }

    /**
     * Resets the world border to its original state:
     *  - Recenters at the initial spawn-position chunk
     *  - Returns size to 16x16
     *
     * Called by /borders reset.
     * Border is re-applied to Overworld, Nether, and End.
     */
    public static void resetBorder(ServerLevel level) {
        // Recalculate chunk center from stored spawn position
        if (BordersMod.STATE.initialSpawnPos != null) {
            ChunkPos chunkPos = new ChunkPos(BordersMod.STATE.initialSpawnPos);
            BordersMod.STATE.borderCenterX = chunkPos.getMiddleBlockX();
            BordersMod.STATE.borderCenterZ = chunkPos.getMiddleBlockZ();
        }

        // Reset size in state
        BordersMod.STATE.currentBorderSize = 16.0;

        // Apply reset parameters to all dimensions
        applyBorderToAllDimensions(level.getServer());
    }
}
