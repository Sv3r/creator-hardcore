package be.sv3r.creatorhardcore.task;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.listener.state.PlayerState;
import be.sv3r.creatorhardcore.util.MessageUtil;
import be.sv3r.creatorhardcore.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class PlayerCrudeTask implements Runnable {
    private final CreatorHardcore plugin;
    private final UUID uuid;

    public PlayerCrudeTask(CreatorHardcore plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Player player = this.plugin.getServer().getPlayer(uuid);

        if (player != null) {
            PersistentDataContainer dataContainer = player.getPersistentDataContainer();

            if (dataContainer.has(PlayerUtil.stateKey, PersistentDataType.STRING)) {
                if (Objects.equals(dataContainer.get(PlayerUtil.stateKey, PersistentDataType.STRING), PlayerState.GRACED.toString())) {
                    MessageUtil.sendRemindGraceMessage(player);
                    PlayerUtil.setPlayerState(player, PlayerState.CRUDE);
                }
            }
        }
    }
}
