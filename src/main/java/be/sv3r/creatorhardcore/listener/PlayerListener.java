package be.sv3r.creatorhardcore.listener;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.task.PlayerGraceReminderTask;
import be.sv3r.creatorhardcore.util.MessageUtil;
import be.sv3r.creatorhardcore.util.PlayerUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (!dataContainer.has(PlayerUtil.joinTimeKey, PersistentDataType.STRING)) {
            dataContainer.set(PlayerUtil.joinTimeKey, PersistentDataType.STRING, Instant.now().toString());

            CreatorHardcore plugin = CreatorHardcore.getPlugin();
            CreatorHardcore.getScheduler().runTaskLaterAsynchronously(plugin, new PlayerGraceReminderTask(plugin, player.getUniqueId()), PlayerUtil.gracePeriod * 1200);
        }

        MessageUtil.sendJoinMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (PlayerUtil.isPlayerDead(player)) {
            spawnDeathFirework(player);
            MessageUtil.broadcastDeathMessage(player);
            player.setRespawnLocation(player.getLocation(), true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!PlayerUtil.isPlayerDead(player)) {
            MessageUtil.sendRespawnMessage(event.getPlayer());
        } else {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    private void spawnDeathFirework(Player player) {
        Location location = player.getLocation();
        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().trail(true).withColor(Color.RED).build());
        firework.setFireworkMeta(fireworkMeta);
        firework.setMetadata("nodamage", new FixedMetadataValue(CreatorHardcore.getPlugin(), true));
        firework.setMetadata("shape", new FixedMetadataValue(CreatorHardcore.getPlugin(), "large_ball"));
        firework.detonate();
    }
}
