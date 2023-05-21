package com.github.kirkkd.doom.listeners;

import com.github.kirkkd.doom.Doom;
import com.github.kirkkd.doom.GunsCollection;
import com.github.kirkkd.doom.weapons.Gun;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.logging.Level;

public class ItemPickups implements Listener {
    public static NamespacedKey keyItemName = new NamespacedKey(Doom.getInstance(), "doom_item_name");
    public static NamespacedKey keyItemDisplayName = new NamespacedKey(Doom.getInstance(), "doom_item_display_name");
    public static NamespacedKey keyItemAmount = new NamespacedKey(Doom.getInstance(), "doom_item_amount");

    private static void playSoundAmmo(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.MASTER, 2F, 1F);
    }

    public enum PickupTypes {
        AMMO_SHOTGUN(Material.OCHRE_FROGLIGHT, ChatColor.GOLD + "Shotgun Ammo", 8),
        AMMO_PISTOL(Material.VERDANT_FROGLIGHT, ChatColor.GREEN + "Pistol Ammo", 10);

        public final Material material;
        public final String displayName;
        public final String dataName;
        public final int maxAmountPer;

        PickupTypes(Material material, String displayName, int maxAmountPer) {
            this.material = material;
            this.displayName = displayName;
            this.dataName = ChatColor.stripColor(displayName);
            this.maxAmountPer = maxAmountPer;
        }
    }

    public static void dropItems(Location location, PickupTypes type, int totalAmount) {
        int maxAmountPer = type.maxAmountPer;
        for (int i = 0; i < Math.floor((double) totalAmount / maxAmountPer); i++)
            dropItems(location, type.material, maxAmountPer, type.displayName, type.dataName);

        if (totalAmount % maxAmountPer != 0)
            dropItems(location, type.material, totalAmount % maxAmountPer, type.displayName, type.dataName);
    }

    public static void dropItems(Location location, Material material, int amount, String name, String dataName) {
        Item item = Objects.requireNonNull(location.getWorld()).dropItem(location, new ItemStack(material));
        PersistentDataContainer pdc = item.getPersistentDataContainer();

        pdc.set(keyItemName, PersistentDataType.STRING, dataName);
        pdc.set(keyItemDisplayName, PersistentDataType.STRING, name);
        pdc.set(keyItemAmount, PersistentDataType.INTEGER, amount);

        item.setGlowing(true);
        item.setUnlimitedLifetime(true);
        item.setPickupDelay(0);
        item.setCustomName(amount + "x " + name);
        item.setCustomNameVisible(true);
    }

    @EventHandler
    public static void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            Item item = event.getItem();
            PersistentDataContainer pdc = item.getPersistentDataContainer();

            String name = pdc.get(keyItemName, PersistentDataType.STRING);
            String displayName = pdc.get(keyItemDisplayName, PersistentDataType.STRING);
            Integer amount = pdc.get(keyItemAmount, PersistentDataType.INTEGER);

            if (name != null && amount != null) {
                GunsCollection guns = GunShotListener.getGuns(player);
                Gun gun = null;

                if (name.equals(PickupTypes.AMMO_PISTOL.dataName)) {
                    gun = guns.getPistol();
                } else if (name.equals(PickupTypes.AMMO_SHOTGUN.dataName)) {
                    gun = guns.getShotgun();
                } else {
                    Doom.getInstance().getLogger().log(Level.SEVERE, "Invalid pickup type: " + name);
                }

                if (gun != null) {
                    int leftover = gun.pickupAmmo(amount);

                    if (leftover == 0) {
                        playSoundAmmo(player);
                        item.remove();
                    } else {
                        pdc.set(keyItemAmount, PersistentDataType.INTEGER, leftover);
                        item.setCustomName(leftover + "x " + displayName);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
