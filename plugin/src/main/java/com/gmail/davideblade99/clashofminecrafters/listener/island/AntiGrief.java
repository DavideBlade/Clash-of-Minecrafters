/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.island;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
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
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class AntiGrief extends IslandListener {

    public AntiGrief(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!isVillageWorld(block.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "build"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(block.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!isVillageWorld(block.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "build"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(block.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBurnBlock(final BlockBurnEvent event) {
        if (!isVillageWorld(event.getBlock().getWorld()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (!isVillageWorld(event.getSource().getWorld()) && !isVillageWorld(event.getBlock().getWorld()))
            return;

        event.setCancelled(true);
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
        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "build"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(hanging.getLocation())) {
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
        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "build"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(hanging.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (!isVillageWorld(event.getBlock().getWorld()))
            return;

        // If the piston moves blocks
        if (!event.getBlocks().isEmpty())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (!isVillageWorld(event.getBlock().getWorld()))
            return;
        if (!event.isSticky())
            return;

        // If the piston moves blocks
        if (!event.getBlocks().isEmpty())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        final InventoryHolder invHolder = event.getInventory().getHolder();
        if (invHolder == null)
            return;

        final Player player = (Player) event.getPlayer();
        if (!(invHolder instanceof Horse))
            return;
        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(((Horse) invHolder).getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();

        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(block.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();

        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(block.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Item item = event.getItemDrop();

        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "drop"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(item.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        final ItemStack item = event.getItem();

        if (block == null)
            return;
        if (!isVillageWorld(player.getWorld()))
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();

        if (!isVillageWorld(player.getWorld()))
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractWithEntity(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity clicked = event.getRightClicked();

        if (!isVillageWorld(player.getWorld()))
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDamageEntity(final EntityDamageByEntityEvent event) {
        final Entity target = event.getEntity();
        if (!isVillageWorld(target.getWorld()))
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

        // If attacker isn't player return
        if (attacker == null)
            return;

        final Village island = plugin.getUser(attacker).getVillage();
        if (island == null || !island.isInsideVillage(target.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupItem(final EntityPickupItemEvent event) {
        final LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        final Item item = event.getItem();
        final Player player = (Player) entity;
        if (!isVillageWorld(player.getWorld()))
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerShearEntity(final PlayerShearEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getEntity();

        if (!isVillageWorld(player.getWorld()))
            return;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(entity.getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleDamage(final VehicleDamageEvent event) {
        final Entity damager = event.getAttacker();
        if (!(damager instanceof Player))
            return;
        if (!isVillageWorld(damager.getWorld()))
            return;

        final Player player = (Player) damager;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(event.getVehicle().getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        final Entity entered = event.getEntered();
        if (!(entered instanceof Player))
            return;
        if (!isVillageWorld(entered.getWorld()))
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleExit(final VehicleExitEvent event) {
        final LivingEntity exited = event.getExited();
        if (!(exited instanceof Player))
            return;
        if (!isVillageWorld(exited.getWorld()))
            return;

        final Player player = (Player) exited;
        if (player.hasPermission(Permissions.ISLAND_BASE + "interact"))
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null || !island.isInsideVillage(event.getVehicle().getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NO_PERMISSION));
        }
    }
}
