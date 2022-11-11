/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.yaml;

import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;

public final class IslandConfiguration extends CoMYamlConfiguration {

    /**
     * {@inheritDoc}
     */
    public IslandConfiguration(@Nonnull final File file, final boolean autoSave) {
        super(file, autoSave);
    }

    public IslandConfiguration(@Nonnull final File islandDataFile) {
        super(islandDataFile);
    }

    public int getX() {
        return super.getInt("X");
    }

    public int getZ() {
        return super.getInt("Z");
    }

    public void setX(final int x) {
        super.set("X", x);
    }

    public void setZ(final int z) {
        super.set("Z", z);
    }
}