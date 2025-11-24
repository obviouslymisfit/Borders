package com.borders.failsafe;

import com.borders.BordersMod;
import com.borders.border.BorderManager;
import com.borders.messages.MessageManager;
import com.borders.scoreboard.ScoreboardManager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;

/**
 * Handles the automatic border expansion failsafe.
 *
 * If the game is active and players have not discovered any new items
 * for a configured number of ticks, the border expands automatically.
 *
 * The amount expanded matches the configured discovery growth:
 *  - discoveryGrowthBlocksPerSide blocks on EACH side
 *  - i.e. (discoveryGrowthBlocksPerSide * 2) total diameter
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
     *  5. If all conditions match → expand border automatically and announce it.
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

        // Get the overworld level (primary border world)
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }

        // Expand the border using the same per-side growth as item discoveries
        int perSide = BordersMod.STATE.discoveryGrowthBlocksPerSide;
        double diameterDelta = perSide * 2.0;

        BordersMod.STATE.currentBorderSize = BorderManager.changeBorderSize(
                overworld,
                BordersMod.STATE.currentBorderSize,
                diameterDelta
        );

        // Update timer baseline so it doesn't immediately re-trigger
        BordersMod.STATE.lastDiscoveryTick = BordersMod.STATE.globalTick;

        // Log for debugging
        BordersMod.LOGGER.info(
                "Failsafe: no new items for {} ticks, border expanded by {} per side ({} diameter) to size {}",
                BordersMod.STATE.borderFailsafeDelayTicks,
                perSide,
                diameterDelta,
                BordersMod.STATE.currentBorderSize
        );

        // Reflect the new border size in the sidebar, if active
        ScoreboardManager.updateBorderSizeLine(server);

        // Broadcast a styled message to all players
        PlayerList playerList = server.getPlayerList();
        Component[] lines = MessageManager.buildFailsafeExpansionMessages();

        for (Component line : lines) {
            playerList.broadcastSystemMessage(line, false);
        }
    }
}
