package net.shortninja.staffplus.staff.reporting.gui.actions;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.IAction;
import net.shortninja.staffplus.staff.reporting.ManageReportService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeleteReportAction implements IAction {
    private ManageReportService manageReportService = IocContainer.getManageReportService();

    @Override
    public void click(Player player, ItemStack item, int slot) {
        int reportId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
        manageReportService.deleteReport(player, reportId);
    }

    @Override
    public boolean shouldClose(Player player) {
        return true;
    }
}
