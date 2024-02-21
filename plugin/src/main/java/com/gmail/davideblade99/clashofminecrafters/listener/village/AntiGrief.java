/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.village;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.handler.VillageHandler;
import com.gmail.davideblade99.clashofminecrafters.listener.VillageListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Prevents damage to islands by players and game events (e.g., fire)
 *
 * @since 3.2
 */
public final class AntiGrief extends VillageListener {

    public AntiGrief(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();

        if (!VillageHandler.isVillageWorld(event.getBlock().getWorld()))
            return;

        if (!player.hasPermission(Permissions.ISLAND_BASE + "build")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (!VillageHandler.isVillageWorld(event.getBlock().getWorld()))
            return;

        if (!player.hasPermission(Permissions.ISLAND_BASE + "build")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
        final Entity entity = event.getRemover();
        if (!(entity instanceof Player))
            return;

        final Player player = (Player) entity;
        final Hanging hanging = event.getEntity();
        if (hanging.getType() != EntityType.ITEM_FRAME && hanging.getType() != EntityType.PAINTING)
            return;
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (!player.hasPermission(Permissions.ISLAND_BASE + "build")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onHangingPlace(final HangingPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player == null)
            return;

        final Hanging hanging = event.getEntity();
        if (hanging.getType() != EntityType.ITEM_FRAME && hanging.getType() != EntityType.PAINTING)
            return;
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (!player.hasPermission(Permissions.ISLAND_BASE + "build")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();

        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (!player.hasPermission(Permissions.ISLAND_BASE + "build")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();

        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (!player.hasPermission(Permissions.ISLAND_BASE + "build")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBurnBlock(final BlockBurnEvent event) {
        if (!VillageHandler.isVillageWorld(event.getBlock().getWorld()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (!VillageHandler.isVillageWorld(event.getSource().getWorld()) && !VillageHandler.isVillageWorld(event.getBlock().getWorld()))
            return;

        event.setCancelled(true);
    }

    /**
     * Prevents pistons from moving blocks
     *
     * @param event Piston extension event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (!VillageHandler.isVillageWorld(event.getBlock().getWorld()))
            return;

        // If the piston moves blocks
        if (!event.getBlocks().isEmpty())
            event.setCancelled(true);
    }

    /**
     * Prevents pistons from moving blocks
     *
     * @param event Piston retraction event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (!VillageHandler.isVillageWorld(event.getBlock().getWorld()))
            return;
        if (!event.isSticky())
            return;

        // If the piston moves blocks
        if (!event.getBlocks().isEmpty())
            event.setCancelled(true);
    }

    /**
     * Prevents theft of items from others' islands
     *
     * @param event Inventory event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final InventoryHolder invHolder = event.getInventory().getHolder();
        if (invHolder == null)
            return;

        final Player player = (Player) event.getPlayer();
        if (!(invHolder instanceof Horse))
            return;
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(((Horse) invHolder).getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from throwing items into others' islands
     *
     * @param event Drop event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Item item = event.getItemDrop();

        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "drop"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(item.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from interacting and modifying with objects (e.g. repeaters) on others' islands
     *
     * @param event Interaction event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        final ItemStack item = event.getItem();

        if (block == null)
            return;
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
            return;
        }

        final Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR)
            return;

        if (item != null) {
            final Material itemMat = item.getType();

            if (itemMat.name().endsWith("EGG")
                    || itemMat.name().endsWith("_BOAT")
                    || itemMat.name().endsWith("MINECART")
                    || itemMat == Material.ITEM_FRAME
                    || itemMat == Material.PAINTING
                    || itemMat == Material.ARMOR_STAND) {
                if (!island.isInsideVillage(block.getLocation())) {
                    event.setCancelled(true);
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
                    return;
                }
            }
        }

        final Material blockMat = block.getType();
        if (Tag.BUTTONS.isTagged(blockMat)
                || blockMat == Material.LEVER
                || Tag.WOODEN_PRESSURE_PLATES.isTagged(blockMat)
                || blockMat == Material.STONE_PRESSURE_PLATE
                || blockMat == Material.HEAVY_WEIGHTED_PRESSURE_PLATE
                || blockMat == Material.LIGHT_WEIGHTED_PRESSURE_PLATE
                || blockMat == Material.HOPPER
                || blockMat == Material.DISPENSER
                || blockMat == Material.DROPPER
                || blockMat == Material.CHEST
                || blockMat == Material.TRAPPED_CHEST
                || blockMat == Material.FURNACE
                || blockMat == Material.BLAST_FURNACE
                || blockMat == Material.COMPARATOR
                || blockMat == Material.REPEATER
                || Tag.WOODEN_DOORS.isTagged(blockMat)
                || Tag.WOODEN_TRAPDOORS.isTagged(blockMat)
                || blockMat.name().endsWith("_GATE")
                || blockMat.name().endsWith("MINECART")
                || blockMat == Material.BEACON) {
            if (!island.isInsideVillage(block.getLocation())) {
                event.setCancelled(true);
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
            }
        }
    }

    /**
     * Prevents players from interacting with armor stands on others' islands
     *
     * @param event Interaction event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();

        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;
        if (entity.getType() != EntityType.ARMOR_STAND)
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(entity.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from interacting with entities on others' islands
     *
     * @param event Interaction event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractWithEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity clicked = event.getRightClicked();

        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;
        if (clicked instanceof Painting)
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(clicked.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from shearing entities in others' islands
     *
     * @param event Shearing event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerShearEntity(final PlayerShearEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getEntity();

        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(entity.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }


    /**
     * Prevents destruction of armor stands and item frames
     *
     * @param event Damage event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDamageEntity(final EntityDamageByEntityEvent event) {
        final Entity target = event.getEntity();
        if (!VillageHandler.isVillageWorld(target.getWorld()))
            return;
        if (target.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;
        if (target.getType() != EntityType.ARMOR_STAND && target.getType() != EntityType.ITEM_FRAME)
            return;

        // Define attacker
        Player attacker = null;
        if (event.getDamager() instanceof Player)
            attacker = (Player) event.getDamager();
        else if (event.getDamager() instanceof Projectile)
            if (((Projectile) event.getDamager()).getShooter() instanceof Player)
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();

        // If attacker is a player
        if (attacker != null) {
            event.setCancelled(true);
            MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from picking up items from others' islands
     *
     * @param event Pickup event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItem(final EntityPickupItemEvent event) {
        final LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        final Item item = event.getItem();
        final Player player = (Player) entity;
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "pickup"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(item.getLocation())) {
            event.setCancelled(true);
            item.setPickupDelay(50);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from destroying vehicles
     *
     * @param event Damage event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleDamage(final VehicleDamageEvent event) {
        final Entity damager = event.getAttacker();
        if (!(damager instanceof Player))
            return;
        if (!VillageHandler.isVillageWorld(damager.getWorld()))
            return;

        final Player player = (Player) damager;
        if (!player.hasPermission(Permissions.ISLAND_BASE + "interact")) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from using vehicles on others' islands
     *
     * @param event Damage event
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        final Entity entered = event.getEntered();
        if (!(entered instanceof Player))
            return;
        if (!VillageHandler.isVillageWorld(entered.getWorld()))
            return;

        final Player player = (Player) entered;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(event.getVehicle().getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    /**
     * Prevents players from damaging each other
     *
     * @param event Damage event
     *
     * @since 3.2.2
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDamageByPlayer(final EntityDamageByEntityEvent event) {
        final Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        // Define shooter
        if (damager instanceof Projectile)
            if (((Projectile) damager).getShooter() instanceof Player)
                damager = (Player) ((Projectile) event.getDamager()).getShooter();

        if (damaged.hasMetadata("NPC") || damager.hasMetadata("NPC"))
            return; // Damaged or damager is a NPC
        if (!(damager instanceof Player) || !(damaged instanceof Player))
            return; // Damaged or damager is not a player

        event.setCancelled(true); // PvP not allowed
    }

    /**
     * Prevents players from damaging each other via potions
     *
     * @param event Potion splash event
     *
     * @since 3.2.2
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDamageByPlayer(final PotionSplashEvent event) {
        final Collection<LivingEntity> damaged = event.getAffectedEntities();
        final ProjectileSource thrower = event.getPotion().getShooter();

        if (!(thrower instanceof Player))
            return;
        if (((LivingEntity) thrower).hasMetadata("NPC"))
            return;

        // Thrower is a real player
        for (LivingEntity e : damaged)
            if (e instanceof Player && !e.hasMetadata("NPC")) // If the damaged entity is a player
                event.setIntensity(e, 0); // Do not apply the effect to the player
    }
}
