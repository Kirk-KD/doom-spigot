package com.github.kirkkd.doom.commands;

import com.github.kirkkd.doom.enemies.EnemyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoomSummon implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player player) {
            EnemyManager.spawnImp(player.getLocation());
        }

        return false;
    }
}
