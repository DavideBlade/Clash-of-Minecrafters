/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.file;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.ArcherTower;
import com.gmail.davideblade99.clashofminecrafters.building.ElixirExtractor;
import com.gmail.davideblade99.clashofminecrafters.building.GoldExtractor;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Balance;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.type.bean.UserDatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.collection.RandomItemExtractor;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.yaml.PlayerConfiguration;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.util.UUID;

//TODO: leggere: https://bukkit.org/threads/async-file-saving.158385/

//TODO: guardare: https://github.com/EssentialsX/Essentials/blob/908189a233af5a317b0fa15546510a9a9e9ad44f/Essentials/src/main/java/com/earth2me/essentials/config/EssentialsConfiguration.java + https://github.com/EssentialsX/Essentials/blob/908189a233af5a317b0fa15546510a9a9e9ad44f/Essentials/src/main/java/com/earth2me/essentials/config/ConfigurationSaveTask.java + https://github.com/EssentialsX/Essentials/blob/908189a233af5a317b0fa15546510a9a9e9ad44f/build-logic/src/main/kotlin/FileCopyTask.kt + https://github.com/EssentialsX/Essentials/blob/908189a233af5a317b0fa15546510a9a9e9ad44f/Essentials/src/main/java/com/earth2me/essentials/BalanceTopImpl.java + https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/net/essentialsx/api/v2/events/AsyncUserDataLoadEvent.java
//TODO: guardare: https://github.com/essentials/Essentials/blob/d36d80933f8f672cd8bb0f210adc23aac10850ea/Essentials/src/com/earth2me/essentials/Jails.java#L130 + https://github.com/essentials/Essentials/blob/d36d80933f8f672cd8bb0f210adc23aac10850ea/Essentials/src/com/earth2me/essentials/UserData.java#L968 + https://github.com/essentials/Essentials/blob/d36d80933f8f672cd8bb0f210adc23aac10850ea/Essentials/src/com/earth2me/essentials/EssentialsConf.java

//TODO: se si può evitare di creare ogni volta un nuovo PlayerConfig è meglio (per es. facendone una cache che rispecchia la cache degli User)

//TODO: gli errori (es. impossibile accedere al file) gestirli come nel MySQL
public final class YAMLDatabase implements PlayerDatabase {

    private final CoM plugin;
    private final File playerFolder;

    public YAMLDatabase(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.playerFolder = new File(plugin.getDataFolder(), "Players");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPlayedBefore(@Nonnull final UUID playerUUID) {
        return getPlayerFile(playerUUID).exists();
    }

    /**
     * Search randomly through the player files for an existing island
     * <p>
     * {@inheritDoc}
     *
     * @throws IllegalStateException If there is any missing data regarding the player or island
     */
    //TODO: è pesante (scorre tutti file, che dopo anni diventano tanti) -> va fatto in async
    @Nullable
    @Override
    public Village getRandomIsland() {
        final File[] listOfFiles = getPlayerFiles();
        if (listOfFiles == null || listOfFiles.length == 0)
            return null;

        final RandomItemExtractor<File> fileRandomizer = new RandomItemExtractor<>(listOfFiles);
        while (!fileRandomizer.isEmpty()) // As long as there are files
        {
            final File file = fileRandomizer.getRandomElement();
            if (file == null)
                continue;

            final PlayerConfiguration conf = new PlayerConfiguration(file);
            final String playerName = plugin.getPlayerHandler().getPlayerName(conf.getUUID());
            final Location spawn = conf.getIslandSpawn();
            final Vector origin = conf.getIslandOrigin();
            final Size2D size = conf.getIslandSize();
            final Size2D expansions = conf.getIslandExpansions();
            if (spawn == null && origin == null && size == null && expansions == null)
                continue; // The player does not have a village

            // Unexpected missing data
            if (playerName == null)
                throw new IllegalStateException("Player name associated with UUID \"" + conf.getUUID() + "\" missing");
            if (spawn == null || origin == null || size == null || expansions == null)
                throw new IllegalStateException("Village of \"" + conf.getUUID() + "\" existing with some missing data");
            //TODO: notificare al giocatore

            // Village found
            return new Village(conf.getUUID(), spawn, origin, size, expansions);
        }

        return null; // No village available
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If there is any missing data regarding the player or island
     */
    //TODO: è pesante (potrebbe scorre tutti file, che dopo anni diventano tanti) -> va fatto in async
    @Nullable
    @Override
    public Village getRandomEnemyIsland(@Nonnull final String clanName) {
        final File[] listOfFiles = getPlayerFiles();
        if (listOfFiles == null || listOfFiles.length == 0)
            return null;

        final RandomItemExtractor<File> fileRandomizer = new RandomItemExtractor<>(listOfFiles);
        while (!fileRandomizer.isEmpty()) // As long as there are files
        {
            final File file = fileRandomizer.getRandomElement();
            if (file == null)
                continue;

            final PlayerConfiguration conf = new PlayerConfiguration(file);
            final String playerName = plugin.getPlayerHandler().getPlayerName(conf.getUUID());
            final Location spawn = conf.getIslandSpawn();
            final Vector origin = conf.getIslandOrigin();
            final Size2D size = conf.getIslandSize();
            final Size2D expansions = conf.getIslandExpansions();
            if (spawn == null && origin == null && size == null && expansions == null)
                continue; // The player does not have an island

            final String targetClan = conf.getClanName();
            if (targetClan == null || targetClan.equals(clanName))
                continue; // The target has no clan or is in the same clan as the attacker

            // Unexpected missing data
            if (playerName == null)
                throw new IllegalStateException("Player name associated with UUID \"" + conf.getUUID() + "\" missing");
            if (spawn == null || origin == null || size == null || expansions == null)
                throw new IllegalStateException("Village of \"" + conf.getUUID() + "\" existing with some missing data");
            //TODO: notificare al giocatore

            // Island found
            return new Village(conf.getUUID(), spawn, origin, size, expansions);
        }
        return null; // No island available
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public UserDatabaseType fetchUser(@Nonnull final UUID playerUUID) {
        final PlayerConfiguration conf = new PlayerConfiguration(getPlayerFile(playerUUID));
        final int gold = conf.getBalance(Currencies.GOLD);
        final int elixir = conf.getBalance(Currencies.ELIXIR);
        final int gems = conf.getBalance(Currencies.GEMS);
        final int trophies = conf.getTrophies();
        final String clanName = conf.getClanName();
        final int townHallLevel = conf.getTownHallLevel();
        final ElixirExtractor elixirExtractor = conf.getElixirExtractor();
        final GoldExtractor goldExtractor = conf.getGoldExtractor();
        final ArcherTower archerTower = conf.getArcherTower();

        final String playerName = plugin.getPlayerHandler().getPlayerName(conf.getUUID());
        if (playerName == null) // Unexpected missing data
            throw new IllegalStateException("Player name associated with UUID \"" + playerUUID + "\" missing"); //TODO: notificare al giocatore
        final Location islandSpawn = conf.getIslandSpawn();
        final Vector islandOrigin = conf.getIslandOrigin();
        final Size2D islandSize = conf.getIslandSize();
        final Size2D islandExpansions = conf.getIslandExpansions();
        final Village village = islandSpawn != null && islandOrigin != null && islandSize != null && islandExpansions != null ? new Village(playerUUID, islandSpawn, islandOrigin, islandSize, islandExpansions) : null;

        final String timestamp = conf.getCollectionTime();
        final LocalDateTime collectionTime = timestamp == null ? null : LocalDateTime.parse(timestamp, CoM.DATE_FORMAT);

        return new UserDatabaseType(new Balance(gold, elixir, gems), trophies, clanName, elixirExtractor, goldExtractor, archerTower, village, collectionTime, townHallLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeUser(@Nonnull final UUID playerUUID, @Nonnull final User user) {
        final int gold = user.getGold();
        final int elixir = user.getElixir();
        final int gems = user.getGems();
        final int trophies = user.getTrophies();
        final String clanName = user.getClanName();
        final int townHallLevel = user.getTownHallLevel();
        final ElixirExtractor elixirExtractor = user.getElixirExtractor();
        final GoldExtractor goldExtractor = user.getGoldExtractor();
        final ArcherTower archerTower = user.getArcherTower();
        final Village island = user.getVillage();
        final LocalDateTime collectionTime = user.getCollectionTime();


        final PlayerConfiguration conf = new PlayerConfiguration(getPlayerFile(playerUUID));

        conf.setBalance(Currencies.GOLD, gold);
        conf.setBalance(Currencies.ELIXIR, elixir);
        conf.setBalance(Currencies.GEMS, gems);
        conf.setTrophies(trophies);
        conf.setClan(clanName);
        conf.setTownHallLevel(townHallLevel);

        if (elixirExtractor != null)
            conf.saveElixirExtractor(elixirExtractor);

        if (goldExtractor != null)
            conf.saveGoldExtractor(goldExtractor);

        if (archerTower != null)
            conf.saveArcherTower(archerTower);

        if (island != null) {
            conf.setIslandSpawn(BukkitLocationUtil.toString(island.getSpawn()));
            conf.setIslandOrigin(island.origin);
            conf.setIslandSize(island.size);
            conf.setIslandExpansions(island.expansions);
        }

        if (collectionTime != null)
            conf.setCollectionTime(CoM.DATE_FORMAT.format(collectionTime));

        conf.save();
    }

    @Nonnull
    private File getPlayerFile(@Nonnull final UUID uuid) {
        return new File(playerFolder, uuid + ".yml");
    }

    //TODO: è pesante (scorre tutti file, che dopo anni diventano tanti) -> va fatto in async

    /**
     * @return An array containing the player files in the {@link #playerFolder}. The array will be empty if the directory is
     * empty. Returns {@code null} if an I/O error occurs.
     */
    @Nullable
    private File[] getPlayerFiles() {
        return playerFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, @Nonnull final String name) {
                return name.endsWith(".yml");
            }
        });
    }
}
