package com.github.kirkkd.doom;

import com.github.kirkkd.doom.weapons.DoomWeapon;
import com.github.kirkkd.doom.weapons.Gun;
import com.github.kirkkd.doom.weapons.Pistol;
import com.github.kirkkd.doom.weapons.Shotgun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class GunsCollection {
    public Pistol getPistol() {
        return pistol;
    }

    public Shotgun getShotgun() {
        return shotgun;
    }

    private final Pistol pistol;
    private final Shotgun shotgun;

    public GunsCollection(Player player) {
        this.pistol = new Pistol(player);
        this.shotgun = new Shotgun(player);
    }

    public void cancelAllReloading() {
        this.pistol.cancelReload();
        this.shotgun.cancelReload();
    }

    public Gun getFromItemStack(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (pdc.has(DoomWeapon.ITEM_ID, PersistentDataType.STRING)) {
            String itemID = Objects.requireNonNull(pdc.get(DoomWeapon.ITEM_ID, PersistentDataType.STRING));

            return switch (itemID) {
                case "PISTOL" -> this.getPistol();
                case "SHOTGUN" -> this.getShotgun();
                default -> null;
            };
        }

        return null;
    }
}
