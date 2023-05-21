package com.github.kirkkd.doom.enemies;

import com.github.kirkkd.doom.Doom;
import com.github.kirkkd.doom.MarkerArmorStand;
import com.github.kirkkd.doom.glory.Animation;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class EnemyManager {
    public static final NamespacedKey
            keyDoom = new NamespacedKey(Doom.getInstance(), "doom"),
            keyDamage = new NamespacedKey(Doom.getInstance(), "doom_damage"),
            keyGlory = new NamespacedKey(Doom.getInstance(), "doom_glory"),
            keyGloryKilled = new NamespacedKey(Doom.getInstance(), "doom_glory_killed");

    public static void spawnImp(Location loc) {
        Imp imp = new Imp(loc);
        ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle().addFreshEntity(imp, CreatureSpawnEvent.SpawnReason.CUSTOM);
        imp.onSpawn();
    }

    public static void setPersistentData(Entity entity) {
        LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
        PersistentDataContainer pdc = livingEntity.getPersistentDataContainer();
        pdc.set(keyDoom, PersistentDataType.BYTE, (byte) 1);
    }

    public static void setHealth(Entity entity, double health) {
        Objects.requireNonNull(((LivingEntity) entity.getBukkitEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
        ((LivingEntity) entity.getBukkitEntity()).setHealth(health);
    }

    public static void setDamage(Entity entity, double damage) {
        Objects.requireNonNull(((LivingEntity) entity.getBukkitEntity()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(damage);
        LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
        PersistentDataContainer pdc = livingEntity.getPersistentDataContainer();
        pdc.set(keyDamage, PersistentDataType.DOUBLE, damage);
    }

    public static void disableHurtCooldown(Entity entity) {
        ((LivingEntity) entity.getBukkitEntity()).setMaximumNoDamageTicks(0);
    }

    public static void setGlory(LivingEntity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(keyGlory, PersistentDataType.BYTE, (byte) 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isDead()) {
                    entity.removePotionEffect(PotionEffectType.GLOWING);
                    entity.removePotionEffect(PotionEffectType.SLOW);
                    pdc.remove(keyGlory);
                }
            }
        }.runTaskLater(Doom.getInstance(), 60L);
    }

    public static boolean isGlory(LivingEntity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.has(keyGlory, PersistentDataType.BYTE);
    }

    public static void setGloryKilled(LivingEntity entity) {
        entity.getPersistentDataContainer().set(keyGloryKilled, PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isGloryKilled(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(keyGloryKilled, PersistentDataType.BYTE);
    }

    public static void playGloryKillAnimation(LivingEntity entity, Player player) {
        Block block = player.getEyeLocation().getBlock();
        Material mat = block.getType();
        block.setType(Material.LIGHT);

        entity.removePotionEffect(PotionEffectType.GLOWING);
        entity.setAI(false);

        double distance = entity.getLocation().distance(player.getLocation());
        Location tpLocation = player.getLocation().add(player.getLocation().getDirection().multiply(distance - 2));
        Location teleportLocation = new Location(player.getWorld(), tpLocation.getX(), entity.getLocation().getY(), tpLocation.getZ());
        teleportLocation.setDirection(entity.getLocation().toVector().subtract(player.getLocation().toVector()));

        BukkitTask lockPlayerTask = new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(teleportLocation);
            }
        }.runTaskTimer(Doom.getInstance(), 0L, 1L);

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));

        Location armorStandLoc = new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());
        armorStandLoc.setDirection(teleportLocation.getDirection());
        armorStandLoc = armorStandLoc.add(armorStandLoc.getDirection().multiply(-0.45));
        armorStandLoc = armorStandLoc.add(armorStandLoc.getDirection().rotateAroundY(Math.toRadians(90)).multiply(0.55));
        armorStandLoc.setY(armorStandLoc.getY() + 1);

        MarkerArmorStand armorStand = new MarkerArmorStand(armorStandLoc);
        ((CraftWorld) player.getWorld()).getHandle().addFreshEntity(armorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        Animation animation = new Animation((ArmorStand) armorStand.getBukkitEntity());
        BukkitTask task = animation.runTaskTimerAsynchronously(Doom.getInstance(), 1L, 5L);

        new BukkitRunnable() {
            @Override
            public void run() {
                lockPlayerTask.cancel();
                task.cancel();

                animation.getArmorStand().remove();

                block.setType(mat);
                entity.getWorld().strikeLightningEffect(entity.getLocation());

                setGloryKilled(entity);
                entity.damage(1000);
            }
        }.runTaskLater(Doom.getInstance(), 17L);
    }
}
