package net.shortninja.staffplus.core.domain.staff.staffchat.config;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.common.config.AbstractConfigLoader;
import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@IocBean
public class StaffChatModuleLoader extends AbstractConfigLoader<StaffChatConfiguration> {

    @Override
    protected StaffChatConfiguration load() {
        boolean staffChatEnabled = defaultConfig.getBoolean("staff-chat-module.enabled");
        boolean bungeeEnabled = defaultConfig.getBoolean("staff-chat-module.bungee");
        List<StaffChatChannelConfiguration> channelConfigurations = getChannelConfigurations(defaultConfig);
        return new StaffChatConfiguration(staffChatEnabled, bungeeEnabled, channelConfigurations);
    }

    private List<StaffChatChannelConfiguration> getChannelConfigurations(FileConfiguration config) {
        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) config.getList("staff-chat-module.channels", new ArrayList<>());

        return Objects.requireNonNull(list).stream().map(map -> {
            String name = (String) map.get("name");
            String command = (String) map.get("command");
            String permission = (String) map.get("permission");
            String prefix = (String) map.get("prefix");
            String handle = (String) map.get("handle");
            return new StaffChatChannelConfiguration(name, command, permission, handle, prefix);
        }).collect(Collectors.toList());
    }
}
