package com.github.kirkkd.doom.weapons;

import com.github.kirkkd.doom.ActionBar;
import com.github.kirkkd.doom.Doom;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public abstract class Gun {
    public static final NamespacedKey
            KEY_LOADED_AMMO = new NamespacedKey(Doom.getInstance(), "doom_loaded_ammo"),
            KEY_COLLECTED_AMMO = new NamespacedKey(Doom.getInstance(), "doom_collected_ammo"),
            KEY_GUN_DAMAGE = new NamespacedKey(Doom.getInstance(), "doom_gun_damage");

    private final Player player;

    // stats
    protected double damage;
    protected int maxCollectedAmmo;
    protected int collectedAmmo;
    protected int maxLoadedAmmo;
    protected int loadedAmmo;

    // velocity factors
    protected double shotSpread;
    protected double shotsPerShot;

    // cooldown
    protected double shotCooldown;
    protected long reloadTime;
    protected long switchWeaponTime;

    // status flags
    private boolean onCooldown = false;
    private boolean reloading = false;
    private boolean switchingWeapon = false;

    // Bukkit tasks
    private BukkitTask switchWeaponTask = null;
    private BukkitTask reloadTask = null;


    public double getDamage() {
        return this.damage;
    }

    public int getMaxCollectedAmmo() {
        return maxCollectedAmmo;
    }

    public int getMaxLoadedAmmo() {
        return maxLoadedAmmo;
    }

    public int getCollectedAmmo() {
        return collectedAmmo;
    }

    public void setCollectedAmmo(int collectedAmmo) {
        this.collectedAmmo = collectedAmmo;
    }

    public int getLoadedAmmo() {
        return loadedAmmo;
    }

    public void setLoadedAmmo(int loadedAmmo) {
        this.loadedAmmo = loadedAmmo;
    }

    public long getReloadTime() {
        return reloadTime;
    }

    public long getSwitchWeaponTime() {
        return switchWeaponTime;
    }

    public BukkitTask getReloadTask() {
        return reloadTask;
    }

    public void setReloadTask(BukkitTask reloadTask) {
        this.reloadTask = reloadTask;
    }

    public BukkitTask getSwitchWeaponTask() {
        return switchWeaponTask;
    }

    public void setSwitchWeaponTask(BukkitTask switchWeaponTask) {
        this.switchWeaponTask = switchWeaponTask;
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getShotSpread() {
        return this.shotSpread;
    }

    public double getShotsPerShot() {
        return this.shotsPerShot;
    }

    public double getShotCooldown() {
        return this.shotCooldown;
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }

    public void setOnCooldown(boolean onCooldown) {
        this.onCooldown = onCooldown;
    }

    public boolean isSwitchingWeapon() {
        return switchingWeapon;
    }

    public void setSwitchingWeapon(boolean switchingWeapon) {
        this.switchingWeapon = switchingWeapon;
    }

    public Gun(Player player) {
        this.player = player;
    }

    public void shoot(ItemStack shooterItemStack) {
        if (this.getLoadedAmmo() > 0) {
            if (this.isOnCooldown()) return;

            this.setOnCooldown(true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    setOnCooldown(false);
                }
            }.runTaskLater(Doom.getInstance(), (long) this.getShotCooldown());

            for (int i = 0; i < this.getShotsPerShot(); i++) this.shootOnce();

            this.shootSound();
            this.setLoadedAmmo(this.getLoadedAmmo() - 1);
        } else {
            this.reload(shooterItemStack);
        }

        this.updateItemStack(shooterItemStack);
    }

    private void shootOnce() {
        Location playerLocation = this.getPlayer().getLocation();
        ThrowableProjectile snowball = this.getPlayer().getWorld().spawn(playerLocation.add(0, this.getPlayer().getHeight() - 0.5, 0), Snowball.class);
        snowball.setGravity(false);
        snowball.setVelocity(this.getShotVelocity(playerLocation.getDirection()));
        snowball.setItem(new ItemStack(Material.POLISHED_BLACKSTONE_BUTTON));

        PersistentDataContainer pdc = snowball.getPersistentDataContainer();
        pdc.set(KEY_GUN_DAMAGE, PersistentDataType.DOUBLE, this.getDamage());
    }
    
    public void reload(ItemStack shooterItemStack) {
        if (this.getCollectedAmmo() > 0 && !this.isReloading()) {
            this.setReloading(true);
            this.setOnCooldown(true);

            this.setReloadTask(new BukkitRunnable() {
                @Override
                public void run() {
                    int ammoNeeded = getMaxLoadedAmmo() - getLoadedAmmo();
                    if (getCollectedAmmo() >= ammoNeeded) {
                        setLoadedAmmo(getLoadedAmmo() + ammoNeeded);
                        setCollectedAmmo(getCollectedAmmo() - ammoNeeded);
                    } else {
                        setLoadedAmmo(getLoadedAmmo() + getCollectedAmmo());
                        setCollectedAmmo(0);
                    }

                    setReloading(false);
                    setOnCooldown(false);
                    updateItemStack(shooterItemStack);
                }
            }.runTaskLater(Doom.getInstance(), this.getReloadTime()));
        }
    }

    public void cancelReload() {
        if (this.getReloadTask() != null) {
            this.getReloadTask().cancel();
            this.setReloadTask(null);
            this.setReloading(false);
            this.setOnCooldown(false);
        }
    }

    public void onSwitchWeapon(Gun from) {
        if (from != null && !from.isSwitchingWeapon()) {
            from.setSwitchingWeapon(false);
            if (from.getSwitchWeaponTask() != null) from.getSwitchWeaponTask().cancel();
        }

        this.setSwitchingWeapon(true);
        this.setOnCooldown(true);
        this.setSwitchWeaponTask(new BukkitRunnable() {
            @Override
            public void run() {
                setOnCooldown(false);
                setSwitchingWeapon(false);
            }
        }.runTaskLater(Doom.getInstance(), this.getSwitchWeaponTime()));
    }

    public void sendReloadingMessage() {
        ActionBar.send(this.getPlayer(), "Reloading...");
    }

    public void sendSwitchWeaponMessage() {
        ActionBar.send(this.getPlayer(), "Switching weapon...");
    }

    public void sendAmmoMessage() {
        ActionBar.send(this.getPlayer(), String.format(
                "%s%s %s/ %s  |  %s%s %s/ %s",
                this.getLoadedAmmo() <= 5 ? ChatColor.RED : ChatColor.WHITE,
                this.getLoadedAmmo(),

                ChatColor.DARK_GRAY,
                this.getMaxLoadedAmmo(),

                this.getCollectedAmmo() <= 5 ? ChatColor.RED : ChatColor.WHITE,
                this.getCollectedAmmo(),

                ChatColor.DARK_GRAY,
                this.getMaxCollectedAmmo()
        ));
    }

    public void sendNoAmmoMessage() {
        ActionBar.send(this.getPlayer(), ChatColor.RED + (this.getCollectedAmmo() == 0 ? "NO AMMO" : "[RIGHT CLICK to reload]"));
    }

    public void sendActionbarMessage() {
        if (this.isReloading()) this.sendReloadingMessage();
        else if (this.getLoadedAmmo() == 0) this.sendNoAmmoMessage();
        else if (this.isSwitchingWeapon()) this.sendSwitchWeaponMessage();
        else this.sendAmmoMessage();
    }

    public void updateItemStack(ItemStack itemStack) {
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_COLLECTED_AMMO, PersistentDataType.INTEGER, this.getCollectedAmmo());
        pdc.set(KEY_LOADED_AMMO, PersistentDataType.INTEGER, this.getLoadedAmmo());
        itemStack.setItemMeta(meta);

//        this.sendActionbarMessage();
    }

    public abstract void shootSound();

    protected Vector getShotVelocity(Vector playerDir) {
        return new Vector(playerDir.getX() + this.calculateSpread(), playerDir.getY() + this.calculateSpread(), playerDir.getZ() + this.calculateSpread()).multiply(3F);
    }

    private double calculateSpread() {
        Random rand = new Random();
        return rand.nextDouble(this.getShotSpread() * 2) - this.getShotSpread();
    }

    public int pickupAmmo(int amount) {
        int needed = this.getMaxCollectedAmmo() - this.getCollectedAmmo();
        int given = Math.min(amount, needed);

        this.setCollectedAmmo(this.getCollectedAmmo() + given);

        return amount - given;
    }
}
