package net.shortninja.staffplus.core.domain.staff.mute.config;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.configuration.ConfigProperty;

@IocBean
public class MuteConfiguration {

    @ConfigProperty("mute-module.enabled")
    public boolean muteEnabled;

    @ConfigProperty("permissions:tempmute")
    public String permissionTempmutePlayer;
    @ConfigProperty("permissions:mute-bypass")
    public String permissionMuteByPass;
    @ConfigProperty("permissions:mute-notifications")
    public String staffNotificationPermission;

    public final MuteGuiItemConfig guiItemConfig;

    public MuteConfiguration(MuteGuiItemConfig guiItemConfig) {
        this.guiItemConfig = guiItemConfig;
    }
}
