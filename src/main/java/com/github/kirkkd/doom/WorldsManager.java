package com.github.kirkkd.doom;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class WorldsManager {
    public static World spaceship = new WorldCreator("world_spaceship").createWorld();

    public static void warp(Player player, World world) {
        String name = world.getName();
        FileConfiguration config = Doom.getInstance().getConfig();

        Location loc;
        if (config.contains("worlds." + name + ".spawn")) {
            double x = config.getDouble("worlds." + name + ".spawn.x");
            double y = config.getDouble("worlds." + name + ".spawn.y");
            double z = config.getDouble("worlds." + name + ".spawn.z");
            loc = new Location(world, x, y, z);
        } else {
            loc = world.getSpawnLocation();
        }

        player.teleport(loc);
    }
}
