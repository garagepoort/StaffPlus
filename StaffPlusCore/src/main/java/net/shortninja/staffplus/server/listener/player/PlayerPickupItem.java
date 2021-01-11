package net.shortninja.staffplus.server.listener.player;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.staff.mode.ModeCoordinator;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.tracing.TraceService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import static net.shortninja.staffplus.staff.tracing.TraceType.PICKUP_ITEM;

public class PlayerPickupItem implements Listener {
    private final Options options = IocContainer.getOptions();
    private final ModeCoordinator modeCoordinator = IocContainer.getModeCoordinator();
    private final TraceService traceService = IocContainer.getTraceService();

    public PlayerPickupItem() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (options.modeConfiguration.isModeItemChange() || !modeCoordinator.isInMode(player.getUniqueId())) {
                traceService.sendTraceMessage(PICKUP_ITEM, player.getUniqueId(), String.format("Picked up item [%s]", event.getItem().getItemStack().getType()));
                return;
            }
            event.setCancelled(true);
        }

    }
}