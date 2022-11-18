/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util;

import com.gmail.davideblade99.clashofminecrafters.schematic.SchematicHandler;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import org.bukkit.Location;
import org.bukkit.World;

import java.awt.*;

public final class SchematicUtil {

    private SchematicUtil() {
        throw new IllegalAccessError();
    }

    public static Location getSpawnLocation(final SchematicHandler schematic, final World world) {
        final Point center = new Point(schematic.getOrigin().getX() + schematic.getSize().getWidth() / 2, schematic.getOrigin().getZ() - schematic.getSize().getLength() / 2);
        final Point position = new Point(center.x, center.y);

        boolean first = true;
        int counter = 0;
        int steps = 1;
        byte direction = 0;

        // Stop when outside
        while (position.x > schematic.getOrigin().getX() && position.x < schematic.getOrigin().getX() + schematic.getSize().getWidth() && position.y < schematic.getOrigin().getZ() && position.y > schematic.getOrigin().getY() - schematic.getSize().getLength()) {
            while (counter < steps) {
                // Move towards the line
                final Location location = checkSpawnLocation(schematic, position, direction, world);

                if (location != null)
                    return location;

                counter++;
            }

            // Reset the counter and change direction
            counter = 0;
            direction = (byte) ((direction + 1) % 4);
            if (steps != 1 || direction == 1 || direction == 3)
                first = !first;

            // If finished, increment the line length
            if (first)
                steps++;
        }

        // Location not found
        return null;
    }

    private static Integer isYValid(final SchematicHandler schematic, final Location loc) {
        for (int y = schematic.getOrigin().getY(); y <= schematic.getOrigin().getY() + schematic.getSize().getHeight(); y++) {
            loc.setY(y);
            if (BukkitLocationUtil.isSafeLocation(loc))
                return y;
        }

        return null;
    }

    private static Location checkSpawnLocation(final SchematicHandler schematic, final Point position, final byte direction,
                                               final World world) {
        final Integer spawnLocation = isYValid(schematic, new Location(world, position.x, schematic.getOrigin().getY(), position.y, -180, 0));

        if (spawnLocation == null) {
            switch (direction) {
                case 0:
                    position.x++;
                    break;
                case 1:
                    position.y--;
                    break;
                case 2:
                    position.x--;
                    break;
                case 3:
                    position.y++;
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + direction);
            }
        } else
            return new Location(world, position.x, spawnLocation, position.y);

        return null;
    }
}
