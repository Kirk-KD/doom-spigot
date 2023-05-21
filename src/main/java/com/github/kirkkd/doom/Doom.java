package com.github.kirkkd.doom;

import com.github.kirkkd.doom.commands.DoomSummon;
import com.github.kirkkd.doom.commands.PlaceStructure;
import com.github.kirkkd.doom.commands.Warp;
import com.github.kirkkd.doom.listeners.EventsListener;
import com.github.kirkkd.doom.listeners.GunShotListener;
import com.github.kirkkd.doom.listeners.ItemPickups;
import com.github.kirkkd.doom.listeners.PlayersListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Doom extends JavaPlugin {
    private static Doom instance;

    public Doom() {
        instance = this;
    }

    public static Doom getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(new EventsListener(), this);
        this.getServer().getPluginManager().registerEvents(new GunShotListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayersListener(), this);
        this.getServer().getPluginManager().registerEvents(new ItemPickups(), this);

        Objects.requireNonNull(this.getCommand("dsummon")).setExecutor(new DoomSummon());
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(this.getCommand("placestructure")).setExecutor(new PlaceStructure());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
