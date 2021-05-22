package net.shortninja.staffplus.core.domain.staff.reporting.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;

import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.reporting.config.CulpritFilterPredicate;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportReasonConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportTypeConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.ReportReasonSelectGui;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplus.core.domain.staff.reporting.ReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.ReportTypeSelectGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@IocBean(conditionalOnProperty = "reports-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class ReportPlayerCmd extends AbstractCmd {
    private static final CulpritFilterPredicate culpritFilterPredicate = new CulpritFilterPredicate(true);
    private final List<ReportTypeConfiguration> reportTypeConfigurations;
    private final List<ReportReasonConfiguration> reportReasonConfigurations;

    private final ReportService reportService;
    private final PlayerManager playerManager;

    public ReportPlayerCmd(Messages messages, Options options, ReportService reportService, PlayerManager playerManager, CommandService commandService) {
        super(options.commandReportPlayer, messages, options, commandService);
        this.reportService = reportService;
        this.playerManager = playerManager;
        setPermission(options.permissionReport);
        setDescription("Sends a report with the given player and reason.");
        setUsage("[player] [reason]");
        reportTypeConfigurations = options.reportConfiguration.getReportTypeConfigurations(culpritFilterPredicate);
        reportReasonConfigurations = options.reportConfiguration.getReportReasonConfigurations(culpritFilterPredicate);
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player) {
        if (!(sender instanceof Player)) {
            throw new BusinessException(messages.onlyPlayers);
        }

        String reason = options.reportConfiguration.isFixedReasonCulprit() ? null : JavaUtils.compileWords(args, 1);

        if (reportTypeConfigurations.isEmpty() && reportReasonConfigurations.isEmpty()) {
            reportService.sendReport((Player) sender, player, reason);
            return true;
        }

        if (!reportTypeConfigurations.isEmpty()) {
            new ReportTypeSelectGui((Player) sender, reason, player, reportTypeConfigurations, reportReasonConfigurations).show((Player) sender);
            return true;
        }

        new ReportReasonSelectGui((Player) sender, player, null, reportReasonConfigurations).show((Player) sender);
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        if (reportReasonConfigurations.isEmpty()) {
            return 2;
        }
        return 1;
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return PlayerRetrievalStrategy.BOTH;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.of(args[0]);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return playerManager.getAllPlayerNames().stream()
                .filter(s -> args[0].isEmpty() || s.contains(args[0]))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}