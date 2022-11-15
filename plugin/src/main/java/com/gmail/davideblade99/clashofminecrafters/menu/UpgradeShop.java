/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.menu.item.BaseItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.UnclickableItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.UpgradeMenuItem;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.setting.Config;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UpgradeShop extends Menu {

    private final static String MAX_LEVEL = "§cMaximum level reached";

    public UpgradeShop(@Nonnull final CoM plugin, @Nonnull final User user) {
        super("§cUpgrades shop", (byte) 36, buildMenuItems(plugin, user));
    }

    /**
     * Creates items to be used in the upgrade shop based on the user's statistics (e.g. level of unlocked
     * constructions)
     *
     * @param plugin Plugin instance
     * @param user   User from whom to retrieve information
     *
     * @return list of {@link BaseItem} in the shop
     */
    @Nonnull
    private static List<BaseItem> buildMenuItems(@Nonnull final CoM plugin, @Nonnull final User user) {
        final Config config = plugin.getConfig();
        final List<BaseItem> items = new ArrayList<>(4);

        // Show item only if building is not disabled
        if (config.isBuildingEnabled(BuildingType.ARCHER_TOWER)) {
            final byte slot = 10;
            final BuildingType currentBuilding = BuildingType.ARCHER_TOWER;
            final int currentLevel = user.getBuildingLevel(currentBuilding);

            items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));
        }

        // Show item only if building is not disabled
        if (config.isBuildingEnabled(BuildingType.ELIXIR_EXTRACTOR)) {
            final byte slot = 13;
            final BuildingType currentBuilding = BuildingType.ELIXIR_EXTRACTOR;
            final int currentLevel = user.getBuildingLevel(currentBuilding);

            items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));
        }

        // Show item only if building is not disabled
        if (config.isBuildingEnabled(BuildingType.GOLD_EXTRACTOR)) {
            final byte slot = 16;
            final BuildingType currentBuilding = BuildingType.GOLD_EXTRACTOR;
            final int currentLevel = user.getBuildingLevel(currentBuilding);

            items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));
        }

        // Show item only if building is not disabled
        if (config.isBuildingEnabled(BuildingType.TOWN_HALL)) {
            final byte slot = 22;
            final BuildingType currentBuilding = BuildingType.TOWN_HALL;
            final int currentLevel = user.getBuildingLevel(currentBuilding);

            items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));
        }

        return items;
    }

    /**
     * Builds an item based on the level of construction
     *
     * @param plugin   Plugin instance
     * @param building Building type
     * @param level    Building level
     * @param slot     Slot in which to place the item
     *
     * @return A {@link BaseItem}
     *
     * @since v3.0.3
     */
    private static BaseItem createBuildingItem(@Nonnull final CoM plugin, @Nonnull final BuildingType building, int level, final byte slot) {
        final Config config = plugin.getConfig();

        /*
         * If any player has a level higher than the current maximum level
         * (e.g., some levels have been removed from the config.yml),
         * they are treated as if they have not purchased any upgrades
         * and must therefore start over.
         */
        final int maxLevel = config.getMaxLevel(building);
        if (level > maxLevel)
            level = building == BuildingType.TOWN_HALL ? 1 : 0; // The base level of the town hall is 1 while for other buildings is 0


        if (level < maxLevel)
            return new UpgradeMenuItem(config.getBuilding(building, level + 1).getItem(plugin), slot, building);
        else // Max level
        {
            final ItemStack item = config.getBuilding(building, level).getItem(plugin);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null)
                meta.setLore(Collections.singletonList(MAX_LEVEL));
            item.setItemMeta(meta);

            return new UnclickableItem(item, slot);
        }
    }
}
