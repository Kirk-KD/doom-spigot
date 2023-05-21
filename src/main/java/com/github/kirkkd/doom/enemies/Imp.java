package com.github.kirkkd.doom.enemies;

import com.github.kirkkd.doom.Doom;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class Imp extends Zombie {
    public Imp(Location location) {
        super(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle());
        this.setPos(location.getX(), location.getY(), location.getZ());

        EnemyManager.setHealth(this, 15);
        EnemyManager.setDamage(this, 4);
        EnemyManager.setPersistentData(this);
        EnemyManager.disableHurtCooldown(this);
    }

    public void onSpawn() {
        Imp thisImp = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (thisImp.isDeadOrDying()) this.cancel();
                else {
                    if (thisImp.getTarget() != null) {
                        Random rand = new Random();
                        if (rand.nextDouble() < 0.3) {
                            CraftEntity target = thisImp.getTarget().getBukkitEntity();
                            CraftEntity imp = thisImp.getBukkitEntity();
                            if (
                                    target.getLocation().distance(thisImp.getBukkitEntity().getLocation()) > 5
                                    && Math.abs(target.getLocation().getY() - thisImp.getBukkitEntity().getLocation().getY()) < 8
                                    && !EnemyManager.isGlory((LivingEntity) imp)
                            ) {
                                imp.setVelocity(target.getLocation().add(0, 3, 0).subtract(imp.getLocation()).toVector().multiply(0.22));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Doom.getInstance(), 0L, 10L);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
