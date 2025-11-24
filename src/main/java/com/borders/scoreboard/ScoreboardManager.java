package com.borders.scoreboard;

import com.borders.BordersMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

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
 */
public class ScoreboardManager {

    /** Name of the objective used for discovery count tracking. */
    public static final String DISCOVERY_OBJECTIVE_NAME = "borders_discoveries";

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
        ScoreHolder spacerHolder = () -> " ";  // pure blank line
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
        ScoreHolder separatorHolder = () -> "§8────────────";  // dark grey line

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

        // Remove previous border line if present
        if (lastBorderLineName != null) {
            ScoreHolder oldHolder = () -> lastBorderLineName;
            scoreboard.resetSinglePlayerScore(oldHolder, objective);
        }

        int displaySize = (int) Math.round(BordersMod.STATE.currentBorderSize);
        String newName = "§bBorder size: §e" + displaySize;

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
}
