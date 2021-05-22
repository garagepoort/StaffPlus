package net.shortninja.staffplus.core.domain.staff.staffchat;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocListener;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.staffchat.bungee.StaffChatBungeeMessage;
import net.shortninja.staffplus.core.domain.staff.staffchat.bungee.StaffChatReceivedBungeeEvent;
import net.shortninja.staffplus.core.session.PlayerSession;
import net.shortninja.staffplus.core.session.SessionManagerImpl;
import net.shortninja.staffplusplus.staffmode.chat.StaffChatEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;

@IocBean
@IocListener
public class StaffChatServiceImpl implements net.shortninja.staffplusplus.staffmode.chat.StaffChatService, Listener {

    private static final String STAFFCHAT = "staffchat";
    private final Messages messages;
    private final Options options;
    private final PermissionHandler permissionHandler;
    private final StaffChatMessageFormatter staffChatMessageFormatter;

    private final SessionManagerImpl sessionManager;

    public StaffChatServiceImpl(Messages messages, Options options, PermissionHandler permissionHandler, StaffChatMessageFormatter staffChatMessageFormatter, SessionManagerImpl sessionManager) {
        this.messages = messages;
        this.options = options;
        this.permissionHandler = permissionHandler;
        this.staffChatMessageFormatter = staffChatMessageFormatter;

        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void handleBungeeMessage(StaffChatReceivedBungeeEvent event) {
        StaffChatBungeeMessage staffChatMessage = event.getStaffChatMessage();
        StaffChatChannelConfiguration channel = getChannel(staffChatMessage.getChannel());
        String formattedMessage = staffChatMessageFormatter.formatMessage(staffChatMessage.getPlayerName(), staffChatMessage.getMessage());
        sendMessageToStaff(channel, formattedMessage);
    }

    public void sendMessage(CommandSender sender, String channelName, String message) {
        StaffChatChannelConfiguration channel = getChannel(channelName);

        String formattedMessage = staffChatMessageFormatter.formatMessage(sender, message);
        sendMessageToStaff(channel, formattedMessage);

        if (sender instanceof Player) {
            sendEvent(new StaffChatEvent((Player) sender, options.serverName, message, channelName));
        }
    }

    private StaffChatChannelConfiguration getChannel(String channelName) {
        return options.staffChatConfiguration.getChannelConfigurations().stream()
            .filter(c -> c.getName().equalsIgnoreCase(channelName))
            .findFirst().orElseThrow(() -> new ConfigurationException("No channel with name [" + channelName + "] configured"));
    }

    public boolean hasHandle(String channelName, String message) {
        StaffChatChannelConfiguration channel = getChannel(channelName);
        return channel.getHandle().isPresent() && StringUtils.isNotEmpty(channel.getHandle().get()) && message.startsWith(channel.getHandle().get());
    }

    /**
     * * @deprecated Please use sendMessage(String channelName, String message)      
     */
    @Deprecated
    @Override
    public void sendMessage(String message) {
        StaffChatChannelConfiguration channel = getChannel(STAFFCHAT);
        sendMessageToStaff(channel, message);
    }

    @Override
    public void sendMessage(String channelName, String message) {
        StaffChatChannelConfiguration channel = getChannel(channelName);
        sendMessageToStaff(channel, message);
    }

    private void sendMessageToStaff(StaffChatChannelConfiguration channel, String formattedMessage) {
        sessionManager.getAll().stream()
            .filter(playerSession -> !playerSession.isStaffChatMuted(channel.getName()))
            .map(PlayerSession::getPlayer)
            .filter(Optional::isPresent)
            .filter(player -> player.get().isOnline() && permissionHandler.has(player.get(), channel.getPermission().orElse(null)))
            .forEach(player -> messages.send(player.get(), formattedMessage, channel.getPrefix()));
    }

}
