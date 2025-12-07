package com.borders.scoreboard;

import com.borders.BordersMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;


/**
 * Manages the scoreboard objective used to track the number of
 * unique items discovered during the Borders game.
 *
 * This class:
 *  - Creates the sidebar objective if missing
 *  - Applies consistent styling to it
 *  - Adds a blank spacer line under the title
 *  - Adds a border-size line under the spacer
 *  - Adds a separator line under the border-size line
 *  - Exposes a reset method used by /borders reset
 *  - Exposes a border-size update helper used when the border changes
 *  - Exposes helpers to export/import player scores for persistence
 */
public class ScoreboardManager {

    /** Name of the objective used for discovery count tracking. */
    public static final String DISCOVERY_OBJECTIVE_NAME = "borders_discoveries";

    /** Fake entry used as a spacer line under the title. */
    private static final String SPACER_NAME = " ";

    /** Fake entry used as a separator line under the border line. */
    private static final String SEPARATOR_NAME = "§8────────────";

    /** Prefix used by the fake "Border size: X" line. */
    private static final String BORDER_LINE_PREFIX = "§bBorder size:";

    /**
     * Tracks the last "Border size: X" line used on the scoreboard so we can
     * remove it cleanly before adding an updated line.
     */
    private static String lastBorderLineName = null;

    /**
     * Ensures the discovery scoreboard objective exists, is styled consistently,
     * and is shown on the sidebar (DisplaySlot.SIDEBAR).
     *
     * It also ensures the spacer, border-size, and separator lines are present.
     *
     * @return The objective instance (new or existing)
     */
    public static Objective getOrCreateDiscoveryObjective(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        // Check if the objective already exists
        Objective objective = scoreboard.getObjective(DISCOVERY_OBJECTIVE_NAME);
        if (objective == null) {

            // Create new sidebar objective
            objective = scoreboard.addObjective(
                    DISCOVERY_OBJECTIVE_NAME,
                    ObjectiveCriteria.DUMMY,
                    Component.literal("BORDERS")
                            .withStyle(style -> style
                                    .withColor(TextColor.fromRgb(0x00FFFF)) // bright aqua
                                    .withBold(true)
                            ),
                    ObjectiveCriteria.RenderType.INTEGER,
                    false,
                    null
            );

        } else {

            // Force the title style even if created earlier
            objective.setDisplayName(
                    Component.literal("BORDERS")
                            .withStyle(style -> style
                                    .withColor(TextColor.fromRgb(0x00FFFF))
                                    .withBold(true)
                            )
            );
        }

        // ------------------------------------------------------------
        // Spacer line directly under the title
        // ------------------------------------------------------------
        ScoreHolder spacerHolder = () -> SPACER_NAME;  // pure blank line
        ScoreAccess spacerScore = scoreboard.getOrCreatePlayerScore(spacerHolder, objective);
        spacerScore.set(10002);
        spacerScore.numberFormatOverride(BlankFormat.INSTANCE);

        // ------------------------------------------------------------
        // Border size line just under the spacer
        // ------------------------------------------------------------
        updateBorderSizeLine(server);

        // ------------------------------------------------------------
        // Add a separator "line" right under the border line
        // ------------------------------------------------------------
        ScoreHolder separatorHolder = () -> SEPARATOR_NAME;  // dark grey line

        ScoreAccess separatorScore = scoreboard.getOrCreatePlayerScore(separatorHolder, objective);

        // High value ensures it sorts below the border line but above players
        separatorScore.set(10000);

        // Hide its actual numeric score (1.21.x style)
        separatorScore.numberFormatOverride(BlankFormat.INSTANCE);

        // ------------------------------------------------------------
        // Make this the active sidebar objective
        // ------------------------------------------------------------
        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);

        return objective;
    }

    /**
     * Removes any existing "Border size" lines from the scoreboard objective,
     * without touching real player entries.
     *
     * This is important across restarts, where we may not know the exact
     * previous line text but we still want to get rid of old border entries.
     */
    private static void cleanupBorderLines(Scoreboard scoreboard, Objective objective) {
        for (ScoreHolder holder : scoreboard.getTrackedPlayers()) {
            String name = holder.getScoreboardName();
            if (name != null && name.startsWith(BORDER_LINE_PREFIX)) {
                scoreboard.resetSinglePlayerScore(holder, objective);
            }
        }
    }

    /**
     * Updates (or creates) the "Border size: <size>" line in the sidebar.
     *
     * Layout target:
     *  [Title: BORDERS]
     *  (blank spacer)
     *  Border size: 112
     *  ────────────
     *  Player1 5
     *  Player2 3
     *
     * The line is a fake "player" entry whose name encodes the border size.
     * Its numeric score is large (10001) and hidden, so ordering is driven
     * by the score while the visible text shows the actual border size.
     */
    public static void updateBorderSizeLine(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(DISCOVERY_OBJECTIVE_NAME);

        if (objective == null) {
            // Objective not created yet – nothing to update
            return;
        }

        // Remove *any* existing border lines (including ones from previous runs)
        cleanupBorderLines(scoreboard, objective);

        // Remove previous border line if we tracked one this session
        if (lastBorderLineName != null) {
            ScoreHolder oldHolder = () -> lastBorderLineName;
            scoreboard.resetSinglePlayerScore(oldHolder, objective);
        }

        // ─────────────────────────────────────────────
        // NEW: sync from the actual world border size
        // ─────────────────────────────────────────────
        double borderSize = BordersMod.STATE.currentBorderSize;

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld != null) {
            borderSize = overworld.getWorldBorder().getSize();
            // Keep GameState in sync with the real border
            BordersMod.STATE.currentBorderSize = borderSize;
        }

        int displaySize = (int) Math.round(borderSize);
        String newName = BORDER_LINE_PREFIX + " §e" + displaySize; // §bBorder size: §eX

        ScoreHolder borderHolder = () -> newName;
        ScoreAccess borderScore = scoreboard.getOrCreatePlayerScore(borderHolder, objective);

        // High score so it appears under the spacer and above the separator
        borderScore.set(10001);
        // Hide the numeric value; we only want the text "Border size: X"
        borderScore.numberFormatOverride(BlankFormat.INSTANCE);

        lastBorderLineName = newName;
    }


    /**
     * Resets all discovery scores — used by /borders reset.
     *
     * Note: this clears all tracked entries for this objective (including
     * fake entries). The next time /borders start or a discovery happens
     * and getOrCreateDiscoveryObjective is called, the spacer/border/separator
     * lines will be recreated.
     */
    public static void resetDiscoveryScores(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(DISCOVERY_OBJECTIVE_NAME);

        if (objective != null) {
            for (ScoreHolder holder : scoreboard.getTrackedPlayers()) {
                scoreboard.resetSinglePlayerScore(holder, objective);
            }
            // Forget any cached border line name so we don't try to delete it later
            lastBorderLineName = null;
        }
    }

    /**
     * Export current player scores (for the Borders objective) so they can be
     * written into persistent storage (JSON).
     *
     * Fake entries (spacer, separator, border line) are skipped.
     */
    public static Map<String, Integer> exportPlayerScores(MinecraftServer server) {
        Map<String, Integer> result = new HashMap<>();

        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(DISCOVERY_OBJECTIVE_NAME);
        if (objective == null) {
            return result;
        }

        for (ScoreHolder holder : scoreboard.getTrackedPlayers()) {
            String name = holder.getScoreboardName();
            if (name == null) {
                continue;
            }

            // Skip our fake entries
            if (name.equals(SPACER_NAME)) continue;
            if (name.equals(SEPARATOR_NAME)) continue;
            if (name.startsWith(BORDER_LINE_PREFIX)) continue;

            ScoreAccess access = scoreboard.getOrCreatePlayerScore(holder, objective);
            int value = access.get();   // 1.21.x method name in this mapping
            result.put(name, value);
        }

        return result;
    }

    /**
     * Import player scores from persistent storage into the Borders objective.
     *
     * This does not touch fake entries (spacer, separator, border line).
     */
    public static void importPlayerScores(MinecraftServer server, Map<String, Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return;
        }

        // Ensure objective + static lines exist
        Objective objective = getOrCreateDiscoveryObjective(server);
        Scoreboard scoreboard = server.getScoreboard();

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String name = entry.getKey();
            int value = entry.getValue();

            if (name == null || name.isEmpty()) {
                continue;
            }
            // Extra paranoia: never treat our fake labels as "players"
            if (name.equals(SPACER_NAME)) continue;
            if (name.equals(SEPARATOR_NAME)) continue;
            if (name.startsWith(BORDER_LINE_PREFIX)) continue;

            ScoreHolder holder = () -> name;
            ScoreAccess access = scoreboard.getOrCreatePlayerScore(holder, objective);
            access.set(value);
        }
    }
}
