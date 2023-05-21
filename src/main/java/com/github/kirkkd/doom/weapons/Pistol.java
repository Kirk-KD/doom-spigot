package com.github.kirkkd.doom.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class Pistol extends Gun implements DoomWeapon {
    public Pistol(Player player) {
        super(player);

        this.damage = 5;
        this.maxCollectedAmmo = 60;
        this.collectedAmmo = 20;
        this.maxLoadedAmmo = 20;
        this.loadedAmmo = 20;
        this.shotsPerShot = 1;
        this.shotSpread = 0.01;
        this.shotCooldown = 7;
        this.reloadTime = 20L;
        this.switchWeaponTime = 8L;
    }

    @Override
    public void shootSound() {
        this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.MASTER, 3, 1);
        this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.MASTER, 3, 1);
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.IRON_HORSE_ARMOR, 1);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(ChatColor.GREEN + "Pistol");

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DoomWeapon.ITEM_ID, PersistentDataType.STRING, "PISTOL");

        item.setItemMeta(meta);

        return item;
    }
}
