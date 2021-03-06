package net.shortninja.staffplus.core.domain.staff.mode;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.application.session.PlayerSession;
import net.shortninja.staffplus.core.application.session.SessionManagerImpl;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplus.core.domain.staff.mode.config.ModeItemConfiguration;
import net.shortninja.staffplus.core.domain.staff.mode.config.modeitems.vanish.VanishModeConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@IocBean
public class StaffModeItemsService {

    private static final Logger logger = StaffPlus.get().getLogger();
    private final List<ModeItemConfiguration> MODE_ITEMS;

    private final Options options;
    private final SessionManagerImpl sessionManager;

    public StaffModeItemsService(Options options, SessionManagerImpl sessionManager) {
        this.options = options;
        this.sessionManager = sessionManager;

        MODE_ITEMS = Arrays.asList(
            options.staffItemsConfiguration.getCompassModeConfiguration(),
            options.staffItemsConfiguration.getRandomTeleportModeConfiguration(),
            options.staffItemsConfiguration.getVanishModeConfiguration(),
            options.staffItemsConfiguration.getGuiModeConfiguration(),
            options.staffItemsConfiguration.getCounterModeConfiguration(),
            options.staffItemsConfiguration.getFreezeModeConfiguration(),
            options.staffItemsConfiguration.getCpsModeConfiguration(),
            options.staffItemsConfiguration.getExamineModeConfiguration(),
            options.staffItemsConfiguration.getFollowModeConfiguration()
        );
    }

    public void setStaffModeItems(Player player, GeneralModeConfiguration modeConfiguration) {
        PlayerSession session = sessionManager.get(player.getUniqueId());
        JavaUtils.clearInventory(player);

        modeConfiguration.getItemSlots().forEach((moduleName, slot) -> {
            Optional<ModeItemConfiguration> module = getModule(moduleName);
            if (!module.isPresent()) {
                logger.warning("No module found with name [" + moduleName + "]. Skipping...");
            } else {
                addModeItem(player, session, module.get(), slot, modeConfiguration);
            }
        });
    }

    private void addModeItem(Player player, PlayerSession session, ModeItemConfiguration modeItem, int slot, GeneralModeConfiguration modeConfiguration) {
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
}
