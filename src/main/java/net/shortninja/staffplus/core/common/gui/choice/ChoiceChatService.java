package net.shortninja.staffplus.core.common.gui.choice;

import be.garagepoort.mcioc.IocBean;
import me.rayzr522.jsonmessage.JSONMessage;
import net.shortninja.staffplus.core.common.JavaUtils;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@IocBean
public class ChoiceChatService {

    private static Map<UUID, ChoiceAction> option1Actions = new HashMap<>();
    private static Map<UUID, ChoiceAction> option2actions = new HashMap<>();

    public void sendConfirmationChoiceMessage(Player player, String message, ChoiceAction choiceAction, ChoiceAction cancelAction) {
        sendChoiceMessage(player, message, "&aConfirm", choiceAction, "&cCancel", cancelAction);
    }

    public void sendChoiceMessage(Player player, String message, String option1Message, ChoiceAction option1Action, String option2Message, ChoiceAction option2Action) {
        UUID uuid = UUID.randomUUID();
        option1Actions.put(uuid, option1Action);
        option2actions.put(uuid, option2Action);

        JSONMessage jsonMessage = JavaUtils.buildChoiceMessage(message,
            option1Message,
            "staffplus:choice-action option1 " + uuid.toString(),
            option2Message,
            "staffplus:choice-action option2 " + uuid.toString());
        jsonMessage.send(player);
    }


    public void selectOption1(UUID uuid, Player player) {
        if(!option1Actions.containsKey(uuid)) {
            throw new BusinessException("&CNo action found");
        }
        ChoiceAction choiceAction = option1Actions.get(uuid);
        option1Actions.remove(uuid);
        option2actions.remove(uuid);

        choiceAction.execute(player);
    }

    public void selectOption2(UUID uuid, Player player) {
        if(!option2actions.containsKey(uuid)) {
            throw new BusinessException("&CNo action found");
        }
        ChoiceAction cancelAction = option2actions.get(uuid);
        option1Actions.remove(uuid);
        option2actions.remove(uuid);

        cancelAction.execute(player);
    }
}
