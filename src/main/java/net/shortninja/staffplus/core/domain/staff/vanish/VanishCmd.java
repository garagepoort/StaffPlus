package net.shortninja.staffplus.core.domain.staff.vanish;

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
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.session.PlayerSession;
import net.shortninja.staffplus.core.session.SessionManagerImpl;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static net.shortninja.staffplus.core.common.cmd.PlayerRetrievalStrategy.ONLINE;

@IocBean(conditionalOnProperty = "vanish-module.enabled=true")
@IocMultiProvider(SppCommand.class)
public class VanishCmd extends AbstractCmd {
    private final SessionManagerImpl sessionManager;
    private final VanishServiceImpl vanishServiceImpl;
    private final PermissionHandler permissionHandler;

    public VanishCmd(Messages messages, Options options, SessionManagerImpl sessionManager, VanishServiceImpl vanishServiceImpl, CommandService commandService, PermissionHandler permissionHandler) {
        super(options.commandVanish, messages, options, commandService);
        this.sessionManager = sessionManager;
        this.vanishServiceImpl = vanishServiceImpl;
        this.permissionHandler = permissionHandler;
        setPermission(options.permissionVanishCommand);
        setDescription("Enables or disables the type of vanish for the player.");
        setUsage("[total | list] {player} {enable | disable}");
    }


    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer targetPlayer) {
        if(!(sender instanceof Player)) {
            throw new BusinessException(messages.onlyPlayers);
        }

        if (args.length >= 3 && permissionHandler.isOp(sender)) {
            String option = args[2];

            if (option.equalsIgnoreCase("enable")) {
                handleVanishArgument(sender, args[0], targetPlayer.getPlayer(), false);
            } else {
                vanishServiceImpl.removeVanish(targetPlayer.getPlayer());
            }

            sessionManager.saveSession(targetPlayer.getPlayer());
            return true;s
        }

        if (args.length == 2 && permissionHandler.isOp(sender)) {
            handleVanishArgument(sender, args[0], targetPlayer.getPlayer(), false);
            sessionManager.saveSession(targetPlayer.getPlayer());
            return true;
        }

        if (args.length == 1) {
            handleVanishArgument(sender, args[0], (Player) sender, true);
            sessionManager.saveSession(targetPlayer.getPlayer());
            return true;
        }

        sendHelp(sender);
        sessionManager.saveSession(targetPlayer.getPlayer());
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            return 1;
        }
        return 2;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return ONLINE;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return Optional.of(args[1]);
        }
        if (sender instanceof Player) {
            return Optional.of(sender.getName());
        }
        return Optional.empty();
    }

    private void handleVanishArgument(CommandSender sender, String argument, Player player, boolean shouldCheckPermission) {
        boolean isValid = JavaUtils.isValidEnum(VanishType.class, argument.toUpperCase());
        VanishType vanishType = VanishType.NONE;
        PlayerSession user = sessionManager.get(player.getUniqueId());

        if (!isValid) {
            sendHelp(sender);
            return;
        } else vanishType = VanishType.valueOf(argument.toUpperCase());

        switch (vanishType) {
            case TOTAL:
                if (permissionHandler.has(player, options.permissionVanishTotal) || !shouldCheckPermission) {
                    if (user.getVanishType() != VanishType.TOTAL) {
                        vanishServiceImpl.addVanish(player, vanishType);
                    } else vanishServiceImpl.removeVanish(player);
                } else messages.send(player, messages.noPermission, messages.prefixGeneral);
                break;
            case LIST:
                if (permissionHandler.has(player, options.permissionVanishList) || !shouldCheckPermission) {
                    if (user.getVanishType() != VanishType.LIST) {
                        vanishServiceImpl.addVanish(player, vanishType);
                    } else vanishServiceImpl.removeVanish(player);
                } else messages.send(player, messages.noPermission, messages.prefixGeneral);
                break;
            case NONE:
                if (permissionHandler.has(player, options.permissionVanishList) || permissionHandler.has(player, options.permissionVanishTotal) || !shouldCheckPermission) {
                    vanishServiceImpl.removeVanish(player);
                } else messages.send(player, messages.noPermission, messages.prefixGeneral);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        messages.send(sender, "&7" + messages.LONG_LINE, "");
        messages.send(sender, "&b/" + getName() + " &7" + getUsage(), messages.prefixGeneral);
        messages.send(sender, "&7" + messages.LONG_LINE, "");
    }
}