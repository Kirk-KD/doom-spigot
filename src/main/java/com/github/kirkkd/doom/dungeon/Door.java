package com.github.kirkkd.doom.dungeon;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Door {
    public Vector localPosition;
    public Location worldLocation;
    public Vector direction;

    public Door(Vector position, Vector direction) {
        this.localPosition = position;
        this.direction = direction;
    }

    public Door(Location location, Vector position, Vector direction) {
        this(position, direction);
        worldLocation = location.add(position);
    }
}
