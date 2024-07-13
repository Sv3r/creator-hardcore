package be.sv3r.creatorhardcore;

import be.sv3r.creatorhardcore.listener.PlayerListener;
import be.sv3r.creatorhardcore.listener.ServerListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class CreatorHardcore extends JavaPlugin implements Listener {
    private static CreatorHardcore plugin;
    private static BukkitScheduler scheduler;

    public static CreatorHardcore getPlugin() {
        return plugin;
    }

    public static BukkitScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public void onEnable() {
        CreatorHardcore.plugin = this;
        CreatorHardcore.scheduler = this.getServer().getScheduler();

        setupConfig();
        registerListeners();
    }

    private void setupConfig() {
        saveDefaultConfig();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
