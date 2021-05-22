package net.shortninja.staffplus.core.common.config;

import be.garagepoort.mcioc.IocBean;
import me.rayzr522.jsonmessage.JSONMessage;
import net.shortninja.staffplus.core.application.data.LanguageFile;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.PlaceholderService;
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.common.utils.Strings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@IocBean
public class Messages {

    public final String LONG_LINE = "&m" + Strings.repeat('-', 48);

    /*
     * Prefixes
     */
    public final String prefixGeneral;
    public final String prefixProtect;
    public final String prefixReports;
    public final String prefixWarnings;
    public final String prefixTrace;
    public final String prefixInvestigations;
    /*
     * General
     */
    public final List<String> staffListStart;
    public final String staffListMember;
    public final List<String> staffListEnd;
    public final String lockdown;
    public final String revivedStaff;
    public final String revivedUser;
    public final String commandBlocked;
    public final String modeCommandBlocked;
    public final String commandOnCooldown;
    public final String noPermission;
    public final String playerOffline;
    public final String playerNotRegistered;
    public final String invalidArguments;
    public final String onlyPlayers;
    public final String noFound;
    public final String typeInput;
    public final String inputAccepted;

    public final String enabled;
    public final String disabled;

    /*
     * Infractions
     */
    public final String reported;
    public final String reportedStaff;
    public final String reportsCleared;
    public final List<String> reportsListStart;
    public final String reportsListEntry;
    public final List<String> reportsListEnd;
    public final String warned;
    public final String warn;
    public final String warningsNotify;
    public final List<String> warningsListStart;
    public final String warningsListEntry;
    public final List<String> warningsListEnd;
    public final List<String> infractionItem;
    /*
     * Chat
     */
    public final String staffChat;
    public final String chatClearLine;
    public final String chatCleared;
    public final String chatToggled;
    public final String chatPrevented;
    public final String chatSlowed;
    public final String chattingFast;
    public final String blacklistChatFormat;
    /*
     * Vanish
     */
    public final String totalVanish;
    public final String listVanish;
    public final String vanishEnabled;
    /*
     * Alerts
     */
    public final String alertChanged;
    public final String alertsName;
    public final String alertsChatPhraseDetected;
    public final String alertsMention;
    public final String alertsXray;
    /*
     * Staff Mode
     */
    public final String modeStatus;
    public final String modeOriginalLocation;
    public final String modeRandomTeleport;
    public final String modeNotEnoughPlayers;
    public final List<String> freeze;
    public final List<String> unfrozen;
    public final String staffFroze;
    public final String staffUnfroze;
    public final String cpsStart;
    public final String cpsFinishNormal;
    public final String cpsFinishMax;
    public final List<String> examineFood;
    public final String examineIp;
    public final String examineGamemode;
    public final String examineLocation;
    public final String examineWarn;
    public final String examineFreeze;
    public final String examineNotes;
    public final String follow;
    public final String noteAdded;
    public final String noteCleared;
    public final List<String> noteListStart;
    public final String noteListEntry;
    public final List<String> noteListEnd;
    public final String bypassed;
    public final String staffChatStatus;
    public final String staffChatMuted;
    public final String staffChatUnmuted;
    public final String freezeLogout;
    public final String freezeTitle;
    public final String freezeSubtitle;
    public final String strip;

    public final String kickedNotify;
    public final String kickMessage;

    public final String unbanned;
    public final String permanentBanned;
    public final String tempBanned;
    public final String permanentBannedKick;
    public final String tempBannedKick;

    public final String muteExpired;
    public final String unmuted;
    public final String permanentMuted;
    public final String tempMuted;
    public final String muted;

    public final String appealCreated;
    public final String appealApproved;
    public final String appealApprove;
    public final String appealRejected;
    public final String appealReject;
    public final String openAppealsNotify;

    // Investigations
    public final String investigatedInvestigationStarted;
    public final String investigatedInvestigationPaused;
    public final String investigatedInvestigationConcluded;
    public final String underInvestigationTitle;
    public final String underInvestigationJoin;
    public final String investigationStaffNotificationsStarted;
    public final String investigationStaffNotificationsConcluded;
    public final String investigationStaffNotificationsPaused;
    public final String investigationEvidenceLinked;
    public final String investigationEvidenceUnlinked;
    public final String investigationNoteAdded;
    public final String investigationNoteDeleted;

    private final PermissionHandler permission;
    private final PlaceholderService placeholderService;

    public Messages(PermissionHandler permission, PlaceholderService placeholderService) {
        this.permission = permission;
        this.placeholderService = placeholderService;
        FileConfiguration config = new LanguageFile().get();
        /*
         * Prefixes
         */
        prefixGeneral = config.getString("general-prefix");
        prefixProtect = config.getString("protect-prefix", "&dProtected &8»");
        prefixReports = config.getString("reports-prefix");
        prefixWarnings = config.getString("warnings-prefix");
        prefixTrace = config.getString("trace-prefix", "&dTrace &8»");
        prefixInvestigations = config.getString("investigations-prefix", "&dInvestigations &8»");
        /*
         * General
         */
        staffListStart = JavaUtils.stringToList(config.getString("staff-list-start"));
        staffListMember = config.getString("staff-list-member");
        staffListEnd = JavaUtils.stringToList(config.getString("staff-list-end"));
        lockdown = config.getString("lockdown");
        revivedStaff = config.getString("revived-staff");
        revivedUser = config.getString("revived-user");
        commandBlocked = config.getString("command-blocked");
        modeCommandBlocked = config.getString("mode-command-blocked");
        commandOnCooldown = config.getString("on-cooldown");
        noPermission = config.getString("no-permission");
        playerOffline = config.getString("player-offline");
        playerNotRegistered = config.getString("player-not-registered", "This player does not exist");
        invalidArguments = config.getString("invalid-arguments");
        onlyPlayers = config.getString("only-players");
        noFound = config.getString("no-found");
        typeInput = config.getString("type-input");
        inputAccepted = config.getString("input-accepted");

        enabled = config.getString("enabled", "enabled");
        disabled = config.getString("disabled", "disabled");

        /*
         * Infractions
         */
        reported = config.getString("reported");
        reportedStaff = config.getString("reported-staff");
        reportsCleared = config.getString("reports-cleared");
        reportsListStart = JavaUtils.stringToList(config.getString("reports-list-start"));
        reportsListEntry = config.getString("reports-list-entry");
        reportsListEnd = JavaUtils.stringToList(config.getString("reports-list-end"));
        warned = config.getString("warned");
        warn = config.getString("warn");
        warningsNotify = config.getString("warnings-notify");
        warningsListStart = JavaUtils.stringToList(config.getString("warnings-list-start"));
        warningsListEntry = config.getString("warnings-list-entry");
        warningsListEnd = JavaUtils.stringToList(config.getString("warnings-list-end"));
        infractionItem = JavaUtils.stringToList(config.getString("infraction-item"));
        /*
         * Chat
         */
        staffChat = config.getString("staff-chat");
        chatClearLine = config.getString("chat-clear-line");
        chatCleared = config.getString("chat-cleared");
        chatToggled = config.getString("chat-toggled");
        chatPrevented = config.getString("chat-prevented");
        chatSlowed = config.getString("chat-slowed");
        chattingFast = config.getString("chatting-fast");
        blacklistChatFormat = config.getString("blacklist-chat-format");
        /*
         * Vanish
         */
        totalVanish = config.getString("total-vanish");
        listVanish = config.getString("list-vanish");
        vanishEnabled = config.getString("vanish-enabled");
        /*
         * Alerts
         */
        alertChanged = config.getString("alert-changed");
        alertsName = config.getString("alerts-name");
        alertsChatPhraseDetected = config.getString("alerts-chat-phrase-detected");
        alertsMention = config.getString("alerts-mention");
        alertsXray = config.getString("alerts-xray");
        /*
         * Staff Mode
         */
        modeStatus = config.getString("mode-status");
        modeOriginalLocation = config.getString("mode-original-location");
        modeRandomTeleport = config.getString("mode-random-teleport");
        modeNotEnoughPlayers = config.getString("mode-not-enough-players");

        cpsStart = config.getString("cps-start");
        cpsFinishNormal = config.getString("cps-finish-normal");
        cpsFinishMax = config.getString("cps-finish-max");
        examineFood = JavaUtils.stringToList(config.getString("examine-food"));
        examineIp = config.getString("examine-ip");
        examineGamemode = config.getString("examine-gamemode");
        examineLocation = config.getString("examine-location");
        examineWarn = config.getString("examine-warn");
        examineFreeze = config.getString("examine-freeze");
        examineNotes = config.getString("examine-notes");
        follow = config.getString("follow");
        noteAdded = config.getString("note-added");
        noteCleared = config.getString("note-cleared");
        noteListStart = JavaUtils.stringToList(config.getString("note-list-start"));
        noteListEntry = config.getString("note-list-entry");
        noteListEnd = JavaUtils.stringToList(config.getString("note-list-end"));
        bypassed = config.getString("bypassed");
        staffChatStatus = config.getString("staff-chat-status");
        staffChatMuted = config.getString("staff-chat-muted");
        staffChatUnmuted = config.getString("staff-chat-unmuted");
        strip = config.getString("strip");

        freeze = JavaUtils.stringToList(config.getString("freeze"));
        freezeLogout = config.getString("freeze-logout");
        freezeTitle = config.getString("freeze-title");
        freezeSubtitle = config.getString("freeze-subtitle");
        unfrozen = JavaUtils.stringToList(config.getString("unfrozen"));
        staffFroze = config.getString("staff-froze");
        staffUnfroze = config.getString("staff-unfroze");


        kickedNotify = config.getString("kick-notifyplayers", "");
        kickMessage = config.getString("kick-kickmessage", "");

        unbanned = config.getString("ban-unbanned", "");
        permanentBanned = config.getString("ban-permabanned", "");
        tempBanned = config.getString("ban-tempbanned", "");
        permanentBannedKick = config.getString("ban-permabanned-kick", "");
        tempBannedKick = config.getString("ban-tempbanned-kick", "");

        muteExpired = config.getString("mute-expired", "");
        unmuted = config.getString("mute-unmuted", "");
        permanentMuted = config.getString("mute-permamuted", "");
        tempMuted = config.getString("mute-tempmuted", "");
        muted = config.getString("mute-muted", "");

        appealCreated = config.getString("appeal-created", "");
        appealApproved = config.getString("appeal-approved", "");
        appealRejected = config.getString("appeal-rejected", "");
        appealApprove = config.getString("appeal-approve", "");
        appealReject = config.getString("appeal-reject", "");
        openAppealsNotify = config.getString("appeal-open-notify", "");

        investigatedInvestigationStarted = config.getString("investigated.investigation-started", null);
        investigatedInvestigationPaused = config.getString("investigated.investigation-paused", null);
        investigatedInvestigationConcluded = config.getString("investigated.investigation-concluded", null);
        underInvestigationTitle = config.getString("investigated.under-investigation-title");
        underInvestigationJoin = config.getString("investigated.under-investigation-join", null);
        investigationStaffNotificationsStarted = config.getString("investigation.staff-notification-started", null);
        investigationStaffNotificationsConcluded = config.getString("investigation.staff-notification-concluded", null);
        investigationStaffNotificationsPaused = config.getString("investigation.staff-notification-paused", null);
        investigationEvidenceLinked = config.getString("investigation.staff-notification-evidence-linked", null);
        investigationEvidenceUnlinked = config.getString("investigation.staff-notification-evidence-unlinked", null);
        investigationNoteAdded = config.getString("investigation.staff-notification-note-added", null);
        investigationNoteDeleted = config.getString("investigation.staff-notification-note-deleted", null);
    }

    public String colorize(String message) {
        message = message.replaceAll("&&", "<ampersand>");
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message.replaceAll("<ampersand>", "&");
    }

    public void send(Player player, String message, String prefix, String permission) {
        if (!this.permission.has(player, permission)) {
            return;
        }

        message = placeholderService.setPlaceholders(player, message);
        player.sendMessage(buildMessage(prefix, message));
    }

    public void send(CommandSender sender, String message, String prefix) {
        message = placeholderService.setPlaceholders(sender, message);
        sender.sendMessage(buildMessage(prefix, message));
    }

    public void sendGlobalMessage(String message, String prefix) {
        Bukkit.broadcastMessage(buildMessage(prefix, message));
    }

    public void sendGroupMessage(String message, String permission, String prefix) {
        if (message == null) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            message = placeholderService.setPlaceholders(player, message);
            send(player, message, prefix, permission);
        }
    }

    public void sendGroupMessage(JSONMessage jsonMessage, String permission) {
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> this.permission.has(p, permission))
            .forEach(jsonMessage::send);
    }

    public void sendCollectedMessage(Player player, Collection<String> messages, String prefix) {
        for (String message : messages) {
            message = placeholderService.setPlaceholders(player, message);
            send(player, message, prefix);
        }
    }

    private String buildMessage(String prefix, String message) {
        if (StringUtils.isEmpty(prefix)) {
            return colorize(message);
        } else {
            return colorize(prefix + " " + message);
        }
    }
}