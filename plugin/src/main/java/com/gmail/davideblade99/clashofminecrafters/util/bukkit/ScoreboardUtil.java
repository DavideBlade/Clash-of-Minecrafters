/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Balance;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;

//TODO: https://github.com/drtshock/Factions/tree/1.6.x/src/main/java/com/massivecraft/factions/scoreboards
public final class ScoreboardUtil {

    private ScoreboardUtil() {
        throw new IllegalAccessError();
    }

    private final static String GEMS = "§2§l> Gems §a";
    private final static String GOLD = "§e§l> Gold §e";
    private final static String ELIXIR = "§d§l> Elixir §5";
    private final static String TROPHIES = "§6§l> Trophies §e";

    /**
     * Create a side scoreboard with the data provided
     *
     * @param balance  Balance to show
     * @param trophies Trophies to show
     *
     * @return A new {@link Scoreboard}
     *
     * @since 3.1.4
     */
    @Nonnull
    public static Scoreboard createScoreboard(@Nonnull final Balance balance, final int trophies) {
        final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        final Objective objective = board.registerNewObjective("CoM", "dummy", "§6§lStats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        board.registerNewTeam("Gems").addEntry(GEMS);
        board.registerNewTeam("Gold").addEntry(GOLD);
        board.registerNewTeam("Elixir").addEntry(ELIXIR);
        board.registerNewTeam("Trophies").addEntry(TROPHIES);

        objective.getScore(" ").setScore(8);
        objective.getScore(GEMS).setScore(7);
        objective.getScore("  ").setScore(6);
        objective.getScore(GOLD).setScore(5);
        objective.getScore("   ").setScore(4);
        objective.getScore(ELIXIR).setScore(3);
        objective.getScore("    ").setScore(2);
        objective.getScore(TROPHIES).setScore(1);

        refreshData(balance, trophies, board);

        return board;
    }

    /**
     * Update the content of the specified side scoreboard
     *
     * @param balance    New balance to show
     * @param trophies   New trophies to show
     * @param scoreboard Scoreboard to be updated
     *
     * @since 3.1.4
     */
    public static void refreshData(@Nonnull final Balance balance, final int trophies, @Nonnull final Scoreboard scoreboard) {
        scoreboard.getTeam("Gems").setSuffix(String.valueOf(balance.getGems()));
        scoreboard.getTeam("Gold").setSuffix(String.valueOf(balance.getGold()));
        scoreboard.getTeam("Elixir").setSuffix(String.valueOf(balance.getElixir()));
        scoreboard.getTeam("Trophies").setSuffix(String.valueOf(trophies));
    }
}