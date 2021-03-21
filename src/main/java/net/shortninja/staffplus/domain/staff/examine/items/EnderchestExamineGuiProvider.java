package net.shortninja.staffplus.domain.staff.examine.items;

import net.shortninja.staffplus.application.IocContainer;
import net.shortninja.staffplus.common.Items;
import net.shortninja.staffplus.common.config.Messages;
import net.shortninja.staffplus.common.config.Options;
import net.shortninja.staffplus.common.gui.IAction;
import net.shortninja.staffplus.common.utils.PermissionHandler;
import net.shortninja.staffplus.domain.player.SppPlayer;
import net.shortninja.staffplus.domain.staff.examine.gui.ExamineGui;
import net.shortninja.staffplus.domain.staff.examine.gui.ExamineGuiItemProvider;
import net.shortninja.staffplus.domain.staff.mode.config.modeitems.examine.ExamineModeConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnderchestExamineGuiProvider implements ExamineGuiItemProvider {

    private final Messages messages;
    private final ExamineModeConfiguration examineModeConfiguration;
    private final Options options;
    private PermissionHandler permissionHandler;

    public EnderchestExamineGuiProvider(Messages messages, Options options, PermissionHandler permissionHandler) {
        this.messages = messages;
        this.options = options;
        this.permissionHandler = permissionHandler;
        examineModeConfiguration = this.options.modeConfiguration.getExamineModeConfiguration();
    }

    @Override
    public ItemStack getItem(SppPlayer player) {
        return locationItem();
    }

    @Override
    public IAction getClickAction(ExamineGui examineGui, Player staff, SppPlayer target) {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                IocContainer.getEnderchestService().openEnderChest(staff, target);
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public boolean enabled(Player staff, SppPlayer player) {
        if (player.isOnline()) {
            return permissionHandler.has(staff, options.enderchestsConfiguration.getPermissionViewOnline());
        } else {
            return permissionHandler.has(staff, options.enderchestsConfiguration.getPermissionViewOffline());
        }
    }

    @Override
    public int getSlot() {
        return 24;
    }

    private ItemStack locationItem() {
        ItemStack item = Items.builder()
            .setMaterial(Material.ENDER_CHEST).setAmount(1)
            .setName("&bPlayer's Enderchest")
            .addLore("View player's Enderchest")
            .build();

        return item;
    }
}
