package com.borders.messages;

import com.borders.BordersMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import com.borders.state.GameState;

import net.minecraft.network.chat.ClickEvent;

/**
 * Handles all chat message construction and formatting for the Borders mod.
 *
 * Right now this focuses on:
 *  - Randomized flavor text when a new item is discovered
 *  - Failsafe (inactivity) border expansions
 *  - Info panels for /borders info
 *  - Death-triggered border shrink messages
 *  - Help panel for /borders help
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
            "One more item and the world needs therapy.",
            "The world sighs… fine, take some more land.",
            "Border panics and scoots back again!",
            "The realm stretches like it just woke up.",
            "Another discovery? The world nervously expands.",
            "World admins have approved more terrain.",
            "The border retreats in pure confusion.",
            "You poke the world. It grows defensively.",
            "The map got stage fright and backed up.",
            "The world mutters something and widens.",
            "Terrain gods say: ‘enlarge that thing.’",
            "Border: ‘not this again…’ *moves away*.",
            "The world reluctantly reveals more secrets.",
            "Another item? Border immediately regrets its life choices.",
            "The world levels up out of peer pressure.",
            "Reality stretches like cheap fabric.",
            "The border steps back to avoid eye contact.",
            "Terrain expands for dramatic effect.",
            "Another victory for exploration… another loss for the border.",
            "The world grows because you kept insisting.",
            "Border moves aside like: ‘after you…’.",
            "Space-time flinches and expands a bit.",
            "The world bloats with newfound confidence.",
            "Your discovery startled the map.",
            "The border shuffles embarrassingly out of the way.",
            "World whispers: ‘okay okay, here’s more land.’",
            "The realm stretches like dough in clumsy hands.",
            "Border evacuates the area.",
            "The map widens out of pure intimidation.",
            "The world expands… again… someone stop this player.",
            "Border flees the scene dramatically.",
            "Another item? The map unlocks panic mode expansion.",
            "The world just rage-expanded.",
            "Reality taps out and grows a size.",
            "Border tries to stand its ground and fails.",

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
            "The realm nudges its walls outward, waiting for you to wake up.",
            "Nobody found anything new, so the world yawns and stretches again.",
            "The border moves on its own, just to stay entertained.",
            "The realm expands out of pure boredom.",
            "Quiet day… so the world pushes the border for fun.",
            "No discoveries detected. Border drifts outward anyway.",
            "The world shrugs and grows a little for no reason.",
            "Border slides back because absolutely nothing was happening.",
            "The realm stretches like it's trying to stay awake.",
            "Nothing going on, so the map widens out of habit.",
            "The world fidgets and expands a bit.",
            "Still silence… border decides to relocate itself.",
            "The realm grows because it's tired of waiting on you.",
            "The world gets restless and shifts the border.",
            "No action? Border moves out of sheer awkwardness.",
            "Terrain expands on auto-pilot.",
            "The world sighs dramatically and grows a tile.",
            "Still no discoveries? Border wanders off.",
            "The realm pokes itself and stretches.",
            "Nothing? Fine. The world does the expanding for you.",
            "Border inches away from boredom.",

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
            "ran out of hearts. The border tightens like a stress ball.",
            "took a reality slap. The world shrinks in disbelief.",
            "got folded instantly. The border backs up nervously.",
            "failed the vibe check. The realm contracts out of sympathy.",
            "respawned unplanned. The border reels inward.",
            "got clapped by physics. The world tightens in concern.",
            "got combo’d by life. The border shrinks from shock.",
            "forgot how health bars work. The realm pulls in awkwardly.",
            "went splat. The border panics and retreats.",
            "got humbled. The world cinches inward.",
            "got outplayed by gravity. The border cringes inward.",
            "misclicked existence. The realm shrinks in confusion.",
            "chose violence… badly. The world pulls closer.",
            "took a fatal detour. The border curls inward.",
            "lost the 1v1 against reality. The realm tightens.",
            "respawned unexpectedly. The world shrinks out of reflex.",
            "got ratio’d by nature. The border recoils inward.",
            "got deleted by the environment. The realm contracts.",
            "failed their survival exam. The border slides closer.",
            "forgot to dodge. The world shrivels a bit.",
            "got sent to the shadow lobby. The border panics and tightens."

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
    // Death-triggered border shrink messages
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
     *
     * Grouped layout:
     *  - Header
     *  - Status
     *  - Growth & Shrink
     *  - Inactivity
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
            long secondsSinceLastDiscovery,
            boolean deathShrinkEnabled,
            int deathShrinkBlocksPerSide
    ) {
        // Header
        Component header = Component.literal("=== Borders Info ===")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x00FFFF)) // aqua
                        .withBold(true)
                );

        // Blank line
        Component blank = Component.literal("");

        // ---------------- STATUS ----------------
        Component statusHeader = Component.literal("Status")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component statusLine = Component.literal("Game: ")
                .append(
                        Component.literal(gameActive ? "ACTIVE" : "INACTIVE")
                                .withStyle(style -> style.withColor(gameActive ? 0x00FF00 : 0xFF5555))
                );

        Component borderLine = Component.literal("Border: ")
                .append(
                        Component.literal(String.valueOf((int) Math.round(borderSize)))
                                .withStyle(style -> style.withColor(0xFFD700))
                )
                .append(Component.literal(" blocks "))
                .append(
                        Component.literal("(center " + (int) centerX + ", " + (int) centerZ + ")")
                                .withStyle(style -> style.withColor(0xAAAAAA))
                );

        Component discoveries = Component.literal("Unique items: ")
                .append(
                        Component.literal(String.valueOf(discoveredCount))
                                .withStyle(style -> style.withColor(0x7FCC19))
                );

        // ---------------- GROWTH & SHRINK ----------------
        Component growthHeader = Component.literal("Growth & Shrink")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component growth = Component.literal("On discovery: ")
                .append(
                        Component.literal("+" + growthBlocksPerSide + " blocks per side")
                                .withStyle(style -> style.withColor(0xFFD700))
                );

        Component deathShrinkLine = Component.literal("On death: ")
                .append(
                        Component.literal(
                                (deathShrinkEnabled ? "-" : "– ") + deathShrinkBlocksPerSide + " blocks per side"
                        ).withStyle(style -> style.withColor(0xFFAA00))
                )
                .append(Component.literal(" ("))
                .append(
                        Component.literal(deathShrinkEnabled ? "ENABLED" : "DISABLED")
                                .withStyle(style -> style.withColor(deathShrinkEnabled ? 0x00FF00 : 0xFF5555))
                )
                .append(Component.literal(")"));

        // ---------------- INACTIVITY ----------------
        Component inactivityHeader = Component.literal("Inactivity")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component failsafeLine = Component.literal("Auto-expand: ")
                .append(
                        Component.literal(failsafeEnabled ? "ON" : "OFF")
                                .withStyle(style -> style.withColor(failsafeEnabled ? 0x00FF00 : 0xFF5555))
                )
                .append(Component.literal(" (after " + failsafeDelaySeconds + "s)"));

        Component lastDiscovery = Component.literal("Time since last discovery: ")
                .append(
                        Component.literal(secondsSinceLastDiscovery + "s")
                                .withStyle(style -> style.withColor(0xAAAAAA))
                );

        return new Component[] {
                header,
                blank,

                statusHeader,
                statusLine,
                borderLine,
                discoveries,
                blank,

                growthHeader,
                growth,
                deathShrinkLine,
                blank,

                inactivityHeader,
                failsafeLine,
                lastDiscovery
        };
    }

    // ---------------------------------------------------------------------
    // /borders help panel
    // ---------------------------------------------------------------------

    /**
     * Builds the help output for /borders help.
     *
     * Groups commands into:
     *  - Core
     *  - Configuration
     *  - Manual border control
     */
    public static Component[] buildHelpMessages() {
        Component header = Component.literal("=== Borders Commands ===")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x00FFFF)) // aqua
                        .withBold(true)
                );

        Component usage = Component.literal("Usage: ")
                .withStyle(style -> style.withColor(0xAAAAAA))
                .append(
                        Component.literal("/borders <command> [args]")
                                .withStyle(style -> style.withColor(0xFFD700))
                );

        Component blank = Component.literal("");

        // Core commands
        Component coreHeader = Component.literal("Core")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component startLine = helpLine(
                "/borders start",
                "Start the Borders game; enable expansion and inactivity growth."
        );

        Component stopLine = helpLine(
                "/borders stop",
                "Stop the Borders game; freeze automatic expansion."
        );

        Component resetLine = helpLine(
                "/borders reset",
                "Reset border, items, inventories, and game state."
        );

        Component infoLine = helpLine(
                "/borders info",
                "Show detailed status of the border and game configuration."
        );

        Component reloadLine = helpLine(
                "/borders reload",
                "Resync borders in all dimensions and rebuild the scoreboard."
        );

        // Configuration commands
        Component configHeader = Component.literal("Configuration")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component growthLine = helpLine(
                "/borders setgrowth <blocksPerSide>",
                "Set growth per new unique item (per side; diameter = x2)."
        );

        Component deathShrinkLine = helpLine(
                "/borders setdeathshrink <blocksPerSide>",
                "Set shrink per player death (per side; diameter = x2)."
        );

        Component toggleDeathLine = helpLine(
                "/borders toggledeathshrink",
                "Enable or disable border shrinking when players die."
        );

        Component timerLine = helpLine(
                "/borders settimer <seconds>",
                "Set inactivity time before automatic border expansion."
        );

        // Manual border control
        Component manualHeader = Component.literal("Manual Border Control")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component growLine = helpLine(
                "/borders grow <blocksPerSide>",
                "Manually grow border by this many blocks on each side."
        );

        Component shrinkLine = helpLine(
                "/borders shrink <blocksPerSide>",
                "Manually shrink border by this many blocks on each side."
        );

        return new Component[] {
                header,
                usage,
                blank,

                coreHeader,
                startLine,
                stopLine,
                resetLine,
                infoLine,
                reloadLine,
                blank,

                configHeader,
                growthLine,
                deathShrinkLine,
                toggleDeathLine,
                timerLine,
                blank,

                manualHeader,
                growLine,
                shrinkLine
        };
    }

    /**
     * Builds the chat-based configuration panel that is shown
     * when an OP right-clicks the Borders Control Book.
     *
     * v2.1: read-only status + config overview.
     * Later we will extend this with clickable buttons.
     */
    public static Component[] buildConfigPanel(GameState state) {
        // Derive some values
        boolean gameActive = state.gameActive;
        boolean failsafeEnabled = state.failsafeEnabled;

        int borderSize = (int) Math.round(state.currentBorderSize);
        int growthPerSide = state.discoveryGrowthBlocksPerSide;
        boolean deathEnabled = state.deathShrinkEnabled;
        int deathAmount = state.deathShrinkBlocksPerSide;

        long failsafeTicks = state.borderFailsafeDelayTicks;
        int failsafeSeconds = (int) (failsafeTicks / 20L);

        // Header
        Component header = Component.literal("=== Borders Configuration ===")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x00FFFF)) // aqua
                        .withBold(true)
                );

        Component blank = Component.literal("");

        // ---------------- STATUS ----------------
        Component statusHeader = Component.literal("Status")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF)) // light aqua
                        .withBold(true)
                );

        Component statusLine = Component.literal("Game: ")
                .append(
                        Component.literal(gameActive ? "ACTIVE" : "INACTIVE")
                                .withStyle(style -> style.withColor(
                                        gameActive ? 0x00FF00 : 0xFF5555
                                ))
                );

        Component borderLine = Component.literal("Border size: ")
                .append(
                        Component.literal(String.valueOf(borderSize))
                                .withStyle(style -> style.withColor(0xFFD700)) // gold
                )
                .append(Component.literal(" blocks (diameter)"))
                .withStyle(style -> style.withColor(0xAAAAAA));

        // ---------------- DISCOVERY EXPANSION ----------------
        Component growthHeader = Component.literal("Discovery Expansion")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component growthLine = Component.literal("Growth per new unique item: ")
                .append(
                        Component.literal("+" + growthPerSide)
                                .withStyle(style -> style.withColor(0x00FF00)) // green
                )
                .append(Component.literal(" blocks per side"))
                .withStyle(style -> style.withColor(0xAAAAAA));

        // Preset buttons for /borders setgrowth
        Component growthPresetsLabel = Component.literal("Presets: ")
                .withStyle(style -> style.withColor(0xAAAAAA));

        Component growthPreset1 = Component.literal("[1]")
                .withStyle(style -> style
                        .withColor(0x00FFFF)
                        .withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/borders setgrowth 1"))
                );

        Component growthPreset2 = Component.literal("[2]")
                .withStyle(style -> style
                        .withColor(0x00FFFF)
                        .withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/borders setgrowth 2"))
                );

        Component growthPreset3 = Component.literal("[3]")
                .withStyle(style -> style
                        .withColor(0x00FFFF)
                        .withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/borders setgrowth 3"))
                );

        Component growthPreset4 = Component.literal("[4]")
                .withStyle(style -> style
                        .withColor(0x00FFFF)
                        .withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/borders setgrowth 4"))
                );

        Component growthPreset5 = Component.literal("[5]")
                .withStyle(style -> style
                        .withColor(0x00FFFF)
                        .withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/borders setgrowth 5"))
                );

        Component growthPresetsLine = Component.literal("")
                .append(growthPresetsLabel)
                .append(growthPreset1).append(Component.literal(" "))
                .append(growthPreset2).append(Component.literal(" "))
                .append(growthPreset3).append(Component.literal(" "))
                .append(growthPreset4).append(Component.literal(" "))
                .append(growthPreset5);

        // Custom value (suggests /borders setgrowth <value>)
        Component growthCustom = Component.literal("[Custom…]")
                .withStyle(style -> style
                        .withColor(0xFFFF55) // yellow
                        .withBold(true)
                        .withClickEvent(new ClickEvent.SuggestCommand("/borders setgrowth "))
                );


        // ---------------- DEATH SHRINK ----------------
        Component deathHeader = Component.literal("Shrink on Death")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component deathStatus = Component.literal("Status: ")
                .append(
                        Component.literal(deathEnabled ? "ENABLED" : "DISABLED")
                                .withStyle(style -> style.withColor(
                                        deathEnabled ? 0x00FF00 : 0xFF5555
                                ))
                );

        Component deathAmountLine = Component.literal("Shrink amount: ")
                .append(
                        Component.literal("-" + deathAmount)
                                .withStyle(style -> style.withColor(0xFF5555)) // red
                )
                .append(Component.literal(" blocks per side"))
                .withStyle(style -> style.withColor(0xAAAAAA));

        // ---------------- INACTIVITY EXPANSION ----------------
        Component failsafeHeader = Component.literal("Inactivity Expansion")
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0x55FFFF))
                        .withBold(true)
                );

        Component failsafeStatus = Component.literal("Status: ")
                .append(
                        Component.literal(failsafeEnabled ? "ENABLED" : "DISABLED")
                                .withStyle(style -> style.withColor(
                                        failsafeEnabled ? 0x00FF00 : 0xFF5555
                                ))
                );

        Component failsafeTimer = Component.literal("Timer: ")
                .append(
                        Component.literal(String.valueOf(failsafeSeconds))
                                .withStyle(style -> style.withColor(0x00FF00))
                )
                .append(Component.literal(" seconds without discoveries"))
                .withStyle(style -> style.withColor(0xAAAAAA));

        return new Component[] {
                header,
                blank,

                statusHeader,
                statusLine,
                borderLine,
                blank,

                growthHeader,
                growthLine,
                growthPresetsLine,
                growthCustom,
                blank,

                deathHeader,
                deathStatus,
                deathAmountLine,
                blank,

                failsafeHeader,
                failsafeStatus,
                failsafeTimer
        };
    }


    /**
     * Small helper to format a colored "/command" + gray description line.
     */
    private static Component helpLine(String command, String description) {
        return Component.literal("")
                .append(
                        Component.literal(command)
                                .withStyle(style -> style.withColor(0xFFD700)) // gold for command
                )
                .append(Component.literal(" - ")
                        .withStyle(style -> style.withColor(0xAAAAAA)))
                .append(
                        Component.literal(description)
                                .withStyle(style -> style.withColor(0xCCCCCC))
                );
    }
}
