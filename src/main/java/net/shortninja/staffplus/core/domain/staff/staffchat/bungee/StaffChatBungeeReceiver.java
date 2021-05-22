package net.shortninja.staffplus.core.domain.staff.staffchat.bungee;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMessageListener;
import net.shortninja.staffplus.core.common.Constants;
import net.shortninja.staffplus.core.common.bungee.BungeeClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Optional;

@IocBean(conditionalOnProperty = "staff-chat-module.bungee=true")
@IocMessageListener(channel = Constants.BUNGEE_CORD_CHANNEL)
public class StaffChatBungeeReceiver implements PluginMessageListener {

    private final BungeeClient bungeeClient;

    public StaffChatBungeeReceiver(BungeeClient bungeeClient) {
        this.bungeeClient = bungeeClient;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Optional<StaffChatBungeeMessage> bungeeMessage = bungeeClient.handleReceived(channel, Constants.BUNGEE_STAFFCHAT_CHANNEL, message, StaffChatBungeeMessage.class);
        bungeeMessage.ifPresent(b -> Bukkit.getPluginManager().callEvent(new StaffChatReceivedBungeeEvent(b)));
    }
}
