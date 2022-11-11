/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.island.creature;

import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.island.building.TownHall;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class GuardianHandler {

    private final Map<String, Zombie> guardians = new HashMap<>(); // <Owner, Guardian>

    /**
     * Spawns the guardian of the specified player's island.
     *
     * @param owner      Owner of the island where spawns the guardian
     * @param playerName Owner's name
     * @param loc        Location to place the guardian
     *
     * @since v3.1
     */
    public void spawn(@Nonnull final User owner, @Nonnull final String playerName, @Nonnull final Location loc) {
        final Zombie zombie = (Zombie) Bukkit.getWorld("Islands").spawnEntity(loc, EntityType.ZOMBIE);

        final TownHall townHall = (TownHall) owner.getBuilding(BuildingType.TOWN_HALL);
        if (townHall != null) // The town hall level may not exist
        {
            // Set equipment
            final EntityEquipment equip = zombie.getEquipment();
            if (townHall.helmet != null) {
                equip.setHelmet(new ItemStack(townHall.helmet, 1));
                equip.setHelmetDropChance(0); // Prevent dropping of the helmet
            }
            if (townHall.chestplate != null) {
                equip.setChestplate(new ItemStack(townHall.chestplate, 1));
                equip.setChestplateDropChance(0); // Prevent dropping of the chestplate
            }
            if (townHall.leggings != null) {
                equip.setLeggings(new ItemStack(townHall.leggings, 1));
                equip.setLeggingsDropChance(0); // Prevent dropping of the leggings
            }
            if (townHall.boots != null) {
                equip.setBoots(new ItemStack(townHall.boots, 1));
                equip.setBootsDropChance(0); // Prevent dropping of the boots
            }

            // Set potion effects
            if (townHall.potions != null && !townHall.potions.isEmpty()) {
                for (PotionEffectType potion : townHall.potions)
                    zombie.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE, 1, false, false), true);
            }

            // Set number of hearts
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(townHall.hearts * 2);
            zombie.setHealth(townHall.hearts * 2);
        }

        // General options
        zombie.setCustomName("§6" + playerName + "'s guardian");
        zombie.setCustomNameVisible(true);
        zombie.setBaby(false);
        zombie.setCanPickupItems(false);
        zombie.setRemoveWhenFarAway(false);

        guardians.put(playerName, zombie);
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