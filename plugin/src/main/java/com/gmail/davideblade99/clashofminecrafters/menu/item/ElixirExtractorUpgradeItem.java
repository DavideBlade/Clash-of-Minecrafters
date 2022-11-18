package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematics;
import com.gmail.davideblade99.clashofminecrafters.setting.Settings;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.BuildingSettings;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents the item used to upgrade elixir extractors
 *
 * @since v3.1.2
 */
public final class ElixirExtractorUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public ElixirExtractorUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingSettings nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     */
    //TODO: riguardare metodo
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        final Settings config = plugin.getConfig();
        final World villageWorld = plugin.getVillageHandler().getVillageWorld();
        final Vector origin = new Vector(clicker.getLocation());
        final User user = plugin.getUser(clicker);

        //TODO: usare max() anziché considerarlo come livello 0 -> meno problemi e più ragionevole
        /*
         * If any player has a level higher than the current maximum level
         * (e.g., some levels have been removed from the config.yml),
         * they are treated as if they have not purchased any upgrades
         * and must therefore start over.
         */
        int currentLevel = nextBuilding.level - 1;
        if (currentLevel > config.getMaxLevel(BuildingType.ELIXIR_EXTRACTOR))
            currentLevel = 0;

        // Check if player have money to upgrade
        final int nextLevel = currentLevel + 1;
        if (!user.hasMoneyToUpgrade(nextLevel, BuildingType.ELIXIR_EXTRACTOR)) {
            final Currency currency = nextBuilding.currency;
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
                    throw new IllegalStateException("Unexpected currency: " + currency);
            }

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        /*
         * Before increasing the level, collect what the extractors were storing.
         * This prevents calculation of the extractor's production from the last collection
         * with the parameters of the next level.
         * Example:
         * the player collected 10 hours ago and his extractor produces 100 units per hour.
         * So it has produced 1000 units so far. The next level produces 200 units per hour.
         * If collecting is not done now, 2000 units would be the result when computing the resources produced.
         */
        user.collectExtractors();
        /*
         * Notifies resource collection only if the player already had an extractor
         * (if he is buying the first one, there is nothing to collect)
         */
        if (user.hasBuilding(BuildingType.ELIXIR_EXTRACTOR))
            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));


        // If the level is 0 it means that the player has not unlocked the building
        if (currentLevel <= 0) {
            // Check if player is into own island
            if (!user.getIsland().canBuildOnLocation(clicker.getLocation())) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED));
                return;
            }

            final Schematic schematic = plugin.getSchematicHandler().getSchematic(Schematics.ELIXIR_EXTRACTOR);

            // Paste schematic
            try {
                schematic.paste(origin.toBukkitLocation(villageWorld));
            } catch (final PastingException ignored) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ELIXIR_EXTRACTOR.getName()));
                return;
            }

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.EXTRACTOR_PLACED));
        }

        user.upgradeBuilding(BuildingType.ELIXIR_EXTRACTOR);
    }
}
