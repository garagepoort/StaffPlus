package net.shortninja.staffplus.domain.staff.mode.handler;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.config.Messages;
import net.shortninja.staffplus.common.utils.MessageCoordinator;
import net.shortninja.staffplus.domain.confirmation.ConfirmationConfig;
import net.shortninja.staffplus.domain.confirmation.ConfirmationService;
import net.shortninja.staffplus.domain.staff.mode.item.CustomModuleConfiguration;
import org.bukkit.Bukkit;

import java.util.Map;

public class ConfirmationCustomModulePreprocessor implements CustomModulePreProcessor {

    private final Messages messages;
    private final ConfirmationService confirmationService;
    private final MessageCoordinator message;

    public ConfirmationCustomModulePreprocessor(Messages messages, ConfirmationService confirmationService, MessageCoordinator message) {
        this.messages = messages;
        this.confirmationService = confirmationService;
        this.message = message;
    }

    @Override
    public CustomModuleExecutor process(CustomModuleExecutor action, CustomModuleConfiguration customModuleConfiguration, Map<String, String> placeholders) {
        if (!customModuleConfiguration.getConfirmationConfig().isPresent()) {
            return action;
        }

        return (player, pl) -> Bukkit.getScheduler().runTaskLater(StaffPlus.get(), () -> {
            ConfirmationConfig confirmationConfig = customModuleConfiguration.getConfirmationConfig().get();
            confirmationService.showConfirmation(player, confirmationConfig, pl,
                player1 -> action.execute(player1, pl),
                p -> message.send(p, "You have cancelled the action", messages.prefixGeneral));
        }, 1);
    }

}
