package net.shortninja.staffplus.core.domain.staff.warn.appeals.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.domain.staff.warn.warnings.Warning;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class AppealReasonSelectAction implements IAction {
    private final Options options = StaffPlus.get().getIocContainer().get(Options.class);
    private final Warning warning;
    private final Supplier<AbstractGui> previousGuiSupplier;

    public AppealReasonSelectAction(Warning warning, Supplier<AbstractGui> previousGuiSupplier) {
        this.warning = warning;
        this.previousGuiSupplier = previousGuiSupplier;
    }

    @Override
    public void click(Player player, ItemStack item, int slot, ClickType clickType) {
        if (warning.getAppeal().isPresent()) {
            return;
        }
        if (options.appealConfiguration.fixedAppealReason) {
            new AppealReasonSelectGui(warning, "Select your reason for appeal", previousGuiSupplier)
                .show(player);
        }
    }

    @Override
    public boolean shouldClose(Player player) {
        return false;
    }
}
