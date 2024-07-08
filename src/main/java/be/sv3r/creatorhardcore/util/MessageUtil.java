package be.sv3r.creatorhardcore.util;

import be.sv3r.creatorhardcore.CreatorHardcore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MessageUtil {
    private static final String prefix = CreatorHardcore.getPlugin().getConfig().getString("prefix");
    private static final String joinMessage;
    private static final String joinGraceMessage;
    private static final String broadcastDeathMessage;
    private static final String respawnMessage;
    private static final String remindGraceMessage;

    static {
        assert prefix != null;
        joinMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("join-message")).replace("%prefix%", prefix);
        joinGraceMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("join-grace-message")).replace("%prefix%", prefix);
        broadcastDeathMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("broadcast-death-message")).replace("%prefix%", prefix);
        respawnMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("respawn-message")).replace("%prefix%", prefix);
        remindGraceMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("remind-grace-message")).replace("%prefix%", prefix);
    }

    public static void sendJoinMessage(Player player) {
        long gracePeriod = PlayerUtil.gracePeriod - PlayerUtil.getGracePeriod(player);
        String message;

        if (gracePeriod <= 0) {
            message = joinMessage;
        } else {
            message = joinGraceMessage.replace("%grace%", String.valueOf(gracePeriod));
        }

        sendMessage(player, message);
    }

    public static void sendRespawnMessage(Player player) {
        long gracePeriod = PlayerUtil.gracePeriod - PlayerUtil.getGracePeriod(player);
        String message = respawnMessage.replace("%grace%", String.valueOf(gracePeriod));

        sendMessage(player, message);
    }

    public static void sendRemindGraceMessage(Player player) {
        sendMessage(player, remindGraceMessage);
    }

    public static void broadcastDeathMessage(Player player) {
        String message = broadcastDeathMessage.replace("%player%", player.getName());
        broadCastMessage(message);
    }

    private static void broadCastMessage(String message) {
        Component parsed = MiniMessage.miniMessage().deserialize(message);
        Bukkit.broadcast(parsed);
    }

    private static void sendMessage(Player player, String message) {
        Component parsed = MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(parsed);
    }
}
