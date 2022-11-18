/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.raid;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Village;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;

public final class EntityDamageByEntity extends IslandListener {

    public EntityDamageByEntity(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method is responsible for setting the damage done by an archer based on the statistics
     * of the archer's tower
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDamagedByArcher(final EntityDamageByEntityEvent event) {
        final Entity target = event.getEntity();
        final Entity damager = event.getDamager();

        if (!isVillageWorld(target.getWorld())) // If target isn't in worlds of island
            return;
        if (!(target instanceof Player)) // If target isn't a player
            return;
        if (!(damager instanceof Arrow))  // If the player isn't damaged by an arrow
            return;

        final ProjectileSource killer = ((Projectile) damager).getShooter();
        if (!(killer instanceof Skeleton)) // If the damager isn't a skeleton
            return;

        final Skeleton archer = (Skeleton) killer;
        final Double archerTowerDamage = archer.getPersistentDataContainer().get(new NamespacedKey(plugin, "ArcherTowerDamage"), PersistentDataType.DOUBLE);
        if (archerTowerDamage == null) // If the skeleton is not an archer
            return;

        // Damage the player according to the statistics of the archer's tower
        event.setDamage(archerTowerDamage * 2);
    }

    /**
     * This method is responsible for checking that players do not hit the guardians or archers of
     * islands under attack by other players
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onIslandCreatureDamaged(final EntityDamageByEntityEvent event) {
        final Entity target = event.getEntity();
        if (!isVillageWorld(target.getWorld())) // If target isn't in worlds of island
            return;

        // Define attacker
        Player attacker = null;
        if (event.getDamager() instanceof Player)
            attacker = (Player) event.getDamager();
        else if (event.getDamager() instanceof Projectile)
            if (((Projectile) event.getDamager()).getShooter() instanceof Player)
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();

        if (attacker == null) // If attacker isn't a player
            return;

        if (target instanceof Zombie) {
            final Zombie guardian = (Zombie) target;
            if (!plugin.getGuardianHandler().isGuardian(guardian))
                return;

            // If the player hits the guardian of an island that is attacked by another player
            final Village attackedIsland = plugin.getWarHandler().getAttackedIsland(attacker);
            if (attackedIsland == null || !attackedIsland.owner.equals(plugin.getGuardianHandler().getOwner(guardian))) {
                event.setCancelled(true);
                MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.CANNOT_HIT_GUARDIAN));
            }
        } else if (target instanceof Skeleton) {
            final Skeleton archer = (Skeleton) target;
            if (!plugin.getArcherHandler().isArcher(archer))
                return;

            // If the player hits the archer of an island that is attacked by another player
            final Village attackedIsland = plugin.getWarHandler().getAttackedIsland(attacker);
            if (attackedIsland == null || !attackedIsland.owner.equals(plugin.getArcherHandler().getOwner(archer))) {
                event.setCancelled(true);
                MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.CANNOT_HIT_ARCHER));
            }
        }
    }
}