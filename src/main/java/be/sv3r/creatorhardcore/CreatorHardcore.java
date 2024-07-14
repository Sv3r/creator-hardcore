package be.sv3r.creatorhardcore;

import be.sv3r.creatorhardcore.listener.PlayerListener;
import be.sv3r.creatorhardcore.listener.ServerListener;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
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

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            provider.getProvider();
        }

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
