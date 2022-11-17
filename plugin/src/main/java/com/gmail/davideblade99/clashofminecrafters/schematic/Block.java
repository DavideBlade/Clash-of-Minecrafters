/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import javax.annotation.Nonnull;
import java.io.Serializable;

public final class Block implements Serializable {

    private static final long serialVersionUID = -8890912475002417906L;

    private final String data;

    public Block(@Nonnull final org.bukkit.block.Block block) {
        this(block.getBlockData().getAsString());
    }

    private Block(@Nonnull final String data) {
        this.data = data;
    }

    @Nonnull
    public String getData() {
        return data;
    }
}