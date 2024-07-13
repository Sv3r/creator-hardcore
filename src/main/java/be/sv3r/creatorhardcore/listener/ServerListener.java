package be.sv3r.creatorhardcore.listener;

import be.sv3r.creatorhardcore.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(MessageUtil.getPlayerJoinMessage(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(MessageUtil.getPlayerLeaveMessage(event.getPlayer()));
    }
}
