/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class ArcherHandler {

    private final CoM plugin;

    public ArcherHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
    }

    private final Map<String, Skeleton> archers = new HashMap<>(); // <Owner, Archer>

    public void spawn(@Nullable final String playerOwner, final double damage, @Nonnull final Location loc) {
        final Skeleton skeleton = (Skeleton) Bukkit.getWorld("Islands").spawnEntity(loc, EntityType.SKELETON);
        final EntityEquipment equip = skeleton.getEquipment();
        equip.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
        equip.setChestplateDropChance(0); // Prevent dropping of the chestplate
        skeleton.setCustomName("§6" + playerOwner + "'s archer");
        skeleton.setCustomNameVisible(true);
        skeleton.setCanPickupItems(false);
        skeleton.setRemoveWhenFarAway(false);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        skeleton.setHealth(20);
        skeleton.getPersistentDataContainer().set(new NamespacedKey(plugin, "ArcherTowerDamage"), PersistentDataType.DOUBLE, damage); // Persistent data (between reload/restart)

        archers.put(playerOwner, skeleton);
    }

    public void kill(@Nonnull final String playerOwner) {
        final Skeleton archer = archers.remove(playerOwner);
        if (archer != null)
            archer.remove();
    }

    public boolean isArcher(@Nonnull final Skeleton skeleton) {
        return archers.containsValue(skeleton);
    }

    @Nullable
    public String getOwner(@Nonnull final Skeleton skeleton) {
        for (Map.Entry<String, Skeleton> entry : archers.entrySet()) {
            if (entry.getValue().equals(skeleton))
                return entry.getKey();
        }
        return null;
    }
}