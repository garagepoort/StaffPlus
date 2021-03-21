package net.shortninja.staffplus.domain.staff.altaccountdetect;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.application.IocContainer;
import net.shortninja.staffplus.common.config.Options;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AltDetectionListener implements Listener {

    private final Options options = IocContainer.getOptions();
    private final AltDetectionService altDetectionService = IocContainer.getAltDetectionService();

    public AltDetectionListener() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!options.altDetectConfiguration.isEnabled()) {
            return;
        }

        altDetectionService.detectAltAccount(event.getPlayer());
    }
}
