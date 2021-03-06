package net.shortninja.staffplus.core.domain.staff.vanish;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.config.Messages;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.permissions.PermissionHandler;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@IocBean
@IocMultiProvider(VanishStrategy.class)
public class PlayerVanishStrategy implements VanishStrategy {

    private final Messages messages;
    private final IProtocolService protocolService;
    private final PermissionHandler permission;
    private final SessionManagerImpl sessionManager;
    private final VanishConfiguration vanishConfiguration;

    public PlayerVanishStrategy(Messages messages, IProtocolService protocolService, PermissionHandler permission, SessionManagerImpl sessionManager, VanishConfiguration vanishConfiguration) {
        this.messages = messages;
        this.protocolService = protocolService;
        this.permission = permission;
        this.sessionManager = sessionManager;
        this.vanishConfiguration = vanishConfiguration;
    }

    @Override
    public void vanish(Player player) {
        String message = messages.playerVanish.replace("%status%", messages.enabled);
        if (StringUtils.isNotEmpty(message)) {
            this.messages.send(player, message, messages.prefixGeneral);
        }

        Bukkit.getOnlinePlayers().stream()
            .filter(p -> !permission.has(p, vanishConfiguration.permissionSeeVanished))
            .forEach(p -> p.hidePlayer(player));

        protocolService.getVersionProtocol().listVanish(player, false);
    }

    @Override
    public void unvanish(Player player) {
        Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));

        String message = messages.playerVanish.replace("%status%", messages.disabled);
        if (StringUtils.isNotEmpty(message)) {
            this.messages.send(player, message, messages.prefixGeneral);
        }
    }

    @Override
    public void updateVanish(Player player) {
        if (!permission.has(player, vanishConfiguration.permissionSeeVanished)) {
            sessionManager.getAll().stream()
                .filter(session -> session.getPlayer().isPresent() && session.getVanishType() == VanishType.PLAYER)
                .forEach(p -> {
                    player.hidePlayer(p.getPlayer().get());
                    protocolService.getVersionProtocol().listVanish(p.getPlayer().get(), false);
                });
        }
    }

    @Override
    public VanishType getVanishType() {
        return VanishType.PLAYER;
    }
}
