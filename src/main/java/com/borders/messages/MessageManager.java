package com.borders.messages;

import com.borders.BordersMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

/**
 * Handles all chat message construction and formatting for the Borders mod.
 *
 * Right now this focuses on:
 *  - Randomized flavor text when a new item is discovered
 *  - Colored player name and item name formatting
 */
public class MessageManager {

    /**
     * Pool of suffix messages appended to the discovery message
     * when a new item is found.
     */
    public static final String[] GROW_MESSAGES = new String[] {
            "The world stretches a little farther...",
            "A new path opens to the brave...",
            "More land to explore!",
            "The horizon just pushed back!",
            "Another corner of the world lights up!",
            "Boom! More world for everyone!",
            "The borders blast outward!",
            "World just leveled up!",
            "Fresh land incoming!",
            "The map just got bigger—go!",
            "The border moved... it got bored of standing still.",
            "The world says: fine, have more.",
            "The border scoots over politely.",
            "The world just chonked up a size.",
            "New land claimed.",
            "Another item, another expansion!",
            "Border shifts! Keep going!",
            "You’re pushing the world to its limits!",
            "The world yields a little more ground.",
            "Another breakthrough—keep it rolling!",
            "The realm grows with your discovery.",
            "The world breathes in... and expands.",
            "The world is impressed and grows in admiration.",
            "The border backs away slowly...",
            "Chunk gods have granted expansion!",
            "Another item?! World expands in disbelief!",
            "The border just couldn't handle the hype.",
            "World: “oh crap, they found something — expand!!”",
            "The border ran away a bit.",
            "Another discovery? Border yeets outward.",
            "One more item and the world needs therapy."
    };

    /**
     * Builds the discovery message shown when a player finds a new item.
     *
     * Format:
     *   <playerName> found <itemName>! <random suffix>
     *
     * Colors:
     *   - Player name: cyan
     *   - Item name: gold
     *
     * @param playerName Name of the player who discovered the item
     * @param itemName   Display name of the item
     * @return A fully formatted Component that can be sent to chat
     */
    public static Component buildDiscoveryMessage(String playerName, String itemName) {
        // Pick a random suffix from the message pool
        String suffix = GROW_MESSAGES[
                BordersMod.RANDOM.nextInt(GROW_MESSAGES.length)
                ];

        // Build the colored message
        return Component.literal("")
                // Player name (cyan)
                .append(
                        Component.literal(playerName)
                                .withStyle(style -> style.withColor(TextColor.fromRgb(0x55FFFF)))
                )
                .append(Component.literal(" found "))
                // Item name (gold)
                .append(
                        Component.literal(itemName)
                                .withStyle(style -> style.withColor(TextColor.fromRgb(0xFFD700)))
                )
                .append(Component.literal("! " + suffix));
    }
}
