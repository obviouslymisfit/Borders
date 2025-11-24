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
 *  - Nicely formatted info messages for /borders info
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

    /**
     * Builds the set of info lines used by /borders info.
     *
     * Style B:
     *  - Header: aqua frame
     *  - Labels: blue / aqua
     *  - Values: yellow for numbers, green/red for status
     *
     * @param gameActive              Whether the Borders game is active
     * @param failsafeEnabled         Whether the failsafe is enabled
     * @param borderSize              Current border size (diameter)
     * @param centerX                 World border center X
     * @param centerZ                 World border center Z
     * @param discoveredCount         Number of unique items discovered
     * @param growthBlocksPerSide     Blocks added on each side per discovery
     * @param failsafeDelaySeconds    Configured failsafe delay in seconds
     * @param secondsSinceDiscovery   Seconds since last discovery
     * @return An array of Components, each to be sent as a separate chat line
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
            long secondsSinceDiscovery
    ) {
        TextColor headerFrameColor = TextColor.fromRgb(0x00CCCC);  // aqua-ish
        TextColor headerTextColor  = TextColor.fromRgb(0x00FFFF);  // bright aqua
        TextColor labelColor       = TextColor.fromRgb(0x00AAAA);  // dark aqua / blue
        TextColor valueNumberColor = TextColor.fromRgb(0xFFD700);  // gold
        TextColor valueNeutralColor= TextColor.fromRgb(0xFFFFFF);  // white
        TextColor greenColor       = TextColor.fromRgb(0x55FF55);  // status: yes/on
        TextColor redColor         = TextColor.fromRgb(0xFF5555);  // status: no/off;
        TextColor grayColor        = TextColor.fromRgb(0xAAAAAA);  // parentheses, etc.

        // Header: === Borders Info ===
        Component header = Component.literal("")
                .append(Component.literal("=== ")
                        .withStyle(style -> style.withColor(headerFrameColor)))
                .append(Component.literal("Borders Info")
                        .withStyle(style -> style.withColor(headerTextColor)))
                .append(Component.literal(" ===")
                        .withStyle(style -> style.withColor(headerFrameColor)));

        // Game active line
        Component gameActiveLine = Component.literal("")
                .append(Component.literal("Game active: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(gameActive ? "Yes" : "No")
                        .withStyle(style -> style.withColor(gameActive ? greenColor : redColor)));

        // Failsafe line
        Component failsafeLine = Component.literal("")
                .append(Component.literal("Failsafe: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(failsafeEnabled ? "Enabled" : "Disabled")
                        .withStyle(style -> style.withColor(failsafeEnabled ? greenColor : redColor)));

        // Border size line
        Component borderLine = Component.literal("")
                .append(Component.literal("Border: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(String.valueOf(borderSize))
                        .withStyle(style -> style.withColor(valueNumberColor)))
                .append(Component.literal(" (diameter)")
                        .withStyle(style -> style.withColor(grayColor)));

        // Center line
        Component centerLine = Component.literal("")
                .append(Component.literal("Center: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal("X=")
                        .withStyle(style -> style.withColor(grayColor)))
                .append(Component.literal(String.valueOf(centerX))
                        .withStyle(style -> style.withColor(valueNumberColor)))
                .append(Component.literal(" Z=")
                        .withStyle(style -> style.withColor(grayColor)))
                .append(Component.literal(String.valueOf(centerZ))
                        .withStyle(style -> style.withColor(valueNumberColor)));

        // Discoveries line
        Component discoveriesLine = Component.literal("")
                .append(Component.literal("Discoveries: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(String.valueOf(discoveredCount))
                        .withStyle(style -> style.withColor(valueNumberColor)));

        // Growth per discovery line
        Component growthLine = Component.literal("")
                .append(Component.literal("Growth per discovery: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(String.valueOf(growthBlocksPerSide))
                        .withStyle(style -> style.withColor(valueNumberColor)))
                .append(Component.literal(" blocks/side")
                        .withStyle(style -> style.withColor(grayColor)));

        // Failsafe delay
        Component failsafeDelayLine = Component.literal("")
                .append(Component.literal("Failsafe delay: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(failsafeDelaySeconds + "s")
                        .withStyle(style -> style.withColor(valueNumberColor)));

        // Since last discovery
        Component lastDiscoveryLine = Component.literal("")
                .append(Component.literal("Since last discovery: ")
                        .withStyle(style -> style.withColor(labelColor)))
                .append(Component.literal(secondsSinceDiscovery + "s")
                        .withStyle(style -> style.withColor(valueNumberColor)));

        return new Component[] {
                header,
                gameActiveLine,
                failsafeLine,
                borderLine,
                centerLine,
                discoveriesLine,
                growthLine,
                failsafeDelayLine,
                lastDiscoveryLine
        };
    }
}
