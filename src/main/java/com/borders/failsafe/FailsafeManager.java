package com.borders.failsafe;

import com.borders.BordersMod;
import com.borders.border.BorderManager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Handles the automatic border expansion failsafe.
 *
 * If the game is active and players have not discovered any new items
 * for a configured number of ticks, the border expands automatically
 * (same amount as a normal discovery event: +2.0 size).
 */
public class FailsafeManager {

    /**
     * Called once per server tick by BordersMod.
     *
     * Logic:
     *  1. Check if the border is initialized.
     *  2. Check if the game is active.
     *  3. Check if the failsafe is enabled.
     *  4. Check if enough time passed since the last discovery.
     *  5. If all conditions match → expand border automatically.
     */
    public static void tick(MinecraftServer server) {

        // Check our activation conditions
        boolean shouldTrigger =
                BordersMod.STATE.BORDER_INITIALIZED &&
                        BordersMod.STATE.gameActive &&
                        BordersMod.STATE.failsafeEnabled &&
                        (BordersMod.STATE.globalTick - BordersMod.STATE.lastDiscoveryTick
                                >= BordersMod.STATE.borderFailsafeDelayTicks);

        if (!shouldTrigger) {
            return; // Failsafe not ready → skip
        }

        // Get the overworld level (border only exists meaningfully there)
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }

        // Expand the border by 2.0 (same logic as discoveries)
        BordersMod.STATE.currentBorderSize = BorderManager.changeBorderSize(
                overworld,
                BordersMod.STATE.currentBorderSize,
                2.0
        );

        // Update timer baseline
        BordersMod.STATE.lastDiscoveryTick = BordersMod.STATE.globalTick;

        // Log for debugging
        BordersMod.LOGGER.info(
                "Failsafe: no new items for {} ticks, border expanded to size {}",
                BordersMod.STATE.borderFailsafeDelayTicks,
                BordersMod.STATE.currentBorderSize
        );
    }
}
