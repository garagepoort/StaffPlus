package net.shortninja.staffplus.core.domain.actions;

import net.shortninja.staffplus.core.common.config.ConfigurationUtil;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionConfigLoader {

    private ActionConfigLoader() {
    }

    public static List<ConfiguredAction> loadActions(List<LinkedHashMap<String, Object>> list) {
        return list.stream().map(map -> {
            if (!map.containsKey("command")) {
                throw new ConfigurationException("Invalid actions configuration. Actions should define a command");
            }
            String command = (String) map.get("command");
            String rollbackCommand = (String) map.get("rollback-command");
            String target = map.containsKey("target") ? (String) map.get("target") : "player";
            ActionRunStrategy runStrategy = map.containsKey("run-strategy") ? ActionRunStrategy.valueOf((String) map.get("run-strategy")) : ActionRunStrategy.ALWAYS;
            ActionRunStrategy rollbackRunStrategy = map.containsKey("rollback-run-strategy") ? ActionRunStrategy.valueOf((String) map.get("rollback-run-strategy")) : runStrategy;

            String filtersString = map.containsKey("filters") ? (String) map.get("filters") : null;
            Map<String, String> filterMap = ConfigurationUtil.loadFilters(filtersString);

            return new ConfiguredAction(command, rollbackCommand, runStrategy, rollbackRunStrategy, filterMap, target);
        }).collect(Collectors.toList());
    }
}
