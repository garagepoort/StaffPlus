package net.shortninja.staffplus.staff.mode;

import net.shortninja.staffplus.common.JavaUtils;
import net.shortninja.staffplus.common.actions.ActionFilter;
import net.shortninja.staffplus.common.actions.ActionService;
import net.shortninja.staffplus.common.actions.ConfiguredAction;
import net.shortninja.staffplus.common.actions.PermissionActionFilter;
import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManagerImpl;
import net.shortninja.staffplus.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplus.staff.vanish.VanishServiceImpl;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplusplus.staffmode.EnterStaffModeEvent;
import net.shortninja.staffplusplus.staffmode.ExitStaffModeEvent;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.util.BukkitUtils.sendEvent;

public class StaffModeService {

    private final MessageCoordinator message;
    private final Messages messages;
    private final SessionManagerImpl sessionManager;
    private final VanishServiceImpl vanishServiceImpl;
    private final StaffModeItemsService staffModeItemsService;
    private final ActionService actionService;

    private final GeneralModeConfiguration modeConfiguration;
    private final ModeDataRepository modeDataRepository;
    private final Options options;
    private final PlayerManager playerManager;

    public StaffModeService(MessageCoordinator message,
                            Options options,
                            Messages messages,
                            SessionManagerImpl sessionManager,
                            VanishServiceImpl vanishServiceImpl,
                            StaffModeItemsService staffModeItemsService,
                            ActionService actionService, ModeDataRepository modeDataRepository, PlayerManager playerManager) {
        this.message = message;
        this.messages = messages;
        this.sessionManager = sessionManager;
        this.vanishServiceImpl = vanishServiceImpl;

        this.options = options;
        this.actionService = actionService;
        this.playerManager = playerManager;
        modeConfiguration = this.options.modeConfiguration;
        this.staffModeItemsService = staffModeItemsService;
        this.modeDataRepository = modeDataRepository;
    }

    public Set<UUID> getModeUsers() {
        return sessionManager.getAll().stream()
            .filter(p -> p.getPlayer().isPresent() && p.getPlayer().get().isOnline())
            .map(PlayerSession::getUuid).collect(Collectors.toSet());
    }

    public void addMode(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerSession session = sessionManager.get(uuid);

        Optional<ModeData> existingModeData = modeDataRepository.retrieveModeData(player.getUniqueId());
        if (!existingModeData.isPresent()) {
            ModeData modeData = new ModeData(player, session.getVanishType());
            modeDataRepository.saveModeData(modeData);
        }

        staffModeItemsService.setStaffModeItems(player);

        player.setAllowFlight(modeConfiguration.isModeFlight() && !modeConfiguration.isModeCreative());
        if (modeConfiguration.isModeCreative()) player.setGameMode(GameMode.CREATIVE);

        runModeCommands(player, true);
        vanishServiceImpl.addVanish(player, modeConfiguration.getModeVanish());
        session.setInStaffMode(true);
        sendEvent(new EnterStaffModeEvent(player.getName(), player.getUniqueId(), player.getLocation(), options.serverName));
        message.send(player, messages.modeStatus.replace("%status%", messages.enabled), messages.prefixGeneral);
    }

    public void removeMode(Player player) {
        PlayerSession session = sessionManager.get(player.getUniqueId());

        Optional<ModeData> existingModeData = modeDataRepository.retrieveModeData(player.getUniqueId());
        if (!existingModeData.isPresent()) {
            return;
        }

        ModeData modeData = existingModeData.get();
        if (modeConfiguration.isModeOriginalLocation()) {
            player.teleport(modeData.getPreviousLocation().setDirection(player.getLocation().getDirection()));
            message.send(player, messages.modeOriginalLocation, messages.prefixGeneral);
        }

        runModeCommands(player, false);
        JavaUtils.clearInventory(player);
        player.getInventory().setContents(modeData.getPlayerInventory());
        player.updateInventory();
        player.setExp(modeData.getXp());
        player.setAllowFlight(modeData.hasFlight());
        player.setGameMode(modeData.getGameMode());

        if (modeData.getVanishType() == VanishType.NONE) {
            vanishServiceImpl.removeVanish(player);
        } else {
            vanishServiceImpl.addVanish(player, modeData.getVanishType());
        }
        modeDataRepository.deleteModeData(player);

        session.setInStaffMode(false);
        sendEvent(new ExitStaffModeEvent(player.getName(), player.getUniqueId(), player.getLocation(), options.serverName));
        message.send(player, messages.modeStatus.replace("%status%", messages.disabled), messages.prefixGeneral);
    }

    private void runModeCommands(Player player, boolean isEnabled) {
        Optional<SppPlayer> target = playerManager.getOnOrOfflinePlayer(player.getUniqueId());
        if (target.isPresent()) {
            List<ActionFilter> actionFilters = Collections.singletonList(new PermissionActionFilter());
            List<ConfiguredAction> actions = isEnabled ? options.modeConfiguration.getModeEnableCommands() : options.modeConfiguration.getModeDisableCommands();
            actionService.executeActions(target.get(), actions, actionFilters);
        }
    }

    public static ItemStack[] getContents(Player p) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i <= 35; i++) {
            itemStacks.add(p.getInventory().getItem(i));
        }
        return itemStacks.toArray(new ItemStack[]{});
    }

}