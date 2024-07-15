package be.sv3r.creatorhardcore.listener;

import be.sv3r.creatorhardcore.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.ELYTRA) {
                event.setCancelled(true);
                event.getItem().setAmount(0);
                MessageUtil.sendElytraMessage(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();
        if (human instanceof Player player) {
            if (event.getCurrentItem() != null) {
                Material item = event.getCurrentItem().getType();
                if (item == Material.ELYTRA) {
                    event.setCancelled(true);
                    player.getInventory().remove(Material.ELYTRA);
                    MessageUtil.sendElytraMessage(player);
                }
            }
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getItem().getItemStack().getType() == Material.ELYTRA) {
                event.setCancelled(true);
                event.getItem().remove();
                MessageUtil.sendElytraMessage(player);
            }
        }
    }

    @EventHandler
    public void onPlayerGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack item = player.getInventory().getChestplate();

            if (item != null) {
                if (event.isGliding() && item.getType() == Material.ELYTRA) {
                    event.setCancelled(true);
                    player.getInventory().setChestplate(null);
                    MessageUtil.sendElytraMessage(player);
                }
            }
        }
    }
}