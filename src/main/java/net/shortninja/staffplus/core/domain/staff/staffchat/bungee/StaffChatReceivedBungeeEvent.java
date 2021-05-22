package net.shortninja.staffplus.core.domain.staff.staffchat.bungee;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StaffChatReceivedBungeeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final StaffChatBungeeMessage staffChatMessage;

    public StaffChatReceivedBungeeEvent(StaffChatBungeeMessage staffChatMessage) {
        this.staffChatMessage = staffChatMessage;
    }

    public StaffChatBungeeMessage getStaffChatMessage() {
        return staffChatMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
