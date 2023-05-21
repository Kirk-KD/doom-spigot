package com.github.kirkkd.doom.glory;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Animation extends BukkitRunnable {
    private final ArmorStand armorStand;

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public Animation(ArmorStand armorStand) {
        this.armorStand = armorStand;

        Objects.requireNonNull(this.armorStand.getEquipment()).setItemInOffHand(new ItemStack(Material.GOLDEN_SWORD));
        this.armorStand.setLeftArmPose(new EulerAngle(0, Math.toRadians(-20), Math.toRadians(45)));
    }

    @Override
    public void run() {
        Location loc = this.armorStand.getLocation();
        Location newLocation = loc.add(loc.getDirection().rotateAroundY(Math.toRadians(-90)).multiply(0.25));
        newLocation.setY(newLocation.getY() - 0.25);
        this.armorStand.teleport(newLocation);

        Location particleLoc = loc.add(new Vector(0, 1, 0));
        BlockData bloodData = Material.RED_WOOL.createBlockData();
        this.armorStand.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleLoc, 20, 0.05, 0.05, 0.05, bloodData);
        this.armorStand.getWorld().spawnParticle(Particle.FALLING_LAVA, particleLoc, 10, 0.2, 0.2, 0.2);
        this.armorStand.getWorld().playSound(this.armorStand.getLocation(), Sound.BLOCK_ROOTED_DIRT_HIT, SoundCategory.MASTER, 10F, 0.5F);
        this.armorStand.getWorld().playSound(this.armorStand.getLocation(), Sound.ENTITY_ZOMBIE_HURT, SoundCategory.MASTER, 1F, 0.7F);
    }
}
