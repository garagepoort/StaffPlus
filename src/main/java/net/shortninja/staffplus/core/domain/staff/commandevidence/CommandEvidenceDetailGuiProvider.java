package net.shortninja.staffplus.core.domain.staff.commandevidence;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplusplus.investigate.evidence.EvidenceGuiClick;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@IocBean
@IocMultiProvider(EvidenceGuiClick.class)
public class CommandEvidenceDetailGuiProvider implements EvidenceGuiClick {

    private final CommandEvidenceService commandEvidenceService;

    public CommandEvidenceDetailGuiProvider(CommandEvidenceService commandEvidenceService) {
        this.commandEvidenceService = commandEvidenceService;
    }

    @Override
    public void onClick(Player player, SppPlayer target, int id, Runnable back) {
        CommandEvidence commandEvidence = commandEvidenceService.getById(id);
        Bukkit.dispatchCommand(player, commandEvidence.getCommand());
    }

    @Override
    public String getType() {
        return "COMMAND_EVIDENCE";
    }
}
