package net.shortninja.staffplus.core.domain.staff.staffchat.cmd;

import be.garagepoort.mcioc.IocMultiProvider;
import be.garagepoort.mcioc.TubingConfiguration;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.domain.chat.ChatInterceptor;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChatInterceptor;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatServiceImpl;
import net.shortninja.staffplus.core.session.SessionManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@TubingConfiguration
public class StaffChatTubingCommandLoader {

    @IocMultiProvider(SppCommand.class)
    public static List<SppCommand> loadCommands(Messages messages, Options options, SessionManagerImpl sessionManager, StaffChatServiceImpl staffChatService, CommandService commandService) {
        List<SppCommand> commands = new ArrayList<>();
        List<StaffChatChannelConfiguration> channelConfigurations = options.staffChatConfiguration.getChannelConfigurations();
        for (StaffChatChannelConfiguration channelConfiguration : channelConfigurations) {
            commands.add(new StaffChatChannelCmd(messages, options, sessionManager, staffChatService, commandService, channelConfiguration));
            commands.add(new StaffChatMuteChannelCmd(messages, options, sessionManager, commandService, channelConfiguration));
        }
        return commands;
    }

    @IocMultiProvider(ChatInterceptor.class)
    public static List<ChatInterceptor> loadChatInterceptors(Options options, SessionManagerImpl sessionManager, PermissionHandler permissionHandler, StaffChatServiceImpl staffChatService) {
        return options.staffChatConfiguration.getChannelConfigurations().stream()
            .map(c -> new StaffChatChatInterceptor(staffChatService, permissionHandler, options, sessionManager, c))
            .collect(Collectors.toList());
    }
}