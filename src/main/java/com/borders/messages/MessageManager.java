package com.borders.messages;

import com.borders.BordersMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

/**
 * Handles all chat message construction and formatting for the Borders mod.
 *
 * Right now this focuses on:
 *  - Randomized flavor text when a new item is discovered
 *  - Failsafe (inactivity) border expansions
 *  - Info panels for /borders info
 *  - Death-triggered border shrink messages (v1.3)
 *  - Colored player/item formatting
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
     * Pool of messages used when the inactivity expansion triggers.
     */
    public static final String[] FAILSAFE_MESSAGES = new String[] {
            "Nobody found anything new, so the world got bored and stretched.",
            "The realm sighs and grows on its own.",
            "Silence... so the border moves just to feel something.",
            "No discoveries? Fine. The border expands out of pity.",
            "The world taps its foot, then scoots the border outward.",
            "Explorers slacking — the world grows anyway.",
            "Inactivity detected. Border expands just to break the silence.",
            "The world twitches from boredom and grows a bit.",
            "Nothing new? The border takes a lazy step back.",
            "The realm nudges its walls outward, waiting for you to wake up."
    };

    /**
     * Pool of messages used when a player dies and the border shrinks.
     * Style: slightly dark, sarcastic, playful.
     */
    public static final String[] DEATH_SHRINK_MESSAGES = new String[] {
            "took an L. The world panics and squeezes inward.",
            "faceplanted. The border shrinks out of pure disappointment.",
            "got deleted. The border retreats, scared it’s next.",
            "forgot how to live. The realm tightens in their honor.",
            "didn’t stick the landing. The border flinches and pulls back.",
            "went bonk. The map shrinks from second-hand trauma.",
            "tripped over gravity. The world nervously tightens its grip.",
            "used the respawn strat. The border rage-shrinks in response.",
            "forgot fall damage exists. The world closes in a bit.",
            "embraced the void. The realm shrinks in awkward silence.",
            "took a skill issue to the face. The border pulls closer.",
            "met the respawn screen. The world reels the walls in.",
            "ran out of hearts. The border tightens like a stress ball."
    };

    // ---------------------------------------------------------------------
    // Discovery message
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // Failsafe (inactivity) expansion messages
    // ---------------------------------------------------------------------

    /**
     * Builds the multi-line message shown when the inactivity expansion triggers.
     *
     * @return An array of Components to broadcast line-by-line.
     */
    public static Component[] buildFailsafeExpansionMessages() {
        String suffix = FAILSAFE_MESSAGES[
                BordersMod.RANDOM.nextInt(FAILSAFE_MESSAGES.length)
                ];

        Component line1 = Component.literal("=== Border Update ===")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0xFFA500)) // orange
                        .withBold(true)
                );

        Component line2 = Component.literal("No new items for a while...")
                .withStyle(ChatFormatting.YELLOW);

        Component line3 = Component.literal(suffix)
                .withStyle(ChatFormatting.GOLD);

        return new Component[] { line1, line2, line3 };
    }

    // ---------------------------------------------------------------------
    // Death-triggered border shrink messages (v1.3)
    // ---------------------------------------------------------------------

    /**
     * Builds the multi-line message shown when a player dies and causes
     * the world border to shrink.
     *
     * @param playerName Name of the player who died
     * @return An array of Components to broadcast line-by-line.
     */
    public static Component[] buildDeathShrinkMessages(String playerName) {
        String suffix = DEATH_SHRINK_MESSAGES[
                BordersMod.RANDOM.nextInt(DEATH_SHRINK_MESSAGES.length)
                ];

        Component line1 = Component.literal("=== Border Shrink ===")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0xFF5555)) // light red
                        .withBold(true)
                );

        Component line2 = Component.literal("")
                .append(
                        Component.literal(playerName)
                                .withStyle(style -> style.withColor(TextColor.fromRgb(0x55FFFF))) // cyan
                )
                .append(Component.literal(" "))
                .append(
                        Component.literal(suffix)
                                .withStyle(style -> style.withColor(TextColor.fromRgb(0xFFAA00))) // gold-ish
                );

        return new Component[] { line1, line2 };
    }

    // ---------------------------------------------------------------------
    // /borders info panel
    // ---------------------------------------------------------------------

    /**
     * Builds the info output for /borders info.
     */
    public static Component[] buildInfoMessages(
            boolean gameActive,
            boolean failsafeEnabled,
            double borderSize,
            double centerX,
            double centerZ,
            int discoveredCount,
            int growthBlocksPerSide,
            long failsafeDelaySeconds,
            long secondsSinceLastDiscovery
    ) {
        Component header = Component.literal("=== Borders Info ===")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x00FFFF)) // aqua
                        .withBold(true)
                );

        Component status = Component.literal("Game: ")
                .append(Component.literal(gameActive ? "ACTIVE" : "INACTIVE")
                        .withStyle(style -> style.withColor(gameActive ? 0x00FF00 : 0xFF5555)));

        Component borderLine = Component.literal("Border size: ")
                .append(Component.literal(String.valueOf((int) Math.round(borderSize)))
                        .withStyle(style -> style.withColor(0xFFD700)))
                .append(Component.literal(" (center: " + (int) centerX + ", " + (int) centerZ + ")"));

        Component discoveries = Component.literal("Unique items discovered: ")
                .append(Component.literal(String.valueOf(discoveredCount))
                        .withStyle(style -> style.withColor(0x7FCC19)));

        Component growth = Component.literal("Growth per discovery: ")
                .append(Component.literal(growthBlocksPerSide + " blocks per side")
                        .withStyle(style -> style.withColor(0xFFD700)));

        Component failsafeLine = Component.literal("Inactivity expansion: ")
                .append(Component.literal(failsafeEnabled ? "ON" : "OFF")
                        .withStyle(style -> style.withColor(failsafeEnabled ? 0x00FF00 : 0xFF5555)))
                .append(Component.literal(" (after " + failsafeDelaySeconds + "s)"));

        Component lastDiscovery = Component.literal("Time since last discovery: ")
                .append(Component.literal(secondsSinceLastDiscovery + "s")
                        .withStyle(style -> style.withColor(0xAAAAAA)));

        return new Component[] {
                header,
                status,
                borderLine,
                discoveries,
                growth,
                failsafeLine,
                lastDiscovery
        };
    }
}
