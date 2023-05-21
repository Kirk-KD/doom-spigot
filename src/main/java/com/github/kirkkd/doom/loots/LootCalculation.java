package com.github.kirkkd.doom.loots;

import com.github.kirkkd.doom.listeners.ItemPickups;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootCalculation {
    public static final LootCalculation NORMAL_LOOT = new LootCalculation(0.2, 0.1, 0.1, 10);
    public static final LootCalculation GLORY_LOOT = new LootCalculation(0.5, 0.5, 0.1, 30);

    protected double pistolAmmoPercentage;
    protected double shotgunAmmoPercentage;

    protected double randomPercentage;
    protected int amount = 1;

    public record LootTuple(ItemPickups.PickupTypes type, int amount) {}

    public LootCalculation(double pistolAmmoPercentage, double shotgunAmmoPercentage, double randomPercentage) {
        this.pistolAmmoPercentage = pistolAmmoPercentage;
        this.shotgunAmmoPercentage = shotgunAmmoPercentage;

        this.randomPercentage = randomPercentage;
    }

    public LootCalculation(double pistolAmmoPercentage, double shotgunAmmoPercentage, double randomPercentage, int amount) {
        this(pistolAmmoPercentage, shotgunAmmoPercentage, randomPercentage);

        this.amount = amount;
    }

    public void dropLoot(Location location, int totalAmount) {
        for (LootTuple lootTuple : this.getLoots(totalAmount)) {
            ItemPickups.dropItems(location, lootTuple.type, lootTuple.amount);
        }
    }

    public void dropLoot(Location location) {
        this.dropLoot(location, this.amount);
    }

    public List<LootTuple> getLoots(int totalAmount) {
        List<LootTuple> loots = new ArrayList<>();

        loots.add(new LootTuple(ItemPickups.PickupTypes.AMMO_PISTOL, calculateRandomAmount(totalAmount, this.pistolAmmoPercentage)));
        loots.add(new LootTuple(ItemPickups.PickupTypes.AMMO_SHOTGUN, calculateRandomAmount(totalAmount, this.shotgunAmmoPercentage)));

        return loots;
    }

    private int calculateRandomAmount(int totalAmount, double percentage) {
        return (int) Math.ceil(calculateRandomPercentage(percentage) * totalAmount);
    }

    private double calculateRandomPercentage(double originalPercentage) {
        Random rand = new Random();
        return originalPercentage + (rand.nextDouble(this.randomPercentage * 2) - this.randomPercentage);
    }
}
