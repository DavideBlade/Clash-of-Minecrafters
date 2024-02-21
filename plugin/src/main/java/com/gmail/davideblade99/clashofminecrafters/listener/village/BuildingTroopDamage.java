/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.village;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.listener.VillageListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

/**
 * Manages the events related to the damage of building troops
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public final class BuildingTroopDamage extends VillageListener {

    public BuildingTroopDamage(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method is responsible for ensuring that players only hit the troops in the village they are attacking
     *
     * @param event Damage event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBuildingTroopDamaged(final NPCDamageByEntityEvent event) {
        final NPC targetNPC = event.getNPC();

        if (!targetNPC.data().has("BuildingTroop"))
            return; // Not a building troop

        // Define attacker
        Player attacker = null;
        if (event.getDamager() instanceof Player)
            attacker = (Player) event.getDamager();
        else if (event.getDamager() instanceof Projectile)
            if (((Projectile) event.getDamager()).getShooter() instanceof Player)
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();

        // If the attacker isn't a player
        if (attacker == null) {
            event.setCancelled(true); // Troops can be attacked only by players
            return;
        }

        final Village attackedVillage = plugin.getWarHandler().getAttackedVillage(attacker);
        if (attackedVillage != null && targetNPC.getOrAddTrait(Owner.class).isOwnedBy(attackedVillage.owner))
            return; // A player can only hit the building troops of the village they are attacking

        event.setCancelled(true);
        MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.CANNOT_HIT_TROOP));
    }
}