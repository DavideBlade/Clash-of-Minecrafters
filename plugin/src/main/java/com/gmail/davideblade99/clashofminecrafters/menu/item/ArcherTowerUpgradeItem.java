package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematics;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.BuildingSettings;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;

/**
 * Represents the item used to upgrade archer towers
 *
 * @since v3.1.2
 */
public final class ArcherTowerUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public ArcherTowerUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingSettings nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        final World villageWorld = plugin.getVillageHandler().getVillageWorld();
        final Vector origin = new Vector(clicker.getLocation());
        final User user = plugin.getUser(clicker);

        final int currentLevel = nextBuilding.level - 1;
        final int nextLevel = currentLevel + 1;

        // Check if player has money to upgrade
        if (!user.hasMoneyToUpgrade(nextLevel, BuildingType.ARCHER_TOWER)) {
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


        // If the level is 0 it means that the player has not unlocked the building
        if (currentLevel <= 0) {
            // Check if player is into own island
            if (!user.getIsland().canBuildOnLocation(clicker.getLocation())) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.TOWER_NOT_PLACED));
                return;
            }

            //TODO: metodo a comune, è uguale per tutti
            // Paste schematic
            final Schematic schematic;
            try {
                schematic = plugin.getSchematicHandler().getSchematic(Schematics.ARCHER_TOWER);
                schematic.paste(origin.toBukkitLocation(villageWorld), new NullableCallback<PastingException>() {
                    @Override
                    public void call(@Nullable final PastingException result) {
                        MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.TOWER_PLACED));

                        // Save tower position
                        user.setArcherPos(new Vector(origin.getX(), (origin.getY() + schematic.getSize().getHeight()), origin.getZ()));

                        user.upgradeBuilding(BuildingType.ARCHER_TOWER);
                    }
                });
            } catch (final FileNotFoundException e) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ARCHER_TOWER.getName()));
                MessageUtil.sendError("It seems that the schematic within the .jar is missing. Download the plugin again.");
                //TODO: file di log
            } catch (final InvalidSchematicFormatException e) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ARCHER_TOWER.getName()));
                MessageUtil.sendError("It seems that the schematic format is invalid. They may not be up to date: create them again by checking for version matches.");
                //TODO: file di log
            } catch (final Exception e) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ARCHER_TOWER.getName()));
                MessageUtil.sendError("A generic error occurred in reading the schematic file.");
                //TODO: file di log
            }
        }
    }
}
