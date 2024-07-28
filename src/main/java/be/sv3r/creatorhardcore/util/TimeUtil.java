package be.sv3r.creatorhardcore.util;

import be.sv3r.creatorhardcore.CreatorHardcore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.*;

public class TimeUtil {
    public static final LocalTime startTime;
    public static final LocalTime stopTime;

    static {
        int startHour = CreatorHardcore.getPlugin().getConfig().getInt("start-time");
        int stopHour = CreatorHardcore.getPlugin().getConfig().getInt("stop-time");

        startTime = LocalTime.of(startHour, 0);
        stopTime = LocalTime.of(stopHour, 0);
    }

    public static boolean isServerClosed() {
        LocalDate date = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();

        Instant startInstant = ZonedDateTime.of(date, startTime, zone).toInstant();
        Instant stopInstant = ZonedDateTime.of(date, stopTime, zone).toInstant();

        if (startTime.isBefore(stopTime)) {
            return !Instant.now().isAfter(startInstant) || !Instant.now().isBefore(stopInstant);
        } else {
            return Instant.now().isAfter(stopInstant) && Instant.now().isBefore(startInstant);
        }
    }

    public static void checkPlayers() {
        if (isServerClosed()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(PlayerUtil.ignoreKickPermission)) continue;
                CreatorHardcore.getScheduler().runTask(CreatorHardcore.getPlugin(), () -> player.kick(MessageUtil.getKickMessage()));
            }
        }
    }

    public static boolean kickPlayerIfClosed(Player player) {
        if (isServerClosed()) {
            if (player.hasPermission(PlayerUtil.ignoreKickPermission)) return false;
            CreatorHardcore.getScheduler().runTaskLater(CreatorHardcore.getPlugin(), () -> player.kick(MessageUtil.getKickMessage()), 60L);
            return true;
        }
        return false;
    }
}
