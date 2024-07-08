package be.sv3r.creatorhardcore.task;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerGraceReminderTask implements Runnable {
    private final CreatorHardcore plugin;
    private final UUID uuid;

    public PlayerGraceReminderTask(CreatorHardcore plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        Player player = this.plugin.getServer().getPlayer(uuid);

        if (player != null) {
            MessageUtil.sendRemindGraceMessage(player);
        }
    }
}
