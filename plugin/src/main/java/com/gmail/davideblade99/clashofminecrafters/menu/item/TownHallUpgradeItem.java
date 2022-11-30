package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.BuildingSettings;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents the item used to upgrade town halls
 *
 * @since v3.1.2
 */
public final class TownHallUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public TownHallUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingSettings nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        final User user = plugin.getUser(clicker);

        final int currentLevel = nextBuilding.level - 1;
        final int nextLevel = currentLevel + 1;

        // Check if player has money to upgrade
        if (!user.hasMoneyToUpgrade(nextLevel, BuildingType.GOLD_EXTRACTOR)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextBuilding.currency);

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        user.upgradeBuilding(BuildingType.TOWN_HALL);
    }
}
