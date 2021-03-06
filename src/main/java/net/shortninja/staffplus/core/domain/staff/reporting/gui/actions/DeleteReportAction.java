package net.shortninja.staffplus.core.domain.staff.reporting.gui.actions;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.domain.staff.reporting.ManageReportService;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class DeleteReportAction implements IAction {
    private ManageReportService manageReportService = StaffPlus.get().getIocContainer().get(ManageReportService.class);

    @Override
    public void click(Player player, ItemStack item, int slot, ClickType clickType) {
        int reportId = Integer.parseInt(StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().getNbtString(item));
        manageReportService.deleteReport(player, reportId);
    }

    @Override
    public boolean shouldClose(Player player) {
        return true;
    }
}
