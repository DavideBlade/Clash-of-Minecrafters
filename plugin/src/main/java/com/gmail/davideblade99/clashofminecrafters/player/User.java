/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.configuration.Config;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.exception.WorldBorderReachedException;
import com.gmail.davideblade99.clashofminecrafters.island.Island;
import com.gmail.davideblade99.clashofminecrafters.island.building.Building;
import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.island.building.Extractor;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.type.bean.UserDatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ScoreboardUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.util.number.IntegerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

//TODO: prevenire gli switch sbagliati (usati per le currency): https://towardsdatascience.com/dont-be-a-basic-coder-and-use-5-possibilities-to-avoid-the-bad-switch-case-c92402f4061
public final class User {

    private final CoM plugin;
    private final AtomicReference<OfflinePlayer> base; // Thread-safe field
    private int gold;
    private int elixir;
    private int gems;
    private int trophies;
    private String clanName;
    private int elixirExtractorLevel;
    private int goldExtractorLevel;
    private int archerLevel;
    private Vector archerLoc;
    private Island island;
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

            final Config config = plugin.getConfig();
            this.gold = config.getStartingGold();
            this.elixir = config.getStartingElixir();
            this.gems = config.getStartingGems();
            this.townHallLevel = 1; // The base level of the town hall is 1

            // Store default values in the database
            database.storeUser(uuid, this);
        } else {
            final UserDatabaseType userDatabaseType = database.fetchUser(uuid); //TODO: questo fetch può essere usato al posto di hasPlayedBefore (se è null non ha mai giocato)

            this.gold = userDatabaseType.gold;
            this.elixir = userDatabaseType.elixir;
            this.gems = userDatabaseType.gems;
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

    public boolean hasMoneyToUpgrade(final int nextLevel, @Nonnull final BuildingType type) {
        final Building nextBuilding;

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

        return getBalance(nextBuilding.currency) >= nextBuilding.price;
    }

    public int getBalance(@Nonnull final Currency type) {
        switch (type) {
            case GEMS:
                return gems;
            case GOLD:
                return gold;
            case ELIXIR:
                return elixir;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public int addBalance(final int amount, @Nonnull final Currency type) {
        final int currentBalance = getBalance(type);
        final int newBalance = IntegerUtil.saturatedAdd(currentBalance, amount); // Avoid overflow

        setBalance(newBalance, type);

        final MessageKey word;
        switch (type) {
            case GEMS:
                word = amount == 1 ? MessageKey.GEM : MessageKey.GEMS;
                break;
            case GOLD:
                word = MessageKey.GOLD;
                break;
            case ELIXIR:
                word = MessageKey.ELIXIR;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        if (getBase() instanceof Player) {
            ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ADDED_TO_BALANCE, String.valueOf(newBalance - currentBalance), Messages.getMessage(word)));

            if (newBalance == Integer.MAX_VALUE)
                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.MAX_MONEY));
        }

        return newBalance;
    }

    public int removeBalance(final int amount, @Nonnull final Currency type) {
        final int currentBalance = getBalance(type);
        final int newBalance = Math.max(IntegerUtil.saturatedSub(currentBalance, amount), 0);

        setBalance(newBalance, type);

        final MessageKey word;
        switch (type) {
            case GEMS:
                word = amount == 1 ? MessageKey.GEM : MessageKey.GEMS;
                break;
            case GOLD:
                word = MessageKey.GOLD;
                break;
            case ELIXIR:
                word = MessageKey.ELIXIR;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        if (getBase() instanceof Player)
            ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.REMOVED_FROM_BALANCE, String.valueOf(currentBalance - newBalance), Messages.getMessage(word)));

        return newBalance;
    }

    private void setBalance(final int amount, @Nonnull final Currency currency) {
        switch (currency) {
            case GEMS:
                gems = amount;
                break;
            case GOLD:
                gold = amount;
                break;
            case ELIXIR:
                elixir = amount;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currency);
        }

        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);

        if (getBase() instanceof Player)
            ScoreboardUtil.refreshData(gems, gold, elixir, trophies, ((Player) getBase()).getScoreboard());
    }

    public void addTrophies(final int amount) {
        final int currentTrophies = trophies;
        final int newAmount = IntegerUtil.saturatedAdd(currentTrophies, amount); // Avoid overflow

        setTrophies(IntegerUtil.saturatedAdd(trophies, amount)); // Avoid overflow

        if (getBase() instanceof Player) {
            ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ADDED_TROPHIES, String.valueOf(newAmount - currentTrophies))); //TODO: plurale e singolare di "trofei"

            if (trophies == Integer.MAX_VALUE) // If trophies reach the upper limit
                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.MAX_TROPHIES));
        }
    }

    public void removeTrophies(final int amount) {
        final int currentTrophies = trophies;
        final int newAmount = Math.max(IntegerUtil.saturatedSub(trophies, amount), 0);

        setTrophies(newAmount);

        if (getBase() instanceof Player)
            ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.REMOVED_TROPHIES, String.valueOf(currentTrophies - newAmount))); //TODO: plurale e singolare di "trofei"
    }

    private void setTrophies(final int trophies) {
        this.trophies = trophies;

        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);

        if (getBase() instanceof Player)
            ScoreboardUtil.refreshData(gems, gold, elixir, trophies, ((Player) getBase()).getScoreboard());
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

        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);
    }

    public void removeClan() {
        setClanName(null);
    }

    public boolean hasBuilding(@Nonnull final BuildingType type) {
        return getBuildingLevel(type) > 0; // If the level is 0 it means that the player has not unlocked the building
    }

    /**
     * Gets the level unlocked by the player for the specified building
     *
     * @param type Type of building of which to obtain the level
     *
     * @return the building level or 0 if the player did not buy the building or some error has occurred
     */
    public int getBuildingLevel(@Nonnull final BuildingType type) {
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

    @Nullable
    public Building getBuilding(@Nonnull final BuildingType type) {
        final int currentLevel = getBuildingLevel(type);
        if (currentLevel == 0) // If the level is 0 it means that it does not have the building
            return null;

        return plugin.getConfig().getBuilding(type, currentLevel);
    }

    public void upgradeBuilding(@Nonnull final BuildingType type) {
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
                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.MAX_LEVEL_REACHED, replacement));
            return;
        }

        final Building nextBuilding = plugin.getConfig().getBuilding(type, nextLevel);
        final int price = nextBuilding.price;
        final Currency currency = nextBuilding.currency;

        if (!hasMoneyToUpgrade(nextLevel, type)) {
            if (getBase() instanceof Player) {
                final String currencyTranslation;
                switch (currency) {
                    case GEMS:
                        currencyTranslation = Messages.getMessage(MessageKey.GEMS);
                        break;
                    case GOLD:
                        currencyTranslation = Messages.getMessage(MessageKey.GOLD);
                        break;
                    case ELIXIR:
                        currencyTranslation = Messages.getMessage(MessageKey.ELIXIR);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currency);
                }

                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            }
            return;
        }

        removeBalance(price, currency);
        setBuildingLevel(nextLevel, type);

        if (getBase() instanceof Player)
            ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.UPGRADE_COMPLETED, replacement, String.valueOf(nextLevel)));
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
    private void setBuildingLevel(final int level, @Nonnull final BuildingType type) {
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

        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);
    }

    @Nullable
    public Location getTowerLoc() {
        return archerLoc == null ? null : new Location(Bukkit.getWorld("Islands"), archerLoc.getX(), archerLoc.getY(), archerLoc.getZ());
    }

    @Nullable
    public Vector getTowerPos() {
        return archerLoc;
    }

    public void setArcherPos(@Nonnull final Vector position) {
        archerLoc = position;

        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);
    }

    @Nullable
    public Island getIsland() {
        return island;
    }

    public void createIsland() {
        try {
            this.island = plugin.getIslandHandler().generateIsland(getBase());

            plugin.getDatabase().storeUser(getBase().getUniqueId(), this);

            if (getBase() instanceof Player) {
                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.TELEPORTATION));

                island.teleportToSpawn((Player) getBase());
            }
        } catch (final PastingException ignored) {
            if (getBase() instanceof Player)
                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ISLAND_CREATION_ERROR));
        } catch (final WorldBorderReachedException ignored) {
            if (getBase() instanceof Player)
                ChatUtil.sendMessage((Player) getBase(), Messages.getMessage(MessageKey.ISLAND_WORLD_LIMIT_REACHED));
        }
    }

    public void setIslandSpawn(@Nonnull final Location loc) {
        island.spawn = loc;

        // Save changes to the database
        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);
    }

    @Nullable
    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }

    /**
     * Collects all resources in the extractors unlocked by the player
     */
    public void collectExtractors() {
        final Extractor goldExtractor = (Extractor) this.getBuilding(BuildingType.GOLD_EXTRACTOR);
        final Extractor elixirExtractor = (Extractor) this.getBuilding(BuildingType.ELIXIR_EXTRACTOR);

        if (goldExtractor != null) // If the player bought the gold extractor
            this.addBalance(getResourcesProduced(goldExtractor, this.collectionTime), Currency.GOLD);
        if (elixirExtractor != null) // If the player bought the elixir extractor
            this.addBalance(getResourcesProduced(elixirExtractor, this.collectionTime), Currency.ELIXIR);

        // Update collection time
        this.collectionTime = LocalDateTime.now();

        // Save changes to the database
        plugin.getDatabase().storeUser(getBase().getUniqueId(), this);
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
    public int getResourcesProduced(@Nonnull final Extractor extractor, @Nonnull final LocalDateTime collectionTime) {
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
                ", gold=" + gold +
                ", elixir=" + elixir +
                ", gems=" + gems +
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
