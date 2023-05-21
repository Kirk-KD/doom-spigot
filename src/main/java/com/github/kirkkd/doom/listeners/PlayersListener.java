package com.github.kirkkd.doom.listeners;

import com.github.kirkkd.doom.Doom;
import com.github.kirkkd.doom.GunsCollection;
import com.github.kirkkd.doom.dungeon.DungeonGeneration;
import com.github.kirkkd.doom.weapons.Gun;
import com.github.kirkkd.doom.weapons.Pistol;
import com.github.kirkkd.doom.weapons.Shotgun;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayersListener implements Listener {
    private static final Map<Player, BukkitTask> actionBarTasks = new HashMap<>();

    private static void giveDefaultPotionEffects(Player player) {
        player.addPotionEffects(Arrays.asList(
                new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2),
                new PotionEffect(PotionEffectType.FAST_DIGGING, PotionEffect.INFINITE_DURATION, 100),
                new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 50)
        ));
    }

    private static void initializePlayer(Player player) {
        player.setGameMode(GameMode.ADVENTURE);

        World newWorld = player.getWorld();
        String worldConfig = "worlds." + newWorld.getName();

        FileConfiguration config = Doom.getInstance().getConfig();
        boolean isGame = newWorld.getName().contains("dungeon") || config.getBoolean(worldConfig + ".game");

        if (config.contains(worldConfig + ".spawn")) {
            player.teleport(new Location(
                    newWorld,
                    config.getDouble(worldConfig + ".spawn.x"),
                    config.getDouble(worldConfig + ".spawn.y"),
                    config.getDouble(worldConfig + ".spawn.z")
            ));
        }

        if (isGame) {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40);
            player.setHealth(40);

            Inventory inventory = player.getInventory();

            inventory.setItem(0, new Pistol(player).createItem());
            inventory.setItem(1, new Shotgun(player).createItem());

            giveDefaultPotionEffects(player);

            startPlayerActionBarTask(player);
        } else {
            player.getInventory().clear();
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    private static void startPlayerActionBarTask(Player player) {
        actionBarTasks.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isDead()) {
                    ItemStack heldItem = player.getInventory().getItemInMainHand();
                    GunsCollection guns = GunShotListener.getGuns(player);
                    Gun gun = guns.getFromItemStack(heldItem);

                    if (gun != null) {
                        if (gun.isSwitchingWeapon()) gun.sendSwitchWeaponMessage();
                        else gun.sendActionbarMessage();
                    }
                }
            }
        }.runTaskTimer(Doom.getInstance(), 0L, 3L));
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        initializePlayer(player);
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        if (actionBarTasks.containsKey(event.getPlayer())) {
            actionBarTasks.get(event.getPlayer()).cancel();
            actionBarTasks.remove(event.getPlayer());
        }
    }

    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                giveDefaultPotionEffects(event.getPlayer());
            }
        }.runTaskLater(Doom.getInstance(), 2L);
    }

    @EventHandler
    public static void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        initializePlayer(player);

        if (event.getFrom().getName().contains("dungeon")) {
            DungeonGeneration.deleteDungeonWorld();
            player.sendMessage("Deleted dungeon world!");
        }
    }
}
