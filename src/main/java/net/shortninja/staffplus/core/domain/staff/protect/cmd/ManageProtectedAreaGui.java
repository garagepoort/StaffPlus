package net.shortninja.staffplus.core.domain.staff.protect.cmd;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.domain.staff.protect.ProtectService;
import net.shortninja.staffplus.core.domain.staff.protect.ProtectedArea;
import net.shortninja.staffplus.core.domain.staff.teleport.TeleportService;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ManageProtectedAreaGui extends AbstractGui {
    private static final int SIZE = 54;

    private final ProtectService protectService = StaffPlus.get().getIocContainer().get(ProtectService.class);
    private final TeleportService teleportService = StaffPlus.get().getIocContainer().get(TeleportService.class);
    private final ProtectedArea protectedArea;

    public ManageProtectedAreaGui(String title, ProtectedArea protectedArea, Supplier<AbstractGui> previousGuiSupplier) {
        super(SIZE, title, previousGuiSupplier);
        this.protectedArea = protectedArea;
    }

    @Override
    public void buildGui() {

        IAction teleportAction = new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                teleportService.teleportSelf(player, protectedArea.getCornerPoint1());
            }

            @Override
            public boolean shouldClose(Player player) {
                return true;
            }
        };

        IAction deleteAction = new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                protectService.deleteProtectedArea(player, protectedArea.getId());
                previousGuiSupplier.get();
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };

        setItem(13, ProtectedAreaItemBuilder.build(protectedArea), null);

        addTeleportItem(protectedArea, teleportAction, 34);
        addTeleportItem(protectedArea, teleportAction, 35);
        addTeleportItem(protectedArea, teleportAction, 43);
        addTeleportItem(protectedArea, teleportAction, 44);

        addDeleteItem(protectedArea, deleteAction, 30);
        addDeleteItem(protectedArea, deleteAction, 31);
        addDeleteItem(protectedArea, deleteAction, 32);
        addDeleteItem(protectedArea, deleteAction, 39);
        addDeleteItem(protectedArea, deleteAction, 40);
        addDeleteItem(protectedArea, deleteAction, 41);
    }

    private void addDeleteItem(ProtectedArea protectedArea, IAction action, int slot) {
        ItemStack item = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(
            Items.editor(Items.createRedColoredGlass("Delete protected area", "Click to delete the protected area"))
                .setAmount(1)
                .build(), String.valueOf(protectedArea.getId()));
        setItem(slot, item, action);
    }

    private void addTeleportItem(ProtectedArea protectedArea, IAction action, int slot) {
        ItemStack item = StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(
            Items.editor(Items.createOrangeColoredGlass("Teleport", "Click to teleport yourself to this area"))
                .setAmount(1)
                .build(), String.valueOf(protectedArea.getId()));
        setItem(slot, item, action);
    }
}