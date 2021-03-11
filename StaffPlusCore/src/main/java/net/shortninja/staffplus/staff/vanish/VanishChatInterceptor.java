package net.shortninja.staffplus.staff.vanish;

import net.shortninja.staffplus.server.chat.ChatInterceptor;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.util.MessageCoordinator;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class VanishChatInterceptor implements ChatInterceptor {

    private final VanishServiceImpl vanishServiceImpl;
    private final Options options;
    private final MessageCoordinator message;
    private final Messages messages;

    public VanishChatInterceptor(VanishServiceImpl vanishServiceImpl, Options options, MessageCoordinator message, Messages messages) {
        this.vanishServiceImpl = vanishServiceImpl;
        this.options = options;
        this.message = message;
        this.messages = messages;
    }

    @Override
    public boolean intercept(AsyncPlayerChatEvent event) {
        if (options.vanishEnabled && !options.vanishChatEnabled && vanishServiceImpl.isVanished(event.getPlayer())) {
            this.message.send(event.getPlayer(), messages.chatPrevented, messages.prefixGeneral);
            return true;
        }
        return false;
    }
}
