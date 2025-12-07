package com.borders.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Holder;


/**
 * Simple JSON-based persistence snapshot for Borders.
 *
 * This does NOT use Minecraft's SavedData / NBT APIs.
 * It just mirrors the important fields from GameState
 * and stores them in config/borders_state.json.
 */
public class BordersSavedData {

    // --------- Fields mirrored from GameState ---------

    /** Current border size (diameter, in blocks). */
    public double currentBorderSize = 16.0;

    /** Whether the game is active (discoveries expand the border). */
    public boolean gameActive = false;

    /** Whether the inactivity expansion (failsafe) is enabled. */
    public boolean failsafeEnabled = true;

    /** Delay (in ticks) before failsafe triggers. */
    public long borderFailsafeDelayTicks = 6000L;

    /** Whether the border shrinks when a player dies. */
    public boolean deathShrinkEnabled = true;

    /** How many blocks per side the border shrinks on death. */
    public int deathShrinkBlocksPerSide = 5;

    /** How many blocks per side the border grows per new item. */
    public int discoveryGrowthBlocksPerSide = 1;

    /** Whether the border has been initialized around the first spawn. */
    public boolean borderInitialized = false;

    /** Cached border center. */
    public double borderCenterX = 0.0;
    public double borderCenterZ = 0.0;

    /**
     * IDs of items that have already been discovered.
     * Stored as registry names, e.g. "minecraft:stone".
     */
    public Set<String> obtainedItemIds = new HashSet<>();

    // --------- JSON + file handling ---------

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final String FILE_NAME = "borders_state.json";

    private static Path getSavePath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve(FILE_NAME);
    }

    // --------- Mapping between GameState and this snapshot ---------

    public static BordersSavedData fromGameState(GameState state) {
        BordersSavedData data = new BordersSavedData();

        data.currentBorderSize = state.currentBorderSize;
        data.gameActive = state.gameActive;
        data.failsafeEnabled = state.failsafeEnabled;
        data.borderFailsafeDelayTicks = state.borderFailsafeDelayTicks;

        data.deathShrinkEnabled = state.deathShrinkEnabled;
        data.deathShrinkBlocksPerSide = state.deathShrinkBlocksPerSide;
        data.discoveryGrowthBlocksPerSide = state.discoveryGrowthBlocksPerSide;

        data.borderInitialized = state.BORDER_INITIALIZED;
        data.borderCenterX = state.borderCenterX;
        data.borderCenterZ = state.borderCenterZ;

        // Mirror discovered items as string IDs
        data.obtainedItemIds.clear();
        for (Item item : state.OBTAINED_ITEMS) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null) {
                data.obtainedItemIds.add(id.toString());
            }
        }

        return data;
    }

    public void applyToGameState(GameState state) {
        state.currentBorderSize = this.currentBorderSize;
        state.gameActive = this.gameActive;
        state.failsafeEnabled = this.failsafeEnabled;
        state.borderFailsafeDelayTicks = this.borderFailsafeDelayTicks;

        state.deathShrinkEnabled = this.deathShrinkEnabled;
        state.deathShrinkBlocksPerSide = this.deathShrinkBlocksPerSide;
        state.discoveryGrowthBlocksPerSide = this.discoveryGrowthBlocksPerSide;

        state.BORDER_INITIALIZED = this.borderInitialized;
        state.borderCenterX = this.borderCenterX;
        state.borderCenterZ = this.borderCenterZ;

        // Rebuild the discovered items set from stored IDs
        state.OBTAINED_ITEMS.clear();
        for (String idString : this.obtainedItemIds) {
            ResourceLocation id = ResourceLocation.tryParse(idString);
            if (id == null) {
                continue;
            }

            // 1.21.x: get() returns Optional<Holder.Reference<Item>>
            var optRef = BuiltInRegistries.ITEM.get(id);
            if (optRef.isPresent()) {
                Holder.Reference<Item> ref = optRef.get();
                Item item = ref.value();
                state.OBTAINED_ITEMS.add(item);
            }
        }

    }

    // --------- Disk I/O ---------

    public void saveToDisk() {
        Path path = getSavePath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            // If something goes wrong, log it, but don't crash the server.
            e.printStackTrace();
        }
    }

    public static BordersSavedData loadFromDisk() {
        Path path = getSavePath();
        if (!Files.exists(path)) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, BordersSavedData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
