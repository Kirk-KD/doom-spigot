package com.github.kirkkd.doom.dungeon;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class RoomStructure {
    private final Room room;
    private Location location;

    public Door doorOne, doorTwo;

    // stupid
    public enum DoorNumber {
        ONE,
        TWO
    }

    public RoomStructure(Room room, Location location) {
        this.room = room;
        this.location = location;

        if (location != null) {
            this.doorOne = new Door(this.location.clone(), this.room.getDoorOne().localPosition.clone(), this.room.getDoorOne().direction.clone());
            this.doorTwo = new Door(this.location.clone(), this.room.getDoorTwo().localPosition.clone(), this.room.getDoorTwo().direction.clone());
        }
    }

    public RoomStructure(Room room, DoorNumber doorNumber, Door otherDoor) {
        this(room, null);

        Door thisDoor = doorNumber == DoorNumber.ONE ? room.getDoorOne() : room.getDoorTwo();
        Location newDoorLoc = otherDoor.worldLocation.clone().add(otherDoor.direction.clone().multiply(new Vector(1, 1, 1)));

        this.location = newDoorLoc.clone().add(thisDoor.localPosition.clone().multiply(new Vector(-1, -1, -1)));
        this.doorOne = new Door(this.location.clone(), this.room.getDoorOne().localPosition.clone(), this.room.getDoorOne().direction.clone());
        this.doorTwo = new Door(this.location.clone(), this.room.getDoorTwo().localPosition.clone(), this.room.getDoorTwo().direction.clone());
    }

    public void place() {
        assert location != null;
        room.place(location);
    }
}
