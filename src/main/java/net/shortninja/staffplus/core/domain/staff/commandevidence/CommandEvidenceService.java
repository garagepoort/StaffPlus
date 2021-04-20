package net.shortninja.staffplus.core.domain.staff.commandevidence;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.common.config.Options;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@IocBean
public class CommandEvidenceService {

    private final Options options;


    private List<CommandEvidence> commandEvidences = new ArrayList<>();

    public CommandEvidenceService(Options options) {
        this.options = options;
    }

    public CommandEvidence linkEvidence(Player player, String command) {
        CommandEvidence commandEvidence = new CommandEvidence(options.serverName, command, 1);
        commandEvidences.add(commandEvidence);
        return commandEvidence;
    }

    public CommandEvidence getById(int id) {
        return commandEvidences.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}
