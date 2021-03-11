package net.shortninja.staffplus.staff.delayedactions;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.common.exceptions.BusinessException;
import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.command.arguments.ArgumentType;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.util.MessageCoordinator;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DelayArgumentExecutor {

    private final Messages messages;
    private final PlayerManager playerManager;
    private final MessageCoordinator message;

    public DelayArgumentExecutor() {
        messages = IocContainer.getMessages();
        playerManager = IocContainer.getPlayerManager();
        message = IocContainer.getMessage();
    }

    public boolean execute(CommandSender commandSender, String playerName, String command) {
        Optional<SppPlayer> player = playerManager.getOnOrOfflinePlayer(playerName);

        if (!player.isPresent()) {
            throw new BusinessException("&CCannot delay the command. No user found on this server with name: [" + playerName + "]", messages.prefixGeneral);
        }

        IocContainer.getDelayedActionsRepository().saveDelayedAction(player.get().getId(), command, Executor.CONSOLE);
        message.send(commandSender, "Your command has been delayed and will be executed next time [" + playerName + "] joins the server", messages.prefixGeneral);
        return true;
    }

    public ArgumentType getType() {
        return ArgumentType.DELAY;
    }

    public List<String> complete() {
        return Arrays.asList(getType().getPrefix());
    }
}
