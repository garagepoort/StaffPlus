package net.shortninja.staffplus.core.domain.staff.commandevidence;

import net.shortninja.staffplusplus.investigate.evidence.Evidence;

public class CommandEvidence implements Evidence {

    private String serverName;
    private String command;
    private int id;

    public CommandEvidence(String serverName, String command, int id) {
        this.serverName = serverName;
        this.command = command;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getEvidenceType() {
        return "COMMAND_EVIDENCE";
    }

    @Override
    public String getDescription() {
        return command;
    }

    public String getCommand() {
        return command;
    }
}
