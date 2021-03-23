package net.shortninja.staffplus.core.domain.staff.revive;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.cmd.AbstractCmd;
import net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy;
import net.shortninja.staffplus.core.common.cmd.arguments.ArgumentType;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.utils.MessageCoordinator;
import net.shortninja.staffplus.core.domain.player.SppPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.ONLINE;
import static net.shortninja.staffplus.core.common.cmd.arguments.ArgumentType.*;

public class ReviveCmd extends AbstractCmd {
    private final MessageCoordinator message = StaffPlus.get().iocContainer.get(MessageCoordinator.class);
    private final ReviveHandler reviveHandler = StaffPlus.get().iocContainer.get(ReviveHandler.class);

    public ReviveCmd(String name) {
        super(name, StaffPlus.get().iocContainer.get(Options.class).permissionRevive);
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player) {
        reviveHandler.restoreInventory(player.getPlayer());
        message.send(sender, messages.revivedStaff.replace("%target%", player.getPlayer().getName()), messages.prefixGeneral);
        return true;
    }

    @Override
    protected void validateExecution(SppPlayer player) {
        if (!reviveHandler.hasSavedInventory(player.getPlayer().getUniqueId())) {
            throw new BusinessException(messages.noFound);
        }
    }

    @Override
    protected List<ArgumentType> getPostExecutionSppArguments() {
        return Arrays.asList(TELEPORT, STRIP, HEALTH);
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return 0;
        }
        return 1;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return ONLINE;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        if (args.length == 0 && (sender instanceof Player)) {
            return Optional.of(sender.getName());
        }
        return Optional.of(args[0]);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            return new ArrayList<>(onlinePlayers);
        }

        return getSppArguments(sender, args);
    }
}