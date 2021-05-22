package net.shortninja.staffplus.core.session;

import net.shortninja.staffplus.core.common.gui.IGui;
import net.shortninja.staffplus.core.domain.chat.ChatAction;
import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplusplus.alerts.AlertType;
import net.shortninja.staffplusplus.session.IPlayerSession;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerSession implements IPlayerSession {

    private final UUID uuid;
    private final String name;
    private Material glassColor;
    private VanishType vanishType = VanishType.NONE;
    private IGui currentGui = null;
    private ChatAction chatAction = null;
    private Map<AlertType, Boolean> alertOptions = new HashMap<>();
    private List<String> playerNotes = new ArrayList<>();

    private String activeStaffChatChannel = null;
    private boolean isFrozen = false;
    private boolean underInvestigation = false;
    private boolean isProtected = false;
    private boolean muted = false;
    private boolean inStaffMode = false;
    private Set<String> mutedStaffChatChannels = new HashSet<>();
    private GeneralModeConfiguration modeConfiguration = null;

    public PlayerSession(UUID uuid, String name, boolean muted) {
        this.uuid = uuid;
        this.muted = muted;
        this.glassColor = Material.WHITE_STAINED_GLASS_PANE;
        this.name = name;

        for (AlertType alertType : AlertType.values()) {
            setAlertOption(alertType, true);
        }
    }

    public PlayerSession(UUID uuid, String name, Material glassColor, List<String> playerNotes, Map<AlertType, Boolean> alertOptions, boolean muted, VanishType vanishType) {
        this.uuid = uuid;
        this.name = name;
        this.glassColor = glassColor;
        this.playerNotes = playerNotes;
        this.alertOptions = alertOptions;
        this.muted = muted;
        this.vanishType = vanishType;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayerExact(name));
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    public Material getGlassColor() {
        return glassColor;
    }

    public void setGlassColor(Material glassColor) {
        this.glassColor = glassColor;
    }

    public List<String> getPlayerNotes() {
        return playerNotes;
    }

    public VanishType getVanishType() {
        return vanishType;
    }

    /**
     * This method should NOT be used if you want to update the user's vanish
     * type! Use the vanish handler!
     */
    public void setVanishType(VanishType vanishType) {
        this.vanishType = vanishType;
    }

    public void setInStaffMode(boolean inStaffMode) {
        this.inStaffMode = inStaffMode;
    }

    public void setModeConfiguration(GeneralModeConfiguration generalModeConfiguration) {
        this.modeConfiguration = generalModeConfiguration;
    }

    public Optional<GeneralModeConfiguration> getModeConfiguration() {
        return Optional.ofNullable(modeConfiguration);
    }

    @Override
    public boolean isInStaffMode() {
        return inStaffMode;
    }

    public Optional<IGui> getCurrentGui() {
        return Optional.ofNullable(currentGui);
    }

    public void setCurrentGui(IGui currentGui) {
        this.currentGui = currentGui;
    }

    public ChatAction getChatAction() {
        return chatAction;
    }

    public void setChatAction(ChatAction chatAction) {
        this.chatAction = chatAction;
    }

    public boolean shouldNotify(AlertType alertType) {
        return alertOptions.get(alertType) != null && alertOptions.get(alertType);
    }

    public Optional<String> getActiveStaffChatChannel() {
        return Optional.ofNullable(activeStaffChatChannel);
    }

    public void setActiveStaffChatChannel(String activeStaffChatChannel) {
        this.activeStaffChatChannel = activeStaffChatChannel;
    }

    @Override
    public boolean isFrozen() {
        return isFrozen;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public void setAlertOption(AlertType alertType, boolean isEnabled) {
        if (alertOptions.containsKey(alertType)) {
            alertOptions.replace(alertType, isEnabled);
        } else {
            alertOptions.put(alertType, isEnabled);
        }
    }

    public void addPlayerNote(String note) {
        playerNotes.add(note);
    }

    @Override
    public boolean isVanished() {
        return this.getVanishType() == VanishType.TOTAL;
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isStaffChatMuted(String channel) {
        return mutedStaffChatChannels.stream().anyMatch(s -> s.equalsIgnoreCase(channel));
    }

    public void setStaffChatMuted(String channelName, boolean staffChatMuted) {
        if (staffChatMuted) {
            mutedStaffChatChannels.add(channelName);
        } else {
            mutedStaffChatChannels.removeIf(c -> c.equalsIgnoreCase(channelName));
        }
    }

    public Set<String> getMutedStaffChatChannels() {
        return mutedStaffChatChannels;
    }

    public void setMutedStaffChatChannels(Set<String> mutedStaffChatChannels) {
        this.mutedStaffChatChannels = mutedStaffChatChannels;
    }

    public boolean isUnderInvestigation() {
        return underInvestigation;
    }

    public void setUnderInvestigation(boolean underInvestigation) {
        this.underInvestigation = underInvestigation;
    }
}