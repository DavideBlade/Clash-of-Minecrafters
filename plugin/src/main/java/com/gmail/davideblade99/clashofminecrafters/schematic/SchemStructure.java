/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size3D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.LinkedHashMap;

final class SchemStructure implements Serializable {

    private static final long serialVersionUID = -8473363306875763620L;

    private final Size3D size;
    private final LinkedHashMap<Vector, Block> blocks; // Blocks with relative position into cuboid

    SchemStructure(@Nonnull final Size3D structureSize, @Nonnull final LinkedHashMap<Vector, Block> blocks) {
        this.size = structureSize;
        this.blocks = blocks;
    }

    @Nonnull
    Size3D getSize() {
        return size;
    }

    @Nonnull
    LinkedHashMap<Vector, Block> getBlocks() {
        return blocks;
    }
}