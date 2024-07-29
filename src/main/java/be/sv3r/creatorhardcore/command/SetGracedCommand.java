package be.sv3r.creatorhardcore.command;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.listener.state.PlayerState;
import be.sv3r.creatorhardcore.task.PlayerCrudeTask;
import be.sv3r.creatorhardcore.util.MessageUtil;
import be.sv3r.creatorhardcore.util.PlayerUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class SetGracedCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        if (!(commandSourceStack.getExecutor() instanceof Player player)) return;
        if (!player.hasPermission("creatorhardcore.admin")) {
            MessageUtil.sendPermissionDeniedMessage(player);
            return;
        }

        if (strings.length != 1) {
            MessageUtil.sendMessageWithPrefix(player, "/setgraced <speler>");
            return;
        }

        Player setGracedPlayer = CreatorHardcore.getPlugin().getServer().getPlayer(strings[0]);
        if (setGracedPlayer == null) {
            MessageUtil.sendMessageWithPrefix(player, "De speler <white><bold>" + strings[0] + "</bold></white> bestaat niet!");
            return;
        }

        PersistentDataContainer dataContainer = setGracedPlayer.getPersistentDataContainer();

        if (!dataContainer.has(PlayerUtil.stateKey, PersistentDataType.STRING)) {
            MessageUtil.sendMessageWithPrefix(player, "Deze speler bevindt zich niet in een status!");
            return;
        }

        Location worldSpawn = CreatorHardcore.getPlugin().getServer().getWorlds().getFirst().getSpawnLocation();

        if (setGracedPlayer.getRespawnLocation() != null) {
            setGracedPlayer.teleport(setGracedPlayer.getRespawnLocation());
        } else {
            setGracedPlayer.teleport(worldSpawn);
        }

        setGracedPlayer.setGameMode(GameMode.SURVIVAL);
        CreatorHardcore plugin = CreatorHardcore.getPlugin();
        CreatorHardcore.getScheduler().runTaskLaterAsynchronously(plugin, new PlayerCrudeTask(plugin, player.getUniqueId()), PlayerUtil.gracePeriod * 1200);
        dataContainer.set(PlayerUtil.joinTimeKey, PersistentDataType.STRING, Instant.now().toString());
        PlayerUtil.setPlayerState(setGracedPlayer, PlayerState.GRACED);
        MessageUtil.sendRespawnMessage(setGracedPlayer);

        MessageUtil.sendMessageWithPrefix(player, "Speler <white><bold>" + setGracedPlayer.getName() + "</bold></white> is nu graced!");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length == 0) {
            return CreatorHardcore.getPlugin().getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }
}
