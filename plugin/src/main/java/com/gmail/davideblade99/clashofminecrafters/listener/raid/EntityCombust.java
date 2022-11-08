/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.raid;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;

import javax.annotation.Nonnull;

public final class EntityCombust extends IslandListener {

    public EntityCombust(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * Method that takes care of blocking burning damage caused by the sun
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityCombustBySun(final EntityCombustEvent event) {
        final Entity entity = event.getEntity();
        if (!isIslandWorld(entity.getWorld()))
            return;
        if (event instanceof EntityCombustByEntityEvent || event instanceof EntityCombustByBlockEvent)
            return;

        if (entity instanceof Zombie) {
            if (!plugin.getGuardianHandler().isGuardian((Zombie) entity))
                return;

            event.setCancelled(true);
        } else if (entity instanceof Skeleton) {
            if (!plugin.getArcherHandler().isArcher((Skeleton) entity))
                return;

            event.setCancelled(true);
        }
    }
}