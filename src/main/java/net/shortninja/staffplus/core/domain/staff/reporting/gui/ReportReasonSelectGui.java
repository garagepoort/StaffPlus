package net.shortninja.staffplus.core.domain.staff.reporting.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.domain.staff.reporting.config.CulpritFilterPredicate;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportReasonConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.actions.SelectReportReasonAction;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.getInventorySize;

public class ReportReasonSelectGui extends AbstractGui {
    private final Player staff;
    private final String type;
    private final SppPlayer targetPlayer;
    private List<ReportReasonConfiguration> reportReasonConfigurations;

    public ReportReasonSelectGui(Player staff, SppPlayer targetPlayer, String type, List<ReportReasonConfiguration> reportReasonConfigurations) {
        super(getInventorySize(StaffPlus.get().getIocContainer().get(Options.class).reportConfiguration.getReportReasonConfigurations(new CulpritFilterPredicate(false)).size()), "Select the reason for report");
        this.staff = staff;
        this.targetPlayer = targetPlayer;
        this.type = type;
        this.reportReasonConfigurations = reportReasonConfigurations.stream()
            .filter(r -> type == null || (r.getReportType().isPresent() && r.getReportType().get().equals(type)))
            .collect(Collectors.toList());
    }

    public ReportReasonSelectGui(Player staff, String type, List<ReportReasonConfiguration> reportReasonConfigurations) {
        super(getInventorySize(StaffPlus.get().getIocContainer().get(Options.class).reportConfiguration.getReportReasonConfigurations(new CulpritFilterPredicate(false)).size()), "Select the reason for report");
        this.staff = staff;
        this.type = type;
        this.targetPlayer = null;
        this.reportReasonConfigurations = reportReasonConfigurations.stream()
            .filter(r -> type == null || (r.getReportType().isPresent() && r.getReportType().get().equals(type)))
            .collect(Collectors.toList());
    }

    @Override
    public void buildGui() {
        int count = 0;
        for (ReportReasonConfiguration reportReasonConfiguration : reportReasonConfigurations) {
            setItem(count, ReportReasonItemBuilder.build(reportReasonConfiguration), new SelectReportReasonAction(reportReasonConfiguration, staff, targetPlayer, type));
            count++;
        }
    }
}