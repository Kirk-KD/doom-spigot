package com.github.kirkkd.doom.dungeon;

import com.github.kirkkd.doom.Doom;
import com.github.kirkkd.doom.WorldsManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class DungeonGeneration implements Listener {
    public static final Room roomChurch;

    static {
        roomChurch = new Room("room_church");
    }

    private World world = null;
    private final Player player;

    public DungeonGeneration(Player player) {
        this.player = player;
    }

    private void generateDungeon() {
        RoomStructure roomStructure = new RoomStructure(roomChurch, new Location(world, 0, 0, 0));
        roomStructure.place();

        RoomStructure secondRoom = new RoomStructure(roomChurch, RoomStructure.DoorNumber.ONE, roomStructure.doorTwo);
        secondRoom.place();
    }

    public void createWorld() {
        this.world = new WorldCreator("dungeon")
                .environment(World.Environment.NETHER)
                .type(WorldType.FLAT)
                .generator(new VoidChunkGenerator())
                .createWorld();

        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);

        player.sendMessage("Creating dungeon world...");
    }

    public void startGeneration() {
        createWorld();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getWorld("dungeon") != null) {
                    player.sendMessage("Dungeon world loaded!");

                    generateDungeon();

                    WorldsManager.warp(player, world);

                    this.cancel();
                }
            }
        }.runTaskTimer(Doom.getInstance(), 0L, 1L);
    }

    private static class VoidChunkGenerator extends ChunkGenerator {
        @Override
        public List<BlockPopulator> getDefaultPopulators(World world) {
            return Collections.emptyList();
        }
    }

    public static void deleteDungeonWorld() {
        if (Bukkit.getWorld("dungeon") != null) {
            File folder = Bukkit.getWorld("dungeon").getWorldFolder();
            Bukkit.unloadWorld(Bukkit.getWorld("dungeon"), false);

//            folder.delete();
            try {
                FileUtils.deleteDirectory(folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
