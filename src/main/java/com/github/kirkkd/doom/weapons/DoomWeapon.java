package com.github.kirkkd.doom.weapons;

import com.github.kirkkd.doom.Doom;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface DoomWeapon {
    NamespacedKey ITEM_ID = new NamespacedKey(Doom.getInstance(), "item_id");

    ItemStack createItem();
}
