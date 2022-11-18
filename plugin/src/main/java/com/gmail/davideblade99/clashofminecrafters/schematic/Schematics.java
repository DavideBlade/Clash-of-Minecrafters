package com.gmail.davideblade99.clashofminecrafters.schematic;

import javax.annotation.Nonnull;

public enum Schematics {
    ISLAND("Island"),
    GOLD_EXTRACTOR("GoldExtractor"),
    ELIXIR_EXTRACTOR("ElixirExtractor"),
    ARCHER("ArcherTower");


    private final String name;

    Schematics(@Nonnull final String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }
}