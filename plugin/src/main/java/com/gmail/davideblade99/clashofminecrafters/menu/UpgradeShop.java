/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.menu.item.*;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.setting.Settings;
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
     * Creates items to be used in the upgrade shop based on the user's statistics (e.g. level of unlocked constructions)
     *
     * @param plugin Plugin instance
     * @param user   User from whom to retrieve information
     *
     * @return list of {@link BaseItem} in the shop
     */
    @Nonnull
    private static List<BaseItem> buildMenuItems(@Nonnull final CoM plugin, @Nonnull final User user) {
        final Settings config = plugin.getConfig();
        final List<BaseItem> items = new ArrayList<>(4);

        // Show item only if building is not disabled
        if (config.isArcherTowerEnabled())
            items.add(createArcherTowerUpgradeItem(plugin, user.getArcherTower() == null ? 0 : user.getArcherTower().getLevel(), (byte) 10));

        // Show item only if building is not disabled
        if (config.isElixirExtractorEnabled())
            items.add(createElixirExtractorUpgradeItem(plugin, user.getElixirExtractor() == null ? 0 : user.getElixirExtractor().getLevel(), (byte) 13));

        // Show item only if building is not disabled
        if (config.isGoldExtractorEnabled())
            items.add(createGoldExtractorUpgradeItem(plugin, user.getGoldExtractor() == null ? 0 : user.getGoldExtractor().getLevel(), (byte) 16));

        // Show item only if building is not disabled
        if (config.isTownHallEnabled())
            items.add(createTownHallUpgradeItem(plugin, user.getTownHallLevel(), (byte) 22));

        return items;
    }

    /**
     * Builds the item representing the archer's tower in the menu of upgrades (/upgrade)
     *
     * @param plugin Plugin instance
     * @param level  Current building level
     * @param slot   Slot in which to place the item
     *
     * @return A new {@link BaseItem}
     * @since 3.2
     */
    private static BaseItem createArcherTowerUpgradeItem(@Nonnull final CoM plugin, final int level, final byte slot) {
        final Settings config = plugin.getConfig();
        final int maxLevel = config.getMaxArcherTowerLevel();

        if (level < maxLevel)
            return new ArcherTowerUpgradeItem(plugin, config.getExistingArcherTower(level + 1), slot);
        else // Max level
        {
            final ItemStack item = config.getExistingArcherTower(maxLevel).getItem(plugin);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null)
                meta.setLore(Collections.singletonList(MAX_LEVEL));
            item.setItemMeta(meta);

            return new UnclickableItem(item, slot);
        }
    }

    /**
     * Builds the item representing the gold extractor in the menu of upgrades (/upgrade)
     *
     * @param plugin Plugin instance
     * @param level  Current building level
     * @param slot   Slot in which to place the item
     *
     * @return A new {@link BaseItem}
     * @since 3.2
     */
    private static BaseItem createGoldExtractorUpgradeItem(@Nonnull final CoM plugin, final int level, final byte slot) {
        final Settings config = plugin.getConfig();
        final int maxLevel = config.getMaxGoldExtractorLevel();

        if (level < maxLevel)
            return new GoldExtractorUpgradeItem(plugin, config.getExistingGoldExtractor(level + 1), slot);
        else // Max level
        {
            final ItemStack item = config.getExistingGoldExtractor(maxLevel).getItem(plugin);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null)
                meta.setLore(Collections.singletonList(MAX_LEVEL));
            item.setItemMeta(meta);

            return new UnclickableItem(item, slot);
        }
    }

    /**
     * Builds the item representing the elixir extractor in the menu of upgrades (/upgrade)
     *
     * @param plugin Plugin instance
     * @param level  Current building level
     * @param slot   Slot in which to place the item
     *
     * @return A new {@link BaseItem}
     * @since 3.2
     */
    private static BaseItem createElixirExtractorUpgradeItem(@Nonnull final CoM plugin, final int level, final byte slot) {
        final Settings config = plugin.getConfig();
        final int maxLevel = config.getMaxElixirExtractorLevel();

        if (level < maxLevel)
            return new ElixirExtractorUpgradeItem(plugin, config.getExistingElixirExtractor(level + 1), slot);
        else // Max level
        {
            final ItemStack item = config.getExistingElixirExtractor(maxLevel).getItem(plugin);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null)
                meta.setLore(Collections.singletonList(MAX_LEVEL));
            item.setItemMeta(meta);

            return new UnclickableItem(item, slot);
        }
    }

    /**
     * Builds the item representing the town hall in the menu of upgrades (/upgrade)
     *
     * @param plugin Plugin instance
     * @param level  Current building level
     * @param slot   Slot in which to place the item
     *
     * @return A new {@link BaseItem}
     * @since 3.2
     */
    private static BaseItem createTownHallUpgradeItem(@Nonnull final CoM plugin, final int level, final byte slot) {
        final Settings config = plugin.getConfig();
        final int maxLevel = config.getMaxTownHallLevel();

        if (level < maxLevel)
            return new TownHallUpgradeItem(plugin, config.getExistingTownHall(level + 1), slot);
        else // Max level
        {
            final ItemStack item = config.getExistingTownHall(maxLevel).getItem(plugin);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null)
                meta.setLore(Collections.singletonList(MAX_LEVEL));
            item.setItemMeta(meta);

            return new UnclickableItem(item, slot);
        }
    }
}
