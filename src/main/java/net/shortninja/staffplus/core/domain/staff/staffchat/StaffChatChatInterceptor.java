package net.shortninja.staffplus.core.domain.staff.staffchat;

import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.domain.chat.ChatInterceptor;
import net.shortninja.staffplus.core.session.PlayerSession;
import net.shortninja.staffplus.core.session.SessionManagerImpl;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StaffChatChatInterceptor implements ChatInterceptor {
    private final StaffChatServiceImpl staffChatService;
    private final PermissionHandler permission;
    private final Options options;
    private final SessionManagerImpl sessionManager;
    private final StaffChatChannelConfiguration channelConfiguration;

    public StaffChatChatInterceptor(StaffChatServiceImpl staffChatService, PermissionHandler permission, Options options, SessionManagerImpl sessionManager, StaffChatChannelConfiguration channelConfiguration) {
        this.staffChatService = staffChatService;
        this.permission = permission;
        this.options = options;
        this.sessionManager = sessionManager;
        this.channelConfiguration = channelConfiguration;
    }

    @Override
    public boolean intercept(AsyncPlayerChatEvent event) {
        if(!options.staffChatConfiguration.isEnabled()) {
            return false;
        }

        PlayerSession session = sessionManager.get(event.getPlayer().getUniqueId());

        if (session.getActiveStaffChatChannel().isPresent() && session.getActiveStaffChatChannel().get().equalsIgnoreCase(channelConfiguration.getName())) {
            staffChatService.sendMessage(event.getPlayer(), session.getActiveStaffChatChannel().get(), event.getMessage());
            return true;
        }

        if (staffChatService.hasHandle(channelConfiguration.getName(), event.getMessage()) && permission.has(event.getPlayer(), channelConfiguration.getPermission().orElse(null))) {
            staffChatService.sendMessage(event.getPlayer(), channelConfiguration.getName(), event.getMessage().substring(channelConfiguration.getHandle().orElse("").length()));
            return true;
        }
        return false;
    }
}
