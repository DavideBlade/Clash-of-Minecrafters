/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.island.creature;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class GuardianHandler {

    private final Map<String, Zombie> guardians = new HashMap<>(); // <Owner, Guardian>

    public void spawn(@Nonnull final String playerOwner, @Nonnull final Location loc) {
        final Zombie zombie = (Zombie) Bukkit.getWorld("Islands").spawnEntity(loc, EntityType.ZOMBIE);
        final EntityEquipment equip = zombie.getEquipment();
        equip.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
        equip.setHelmetDropChance(0); // Prevent dropping of the helmet
        zombie.setCustomName("§6" + playerOwner + "'s guardian");
        zombie.setCustomNameVisible(true);
        zombie.setBaby(false);
        zombie.setCanPickupItems(false);
        zombie.setRemoveWhenFarAway(false);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        zombie.setHealth(40);

        guardians.put(playerOwner, zombie);
    }

    /**
     * Kill, if it exists, the guardian protecting the specified player's island
     *
     * @param playerOwner Owner of the island
     */
    public void kill(@Nonnull final String playerOwner) {
        final Zombie guardian = guardians.remove(playerOwner);
        if (guardian != null)
            guardian.remove();
    }

    /**
     * @param zombie Zombie to be checked
     *
     * @return True if the zombie is a guardian of any island under attack, otherwise false
     */
    public boolean isGuardian(@Nonnull final Zombie zombie) {
        return guardians.containsValue(zombie);
    }

    @Nullable
    public String getOwner(@Nonnull final Zombie zombie) {
        for (Map.Entry<String, Zombie> entry : guardians.entrySet()) {
            if (entry.getValue().equals(zombie))
                return entry.getKey();
        }
        return null;
    }
}