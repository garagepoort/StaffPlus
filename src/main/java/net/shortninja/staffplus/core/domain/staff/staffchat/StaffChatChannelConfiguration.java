package net.shortninja.staffplus.core.domain.staff.staffchat;

import java.util.Optional;

public class StaffChatChannelConfiguration {

    private final String name;
    private String command;
    private String permission;
    private String handle;
    private String prefix;

    public StaffChatChannelConfiguration(String name, String command, String permission, String handle, String prefix) {
        this.name = name;
        this.command = command;
        this.permission = permission;
        this.handle = handle;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public Optional<String> getHandle() {
        return Optional.ofNullable(handle);
    }

    public String getPrefix() {
        return prefix;
    }
}
