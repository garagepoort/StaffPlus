package net.shortninja.staffplus.staff.reporting.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.IAction;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.player.attribute.gui.PagedGui;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.reporting.Report;
import net.shortninja.staffplus.staff.reporting.ReportFilters;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class FindReportsGui extends PagedGui {

    private final PermissionHandler permissionHandler;
    private final Options options;
    private final ReportFilters reportFilters;

    public FindReportsGui(Player player, ReportFilters reportFilters, int page) {
        super(player, "Find reports", page);
        this.reportFilters = reportFilters;
        permissionHandler = IocContainer.getPermissionHandler();
        options = IocContainer.getOptions();
    }

    @Override
    protected FindReportsGui getNextUi(Player player, SppPlayer target, String title, int page) {
        return new FindReportsGui(player, reportFilters, page);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                int reportId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
                Report report = IocContainer.getReportService().getReport(reportId);
                new ManageReportGui(player, "Report by: " + report.getReporterName(), report, () -> new FindReportsGui(player, reportFilters, getCurrentPage()))
                    .show(player);
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return IocContainer.getReportService().findReports(reportFilters, offset, amount).stream()
                .map(ReportItemBuilder::build)
                .collect(Collectors.toList());
    }
}