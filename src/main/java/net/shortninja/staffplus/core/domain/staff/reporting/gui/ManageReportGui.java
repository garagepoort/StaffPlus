package net.shortninja.staffplus.core.domain.staff.reporting.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.investigate.gui.InvestigationGuiComponent;
import net.shortninja.staffplus.core.domain.staff.reporting.Report;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ManageReportConfiguration;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.actions.DeleteReportAction;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.actions.RejectReportAction;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.actions.ReopenReportAction;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.actions.ResolveReportAction;
import net.shortninja.staffplus.core.domain.staff.reporting.gui.actions.TeleportToReportLocationAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ManageReportGui extends AbstractGui {
    private static final int SIZE = 54;

    private final PermissionHandler permission = StaffPlus.get().getIocContainer().get(PermissionHandler.class);
    private final ReportItemBuilder reportItemBuilder = StaffPlus.get().getIocContainer().get(ReportItemBuilder.class);
    private final InvestigationGuiComponent investigationGuiComponent = StaffPlus.get().getIocContainer().get(InvestigationGuiComponent.class);
    private final ManageReportConfiguration manageReportConfiguration = StaffPlus.get().getIocContainer().get(ManageReportConfiguration.class);

    private final Player player;
    private final Report report;

    public ManageReportGui(Player player, String title, Report report, Supplier<AbstractGui> previousGuiSupplier) {
        super(SIZE, title, previousGuiSupplier);
        this.player = player;
        this.report = report;
    }

    @Override
    public void buildGui() {

        IAction reopenAction = new ReopenReportAction();
        IAction resolveAction = new ResolveReportAction();
        IAction rejectAction = new RejectReportAction();
        IAction deleteAction = new DeleteReportAction();
        IAction teleportAction = new TeleportToReportLocationAction(report);

        setItem(13, reportItemBuilder.build(report), null);

        if(isAssignee() && permission.has(player, manageReportConfiguration.permissionResolve)) {
            addResolveItem(report, resolveAction, 34);
            addResolveItem(report, resolveAction, 35);
            addResolveItem(report, resolveAction, 43);
            addResolveItem(report, resolveAction, 44);
        }

        if(isAssignee() || permission.has(player, manageReportConfiguration.permissionReopenOther)) {
            addReopenItem(report, reopenAction, 27);
            addReopenItem(report, reopenAction, 28);
            addReopenItem(report, reopenAction, 36);
            addReopenItem(report, reopenAction, 37);
        }
        if(isAssignee() && permission.has(player, manageReportConfiguration.permissionReject)) {
            addRejectItem(report, rejectAction, 30);
            addRejectItem(report, rejectAction, 31);
            addRejectItem(report, rejectAction, 32);
            addRejectItem(report, rejectAction, 39);
            addRejectItem(report, rejectAction, 40);
            addRejectItem(report, rejectAction, 41);
        }
        if(isAssignee() && permission.has(player, manageReportConfiguration.permissionDelete)) {
            addDeleteItem(report, deleteAction, 8);
        }

        if(permission.has(player, manageReportConfiguration.permissionTeleport)) {
            addTeleportItem(teleportAction, 0);
        }
        investigationGuiComponent.addEvidenceButton(this, 14, report);
    }

    private boolean isAssignee() {
        return player.getUniqueId().equals(report.getStaffUuid());
    }

    private void addResolveItem(Report report, IAction action, int slot) {
        ItemStack item = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(
            Items.editor(Items.createGreenColoredGlass("Resolve report", "Click to mark this report as resolved"))
                .setAmount(1)
                .build(), String.valueOf(report.getId()));
        setItem(slot, item, action);
    }

    private void addRejectItem(Report report, IAction action, int slot) {
        ItemStack item = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(
            Items.editor(Items.createRedColoredGlass("Reject report", "Click to mark this report as rejected"))
                .setAmount(1)
                .build(), String.valueOf(report.getId()));
        setItem(slot, item, action);
    }

    private void addReopenItem(Report report, IAction action, int slot) {
        ItemStack item = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(
            Items.editor(Items.createWhiteColoredGlass("Unassign", "Click to unassign yourself from this report"))
                .setAmount(1)
                .build(), String.valueOf(report.getId()));
        setItem(slot, item, action);
    }

    private void addDeleteItem(Report report, IAction action, int slot) {
        ItemStack itemstack = Items.builder()
            .setMaterial(Material.REDSTONE_BLOCK)
            .setName("Delete")
            .addLore("Click to delete this report")
            .build();

        ItemStack item = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(
            Items.editor(itemstack)
                .setAmount(1)
                .build(), String.valueOf(report.getId()));
        setItem(slot, item, action);
    }

    private void addTeleportItem(IAction action, int slot) {
        ItemStack item = Items.createOrangeColoredGlass("Teleport", "Click to teleport to where this report was created");
        setItem(slot, item, action);
    }
}