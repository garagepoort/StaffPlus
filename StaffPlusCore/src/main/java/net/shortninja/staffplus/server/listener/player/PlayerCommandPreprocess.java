package net.shortninja.staffplus.server.listener.player;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.command.BaseCmd;
import net.shortninja.staffplus.server.command.CmdHandler;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.SessionManagerImpl;
import net.shortninja.staffplus.staff.freeze.FreezeHandler;
import net.shortninja.staffplus.staff.mode.StaffModeService;
import net.shortninja.staffplus.staff.tracing.TraceService;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.staff.tracing.TraceType.COMMANDS;


public class PlayerCommandPreprocess implements Listener {
    private final PermissionHandler permission = IocContainer.getPermissionHandler();
    private final MessageCoordinator message = IocContainer.getMessage();
    private final Options options = IocContainer.getOptions();
    private final Messages messages = IocContainer.getMessages();
    private final FreezeHandler freezeHandler = IocContainer.getFreezeHandler();
    private final CmdHandler cmdHandler = StaffPlus.get().cmdHandler;
    private final StaffModeService staffModeService = IocContainer.getModeCoordinator();
    private final TraceService traceService = IocContainer.getTraceService();
    private final SessionManagerImpl sessionManager = IocContainer.getSessionManager();

    public PlayerCommandPreprocess() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String command = event.getMessage().toLowerCase();
        traceService.sendTraceMessage(COMMANDS, uuid, "Player invoked command: [" + command + "]");

        if (command.startsWith("/help staffplus") || command.startsWith("/help staff+")) {
            sendHelp(player);
            event.setCancelled(true);
            return;
        }

        if (options.blockedCommands.contains(command) && permission.hasOnly(player, options.permissionBlock)) {
            message.send(player, messages.commandBlocked, messages.prefixGeneral);
            event.setCancelled(true);
        } else if (sessionManager.get(uuid).isInStaffMode() && options.blockedModeCommands.contains(command)) {
            message.send(player, messages.modeCommandBlocked, messages.prefixGeneral);
            event.setCancelled(true);
        } else if (freezeHandler.isFrozen(uuid) && (!options.modeConfiguration.getFreezeModeConfiguration().isModeFreezeChat() && !command.startsWith("/" + options.commandLogin))) {
            message.send(player, messages.chatPrevented, messages.prefixGeneral);
            event.setCancelled(true);
        }
    }


    private void sendHelp(Player player) {
        int count = 0;

        message.send(player, "&7" + message.LONG_LINE, "");

        List<BaseCmd> sortedCommands = Arrays.stream(cmdHandler.commands)
            .sorted(Comparator.comparing(o -> o.getCommand().getName()))
            .collect(Collectors.toList());

        for (BaseCmd baseCmd : sortedCommands) {
            if (baseCmd.getPermissions().isEmpty()) {
                message.send(player, "&b/" + baseCmd.getCommand().getName() + " &7: " + baseCmd.getDescription().toLowerCase(), "");
                count++;
            } else {
                for (String permission : baseCmd.getPermissions()) {
                    if (this.permission.has(player, permission)) {
                        message.send(player, "&b/" + baseCmd.getCommand().getName() + " &7: " + baseCmd.getDescription().toLowerCase(), "");
                        count++;
                        break;
                    }
                }
            }
        }

        if (count == 0) {
            message.send(player, messages.noPermission, messages.prefixGeneral);
        }

        message.send(player, "&7" + message.LONG_LINE, "");
    }
}