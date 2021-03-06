package net.shortninja.staffplus.core.domain.staff.mute.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.PagedGui;
import net.shortninja.staffplus.core.domain.staff.mute.Mute;
import net.shortninja.staffplus.core.domain.staff.mute.MuteService;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MutedPlayersGui extends PagedGui {

    private final MutedPlayerItemBuilder mutedPlayerItemBuilder = StaffPlus.get().getIocContainer().get(MutedPlayerItemBuilder.class);

    public MutedPlayersGui(Player player, String title, int page, Supplier<AbstractGui> backGuiSupplier) {
        super(player, title, page, backGuiSupplier);
    }

    @Override
    protected MutedPlayersGui getNextUi(Player player, SppPlayer target, String title, int page) {
        return new MutedPlayersGui(player, title, page, this.previousGuiSupplier);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                int muteId = Integer.parseInt(StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().getNbtString(item));
                Mute mute = StaffPlus.get().getIocContainer().get(MuteService.class).getById(muteId);
                new ManageMutedPlayerGui("Player: " + mute.getTargetName(), mute, () -> new MutedPlayersGui(player, getTitle(), getCurrentPage(), getPreviousGuiSupplier())).show(player);
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return StaffPlus.get().getIocContainer().get(MuteService.class).getAllPaged(offset, amount).stream()
            .map(mutedPlayerItemBuilder::build)
            .collect(Collectors.toList());
    }
}