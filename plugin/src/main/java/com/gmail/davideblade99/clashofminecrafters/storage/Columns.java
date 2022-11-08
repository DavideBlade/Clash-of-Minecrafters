/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage;

/**
 * Columns/keys of the database in which to store data. They can be, for example, columns (in the case of SQL
 * databases) or paths (in the case of YAML).
 */
public final class Columns {

    private Columns() {
        throw new IllegalAccessError();
    }

    public static final String UUID = "UUID";
    public static final String GEMS = "Gems";
    public static final String GOLD = "Gold";
    public static final String ELIXIR = "Elixir";
    public static final String TROPHIES = "Trophies";
    public static final String CLAN = "Clan";
    public static final String COLLECTION_TIME = "CollectionTime";
    public static final String ARCHER_TOWER_LEVEL = "ArcherTowerLevel";
    public static final String ARCHER_TOWER_LOCATION = "ArcherTowerLoc";
    public static final String GOLD_EXTRACTOR_LEVEL = "GoldExtractorLevel";
    public static final String ELIXIR_EXTRACTOR_LEVEL = "ElixirExtractorLevel";
    public static final String ISLAND_SPAWN = "IslandSpawn";
    public static final String ISLAND_ORIGIN = "IslandOrigin";
    public static final String ISLAND_SIZE = "IslandSize";
    public static final String ISLAND_EXPANSIONS = "IslandExpansions";
    public static final String TOWN_HALL_LEVEL = "TownHallLevel";
}
