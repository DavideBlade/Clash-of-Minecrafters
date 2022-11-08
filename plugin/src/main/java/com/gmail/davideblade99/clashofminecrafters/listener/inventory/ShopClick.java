/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.inventory;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.listener.CoMListener;
import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import com.gmail.davideblade99.clashofminecrafters.menu.holder.MenuInventoryHolder;
import com.gmail.davideblade99.clashofminecrafters.menu.item.BaseItem;
import com.gmail.davideblade99.clashofminecrafters.menu.item.ConfigItem;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import javafx.util.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class ShopClick extends CoMListener {

    public ShopClick(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShopClick(final InventoryClickEvent event) {
        final Inventory topInv = event.getInventory();
        final InventoryHolder holder = topInv.getHolder();

        // If top inventory is not a shop (which also includes the upgrade shop)
        if (!(holder instanceof MenuInventoryHolder))
            return;
        if (event.getClickedInventory() == null)
            return;

        final ItemStack clickedItem = event.getCurrentItem(); // Item that was clicked
        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return;

        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked(); // Player that clicked the item
        final Menu menu = ((MenuInventoryHolder) holder).getShop(); // Shop clicked
        for (BaseItem item : menu.getItems()) {
            if (event.getSlot() == item.getSlot()) {
                if (item instanceof ConfigItem) {
                    final ConfigItem configItem = (ConfigItem) item;
                    final Pair<Integer, Currency> requiredBalance = configItem.getRequiredBalance();
                    final ItemStack requiredItem = configItem.getRequiredItem();
                    if (requiredBalance != null) {
                        final int price = requiredBalance.getKey();
                        final Currency currency = requiredBalance.getValue();

                        // Check if the player has enough currency
                        if (price != 0 && currency != null) {
                            final User user = plugin.getUser(player);

                            if (user.getBalance(currency) < price) {
                                final String currencyTranslation;
                                switch (currency) {
                                    case GEMS:
                                        currencyTranslation = Messages.getMessage(MessageKey.GEMS);
                                        break;
                                    case GOLD:
                                        currencyTranslation = Messages.getMessage(MessageKey.GOLD);
                                        break;
                                    case ELIXIR:
                                        currencyTranslation = Messages.getMessage(MessageKey.ELIXIR);
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + currency);
                                }
                                ChatUtil.sendMessage(player, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
                                player.closeInventory();
                                return;
                            }
                        }
                    }

                    // Check if the player has the required item
                    if (requiredItem != null) {
                        if (!player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                            ChatUtil.sendMessage(player, Messages.getMessage(MessageKey.ITEM_NOT_FOUND));
                            player.closeInventory();
                            return;
                        }

                        player.getInventory().removeItem(requiredItem); // Remove sold item
                    }
                }


                player.closeInventory();
                item.onClick(plugin, player); // Execute custom actions on click
                return;
            }
        }
    }
}