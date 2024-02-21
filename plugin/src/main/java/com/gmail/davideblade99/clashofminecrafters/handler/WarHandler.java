/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.ArcherTower;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidLostEvent;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
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
     * If the war has to start, it indicates the time remaining in the countdown, otherwise it indicates the remaining
     * duration of the war
     */
    private final AtomicInteger timer;

    /** Map associating the villages under attack with its attacker and the task id that counts the time of the raid */
    private final Map<Player, Pair<Village, Integer>> attacks;

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
     * This method handles starting the raid for the player passed as a parameter. Specifically, it deals with:
     * <ol>
     *      <li>finding an enemy village to attack</li>
     *      <li>teleporting the player to the target village</li>
     *      <li>spawn the guardian and archer (if the defender owns the archer's tower)</li>
     * </ol>
     * If no island is found after 10 seconds, the search is aborted.
     *
     * @param attacker Player who wants to start the raid
     *
     * @throws IllegalStateException If an unexpected error occurs with the cache and player data is not found
     */
    public void startRaid(@Nonnull final Player attacker) {
        //TODO: se non ci sono villaggi nemiche non cominciare neanche a cercare
        new IslandFinder(attacker, villageFound -> {
            if (villageFound == null) {
                MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.ISLAND_NOT_AVAILABLE));
                return;
            }

            final User defender = plugin.getUser(villageFound.owner);
            if (defender == null) // Unexpected missing data
                throw new IllegalStateException("User of the player \"" + villageFound.owner + "\" was not found");

            // Task that ends the raid after time runs out
            final int taskID = Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getPluginManager().callEvent(new RaidLostEvent(attacker, defender)), plugin.getConfig().getRaidTimeout() * 20L).getTaskId();
            setUnderAttack(attacker, villageFound, taskID);

            // Spawn guardian
            plugin.getBuildingTroopRegistry().createGuardian(defender, attacker);

            final ArcherTower archerTower = defender.getArcherTower();
            if (archerTower != null) // If the player has archer tower
                plugin.getBuildingTroopRegistry().createArcher(defender, attacker);

            // Teleport player
            MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.TELEPORTATION));
            attacker.teleport(villageFound.getSpawn());
        });
    }

    public boolean isUnderAttack(@Nonnull final Village village) {
        return getAttacker(village) != null;
    }

    /**
     * Removes the village attacked by the player passed as a parameter from the list of those under attack
     *
     * @param attacker Player who is attacking the village
     */
    public void removeUnderAttack(@Nonnull final Player attacker) {
        final Pair<Village, Integer> removed = attacks.remove(attacker);

        if (removed != null)
            Bukkit.getScheduler().cancelTask(removed.getValue());
    }

    private void setUnderAttack(@Nonnull final Player attacker, @Nonnull final Village village, final int taskID) {
        attacks.put(attacker, new Pair<>(village, taskID));
    }

    @Nullable
    public Player getAttacker(@Nonnull final Village village) {
        for (Map.Entry<Player, Pair<Village, Integer>> entry : attacks.entrySet()) {
            if (entry.getValue().getKey().equals(village))
                return entry.getKey();
        }
        return null;
    }

    public boolean isAttacking(@Nonnull final Player player) {
        return attacks.containsKey(player);
    }

    /**
     * Gets the village attacked by the specified player
     *
     * @param attacker Attacking player
     *
     * @return The attacked village or {@code null}, if not found
     * @since 3.2.2
     */
    @Nullable
    public Village getAttackedVillage(@Nonnull final Player attacker) {
        final Pair<Village, Integer> pair = attacks.get(attacker);
        return pair == null ? null : pair.getKey();
    }

    /**
     * Remove all islands under attack and teleport attacker to spawn. This method must be called from the main thread since
     * it uses the Spigot API, which is not thread-safe.
     *
     * @throws IllegalStateException If the method is not called from the main thread
     */
    public void removeAllIslandsUnderAttack(@Nonnull final String reason) {
        if (attacks.isEmpty())
            return;

        for (Map.Entry<Player, Pair<Village, Integer>> entry : attacks.entrySet()) {
            final Player attacker = entry.getKey();
            final Village village = entry.getValue().getKey();
            final int taskID = entry.getValue().getValue();

            Bukkit.getScheduler().cancelTask(taskID);

            attacker.teleport(plugin.getConfig().getSpawn());
            MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.RAID_CANCELLED, reason));

            plugin.getBuildingTroopRegistry().removeTroops(plugin.getUser(village.owner));
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

                    MessageUtil.broadcast(Messages.getMessage(MessageKey.WAR_FINISHED));
                } else // War started
                {
                    // Start clan war
                    timer.set(DURATION);
                    currentState.set(WarState.STARTED);
                    MessageUtil.broadcast(Messages.getMessage(MessageKey.WAR_STARTED));
                }
            } else // Countdown messages
            {
                if (currentState.get() == WarState.PREPARATION) {
                    if (timer.get() <= 10) {
                        final String word = Messages.getMessage(timer.get() == 1 ? MessageKey.SECOND : MessageKey.SECONDS);

                        MessageUtil.broadcast(Messages.getMessage(MessageKey.STARTING_WAR, Integer.toString(timer.get()), word));
                    }
                } else if (currentState.get() == WarState.STARTED) {
                    if (timer.get() <= 10) {
                        final String word = Messages.getMessage(timer.get() == 1 ? MessageKey.SECOND : MessageKey.SECONDS);

                        MessageUtil.broadcast(Messages.getMessage(MessageKey.ENDING_WAR, Integer.toString(timer.get()), word));
                    }
                }

                timer.decrementAndGet(); // Decrease countdown
            }
        }
    }

    /**
     * Task that deals with finding an enemy island to attack. Sends the result of the lookup to the callback passed to the
     * constructor {@link #IslandFinder(Player, NullableCallback)}. If no island is found in a reasonable time {@code null}
     * is returned, otherwise the {@link Village} found.
     * <p>
     * The result is passed synchronously, that is, {@link NullableCallback#call(Object)} is called from the main thread.
     */
    private class IslandFinder extends BukkitRunnable {

        private final Player attacker;
        private final NullableCallback<Village> callback;

        public IslandFinder(@Nonnull final Player attacker, @Nonnull final NullableCallback<Village> callback) {
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
                final Village villageFound = supplySync(() -> {
                    // Choose a target village (of a player in a different clan)
                    return plugin.getDatabase().getRandomEnemyIsland(plugin.getUser(attacker).getClanName());
                });
                if (villageFound == null)
                    break; // Village not found

                // getUser() is not thread-safe
                final User targetUser = supplySync(() -> plugin.getUser(villageFound.owner));
                if (targetUser == null)
                    throw new IllegalStateException("User of the player \"" + villageFound.owner + "\" was not found");

                // If the village owner is online or the village is already under attack
                if (targetUser.getBase().isOnline() || isUnderAttack(villageFound))
                    continue;

                // Village found
                Bukkit.getScheduler().runTask(plugin, () -> callback.call(villageFound)); // Go back on main thread
                return;
            }

            // Village not found
            Bukkit.getScheduler().runTask(plugin, () -> callback.call(null)); // Go back on main thread
        }

        /**
         * Blocking method that is responsible for executing in the main thread the function passed as a parameter and
         * handling checked exceptions ({@link Exception}) that may occur. Specifically, in case of exceptions, it will send
         * a notification to the player and print the stack trace.
         *
         * @param toExecute Function to be executed on the main thread
         * @param <T>       Type of data returned by the function (executed on the main thread)
         *
         * @return The data produced by the function or {@code null} in case of error
         * @see #runSync(Supplier)
         */
        @Nullable
        private <T> T supplySync(@Nonnull final Supplier<T> toExecute) {
            final CompletableFuture<T> future = runSync(toExecute);

            try {
                return future.get();
            } catch (final Exception e) {
                MessageUtil.sendError(attacker, "An error occurred while searching for the island to attack. Contact an administrator.");
                e.printStackTrace();
                return null;
            }
        }

        /**
         * This method is responsible to execute on the main thread the function passed as a parameter. The future completes
         * when the function returns and produces its result.
         *
         * @param toExecute Function to be executed on the main thread
         * @param <T>       Type of data returned by the function (executed on the main thread)
         *
         * @return A {@link CompletableFuture<T>} that completes successfully when the function ends its execution. It
         * completes exceptionally  if an unexpected error occurs.
         */
        @Nonnull
        private <T> CompletableFuture<T> runSync(@Nonnull final Supplier<T> toExecute) {
            final CompletableFuture<T> future = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(plugin, () -> future.complete(toExecute.get()));
            return future;
        }
    }
}