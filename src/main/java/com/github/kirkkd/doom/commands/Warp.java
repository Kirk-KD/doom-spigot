package com.github.kirkkd.doom.commands;

import com.github.kirkkd.doom.dungeon.DungeonGeneration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Warp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
//            WorldsManager.warp(player, WorldsManager.spaceship);
            new DungeonGeneration(player).startGeneration();
        }

        return false;
    }
}
