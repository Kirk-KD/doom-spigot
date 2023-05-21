package com.github.kirkkd.doom.commands;

import com.github.kirkkd.doom.StructuresManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaceStructure implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            if (strings.length != 4) return false;

            try {
                int x = Integer.parseInt(strings[1]);
                int y = Integer.parseInt(strings[2]);
                int z = Integer.parseInt(strings[3]);
                Location loc = new Location(player.getWorld(), x, y, z);

                String name = strings[0];

                StructuresManager.placeStructure(name, loc);

                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }
}
