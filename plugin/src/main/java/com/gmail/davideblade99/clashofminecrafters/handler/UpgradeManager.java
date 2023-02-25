/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Upgradeable;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Class that deals with the upgrading of the {@link Upgradeable} objects
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public final class UpgradeManager {

    private final CoM plugin;

    public UpgradeManager(@Nonnull final CoM plugin) {
        this.plugin = plugin;
    }

    /**
     * Check that the upgrade can be performed. It then checks that the player does not already have the maximum
     * level of the upgrade and is inside his island, in case a schematic needs to be pasted.
     *
     * @param player Player whose upgrade to check
     *
     * @throws IllegalStateException TODO
     */
    public void validateUpgrade(@Nonnull final Player player) {
        //TODO
    }

    public final class ArcherTower {

        /**
         * Upgrade the archer's tower for the specified player, after verifying conditions with {@link
         * #validateUpgrade(Player)}
         *
         * @param user User to upgrade the archer's tower to
         */
        public void upgrade(@Nonnull final User user) {
            //TODO
        }
    }

    public final class ElixirExtractor {
        public void upgrade() {
            //TODO
        }
    }

    public final class GoldExtractor {
        public void upgrade() {
            //TODO
        }
    }

    public final class TownHall {
        public void upgrade() {
            //TODO
        }
    }
}
