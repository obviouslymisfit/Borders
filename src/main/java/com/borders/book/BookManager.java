package com.borders.book;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.core.component.DataComponents;

import com.borders.BordersMod;
import com.borders.messages.MessageManager;
import com.borders.state.GameState;


/**
 * Handles creation and delivery of the Border Control Book.
 *
 * v2.0 goal:
 *  - Build a dynamically generated written book for admins
 *  - Book contains clickable configuration entries for the Borders mod
 *
 * For now this class only:
 *  - Checks permissions
 *  - Creates a basic placeholder written book
 *  - Gives it to the player (or drops it if inventory is full)
 */
public class BookManager {

    /** Display name of the book shown to the player. */
    public static final String BOOK_TITLE = "Border Control Book";

    /** Author name shown in the book UI. (Conceptual, we'll wire it later.) */
    public static final String BOOK_AUTHOR = "The Overseer";

    /**
     * Gives the Border Control Book to the given player,
     * if they have sufficient permissions.
     *
     * Non-OP players get a friendly rejection message instead.
     */
    public static void giveAdminBook(ServerPlayer player) {
        // Require permission level 2 (same as /borders commands)
        if (!player.hasPermissions(2)) {
            player.sendSystemMessage(
                    Component.literal("Nice try, adventurer.")
            );
            return;
        }

        ItemStack book = buildControlBook();

        Inventory inv = player.getInventory();

        // Try to add to inventory; if full, drop at feet
        if (!inv.add(book)) {
            player.drop(book, false);
        }
    }

    /**
     * Opens the Borders configuration UI for the given player.
     *
     * For v2.1 this shows a chat-based configuration panel.
     * Later we will move the same logic into the book pages themselves.
     */
    public static void openConfigPanel(ServerPlayer player) {
        // Require permission level 2 (same as /borders commands)
        if (!player.hasPermissions(2)) {
            player.sendSystemMessage(
                    Component.literal("Nice try, adventurer.")
            );
            return;
        }

        GameState state = BordersMod.STATE;
        if (state == null) {
            player.sendSystemMessage(
                    Component.literal("[Borders] No game state available.")
            );
            return;
        }

        Component[] lines = MessageManager.buildConfigPanel(state);
        for (Component line : lines) {
            player.sendSystemMessage(line);
        }
    }



    /**
     * Builds the Border Control Book item.
     *
     * For now this is a minimal written book with:
     *  - Custom hover name (title)
     *  - No pages yet (we'll add them in the next steps)
     */
    private static ItemStack buildControlBook() {
        ItemStack stack = new ItemStack(Items.WRITABLE_BOOK);

        // This is the correct way to set a custom item name in Mojang mappings 1.20.5+ / 1.21+
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(BOOK_TITLE));

        return stack;
    }


}
