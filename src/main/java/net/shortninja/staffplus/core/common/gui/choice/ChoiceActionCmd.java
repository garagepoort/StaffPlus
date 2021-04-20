package net.shortninja.staffplus.core.common.gui.choice;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;

import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@IocBean
@IocMultiProvider(SppCommand.class)
public class ChoiceActionCmd extends AbstractCmd {

    private final ChoiceChatService choiceChatService;

    public ChoiceActionCmd(Messages messages, Options options, ChoiceChatService choiceChatService, CommandService commandService) {
        super("choice-action", messages, options, commandService);
        this.choiceChatService = choiceChatService;
        setPermission(options.permissionMode);
        setDescription("Selects 1 of 2 action.");
        setUsage("[option1|option2] [actionUuid]");
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player) {
        if (!(sender instanceof Player)) {
            throw new BusinessException(messages.onlyPlayers);
        }
        String action = args[0];
        UUID uuid = UUID.fromString(args[1]);

        if (action.equalsIgnoreCase("option1")) {
            choiceChatService.selectOption1(uuid, (Player) sender);
            return true;
        }
        if (action.equalsIgnoreCase("option2")) {
            choiceChatService.selectOption2(uuid, (Player) sender);
            return true;
        }
        return false;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 2;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return PlayerRetrievalStrategy.NONE;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }
}
