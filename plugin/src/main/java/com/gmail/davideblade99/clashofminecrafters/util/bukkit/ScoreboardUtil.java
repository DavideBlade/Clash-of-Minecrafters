/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

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

    @Nonnull
    public static Scoreboard createScoreboard(final int gems, final int gold, final int elixir, final int trophies) {
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

        refreshData(gems, gold, elixir, trophies, board);

        return board;
    }

    // Update scoreboard content
    public static void refreshData(final int gems, final int gold, final int elixir, final int trophies, @Nonnull final Scoreboard scoreboard) {
        scoreboard.getTeam("Gems").setSuffix(String.valueOf(gems));
        scoreboard.getTeam("Gold").setSuffix(String.valueOf(gold));
        scoreboard.getTeam("Elixir").setSuffix(String.valueOf(elixir));
        scoreboard.getTeam("Trophies").setSuffix(String.valueOf(trophies));
    }
}