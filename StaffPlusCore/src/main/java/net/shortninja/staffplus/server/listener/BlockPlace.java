package net.shortninja.staffplus.server.listener;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManagerImpl;
import net.shortninja.staffplus.staff.freeze.FreezeHandler;
import net.shortninja.staffplus.staff.mode.StaffModeService;
import net.shortninja.staffplus.staff.tracing.TraceService;
import net.shortninja.staffplus.staff.tracing.TraceType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class BlockPlace implements Listener {
    private final Options options = IocContainer.getOptions();
    private final FreezeHandler freezeHandler = IocContainer.getFreezeHandler();
    private final StaffModeService staffModeService = IocContainer.getModeCoordinator();
    private final TraceService traceService = IocContainer.getTraceService();
    private final SessionManagerImpl sessionManager = IocContainer.getSessionManager();

    public BlockPlace() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerSession session = sessionManager.get(uuid);

        if ((options.modeConfiguration.isModeBlockManipulation() || !session.isInStaffMode()) && !freezeHandler.isFrozen(uuid)) {
            traceService.sendTraceMessage(TraceType.BLOCK_PLACE, uuid, "Blocked [" + event.getBlock().getType() + "] placed");
            return;
        }

        event.setCancelled(true);
    }
}