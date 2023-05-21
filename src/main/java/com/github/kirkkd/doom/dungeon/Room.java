package com.github.kirkkd.doom.dungeon;

import com.github.kirkkd.doom.Doom;
import com.github.kirkkd.doom.StructuresManager;
import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

public class Room {
    public String getName() {
        return name;
    }

    public Structure getStructure() {
        return structure;
    }

    public BlockVector getSize() {
        return size;
    }

    private final String name;
    private Structure structure = null;
    private BlockVector size = null;

    public Door getDoorOne() {
        return doorOne;
    }

    public Door getDoorTwo() {
        return doorTwo;
    }

    private Door doorOne, doorTwo;

    public Room(String name) {
        this.name = name;

        try {
            this.structure = StructuresManager.loadStructure(name);
            this.size = this.structure.getSize();

            FileConfiguration config = Doom.getInstance().getConfig();

            this.doorOne = new Door(
                    new Vector(
                            config.getInt("rooms." + this.getName() + ".door_1.self.x"),
                            config.getInt("rooms." + this.getName() + ".door_1.self.y"),
                            config.getInt("rooms." + this.getName() + ".door_1.self.z")
                    ),
                    new Vector(
                            config.getInt("rooms." + this.getName() + ".door_1.dir.x"),
                            config.getInt("rooms." + this.getName() + ".door_1.dir.y"),
                            config.getInt("rooms." + this.getName() + ".door_1.dir.z")
                    )
            );

            this.doorTwo = new Door(
                    new Vector(
                            config.getInt("rooms." + this.getName() + ".door_2.self.x"),
                            config.getInt("rooms." + this.getName() + ".door_2.self.y"),
                            config.getInt("rooms." + this.getName() + ".door_2.self.z")
                    ),
                    new Vector(
                            config.getInt("rooms." + this.getName() + ".door_2.dir.x"),
                            config.getInt("rooms." + this.getName() + ".door_2.dir.y"),
                            config.getInt("rooms." + this.getName() + ".door_2.dir.z")
                    )
            );
        } catch (IOException e) {
            Doom.getInstance().getLogger().log(Level.SEVERE, "Failed to load " + this.getName() + ": " + e.getMessage());
        }
    }

    public void place(Location location) {
        this.getStructure().place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1, new Random());
    }
}
