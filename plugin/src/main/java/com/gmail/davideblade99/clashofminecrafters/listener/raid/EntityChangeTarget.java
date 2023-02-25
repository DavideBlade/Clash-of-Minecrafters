/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.raid;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import javax.annotation.Nonnull;

public final class EntityChangeTarget extends IslandListener {

    public EntityChangeTarget(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method is responsible for making sure that archers and guardians always target only the attacking
     * player
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeTarget(final EntityTargetLivingEntityEvent event) {
        final Entity newTarget = event.getTarget();
        final Entity attacker = event.getEntity();
        if (!isVillageWorld(attacker.getWorld())) // If target isn't in worlds of island
            return;

        if (attacker instanceof Skeleton) {
            final Skeleton archer = (Skeleton) attacker;
            if (!plugin.getArcherHandler().isArcher(archer))
                return;

            final Player islandAttacker = plugin.getWarHandler().getAttacker(plugin.getUser(plugin.getArcherHandler().getOwner(archer)).getVillage());
            if (islandAttacker == null)
                throw new IllegalStateException("Attacker not found");

            if (islandAttacker.equals(newTarget))
                return; // Target already correct

            event.setTarget(islandAttacker);
        } else if (attacker instanceof Zombie) {
            final Zombie guardian = (Zombie) attacker;
            if (!plugin.getGuardianHandler().isGuardian(guardian))
                return;

            final Player islandAttacker = plugin.getWarHandler().getAttacker(plugin.getUser(plugin.getGuardianHandler().getOwner(guardian)).getVillage());
            if (islandAttacker == null)
                throw new IllegalStateException("Attacker not found");

            if (islandAttacker.equals(newTarget))
                return; // Target already correct

            event.setTarget(islandAttacker);
        }
    }
}
