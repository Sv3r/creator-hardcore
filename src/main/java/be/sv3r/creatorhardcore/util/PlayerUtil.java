package be.sv3r.creatorhardcore.util;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.listener.state.PlayerState;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerUtil {
    public static final NamespacedKey joinTimeKey = new NamespacedKey(CreatorHardcore.getPlugin(), "jointime");
    public static final NamespacedKey stateKey = new NamespacedKey(CreatorHardcore.getPlugin(), "state");
    public static final long gracePeriod = CreatorHardcore.getPlugin().getConfig().getLong("grace-period");
    public static final String ignorePermission = "creatorhardcore.ignore";
    public static final String adminPermission = "creatorhardcore.admin";
    public static final String ignoreKickPermission = "creatorhardcore.ignore-kick";

    public static void setPlayerState(Player player, PlayerState playerState) {
        String state = playerState.toString();

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(PlayerUtil.stateKey, PersistentDataType.STRING, state);

        User user = LuckPermsProvider.get().getPlayerAdapter(Player.class).getUser(player);

        Set<String> groups = user.getNodes().stream()
                .filter(NodeType.INHERITANCE::matches)
                .map(NodeType.INHERITANCE::cast)
                .map(InheritanceNode::getGroupName)
                .collect(Collectors.toSet());

        for (String group : groups) {
            user.data().remove(Node.builder("group." + group).build());
        }

        user.data().add(Node.builder("group." + state).build());
        LuckPermsProvider.get().getUserManager().saveUser(user);
    }

    public static boolean isPlayerCrude(Player player) {
        return getPassedTimeSinceFirstJoin(player) >= gracePeriod;
    }

    public static long getRemainingGraceTime(Player player) {
        return gracePeriod - getPassedTimeSinceFirstJoin(player);
    }

    public static long getPassedTimeSinceFirstJoin(Player player) {
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
