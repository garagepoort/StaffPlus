package net.shortninja.staffplus.core.common.config.migrators;

import net.shortninja.staffplus.core.common.config.ConfigurationFile;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class StaffChatChannelMigrator implements ConfigMigrator {

    @Override
    public void migrate(List<ConfigurationFile> configs) {
        FileConfiguration config = getConfig(configs, "config");
        FileConfiguration permissionsConfig = getConfig(configs, "permissions");
        FileConfiguration commandsConfig = getConfig(configs, "commands");

        if (config.contains("staff-chat-module.handle")) {
            String handle = config.getString("staff-chat-module.handle");
            config.set("staff-chat-module.handle", null);

            String command = commandsConfig.getString("commands.staff-chat", "sc");
            String permission = permissionsConfig.getString("permissions.staff-chat", "staff.staffchat");
            migrateChannel(config, handle, command, permission);
        }
    }

    private void migrateChannel(FileConfiguration config, String handle, String command, String permission) {
        List<LinkedHashMap<String, Object>> channels =new ArrayList<>();
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();

        linkedHashMap.put("name", "staffchat");
        linkedHashMap.put("command", command);
        linkedHashMap.put("permission", permission);
        linkedHashMap.put("prefix", "&dStaffChat &8»");
        linkedHashMap.put("handle", handle);
        channels.add(linkedHashMap);
        config.set("staff-chat-module.channels", channels);
    }
}
