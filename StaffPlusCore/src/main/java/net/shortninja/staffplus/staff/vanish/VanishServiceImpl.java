package net.shortninja.staffplus.staff.vanish;

import be.garagepoort.staffplusplus.craftbukkit.common.IProtocol;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManagerImpl;
import net.shortninja.staffplus.util.Message;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class VanishServiceImpl {
    private final IProtocol versionProtocol;
    private final PermissionHandler permission;
    private final MessageCoordinator message;
    private final Options options;
    private final Messages messages;
    private final SessionManagerImpl sessionManager;

    public VanishServiceImpl(IProtocol versionProtocol, PermissionHandler permission, MessageCoordinator message, Options options, Messages messages, SessionManagerImpl sessionManager) {
        this.versionProtocol = versionProtocol;
        this.permission = permission;
        this.message = message;
        this.options = options;
        this.messages = messages;
        this.sessionManager = sessionManager;

        if (options.vanishMessageEnabled) {
            Bukkit.getScheduler().runTaskTimer(StaffPlus.get(), () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerSession playerSession = sessionManager.get(p.getUniqueId());
                    if (playerSession.isVanished()) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Message.colorize(messages.vanishEnabled)));
                    }
                }
            }, 20L, 20L);
        }
    }

    public void addVanish(Player player, VanishType vanishType) {
        PlayerSession session = sessionManager.get(player.getUniqueId());
        VanishType userVanishType = session.getVanishType();

        if (userVanishType == vanishType) {
            return;
        } else if (userVanishType != VanishType.NONE) {
            unapplyVanish(player, userVanishType, true);
        }

        applyVanish(player, vanishType, true);
        session.setVanishType(vanishType);
    }

    public void removeVanish(Player player) {
        PlayerSession session = sessionManager.get(player.getUniqueId());
        VanishType vanishType = session.getVanishType();

        if (vanishType == VanishType.NONE) {
            return;
        }

        unapplyVanish(player, vanishType, true);
        session.setVanishType(VanishType.NONE);
    }

    public boolean isVanished(Player player) {
        PlayerSession user = sessionManager.get(player.getUniqueId());
        return user.getVanishType() != VanishType.NONE;
    }

    public void updateVanish() {
        for (PlayerSession user : sessionManager.getAll()) {
            Optional<Player> player = user.getPlayer();

            if (player.isPresent() && user.getVanishType() == VanishType.TOTAL) {
                applyVanish(player.get(), user.getVanishType(), false);
            }
        }
    }

    private void applyVanish(Player player, VanishType vanishType, boolean shouldMessage) {
        String message = "";

        switch (vanishType) {
            case TOTAL:
                Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !options.staffView || !permission.has(p, options.permissionMode))
                    .forEach(p -> p.hidePlayer(player));
                message = messages.totalVanish.replace("%status%", messages.enabled);
                break;
            case LIST:
                if (options.vanishTabList) {
                    versionProtocol.listVanish(player, true);
                }

                message = messages.listVanish.replace("%status%", messages.enabled);
                break;
        }

        if (shouldMessage && !message.isEmpty()) {
            this.message.send(player, message, messages.prefixGeneral);
        }
    }

    private void unapplyVanish(Player player, VanishType vanishType, boolean shouldMessage) {

        String message = "";

        switch (vanishType) {
            case TOTAL:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showPlayer(player);
                }
                message = messages.totalVanish.replace("%status%", messages.disabled);
                break;
            case LIST:
                versionProtocol.listVanish(player, false);
                message = messages.listVanish.replace("%status%", messages.disabled);
                break;
            default:
                break;
        }

        if (shouldMessage && !message.isEmpty()) {
            this.message.send(player, message, messages.prefixGeneral);
        }
    }
}
