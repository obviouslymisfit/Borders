package com.borders.scoreboard;

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
 *  - Adds a separator line under the title
 *  - Exposes a reset method used by /borders reset
 */
public class ScoreboardManager {

    /** Name of the objective used for discovery count tracking. */
    public static final String DISCOVERY_OBJECTIVE_NAME = "borders_discoveries";

    /**
     * Ensures the discovery scoreboard objective exists, is styled consistently,
     * and is shown on the sidebar (DisplaySlot.SIDEBAR).
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
                    Component.literal("ITEMS FOUND")
                            .withStyle(style -> style
                                    .withColor(TextColor.fromRgb(0x7FCC19)) // greenish color
                                    .withBold(true)
                            ),
                    ObjectiveCriteria.RenderType.INTEGER,
                    false,
                    null
            );

        } else {

            // Force the title style even if created earlier
            objective.setDisplayName(
                    Component.literal("ITEMS FOUND")
                            .withStyle(style -> style
                                    .withColor(TextColor.fromRgb(0x7FCC19))
                                    .withBold(true)
                            )
            );
        }

        // ------------------------------------------------------------
        // Add a separator "line" right under the title
        // ------------------------------------------------------------
        ScoreHolder separatorHolder = () -> "§8────────";  // dark grey line

        ScoreAccess separatorScore = scoreboard.getOrCreatePlayerScore(separatorHolder, objective);

        // High value ensures it sorts immediately under the title
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
     * Resets all discovery scores — used by /borders reset.
     */
    public static void resetDiscoveryScores(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(DISCOVERY_OBJECTIVE_NAME);

        if (objective != null) {
            for (ScoreHolder holder : scoreboard.getTrackedPlayers()) {
                scoreboard.resetSinglePlayerScore(holder, objective);
            }
        }
    }
}
