/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.exception.WorldBorderReachedException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Balance;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.setting.Settings;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.BuildingSettings;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.ExtractorSettings;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.type.bean.UserDatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ScoreboardUtil;
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
    private int elixirExtractorLevel;
    private int goldExtractorLevel;
    private int archerLevel;
    private Vector archerLoc;
    private Village island;
    private LocalDateTime collectionTime;
    private int townHallLevel;

    /**
     * Create a new user from the {@code base} player. If he has already played on the server, the data will be
     * automatically retrieved from the database, otherwise they will be generated.
     *
     * @param plugin Plugin instance
     * @param base   Core player
     *
     * @throws RuntimeException         If an error occurs with the database
     * @throws IllegalArgumentException If the level of the buildings are negative or the level of the town hall is
     *                                  less than 1
     * @throws IllegalStateException    If the base player has never entered the server: you are creating a user of
     *                                  a non-existent player, whose files in the world folder do not even exist
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
            this.townHallLevel = 1; // The base level of the town hall is 1

            // Store default values in the database
            updateDatabase();
        } else {
            final UserDatabaseType userDatabaseType = database.fetchUser(uuid); //TODO: questo fetch può essere usato al posto di hasPlayedBefore (se è null non ha mai giocato)

            this.balance = userDatabaseType.balance;
            this.trophies = userDatabaseType.trophies;
            this.clanName = userDatabaseType.clanName;
            this.elixirExtractorLevel = userDatabaseType.elixirExtractorLevel;
            this.goldExtractorLevel = userDatabaseType.goldExtractorLevel;
            this.archerLevel = userDatabaseType.archerTowerLevel;
            this.archerLoc = userDatabaseType.archerTowerLoc;
            this.island = userDatabaseType.island;
            this.collectionTime = userDatabaseType.collectionTime;
            this.townHallLevel = userDatabaseType.townHallLevel;

            // Valiate levels
            if (elixirExtractorLevel < 0 || goldExtractorLevel < 0 || archerLevel < 0)
                throw new IllegalArgumentException("Invalid building level: must be greater than or equal to 0.");
            if (townHallLevel < 1)
                throw new IllegalArgumentException("Invalid town hall level: must be greater than or equal to 1.");
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
     * Checks whether the player has sufficient balance to upgrade a building
     *
     * @param nextLevel Next level to purchase
     * @param type      Type of building to upgrade
     *
     * @return True if the player can afford it, otherwise false
     *
     * @throws IllegalArgumentException If the specified level does not exist for the passed building type
     */
    public boolean hasMoneyToUpgrade(final int nextLevel, @Nonnull final Buildings type) {
        final BuildingSettings nextBuilding;

        switch (type) {
            case ELIXIR_EXTRACTOR:
                nextBuilding = plugin.getConfig().getElixirExtractor(nextLevel);
                break;
            case GOLD_EXTRACTOR:
                nextBuilding = plugin.getConfig().getGoldExtractor(nextLevel);
                break;
            case ARCHER_TOWER:
                nextBuilding = plugin.getConfig().getArcherTower(nextLevel);
                break;
            case TOWN_HALL:
                nextBuilding = plugin.getConfig().getTownHall(nextLevel);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        if (nextBuilding == null)
            throw new IllegalArgumentException("The \"" + nextLevel + "\" level of the building \"" + type + "\" does not exist");

        return getBalance(nextBuilding.currency) >= nextBuilding.price;
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
     * @since v3.1.4
     */
    private void refreshScoreboard() {
        if (getBase() instanceof Player)
            ScoreboardUtil.refreshData(balance, trophies, ((Player) getBase()).getScoreboard());
    }

    /**
     * Update the database with new player data
     *
     * @since v3.1.4
     */
    private void updateDatabase() {
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

    public boolean hasBuilding(@Nonnull final Buildings type) {
        return getBuildingLevel(type) > 0; // If the level is 0 it means that the player has not unlocked the building
    }

    /**
     * Checks whether the player has unlocked at least one type of extractor
     *
     * @return True if the player owns at least one extractor, otherwise false
     *
     * @since v3.1.2
     */
    public boolean hasExtractor() {
        return getBuilding(Buildings.GOLD_EXTRACTOR) != null || getBuilding(Buildings.ELIXIR_EXTRACTOR) != null;
    }

    /**
     * Gets the level unlocked by the player for the specified building
     *
     * @param type Type of building of which to obtain the level
     *
     * @return the building level or 0 if the player did not buy the building or some error has occurred
     */
    public int getBuildingLevel(@Nonnull final Buildings type) {
        switch (type) {
            case ARCHER_TOWER:
                return archerLevel;
            case GOLD_EXTRACTOR:
                return goldExtractorLevel;
            case ELIXIR_EXTRACTOR:
                return elixirExtractorLevel;
            case TOWN_HALL:
                return townHallLevel;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * @param type Type of construction to obtain
     *
     * @return the {@link BuildingSettings} corresponding to the level the player has unlocked or {@code null} if
     * the player has not unlocked the building or if the level is not found among the configured ones (this can
     * happen if, for example, there is a configuration error or if levels have been deleted)
     */
    @Nullable
    public BuildingSettings getBuilding(@Nonnull final Buildings type) {
        final int currentLevel = getBuildingLevel(type);

        // If the level is 1, it means that the player has the basic level of the town hall (never upgraded it)
        if (type == Buildings.TOWN_HALL && currentLevel == 1)
            return null;
        // If the level is 0 it means that the player does not have the building (never unlocked)
        if (type != Buildings.TOWN_HALL && currentLevel == 0)
            return null;

        return plugin.getConfig().getBuilding(type, currentLevel);
    }

    public void upgradeBuilding(@Nonnull final Buildings type) {
        final int nextLevel = getBuildingLevel(type) + 1;

        final String replacement;
        switch (type) {
            case ARCHER_TOWER:
                replacement = Messages.getMessage(MessageKey.ARCHER_TOWER);
                break;
            case GOLD_EXTRACTOR:
                replacement = Messages.getMessage(MessageKey.GOLD_EXTRACTOR);
                break;
            case ELIXIR_EXTRACTOR:
                replacement = Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR);
                break;
            case TOWN_HALL:
                replacement = Messages.getMessage(MessageKey.TOWN_HALL);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        // Max level reached
        if (nextLevel > plugin.getConfig().getMaxLevel(type)) {
            if (getBase() instanceof Player)
                MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.MAX_LEVEL_REACHED, replacement));
            return;
        }

        final BuildingSettings nextBuilding = plugin.getConfig().getBuilding(type, nextLevel);
        final int price = nextBuilding.price;
        final Currencies currency = nextBuilding.currency;

        if (!hasMoneyToUpgrade(nextLevel, type)) {
            if (getBase() instanceof Player) {
                final String currencyTranslation = balance.getCurrencyTranslation(currency);

                MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            }
            return;
        }

        removeBalance(price, currency);
        setBuildingLevel(nextLevel, type);

        if (getBase() instanceof Player)
            MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.UPGRADE_COMPLETED, replacement, String.valueOf(nextLevel)));
    }

    /**
     * Set the level of the specified building
     *
     * @param level New level to set, greater than or equal to 0
     * @param type  Type of building which level is to be set
     *
     * @throws IllegalArgumentException If the level is negative
     * @throws IllegalStateException    If the building type does not exist
     */
    private void setBuildingLevel(final int level, @Nonnull final Buildings type) {
        if (level < 0)
            throw new IllegalArgumentException("Level must be greater than or equal to 0");

        switch (type) {
            case ARCHER_TOWER:
                archerLevel = level;
                break;
            case GOLD_EXTRACTOR:
                goldExtractorLevel = level;
                break;
            case ELIXIR_EXTRACTOR:
                elixirExtractorLevel = level;
                break;
            case TOWN_HALL:
                townHallLevel = level;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        updateDatabase();
    }

    @Nullable
    public Location getTowerLoc() {
        return archerLoc == null ? null : new Location(plugin.getVillageHandler().getVillageWorld(), archerLoc.getX(), archerLoc.getY(), archerLoc.getZ());
    }

    @Nullable
    public Vector getTowerPos() {
        return archerLoc;
    }

    public void setArcherPos(@Nonnull final Vector position) {
        archerLoc = position;

        updateDatabase();
    }

    @Nullable
    public Village getIsland() {
        return island;
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

            User.this.island = village;

            updateDatabase();

            if (getBase() instanceof Player) {
                MessageUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.TELEPORTATION));

                island.teleportToSpawn((Player) getBase());
            }
        });
    }

    public void setIslandSpawn(@Nonnull final Location loc) {
        island.spawn = loc;

        // Save changes to the database
        updateDatabase();
    }

    @Nullable
    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }

    /**
     * Collects all resources in the extractors unlocked by the player
     */
    public void collectExtractors() {
        final ExtractorSettings goldExtractor = (ExtractorSettings) this.getBuilding(Buildings.GOLD_EXTRACTOR);
        final ExtractorSettings elixirExtractor = (ExtractorSettings) this.getBuilding(Buildings.ELIXIR_EXTRACTOR);

        if (goldExtractor != null) // If the player bought the gold extractor
            this.addBalance(getResourcesProduced(goldExtractor, this.collectionTime), Currencies.GOLD);
        if (elixirExtractor != null) // If the player bought the elixir extractor
            this.addBalance(getResourcesProduced(elixirExtractor, this.collectionTime), Currencies.ELIXIR);

        // Update collection time
        this.collectionTime = LocalDateTime.now();

        // Save changes to the database
        updateDatabase();
    }

    /**
     * Calculates the resources produced by the specified extractor since the last time the user collected. If the
     * amount produced exceeds the capacity of the extractor, the capacity will be returned.
     *
     * @param extractor      Extractor that produces the resources to be calculated
     * @param collectionTime Timestamp of the last time the player collected from the extractor
     *
     * @return the amount of resources produced (accumulated in the extractor)
     */
    public int getResourcesProduced(@Nonnull final ExtractorSettings extractor, @Nonnull final LocalDateTime collectionTime) {
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
                ", elixir extractor level=" + elixirExtractorLevel +
                ", gold extractor level=" + goldExtractorLevel +
                ", archer level=" + archerLevel +
                ", archer location=" + archerLoc +
                ", island=" + island +
                ", collection time=" + collectionTime +
                ", town hall level=" + townHallLevel +
                '}';
    }
}
