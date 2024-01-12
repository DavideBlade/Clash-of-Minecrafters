/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Item configurable from config.yml
 *
 * @author DavideBlade
 * @since 3.2
 */
public final class ConfigurableItem extends BaseItem {

    private final Pair<Integer, Currencies> requiredBalance;
    private final ItemStack requiredItem;
    private final String command;

    public ConfigurableItem(@Nonnull final ItemStack item, final byte slot, @Nullable final String command) {
        this(item, slot, null, null, command);
    }

    public ConfigurableItem(@Nonnull final ItemStack item, final byte slot, @Nullable final Pair<Integer, Currencies> requiredBalance, @Nullable final String command) {
        this(item, slot, requiredBalance, null, command);
    }

    public ConfigurableItem(@Nonnull final ItemStack item, final byte slot, @Nullable final ItemStack requiredItem, @Nullable final String command) {
        this(item, slot, null, requiredItem, command);
    }

    public ConfigurableItem(@Nonnull final ItemStack item, final byte slot, @Nullable final Pair<Integer, Currencies> requiredBalance, @Nullable final ItemStack requiredItem, @Nullable final String command) {
        super(item, slot);

        this.requiredBalance = requiredBalance;
        this.requiredItem = requiredItem;
        this.command = command;
    }

    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        if (command == null || command.isEmpty())
            return;

        String[] commands = new String[]{command};
        if (command.contains(";"))
            commands = command.split(";");

        for (String command : commands) {
            command = command.replace("%player", clicker.getName());

            if (command.contains("player:"))
                Bukkit.dispatchCommand(clicker, command.replace("player:", "").trim());
            else if (command.contains("console:"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("console:", "").trim());
            else
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.trim());
        }
    }

    @Nullable
    public Pair<Integer, Currencies> getRequiredBalance() {
        return requiredBalance;
    }

    @Nullable
    public ItemStack getRequiredItem() {
        return requiredItem;
    }
}
