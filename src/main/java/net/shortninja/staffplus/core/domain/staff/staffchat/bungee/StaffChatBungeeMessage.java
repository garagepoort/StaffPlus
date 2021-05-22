package net.shortninja.staffplus.core.domain.staff.staffchat.bungee;

import net.shortninja.staffplus.core.common.bungee.BungeeMessage;

public class StaffChatBungeeMessage extends BungeeMessage {

    private String channel;
    private String message;
    private String playerName;

    public StaffChatBungeeMessage(String serverName, String channel, String message, String playerName) {
        super(serverName);
        this.channel = channel;
        this.message = message;
        this.playerName = playerName;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String getPlayerName() {
        return playerName;
    }
}
