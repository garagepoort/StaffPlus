package net.shortninja.staffplus.core.domain.staff.mute.cmd;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.Command;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.mute.MuteService;
import net.shortninja.staffplus.core.domain.staff.mute.config.MuteConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.BOTH;

@Command(
    command = "commands:mute",
    permissions = "permissions:mute",
    description = "Permanent mute a player",
    usage = "[player] [reason]",
    playerRetrievalStrategy = BOTH
)
@IocBean(conditionalOnProperty = "mute-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class MuteCmd extends AbstractCmd {

    private final PermissionHandler permissionHandler;
    private final MuteService muteService;
    private final SessionManagerImpl sessionManager;
    private final PlayerManager playerManager;
    private final MuteConfiguration muteConfiguration;

    public MuteCmd(PermissionHandler permissionHandler, Messages messages, MuteService muteService, SessionManagerImpl sessionManager, CommandService commandService, PlayerManager playerManager, MuteConfiguration muteConfiguration) {
        super(messages, permissionHandler, commandService);
        this.permissionHandler = permissionHandler;
        this.muteService = muteService;
        this.sessionManager = sessionManager;
        this.playerManager = playerManager;
        this.muteConfiguration = muteConfiguration;
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player, Map<String, String> optionalParameters) {
        String reason = JavaUtils.compileWords(args, 1);

        muteService.permMute(sender, player, reason);
        if (player.isOnline()) {
            sessionManager.get(player.getId()).setMuted(true);
        }
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 2;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.ofNullable(args[0]);
    }

    @Override
    protected boolean canBypass(Player player) {
        return permissionHandler.has(player, muteConfiguration.permissionMuteByPass);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return playerManager.getAllPlayerNames().stream()
                .filter(s -> args[0].isEmpty() || s.contains(args[0]))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
