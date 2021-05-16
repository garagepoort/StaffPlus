package net.shortninja.staffplus.core.domain.chat;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;

import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.NONE;

@IocBean(conditionalOnProperty = "chat-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class ChatCmd extends AbstractCmd {
    private final ChatHandler chatHandler;
    private final PermissionHandler permissionHandler;

    public ChatCmd(PermissionHandler permissionHandler, Messages messages, Options options, ChatHandler chatHandler, CommandService commandService) {
        super(options.commandChat, messages, options, commandService);
        this.chatHandler = chatHandler;
        this.permissionHandler = permissionHandler;
        setPermissions(new HashSet<>(Arrays.asList(options.permissionChatClear, options.permissionChatSlow, options.permissionChatToggle)));
        setDescription("Executes the given chat management action.");
        setUsage("[clear | toggle | slow] {enable | disable | time}");
    }

    @Override
    public boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer targetPlayer) {
        if (args.length >= 2 && permissionHandler.isOp(sender)) {
            handleChatArgument(sender, args[0], args[1], false);
        } else if (args.length == 1) {
            handleChatArgument(sender, args[0], "", true);
        } else {
            sendHelp(sender);
        }

        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 1;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return NONE;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }

    private void handleChatArgument(CommandSender sender, String argument, String option, boolean shouldCheckPermission) {
        String name = sender instanceof Player ? sender.getName() : "Console";

        switch (argument.toLowerCase()) {
            case "clear":
                if (!shouldCheckPermission || permissionHandler.has(sender, options.permissionChatClear)) {
                    chatHandler.clearChat(name);
                } else messages.send(sender, messages.noPermission, messages.prefixGeneral);
                break;
            case "toggle":
                if (!shouldCheckPermission || permissionHandler.has(sender, options.permissionChatToggle)) {
                    chatHandler.setChatEnabled(name, option.isEmpty() ? !chatHandler.isChatEnabled() : Boolean.parseBoolean(option));
                } else messages.send(sender, messages.noPermission, messages.prefixGeneral);
                break;
            case "slow":
                if (!shouldCheckPermission || permissionHandler.has(sender, options.permissionChatSlow)) {
                    if (JavaUtils.isInteger(option)) {
                        chatHandler.setChatSlow(name, Integer.parseInt(option));
                    } else
                        messages.send(sender, messages.invalidArguments.replace("%usage%", getName() + " &7" + getUsage()), messages.prefixGeneral);
                } else messages.send(sender, messages.noPermission, messages.prefixGeneral);
                break;
            default:
                messages.send(sender, messages.invalidArguments.replace("%usage%", getName() + " &7" + getUsage()), messages.prefixGeneral);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        messages.send(sender, "&7" + messages.LONG_LINE, "");
        messages.send(sender, "&b/" + getName() + " &7" + getUsage(), messages.prefixGeneral);
        messages.send(sender, "&7" + messages.LONG_LINE, "");
    }
}