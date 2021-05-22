package net.shortninja.staffplus.core.common.cmd;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.authentication.AuthenticationService;
import net.shortninja.staffplus.core.common.cmd.arguments.ArgumentProcessor;
import net.shortninja.staffplus.core.common.cmd.arguments.ArgumentType;
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.domain.delayedactions.DelayArgumentExecutor;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static net.shortninja.staffplus.core.common.cmd.arguments.ArgumentType.DELAY;

@IocBean
public class CommandService {

    private final PermissionHandler permissionHandler;
    private final CommandPlayerRetriever commandPlayerRetriever;
    private final DelayArgumentExecutor delayArgumentExecutor;
    private final AuthenticationService authenticationService;
    private final ArgumentProcessor argumentProcessor;

    public CommandService(PermissionHandler permissionHandler, CommandPlayerRetriever commandPlayerRetriever, DelayArgumentExecutor delayArgumentExecutor, AuthenticationService authenticationService, ArgumentProcessor argumentProcessor) {
        this.permissionHandler = permissionHandler;
        this.commandPlayerRetriever = commandPlayerRetriever;
        this.delayArgumentExecutor = delayArgumentExecutor;
        this.authenticationService = authenticationService;
        this.argumentProcessor = argumentProcessor;
    }

    public void processArguments(CommandSender sender, String[] args, String playerName, List<ArgumentType> executionSppArguments, int minimumArguments) {
        List<String> sppArguments = getSppArguments(args, minimumArguments);
        argumentProcessor.parseArguments(sender, playerName, sppArguments, executionSppArguments);
    }

    private List<String> getSppArguments(String[] args, int minimumArguments) {
        return Arrays.asList(Arrays.copyOfRange(args, minimumArguments, args.length));
    }

    public void validateAuthentication(boolean authenticate, CommandSender sender) {
        if (authenticate && sender instanceof Player) {
            authenticationService.checkAuthentication((Player) sender);
        }
    }

    public void validatePermissions(CommandSender sender, Set<String> permissions) {
        permissionHandler.validateAny(sender, permissions);
    }

    public Optional<SppPlayer> retrievePlayer(String[] args, String playerName, PlayerRetrievalStrategy playerRetrievalStrategy, boolean isDelayable) {
        return commandPlayerRetriever.retrievePlayer(playerRetrievalStrategy, playerName, shouldDelay(args, isDelayable));
    }

    public boolean shouldDelay(String[] args, boolean isDelayable) {
        return isDelayable && Arrays.asList(args).contains(delayArgumentExecutor.getType().getPrefix());
    }

    public void delayCommand(CommandSender sender, String alias, String[] args, String playerName) {
        String delayedCommand = alias + " " + Stream.of(args)
            .filter(a -> !a.equals(delayArgumentExecutor.getType().getPrefix()))
            .collect(Collectors.joining(" "));

        delayArgumentExecutor.execute(sender, playerName, delayedCommand);
    }

    public List<String> getSppArgumentsSuggestions(CommandSender sender, String[] args, List<ArgumentType> preExecutionSppArguments, List<ArgumentType> postExecutionSppArguments, boolean isDelayable) {
        List<ArgumentType> validArguments = Stream.concat(preExecutionSppArguments.stream(), postExecutionSppArguments.stream())
            .collect(toList());

        List<String> suggestions = new ArrayList<>(argumentProcessor.getArgumentsSuggestions(sender, args[args.length - 1], validArguments));
        if (isDelayable) {
            suggestions.add(DELAY.getPrefix());
        }
        return suggestions;
    }
}
