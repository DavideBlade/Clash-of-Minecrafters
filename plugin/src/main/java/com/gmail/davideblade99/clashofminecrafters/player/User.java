/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.ArcherTower;
import com.gmail.davideblade99.clashofminecrafters.building.ElixirExtractor;
import com.gmail.davideblade99.clashofminecrafters.building.GoldExtractor;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.exception.WorldBorderReachedException;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Balance;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.setting.ExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.Settings;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.type.bean.UserDatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ScoreboardUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.util.number.IntegerUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class User {

    private final CoM plugin;
    private final AtomicReference<OfflinePlayer> base; // Thread-safe field
    private final Balance balance;
    private int trophies;
    private String clanName;
    private ElixirExtractor elixirExtractor;
    private GoldExtractor goldExtractor;
    private ArcherTower archerTower;
    private Village village;
    private LocalDateTime collectionTime;
    private int townHallLevel;

    /**
     * Create a new user from the {@code base} player. If he has already played on the server, the data will be automatically
     * retrieved from the database, otherwise they will be generated.
     *
     * @param plugin Plugin instance
     * @param base   Core player
     *
     * @throws RuntimeException         If an error occurs with the database
     * @throws IllegalArgumentException If the level of the buildings are negative or the level of the town hall is less than
     *                                  1
     * @throws IllegalStateException    If the base player has never entered the server: you are creating a user of a
     *                                  non-existent player, whose files in the world folder do not even exist
     */
    public User(@Nonnull final CoM plugin, @Nonnull final OfflinePlayer base) {
        this.plugin = plugin;
        this.base = new AtomicReference<>(base);


        final UUID uuid = base.getUniqueId();
        final PlayerDatabase database = plugin.getDatabase();
        if (!database.hasPlayedBefore(uuid)) {
            if (!(base instanceof Player))
                throw new IllegalStateException("Unable to create data for player who has never been on the server!");

            final Settings config = plugin.getConfig();
            this.balance = new Balance(config.getStartingGold(), config.getStartingElixir(), config.getStartingGems());

            // Store default values in the database
            updateDatabase();
        } else {
            final UserDatabaseType userDatabaseType = database.fetchUser(uuid); //TODO: questo fetch può essere usato al posto di hasPlayedBefore (se è null non ha mai giocato)

            this.balance = userDatabaseType.balance;
            this.trophies = userDatabaseType.trophies;
            this.clanName = userDatabaseType.clanName;
            this.elixirExtractor = userDatabaseType.elixirExtractor;
            this.goldExtractor = userDatabaseType.goldExtractor;
            this.archerTower = userDatabaseType.archerTower;
            this.village = userDatabaseType.island;
            this.collectionTime = userDatabaseType.collectionTime;
            this.townHallLevel = userDatabaseType.townHallLevel;

            // Validate town hall level
            if (townHallLevel < 0)
                throw new IllegalArgumentException("Invalid town hall level: must be greater than or equal to 0.");
        }
    }

    @Nonnull
    public OfflinePlayer getBase() {
        return base.get();
    }

    public void setBasePlayer(@Nonnull final OfflinePlayer player) {
        this.base.set(player);
    }

    /**
     * @return The {@link Balance} of the user
     */
    public Balance getBalance() {
        return balance;
    }

    /**
     * @param currency Currency of which to obtain the balance
     *
     * @return The player's balance for the specified currency
     */
    public int getBalance(@Nonnull final Currencies currency) {
        return balance.getBalance(currency);
    }

    /**
     * @return The amount of elixir the player owns
     */
    public int getElixir() {
        return balance.getBalance(Currencies.ELIXIR);
    }

    /**
     * @return The amount of elixir the player owns
     */
    public int getGold() {
        return balance.getBalance(Currencies.GOLD);
    }

    /**
     * @return The amount of elixir the player owns
     */
    public int getGems() {
        return balance.getBalance(Currencies.GEMS);
    }

    public int addBalance(final int amount, @Nonnull final Currencies currency) {
        final int currentBalance = balance.getBalance(currency);
        final int currencyAdded = balance.addCurrency(currency, amount);
        final int newBalance = currentBalance + currencyAdded;

        updateDatabase();
        refreshScoreboard();

        if (getBase() instanceof Player) {
            final String currencyTranslation = balance.getCurrencyTranslation(currency);

            MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ADDED_TO_BALANCE, String.valueOf(currencyAdded), currencyTranslation));

            if (newBalance == currency.getMax())
                MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.MAX_MONEY));
        }

        return currencyAdded;
    }

    public int removeBalance(final int amount, @Nonnull final Currencies currency) {
        final int currencyRemoved = balance.removeCurrency(currency, amount);

        updateDatabase();
        refreshScoreboard();

        if (getBase() instanceof Player) {
            final String currencyTranslation = balance.getCurrencyTranslation(currency);

            MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.REMOVED_FROM_BALANCE, String.valueOf(currencyRemoved), currencyTranslation));
        }

        return currencyRemoved;
    }

    /**
     * Updates the data in the player's scoreboard if he is an online player
     *
     * @since 3.1.4
     */
    private void refreshScoreboard() {
        if (getBase() instanceof Player)
            ScoreboardUtil.refreshData(balance, trophies, ((Player) getBase()).getScoreboard());
    }

    /**
     * Update the database with new player data
     *
     * @since 3.2
     */
    public void updateDatabase() {
        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);
    }

    public void addTrophies(final int amount) {
        final int currentTrophies = trophies;
        final int newAmount = IntegerUtil.saturatedAdd(currentTrophies, amount); // Avoid overflow

        setTrophies(IntegerUtil.saturatedAdd(trophies, amount)); // Avoid overflow

        if (getBase() instanceof Player) {
            MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ADDED_TROPHIES, String.valueOf(newAmount - currentTrophies))); //TODO: plurale e singolare di "trofei"

            if (trophies == Integer.MAX_VALUE) // If trophies reach the upper limit
                MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.MAX_TROPHIES));
        }
    }

    public void removeTrophies(final int amount) {
        final int currentTrophies = trophies;
        final int newAmount = Math.max(IntegerUtil.saturatedSub(trophies, amount), 0);

        setTrophies(newAmount);

        if (getBase() instanceof Player)
            MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.REMOVED_TROPHIES, String.valueOf(currentTrophies - newAmount))); //TODO: plurale e singolare di "trofei"
    }

    private void setTrophies(final int trophies) {
        this.trophies = trophies;

        updateDatabase();
        refreshScoreboard();
    }

    public int getTrophies() {
        return trophies;
    }

    @Nullable
    public String getClanName() {
        return clanName;
    }

    public void setClanName(@Nullable final String clanName) {
        this.clanName = clanName;

        updateDatabase();
    }

    public void removeClan() {
        setClanName(null);
    }

    /**
     * Checks whether the user owns any level of the archer's tower
     *
     * @return True if the user purchased the building, otherwise false
     * @since 3.2
     */
    public boolean hasUnlockedArcherTower() {
        return archerTower != null;
    }

    /**
     * Checks whether the user owns any level of the gold extractor
     *
     * @return True if the user purchased the building, otherwise false
     * @since 3.2
     */
    public boolean hasUnlockedGoldExtractor() {
        return goldExtractor != null;
    }

    /**
     * Checks whether the user owns any level of the elixir extractor
     *
     * @return True if the user purchased the building, otherwise false
     * @since 3.2
     */
    public boolean hasUnlockedElixirExtractor() {
        return elixirExtractor != null;
    }

    /**
     * Checks whether the player has unlocked at least one type of extractor
     *
     * @return True if the player owns at least one extractor, otherwise false
     * @since 3.1.2
     */
    public boolean hasExtractor() {
        return hasUnlockedGoldExtractor() || hasUnlockedElixirExtractor();
    }

    /**
     * Gets the level of the town hall unlocked by the player
     *
     * @return the building level or 0 if the player did not buy the building
     * @since 3.2
     */
    public int getTownHallLevel() {
        return townHallLevel;
    }

    /**
     * Sets the specified level for the town hall
     *
     * @param level New level to set, greater than or equal to 0
     *
     * @throws IllegalArgumentException If the level is negative
     */
    public void setTownHallLevel(final int level) {
        if (level < 0)
            throw new IllegalArgumentException("'" + level + "' is not a valid town hall level: it must be greater than or equal to 0");

        this.townHallLevel = level;
    }

    /**
     * @return {@link ElixirExtractor} owned by the player or {@code} if the player has never purchased one
     * @since 3.2
     */
    @Nullable
    public ElixirExtractor getElixirExtractor() {
        return elixirExtractor;
    }

    /**
     * Creates a new level 1 elixir extractor that replaces the old one and saves it in the database
     *
     * @param corner1 Any corner of the building
     * @param corner2 Corner of building opposite to {@code corner1}
     *
     * @since 3.2
     */
    public void newElixirExtractor(@Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        this.elixirExtractor = new ElixirExtractor(1, corner1, corner2);

        // Start collecting resources from now
        this.collectionTime = LocalDateTime.now();

        updateDatabase();
    }

    /**
     * @return {@link GoldExtractor} owned by the player or {@code} if the player has never purchased one
     * @since 3.2
     */
    @Nullable
    public GoldExtractor getGoldExtractor() {
        return goldExtractor;
    }

    /**
     * Creates a new level 1 gold extractor that replaces the old one and saves it in the database
     *
     * @param corner1 Any corner of the building
     * @param corner2 Corner of building opposite to {@code corner1}
     *
     * @since 3.2
     */
    public void newGoldExtractor(@Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        this.goldExtractor = new GoldExtractor(1, corner1, corner2);

        // Start collecting resources from now
        this.collectionTime = LocalDateTime.now();

        updateDatabase();
    }

    /**
     * @return {@link ArcherTower} owned by the player or {@code} if the player has never purchased one
     * @since 3.2
     */
    @Nullable
    public ArcherTower getArcherTower() {
        return archerTower;
    }

    /**
     * Creates a new level 1 archer tower that replaces the old one and saves it in the database
     *
     * @param corner1 Any corner of the building
     * @param corner2 Corner of building opposite to {@code corner1}
     *
     * @since 3.2
     */
    public void newArcherTower(@Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        this.archerTower = new ArcherTower(1, corner1, corner2);

        updateDatabase();
    }

    @Nullable
    public Village getVillage() {
        return village;
    }

    public void createIsland() {
        plugin.getVillageHandler().generateVillage(getBase(), result -> {
            final Village village = result.getKey();
            final Exception exception = result.getValue();

            if (exception != null) {
                if (getBase() instanceof Player) {
                    if (exception instanceof PastingException)
                        MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ISLAND_CREATION_ERROR));
                    if (exception instanceof WorldBorderReachedException)
                        MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ISLAND_WORLD_LIMIT_REACHED));
                }
                return;
            }

            User.this.village = village;

            updateDatabase();

            if (getBase() instanceof Player) {
                MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.TELEPORTATION));

                this.village.teleportToSpawn((Player) getBase());
            }
        });
    }

    /**
     * Sets a new spawn point for the player's village
     *
     * @param loc New spawn point to be set
     *
     * @return True if the location was safe and the new spawn could be set, otherwise false
     * @since 3.1.4
     */
    public boolean setIslandSpawn(@Nonnull final Location loc) {
        final boolean result = village.setSpawn(loc);

        // Save changes to the database
        updateDatabase();

        return result;
    }

    @Nullable
    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }

    /**
     * Collects all resources in the extractors unlocked by the player
     */
    public void collectExtractors() {
        if (!hasExtractor())
            return;

        if (hasUnlockedGoldExtractor()) // If the player bought the gold extractor
            /*
             * If any player has a level higher than the current maximum level
             * (e.g., some levels have been removed from the config.yml),
             * the currently configured maximum level is taken into account.
             */
            this.addBalance(getResourcesProduced(plugin.getConfig().getExistingGoldExtractor(goldExtractor.getLevel()), this.collectionTime), Currencies.GOLD);

        if (hasUnlockedElixirExtractor()) // If the player bought the elixir extractor
            /*
             * If any player has a level higher than the current maximum level
             * (e.g., some levels have been removed from the config.yml),
             * the currently configured maximum level is taken into account.
             */
            this.addBalance(getResourcesProduced(plugin.getConfig().getExistingElixirExtractor(elixirExtractor.getLevel()), this.collectionTime), Currencies.ELIXIR);

        // Update collection time
        this.collectionTime = LocalDateTime.now();

        // Save changes to the database
        updateDatabase();
    }

    /**
     * Calculates the resources produced by the specified extractor since the last time the user collected. If the amount
     * produced exceeds the capacity of the extractor, the capacity will be returned.
     *
     * @param extractor      Extractor that produces the resources to be calculated
     * @param collectionTime Timestamp of the last time the player collected from the extractor
     *
     * @return the amount of resources produced (accumulated in the extractor)
     */
    public int getResourcesProduced(@Nonnull final ExtractorLevel extractor, @Nonnull final LocalDateTime collectionTime) {
        /*
         * Calculate the hours that have passed since the last time the player collected.
         * For the minutes that are left (less than 60), I calculate the approximate production.
         * In this way the minimum number of resources is lost due to the approximation of the division
         * (necessary since I do not have production per minute but per hour).
         *
         * Example: the player collected 16 hours and 50 minutes ago. Hourly production is 100.
         * 16*100 = 1600
         * 50 * (100/60) = 50 * 1.6666666666666667 = 83.33333333333333
         * 1600 + 83.33333333333333 = 1683.33333. This is the total production till now
         * 1683 will be collected and 0.3 units will be lost
         */
        final LocalDateTime currentTime = LocalDateTime.now();
        final long hours = collectionTime.until(currentTime, ChronoUnit.HOURS);
        final long minutes = collectionTime.plusHours(hours).until(currentTime, ChronoUnit.MINUTES);

        final long produced = hours * extractor.production + (int) (minutes * (extractor.production / 60D));

        return Math.min(extractor.capacity, (int) produced); // The maximum the extractor can contain is its maximum capacity
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof User))
            return false;
        else {
            final User user = (User) obj;
            return getBase().getUniqueId().equals(user.getBase().getUniqueId());
        }
    }

    @Override
    public int hashCode() {
        return getBase().getUniqueId().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + getBase().getUniqueId() +
                ", gold=" + balance.getGold() +
                ", elixir=" + balance.getElixir() +
                ", gems=" + balance.getGems() +
                ", trophies=" + trophies +
                ", clan='" + clanName + "'" +
                ", elixir extractor=" + elixirExtractor +
                ", gold extractor=" + goldExtractor +
                ", archer tower=" + archerTower +
                ", island=" + village +
                ", collection time=" + collectionTime +
                ", town hall level=" + townHallLevel +
                '}';
    }
}
