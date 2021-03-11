package net.shortninja.staffplus.staff.warn.appeals.gui.actions;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManagerImpl;
import net.shortninja.staffplus.staff.warn.appeals.AppealService;
import net.shortninja.staffplus.common.IAction;
import net.shortninja.staffplus.util.MessageCoordinator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ApproveAppealAction implements IAction {
    private static final String CANCEL = "cancel";
    private final Messages messages = IocContainer.getMessages();
    private final MessageCoordinator messageCoordinator = IocContainer.getMessage();
    private final SessionManagerImpl sessionManager = IocContainer.getSessionManager();
    private final AppealService appealService = IocContainer.getAppealService();
    private final Options options = IocContainer.getOptions();

    private final int appealId;

    public ApproveAppealAction(int appealId) {
        this.appealId = appealId;
    }

    @Override
    public void click(Player player, ItemStack item, int slot) {
        if (options.appealConfiguration.isResolveReasonEnabled()) {
            messageCoordinator.send(player, "&1===================================================", messages.prefixWarnings);
            messageCoordinator.send(player, "&6       You have chosen to approve this appeal", messages.prefixWarnings);
            messageCoordinator.send(player, "&6Type your closing reason in chat to approve the appeal", messages.prefixWarnings);
            messageCoordinator.send(player, "&6      Type \"cancel\" to cancel approving the appeal ", messages.prefixWarnings);
            messageCoordinator.send(player, "&1===================================================", messages.prefixWarnings);
            PlayerSession playerSession = sessionManager.get(player.getUniqueId());
            playerSession.setChatAction((player1, message) -> {
                if (message.equalsIgnoreCase(CANCEL)) {
                    messageCoordinator.send(player, "&CYou have cancelled approving this appeal", messages.prefixWarnings);
                    return;
                }
                appealService.approveAppeal(player, appealId, message);
            });
        } else {
            appealService.approveAppeal(player, appealId);
        }
    }

    @Override
    public boolean shouldClose(Player player) {
        return true;
    }
}
