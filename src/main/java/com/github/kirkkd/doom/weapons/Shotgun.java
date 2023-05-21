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

public class Shotgun extends Gun implements DoomWeapon {
    public Shotgun(Player player) {
        super(player);

        this.damage = 8;
        this.maxCollectedAmmo = 40;
        this.collectedAmmo = 8;
        this.maxLoadedAmmo = 8;
        this.loadedAmmo = 8;
        this.shotsPerShot = 5;
        this.shotSpread = 0.06;
        this.shotCooldown = 15;
        this.reloadTime = 25L;
        this.switchWeaponTime = 15L;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_HORSE_ARMOR, 1);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(ChatColor.GOLD + "Shotgun");

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(DoomWeapon.ITEM_ID, PersistentDataType.STRING, "SHOTGUN");

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public void shootSound() {
        Player player = this.getPlayer();
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.MASTER, 3, 0.8F);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 0.7F, 0.5F);
    }
}
