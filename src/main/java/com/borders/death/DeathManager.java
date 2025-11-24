package com.borders.death;

import com.borders.BordersMod;
import com.borders.border.BorderManager;
import com.borders.messages.MessageManager;
import com.borders.scoreboard.ScoreboardManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;

/**
 * Handles the v1.3 mechanic:
 *  - Whenever a player dies (and the feature is enabled),
 *    the world border shrinks by a configurable amount.
 *
 *  Default:
 *    - deathShrinkEnabled = true
 *    - deathShrinkBlocksPerSide = 5 (10 diameter)
 *
 *  Minimum border size is always clamped to 16.0.
 */
public class DeathManager {

    /**
     * Registers the AFTER_DEATH event handler.
     * Should be called once from BordersMod.onInitialize().
     */
    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if (!(livingEntity instanceof ServerPlayer player)) {
                return;
            }
            handlePlayerDeath(player);
        });
    }

    /**
     * Called whenever a ServerPlayer dies.
     */
    private static void handlePlayerDeath(ServerPlayer player) {
        // Basic feature gating
        if (!BordersMod.STATE.BORDER_INITIALIZED) return;
        if (!BordersMod.STATE.gameActive) return;
        if (!BordersMod.STATE.deathShrinkEnabled) return;

        int perSide = BordersMod.STATE.deathShrinkBlocksPerSide;
        if (perSide <= 0) {
            // Misconfigured; do nothing
            return;
        }

        MinecraftServer server = player.level().getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }

        double diameterDelta = perSide * 2.0;

        // Apply shrink on the overworld border first
        BordersMod.STATE.currentBorderSize = BorderManager.changeBorderSize(
                overworld,
                BordersMod.STATE.currentBorderSize,
                -diameterDelta
        );

        // Clamp minimum size
        if (BordersMod.STATE.currentBorderSize < 16.0) {
            BordersMod.STATE.currentBorderSize = 16.0;
            overworld.getWorldBorder().setSize(BordersMod.STATE.currentBorderSize);
        }

        // Sync all dimensions to the new (possibly clamped) size
        BorderManager.applyBorderToAllDimensions(server);

        // Update border size in sidebar scoreboard
        ScoreboardManager.updateBorderSizeLine(server);

        // Build and broadcast a fun death-shrink message
        String playerName = player.getName().getString();
        Component[] lines = MessageManager.buildDeathShrinkMessages(playerName);

        PlayerList playerList = server.getPlayerList();
        for (Component line : lines) {
            playerList.broadcastSystemMessage(line, false);
        }

        BordersMod.LOGGER.info(
                "Death shrink triggered by {}: border shrunk by {} per side ({} diameter). New size: {}",
                playerName,
                perSide,
                diameterDelta,
                BordersMod.STATE.currentBorderSize
        );
    }
}
