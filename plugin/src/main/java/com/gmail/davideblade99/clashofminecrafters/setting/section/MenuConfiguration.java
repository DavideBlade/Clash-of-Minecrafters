/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import com.gmail.davideblade99.clashofminecrafters.menu.item.BaseItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.ConfigItem;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ColorUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Class representing the menu section in the config.yml
 *
 * @author DavideBlade
 * @since v3.1
 */
public final class MenuConfiguration extends SectionConfiguration {

    /** Name of the section in the YAML file */
    private final static String SECTION = "Menus";


    /** Map that associates the name of the menu with its settings */
    private final Map<String, Menu> menus;

    /**
     * Create a new instance of {@link MenuConfiguration} and load all the menus with {@link #loadMenus()}
     *
     * @param configuration Configuration containing the menu section
     */
    public MenuConfiguration(@Nonnull final CoMYamlConfiguration configuration) {
        super(configuration, SECTION);

        this.menus = super.section == null ? Collections.emptyMap() : new HashMap<>();

        loadMenus();
    }

    /**
     * @return The map containing all the (valid and) loaded menus and associating the menu name with its settings
     */
    public Map<String, Menu> getMenus() {
        return this.menus;
    }

    /**
     * Reads the menus section in the {@link SectionConfiguration#section} and builds {@link Menu}s. Menus
     * containing invalid or missing settings will be ignored.
     */
    private void loadMenus() {
        final ConfigurationSection menuSection = super.section;
        final Set<String> keys;

        // Check if menu list is empty
        if (menuSection == null || (keys = menuSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! There are no menus set up in the config.");
            return;
        }

        // Load all menus
        for (String menuName : keys) {
            byte size = (byte) menuSection.getInt(menuName + ".Settings.Row", -1);
            String title = menuSection.getString(menuName + ".Settings.Title", null);

            if (size < 1 || size > 6) {
                MessageUtil.sendError("The number of rows of the menu \"" + menuName + "\" isn't correct. The menu will be ignored.");
                continue;
            }
            size *= 9; // Total number of slots

            if (title == null) {
                MessageUtil.sendError("The name of the menu \"" + menuName + "\" is missing. The menu will be ignored.");
                continue;
            }
            title = ColorUtil.colour(title);


            /*
             * Load all items for current menu
             */
            // Check if there are no items
            final ConfigurationSection itemsConf = menuSection.getConfigurationSection(menuName + ".Items");
            if (itemsConf == null || itemsConf.getKeys(false).isEmpty()) {
                MessageUtil.sendError("Warning: the menu \"" + menuName + "\"  has no items!");
                MessageUtil.sendError("This menu will not be created.");
                continue;
            }

            this.menus.put(menuName.toLowerCase(), new Menu(title, size, loadItems(itemsConf, size)));
        }
    }

    /**
     * Load menu items from the passed section
     *
     * @param itemSection Section of the items in the menu whose items you want to load
     * @param menuSize    Size of the menu whose items are to be loaded
     *
     * @return A list of items loaded from the configuration
     */
    @Nonnull
    private List<BaseItem> loadItems(@Nonnull final ConfigurationSection itemSection, final byte menuSize) {
        final String menuName = itemSection.getParent().getName(); // Name of the menu whose items are being loaded
        final Set<String> keys = itemSection.getKeys(false);
        final List<BaseItem> items = new ArrayList<>(keys.size());

        for (String itemName : keys) {
            final ItemBuilder itemBuilder = new ItemBuilder();

            final Material material;
            short damage;
            final String displayName;
            final List<String> lore;
            final Color color;
            final String headOwner;
            final boolean hideAttribute;
            final byte slot;
            final Pair<Integer, Currencies> requiredBalance;
            final ItemStack requiredItem;
            final String command;

            final String materialStr = itemSection.getString(itemName + ".Item", null);
            if (materialStr == null) {
                MessageUtil.sendError("Missing material for item \"" + itemName + "\" in menu \"" + menuName + "\". This item will be ignored.");
                continue;
            }
            material = Material.matchMaterial(materialStr);
            if (material == null) {
                MessageUtil.sendError("Unknown material \"" + materialStr + "\" in menu \"" + menuName + "\". This item will be ignored.");
                continue;
            }
            itemBuilder.setMaterial(material);


            damage = (short) itemSection.getInt(itemName + ".Durability", 0);
            if (damage < 0) {
                MessageUtil.sendError("The durability of \"" + itemName + "\" item in menu \"" + menuName + "\" is negative. The durability will be ignored.");
                damage = 0;
            }
            itemBuilder.setDamage(damage);


            displayName = itemSection.getString(itemName + ".Name", null);
            itemBuilder.setName(displayName);


            /*
             * In the ConfigurationSection#getStringList() method,
             * it is not possible to pass the default value to be returned.
             * By default, the default value becomes equal to the value
             * in the config.yml embedded in the .jar.
             *
             * In the case where there is no lore set in the config.yml,
             * indicating that there should be no lore, #getStringList()
             * will return the default value (the one in the config.yml in the .jar)
             * which may not be null (and thus a lore will be set, which is wrong).
             *
             * To solve this problem, it is previously checked whether the lore is set (#isSet()) in the config.yml.
             */
            lore = itemSection.isSet(itemName + ".Lore") ? itemSection.getStringList(itemName + ".Lore") : null;
            itemBuilder.setLore(lore);


            // Necessary check to avoid the problem explained above
            if (itemSection.isSet(itemName + ".Enchantments"))
                itemBuilder.setEnchantments(loadEnchantments(itemSection, itemName));


            //TODO: se il formato o il colore è sbagliato non lo segnalo, semplicemente ignoro -> inviare notifica
            color = ColorUtil.matchColor(itemSection.getString(itemName + ".Color", null));
            itemBuilder.setLeatherArmorColor(color);


            headOwner = itemSection.getString(itemName + ".Skull", null);
                /* Method using UUIDs. Unfortunately, in the ItemBuilder when setting the owner in the ItemMeta the non-deprecated method (using UUIDs) does not work if the player is not online.
                UUID headOwner = null;
                try {
                    final String owner = itemsConf.getString(itemName + ".Skull", null);
                    if (owner != null)
                        headOwner = UUID.fromString(owner);
                }
                catch (final Exception ignored) {
                    // Wrong UUID format
                    ChatUtil.sendError("The skull UUID of \"" + itemName + "\" item in menu \"" + menuName + "\" is in the wrong format. Enchantment will be ignored.");
                }*/
            itemBuilder.setSkullOwner(headOwner);


            itemBuilder.setBannerPatterns(loadPatterns(itemSection, itemName));


            hideAttribute = !itemSection.getBoolean(itemName + ".Attributes", true);
            itemBuilder.hideAttributes(hideAttribute);


            slot = (byte) itemSection.getInt(itemName + ".Slot", -1);
            if (slot < 0 || slot >= menuSize) {
                MessageUtil.sendError("The slot of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. The item will be ignored.");
                continue;
            }


            final String requiredBalanceStr = itemSection.getString(itemName + ".Required balance", null);
            if (requiredBalanceStr == null)
                requiredBalance = null;
            else {
                final String[] split = requiredBalanceStr.split(",");
                if (split.length != 2) {
                    MessageUtil.sendError("Required balance for item \"" + itemName + "\" in menu \"" + menuName + "\" is in the wrong format. This item will be ignored.");
                    continue;
                }

                final int requiredAmount;
                try {
                    requiredAmount = Integer.parseInt(split[0].trim());
                } catch (final NumberFormatException ignored) {
                    MessageUtil.sendError("The currency amount \"" + split[0] + "\", in \"Required item\", of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. This item will be ignored.");
                    continue; // Wrong amount
                }
                if (requiredAmount < 0) {
                    MessageUtil.sendError("The currency amount of \"" + itemName + "\" item in menu \"" + menuName + "\" is negative. The item will be ignored.");
                    continue; // Negative amount
                }

                final Currencies requiredCurrency = Currencies.matchCurrency(split[1].trim());
                if (requiredCurrency == null) {
                    MessageUtil.sendError("The currency \"" + split[1] + "\", in \"Required item\", of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. This item will be ignored.");
                    continue; // Wrong currency
                }

                requiredBalance = new Pair<>(requiredAmount, requiredCurrency);
            }


            final String requiredItemStr = itemSection.getString(itemName + ".Required item", null);
            if (requiredItemStr != null && Material.matchMaterial(requiredItemStr) == null) {
                MessageUtil.sendError("Required item of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. The item will be ignored.");
                continue;
            }
            requiredItem = requiredItemStr != null ? new ItemBuilder(Material.matchMaterial(requiredItemStr)).build() : null;


            command = ColorUtil.colour(itemSection.getString(itemName + ".Command", null));


            items.add(new ConfigItem(itemBuilder.build(), slot, requiredBalance, requiredItem, command));
        }

        return items;
    }

    /**
     * Load, from the passed section, the enchantments of the specified item. Enchantments in an invalid format
     * will be ignored.
     *
     * @param itemSection Item section of a menu from which to get enchantments
     * @param itemName    Name of the item in the section whose enchantments are to be loaded
     *
     * @return A map containing the enchantments loaded with the corresponding level
     */
    private Map<Enchantment, Integer> loadEnchantments(@Nonnull final ConfigurationSection itemSection, @Nonnull final String itemName) {
        final String menuName = itemSection.getParent().getParent().getName(); // Name of the menu whose items are being loaded
        final List<String> enchantmentList = itemSection.getStringList(itemName + ".Enchantments");
        final Map<Enchantment, Integer> enchantments = Maps.newHashMapWithExpectedSize(enchantmentList.size());

        for (String enchant : enchantmentList) {
            final String[] split = enchant.split(",");
            if (split.length != 2) {
                MessageUtil.sendError("The enchantment \"" + enchant + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is in the wrong format. Enchantment will be ignored.");
                continue;
            }

            final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(split[0].replace(" ", "_").toLowerCase(Locale.ENGLISH)));
            if (enchantment == null) {
                MessageUtil.sendError("The enchantment \"" + split[0] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Enchantment will be ignored.");
                continue; // Wrong enchant name
            }

            final int level;
            try {
                level = Integer.parseInt(split[1].trim());
            } catch (final NumberFormatException ignored) {
                MessageUtil.sendError("The level of the enchantment \"" + split[0] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Enchantment will be ignored.");
                continue; // Wrong level format
            }

            enchantments.put(enchantment, level);
        }

        return enchantments;
    }

    /**
     * Load, from the passed section, the patterns of the specified item. Patterns in an invalid format will be
     * ignored.
     *
     * @param itemSection Item section of a menu from which to get pattern
     * @param itemName    Name of the item in the section whose patterns are to be loaded
     *
     * @return A list containing the patterns loaded
     */
    private List<Pattern> loadPatterns(@Nonnull final ConfigurationSection itemSection, @Nonnull final String itemName) {
        final String menuName = itemSection.getParent().getParent().getName(); // Name of the menu whose items are being loaded
        final List<String> patternList = itemSection.getStringList(itemName + ".Pattern");
        final List<Pattern> pattern = new ArrayList<>(patternList.size());

        for (String str : patternList) {
            final String[] split = str.split(",");
            if (split.length != 2) {
                MessageUtil.sendError("The banner pattern \"" + str + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is in the wrong format. Pattern will be ignored.");
                continue;
            }

            final DyeColor patternColor = ColorUtil.matchDyeColor(split[0]);
            if (patternColor == null) {
                MessageUtil.sendError("The pattern color \"" + split[0] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Pattern will be ignored.");
                continue; // Non-existent color
            }

            final PatternType patternType = PatternType.getByIdentifier(split[1].trim().toLowerCase(Locale.ENGLISH));
            if (patternType == null) {
                MessageUtil.sendError("The banner pattern \"" + split[1] + "\" of \"" + itemName + "\" item in menu \"" + menuName + "\" is incorrect. Pattern will be ignored.");
                continue; // Wrong pattern code
            }

            pattern.add(new Pattern(patternColor, patternType));
        }

        return pattern;
    }
}
