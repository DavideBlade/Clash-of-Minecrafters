/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.configuration.Config;
import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.menu.item.BaseItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.UnclickableItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.UpgradeMenuItem;
import com.gmail.davideblade99.clashofminecrafters.player.User;
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
        final List<BaseItem> items = new ArrayList<>(4);

        byte slot = 10;
        BuildingType currentBuilding = BuildingType.ARCHER_TOWER;
        int currentLevel = user.getBuildingLevel(currentBuilding);
        items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));

        slot = 13;
        currentBuilding = BuildingType.ELIXIR_EXTRACTOR;
        currentLevel = user.getBuildingLevel(currentBuilding);
        items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));

        slot = 16;
        currentBuilding = BuildingType.GOLD_EXTRACTOR;
        currentLevel = user.getBuildingLevel(currentBuilding);
        items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));

        slot = 22;
        currentBuilding = BuildingType.TOWN_HALL;
        currentLevel = user.getBuildingLevel(currentBuilding);
        items.add(createBuildingItem(plugin, currentBuilding, currentLevel, slot));

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
    private static BaseItem createBuildingItem(@Nonnull final CoM plugin, @Nonnull final BuildingType building, final int level, final byte slot) {
        final Config config = plugin.getConfig();

        if (level < config.getMaxLevel(building))
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
