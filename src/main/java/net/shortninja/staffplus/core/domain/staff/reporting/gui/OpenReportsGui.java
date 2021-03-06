package net.shortninja.staffplus.core.domain.staff.reporting.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.PagedGui;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.reporting.ManageReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.ReportService;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ManageReportConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OpenReportsGui extends PagedGui {

    private final PermissionHandler permissionHandler;
    private final ManageReportConfiguration manageReportConfiguration = StaffPlus.get().getIocContainer().get(ManageReportConfiguration.class);
    private final ReportItemBuilder reportItemBuilder = StaffPlus.get().getIocContainer().get(ReportItemBuilder.class);
    private final Supplier<AbstractGui> backGuiSupplier;

    public OpenReportsGui(Player player, String title, int page, Supplier<AbstractGui> backGuiSupplier) {
        super(player, title, page, backGuiSupplier);
        this.backGuiSupplier = backGuiSupplier;
        permissionHandler = StaffPlus.get().getIocContainer().get(PermissionHandler.class);
    }

    @Override
    protected OpenReportsGui getNextUi(Player player, SppPlayer target, String title, int page) {
        return new OpenReportsGui(player, title, page, backGuiSupplier);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                if (!permissionHandler.has(player, manageReportConfiguration.permissionAccept)) {
                    return;
                }
                int reportId = Integer.parseInt(StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().getNbtString(item));
                StaffPlus.get().getIocContainer().get(ManageReportService.class).acceptReport(player, reportId);
                backGuiSupplier.get().show(player);
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return StaffPlus.get().getIocContainer().get(ReportService.class).getUnresolvedReports(offset, amount).stream()
            .map(reportItemBuilder::build)
            .collect(Collectors.toList());
    }
}