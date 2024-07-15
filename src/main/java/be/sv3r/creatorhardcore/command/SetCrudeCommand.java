package be.sv3r.creatorhardcore.command;

import be.sv3r.creatorhardcore.CreatorHardcore;
import be.sv3r.creatorhardcore.listener.state.PlayerState;
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
public class SetCrudeCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        if (!(commandSourceStack.getExecutor() instanceof Player player)) return;
        if (!player.hasPermission("creatorhardcore.admin")) {
            MessageUtil.sendPermissionDeniedMessage(player);
            return;
        }

        if (strings.length != 1) {
            MessageUtil.sendMessageWithPrefix(player, "/setcrude <speler>");
            return;
        }

        Player setCrudePlayer = CreatorHardcore.getPlugin().getServer().getPlayer(strings[0]);
        if (setCrudePlayer == null) {
            MessageUtil.sendMessageWithPrefix(player, "De speler <white><bold>" + strings[0] + "</bold></white> bestaat niet!");
            return;
        }

        PersistentDataContainer dataContainer = setCrudePlayer.getPersistentDataContainer();

        if (!dataContainer.has(PlayerUtil.stateKey, PersistentDataType.STRING)) {
            MessageUtil.sendMessageWithPrefix(player, "Deze speler bevindt zich niet in een status!");
            return;
        }

        PlayerUtil.setPlayerState(setCrudePlayer, PlayerState.CRUDE);

        Location worldSpawn = CreatorHardcore.getPlugin().getServer().getWorlds().getFirst().getSpawnLocation();

        if (setCrudePlayer.getRespawnLocation() != null) {
            setCrudePlayer.teleport(setCrudePlayer.getRespawnLocation());
        } else {
            setCrudePlayer.teleport(worldSpawn);
        }

        setCrudePlayer.setGameMode(GameMode.SURVIVAL);
        dataContainer.set(PlayerUtil.joinTimeKey, PersistentDataType.STRING, Instant.MIN.toString());

        MessageUtil.sendMessageWithPrefix(player, "Speler <white><bold>" + setCrudePlayer.getName() + "</bold></white> is nu crude!");
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
