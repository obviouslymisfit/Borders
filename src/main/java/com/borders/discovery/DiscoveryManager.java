package com.borders.discovery;

import com.borders.BordersMod;
import com.borders.border.BorderManager;
import com.borders.messages.MessageManager;
import com.borders.scoreboard.ScoreboardManager;
import com.borders.state.GameState;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;

/**
 * Handles what happens when a player discovers a new item:
 *  - Builds and broadcasts a discovery message
 *  - Updates the scoreboard objective
 *  - Updates discovery timer
 *  - Expands the border if the game is active
 */
public class DiscoveryManager {

    /**
     * Called when a newly obtained item is detected for the first time.
     *
     * @param server The current Minecraft server instance
     * @param player The player who discovered the item
     * @param item   The item that was newly obtained
     */
    public static void handleItemDiscovery(MinecraftServer server, ServerPlayer player, Item item) {
        GameState state = BordersMod.STATE;

        // ─────────────────────────────────────────────
        // Episode 2 "backlog" handling:
        // Ignore the first N unique discoveries for growth & scores,
        // BUT still record items in OBTAINED_ITEMS so they can
        // never trigger growth later.
        // ─────────────────────────────────────────────
        if (state.ignoredDiscoveries > 0) {
            state.ignoredDiscoveries--;

            // VERY IMPORTANT: still mark this item as obtained.
            state.OBTAINED_ITEMS.add(item);

            // Optional: log for debugging
            BordersMod.LOGGER.info(
                    "[Borders] Ignored discovery for backlog ({} remaining). Item={}",
                    state.ignoredDiscoveries,
                    item.toString()
            );

            // Do NOT:
            //  - grow border
            //  - increase scoreboard
            //  - send "border grew" message
            // Just exit.
            return;
        }

        // Build item & player names for messaging
        String itemName = new ItemStack(item).getHoverName().getString();
        String playerName = player.getName().getString();

        // Build the formatted discovery message via MessageManager
        Component message = MessageManager.buildDiscoveryMessage(playerName, itemName);

        // Broadcast the discovery message to all players
        PlayerList playerList = server.getPlayerList();
        playerList.broadcastSystemMessage(message, false);

        // --------------------------------------------------------------------
        // Update leaderboard score
        // --------------------------------------------------------------------

        Objective objective = ScoreboardManager.getOrCreateDiscoveryObjective(server);

        // In 1.21.x Mojang mappings, ServerPlayer implements ScoreHolder,
        // and getOrCreatePlayerScore returns a ScoreAccess.
        ScoreAccess score = server.getScoreboard().getOrCreatePlayerScore(player, objective);

        // Increase score by 1
        score.add(1);

        // --------------------------------------------------------------------
        // Discovery timing / failsafe baseline
        // --------------------------------------------------------------------

        // Reset the discovery timer so failsafe doesn't trigger while items are being found
        BordersMod.STATE.lastDiscoveryTick = BordersMod.STATE.globalTick;

        // --------------------------------------------------------------------
        // Border expansion
        // --------------------------------------------------------------------

        if (BordersMod.STATE.BORDER_INITIALIZED) {
            ServerLevel level = (ServerLevel) player.level();

            // Convert "blocks per side" into a diameter change for the world border
            int perSide = BordersMod.STATE.discoveryGrowthBlocksPerSide;
            double diameterDelta = perSide * 2.0;

            BordersMod.STATE.currentBorderSize = BorderManager.changeBorderSize(
                    level,
                    BordersMod.STATE.currentBorderSize,
                    diameterDelta
            );

            BordersMod.LOGGER.info(
                    "World border expanded by {} blocks per side ({} diameter). New size: {}",
                    perSide,
                    diameterDelta,
                    BordersMod.STATE.currentBorderSize
            );

            // Sync border size + center across Overworld, Nether, and End
            BorderManager.applyBorderToAllDimensions(server);

            // Reflect the new border size in the sidebar
            ScoreboardManager.updateBorderSizeLine(server);
        }
    }
}
