/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.yaml;

import com.gmail.davideblade99.clashofminecrafters.building.ArcherTower;
import com.gmail.davideblade99.clashofminecrafters.building.ElixirExtractor;
import com.gmail.davideblade99.clashofminecrafters.building.GoldExtractor;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.storage.Columns;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

public final class PlayerConfiguration extends CoMYamlConfiguration {

    private final static String HEADER_COMMENT =
            "If you change this file by hand you risk corrupting the file and thus losing data." + "\n" +
                    "It is recommended to use in-game commands.";

    /**
     * {@inheritDoc}
     */
    public PlayerConfiguration(@Nonnull final File file, final boolean autoSave) {
        super(file, autoSave);
    }

    public PlayerConfiguration(@Nonnull final File file) {
        super(file);

        // Add comment on the top of the file
        super.options().header(HEADER_COMMENT);
        if (autoSave)
            super.save();
    }

    @Nonnull
    public UUID getUUID() {
        return UUID.fromString(super.file.getName().replace(".yml", ""));
    }

    public int getTrophies() {
        return super.getInt(Columns.TROPHIES);
    }

    /**
     * Sets the trophies to the respective path
     *
     * @param amount New trophies amount
     */
    public void setTrophies(final int amount) {
        super.set(Columns.TROPHIES, amount);
    }

    public String getClanName() {
        return super.getString(Columns.CLAN);
    }

    /**
     * Sets the player's clan to the respective path
     *
     * @param clanName Clan name to set
     */
    public void setClan(@Nullable final String clanName) {
        super.set(Columns.CLAN, clanName);
    }

    /**
     * @return the timestamp when the player collected from the extractors or {@code null} if not saved
     */
    @Nullable
    public String getCollectionTime() {
        return super.getString(Columns.COLLECTION_TIME);
    }

    /**
     * Sets the timestamp when the player collected from the extractors to the respective path
     *
     * @param timestamp Timestamp to set
     */
    public void setCollectionTime(@Nullable final String timestamp) {
        super.set(Columns.COLLECTION_TIME, timestamp);
    }

    @Nullable
    public Location getIslandSpawn() {
        return BukkitLocationUtil.fromString(super.getString(Columns.ISLAND_SPAWN));
    }

    /**
     * Sets the island spawn to the respective path
     *
     * @param location Location to set
     */
    public void setIslandSpawn(@Nonnull final String location) {
        super.set(Columns.ISLAND_SPAWN, location);
    }

    @Nullable
    public Vector getIslandOrigin() {
        return Vector.fromString(super.getString(Columns.ISLAND_ORIGIN));
    }

    /**
     * Sets the island origin to the respective path
     *
     * @param location Location to set
     */
    public void setIslandOrigin(@Nonnull final Vector location) {
        super.set(Columns.ISLAND_ORIGIN, location.toString());
    }

    @Nullable
    public Size2D getIslandSize() {
        return Size2D.fromString(super.getString(Columns.ISLAND_SIZE));
    }

    /**
     * Sets the island size to the respective path
     *
     * @param size New size to set
     */
    public void setIslandSize(@Nonnull final Size2D size) {
        super.set(Columns.ISLAND_SIZE, size.toString());
    }

    @Nullable
    public Size2D getIslandExpansions() {
        return Size2D.fromString(super.getString(Columns.ISLAND_EXPANSIONS));
    }

    /**
     * Sets the number of island's expansions to the respective path
     *
     * @param expansions Number of expansions to set
     */
    public void setIslandExpansions(@Nonnull final Size2D expansions) {
        super.set(Columns.ISLAND_EXPANSIONS, expansions.toString());
    }

    /**
     * Stores all information about the archer's tower in the respective path
     *
     * @param archerTower Archer tower to save
     *
     * @since 3.2
     */
    public void saveArcherTower(@Nonnull final ArcherTower archerTower) {
        super.set(Columns.ARCHER_TOWER_LEVEL, archerTower.getLevel());
        super.set(Columns.TOWER_ARCHER_LOCATION, archerTower.getArcherPos().toString());
        super.set(Columns.ARCHER_TOWER_MIN_CORNER, archerTower.getBuildingArea().getMinCorner().toString());
        super.set(Columns.ARCHER_TOWER_MAX_CORNER, archerTower.getBuildingArea().getMaxCorner().toString());
    }

    /**
     * @return A new {@link ArcherTower} containing the fetched information or {@code null} if the player does not own an
     * archer's tower
     * @since 3.2
     */
    @Nullable
    public ArcherTower getArcherTower() {
        final int level = super.getInt(Columns.ARCHER_TOWER_LEVEL);
        final Vector archerPos = Vector.fromString(super.getString(Columns.TOWER_ARCHER_LOCATION));
        final Vector towerCorner1 = Vector.fromString(super.getString(Columns.ARCHER_TOWER_MIN_CORNER));
        final Vector towerCorner2 = Vector.fromString(super.getString(Columns.ARCHER_TOWER_MAX_CORNER));

        if (level < 0 || archerPos == null || towerCorner1 == null || towerCorner2 == null)
            return null;

        return new ArcherTower(level, archerPos, towerCorner1, towerCorner2);
    }

    /**
     * Stores all extractor information in the respective path
     *
     * @param goldExtractor Gold extractor to save
     *
     * @since 3.2
     */
    public void saveGoldExtractor(@Nonnull final GoldExtractor goldExtractor) {
        super.set(Columns.GOLD_EXTRACTOR_LEVEL, goldExtractor.getLevel());
        super.set(Columns.GOLD_EXTRACTOR_MIN_CORNER, goldExtractor.getBuildingArea().getMinCorner().toString());
        super.set(Columns.GOLD_EXTRACTOR_MAX_CORNER, goldExtractor.getBuildingArea().getMaxCorner().toString());
    }

    /**
     * @return A new {@link GoldExtractor} containing the fetched information or {@code null} if the player does not own a
     * gold extractor
     * @since 3.2
     */
    @Nullable
    public GoldExtractor getGoldExtractor() {
        final int level = super.getInt(Columns.GOLD_EXTRACTOR_LEVEL);
        final Vector extractorCorner1 = Vector.fromString(super.getString(Columns.GOLD_EXTRACTOR_MIN_CORNER));
        final Vector extractorCorner2 = Vector.fromString(super.getString(Columns.GOLD_EXTRACTOR_MAX_CORNER));

        //TODO: se c'è qualche dato si e qualche dato no errore: unexpected missing data - questo per tutti
        //TODO: copiare MySQL
        if (level < 0 || extractorCorner1 == null || extractorCorner2 == null)
            return null;

        return new GoldExtractor(level, extractorCorner1, extractorCorner2);
    }

    /**
     * Stores all extractor information in the respective path
     *
     * @param elixirExtractor Elixir extractor to save
     *
     * @since 3.2
     */
    public void saveElixirExtractor(@Nonnull final ElixirExtractor elixirExtractor) {
        super.set(Columns.ELIXIR_EXTRACTOR_LEVEL, elixirExtractor.getLevel());
        super.set(Columns.ELIXIR_EXTRACTOR_MIN_CORNER, elixirExtractor.getBuildingArea().getMinCorner().toString());
        super.set(Columns.ELIXIR_EXTRACTOR_MAX_CORNER, elixirExtractor.getBuildingArea().getMaxCorner().toString());
    }

    /**
     * @return A new {@link ElixirExtractor} containing the fetched information or {@code null} if the player does not own an
     * elixir extractor
     * @since 3.2
     */
    @Nullable
    public ElixirExtractor getElixirExtractor() {
        final int level = super.getInt(Columns.ELIXIR_EXTRACTOR_LEVEL);
        final Vector extractorCorner1 = Vector.fromString(super.getString(Columns.ELIXIR_EXTRACTOR_MIN_CORNER));
        final Vector extractorCorner2 = Vector.fromString(super.getString(Columns.ELIXIR_EXTRACTOR_MAX_CORNER));

        if (level < 0 || extractorCorner1 == null || extractorCorner2 == null)
            return null;

        return new ElixirExtractor(level, extractorCorner1, extractorCorner2);
    }

    public int getBalance(@Nonnull final Currencies currency) {
        switch (currency) {
            case GOLD:
                return super.getInt(Columns.GOLD);
            case GEMS:
                return super.getInt(Columns.GEMS);
            case ELIXIR:
                return super.getInt(Columns.ELIXIR);
            default:
                throw new IllegalStateException("Unexpected value: " + currency);
        }
    }

    /**
     * Sets the player's balance to the respective path
     *
     * @param currency Currency to set
     * @param amount   New amount to set
     */
    public void setBalance(@Nonnull final Currencies currency, final int amount) {
        switch (currency) {
            case GOLD:
                super.set(Columns.GOLD, amount);
                break;

            case GEMS:
                super.set(Columns.GEMS, amount);
                break;

            case ELIXIR:
                super.set(Columns.ELIXIR, amount);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + currency);
        }
    }

    /**
     * Sets the level of the town hall to the respective path
     *
     * @param level The new level to set
     *
     * @since 3.2
     */
    public void setTownHallLevel(final int level) {
        super.set(Columns.TOWN_HALL_LEVEL, level);
    }

    /**
     * @return The level of the town hall
     * @since 3.2
     */
    public int getTownHallLevel() {
        return super.getInt(Columns.TOWN_HALL_LEVEL, 1);
    }
}