/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.raid;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidWinEvent;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import javax.annotation.Nonnull;

public final class EntityDeath extends IslandListener {

    public EntityDeath(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method is responsible for checking when a guardian is killed
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onGuardianDeath(final EntityDeathEvent event) {
        final LivingEntity entityDeath = event.getEntity();
        if (!(entityDeath instanceof Zombie))
            return;
        if (!isVillageWorld(entityDeath.getWorld()))
            return;

        final Zombie zombie = (Zombie) entityDeath;
        if (!plugin.getGuardianHandler().isGuardian(zombie))
            return;

        // Remove exp and dropped items
        event.setDroppedExp(0);
        event.getDrops().clear();


        final Player attacker = plugin.getWarHandler().getAttacker(plugin.getUser(plugin.getGuardianHandler().getOwner(zombie)).getIsland());
        if (attacker == null)
            throw new IllegalStateException("Attacker not found");

        Bukkit.getPluginManager().callEvent(new RaidWinEvent(attacker, plugin.getWarHandler().getAttackedIsland(attacker).owner));
    }

    /**
     * This method is responsible for checking when an archer is killed
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onArcherDeath(final EntityDeathEvent event) {
        final LivingEntity entityDeath = event.getEntity();
        if (!(entityDeath instanceof Skeleton))
            return;
        if (!isVillageWorld(entityDeath.getWorld()))
            return;
        if (!plugin.getArcherHandler().isArcher((Skeleton) entityDeath))
            return;

        // Remove exp and dropped items
        event.setDroppedExp(0);
        event.getDrops().clear();
    }
}