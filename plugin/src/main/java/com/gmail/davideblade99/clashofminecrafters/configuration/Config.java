/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.configuration;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.island.building.*;
import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import com.gmail.davideblade99.clashofminecrafters.menu.item.BaseItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.ConfigItem;
import com.gmail.davideblade99.clashofminecrafters.message.Messages.Language;
import com.gmail.davideblade99.clashofminecrafters.storage.DatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ColorUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import javafx.util.Pair;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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
    private final Map<String, Menu> menus = new HashMap<>();

    /*
     * Town hall settings
     */
    private final List<TownHall> townHalls = new ArrayList<>(); // List containing various levels of town halls

    /*
     * Structure settings
     */
    private final List<GoldExtractor> goldExtractors = new ArrayList<>(); // List containing various levels of gold extractors
    private final List<ElixirExtractor> elixirExtractors = new ArrayList<>(); // List containing various levels of elixir extractors
    private final List<ArcherTower> archerTowers = new ArrayList<>(); // List containing various levels of archer towers

    /*
     * Clan settings
     */
    private final List<Pair<Integer, String>> clans = new ArrayList<>(); // <Exp required, Command>

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
            ChatUtil.sendMessage("&cThe language specified in the \"Locale\" line (in the config) does not exist.");
            ChatUtil.sendMessage("&cThe default language (English) will be used.");

            locale = Language.EN;
        }


        // Check the number of players per clan
        int maxPlayerPerClan = super.getInt("Max players per clan", -1);
        if (maxPlayerPerClan < 0) {
            maxPlayerPerClan = 15;

            ChatUtil.sendMessage("&cThe number specified in the \"Max players per clan\" line (in the config) is incorrect.");
            ChatUtil.sendMessage("&cThe default value (" + maxPlayerPerClan + ") will be used.");
        }


        // Check the number of players per clan
        int clanRaidRewards = super.getInt("Exp for raid", -1);
        if (clanRaidRewards < 0) {
            clanRaidRewards = 2;

            ChatUtil.sendMessage("&cThe number specified in the \"Exp for raid\" line (in the config) is incorrect.");
            ChatUtil.sendMessage("&cThe default value (" + clanRaidRewards + ") will be used.");
        }


        // Check the number of expansions
        byte maxExpansions = super.getByte("Max expansions", (byte) -1);
        if (maxExpansions < 0 || maxExpansions > 120) {
            maxExpansions = 8;

            ChatUtil.sendMessage("&cThe number specified in the \"Max expansions\" line (in the config) is incorrect.");
            ChatUtil.sendMessage("&cThe default value (" + maxExpansions + ") will be used.");
        }


        // Check if the spawn location is valid
        Location spawn;
        final String[] defaultSpawn = super.getString("Default spawn", "").split(",");
        if (defaultSpawn.length != 4) {
            spawn = Bukkit.getWorlds().get(0).getSpawnLocation();

            ChatUtil.sendMessage("&cThe \"Default spawn\" line (in the config) is not valid.");
            ChatUtil.sendMessage("&cThe spawn point will be set to that of the default world: " + BukkitLocationUtil.toString(spawn));
        } else {
            // Check if the world set exists
            World spawnWorld = Bukkit.getWorld(defaultSpawn[0].trim());
            if (spawnWorld == null) {
                spawnWorld = Bukkit.getWorlds().get(0); // Default world

                ChatUtil.sendMessage("&cThe world in the \"Default spawn\" line (in the config) doesn't exist.");
                ChatUtil.sendMessage("&cSpawn will be set in the default world \"" + spawnWorld + "\".");
            }

            // Check if the coordinates are valid numbers
            try {
                spawn = new Location(spawnWorld, Double.parseDouble(defaultSpawn[1]), Double.parseDouble(defaultSpawn[2]), Double.parseDouble(defaultSpawn[3]));
            } catch (final RuntimeException ignored) {
                spawn = spawnWorld.getSpawnLocation();

                ChatUtil.sendMessage("&cCoordinates in the \"Default spawn\" line (in the config) are not valid numbers.");
                ChatUtil.sendMessage("&cThe spawn will be set to the world's default spawn point: " + BukkitLocationUtil.toString(spawn));
            }

            // If the spawn is outside the borders of the world
            if (!spawnWorld.getWorldBorder().isInside(spawn)) {
                spawn = spawnWorld.getSpawnLocation();

                ChatUtil.sendMessage("&cCoordinates in the \"Default spawn\" line (in the config) exceed the borders of the world.");
                ChatUtil.sendMessage("&cThe spawn will be set to the world's default spawn point: " + BukkitLocationUtil.toString(spawn));
            }
        }


        // Check raid rewards
        Currency rewardCurrency = Currency.matchCurrency(super.getString("Raid rewards.Currency", null));
        if (rewardCurrency == null) {
            rewardCurrency = Currency.GEMS;

            ChatUtil.sendMessage("&cThe currency specified in the \"Raid rewards\" section (in the config) does not exist.");
            ChatUtil.sendMessage("&c" + rewardCurrency + " will be used as currency.");
        }
        int rewardAmount = super.getInt("Raid rewards.Amount", -1);
        if (rewardAmount < 0) {
            rewardAmount = 0;

            ChatUtil.sendMessage("&cThe amount of the rewards in the \"Raid rewards\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + rewardAmount + "\" will be used as replacement value.");
        }
        int rewardTrophies = super.getInt("Raid rewards.Trophies won", -1);
        if (rewardTrophies < 0) {
            rewardTrophies = 0;

            ChatUtil.sendMessage("&cThe number of trophies in the \"Raid rewards\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + rewardTrophies + "\" will be used as replacement value.");
        }


        // Check raid penalty
        Currency penaltyCurrency = Currency.matchCurrency(super.getString("Raid failure.Currency", null));
        if (penaltyCurrency == null) {
            penaltyCurrency = Currency.GEMS;

            ChatUtil.sendMessage("&cThe currency specified in the \"Raid failure\" section (in the config) does not exist.");
            ChatUtil.sendMessage("&c" + penaltyCurrency + " will be used as currency.");
        }
        int penaltyAmount = super.getInt("Raid failure.Amount", -1);
        if (penaltyAmount < 0) {
            penaltyAmount = 0;

            ChatUtil.sendMessage("&ccThe amount of the penalty in the \"Raid failure\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + penaltyAmount + "\" will be used as replacement value.");
        }
        int penaltyTrophies = super.getInt("Raid failure.Trophies lost", -1);
        if (penaltyTrophies < 0) {
            penaltyTrophies = 0;

            ChatUtil.sendMessage("&cThe number of penalty in the \"Raid failure\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + penaltyTrophies + "\" will be used as replacement value.");
        }


        // Check raid timeout
        int raidTimeout = super.getInt("Raid timeout", -1);
        if (raidTimeout < 0) {
            raidTimeout = 180;

            ChatUtil.sendMessage("&cThe duration of the raid in the \"Raid timeout\" line (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + raidTimeout + "\" will be used as replacement value.");
        }


        // Check the starting balances
        int startingGold = super.getInt("Starting balance.Gold");
        if (startingGold < 0) {
            startingGold = 0;

            ChatUtil.sendMessage("&cThe gold in the \"Starting balance\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + startingGold + "\" will be used as replacement value.");
        }
        int startingElixir = super.getInt("Starting balance.Elixir");
        if (startingElixir < 0) {
            startingElixir = 0;

            ChatUtil.sendMessage("&cThe elixir in the \"Starting balance\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + startingElixir + "\" will be used as replacement value.");
        }
        int startingGems = super.getInt("Starting balance.Gems");
        if (startingGems < 0) {
            startingGems = 0;

            ChatUtil.sendMessage("&cThe gems in the \"Starting balance\" section (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + startingGems + "\" will be used as replacement value.");
        }


        int maxPlayerCacheCount = super.getInt("Max player cache");
        if (maxPlayerCacheCount < 0) {
            final long maxAllocatedMemory = Runtime.getRuntime().maxMemory(); // Parameter -Xmx, in bytes
            final long count = maxAllocatedMemory / 1024 / 200; // Dividing by 200 means that for every 200Kb allocated, 1 player can be cached
            if (count <= Integer.MAX_VALUE)
                maxPlayerCacheCount = (int) count;
            else
                maxPlayerCacheCount = 1500;

            ChatUtil.sendMessage("&ccThe number specified in the \"Max player cache\" line (in the config) cannot be negative!");
            ChatUtil.sendMessage("&c\"" + maxPlayerCacheCount + "\" will be used as replacement value.");
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
        loadMenuSettings();

        /*
         * Town hall settings
         */
        loadTownHallSettings();

        /*
         * Structure settings
         */
        loadExtractorSettings();
        loadArcherTowerSettings();

        /*
         * Clan settings
         */
        loadClanSettings();
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
     * @return {@link TownHall} containing the configured price, currency, and command corresponding to the
     * specified level
     *
     * @since v3.0.3
     */
    @Nonnull
    public TownHall getTownHall(final int level) {
        return townHalls.get(level - 2);
    }

    @Nonnull
    public Extractor getGoldExtractor(final int level) {
        return goldExtractors.get(level - 1);
    }

    @Nonnull
    public Extractor getElixirExtractor(final int level) {
        return elixirExtractors.get(level - 1);
    }

    @Nonnull
    public ArcherTower getArcherTower(final int level) {
        return archerTowers.get(level - 1);
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
            case ELIXIR_EXTRACTOR:
                return goldExtractors.size();
            case TOWN_HALL:
                return townHalls.size() + 1;

            default:
                throw new IllegalStateException("Unexpected value: " + building);
        }
    }

    @Nonnull
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

    private void loadMenuSettings() {
        // Check if menu list is empty
        final ConfigurationSection menuConfig = super.getConfigurationSection("Menus");
        if (menuConfig == null || menuConfig.getKeys(false).isEmpty()) {
            ChatUtil.sendMessage("&cWarning: there are no menus set up in the config.");
            return;
        }

        // Load all menus
        for (String menuName : menuConfig.getKeys(false)) {
            byte size = (byte) menuConfig.getInt(menuName + ".Settings.Row", -1);
            String title = menuConfig.getString(menuName + ".Settings.Title", null);

            if (size < 1 || size > 6) {
                ChatUtil.sendMessage("&cThe number of rows of the menu \"" + menuName + "\" isn't correct. The menu will be ignored.");
                continue;
            }
            size *= 9; // Total number of slots

            if (title == null) {
                ChatUtil.sendMessage("&cThe name of the menu \"" + menuName + "\" is missing. The menu will be ignored.");
                continue;
            }
            title = ColorUtil.colour(title);


            /*
             * Load all items for current menu
             */
            // Check if there are no items
            final ConfigurationSection itemsConf = menuConfig.getConfigurationSection(menuName + ".Items");
            if (itemsConf == null || itemsConf.getKeys(false).isEmpty()) {
                ChatUtil.sendMessage("&cWarning: the menu \"" + menuName + "\"  has no items!");
                ChatUtil.sendMessage("&cThis menu will not be created.");
                continue;
            }

            final List<BaseItem> items = new ArrayList<>();
            for (String itemName : itemsConf.getKeys(false)) {
                final ItemBuilder itemBuilder = new ItemBuilder();

                final Material material;
                short damage;
                final String displayName;
                final List<String> lore;
                final Map<Enchantment, Integer> enchantments = new HashMap<>();
                final Color color;
                final String headOwner;
                final List<Pattern> patterns = new ArrayList<>();
                final boolean hideAttribute;
                final byte slot;
                final Pair<Integer, Currency> requiredBalance;
                final ItemStack requiredItem;
                final String command;

                final String materialStr = itemsConf.getString(itemName + ".Item", null);
                if (materialStr == null) {
                    ChatUtil.sendMessage("&cMissing material for item \"" + itemName + "\" in menu \"" + menuName + "\". This item will be ignored.");
                    continue;
                }
                material = Material.matchMaterial(materialStr);
                if (material == null) {
                    ChatUtil.sendMessage("&cUnknown material \"" + materialStr + "\" in menu \"" + menuName + "\". This item will be ignored.");
                    continue;
                }
                itemBuilder.setMaterial(material);


                damage = (short) itemsConf.getInt(itemName + ".Durability", 0);
                if (damage < 0) {
                    ChatUtil.sendMessage("&cThe durability of \"" + itemName + "\" item in menu \"" + menuName + "\" is negative. The durability will be ignored.");
                    damage = 0;
                }
                itemBuilder.setDamage(damage);


                displayName = itemsConf.getString(itemName + ".Name", null);
                itemBuilder.setName(displayName);


                /*
                 * In the ConfigurationSection#getStringList() method,
                 * it is not possible to pass the default value to be returned.
                 * By default, the default value becomes equal to the value
                 * in the config.yml embedded in the .jar.
                 *
                 * In the case where there is no lore set in the config.yml,
                 * indicating that there should be no lore, #getStringList()
                 * will return the default value (the one in the config.yml in the .jar)
                 * which may not be null (and thus a lore will be set, which is wrong).
                 *
                 * To solve this problem, it is previously checked whether the lore is set (#isSet()) in the config.yml.
                 */
                lore = itemsConf.isSet(itemName + ".Lore") ? itemsConf.getStringList(itemName + ".Lore") : null;
                itemBuilder.setLore(lore);


                if (itemsConf.isSet(itemName + ".Enchantments")) // Check necessary to avoid the problem explained above
                {
                    for (String enchant : itemsConf.getStringList(itemName + ".Enchantments")) {
                        final String[] split = enchant.split(",");
                        if (split.length != 2) {
                            ChatUtil.sendMessage("&cThe enchantment \"" + enchant + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is in the wrong format. Enchantment will be ignored.");
                            continue;
                        }

                        final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(split[0].replace(" ", "_").toLowerCase(Locale.ENGLISH)));
                        if (enchantment == null) {
                            ChatUtil.sendMessage("&cThe enchantment \"" + split[0] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Enchantment will be ignored.");
                            continue; // Wrong enchant name
                        }

                        final int level;
                        try {
                            level = Integer.parseInt(split[1].trim());
                        } catch (final NumberFormatException ignored) {
                            ChatUtil.sendMessage("&cThe level of the enchantment \"" + split[0] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Enchantment will be ignored.");
                            continue; // Wrong level format
                        }

                        enchantments.put(enchantment, level);
                    }

                    itemBuilder.setEnchantments(enchantments);
                }


                //TODO: se il formato o il colore è sbagliato non lo segnalo, semplicemente ignoro -> inviare notifica
                color = ColorUtil.matchColor(itemsConf.getString(itemName + ".Color", null));
                itemBuilder.setLeatherArmorColor(color);


                headOwner = itemsConf.getString(itemName + ".Skull", null);
                /* Method using UUIDs. Unfortunately, in the ItemBuilder when setting the owner in the ItemMeta the non-deprecated method (using UUIDs) does not work if the player is not online.
                UUID headOwner = null;
                try {
                    final String owner = itemsConf.getString(itemName + ".Skull", null);
                    if (owner != null)
                        headOwner = UUID.fromString(owner);
                }
                catch (final Exception ignored) {
                    // Wrong UUID format
                    ChatUtil.sendMessage("&cThe skull UUID of \"" + itemName + "\" item in menu \"" + menuName + "\" is in the wrong format. Enchantment will be ignored.");
                }*/
                itemBuilder.setSkullOwner(headOwner);


                for (String pattern : itemsConf.getStringList(itemName + ".Pattern")) {
                    final String[] split = pattern.split(",");
                    if (split.length != 2) {
                        ChatUtil.sendMessage("&cThe banner pattern \"" + pattern + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is in the wrong format. Pattern will be ignored.");
                        continue;
                    }

                    final DyeColor patternColor = ColorUtil.matchDyeColor(split[0]);
                    if (patternColor == null) {
                        ChatUtil.sendMessage("&cThe pattern color \"" + split[0] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Pattern will be ignored.");
                        continue; // Non-existent color
                    }

                    final PatternType patternType = PatternType.getByIdentifier(split[1].trim().toLowerCase(Locale.ENGLISH));
                    if (patternType == null) {
                        ChatUtil.sendMessage("&cThe banner pattern \"" + split[1] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Pattern will be ignored.");
                        continue; // Wrong pattern code
                    }

                    patterns.add(new Pattern(patternColor, patternType));
                }
                itemBuilder.setBannerPatterns(patterns);


                hideAttribute = !itemsConf.getBoolean(itemName + ".Attributes", true);
                itemBuilder.hideAttributes(hideAttribute);


                slot = (byte) itemsConf.getInt(itemName + ".Slot", -1);
                if (slot < 0 || slot >= size) {
                    ChatUtil.sendMessage("&cThe slot of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. The item will be ignored.");
                    continue;
                }


                final String requiredBalanceStr = itemsConf.getString(itemName + ".Required balance", null);
                if (requiredBalanceStr == null)
                    requiredBalance = null;
                else {
                    final String[] split = requiredBalanceStr.split(",");
                    if (split.length != 2) {
                        ChatUtil.sendMessage("&cRequired balance for item \"" + itemName + "\" in menu \"" + menuName + "\" is in the wrong format. This item will be ignored.");
                        continue;
                    }

                    final int requiredAmount;
                    try {
                        requiredAmount = Integer.parseInt(split[0].trim());
                    } catch (final NumberFormatException ignored) {
                        ChatUtil.sendMessage("&cThe currency amount \"" + split[0] + "\", in \"Required item\", of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. This item will be ignored.");
                        continue; // Wrong amount
                    }
                    if (requiredAmount < 0) {
                        ChatUtil.sendMessage("&cThe currency amount of \"" + itemName + "\" item in menu \"" + menuName + "\" is negative. The item will be ignored.");
                        continue; // Negative amount
                    }

                    final Currency requiredCurrency = Currency.matchCurrency(split[1].trim());
                    if (requiredCurrency == null) {
                        ChatUtil.sendMessage("&cThe currency \"" + split[1] + "\", in \"Required item\", of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. This item will be ignored.");
                        continue; // Wrong currency
                    }

                    requiredBalance = new Pair<>(requiredAmount, requiredCurrency);
                }


                final String requiredItemStr = itemsConf.getString(itemName + ".Required item", null);
                if (requiredItemStr != null && Material.matchMaterial(requiredItemStr) == null) {
                    ChatUtil.sendMessage("&cRequired item of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. The item will be ignored.");
                    continue;
                }
                requiredItem = requiredItemStr != null ? new ItemBuilder(Material.matchMaterial(requiredItemStr)).build() : null;


                command = ColorUtil.colour(itemsConf.getString(itemName + ".Command", null));


                items.add(new ConfigItem(itemBuilder.build(), slot, requiredBalance, requiredItem, command));
            }

            this.menus.put(menuName.toLowerCase(), new Menu(title, size, items));
        }
    }

    /**
     * Load from the configuration file the settings for the different levels of town halls
     *
     * @since v3.1
     */
    private List<TownHall> loadTownHallSettings() {
        // Check if town hall list is empty
        final ConfigurationSection townHallConfig = super.getConfigurationSection("TownHall");
        if (townHallConfig == null || townHallConfig.getKeys(false).isEmpty()) {
            ChatUtil.sendWarning("Warning! The town hall configuration is missing in the config.");

            return Collections.emptyList();
        }

        int level = 1; // Help to calculate default values for production and price
        for (String townHall : townHallConfig.getKeys(false)) {
            level++;

            int price = townHallConfig.getInt(townHall + ".Price", -1);
            Currency currency = Currency.matchCurrency(townHallConfig.getString(townHall + ".Currency", null));
            final String command = townHallConfig.getString(townHall + ".Command", null);

            if (price < 0) {
                price = 1000 * (int) Math.pow(level, 2);

                ChatUtil.sendMessage("&cThe price of level \"" + townHall + "\" town hall (in the config) cannot be negative!");
                ChatUtil.sendMessage("&cPrice will be set to " + price + ".");
            }
            if (currency == null) {
                currency = (level % 2 == 0) ? Currency.ELIXIR : Currency.GOLD;

                ChatUtil.sendMessage("&cThe currency set for town hall \"" + townHall + "\" (in the config) does not exist.");
                ChatUtil.sendMessage("&c" + currency + " will be used as currency.");
            }

            this.townHalls.add(new TownHall(level, price, currency, command));
        }
    }

    private void loadExtractorSettings() {
        // Check if extractor list is empty
        final ConfigurationSection extractorConfig = super.getConfigurationSection("Extractors");
        if (extractorConfig == null || extractorConfig.getKeys(false).isEmpty()) {
            ChatUtil.sendMessage("&cThe extractor configuration is missing in the config!");
            ChatUtil.sendMessage("&c10 default extractors will be created.");

            //TODO: qui, per i municipi, le torri degli arcieri e i clan -> nel caso la lista sia vuota significa che non vuole più livelli => lasciare così e non mostrare l'elemento nel menù /upgrade
            for (int level = 1; level <= 10; level++) {
                int production = 12 * level;
                int capacity = production * 48;
                int price = 10 * 3 * level;
                Currency currency = Currency.GEMS;

                this.goldExtractors.add(new GoldExtractor(level, production, capacity, price, currency));
                this.elixirExtractors.add(new ElixirExtractor(level, production, capacity, price, currency));
            }
        } else {
            int level = 0; // Help to calculate default values for production and price
            for (String extractor : extractorConfig.getKeys(false)) {
                level++;

                int production = extractorConfig.getInt(extractor + ".Production", -1);
                int capacity = extractorConfig.getInt(extractor + ".Capacity", 0);
                int price = extractorConfig.getInt(extractor + ".Price", -1);
                Currency currency = Currency.matchCurrency(extractorConfig.getString(extractor + ".Currency", null));

                if (production < 0) {
                    production = 12 * level;

                    ChatUtil.sendMessage("&cThe production of level \"" + extractor + "\" extractor (in the config) cannot be negative!");
                    ChatUtil.sendMessage("&cProduction will be set to " + production + ".");
                }
                if (capacity < 1) {
                    capacity = production * 48;

                    ChatUtil.sendMessage("&cThe capacity of level \"" + extractor + "\" extractor (in the config) must be positive!");
                    ChatUtil.sendMessage("&cCapacity will be set to " + production + ".");
                }
                if (price < 0) {
                    price = 10 * 3 * level;

                    ChatUtil.sendMessage("&cThe price of level \"" + extractor + "\" extractor (in the config) cannot be negative!");
                    ChatUtil.sendMessage("&cPrice will be set to " + price + ".");
                }
                if (currency == null) {
                    currency = Currency.GEMS;

                    ChatUtil.sendMessage("&cThe currency set for extractor \"" + extractor + "\" (in the config) does not exist.");
                    ChatUtil.sendMessage("&c" + currency + " will be used as currency.");
                }

                this.goldExtractors.add(new GoldExtractor(level, production, capacity, price, currency));
                this.elixirExtractors.add(new ElixirExtractor(level, production, capacity, price, currency));
            }
        }
    }

    private void loadArcherTowerSettings() {
        // Check if archer tower list is empty
        final ConfigurationSection archerConfig = super.getConfigurationSection("ArcherTowers");
        if (archerConfig == null || archerConfig.getKeys(false).isEmpty()) {
            ChatUtil.sendMessage("&cThe configuration of the archer towers is missing in the config!");
            ChatUtil.sendMessage("&c10 default archer towers will be created.");

            //TODO: qui, per i municipi, gli estrattori e i clan -> nel caso la lista sia vuota significa che non vuole più livelli => lasciare così e non mostrare l'elemento nel menù /upgrade
            for (int level = 1; level <= 10; level++) {
                double damage = 0.5 * level;
                int price = 20 * 3 * level;
                Currency currency = Currency.GEMS;

                this.archerTowers.add(new ArcherTower(level, damage, price, currency));
            }
        } else {
            int level = 0; // Help to calculate default values for damage and price
            for (String archerTower : archerConfig.getKeys(false)) {
                level++;

                double damage = archerConfig.getDouble(archerTower + ".Damage", -1);
                int price = archerConfig.getInt(archerTower + ".Price", -1);
                Currency currency = Currency.matchCurrency(archerConfig.getString(archerTower + ".Currency", null));

                if (damage < 0) {
                    damage = 0.5 * level;

                    ChatUtil.sendMessage("&cThe damage of the archer tower \"" + archerTower + "\" (in the config) cannot be negative!");
                    ChatUtil.sendMessage("&cDamage will be set to " + damage + ".");
                }
                if (price < 0) {
                    price = 20 * 3 * level;

                    ChatUtil.sendMessage("&cThe price for the archer tower \"" + archerTower + "\" (in the config) cannot be negative!");
                    ChatUtil.sendMessage("&cPrice will be set to " + price + ".");
                }
                if (currency == null) {
                    currency = Currency.GEMS;

                    ChatUtil.sendMessage("&cThe currency set for the archer tower \"" + archerTower + "\" (in the config) does not exist.");
                    ChatUtil.sendMessage("&c" + currency + " will be used as currency.");
                }

                this.archerTowers.add(new ArcherTower(level, damage, price, currency));
            }
        }
    }

    private void loadClanSettings() {
        // Check if clans list is empty
        final ConfigurationSection clanConfig = super.getConfigurationSection("Clans");
        if (clanConfig == null || clanConfig.getKeys(false).isEmpty()) {
            ChatUtil.sendMessage("&cThe configuration of the clans is missing in the config!");
            ChatUtil.sendMessage("&c5 default levels will be added will be created.");

            //TODO: qui, per i municipi, gli estrattori e le torri degli arcieri -> nel caso la lista sia vuota significa che non vuole più livelli => lasciare così e bloccare l'incremento di livello
            for (int level = 1; level <= 5; level++) {
                final int expRequired = 100 + (3 * (level - 1));

                this.clans.add(new Pair<>(expRequired, null));
            }
        } else {
            int level = 0; // Help to calculate default values for damage and price
            for (String clan : clanConfig.getKeys(false)) {
                level++;

                int expRequired = clanConfig.getInt(clan + ".Exp required", -1);
                final String command = clanConfig.getString(clan + ".Command", null);

                if (expRequired < 0) {
                    expRequired = 100 + (3 * (level - 1));

                    ChatUtil.sendMessage("&c\"Exp required\" of the clan \"" + clan + "\" (in the config) cannot be negative!");
                    ChatUtil.sendMessage("&cExp required will be set to " + expRequired + ".");
                }

                this.clans.add(new Pair<>(expRequired, command));
            }
        }
    }

    private void loadDefaultValue() {
        final InputStream defConfigStream = plugin.getResource("config.yml");
        if (defConfigStream != null)
            super.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    @Override
    void load() throws Exception {
        if (!file.exists())
            FileUtil.copyFile(file.getName(), file);

        super.load(file);
    }
}
