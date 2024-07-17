package be.sv3r.creatorhardcore;

import be.sv3r.creatorhardcore.command.GraceCommand;
import be.sv3r.creatorhardcore.command.SetCrudeCommand;
import be.sv3r.creatorhardcore.command.SetGracedCommand;
import be.sv3r.creatorhardcore.listener.ElytraListener;
import be.sv3r.creatorhardcore.listener.PlayerListener;
import be.sv3r.creatorhardcore.listener.ServerListener;
import be.sv3r.creatorhardcore.util.TimeUtil;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
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
        registerCommands();
        registerServerTimeHandler();
    }

    private void setupConfig() {
        saveDefaultConfig();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ServerListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ElytraListener(), this);
    }

    private void registerCommands() {
        @NotNull LifecycleEventManager<Plugin> manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("grace", "Laat je resterende grace periode zien", new GraceCommand());
            commands.register("setcrude", "Geeft de gekozen speler de status crude", new SetCrudeCommand());
            commands.register("setgraced", "Geeft de gekozen speler de status graced", new SetGracedCommand());
        });
    }

    private void registerServerTimeHandler() {
        getScheduler().runTaskTimer(this, TimeUtil::checkPlayers, 0, 1200L);
    }
}
