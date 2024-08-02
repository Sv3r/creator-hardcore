package be.sv3r.creatorhardcore.listener;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.listener.state.PlayerState;
import be.sv3r.creatorhardcore.task.PlayerCrudeTask;
import be.sv3r.creatorhardcore.util.MessageUtil;
import be.sv3r.creatorhardcore.util.PlayerUtil;
import be.sv3r.creatorhardcore.util.TimeUtil;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerListener implements Listener {
    public final HashMap<UUID, Location> deathLocations = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        if (event.getPlayer().hasPermission(PlayerUtil.adminPermission) || event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            event.message(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (TimeUtil.kickPlayerIfClosed(player)) {
            return;
        }

        if (player.hasPermission(PlayerUtil.ignorePermission)) return;

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (!dataContainer.has(PlayerUtil.joinTimeKey, PersistentDataType.STRING)) {
            dataContainer.set(PlayerUtil.joinTimeKey, PersistentDataType.STRING, Instant.now().toString());
            CreatorHardcore plugin = CreatorHardcore.getPlugin();
            CreatorHardcore.getScheduler().runTaskLaterAsynchronously(plugin, new PlayerCrudeTask(plugin, player.getUniqueId()), PlayerUtil.gracePeriod * 1200);
        }

        if (!dataContainer.has(PlayerUtil.stateKey, PersistentDataType.STRING)) {
            PlayerUtil.setPlayerState(player, PlayerState.GRACED);
        }

        if (PlayerUtil.isPlayerCrude(player)) {
            if (!(Objects.equals(dataContainer.get(PlayerUtil.stateKey, PersistentDataType.STRING), PlayerState.FELLED.toString()))) {
                PlayerUtil.setPlayerState(player, PlayerState.CRUDE);
            }
        }

        MessageUtil.sendJoinMessage(player);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        String deathMessage = event.getDeathMessage();

        event.deathMessage(null);

        if (player.hasPermission(PlayerUtil.ignorePermission)) return;

        UUID playerUuid = player.getUniqueId();

        if (!PlayerUtil.isPlayerFelled(player) && PlayerUtil.isPlayerCrude(player)) {
            deathLocations.put(playerUuid, player.getLocation());

            player.setGameMode(GameMode.SPECTATOR);
            PlayerUtil.setPlayerState(player, PlayerState.FELLED);

            MessageUtil.sendDeathTitleMessage(player);
            MessageUtil.broadcastDeathMessage(player);

            event.setKeepInventory(false);
            event.setKeepLevel(false);
            event.setShouldDropExperience(true);

            CreatorHardcore.getScheduler().runTaskLater(CreatorHardcore.getPlugin(), () -> player.spigot().respawn(), 1L);

            String discordMessage = String.format("@here\n**%s** is uitgeschakeld!\n**Cause of death**: %s", player.getName(), deathMessage);
            DiscordUtil.queueMessage(DiscordUtil.getTextChannelById(MessageUtil.getDiscordDeathChannel()), discordMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(PlayerUtil.ignorePermission)) return;

        UUID playerUuid = player.getUniqueId();

        if (!PlayerUtil.isPlayerCrude(player) || PlayerUtil.isPlayerFelled(player)) {
            MessageUtil.sendRespawnMessage(event.getPlayer());
        } else {
            if (deathLocations.containsKey(playerUuid)) {
                CreatorHardcore.getScheduler().runTaskLater(CreatorHardcore.getPlugin(), () -> {
                    player.teleport(deathLocations.get(playerUuid));
                    deathLocations.remove(playerUuid);
                }, 1L);
            }

            spawnDeathFirework(player.getLocation().add(0, 1, 0));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework firework) {
            if (Objects.equals(firework.getFireworkMeta().displayName(), Component.text("death"))) {
                event.setCancelled(true);
            }
        }

        if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player damager) {
                if (damager.hasPermission(PlayerUtil.ignorePermission)) return;

                if (event.getEntity() instanceof Player victim) {
                    if (isPvpDisabled(damager, victim)) {
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (event.getDamager() instanceof Player damager) {
            if (damager.hasPermission(PlayerUtil.ignorePermission)) return;

            if (event.getEntity() instanceof Player victim) {
                if (isPvpDisabled(damager, victim)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void spawnDeathFirework(Location location) {
        ItemStack fireworkItem = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta meta = (FireworkMeta) fireworkItem.getItemMeta();
        meta.addEffect(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL).withFlicker().withTrail().build());
        meta.displayName(Component.text("death"));
        meta.setPower(3);
        fireworkItem.setItemMeta(meta);

        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        firework.setItem(fireworkItem);

        CreatorHardcore.getScheduler().runTaskLater(CreatorHardcore.getPlugin(), firework::detonate, 1L);
    }

    private boolean isPvpDisabled(Player damager, Player victim) {
        PersistentDataContainer damagerDataContainer = damager.getPersistentDataContainer();
        PersistentDataContainer victimDataContainer = victim.getPersistentDataContainer();

        PlayerState damagerState = PlayerState.valueOf(damagerDataContainer.get(PlayerUtil.stateKey, PersistentDataType.STRING));
        PlayerState victimState = PlayerState.valueOf(victimDataContainer.get(PlayerUtil.stateKey, PersistentDataType.STRING));

        return !damagerState.equals(PlayerState.CRUDE) || !victimState.equals(PlayerState.CRUDE);
    }
}
