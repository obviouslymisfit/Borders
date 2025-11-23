package com.borders.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for detecting when a player's inventory count for any item increases.
 *
 * BordersMod stores a snapshot of each player's inventory each tick.
 * This class compares the previous tick's snapshot to the current one
 * and returns only the items that increased in quantity.
 *
 * Used by BordersMod to detect "newly obtained" items.
 */
public class InventoryTracker {

    /**
     * Detects which items increased in count between the previous and current snapshot.
     *
     * @param previous Map of item → count from the previous tick (may be null)
     * @param current  Map of item → count from the current tick
     * @return A map of items that increased in count, with the amount increased.
     */
    public static Map<Item, Integer> detectIncreasedItems(
            Map<Item, Integer> previous,
            Map<Item, Integer> current
    ) {
        // No previous snapshot means we can't detect increases
        if (previous == null) {
            return Map.of(); // empty immutable map
        }

        Map<Item, Integer> increased = new HashMap<>();

        // Compare old vs new counts
        for (Map.Entry<Item, Integer> entry : current.entrySet()) {
            Item item = entry.getKey();
            int currentCount = entry.getValue();
            int previousCount = previous.getOrDefault(item, 0);

            // Detect increase
            if (currentCount > previousCount) {
                increased.put(item, currentCount - previousCount);
            }
        }

        return increased;
    }

    /**
     * Returns a Map of Item → total count in the player's inventory.
     * Counts all inventory slots (main, armor, offhand).
     */
    public static Map<Item, Integer> countPlayerInventory(ServerPlayer player) {
        Map<Item, Integer> counts = new HashMap<>();

        Inventory inv = player.getInventory();
        int size = inv.getContainerSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = inv.getItem(slot);
            if (stack.isEmpty()) continue;

            counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }

        return counts;
    }

}
