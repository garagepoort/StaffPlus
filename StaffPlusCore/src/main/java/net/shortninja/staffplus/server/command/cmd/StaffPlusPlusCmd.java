package net.shortninja.staffplus.server.command.cmd;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.common.BusinessException;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.server.command.arguments.ArgumentProcessor;
import net.shortninja.staffplus.server.command.arguments.DelayArgumentExecutor;
import net.shortninja.staffplus.server.data.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.shortninja.staffplus.common.CommandUtil.executeCommand;

public abstract class StaffPlusPlusCmd extends BukkitCommand {
    private static DelayArgumentExecutor delayArgumentExecutor = new DelayArgumentExecutor();

    private UserManager userManager = IocContainer.getUserManager();
    private Messages messages = IocContainer.getMessages();
    protected ArgumentProcessor argumentProcessor = ArgumentProcessor.getInstance();

    protected StaffPlusPlusCmd(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        return executeCommand(sender, () -> {
            if (args.length < getMinimumArguments(args)) {
                throw new BusinessException(messages.invalidArguments.replace("%usage%", getName() + " &7" + getUsage()), messages.prefixGeneral);
            }

            String playerName = getPlayerName(args);
            if(!userManager.playerExists(playerName)) {
                throw new BusinessException(messages.playerNotRegistered, messages.prefixGeneral);
            }

            if(shouldDelay(args)) {
                delayCommand(sender, alias, args, playerName);
                return true;
            }

            executeCmd(sender, alias, args);
            return true;
        });
    }

    protected abstract String getPlayerName(String[] args);

    protected abstract int getMinimumArguments(String[] args);

    protected abstract boolean isDelayable();

    protected abstract boolean executeCmd(CommandSender sender, String alias, String[] args);


    private String getDelayedCommand(String alias, String[] args) {
        return alias + " " + Stream.of(args).filter(a -> !a.equals(delayArgumentExecutor.getType().getPrefix())).collect(Collectors.joining(" "));
    }

    private boolean shouldDelay(String[] args) {
        return Arrays.asList(args).contains(delayArgumentExecutor.getType().getPrefix()) && isDelayable();
    }

    private void delayCommand(CommandSender sender, String alias, String[] args, String playerName) {
        delayArgumentExecutor.execute(sender, playerName, getDelayedCommand(alias, args));
    }
}
