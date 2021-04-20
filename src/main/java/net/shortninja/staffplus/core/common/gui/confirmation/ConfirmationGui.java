package net.shortninja.staffplus.core.common.gui.confirmation;

import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.choice.ChoiceAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmationGui extends AbstractGui {
    private final ChoiceAction onConfirm;
    private final ChoiceAction onCancel;
    private final String confirmationMessage;
    private boolean closeOnCancel = true;
    private boolean closeOnConfirmation = true;

    public ConfirmationGui(String title, String confirmationMessage, ChoiceAction onConfirm, ChoiceAction onCancel) {
        super(27, title);
        this.confirmationMessage = confirmationMessage;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    public ConfirmationGui closeOnCancel(boolean closeOnCancel) {
        this.closeOnCancel = closeOnCancel;
        return this;
    }

    public ConfirmationGui closeOnConfirmation(boolean closeOnConfirmation) {
        this.closeOnConfirmation = closeOnConfirmation;
        return this;
    }

    @Override
    public void buildGui() {
        addBook(4);

        addCancelItem(onCancel, 9);
        addCancelItem(onCancel, 10);
        addCancelItem(onCancel, 18);
        addCancelItem(onCancel, 19);

        addConfirmItem(onConfirm, 16);
        addConfirmItem(onConfirm, 17);
        addConfirmItem(onConfirm, 25);
        addConfirmItem(onConfirm, 26);
    }

    private void addBook(int slot) {
        ItemStack itemStack = Items.createBook("&6Confirm", confirmationMessage);
        setItem(slot, itemStack, null);
    }

    private void addConfirmItem(ChoiceAction onConfirm, int slot) {
        ItemStack itemStack = Items.createGreenColoredGlass("Confirm","");
        setItem(slot, itemStack, new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                onConfirm.execute(player);
            }

            @Override
            public boolean shouldClose(Player player) {
                return closeOnConfirmation;
            }
        });
    }

    private void addCancelItem(ChoiceAction onCancel, int slot) {
        ItemStack itemStack = Items.createRedColoredGlass("Cancel", "");
        setItem(slot, itemStack, new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                    onCancel.execute(player);
            }

            @Override
            public boolean shouldClose(Player player) {
                return closeOnCancel;
            }
        });
    }
}
