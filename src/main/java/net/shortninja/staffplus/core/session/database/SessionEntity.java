package net.shortninja.staffplus.core.session.database;

import net.shortninja.staffplusplus.vanish.VanishType;

import java.util.Set;
import java.util.UUID;

public class SessionEntity {

    private int id;
    private UUID playerUuid;
    private VanishType vanishType;
    private boolean staffMode;
    private Set<String> mutedStaffChatChannels;
    private String staffModeName;

    public SessionEntity() {
    }

    public SessionEntity(int id, UUID playerUuid, VanishType vanishType, boolean staffMode, Set<String> mutedStaffChatChannels, String staffModeName) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.vanishType = vanishType;
        this.staffMode = staffMode;
        this.mutedStaffChatChannels = mutedStaffChatChannels;
        this.staffModeName = staffModeName;
    }

    public SessionEntity(UUID playerUuid, VanishType vanishType, boolean staffMode, Set<String> mutedStaffChatChannels, String staffModeName) {
        this.playerUuid = playerUuid;
        this.vanishType = vanishType;
        this.staffMode = staffMode;
        this.mutedStaffChatChannels = mutedStaffChatChannels;
        this.staffModeName = staffModeName;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public VanishType getVanishType() {
        return vanishType;
    }

    public void setVanishType(VanishType vanishType) {
        this.vanishType = vanishType;
    }

    public void setStaffMode(boolean staffMode) {
        this.staffMode = staffMode;
    }

    public void setStaffModeName(String staffModeName) {
        this.staffModeName = staffModeName;
    }

    public boolean getStaffMode() {
        return staffMode;
    }

    public int getId() {
        return id;
    }

    public void setMutedStaffChatChannels(Set<String> mutedStaffChatChannels) {
        this.mutedStaffChatChannels = mutedStaffChatChannels;
    }

    public Set<String> getMutedStaffChatChannels() {
        return mutedStaffChatChannels;
    }

    public String getStaffModeName() {
        return staffModeName;
    }
}
