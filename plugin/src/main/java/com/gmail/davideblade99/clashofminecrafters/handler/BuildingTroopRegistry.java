/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */


package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.setting.ArcherTowerLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.TownHallLevel;
import com.google.common.collect.ArrayListMultimap;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcmonkey.sentinel.SentinelTrait;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Responsible for creating, registering and tracking troops from buildings
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public class BuildingTroopRegistry {

    // Generated with https://mineskin.org/
    private final static String ARCHER_SKIN_SIGNATURE = "CyMtxk6MY/giGIYcbq+m0jVZ6b3tb0+H+FNLWV2+mnaYY9lVdLr8dPvAeg8CmI1jKjpzuoGgsEwCrFKvzbBS9zab7/PeDnQbHVMltGSzi5hB5Lqa+UJ1EY8yciYZ/HhkpTYOWeVKZoZ/HJr3pqWNHNEYxHSOlyz31NfxG/mv07f5YmoC8VgASi+QxJLZm4Jd5HZfXfHYGDuXtU75SxgBlRroi1Kd152GGB85Cw2fkrhg6aWTI1erpIRAL7pECV8KmXkwuHe0lLVZolpBGEf3H1SVRKpNfeEhLn4IKm4ZcJ6WBGTW6H1C9AxgXDKZrZj3rHAQlkwX55N6R3vSwtJvESr/Je76gLUYJXAPe40CumkaStDHeaZWI64RAKElSCq665mFQlxfGoShE7lC/XolniLyehA+S890usHXNJI0Vvwr7S7VdeGghaAaD04ranXZxtwt4co7xb1vXTs+f0dKAI3WnxxoINTmguI6Lbg7rWMdoCedcdQEC6WmsQsYFSqwTQVSrmokz5puGUeMTzSCxOystq6ANiwc4L9x836+sT4+j0Vyds0QRznWwisF0LOOt6Wk+XKdzoFJe+LwmK0bsRGU/YtR3LpJhcJJsKXFN/PrH9szAZKmgmKvEXG4VDD6mgANPH9aQF0WjOSXZ7OZimgK6WDeI8nHMf8xgVJWYfQ=";
    private final static String ARCHER_SKIN_VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTYzNzc3ODM3ODY4MiwKICAicHJvZmlsZUlkIiA6ICIzOWEzOTMzZWE4MjU0OGU3ODQwNzQ1YzBjNGY3MjU2ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkZW1pbmVjcmFmdGVybG9sIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1ODIyNmM4YzdhNGRhMzllNDRhYTRkYTZjY2Y1MTQzMTJmMDAzYzRkYjU4OTU5ODczYmRiYWZhYTE5NjRiYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    private final static String GUARDIAN_SKIN_SIGNATURE = "GwNoH9pRktjC1IApcXlZEvTwFco2559TUkO4z07jcybLSvrbWEQtu+oj6E0gekBcFCQW+XLCrLVdA4mDkKvgbWVgI3nc7Za4hiG+SLF0KSraBzgdXduibXnav7C6ssz4xVVCSK8QfGjl2XccRSZYqnJp6Y8YA9NpWn5JizEzj3P3ASwGywHXY/qLdXnsP0xKT41D8Vir2PwI0zWZXjycLMLyscUOD23OQhvJNXZzwvHaNHW0qkzVlDYth2Jj80oqoMj1Qydloea5c1+zpSCb/P8mPSRlGbO8mvREQkR9yeE/QEGHmK+y9gd3uRuyrOGNGfFiQQg/4cu+fZ1UuOtun0HT9pITUyfQ01CQS7Ts9hpsxBHntlQspQez8QMQccm738hCiE6J9RanHbh2HE/aEUiD0WIgQNqPwM5WxYyi7elfR/yMfaSqzc+J7afZxvscvTe96STGNJMBtI7q8RuCNo6bEq26HIkg0sENcVff6TZaYtW8eHbGpOZtZCmvqsLoi1dWssqxIbtMRCd/NhPObT9vrEqZpudvVR5MEKnBquDHkxSoXwRHVhy9iv5fDhyDxNPIHhbgdoDTUaNfqtjGisnyoGfvGjmHlQP+1h1CL58f9Fd9yfxU8r94ur6pyZnwV6CC3hL1dfeTsWm+4IVOveLgkIU0b5d6rYqQxd9HPJk=";
    private final static String GUARDIAN_SKIN_VALUE = "ewogICJ0aW1lc3RhbXAiIDogMTY0MjcxMzAwNTcwOSwKICAicHJvZmlsZUlkIiA6ICIxZjEyNTNhYTVkYTQ0ZjU5YWU1YWI1NmFhZjRlNTYxNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RNaUt5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJmMTBiZTY4ODdjZDVlMGMxZjU2NjczMzI2MTdmN2RmMzg5ZGY5MzlkNmVmMTZmZDc0ZWJhNzdhZjk1NzE4YzIiCiAgICB9CiAgfQp9";

    private final CoM plugin;
    private final ArrayListMultimap<Village, NPC> troops = ArrayListMultimap.create();

    public BuildingTroopRegistry(@Nonnull final CoM plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawns and registers a new guardian with the stats of the owner's town hall level that will stand still
     *
     * @param owner Owner of the village in which to spawn the guardian
     *
     * @see #createGuardian(User, Player)
     */
    public void createGuardian(@Nonnull final User owner) {
        createGuardian(owner, null);
    }

    /**
     * Spawns and registers a new guardian with the stats of the owner's town hall level, which will attack the target if
     * specified
     *
     * @param owner        Owner of the village in which to spawn the guardian
     * @param playerTarget Player to be attacked or {@code null} if the NPC must stand still
     *
     * @see NPCRegistry#createNPC(EntityType, String, Location)
     * @see SkinTrait
     * @see SentinelTrait
     * @see TownHallLevel
     */
    public void createGuardian(@Nonnull final User owner, @Nullable final Player playerTarget) {
        final NPC guardian = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§6Guardian");
        final SkinTrait skin = guardian.getOrAddTrait(SkinTrait.class);
        final SentinelTrait sentinel = guardian.getOrAddTrait(SentinelTrait.class);
        final TownHallLevel guardianStats = plugin.getConfig().getExistingTownHall(owner.getTownHallLevel());

        skin.setShouldUpdateSkins(false); // Fixed skin, no need to contact Mojang servers to update it
        skin.setFetchDefaultSkin(false); // Skin set up with signature, no need to contact Mojang servers
        skin.setSkinPersistent("Guardian", GUARDIAN_SKIN_SIGNATURE, GUARDIAN_SKIN_VALUE);

        sentinel.setInvincible(false);
        sentinel.setHealth(guardianStats.hearts * 2);
        sentinel.deathXP = 0;
        if (playerTarget != null)
            sentinel.addTarget("uuid:" + playerTarget.getUniqueId());

        guardian.getOrAddTrait(Owner.class).setOwner(owner.getBase().getUniqueId());

        guardian.data().setPersistent(NPC.Metadata.COLLIDABLE, false); // Prevents NPC from being pushed by players
        guardian.data().setPersistent(NPC.Metadata.FLUID_PUSHABLE, false); // Prevents NPC from being moved by liquids
        guardian.data().setPersistent(NPC.Metadata.PICKUP_ITEMS, false); // Prevents item pickup
        guardian.data().setPersistent(NPC.Metadata.DROPS_ITEMS, false); // Prevents item drops
        guardian.data().setPersistent(NPC.Metadata.DAMAGE_OTHERS, true); // Allows the NPC to damage other entities
        guardian.data().setPersistent(NPC.Metadata.TARGETABLE, false); // Prevents the NPC from being targeted by mobs
        guardian.data().setPersistent("BuildingTroop", "Guardian"); // Identifies the NPC as a guardian of this plugin

        guardian.spawn(owner.getVillage().getSpawn());

        troops.put(owner.getVillage(), guardian);
    }

    /**
     * Spawns and registers a new archer with the stats of the owner's town hall level that will stand still
     *
     * @param owner UUID of the player owning the archer
     *
     * @see #createArcher(User, Player)
     */
    public void createArcher(@Nonnull final User owner) {
        createArcher(owner, null);
    }

    /**
     * Spawns and registers a new archer with the stats of the owner's town hall level, which will attack the target if
     * specified
     *
     * @param owner        Owner of the village in which to spawn the guardian
     * @param playerTarget Player to be attacked or {@code null} if the NPC must stand still
     *
     * @see NPCRegistry#createNPC(EntityType, String, Location)
     * @see SkinTrait
     * @see SentinelTrait
     * @see TownHallLevel
     */
    public void createArcher(@Nonnull final User owner, @Nullable final Player playerTarget) {
        final NPC archer = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§6Archer");
        final Equipment equip = archer.getOrAddTrait(Equipment.class);
        final SkinTrait skin = archer.getOrAddTrait(SkinTrait.class);
        final SentinelTrait sentinel = archer.getOrAddTrait(SentinelTrait.class);
        final ArcherTowerLevel archerStats = plugin.getConfig().getExistingArcherTower(owner.getArcherTower().getLevel());

        equip.set(EquipmentSlot.CHESTPLATE, new ItemStack(Material.GOLDEN_CHESTPLATE));
        equip.set(EquipmentSlot.HAND, new ItemStack(Material.BOW));

        skin.setShouldUpdateSkins(false); // Fixed skin, no need to contact Mojang servers to update it
        skin.setFetchDefaultSkin(false); // Skin set up with signature, no need to contact Mojang servers
        skin.setSkinPersistent("Archer", ARCHER_SKIN_SIGNATURE, ARCHER_SKIN_VALUE);

        sentinel.setInvincible(false);
        sentinel.setHealth(20);
        sentinel.damage = archerStats.damage;
        sentinel.deathXP = 0;
        if (playerTarget != null)
            sentinel.addTarget("uuid:" + playerTarget.getUniqueId());

        archer.getOrAddTrait(Owner.class).setOwner(owner.getBase().getUniqueId());

        archer.data().setPersistent(NPC.Metadata.COLLIDABLE, false); // Prevents NPC from being pushed by players
        archer.data().setPersistent(NPC.Metadata.FLUID_PUSHABLE, false); // Prevents NPC from being moved by liquids
        archer.data().setPersistent(NPC.Metadata.PICKUP_ITEMS, false); // Prevents item pickup
        archer.data().setPersistent(NPC.Metadata.DROPS_ITEMS, false); // Prevents item drops
        archer.data().setPersistent(NPC.Metadata.DAMAGE_OTHERS, true); // Allows the NPC to damage other entities
        archer.data().setPersistent(NPC.Metadata.TARGETABLE, false); // Prevents the NPC from being targeted by mobs
        archer.data().setPersistent("BuildingTroop", "Archer"); // Identifies the NPC as an archer of this plugin

        archer.spawn(owner.getArcherTower().getArcherPos().toBukkitLocation(plugin.getVillageHandler().getVillageWorld()));

        troops.put(owner.getVillage(), archer);
    }

    /**
     * Removes NPCs in the specified player's village from the world and deregisters them
     *
     * @param owner Owner of NPCs to be removed
     */
    public void removeTroops(@Nonnull final User owner) {
        final List<NPC> npcs = troops.removeAll(owner.getVillage());
        for (NPC npc : npcs)
            npc.destroy();
    }
}