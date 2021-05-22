package net.shortninja.staffplus.core.domain.staff.staffchat.cmd;

import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatServiceImpl;
import net.shortninja.staffplus.core.session.PlayerSession;
import net.shortninja.staffplus.core.session.SessionManagerImpl;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class StaffChatChannelCmd extends AbstractCmd {
    private final SessionManagerImpl sessionManager;
    private final StaffChatServiceImpl staffChatService;
    private final StaffChatChannelConfiguration channelConfiguration;

    public StaffChatChannelCmd(Messages messages, Options options, SessionManagerImpl sessionManager, StaffChatServiceImpl staffChatService, CommandService commandService, StaffChatChannelConfiguration channelConfiguration) {
        super(channelConfiguration.getCommand(), messages, options, commandService);
        this.sessionManager = sessionManager;
        this.staffChatService = staffChatService;
        this.channelConfiguration = channelConfiguration;
        setDescription("Sends a message or toggles staff chat for channel:" + channelConfiguration.getName());
        setUsage("{message}");
        channelConfiguration.getPermission().ifPresent(this::setPermission);
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player) {
        if (args.length > 0) {
            staffChatService.sendMessage(sender, channelConfiguration.getName(), JavaUtils.compileWords(args, 0));
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Please provide a message");
                return false;
            }
            PlayerSession session = sessionManager.get(((Player) sender).getUniqueId());

            if (session.getActiveStaffChatChannel().isPresent() && session.getActiveStaffChatChannel().get().equalsIgnoreCase(channelConfiguration.getName())) {
                messages.send(sender, messages.staffChatStatus.replace("%status%", messages.disabled), channelConfiguration.getPrefix());
                session.setActiveStaffChatChannel(null);
            } else {
                messages.send(sender, messages.staffChatStatus.replace("%status%", messages.enabled), channelConfiguration.getPrefix());
                session.setActiveStaffChatChannel(channelConfiguration.getName());
            }
        }

        if (sender instanceof Player) {
            sessionManager.saveSession((Player) sender);
        }
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 0;
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