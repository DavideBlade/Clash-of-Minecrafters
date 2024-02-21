/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.village;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidWinEvent;
import com.gmail.davideblade99.clashofminecrafters.listener.VillageListener;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

/**
 * Manages the events related to the death of building troops
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public final class BuildingTroopDeath extends VillageListener {

    public BuildingTroopDeath(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method is responsible for checking when a guardian is killed during a raid
     *
     * @param event Death event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onGuardianDeath(final NPCDeathEvent event) {
        final NPC targetNPC = event.getNPC();
        if (!"Guardian".equals(targetNPC.data().get("BuildingTroop")))
            return; // Not a guardian

        final User defender = plugin.getUser(targetNPC.getOrAddTrait(Owner.class).getOwnerId());
        if (defender == null)
            throw new IllegalStateException("User of the owner of the NPC \"" + targetNPC.getId() + "\" was not found");

        final Village attackedVillage = defender.getVillage();
        if (attackedVillage == null)
            throw new IllegalStateException("Village of the player \"" + defender.getBase().getUniqueId() + "\" was not found");

        final Player attacker = plugin.getWarHandler().getAttacker(attackedVillage);
        if (attacker == null)
            throw new IllegalStateException("The village of \"" + defender.getBase().getUniqueId() + "\" is not under attack");

        Bukkit.getPluginManager().callEvent(new RaidWinEvent(attacker, defender));
    }
}