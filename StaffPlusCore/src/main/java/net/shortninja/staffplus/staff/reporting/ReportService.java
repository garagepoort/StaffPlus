package net.shortninja.staffplus.staff.reporting;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.bungee.ServerSwitcher;
import net.shortninja.staffplus.common.exceptions.BusinessException;
import net.shortninja.staffplus.staff.delayedactions.DelayedActionsRepository;
import net.shortninja.staffplus.staff.delayedactions.Executor;
import net.shortninja.staffplusplus.reports.CreateReportEvent;
import net.shortninja.staffplusplus.reports.ReportStatus;
import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.infractions.Infraction;
import net.shortninja.staffplus.staff.infractions.InfractionCount;
import net.shortninja.staffplus.staff.infractions.InfractionProvider;
import net.shortninja.staffplus.staff.infractions.InfractionType;
import net.shortninja.staffplus.staff.reporting.database.ReportRepository;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;
import java.util.*;

import static net.shortninja.staffplus.util.BukkitUtils.sendEvent;
import static org.bukkit.Bukkit.getScheduler;

public class ReportService implements InfractionProvider {

    private static final Map<UUID, Long> lastUse = new HashMap<>();

    private final PermissionHandler permission;
    private final MessageCoordinator message;
    private final Options options = IocContainer.getOptions();
    private final Messages messages;
    private final PlayerManager playerManager;
    private final ReportRepository reportRepository;
    private final DelayedActionsRepository delayedActionsRepository;

    public ReportService(PermissionHandler permission, MessageCoordinator message, ReportRepository reportRepository, Messages messages, PlayerManager playerManager, DelayedActionsRepository delayedActionsRepository) {
        this.permission = permission;
        this.message = message;
        this.reportRepository = reportRepository;
        this.messages = messages;
        this.playerManager = playerManager;
        this.delayedActionsRepository = delayedActionsRepository;
    }

    public List<Report> getReports(SppPlayer player, int offset, int amount) {
        return reportRepository.getReports(player.getId(), offset, amount);
    }

    public List<Report> findReports(ReportFilters reportFilters, int offset, int amount) {
        return reportRepository.findReports(reportFilters, offset, amount);
    }

    public List<Report> getReports(UUID playerUuid, int offset, int amount) {
        SppPlayer user = getUser(playerUuid);
        return reportRepository.getReports(user.getId(), offset, amount);
    }

    public void sendReport(Player player, SppPlayer user, String reason) {
        validateCoolDown(player);
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {

            // Offline users cannot bypass being reported this way. Permissions are taken away upon logging out
            if (user.isOnline() && permission.has(user.getPlayer(), options.permissionReportBypass)) {
                message.send(player, messages.bypassed, messages.prefixGeneral);
                return;
            }

            Report report = new Report(
                user.getId(),
                user.getUsername(),
                reason,
                player.getName(),
                player.getUniqueId(),
                ReportStatus.OPEN,
                ZonedDateTime.now(),
                player.getLocation(),
                options.serverName);

            int id = reportRepository.addReport(report);
            report.setId(id);

            message.send(player, messages.reported.replace("%player%", report.getReporterName()).replace("%target%", report.getCulpritName()).replace("%reason%", report.getReason()), messages.prefixReports);
            message.sendGroupMessage(messages.reportedStaff.replace("%target%", report.getReporterName()).replace("%player%", report.getCulpritName()).replace("%reason%", report.getReason()), options.permissionReportUpdateNotifications, messages.prefixReports);
            options.reportConfiguration.getSound().playForGroup(options.permissionReportUpdateNotifications);

            lastUse.put(player.getUniqueId(), System.currentTimeMillis());
            sendEvent(new CreateReportEvent(report));
        });
    }

    public void sendReport(Player player, String reason) {
        validateCoolDown(player);
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Report report = new Report(
                null,
                null,
                reason,
                player.getName(),
                player.getUniqueId(),
                ReportStatus.OPEN,
                ZonedDateTime.now(),
                player.getLocation(),
                options.serverName);

            int id = reportRepository.addReport(report);
            report.setId(id);

            message.send(player, messages.reported.replace("%player%", report.getReporterName()).replace("%target%", "unknown").replace("%reason%", report.getReason()), messages.prefixReports);
            message.sendGroupMessage(messages.reportedStaff.replace("%target%", report.getReporterName()).replace("%player%", "unknown").replace("%reason%", report.getReason()), options.permissionReportUpdateNotifications, messages.prefixReports);
            options.reportConfiguration.getSound().playForGroup(options.permissionReportUpdateNotifications);

            lastUse.put(player.getUniqueId(), System.currentTimeMillis());
            sendEvent(new CreateReportEvent(report));
        });
    }

    public Collection<Report> getUnresolvedReports(int offset, int amount) {
        return reportRepository.getUnresolvedReports(offset, amount);
    }

    public Collection<Report> getAllAssignedReports(int offset, int amount) {
        return reportRepository.getAssignedReports(offset, amount);
    }

    public Collection<Report> getAssignedReports(UUID staffUuid, int offset, int amount) {
        return reportRepository.getAssignedReports(staffUuid, offset, amount);
    }

    public Collection<Report> getMyReports(UUID reporterUuid, int offset, int amount) {
        return reportRepository.getMyReports(reporterUuid, offset, amount);
    }

    public List<Report> getMyReports(UUID reporterUuid) {
        return reportRepository.getMyReports(reporterUuid);
    }

    private void validateCoolDown(CommandSender sender) {
        long last = sender instanceof Player ? (lastUse.containsKey(((Player) sender).getUniqueId()) ? lastUse.get(((Player) sender).getUniqueId()) : 0) : 0;
        long remaining = (System.currentTimeMillis() - last) / 1000;

        if (remaining < options.reportConfiguration.getCooldown()) {
            throw new BusinessException(messages.commandOnCooldown.replace("%seconds%", Long.toString(options.reportConfiguration.getCooldown() - remaining)), messages.prefixGeneral);
        }
    }

    private SppPlayer getUser(UUID playerUuid) {
        Optional<SppPlayer> player = playerManager.getOnOrOfflinePlayer(playerUuid);
        if (!player.isPresent()) {
            throw new BusinessException(messages.playerNotRegistered, messages.prefixGeneral);
        }
        return player.get();
    }

    public Report getReport(int reportId) {
        return reportRepository.findReport(reportId).orElseThrow(() -> new BusinessException("Report with id [" + reportId + "] not found", messages.prefixReports));
    }

    public void goToReportLocation(Player player, int reportId) {
        Report report = getReport(reportId);
        Location location = report.getLocation().orElseThrow(() -> new BusinessException("Cannot teleport to report, report has no known location"));
        if (report.getServerName().equalsIgnoreCase(options.serverName)) {
            player.teleport(location);
            message.send(player, "You have been teleported to the location where this report was created", messages.prefixReports);
        } else {
            String command = "staffplus:teleport-to-report " + reportId + " " + report.getServerName();
            delayedActionsRepository.saveDelayedAction(player.getUniqueId(), command, Executor.PLAYER, report.getServerName());
            ServerSwitcher.switchServer(player, report.getServerName());
        }
    }

    @Override
    public List<? extends Infraction> getInfractions(Player executor, UUID playerUUID) {
        if (!options.infractionsConfiguration.isShowReported()) {
            return Collections.emptyList();
        }
        return reportRepository.getReportsByOffender(playerUUID);
    }

    @Override
    public Optional<InfractionCount> getInfractionsCount() {
        if (!options.infractionsConfiguration.isShowReported()) {
            return Optional.empty();
        }
        return Optional.of(new InfractionCount(InfractionType.REPORTED, reportRepository.getReportedCount()));
    }

    @Override
    public InfractionType getType() {
        return InfractionType.REPORTED;
    }
}
