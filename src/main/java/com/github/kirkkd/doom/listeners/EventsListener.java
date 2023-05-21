package com.github.kirkkd.doom.listeners;

import com.github.kirkkd.doom.Doom;
import com.github.kirkkd.doom.enemies.EnemyManager;
import com.github.kirkkd.doom.loots.LootCalculation;
import com.github.kirkkd.doom.weapons.Gun;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class EventsListener implements Listener {
    private static final Map<Projectile, BukkitTask> particleTasks = new HashMap<>();

    @EventHandler
    public static void onProjectileHit(ProjectileHitEvent event) {
        event.setCancelled(true);

        Projectile projectile = event.getEntity();
        Entity entity = event.getHitEntity();
        Block block = event.getHitBlock();

        if (block != null) {
            World world = block.getWorld();

            BlockData blockData = block.getBlockData();
            Location midpoint = block.getLocation().toVector().midpoint(projectile.getLocation().toVector()).toLocation(block.getWorld());
            Location hitLocation = new Location(block.getWorld(), midpoint.getX(), projectile.getLocation().getY(), midpoint.getZ());

            world.playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 0.7F, 0.9F);
            world.spawnParticle(Particle.BLOCK_CRACK, hitLocation, 5, 0.15, 0.15, 0.15, blockData);
        } else if (entity instanceof LivingEntity hitEnemy) {
            PersistentDataContainer pdc = projectile.getPersistentDataContainer();
            if (pdc.has(Gun.KEY_GUN_DAMAGE, PersistentDataType.DOUBLE)) {
                double damage = Objects.requireNonNull(pdc.get(Gun.KEY_GUN_DAMAGE, PersistentDataType.DOUBLE));

                if (Math.abs(projectile.getLocation().getY() - hitEnemy.getEyeLocation().getY()) <= 0.35) { // headshot
                    damage *= 2;
                    hitEnemy.getWorld().spawnParticle(Particle.CRIT_MAGIC, hitEnemy.getEyeLocation(), 20, 0.15, 0.25, 0.15);
                }

                double healthLeft = hitEnemy.getHealth() - damage;
                if (!EnemyManager.isGlory(hitEnemy) && healthLeft <= 0 && healthLeft > -(damage / 3)) { // glory
                    EnemyManager.setGlory(hitEnemy);
                    hitEnemy.damage(0);
                    hitEnemy.getWorld().spawnParticle(Particle.CRIMSON_SPORE, hitEnemy.getLocation(), 50, 0.2, 2, 0.2);
                    hitEnemy.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 1));
                    hitEnemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, PotionEffect.INFINITE_DURATION, 5));
                } else {
                    hitEnemy.removePotionEffect(PotionEffectType.GLOWING);
                    hitEnemy.damage(EnemyManager.isGlory(hitEnemy) ? 1000 : damage);
                }

                BlockData bloodData = Material.RED_WOOL.createBlockData();
                Location midpoint = entity.getLocation().toVector().midpoint(projectile.getLocation().toVector()).toLocation(entity.getWorld());
                Location hitLocation = new Location(entity.getWorld(), midpoint.getX(), projectile.getLocation().getY(), midpoint.getZ());
                entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, hitLocation, 10, 0.2, 0.2, 0.2, bloodData);
            }
        }

        if (!(entity instanceof Player)) {
            BukkitTask task = particleTasks.get(projectile);
            if (task != null) {
                task.cancel();
                particleTasks.remove(projectile);
            }
            projectile.remove();
        }
    }

    @EventHandler
    public static void onProjectileLaunch(ProjectileLaunchEvent event) {
        particleTasks.put(event.getEntity(), new BukkitRunnable() {
            @Override
            public void run() {
                Location location = event.getEntity().getLocation();
                World world = Objects.requireNonNull(location.getWorld());
                world.spawnParticle(Particle.ASH, location, 5);
            }
        }.runTaskTimer(Doom.getInstance(), 0L, 1L));
    }

    @EventHandler
    public static void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() instanceof Player) {
                double reducedDamage = event.getDamage() / 2;
                if (reducedDamage <= 1) event.setCancelled(true);
                else event.setDamage(reducedDamage);
            } else event.setCancelled(true);
        }
        else if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM && event.getEntity() instanceof Player) {
            // player shot himself lmao
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            LivingEntity livingEntity = (LivingEntity) event.getDamager();
            PersistentDataContainer pdc = livingEntity.getPersistentDataContainer();
            if (pdc.has(EnemyManager.keyDoom, PersistentDataType.BYTE)) {
                double damage = Objects.requireNonNull(pdc.get(EnemyManager.keyDamage, PersistentDataType.DOUBLE));
                event.setDamage(damage);
            }
        } else if (event.getDamager() instanceof Player player) {
            event.setCancelled(true);
            LivingEntity entity = (LivingEntity) event.getEntity();

            if (EnemyManager.isGlory(entity)) {
                EnemyManager.playGloryKillAnimation(entity, player);
            }
        }
    }

    @EventHandler
    public static void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public static void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return; // imagine dying in a children's game lmao

        if (EnemyManager.isGloryKilled(event.getEntity())) LootCalculation.GLORY_LOOT.dropLoot(event.getEntity().getLocation());
        else LootCalculation.NORMAL_LOOT.dropLoot(event.getEntity().getLocation());
    }
}
