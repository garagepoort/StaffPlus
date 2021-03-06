package net.shortninja.staffplus.core.domain.staff.investigate.gui.investigation;

import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.investigate.Investigation;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationService;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ResumeInvestigationAction implements IAction {

    private final InvestigationService investigationService;
    private final Investigation investigation;
    private final PlayerManager playerManager;

    public ResumeInvestigationAction(InvestigationService investigationService, Investigation investigation, PlayerManager playerManager) {
        this.investigationService = investigationService;
        this.investigation = investigation;
        this.playerManager = playerManager;
    }

    @Override
    public void click(Player player, ItemStack item, int slot, ClickType clickType) {
        if (investigation.getInvestigatedUuid().isPresent()) {
            SppPlayer investigated = playerManager.getOnOrOfflinePlayer(investigation.getInvestigatedUuid().get())
                .orElseThrow(() -> new BusinessException("Can't resume investigation. Player not found."));
            investigationService.resumeInvestigation(player, investigated);
        } else {
            investigationService.resumeInvestigation(player, investigation.getId());
        }
    }

    @Override
    public boolean shouldClose(Player player) {
        return true;
    }
}
