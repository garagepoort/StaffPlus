package net.shortninja.staffplus.core.domain.staff.commandevidence;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.CommandService;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.cmd.SppCommand;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.domain.staff.investigate.Investigation;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationEvidenceService;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationService;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@IocBean
@IocMultiProvider(SppCommand.class)
public class LinkCommandEvidenceCmd extends AbstractCmd {

    private final CommandEvidenceService commandEvidenceService;
    private final InvestigationService investigationService;
    private final InvestigationEvidenceService investigationEvidenceService;

    public LinkCommandEvidenceCmd(Messages messages, Options options, CommandService commandService, CommandEvidenceService commandEvidenceService, InvestigationService investigationService, InvestigationEvidenceService investigationEvidenceService) {
        super("link-command-evidence", messages, options, commandService);
        this.commandEvidenceService = commandEvidenceService;
        this.investigationService = investigationService;
        this.investigationEvidenceService = investigationEvidenceService;
        setDescription("Link a command as evidence to an investigation");
        setUsage("[command]");
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player) {
        validateIsPlayer(sender);
        Investigation investigation = investigationService.findCurrentActiveInvestigation((Player) sender).orElseThrow(() -> new BusinessException("Cannot link command. No current investigation running"));
        CommandEvidence commandEvidence = commandEvidenceService.linkEvidence((Player) sender, JavaUtils.compileWords(args, 0));
        investigationEvidenceService.linkEvidence((Player) sender, investigation, commandEvidence);
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 1;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return PlayerRetrievalStrategy.NONE;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }
}
