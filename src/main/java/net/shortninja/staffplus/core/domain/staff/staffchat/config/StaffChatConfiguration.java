package net.shortninja.staffplus.core.domain.staff.staffchat.config;

import net.shortninja.staffplus.core.domain.staff.staffchat.StaffChatChannelConfiguration;

import java.util.List;

public class StaffChatConfiguration {

    private final boolean enabled;
    private final boolean bungeeEnabled;
    private final List<StaffChatChannelConfiguration> channelConfigurations;

    public StaffChatConfiguration(boolean enabled,
                                  boolean bungeeEnabled,
                                  List<StaffChatChannelConfiguration> channelConfigurations) {
        this.enabled = enabled;
        this.bungeeEnabled = bungeeEnabled;
        this.channelConfigurations = channelConfigurations;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isBungeeEnabled() {
        return bungeeEnabled;
    }

    public List<StaffChatChannelConfiguration> getChannelConfigurations() {
        return channelConfigurations;
    }
}
