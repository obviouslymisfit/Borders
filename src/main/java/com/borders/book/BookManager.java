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

import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.server.network.Filterable;
import net.minecraft.network.chat.ClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles creation and delivery of the Border Control Book.
 *
 * v2.1:
 *  - Builds a written book for admins
 *  - Book contains clickable configuration entries for the Borders mod
 */
public class BookManager {

    /** Display name of the book shown to the player. */
    public static final String BOOK_TITLE = "Border Control Book";

    /** Author name shown in the book UI. */
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
     * The book UI is now the primary interface, but chat remains as a fallback.
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
     * v2.1: this is now a proper written book using the
     * WRITTEN_BOOK_CONTENT data component.
     */
    private static ItemStack buildControlBook() {
        // Give a written book, not a writable one
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

        // Hover name in inventory
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(BOOK_TITLE));

        // --- Build pages ---

        List<Filterable<Component>> pages = new ArrayList<>();

        // PAGE 1 — HOME / INDEX (with page navigation)
        Component page1 = Component.literal("§6BORDER CONTROL BOOK\n")
                .append(Component.literal("§7Configuration Menu\n\n"))

                // Game Control → page 2
                .append(
                        Component.literal("§3Game Control\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(2)
                                ))
                )

                // Expansion Amount → page 3
                .append(
                        Component.literal("§3Expansion Amount\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(3)
                                ))
                )

                // Death Shrink → page 4
                .append(
                        Component.literal("§3Death Shrink\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(4)
                                ))
                )

                // Inactivity Expansion → page 5
                .append(
                        Component.literal("§3Inactivity Expansion\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(5)
                                ))
                )

                // Manual Tools → page 6
                .append(
                        Component.literal("§3Manual Tools\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(6)
                                ))
                )

                // Border Info → page 7
                .append(
                        Component.literal("§3Border Info\n\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(7)
                                ))
                )

                .append(Component.literal("§7Tap a section to open it."));

        pages.add(Filterable.passThrough(page1));

        // PAGE 2 — GAME CONTROL
        Component page2Header = Component.literal("§6GAME CONTROL\n\n");

        Component page2Start = Component.literal("§3Start Game\n")
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/borders start")
                ));

        Component page2Stop = Component.literal("§3Stop Game\n")
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/borders stop")
                ));

        Component page2Reset = Component.literal("§3Reset Everything\n")
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/borders reset")
                ));

        Component page2Reload = Component.literal("§3Reload Borders\n")
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent.RunCommand("/borders reload")
                ));

        Component page2 = Component.literal("")
                .append(page2Header)
                .append(page2Start)
                .append(page2Stop)
                .append(page2Reset)
                .append(page2Reload)
                    .append(Component.literal("\n§7Back: "))
                        .append(
                            Component.literal("§3[ Home ]")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(1)
                                ))
                );

        pages.add(Filterable.passThrough(page2));

        // PAGE 3 — DISCOVERY EXPANSION (buttons + custom hint)
        Component page3 = Component.literal("§6EXPANSION PER DISCOVERY\n\n")
                // [ 1 ]
                .append(
                        Component.literal("§3[ 1 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setgrowth 1")
                                ))
                )
                // [ 2 ]
                .append(
                        Component.literal("§3[ 2 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setgrowth 2")
                                ))
                )
                // [ 3 ]
                .append(
                        Component.literal("§3[ 3 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setgrowth 3")
                                ))
                )
                // [ 5 ]
                .append(
                        Component.literal("§3[ 5 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setgrowth 5")
                                ))
                )
                // [ 10 ]
                .append(
                        Component.literal("§3[ 10 ]\n\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setgrowth 10")
                                ))
                )
                // Custom hint (no click) + back link
                .append(
                        Component.literal("§3Custom: §7/borders setgrowth <amount>\n\n")
                )
                .append(
                        Component.literal("§7Back: ")
                )
                .append(
                        Component.literal("§3[ Home ]")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(1)
                                ))
                );


        pages.add(Filterable.passThrough(page3));

        // PAGE 4 — SHRINK ON DEATH
        Component page4 = Component.literal("§6SHRINK ON DEATH\n\n")
                // [ 1 ]
                .append(
                        Component.literal("§3[ 1 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setdeathshrink 1")
                                ))
                )
                // [ 2 ]
                .append(
                        Component.literal("§3[ 2 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setdeathshrink 2")
                                ))
                )
                // [ 3 ]
                .append(
                        Component.literal("§3[ 3 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setdeathshrink 3")
                                ))
                )
                // [ 5 ]
                .append(
                        Component.literal("§3[ 5 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setdeathshrink 5")
                                ))
                )
                // [ 10 ]
                .append(
                        Component.literal("§3[ 10 ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders setdeathshrink 10")
                                ))
                )
                // [ TOGGLE ]
                .append(
                        Component.literal("§3[ TOGGLE ]\n\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders toggledeathshrink")
                                ))
                )
                // Custom line + back link
                .append(
                        Component.literal("§3Custom: §7/borders setdeathshrink <amount>\n\n")
                )
                .append(
                        Component.literal("§7Back: ")
                )
                .append(
                        Component.literal("§3[ Home ]")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(1)
                                ))
                );


        pages.add(Filterable.passThrough(page4));

        // PAGE 5 — INACTIVITY TIMER
        Component page5 = Component.literal("§6INACTIVITY TIMER\n\n")
                // [ 30s ]
                .append(
                        Component.literal("§3[ 30s ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders settimer 30")
                                ))
                )
                // [ 60s ]
                .append(
                        Component.literal("§3[ 60s ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders settimer 60")
                                ))
                )
                // [ 120s ]
                .append(
                        Component.literal("§3[ 120s ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders settimer 120")
                                ))
                )
                // [ 300s ]
                .append(
                        Component.literal("§3[ 300s ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders settimer 300")
                                ))
                )
                // [ 600s ]
                .append(
                        Component.literal("§3[ 600s ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders settimer 600")
                                ))
                )
                // [ TOGGLE ]
                .append(
                        Component.literal("§3[ TOGGLE ]\n\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders togglefailsafe")
                                ))
                )
                // Custom line + back link
                .append(
                        Component.literal("§3Custom: §7/borders settimer <seconds>\n\n")
                )
                .append(
                        Component.literal("§7Back: ")
                )
                .append(
                        Component.literal("§3[ Home ]")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(1)
                                ))
                );


        pages.add(Filterable.passThrough(page5));

        // PAGE 6 — MANUAL TOOLS
        Component page6 = Component.literal("§6MANUAL TOOLS\n\n")
                // Grow section
                .append(Component.literal("§7Grow border by:\n"))
                .append(
                        Component.literal("§3[ +1 per side ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders grow 1")
                                ))
                )
                .append(
                        Component.literal("§3[ +5 per side ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders grow 5")
                                ))
                )

                // Shrink section
                .append(Component.literal("§7Shrink border by:\n"))
                .append(
                        Component.literal("§3[ -1 per side ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders shrink 1")
                                ))
                )
                .append(
                        Component.literal("§3[ -5 per side ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders shrink 5")
                                ))
                )

                // Custom lines + back link
                .append(
                        Component.literal("§3Custom: §7/borders grow <blocks>\n")
                )
                .append(
                        Component.literal("§3Custom: §7/borders shrink <blocks>\n\n")
                )
                .append(
                        Component.literal("§7Back: ")
                )
                .append(
                        Component.literal("§3[ Home ]")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(1)
                                ))
                );


        pages.add(Filterable.passThrough(page6));

        // PAGE 7 — BORDER INFO
        Component page7 = Component.literal("§6BORDER INFO\n\n")
                .append(Component.literal("§7Useful commands:\n\n"))
                // [ SHOW INFO ]
                .append(
                        Component.literal("§3[ SHOW INFO ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders info")
                                ))
                )
                // [ RELOAD ]
                .append(
                        Component.literal("§3[ RELOAD ]\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders reload")
                                ))
                )
                // [ HELP ] + back link
                .append(
                        Component.literal("§3[ HELP ]\n\n")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.RunCommand("/borders help")
                                ))
                )
                .append(
                        Component.literal("§7Back: ")
                )
                .append(
                        Component.literal("§3[ Home ]")
                                .withStyle(style -> style.withClickEvent(
                                        new ClickEvent.ChangePage(1)
                                ))
                );


        pages.add(Filterable.passThrough(page7));

        // Build the written book content component
        WrittenBookContent content = new WrittenBookContent(
                Filterable.passThrough(BOOK_TITLE), // title (record component)
                BOOK_AUTHOR,                        // author
                0,                                  // generation (Original)
                pages,                              // pages
                true                                // resolved -> allow all click events
        );

        // Attach to stack
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, content);

        return stack;
    }
}
