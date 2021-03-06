package net.shortninja.staffplus.core.domain.player.gui.hub;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.PagedGui;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MinerGui extends PagedGui {
    private final Messages messages = StaffPlus.get().getIocContainer().get(Messages.class);

    public MinerGui(Player player, String title, int page, Supplier<AbstractGui> backGuiSupplier) {
        super(player, title, page, backGuiSupplier);
    }

    @Override
    protected MinerGui getNextUi(Player player, SppPlayer target, String title, int page) {
        return new MinerGui(player, title, page, previousGuiSupplier);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                Player p = Bukkit.getPlayerExact(item.getItemMeta().getDisplayName().substring(2));

                if (p != null) {
                    player.teleport(p);
                } else messages.send(player, messages.playerOffline, messages.prefixGeneral);
            }

            @Override
            public boolean shouldClose(Player player) {
                return true;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return StaffPlus.get().getIocContainer().get(PlayerManager.class).getOnlinePlayers().stream()
            .filter(p -> p.getLocation().getBlockY() < StaffPlus.get().getIocContainer().get(Options.class).staffItemsConfiguration.getGuiModeConfiguration().modeGuiMinerLevel)
            .map(this::minerItem)
            .collect(Collectors.toList());
    }

    private ItemStack minerItem(Player player) {
        Location location = player.getLocation();

        return Items.editor(Items.createSkull(player.getName())).setAmount(1)
                .setName("&b" + player.getName())
                .addLore("&7" + location.getWorld().getName() + " &8 | &7" + JavaUtils.serializeLocation(location))
                .build();
    }
}