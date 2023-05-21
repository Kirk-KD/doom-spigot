package com.github.kirkkd.doom.listeners;

import com.github.kirkkd.doom.GunsCollection;
import com.github.kirkkd.doom.weapons.Gun;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GunShotListener implements Listener {
    private static final Map<Player, GunsCollection> playerGuns = new HashMap<>();

    private static void initializeGuns(Player player) {
        if (playerGuns.get(player) == null) {
            playerGuns.put(player, new GunsCollection(player));
        }
    }

    public static GunsCollection getGuns(Player player) {
        initializeGuns(player);
        return playerGuns.get(player);
    }

    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack heldItem = event.getItem();

        if (heldItem == null) return;

        Gun gun = getGuns(player).getFromItemStack(heldItem);

        if (gun == null) return;

        if (gun.isSwitchingWeapon()) return;

        gun.shoot(heldItem);
        event.setCancelled(true);
    }

    @EventHandler
    public static void onPlayerItemHeld(PlayerItemHeldEvent event) {
        getGuns(event.getPlayer()).cancelAllReloading();

        ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (itemStack != null) {
            Gun gun = getGuns(event.getPlayer()).getFromItemStack(itemStack);
            if (gun != null) {
                ItemStack fromItemStack = event.getPlayer().getInventory().getItem(event.getPreviousSlot());

                if (fromItemStack != null) gun.onSwitchWeapon(getGuns(event.getPlayer()).getFromItemStack(fromItemStack));
                else gun.onSwitchWeapon(null);
            }
        }
    }
}
