/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import javax.annotation.Nullable;

/**
 * Interface representing all buildings that have a physical structure, obtained by pasting a schematic
 *
 * @author DavideBlade
 * @since 3.2
 */
public interface Pasteable {

    /**
     * @return The name of the schematic file to be pasted in when the player buys the building. {@code Null} if no schematic
     * is to be pasted.
     * @implSpec The name returned should not contain the ".schematic" extension but only the file name.
     */
    @Nullable
    String getRelatedSchematic();
}
