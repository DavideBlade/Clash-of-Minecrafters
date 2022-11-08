/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.clan;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidLostEvent;
import com.gmail.davideblade99.clashofminecrafters.island.Island;
import com.gmail.davideblade99.clashofminecrafters.island.building.ArcherTower;
import com.gmail.davideblade99.clashofminecrafters.island.building.Building;
import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Class that thread-safe handles clan wars
 */
public final class WarHandler {

    private final static short COUNTDOWN = 3600; // 1 hour = 3600 seconds
    private final static short DURATION = COUNTDOWN;

    private final CoM plugin;

    /** War status */
    private final AtomicReference<WarState> currentState;

    /**
     * If the war has to start, it indicates the time remaining in the countdown, otherwise it indicates the
     * remaining duration of the war
     */
    private final AtomicInteger timer;

    /** Map associating the islands under attack with its attacker and the task id that counts the time of the raid */
    private final Map<Player, Pair<Island, Integer>> attacks;

    public WarHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.currentState = new AtomicReference<>(WarState.PREPARATION);
        this.timer = new AtomicInteger(COUNTDOWN);
        this.attacks = new ConcurrentHashMap<>();

        new WarCountdown().runTaskTimerAsynchronously(plugin, 0, 20); // Start task and repeat it every second
    }

    public boolean isStarted() {
        return currentState.get() == WarState.STARTED;
    }

    public int getTimeToStart() {
        return currentState.get() == WarState.STARTED ? 0 : timer.get();
    }

    /**
     * This method handles starting the raid for the player passed as a parameter. Specifically, it deals with: 1.
     * finding an enemy island to attack 2. teleporting the player to the target island 3. spawn the guardian and
     * archer (if the defender owns the archer tower) If no island is found after 10 seconds, the search is
     * aborted.
     *
     * @param attacker Player who wants to start the raid
     */
    public void startRaid(@Nonnull final Player attacker) {
        //TODO: se non ci sono isole nemiche non cominciare neanche a cercare
        new IslandFinder(attacker, targetIsland -> {
            if (targetIsland == null) {
                ChatUtil.sendMessage(attacker, Messages.getMessage(MessageKey.ISLAND_NOT_AVAILABLE));
                return;
            }

            // Unexpected missing data
            final User targetUser = plugin.getUser(targetIsland.owner);
            if (targetUser == null)
                throw new IllegalStateException("The User of the player \"" + targetIsland.owner + "\" was not found");

            // Task that ends the raid after time runs out
            final int taskID = Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getPluginManager().callEvent(new RaidLostEvent(attacker, targetIsland.owner)), plugin.getConfig().getRaidTimeout() * 20L).getTaskId();
            setUnderAttack(attacker, targetIsland, taskID);

            // Spawn creature
            plugin.getGuardianHandler().spawn(targetIsland.owner, targetIsland.spawn);

            final Building archerTower = targetUser.getBuilding(BuildingType.ARCHER_TOWER);
            if (archerTower != null) // If the player has archer tower
                plugin.getArcherHandler().spawn(targetIsland.owner, ((ArcherTower) archerTower).damage, targetUser.getTowerLoc());

            // Teleport player
            ChatUtil.sendMessage(attacker, Messages.getMessage(MessageKey.TELEPORTATION));
            attacker.teleport(targetIsland.spawn);
        });
    }

    public boolean isUnderAttack(@Nonnull final Island i) {
        return getAttacker(i) != null;
    }

    /**
     * Removes the island attacked by the player passed as a parameter from the list of those under attack
     *
     * @param attacker Player who is attacking the island
     */
    public void removeUnderAttack(@Nonnull final Player attacker) {
        final Pair<Island, Integer> removed = attacks.remove(attacker);

        if (removed != null)
            Bukkit.getScheduler().cancelTask(removed.getValue());
    }

    private void setUnderAttack(@Nonnull final Player attacker, @Nonnull final Island i, final int taskID) {
        attacks.put(attacker, new Pair<>(i, taskID));
    }

    @Nullable
    public Player getAttacker(@Nonnull final Island i) {
        for (Map.Entry<Player, Pair<Island, Integer>> entry : attacks.entrySet()) {
            if (entry.getValue().getKey().equals(i))
                return entry.getKey();
        }
        return null;
    }

    public boolean isAttacking(@Nonnull final Player player) {
        return attacks.containsKey(player);
    }

    /**
     * Get island attacked by specified player
     */
    @Nullable
    public Island getAttackedIsland(@Nonnull final Player attacker) {
        final Pair<Island, Integer> pair = attacks.get(attacker);
        return pair == null ? null : pair.getKey();
    }

    /**
     * Remove all islands under attack and teleport attacker to spawn. This method must be called from the main
     * thread since it uses the Spigot API, which is not thread-safe.
     *
     * @throws IllegalStateException If the method is not called from the main thread
     */
    public void removeAllIslandsUnderAttack(@Nonnull final String reason) {
        if (attacks.isEmpty())
            return;

        for (Map.Entry<Player, Pair<Island, Integer>> entry : attacks.entrySet()) {
            final Player attacker = entry.getKey();
            final Island island = entry.getValue().getKey();
            final int taskID = entry.getValue().getValue();

            Bukkit.getScheduler().cancelTask(taskID);

            attacker.teleport(plugin.getConfig().getSpawn());
            ChatUtil.sendMessage(attacker, Messages.getMessage(MessageKey.RAID_CANCELLED, reason));

            plugin.getGuardianHandler().kill(island.owner);
            plugin.getArcherHandler().kill(island.owner);
        }
        attacks.clear();
    }

    private enum WarState {
        PREPARATION, STARTED
    }

    /**
     * Thread managing the countdown and phases of the clan war
     */
    private class WarCountdown extends BukkitRunnable {
        //TODO: i messaggi (classe Messages) non sarebbero proprio thread-safe (nessuno li modifica, ma potenzialmente si possono modificare; ci vorrebbe la classe ImmutableYamlConfiguration)
        @Override
        public void run() {
            if (timer.get() == 0) {

                if (currentState.get() == WarState.STARTED) // War ended
                {
                    // Stop the war and start the countdown again
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            removeAllIslandsUnderAttack(Messages.getMessage(MessageKey.WAR_ENDED));
                        }
                    });

                    timer.set(COUNTDOWN);
                    currentState.set(WarState.PREPARATION);

                    ChatUtil.broadcast(Messages.getMessage(MessageKey.WAR_FINISHED));
                } else // War started
                {
                    // Start clan war
                    timer.set(DURATION);
                    currentState.set(WarState.STARTED);
                    ChatUtil.broadcast(Messages.getMessage(MessageKey.WAR_STARTED));
                }
            } else // Countdown messages
            {
                if (currentState.get() == WarState.PREPARATION) {
                    if (timer.get() <= 10) {
                        final String word = Messages.getMessage(timer.get() == 1 ? MessageKey.SECOND : MessageKey.SECONDS);

                        ChatUtil.broadcast(Messages.getMessage(MessageKey.STARTING_WAR, Integer.toString(timer.get()), word));
                    }
                } else if (currentState.get() == WarState.STARTED) {
                    if (timer.get() <= 10) {
                        final String word = Messages.getMessage(timer.get() == 1 ? MessageKey.SECOND : MessageKey.SECONDS);

                        ChatUtil.broadcast(Messages.getMessage(MessageKey.ENDING_WAR, Integer.toString(timer.get()), word));
                    }
                }

                timer.decrementAndGet(); // Decrease countdown
            }
        }
    }

    /**
     * Task that deals with finding an enemy island to attack. Sends the result of the lookup to the callback
     * passed to the constructor {@link #IslandFinder(Player, NullableCallback)}. If no island is found in a
     * reasonable time {@code null} is returned, otherwise the {@link Island} found.
     *
     * The result is passed synchronously, that is, {@link NullableCallback#call(Object)} is called from the main
     * thread.
     */
    private class IslandFinder extends BukkitRunnable {

        private final Player attacker;
        private final NullableCallback<Island> callback;

        public IslandFinder(@Nonnull final Player attacker, @Nonnull final NullableCallback<Island> callback) {
            this.attacker = attacker;
            this.callback = callback;

            this.runTaskAsynchronously(plugin);
        }

        @Override
        public void run() {
            final long end = System.currentTimeMillis() + (10 * 1000);
            while (System.currentTimeMillis() <= end) // After 10 seconds break the loop
            {
                // Database and getUser() are not thread-safe
                final Island targetIsland = supplySync(() -> {
                    // Choose a target island (of a player in a different clan)
                    return plugin.getDatabase().getRandomEnemyIsland(plugin.getUser(attacker).getClanName());
                });
                if (targetIsland == null)
                    break; // Island not found

                // getUser() is not thread-safe
                final User targetUser = supplySync(() -> plugin.getUser(targetIsland.owner));
                if (targetUser == null)
                    throw new IllegalStateException("The User of the player \"" + targetIsland.owner + "\" was not found");

                // If the player is online or the island is already under attack
                if (targetUser.getBase().isOnline() || isUnderAttack(targetIsland))
                    continue;

                // Island found
                Bukkit.getScheduler().runTask(plugin, () -> callback.call(targetIsland)); // Go back on main thread
                return;
            }

            // Island not found
            Bukkit.getScheduler().runTask(plugin, () -> callback.call(null)); // Go back on main thread
        }

        /**
         * Blocking method that is responsible for executing in the main thread the function passed as a parameter
         * and handling checked exceptions ({@link Exception}) that may occur. Specifically, in case of exceptions,
         * it will send a notification to the player and print the stack trace.
         *
         * @param toExecute Function to be executed on the main thread
         * @param <T>       Type of data returned by the function (executed on the main thread)
         *
         * @return The data produced by the function or {@code null} in case of error
         *
         * @see #runSync(Supplier)
         */
        @Nullable
        private <T> T supplySync(@Nonnull final Supplier<T> toExecute) {
            final CompletableFuture<T> future = runSync(toExecute);

            try {
                return future.get();
            } catch (final Exception e) {
                ChatUtil.sendMessage(attacker, "§cAn error occurred while searching for the island to attack. Contact an administrator.");
                e.printStackTrace();
                return null;
            }
        }

        /**
         * This method is responsible to execute on the main thread the function passed as a parameter. The future
         * completes when the function returns and produces its result.
         *
         * @param toExecute Function to be executed on the main thread
         * @param <T>       Type of data returned by the function (executed on the main thread)
         *
         * @return A {@link CompletableFuture<T>} that completes successfully when the function ends its execution.
         * It completes exceptionally  if an unexpected error occurs.
         */
        @Nonnull
        private <T> CompletableFuture<T> runSync(@Nonnull final Supplier<T> toExecute) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(plugin, () -> future.complete(toExecute.get()));
            return future;
        }
    }
}