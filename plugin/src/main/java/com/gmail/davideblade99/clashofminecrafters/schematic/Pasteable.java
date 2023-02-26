package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.building.Upgradeable;

import javax.annotation.Nullable;

/**
 * Interface representing all buildings that have a physical structure, obtained by pasting a schematic
 *
 * @author DavideBlade
 * @see Upgradeable
 * @since v3.1.4
 */
public interface Pasteable {

    /**
     * @return The name of the schematic file to be pasted in when the player buys the building. {@code Null} if no
     * schematic is to be pasted.
     *
     * @implSpec In the case of {@link Upgradeable} buildings, the first level should always return {@code null},
     * as the player has not yet unlocked anything.
     * @implSpec The name returned should not contain the ".schematic" extension but only the file name.
     */
    @Nullable
    String getRelatedSchematic();
}
