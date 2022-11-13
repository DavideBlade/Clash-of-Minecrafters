/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.setting.section.MenuConfiguration;
import com.gmail.davideblade99.clashofminecrafters.island.building.*;
import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import com.gmail.davideblade99.clashofminecrafters.message.Messages.Language;
import com.gmail.davideblade99.clashofminecrafters.storage.DatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

//TODO: mi serve avere tutto in memoria? Le cose (pesanti, quindi non di certo un int o un boolean) che leggo 1 volta ogni mai posso andare a leggerle direttamente
// la YamlConfiguration è già di per sè un posto dove memorizzare la roba; non c'è bisogno di rimemorizzarla con singoli campi
//TODO: forse conviene che questa diventi la classe di Parsing, e poi ci sia una classe Settings o ConfigCache in cui ci sono tutti i campi (field) -> almeno la mappa in YamlConfiguration la svuoto
public final class Config extends CoMYamlConfiguration {

    private final CoM plugin;

    /*
     * General settings
     */
    private final Language lang;
    private final byte maxExpansions;
    private final Location spawn;
    private final Pair<Pair<Integer, Currency>, Integer> raidRewards; // <<Amount, Currency>, Trophies>
    private final Pair<Pair<Integer, Currency>, Integer> raidPenalty; // <<Amount, Currency>, Trophies>
    private final int raidTimeout;
    private final int clanRaidRewards;
    private final int maxPlayerPerClan;
    private final int startingGold;
    private final int startingElixir;
    private final int startingGems;
    private final int maxPlayerCacheCount;
    private final DatabaseType databaseType;

    /*
     * Menu settings
     */
    private final Map<String, Menu> menus;

    /*
     * Town hall settings
     */
    private final List<TownHall> townHalls; // List containing various levels of town halls

    /*
     * Structure settings
     */
    private final List<GoldExtractor> goldExtractors; // List containing various levels of gold extractors
    private final List<ElixirExtractor> elixirExtractors; // List containing various levels of elixir extractors
    private final List<ArcherTower> archerTowers; // List containing various levels of archer towers

    /*
     * Clan settings
     */
    private final List<Pair<Integer, String>> clans; // <Exp required, Command>

    public Config(@Nonnull final CoM plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));

        this.plugin = plugin;

        loadDefaultValue();

        //TODO: a volte stampo che il numero non può essere negativo anche se in realtà magari non c'è alcun valore settato (ho messo io come valore di default un numero negativo)
        /*
         * Checks
         */

        // Check the language
        Language locale = Language.matchLanguage(super.getString("Locale", null));
        if (locale == null) {
            MessageUtil.sendError("The language specified in the \"Locale\" line (in the config) does not exist.");
            MessageUtil.sendError("The default language (English) will be used.");

            locale = Language.EN;
        }


        // Check the number of players per clan
        int maxPlayerPerClan = super.getInt("Max players per clan", -1);
        if (maxPlayerPerClan < 0) {
            maxPlayerPerClan = 15;

            MessageUtil.sendError("The number specified in the \"Max players per clan\" line (in the config) is incorrect.");
            MessageUtil.sendError("The default value (" + maxPlayerPerClan + ") will be used.");
        }


        // Check the number of players per clan
        int clanRaidRewards = super.getInt("Exp for raid", -1);
        if (clanRaidRewards < 0) {
            clanRaidRewards = 2;

            MessageUtil.sendError("The number specified in the \"Exp for raid\" line (in the config) is incorrect.");
            MessageUtil.sendError("The default value (" + clanRaidRewards + ") will be used.");
        }


        // Check the number of expansions
        byte maxExpansions = super.getByte("Max expansions", (byte) -1);
        if (maxExpansions < 0 || maxExpansions > 120) {
            maxExpansions = 8;

            MessageUtil.sendError("The number specified in the \"Max expansions\" line (in the config) is incorrect.");
            MessageUtil.sendError("The default value (" + maxExpansions + ") will be used.");
        }


        // Check if the spawn location is valid
        Location spawn;
        final String[] defaultSpawn = super.getString("Default spawn", "").split(",");
        if (defaultSpawn.length != 4) {
            spawn = Bukkit.getWorlds().get(0).getSpawnLocation();

            MessageUtil.sendError("The \"Default spawn\" line (in the config) is not valid.");
            MessageUtil.sendError("The spawn point will be set to that of the default world: " + BukkitLocationUtil.toString(spawn));
        } else {
            // Check if the world set exists
            World spawnWorld = Bukkit.getWorld(defaultSpawn[0].trim());
            if (spawnWorld == null) {
                spawnWorld = Bukkit.getWorlds().get(0); // Default world

                MessageUtil.sendError("The world in the \"Default spawn\" line (in the config) doesn't exist.");
                MessageUtil.sendError("Spawn will be set in the default world \"" + spawnWorld + "\".");
            }

            // Check if the coordinates are valid numbers
            try {
                spawn = new Location(spawnWorld, Double.parseDouble(defaultSpawn[1]), Double.parseDouble(defaultSpawn[2]), Double.parseDouble(defaultSpawn[3]));
            } catch (final RuntimeException ignored) {
                spawn = spawnWorld.getSpawnLocation();

                MessageUtil.sendError("Coordinates in the \"Default spawn\" line (in the config) are not valid numbers.");
                MessageUtil.sendError("The spawn will be set to the world's default spawn point: " + BukkitLocationUtil.toString(spawn));
            }

            // If the spawn is outside the borders of the world
            if (!spawnWorld.getWorldBorder().isInside(spawn)) {
                spawn = spawnWorld.getSpawnLocation();

                MessageUtil.sendError("Coordinates in the \"Default spawn\" line (in the config) exceed the borders of the world.");
                MessageUtil.sendError("The spawn will be set to the world's default spawn point: " + BukkitLocationUtil.toString(spawn));
            }
        }


        // Check raid rewards
        Currency rewardCurrency = Currency.matchCurrency(super.getString("Raid rewards.Currency", null));
        if (rewardCurrency == null) {
            rewardCurrency = Currency.GEMS;

            MessageUtil.sendError("The currency specified in the \"Raid rewards\" section (in the config) does not exist.");
            MessageUtil.sendError(rewardCurrency + " will be used as currency.");
        }
        int rewardAmount = super.getInt("Raid rewards.Amount", -1);
        if (rewardAmount < 0) {
            rewardAmount = 0;

            MessageUtil.sendError("The amount of the rewards in the \"Raid rewards\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + rewardAmount + "\" will be used as replacement value.");
        }
        int rewardTrophies = super.getInt("Raid rewards.Trophies won", -1);
        if (rewardTrophies < 0) {
            rewardTrophies = 0;

            MessageUtil.sendError("The number of trophies in the \"Raid rewards\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + rewardTrophies + "\" will be used as replacement value.");
        }


        // Check raid penalty
        Currency penaltyCurrency = Currency.matchCurrency(super.getString("Raid failure.Currency", null));
        if (penaltyCurrency == null) {
            penaltyCurrency = Currency.GEMS;

            MessageUtil.sendError("The currency specified in the \"Raid failure\" section (in the config) does not exist.");
            MessageUtil.sendError(penaltyCurrency + " will be used as currency.");
        }
        int penaltyAmount = super.getInt("Raid failure.Amount", -1);
        if (penaltyAmount < 0) {
            penaltyAmount = 0;

            MessageUtil.sendError("The amount of the penalty in the \"Raid failure\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + penaltyAmount + "\" will be used as replacement value.");
        }
        int penaltyTrophies = super.getInt("Raid failure.Trophies lost", -1);
        if (penaltyTrophies < 0) {
            penaltyTrophies = 0;

            MessageUtil.sendError("The number of penalty in the \"Raid failure\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + penaltyTrophies + "\" will be used as replacement value.");
        }


        // Check raid timeout
        int raidTimeout = super.getInt("Raid timeout", -1);
        if (raidTimeout < 0) {
            raidTimeout = 180;

            MessageUtil.sendError("The duration of the raid in the \"Raid timeout\" line (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + raidTimeout + "\" will be used as replacement value.");
        }


        // Check the starting balances
        int startingGold = super.getInt("Starting balance.Gold");
        if (startingGold < 0) {
            startingGold = 0;

            MessageUtil.sendError("The gold in the \"Starting balance\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + startingGold + "\" will be used as replacement value.");
        }
        int startingElixir = super.getInt("Starting balance.Elixir");
        if (startingElixir < 0) {
            startingElixir = 0;

            MessageUtil.sendError("The elixir in the \"Starting balance\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + startingElixir + "\" will be used as replacement value.");
        }
        int startingGems = super.getInt("Starting balance.Gems");
        if (startingGems < 0) {
            startingGems = 0;

            MessageUtil.sendError("The gems in the \"Starting balance\" section (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + startingGems + "\" will be used as replacement value.");
        }


        int maxPlayerCacheCount = super.getInt("Max player cache");
        if (maxPlayerCacheCount < 0) {
            final long maxAllocatedMemory = Runtime.getRuntime().maxMemory(); // Parameter -Xmx, in bytes
            final long count = maxAllocatedMemory / 1024 / 200; // Dividing by 200 means that for every 200Kb allocated, 1 player can be cached
            if (count <= Integer.MAX_VALUE)
                maxPlayerCacheCount = (int) count;
            else
                maxPlayerCacheCount = 1500;

            MessageUtil.sendError("The number specified in the \"Max player cache\" line (in the config) cannot be negative!");
            MessageUtil.sendError("\"" + maxPlayerCacheCount + "\" will be used as replacement value.");
        }


        // Global settings
        this.lang = locale;
        this.maxExpansions = maxExpansions;
        this.spawn = spawn;
        this.raidRewards = new Pair<>(new Pair<>(rewardAmount, rewardCurrency), rewardTrophies);
        this.raidPenalty = new Pair<>(new Pair<>(penaltyAmount, penaltyCurrency), penaltyTrophies);
        this.raidTimeout = raidTimeout;
        this.clanRaidRewards = clanRaidRewards;
        this.maxPlayerPerClan = maxPlayerPerClan;
        this.startingGold = startingGold;
        this.startingElixir = startingElixir;
        this.startingGems = startingGems;
        this.maxPlayerCacheCount = maxPlayerCacheCount;
        this.databaseType = super.getBoolean("MySQL.Enabled", false) ? DatabaseType.MYSQL : DatabaseType.YAML;


        /*
         * Menu settings
         */
        this.menus = new MenuConfiguration(this).getMenus();

        /*
         * Town hall settings
         */
        this.townHalls = loadTownHallSettings();

        /*
         * Structure settings
         */
        this.goldExtractors = loadGoldExtractorSettings();
        this.elixirExtractors = loadElixirExtractorSettings();
        this.archerTowers = loadArcherTowerSettings();

        /*
         * Clan settings
         */
        this.clans = loadClanSettings();
    }

    @Nonnull
    public Language getLang() {
        return lang;
    }

    public byte getMaxExpansions() {
        return maxExpansions;
    }

    @Nonnull
    public Location getSpawn() {
        return spawn;
    }

    @Nonnull
    public Pair<Pair<Integer, Currency>, Integer> getRaidRewards() {
        return raidRewards;
    }

    @Nonnull
    public Pair<Pair<Integer, Currency>, Integer> getRaidPenalty() {
        return raidPenalty;
    }

    public int getRaidTimeout() {
        return raidTimeout;
    }

    public int getClanRaidRewards() {
        return clanRaidRewards;
    }

    public int getMaxPlayerPerClan() {
        return maxPlayerPerClan;
    }

    public boolean isCheckForUpdate() {
        return super.getBoolean("Check for update", true);
    }

    public int getStartingGold() {
        return startingGold;
    }

    public int getStartingElixir() {
        return startingElixir;
    }

    public int getStartingGems() {
        return startingGems;
    }

    public int getMaxPlayerCacheCount() {
        return maxPlayerCacheCount;
    }

    public boolean useIslandSchematic() {
        return super.getBoolean("Island schematic", false);
    }

    public boolean useElixirExtractorSchematic() {
        return super.getBoolean("Elixir extractor schematic", false);
    }

    public boolean useGoldExtractorSchematic() {
        return super.getBoolean("Gold extractor schematic", false);
    }

    public boolean useArcherSchematic() {
        return super.getBoolean("Archer schematic", false);
    }

    /**
     * @return The type of database to use
     */
    @Nonnull
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    @Nonnull
    public String getHost() {
        return super.getString("MySQL.Host", "localhost");
    }

    public int getPort() {
        return super.getInt("MySQL.Port", 3306);
    }

    /**
     * @return The username for the database login
     */
    @Nonnull
    public String getUsername() {
        return super.getString("MySQL.Username", "root");
    }

    /**
     * @return The password for the database login
     */
    @Nonnull
    public String getPassword() {
        return super.getString("MySQL.Password", "");
    }

    /**
     * @return The name of the database in which to store data
     */
    @Nonnull
    public String getDatabase() {
        return super.getString("MySQL.Database", "CoM");
    }

    @Nullable
    public Menu getMenu(@Nonnull final String menuName) {
        return menus.get(menuName.toLowerCase());
    }

    /**
     * @param level Town hall level
     *
     * @return {@link TownHall} corresponding to the specified level or {@code null} if the specified level does
     * not exist
     *
     * @since v3.1
     */
    @Nullable
    public TownHall getTownHall(final int level) {
        final int index = level - 2;

        return index >= townHalls.size() ? null : townHalls.get(index);
    }

    /**
     * @param level Gold extractor level
     *
     * @return {@link GoldExtractor} corresponding to the specified level or {@code null} if the specified level
     * does not exist
     *
     * @since v3.1
     */
    @Nullable
    public GoldExtractor getGoldExtractor(final int level) {
        final int index = level - 1;

        return index >= goldExtractors.size() ? null : goldExtractors.get(index);
    }

    /**
     * @param level Elixir extractor level
     *
     * @return {@link ElixirExtractor} corresponding to the specified level or {@code null} if the specified level
     * does not exist
     *
     * @since v3.1
     */
    @Nullable
    public ElixirExtractor getElixirExtractor(final int level) {
        final int index = level - 1;

        return index >= elixirExtractors.size() ? null : elixirExtractors.get(index);
    }

    /**
     * @param level Archer tower level
     *
     * @return {@link ArcherTower} corresponding to the specified level or {@code null} if the specified level does
     * not exist
     *
     * @since v3.1
     */
    @Nullable
    public ArcherTower getArcherTower(final int level) {
        final int index = level - 1;

        return index >= archerTowers.size() ? null : archerTowers.get(index);
    }

    /**
     * @param building Building type
     *
     * @return the maximum configured level of the specified building
     */
    public int getMaxLevel(@Nonnull final BuildingType building) {
        switch (building) {
            case ARCHER_TOWER:
                return archerTowers.size();
            case GOLD_EXTRACTOR:
                return goldExtractors.size();
            case ELIXIR_EXTRACTOR:
                return elixirExtractors.size();
            case TOWN_HALL:
                return townHalls.size() + 1;

            default:
                throw new IllegalStateException("Unexpected value: " + building);
        }
    }

    /**
     * Check if that type of building is disabled. This occurs when, in the config.yml, the building section is
     * deleted or is misconfigured.
     *
     * @param building Type of the building to be checked
     *
     * @return False if building is disabled, otherwise true
     *
     * @since v3.1
     */
    public boolean isBuildingEnabled(@Nonnull final BuildingType building) {
        switch (building) {
            case ARCHER_TOWER:
                return !archerTowers.isEmpty();
            case GOLD_EXTRACTOR:
                return !goldExtractors.isEmpty();
            case ELIXIR_EXTRACTOR:
                return !elixirExtractors.isEmpty();
            case TOWN_HALL:
                return !townHalls.isEmpty();

            default:
                throw new IllegalStateException("Unexpected value: " + building);
        }
    }

    /**
     * @return True if there is at least one active building, otherwise false
     *
     * @since v3.1
     */
    public boolean anyBuildingEnabled() {
        for (BuildingType type : BuildingType.values())
            if (isBuildingEnabled(type))
                return true;
        return false;
    }

    /**
     * @param type  Type of building to obtain
     * @param level Building level in which you are interested
     *
     * @return {@link Building} corresponding to the specified level or {@code null} if the specified level does
     * not exist
     *
     * @see #getArcherTower(int)
     * @see #getGoldExtractor(int)
     * @see #getElixirExtractor(int)
     * @see #getTownHall(int)
     */
    @Nullable
    public Building getBuilding(@Nonnull final BuildingType type, final int level) {
        switch (type) {
            case ARCHER_TOWER:
                return getArcherTower(level);
            case GOLD_EXTRACTOR:
                return getGoldExtractor(level);
            case ELIXIR_EXTRACTOR:
                return getElixirExtractor(level);
            case TOWN_HALL:
                return getTownHall(level);

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * @param level Level of which you want to know the exp required to gain it
     *
     * @return The exp that a clan must have to gain the specified level
     */
    public int getRequiredClanExp(final int level) {
        return clans.get(level - 1).getKey();
    }

    /**
     * @param level Level of which you want to know the command to execute
     *
     * @return The command to execute when a clan reaches level passed as parameter or {@code null} if no command
     * has to be executed
     */
    @Nullable
    public String getClanCommand(final int level) {
        return clans.get(level - 1).getValue();
    }

    /**
     * @return The total number of levels of the clans
     */
    public int getClanLevels() {
        return clans.size();
    }

    /**
     * Load from the configuration file the settings for the different levels of town halls
     *
     * @return A list containing all loaded levels. It will be empty if any problems have occurred or if no level
     * has been configured.
     *
     * @since v3.1
     */
    @Nonnull
    private List<TownHall> loadTownHallSettings() {
        final ConfigurationSection townHallSection = super.getConfigurationSection("Town halls");
        final Set<String> keys;

        // Check if town hall list is empty
        if (townHallSection == null || (keys = townHallSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The town hall configuration is missing in the config.");
            return Collections.emptyList();
        }


        final List<TownHall> result = new ArrayList<>(keys.size());
        int level = 1;
        for (String townHall : keys) {
            level++;

            final int price = townHallSection.getInt(townHall + ".Price", -1);
            final Currency currency = Currency.matchCurrency(townHallSection.getString(townHall + ".Currency", null));
            final String command = townHallSection.getString(townHall + ".Command", null);
            final byte hearts = (byte) townHallSection.getInt(townHall + ".Guardian.Health", -1); //TODO: differenziare il caso in cui è -1 perché non lo ha configurato (ammesso) o perché ha settato lui -1
            final Material helmet = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Helmet", ""));
            final Material chestplate = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Chestplate", ""));
            final Material leggings = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Leggings", ""));
            final Material boots = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Boots", ""));

            final List<?> effectString = townHallSection.getList(townHall + ".Guardian.Equipment.Potions", null);
            final List<PotionEffectType> potions;
            if (effectString == null)
                potions = null;
            else {
                potions = new ArrayList<>(effectString.size());
                for (Object obj : effectString) {
                    if (!(obj instanceof String))
                        continue;

                    final PotionEffectType potion = PotionEffectType.getByName((String) obj);
                    if (potion != null)
                        potions.add(potion);
                    else
                        MessageUtil.sendWarning("Warning! The potion effect \"" + obj + "\" of level \"" + townHall + "\" town hall (in the config) will be ignored, because it does not exist.");
                }
            }


            if (price < 0) {
                MessageUtil.sendError("The price of level \"" + townHall + "\" town hall (in the config) cannot be negative!");
                MessageUtil.sendError("Town hall levels will be disabled.");
                return Collections.emptyList();
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for town hall \"" + townHall + "\" (in the config) does not exist.");
                MessageUtil.sendError("Town hall levels will be disabled.");
                return Collections.emptyList();
            }

            result.add(new TownHall(level, price, currency, command, hearts, helmet, chestplate, leggings, boots, potions));
        }

        return result;
    }

    /**
     * Load from the configuration file the settings for the different levels of gold extractors
     *
     * @return A list containing all loaded levels. It will be empty if any problems have occurred or if no level
     * has been configured.
     *
     * @since v3.1
     */
    private List<GoldExtractor> loadGoldExtractorSettings() {
        final ConfigurationSection extractorConfig = super.getConfigurationSection("Gold extractors");
        final Set<String> keys;

        // Check if extractor list is empty
        if (extractorConfig == null || (keys = extractorConfig.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The gold extractor configuration is missing in the config.");
            return Collections.emptyList();
        }


        final List<GoldExtractor> result = new ArrayList<>(keys.size());
        int level = 0;
        for (String extractor : keys) {
            level++;

            final int production = extractorConfig.getInt(extractor + ".Production", -1);
            final int capacity = extractorConfig.getInt(extractor + ".Capacity", 0);
            final int price = extractorConfig.getInt(extractor + ".Price", -1);
            final Currency currency = Currency.matchCurrency(extractorConfig.getString(extractor + ".Currency", null));

            if (production < 0) {
                MessageUtil.sendError("The production of level \"" + extractor + "\" gold extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Gold extractor will be disabled.");
                return Collections.emptyList();
            }
            if (capacity < 1) {
                MessageUtil.sendError("The capacity of level \"" + extractor + "\" gold extractor (in the config) must be positive!");
                MessageUtil.sendError("Gold extractor will be disabled.");
                return Collections.emptyList();
            }
            if (price < 0) {
                MessageUtil.sendError("The price of level \"" + extractor + "\" gold extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Gold extractor will be disabled.");
                return Collections.emptyList();
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for gold extractor \"" + extractor + "\" (in the config) does not exist.");
                MessageUtil.sendError("Gold extractor will be disabled.");
                return Collections.emptyList();
            }

            result.add(new GoldExtractor(level, production, capacity, price, currency));
        }

        return result;
    }

    /**
     * Load from the configuration file the settings for the different levels of elixir extractors
     *
     * @return A list containing all loaded levels. It will be empty if any problems have occurred or if no level
     * has been configured.
     *
     * @since v3.1
     */
    private List<ElixirExtractor> loadElixirExtractorSettings() {
        final ConfigurationSection extractorConfig = super.getConfigurationSection("Elixir extractors");
        final Set<String> keys;

        // Check if extractor list is empty
        if (extractorConfig == null || (keys = extractorConfig.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The elixir extractor configuration is missing in the config.");
            return Collections.emptyList();
        }


        final List<ElixirExtractor> result = new ArrayList<>(keys.size());
        int level = 0;
        for (String extractor : keys) {
            level++;

            final int production = extractorConfig.getInt(extractor + ".Production", -1);
            final int capacity = extractorConfig.getInt(extractor + ".Capacity", 0);
            final int price = extractorConfig.getInt(extractor + ".Price", -1);
            final Currency currency = Currency.matchCurrency(extractorConfig.getString(extractor + ".Currency", null));

            if (production < 0) {
                MessageUtil.sendError("The production of level \"" + extractor + "\" elixir extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Elixir extractor will be disabled.");
                return Collections.emptyList();
            }
            if (capacity < 1) {
                MessageUtil.sendError("The capacity of level \"" + extractor + "\" elixir extractor (in the config) must be positive!");
                MessageUtil.sendError("Elixir extractor will be disabled.");
                return Collections.emptyList();
            }
            if (price < 0) {
                MessageUtil.sendError("The price of level \"" + extractor + "\" elixir extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Elixir extractor will be disabled.");
                return Collections.emptyList();
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for elixir extractor \"" + extractor + "\" (in the config) does not exist.");
                MessageUtil.sendError("Elixir extractor will be disabled.");
                return Collections.emptyList();
            }

            result.add(new ElixirExtractor(level, production, capacity, price, currency));
        }

        return result;
    }

    /**
     * Load from the configuration file the settings for the different levels of archer towers
     *
     * @return A list containing all loaded levels. It will be empty if any problems have occurred or if no level
     * has been configured.
     *
     * @since v3.1
     */
    private List<ArcherTower> loadArcherTowerSettings() {
        final ConfigurationSection archerConfig = super.getConfigurationSection("Archer towers");
        final Set<String> keys;

        // Check if archer tower list is empty
        if (archerConfig == null || (keys = archerConfig.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The configuration of the archer towers is missing in the config.");
            return Collections.emptyList();
        }


        final List<ArcherTower> result = new ArrayList<>(keys.size());
        int level = 0;
        for (String archerTower : keys) {
            level++;

            final double damage = archerConfig.getDouble(archerTower + ".Damage", -1);
            final int price = archerConfig.getInt(archerTower + ".Price", -1);
            final Currency currency = Currency.matchCurrency(archerConfig.getString(archerTower + ".Currency", null));

            if (damage < 0) {
                MessageUtil.sendError("The damage of the archer tower \"" + archerTower + "\" (in the config) cannot be negative!");
                MessageUtil.sendError("Archer towers will be disabled.");
                return Collections.emptyList();
            }
            if (price < 0) {
                MessageUtil.sendError("The price for the archer tower \"" + archerTower + "\" (in the config) cannot be negative!");
                MessageUtil.sendError("Archer towers will be disabled.");
                return Collections.emptyList();
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for the archer tower \"" + archerTower + "\" (in the config) does not exist.");
                MessageUtil.sendError("Archer towers will be disabled.");
                return Collections.emptyList();
            }

            result.add(new ArcherTower(level, damage, price, currency));
        }

        return result;
    }

    /**
     * Load from the configuration file the settings for the different levels of clans
     *
     * @return A list containing all loaded levels. It will be empty if any problems have occurred or if no level
     * has been configured.
     *
     * @since v3.1
     */
    private List<Pair<Integer, String>> loadClanSettings() {
        final ConfigurationSection clanConfig = super.getConfigurationSection("Clans");
        final Set<String> keys;

        // Check if clans list is empty
        if (clanConfig == null || (keys = clanConfig.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The configuration of the clans is missing in the config.");
            return Collections.emptyList();
        }

        final List<Pair<Integer, String>> result = new ArrayList<>(keys.size());
        for (String clan : keys) {
            final int expRequired = clanConfig.getInt(clan + ".Exp required", -1);
            final String command = clanConfig.getString(clan + ".Command", null);

            if (expRequired < 0) {
                MessageUtil.sendError("\"Exp required\" of the clan \"" + clan + "\" (in the config) cannot be negative!");
                MessageUtil.sendError("Clan levels will be disabled.");
                return Collections.emptyList();
            }

            result.add(new Pair<>(expRequired, command));
        }

        return result;
    }

    /**
     * Loads into memory, as default values, the values of the config.yml embedded in the .jar
     */
    private void loadDefaultValue() {
        final InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null)
            super.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    @Override
    protected void load() throws Exception {
        if (!file.exists())
            FileUtil.copyFile(file.getName(), file);

        super.load(file);
    }
}
