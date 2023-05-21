package com.github.kirkkd.doom;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.Objects;

public class MarkerArmorStand extends ArmorStand {
    public MarkerArmorStand(Location location) {
        super(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(), location.getX(), location.getY(), location.getZ());

        this.setYRot(location.getYaw());
        this.setXRot(location.getPitch());

        this.setMarker(true);
        this.setInvisible(true);
        this.setNoGravity(true);
    }
}
