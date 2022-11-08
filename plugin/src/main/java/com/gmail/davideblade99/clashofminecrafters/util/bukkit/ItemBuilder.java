/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import javafx.util.Pair;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Utility class that makes creating ItemStacks easier
 */
public final class ItemBuilder {

    private Material material;
    private int amount;
    private short damage;

    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private Color leatherColor;
    private String skullOwner;
    private List<Pattern> bannerPatterns;
    private boolean unbreakable;
    private boolean hideAttributes;
    private List<Pair<NamespacedKey, String>> persistentData;

    /**
     * Create a new ItemBuilder from scratch
     */
    public ItemBuilder() {
        this(null);
    }

    /**
     * Create a new ItemBuilder from scratch
     *
     * @param mat The material to create the ItemBuilder with
     */
    public ItemBuilder(@Nullable final Material mat) {
        this(mat, 1);
    }

    /**
     * Create a new ItemBuilder from scratch
     *
     * @param mat    The material of the item
     * @param amount The amount of the item
     */
    public ItemBuilder(@Nullable final Material mat, final int amount) {
        this(mat, amount, (short) 0);
    }

    /**
     * Create a new ItemBuilder from scratch
     *
     * @param mat    The material of the item
     * @param amount The amount of the item
     * @param damage The item's damage
     */
    public ItemBuilder(@Nullable final Material mat, final int amount, final short damage) {
        this.material = mat == null ? Material.AIR : mat;
        this.amount = (amount > this.material.getMaxStackSize() || amount <= 0) ? 1 : amount;
        this.damage = damage < 0 ? 0 : damage;
    }

    /**
     * Set the material of the item
     *
     * @param material The material to set
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setMaterial(@Nullable final Material material) {
        this.material = material == null ? Material.AIR : material;
        return this;
    }

    /**
     * Change the amount of the item
     *
     * @param amount The amount to set
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setAmount(final int amount) {
        this.amount = (amount > this.material.getMaxStackSize() || amount <= 0) ? 1 : amount;
        return this;
    }

    /**
     * Change the durability (damage) of the item
     *
     * @param damage The damage to set it to
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setDamage(final short damage) {
        this.damage = damage < 0 ? 0 : damage;
        return this;
    }

    /**
     * Set the display name of the item
     *
     * @param name The name to change it to
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setName(@Nullable final String name) {
        this.name = name;
        return this;
    }

    /**
     * Add an enchantment to the item
     *
     * @param ench The enchantment to add
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder addEnchant(@Nonnull final Enchantment ench) {
        return addEnchant(ench, 1);
    }

    /**
     * Add an enchantment to the item
     *
     * @param ench  The enchantment to add
     * @param level The level
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder addEnchant(@Nonnull final Enchantment ench, final int level) {
        if (enchantments == null)
            enchantments = new HashMap<>();

        enchantments.put(ench, level);
        return this;
    }

    /**
     * Remove a certain enchant from the item
     *
     * @param ench The enchantment to remove
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder removeEnchantment(@Nonnull final Enchantment ench) {
        if (enchantments == null)
            return this;

        enchantments.remove(ench);
        return this;
    }

    /**
     * Add multiple enchants at once
     *
     * @param enchantments The enchants to add
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setEnchantments(@Nullable final Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;

        return this;
    }

    /**
     * Re-sets the lore
     *
     * @param lore The lore to set it to
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setLore(@Nullable final String... lore) {
        return setLore(lore == null ? null : Arrays.asList(lore));
    }

    /**
     * Re-sets the lore
     *
     * @param lore The lore to set it to
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setLore(@Nullable final List<String> lore) {
        if (lore == null || lore.isEmpty())
            this.lore = null;
        else {
            this.lore = new ArrayList<>(lore.size());

            for (String line : lore)
                this.lore.add(ColorUtil.colour(line));
        }

        return this;
    }

    /**
     * Adds an empty lore line. This can be used to skip a line, a.k.a. a spacer.
     *
     * @return This ItemBuilder
     */
    @Contract(value = "-> this", mutates = "this")
    @Nonnull
    public ItemBuilder addLoreSpacer() {
        return addLoreLine(null);
    }

    /**
     * Add a lore line translating the colour code
     *
     * @param line The lore line to add
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder addLoreLine(@Nullable final String line) {
        if (lore == null)
            lore = new ArrayList<>();

        lore.add(line == null ? "" : ColorUtil.colour(line));
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece
     *
     * @param leatherColor The color to set it to
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setLeatherArmorColor(@Nullable final Color leatherColor) {
        this.leatherColor = leatherColor;

        return this;
    }

    /**
     * Sets the owner of the skull
     *
     * @param skullOwner The name of the Mojang account to get the skin from
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setSkullOwner(@Nullable final String skullOwner) {
        this.skullOwner = skullOwner;

        return this;
    }

    /**
     * Sets banner patterns
     *
     * @param bannerPatterns Patterns to set
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setBannerPatterns(@Nullable final Pattern... bannerPatterns) {
        return setBannerPatterns(bannerPatterns == null ? null : Arrays.asList(bannerPatterns));
    }

    /**
     * Sets banner patterns
     *
     * @param bannerPatterns Patterns to set
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder setBannerPatterns(@Nullable final List<Pattern> bannerPatterns) {
        this.bannerPatterns = bannerPatterns;

        return this;
    }

    /**
     * Makes or removes the Unbreakable Flag from the item
     *
     * @param unbreakable If it should be unbreakable
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder unbreakable(final boolean unbreakable) {
        this.unbreakable = unbreakable;

        return this;
    }

    /**
     * Show or hide all text details (damage, enchantments, potions...)
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder hideAttributes(final boolean hideAttributes) {
        this.hideAttributes = hideAttributes;

        return this;
    }

    /**
     * Insert persistent information (not lost at server restart) inside the item
     *
     * @param key   the key under which this value will be stored
     * @param value the value to store
     *
     * @return This ItemBuilder
     */
    @Contract(value = "_, _ -> this", mutates = "this")
    @Nonnull
    public ItemBuilder storePersistentData(@Nonnull final NamespacedKey key, @Nonnull final String value) {
        if (persistentData == null)
            persistentData = new ArrayList<>();

        persistentData.add(new Pair<>(key, value));
        return this;
    }

    /**
     * Retrieves the ItemStack from the ItemBuilder
     *
     * @return The ItemStack created/modified by the ItemBuilder instance
     */
    @Contract(value = "-> new", pure = true)
    @Nonnull
    public ItemStack build() {
        final ItemStack item = new ItemStack(material, amount);

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) // Item (AIR) hasn't item meta
            return item;

        if (name != null)
            meta.setDisplayName(ColorUtil.colour(name));
        if (lore != null)
            meta.setLore(lore);
        if (damage > 0 && meta instanceof Damageable)
            ((Damageable) meta).setDamage(damage);
        if (leatherColor != null && meta instanceof LeatherArmorMeta)
            ((LeatherArmorMeta) meta).setColor(leatherColor);
        if (skullOwner != null && meta instanceof SkullMeta) {
            ((SkullMeta) meta).setOwner(skullOwner);

            /* Non-deprecated method (using UUIDs, so skullOwner is a UUID) but only works if the player is online (at the time this set is executed):
             *
             * ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner));
             *
             * The cause seems to be the missing name when creating a "new GameProfile(...)" (indeed, null is passed in the non-deprecated method).
             * The other difference is in the UUID, but it does not seem to make any difference.
             */
        }
        if (bannerPatterns != null && meta instanceof BannerMeta)
            ((BannerMeta) meta).setPatterns(bannerPatterns);
        if (unbreakable)
            meta.setUnbreakable(true);
        if (hideAttributes)
            meta.addItemFlags(ItemFlag.values());
        if (persistentData != null) {
            for (Pair<NamespacedKey, String> data : persistentData)
                meta.getPersistentDataContainer().set(data.getKey(), PersistentDataType.STRING, data.getValue());
        }

        item.setItemMeta(meta);

        if (enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
                item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }

        return item;
    }
}