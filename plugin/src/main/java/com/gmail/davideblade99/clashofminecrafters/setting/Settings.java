/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import com.gmail.davideblade99.clashofminecrafters.message.Messages.Language;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.*;
import com.gmail.davideblade99.clashofminecrafters.setting.section.*;
import com.gmail.davideblade99.clashofminecrafters.storage.DatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

//TODO: mi serve avere tutto in memoria? Le cose (pesanti, quindi non di certo un int o un boolean) che leggo 1 volta ogni mai posso andare a leggerle direttamente
// la YamlConfiguration è già di per sè un posto dove memorizzare la roba; non c'è bisogno di rimemorizzarla con singoli campi
//TODO: forse conviene che questa diventi la classe di Parsing, e poi ci sia una classe Settings o ConfigCache in cui ci sono tutti i campi (field) -> almeno la mappa in YamlConfiguration la svuoto
public final class Settings extends CoMYamlConfiguration {

    private final CoM plugin;

    /*
     * General settings
     */
    private final Language lang;
    private final byte maxExpansions;
    private final Location spawn;
    private final Pair<Pair<Integer, Currencies>, Integer> raidRewards; // <<Amount, Currency>, Trophies>
    private final Pair<Pair<Integer, Currencies>, Integer> raidPenalty; // <<Amount, Currency>, Trophies>
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
    private final List<TownHallSettings> townHalls; // List containing various levels of town halls

    /*
     * Structure settings
     */
    private final List<GoldExtractorSettings> goldExtractors; // List containing various levels of gold extractors
    private final List<ElixirExtractorSettings> elixirExtractors; // List containing various levels of elixir extractors
    private final List<ArcherTowerSettings> archerTowers; // List containing various levels of archer towers

    /*
     * Clan settings
     */
    private final List<ClanSettings> clans; // // List containing various levels of clans

    public Settings(@Nonnull final CoM plugin) {
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
        final String[] defaultSpawn = super.getString("Default spawn.Location", "").split(",");
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
        Currencies rewardCurrency = Currencies.matchCurrency(super.getString("Raid rewards.Currency", null));
        if (rewardCurrency == null) {
            rewardCurrency = Currencies.GEMS;

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
        Currencies penaltyCurrency = Currencies.matchCurrency(super.getString("Raid failure.Currency", null));
        if (penaltyCurrency == null) {
            penaltyCurrency = Currencies.GEMS;

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
        this.townHalls = new TownHallConfiguration(this).getTownHalls();

        /*
         * Structure settings
         */
        this.goldExtractors = new GoldExtractorConfiguration(this).getGoldExtractors();
        this.elixirExtractors = new ElixirExtractorConfiguration(this).getElixirExtractors();
        this.archerTowers = new ArcherTowerConfiguration(this).getArcherTowers();

        /*
         * Clan settings
         */
        this.clans = new ClanConfiguration(this).getClans();
    }

    @Nonnull
    public Language getLang() {
        return lang;
    }

    public byte getMaxExpansions() {
        return maxExpansions;
    }

    /**
     * @return True if the player must be teleported to the spawn when joining the server, otherwise false
     *
     * @since v3.1.1
     */
    public boolean teleportOnJoin() {
        return super.getBoolean("Default spawn.On join", false);
    }

    @Nonnull
    public Location getSpawn() {
        return spawn;
    }

    @Nonnull
    public Pair<Pair<Integer, Currencies>, Integer> getRaidRewards() {
        return raidRewards;
    }

    @Nonnull
    public Pair<Pair<Integer, Currencies>, Integer> getRaidPenalty() {
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
     * @return {@link TownHallSettings} corresponding to the specified level or {@code null} if the specified level
     * does not exist
     *
     * @since v3.1
     */
    @Nullable
    public TownHallSettings getTownHall(final int level) {
        final int index = level - 2;

        return index >= townHalls.size() ? null : townHalls.get(index);
    }

    /**
     * @param level Gold extractor level
     *
     * @return {@link GoldExtractorSettings} corresponding to the specified level or {@code null} if the specified
     * level does not exist
     *
     * @since v3.1
     */
    @Nullable
    public GoldExtractorSettings getGoldExtractor(final int level) {
        final int index = level - 1;

        return index >= goldExtractors.size() ? null : goldExtractors.get(index);
    }

    /**
     * @param level Elixir extractor level
     *
     * @return {@link ElixirExtractorSettings} corresponding to the specified level or {@code null} if the
     * specified level does not exist
     *
     * @since v3.1
     */
    @Nullable
    public ElixirExtractorSettings getElixirExtractor(final int level) {
        final int index = level - 1;

        return index >= elixirExtractors.size() ? null : elixirExtractors.get(index);
    }

    /**
     * @param level Archer tower level
     *
     * @return {@link ArcherTowerSettings} corresponding to the specified level or {@code null} if the specified
     * level does not exist
     *
     * @since v3.1
     */
    @Nullable
    public ArcherTowerSettings getArcherTower(final int level) {
        final int index = level - 1;

        return index >= archerTowers.size() ? null : archerTowers.get(index);
    }

    /**
     * @param building Building type
     *
     * @return the maximum configured level of the specified building
     */
    public int getMaxLevel(@Nonnull final Buildings building) {
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
    public boolean isBuildingEnabled(@Nonnull final Buildings building) {
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
        for (Buildings type : Buildings.values())
            if (isBuildingEnabled(type))
                return true;
        return false;
    }

    /**
     * @param type  Type of building to obtain
     * @param level Building level in which you are interested
     *
     * @return {@link BuildingSettings} corresponding to the specified level or {@code null} if the specified level
     * does not exist
     *
     * @see #getArcherTower(int)
     * @see #getGoldExtractor(int)
     * @see #getElixirExtractor(int)
     * @see #getTownHall(int)
     */
    @Nullable
    public BuildingSettings getBuilding(@Nonnull final Buildings type, final int level) {
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
        return clans.get(level - 1).expRequired;
    }

    /**
     * @param level Level of which you want to know the command to execute
     *
     * @return The command to execute when a clan reaches level passed as parameter or {@code null} if no command
     * has to be executed
     */
    @Nullable
    public String getClanCommand(final int level) {
        return clans.get(level - 1).command;
    }

    /**
     * @return The total number of levels of the clans
     */
    public int getClanLevels() {
        return clans.size();
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
