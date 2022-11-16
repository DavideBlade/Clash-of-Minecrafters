/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.yaml;

import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.BuildingType;
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
     * @return the timestamp when the player collected from the extractors
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

    @Nullable
    public Vector getArcherTowerPosition() {
        return Vector.fromString(super.getString(Columns.ARCHER_TOWER_LOCATION));
    }

    /**
     * Sets the archer tower location to the respective path
     *
     * @param location Location to set
     */
    public void setArcherTowerLocation(@Nullable final String location) {
        super.set(Columns.ARCHER_TOWER_LOCATION, location);
    }

    public int getBalance(@Nonnull final Currency currency) {
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
    public void setBalance(@Nonnull final Currency currency, final int amount) {
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

    public boolean hasBuilding(@Nonnull final BuildingType type) {
        switch (type) {
            case ARCHER_TOWER:
                return super.contains(Columns.ARCHER_TOWER_LEVEL);
            case GOLD_EXTRACTOR:
                return super.contains(Columns.GOLD_EXTRACTOR_LEVEL);
            case ELIXIR_EXTRACTOR:
                return super.contains(Columns.ELIXIR_EXTRACTOR_LEVEL);
            case TOWN_HALL:
                return super.contains(Columns.TOWN_HALL_LEVEL);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public int getBuildingLevel(@Nonnull final BuildingType type) {
        switch (type) {
            case ARCHER_TOWER:
                return super.getInt(Columns.ARCHER_TOWER_LEVEL);
            case GOLD_EXTRACTOR:
                return super.getInt(Columns.GOLD_EXTRACTOR_LEVEL);
            case ELIXIR_EXTRACTOR:
                return super.getInt(Columns.ELIXIR_EXTRACTOR_LEVEL);
            case TOWN_HALL:
                return super.getInt(Columns.TOWN_HALL_LEVEL, 1);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * Sets the level of the building specified to the respective path
     *
     * @param type  Building type to set
     * @param level New level to set
     */
    public void setBuildingLevel(@Nonnull final BuildingType type, final int level) {
        switch (type) {
            case ARCHER_TOWER:
                super.set(Columns.ARCHER_TOWER_LEVEL, level);
                break;

            case GOLD_EXTRACTOR:
                super.set(Columns.GOLD_EXTRACTOR_LEVEL, level);
                break;

            case ELIXIR_EXTRACTOR:
                super.set(Columns.ELIXIR_EXTRACTOR_LEVEL, level);
                break;

            case TOWN_HALL:
                super.set(Columns.TOWN_HALL_LEVEL, level);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}