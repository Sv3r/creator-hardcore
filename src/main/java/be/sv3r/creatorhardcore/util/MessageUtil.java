package be.sv3r.creatorhardcore.util;

import be.sv3r.creatorhardcore.CreatorHardcore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MessageUtil {
    private static final String prefix = CreatorHardcore.getPlugin().getConfig().getString("prefix");
    private static final String joinMessage;
    private static final String joinGraceMessage;
    private static final List<String> deathTitleMessages;
    private static final String broadcastDeathMessage;
    private static final String respawnMessage;
    private static final String remindGraceMessage;

    private static final String playerJoin;
    private static final String playerLeave;

    private static final String elytraMessage;

    static {
        assert prefix != null;
        joinMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("join-message")).replace("%prefix%", prefix);
        joinGraceMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("join-grace-message")).replace("%prefix%", prefix);
        broadcastDeathMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("broadcast-death-message")).replace("%prefix%", prefix);
        deathTitleMessages = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getStringList("death-title-messages"));
        respawnMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("respawn-message")).replace("%prefix%", prefix);
        remindGraceMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("remind-grace-message")).replace("%prefix%", prefix);

        playerJoin = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("player-join"));
        playerLeave = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("player-leave"));

        elytraMessage = Objects.requireNonNull(CreatorHardcore.getPlugin().getConfig().getString("elytra-message")).replace("%prefix%", prefix);
    }

    public static void sendJoinMessage(Player player) {
        long gracePeriod = PlayerUtil.getRemainingGraceTime(player);
        String message;

        if (gracePeriod <= 0) {
            message = joinMessage;
        } else {
            message = joinGraceMessage.replace("%grace%", String.valueOf(gracePeriod));
        }

        sendMessage(player, message);
    }

    public static void sendRespawnMessage(Player player) {
        long gracePeriod = PlayerUtil.getRemainingGraceTime(player);
        String message = respawnMessage.replace("%grace%", String.valueOf(gracePeriod));

        sendMessage(player, message);
    }

    public static void sendRemindGraceMessage(Player player) {
        sendMessage(player, remindGraceMessage);
    }

    public static void sendElytraMessage(Player player) {
        sendMessage(player, elytraMessage);
    }

    public static void sendDeathTitleMessage(Player player) {
        Random random = new Random();

        int randomIndex = random.nextInt(deathTitleMessages.size());
        sendTitleMessage(player, deathTitleMessages.get(randomIndex));
    }

    public static void broadcastDeathMessage(Player player) {
        String message = broadcastDeathMessage.replace("%player%", player.getName());
        broadCastMessage(message);
    }

    public static @NotNull Component getPlayerJoinMessage(Player player) {
        String message = playerJoin.replace("%player%", player.getName());
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static @NotNull Component getPlayerLeaveMessage(Player player) {
        String message = playerLeave.replace("%player%", player.getName());
        return MiniMessage.miniMessage().deserialize(message);
    }

    private static void broadCastMessage(String message) {
        Component parsed = MiniMessage.miniMessage().deserialize(message);
        Bukkit.broadcast(parsed);
    }

    private static void sendMessage(Player player, String message) {
        Component parsed = MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(parsed);
    }

    private static void sendTitleMessage(Player player, String message) {
        Component parsed = MiniMessage.miniMessage().deserialize(message);
        player.sendTitlePart(TitlePart.TITLE, parsed);
    }
}
