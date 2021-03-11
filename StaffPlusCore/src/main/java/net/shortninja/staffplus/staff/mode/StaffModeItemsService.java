package net.shortninja.staffplus.staff.mode;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManagerImpl;
import net.shortninja.staffplus.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplus.staff.mode.config.ModeItemConfiguration;
import net.shortninja.staffplus.staff.mode.config.gui.GuiConfiguration;
import net.shortninja.staffplus.staff.mode.config.modeitems.vanish.VanishModeConfiguration;
import net.shortninja.staffplus.util.Permission;
import net.shortninja.staffplus.common.JavaUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaffModeItemsService {

    private static final Logger logger = StaffPlus.get().getLogger();
    private final List<ModeItemConfiguration> MODE_ITEMS;

    private final Permission permissionHandler;
    private final Options options;
    private final SessionManagerImpl sessionManager;
    private final GeneralModeConfiguration modeConfiguration;

    public StaffModeItemsService(Permission permissionHandler, Options options, SessionManagerImpl sessionManager) {
        this.permissionHandler = permissionHandler;
        this.options = options;
        modeConfiguration = options.modeConfiguration;
        this.sessionManager = sessionManager;

        MODE_ITEMS = Arrays.asList(
            options.modeConfiguration.getCompassModeConfiguration(),
            options.modeConfiguration.getRandomTeleportModeConfiguration(),
            options.modeConfiguration.getVanishModeConfiguration(),
            options.modeConfiguration.getGuiModeConfiguration(),
            options.modeConfiguration.getCounterModeConfiguration(),
            options.modeConfiguration.getFreezeModeConfiguration(),
            options.modeConfiguration.getCpsModeConfiguration(),
            options.modeConfiguration.getExamineModeConfiguration(),
            options.modeConfiguration.getFollowModeConfiguration()
        );
    }

    public void setStaffModeItems(Player player) {
        PlayerSession session = sessionManager.get(player.getUniqueId());
        JavaUtils.clearInventory(player);

        if (permissionHandler.isOp(player) || customGuiConfigurations().isEmpty()) {
            getAllModeItems().forEach(modeItem -> addModeItem(player, session, modeItem, modeItem.getSlot()));
            return;
        }

        Optional<GuiConfiguration> applicableGui = customGuiConfigurations().stream()
            .filter(gui -> permissionHandler.has(player, gui.getPermission()))
            .findFirst();

        if (!applicableGui.isPresent()) {
            logger.warning("No gui configuration found for player " + player.getName() + ". Make sure this player has one of the staff mode rank permissions");
            return;
        }

        applicableGui.get().getItemSlots().forEach((moduleName, slot) -> {
            Optional<ModeItemConfiguration> module = getModule(moduleName);
            if (!module.isPresent()) {
                logger.warning("No module found with name [" + moduleName + "]. Skipping...");
            } else {
                addModeItem(player, session, module.get(), slot);
            }
        });
    }

    private List<GuiConfiguration> customGuiConfigurations() {
        return modeConfiguration.getStaffGuiConfigurations();
    }

    private void addModeItem(Player player, PlayerSession session, ModeItemConfiguration modeItem, int slot) {
        if (!modeItem.isEnabled()) {
            return;
        }

        if (modeItem instanceof VanishModeConfiguration) {
            player.getInventory().setItem(slot, ((VanishModeConfiguration) modeItem).getModeVanishItem(session, modeConfiguration.getModeVanish()));
        } else {
            player.getInventory().setItem(slot, modeItem.getItem());
        }
    }

    private Optional<ModeItemConfiguration> getModule(String name) {
        return getAllModeItems().stream().filter(m -> m.getIdentifier().equals(name)).findFirst();
    }

    private List<ModeItemConfiguration> getAllModeItems() {
        return Stream.of(MODE_ITEMS, options.customModuleConfigurations)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public static ItemStack[] getContents(Player p) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i <= 35; i++) {
            itemStacks.add(p.getInventory().getItem(i));
        }
        return itemStacks.toArray(new ItemStack[]{});
    }
}
