package com.borders.commands;

import com.borders.BordersMod;
import com.borders.border.BorderManager;
import com.borders.messages.MessageManager;
import com.borders.scoreboard.ScoreboardManager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;

/**
 * Registers and defines all /borders commands.
 * This class contains only command logic. Actual behavior is delegated to:
 *  - BorderManager (border manipulation)
 *  - ScoreboardManager (scores)
 *  - GameState (shared mod state)
 */
public class CommandManager {

    /**
     * Registers the root command `/borders` and all its subcommands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("borders")
                        .requires(source -> source.hasPermission(2))  // OP-only

                        // ------------------------------------------------------------
                        // /borders start
                        // ------------------------------------------------------------
                        .then(Commands.literal("start").executes(ctx -> {
                            BordersMod.STATE.gameActive = true;
                            BordersMod.STATE.failsafeEnabled = true;
                            BordersMod.STATE.lastDiscoveryTick = BordersMod.STATE.globalTick;

                            // Ensure scoreboard objective exists and is shown
                            MinecraftServer server = ctx.getSource().getServer();
                            ScoreboardManager.getOrCreateDiscoveryObjective(server);
                            ScoreboardManager.updateBorderSizeLine(server);

                            ctx.getSource().sendSystemMessage(
                                    Component.literal("[Borders] Game started.")
                            );
                            return 1;
                        }))

                        // ------------------------------------------------------------
                        // /borders stop
                        // ------------------------------------------------------------
                        .then(Commands.literal("stop").executes(ctx -> {
                            BordersMod.STATE.gameActive = false;
                            BordersMod.STATE.failsafeEnabled = false;

                            ctx.getSource().sendSystemMessage(
                                    Component.literal("[Borders] Game stopped.")
                            );
                            return 1;
                        }))

                        // ------------------------------------------------------------
                        // /borders settimer <seconds>
                        // ------------------------------------------------------------
                        .then(Commands.literal("settimer")
                                .then(Commands.argument("seconds", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            int seconds = IntegerArgumentType.getInteger(ctx, "seconds");

                                            BordersMod.STATE.borderFailsafeDelayTicks = seconds * 20L;
                                            BordersMod.STATE.lastDiscoveryTick = BordersMod.STATE.globalTick;

                                            ctx.getSource().sendSystemMessage(
                                                    Component.literal("[Borders] Inactivity expansion timer set to "
                                                            + seconds + " seconds.")
                                            );
                                            return 1;
                                        })
                                )
                        )

                        // ------------------------------------------------------------
                        // /borders setgrowth <blocksPerSide>
                        // ------------------------------------------------------------
                        .then(Commands.literal("setgrowth")
                                .then(Commands.argument("blocksPerSide", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int perSide = IntegerArgumentType.getInteger(ctx, "blocksPerSide");

                                            BordersMod.STATE.discoveryGrowthBlocksPerSide = perSide;

                                            int diameter = perSide * 2;

                                            ctx.getSource().sendSystemMessage(
                                                    Component.literal("[Borders] Discovery growth set to ")
                                                            .append(Component.literal(String.valueOf(perSide))
                                                                    .withStyle(style -> style.withColor(0xFFD700)))
                                                            .append(Component.literal(" blocks per side ("))
                                                            .append(Component.literal(String.valueOf(diameter))
                                                                    .withStyle(style -> style.withColor(0xFFD700)))
                                                            .append(Component.literal(" diameter per discovery)."))
                                            );
                                            return 1;
                                        })
                                )
                        )

                        // ------------------------------------------------------------
                        // /borders grow <blocks>
                        // ------------------------------------------------------------
                        .then(Commands.literal("grow")
                                .then(Commands.argument("blocks", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int blocks = IntegerArgumentType.getInteger(ctx, "blocks");
                                            MinecraftServer server = ctx.getSource().getServer();

                                            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                                            if (overworld == null) {
                                                ctx.getSource().sendSystemMessage(
                                                        Component.literal("[Borders] Overworld not available.")
                                                );
                                                return 0;
                                            }

                                            double delta = blocks * 2.0;

                                            BordersMod.STATE.currentBorderSize =
                                                    BorderManager.changeBorderSize(
                                                            overworld,
                                                            BordersMod.STATE.currentBorderSize,
                                                            delta
                                                    );

                                            // Sync all dimensions to the new size
                                            BorderManager.applyBorderToAllDimensions(server);

                                            // Update border size in scoreboard
                                            ScoreboardManager.updateBorderSizeLine(server);

                                            ctx.getSource().sendSystemMessage(
                                                    Component.literal("[Borders] Border grown by " + blocks
                                                            + " blocks each side. New size: "
                                                            + BordersMod.STATE.currentBorderSize)
                                            );
                                            return 1;
                                        })
                                )
                        )

                        // ------------------------------------------------------------
                        // /borders shrink <blocks>
                        // ------------------------------------------------------------
                        .then(Commands.literal("shrink")
                                .then(Commands.argument("blocks", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int blocks = IntegerArgumentType.getInteger(ctx, "blocks");
                                            MinecraftServer server = ctx.getSource().getServer();

                                            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                                            if (overworld == null) {
                                                ctx.getSource().sendSystemMessage(
                                                        Component.literal("[Borders] Overworld not available.")
                                                );
                                                return 0;
                                            }

                                            double delta = blocks * 2.0;

                                            BordersMod.STATE.currentBorderSize =
                                                    BorderManager.changeBorderSize(
                                                            overworld,
                                                            BordersMod.STATE.currentBorderSize,
                                                            -delta
                                                    );

                                            // Clamp minimum border size to vanilla starting size
                                            if (BordersMod.STATE.currentBorderSize < 16.0) {
                                                BordersMod.STATE.currentBorderSize = 16.0;
                                                overworld.getWorldBorder()
                                                        .setSize(BordersMod.STATE.currentBorderSize);
                                            }

                                            // Sync all dimensions to the new (possibly clamped) size
                                            BorderManager.applyBorderToAllDimensions(server);

                                            // Update border size in scoreboard
                                            ScoreboardManager.updateBorderSizeLine(server);

                                            ctx.getSource().sendSystemMessage(
                                                    Component.literal("[Borders] Border shrunk by " + blocks
                                                            + " blocks each side. New size: "
                                                            + BordersMod.STATE.currentBorderSize)
                                            );
                                            return 1;
                                        })
                                )
                        )

                        // ------------------------------------------------------------
                        // /borders reset
                        // ------------------------------------------------------------
                        .then(Commands.literal("reset").executes(ctx -> {
                            MinecraftServer server = ctx.getSource().getServer();

                            // Reset scoreboard
                            ScoreboardManager.resetDiscoveryScores(server);

                            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                            if (overworld == null) {
                                ctx.getSource().sendSystemMessage(
                                        Component.literal("[Borders] Overworld not available.")
                                );
                                return 0;
                            }

                            PlayerList playerList = server.getPlayerList();

                            // Clear all player inventories + teleport to spawn
                            for (ServerPlayer player : playerList.getPlayers()) {

                                // Clear inventory
                                Inventory inv = player.getInventory();
                                int size = inv.getContainerSize();
                                for (int slot = 0; slot < size; slot++) {
                                    inv.setItem(slot, ItemStack.EMPTY);
                                }

                                // Teleport to spawn if known
                                if (BordersMod.STATE.initialSpawnPos != null) {
                                    player.teleportTo(
                                            overworld,
                                            BordersMod.STATE.initialSpawnPos.getX() + 0.5,
                                            BordersMod.STATE.initialSpawnPos.getY(),
                                            BordersMod.STATE.initialSpawnPos.getZ() + 0.5,
                                            Collections.emptySet(),  // no relative flags
                                            player.getYRot(),
                                            player.getXRot(),
                                            false
                                    );
                                }
                            }

                            // Reset border (Overworld + sync to Nether/End inside)
                            BorderManager.resetBorder(overworld);

                            // Reset game state
                            BordersMod.STATE.OBTAINED_ITEMS.clear();
                            BordersMod.STATE.LAST_INVENTORIES.clear();
                            BordersMod.STATE.globalTick = 0L;
                            BordersMod.STATE.lastDiscoveryTick = 0L;
                            BordersMod.STATE.gameActive = false;
                            BordersMod.STATE.failsafeEnabled = false;

                            ctx.getSource().sendSystemMessage(
                                    Component.literal("[Borders] Game reset: border, items, and inventories cleared.")
                            );

                            return 1;
                        }))

                        // ------------------------------------------------------------
                        // /borders info
                        // ------------------------------------------------------------
                        .then(Commands.literal("info").executes(ctx -> {
                            MinecraftServer server = ctx.getSource().getServer();
                            ServerLevel overworld = server.getLevel(Level.OVERWORLD);

                            if (overworld == null) {
                                ctx.getSource().sendSystemMessage(
                                        Component.literal("[Borders] Overworld not available.")
                                );
                                return 0;
                            }

                            boolean gameActive = BordersMod.STATE.gameActive;
                            boolean failsafeEnabled = BordersMod.STATE.failsafeEnabled;

                            double borderSize = BordersMod.STATE.currentBorderSize;
                            double centerX = overworld.getWorldBorder().getCenterX();
                            double centerZ = overworld.getWorldBorder().getCenterZ();

                            long globalTick = BordersMod.STATE.globalTick;
                            long lastDiscoveryTick = BordersMod.STATE.lastDiscoveryTick;
                            long ticksSinceLastDiscovery = Math.max(0L, globalTick - lastDiscoveryTick);
                            long secondsSinceLastDiscovery = ticksSinceLastDiscovery / 20L;

                            long failsafeDelayTicks = BordersMod.STATE.borderFailsafeDelayTicks;
                            long failsafeDelaySeconds = failsafeDelayTicks / 20L;

                            int discoveredCount = BordersMod.STATE.OBTAINED_ITEMS.size();
                            int growthBlocksPerSide = BordersMod.STATE.discoveryGrowthBlocksPerSide;

                            Component[] lines = MessageManager.buildInfoMessages(
                                    gameActive,
                                    failsafeEnabled,
                                    borderSize,
                                    centerX,
                                    centerZ,
                                    discoveredCount,
                                    growthBlocksPerSide,
                                    failsafeDelaySeconds,
                                    secondsSinceLastDiscovery
                            );

                            for (Component line : lines) {
                                ctx.getSource().sendSystemMessage(line);
                            }

                            return 1;
                        }))
        );
    }
}
