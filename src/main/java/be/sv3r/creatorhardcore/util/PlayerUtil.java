package be.sv3r.creatorhardcore.util;

import be.sv3r.creatorhardcore.CreatorHardcore;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.Instant;

public class PlayerUtil {
    public static final NamespacedKey joinTimeKey = new NamespacedKey(CreatorHardcore.getPlugin(), "jointime");
    public static final long gracePeriod = CreatorHardcore.getPlugin().getConfig().getLong("grace-period");
    public static final String ignorePermission = "creatorhardcore.ignore";

    public static boolean isPlayerVulnerable(Player player) {
        return getGracePeriod(player) >= gracePeriod;
    }

    public static long getGracePeriod(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (dataContainer.has(joinTimeKey, PersistentDataType.STRING)) {
            String joinTimeString = dataContainer.get(joinTimeKey, PersistentDataType.STRING);

            if (joinTimeString != null) {
                Instant joinTime = Instant.parse(joinTimeString);
                Instant now = Instant.now();
                Duration duration = Duration.between(joinTime, now);

                return duration.toMinutes();
            }
        }

        return 0;
    }
}
