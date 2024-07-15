package be.sv3r.creatorhardcore.command;

import be.sv3r.creatorhardcore.listener.state.PlayerState;
import be.sv3r.creatorhardcore.util.MessageUtil;
import be.sv3r.creatorhardcore.util.PlayerUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class GraceCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        if (!(commandSourceStack.getExecutor() instanceof Player player)) return;

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!(dataContainer.has(PlayerUtil.joinTimeKey) || !(dataContainer.has(PlayerUtil.stateKey)))) {
            MessageUtil.sendPermissionDeniedMessage(player);
            return;
        }

        String state = dataContainer.get(PlayerUtil.stateKey, PersistentDataType.STRING);

        if (Objects.equals(state, PlayerState.GRACED.toString())) {
            MessageUtil.sendRespawnMessage(player);
        } else if (Objects.equals(state, PlayerState.CRUDE.toString())) {
            MessageUtil.sendRemindGraceMessage(player);
        } else {
            MessageUtil.sendPermissionDeniedMessage(player);
        }
    }
}
