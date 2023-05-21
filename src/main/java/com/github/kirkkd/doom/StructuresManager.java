package com.github.kirkkd.doom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Level;

public class StructuresManager {
    public static Structure loadStructure(String name) throws IOException {
        Path path = Doom.getInstance().getDataFolder().toPath().resolve("structures/" + name + ".nbt");
        return Bukkit.getStructureManager().loadStructure(path.toFile());
    }

    public static void placeStructure(String name, Location location) {
        try {
            Structure structure = loadStructure(name);
            structure.place(location, false, StructureRotation.NONE, Mirror.NONE, 0, 1, new Random());
        } catch (IOException e) {
            Doom.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
        }
    }
}
