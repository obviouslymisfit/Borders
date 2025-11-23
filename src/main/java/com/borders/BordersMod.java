package com.borders;

import com.borders.border.BorderManager;
import com.borders.commands.CommandManager;
import com.borders.discovery.DiscoveryManager;
import com.borders.failsafe.FailsafeManager;
import com.borders.inventory.InventoryTracker;
import com.borders.state.GameState;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.border.WorldBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Main mod entrypoint.
 *
 * Responsibilities:
 *  - Register tick handlers
 *  - Register join events
 *  - Register commands
 *  - Wire the managers together
 *
 * NOTE: All game state lives in GameState (STATE).
 *       All logic lives in manager classes.
 *       BordersMod now only provides wiring between systems.
 */
public class BordersMod implements ModInitializer {

	/** Shared global mutable state for the mod (border, timers, items, flags, etc). */
	public static final GameState STATE = new GameState();

	public static final String MOD_ID = "borders";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Used for randomized message suffix selection in MessageManager. */
	public static final Random RANDOM = new Random();

	/** (Currently unused) Intended for throttling inventory scans (e.g. every 20 ticks). */
	public static int tickCounter = 0;

	@Override
	public void onInitialize() {
		LOGGER.info("Borders mod initializing...");

		// Per-tick handler for main game loop logic
		ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);

		// On player join:
		//  - Initialize world border if first player joined
		//  - Otherwise clamp player inside the existing border
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			ServerLevel level = (ServerLevel) player.level();
			WorldBorder border = level.getWorldBorder(); // required for access, not used directly

			if (!STATE.BORDER_INITIALIZED) {
				BorderManager.initializeBorderOnFirstJoin(level, player);
				STATE.lastDiscoveryTick = STATE.globalTick;
			} else {
				BorderManager.clampPlayerInsideBorder(level, player);
			}
		});

		// Register /borders commands (start, stop, grow, shrink, reset, settimer)
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandManager.register(dispatcher);
		});
	}

	/**
	 * Main server tick loop.
	 *
	 * Responsibilities:
	 *  - Increment global tick counter
	 *  - Scan each player's inventory
	 *  - Detect newly obtained items
	 *  - Pass newly discovered items into DiscoveryManager
	 *  - Update LAST_INVENTORIES
	 *  - Run failsafe logic each tick
	 */
	public void onEndServerTick(MinecraftServer server) {
		// Advance global timer
		STATE.globalTick++;

		PlayerList playerList = server.getPlayerList();
		List<ServerPlayer> players = playerList.getPlayers();

		for (ServerPlayer player : players) {
			UUID uuid = player.getUUID();

			// Previous + current inventory snapshots
			Map<Item, Integer> previous = STATE.LAST_INVENTORIES.get(uuid);
			Map<Item, Integer> current = InventoryTracker.countPlayerInventory(player);

			// Detect items that increased in count
			Map<Item, Integer> increasedItems = InventoryTracker.detectIncreasedItems(previous, current);

			// Handle newly discovered items
			for (Item item : increasedItems.keySet()) {
				if (STATE.gameActive && !STATE.OBTAINED_ITEMS.contains(item)) {
					STATE.OBTAINED_ITEMS.add(item);
					DiscoveryManager.handleItemDiscovery(server, player, item);
				}
			}

			// Store latest snapshot for next tick
			STATE.LAST_INVENTORIES.put(uuid, current);
		}

		// Failsafe logic (idle expansion)
		FailsafeManager.tick(server);
	}
}
