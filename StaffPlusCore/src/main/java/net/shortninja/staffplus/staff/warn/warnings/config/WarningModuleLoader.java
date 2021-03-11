package net.shortninja.staffplus.staff.warn.warnings.config;

import net.shortninja.staffplus.common.Sounds;
import net.shortninja.staffplus.common.actions.ConfiguredAction;
import net.shortninja.staffplus.common.config.ConfigLoader;
import net.shortninja.staffplus.common.exceptions.ConfigurationException;
import net.shortninja.staffplus.common.time.TimeUnit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.common.actions.ActionConfigLoader.loadActions;

public class WarningModuleLoader extends ConfigLoader<WarningConfiguration> {

    private static final String SCORE = "score";
    private static final String ACTIONS = "actions";

    @Override
    protected WarningConfiguration load(FileConfiguration config) {
        boolean enabled = config.getBoolean("warnings-module.enabled");
        boolean showIssuer = config.getBoolean("warnings-module.show-issuer");
        boolean notifyUser = config.getBoolean("warnings-module.user-notifications.enabled");
        boolean alwaysNotifyUser = config.getBoolean("warnings-module.user-notifications.always-notify");
        List<ConfiguredAction> actions = loadActions((List<LinkedHashMap<String, Object>>) config.getList("warnings-module.actions", new ArrayList<>()));
        String myWarningsPermission = config.getString("permissions.view-my-warnings");
        String myWarningsCmd = config.getString("commands.my-warnings");
        Sounds sound = stringToSound(sanitize(config.getString("warnings-module.sound")));

        return new WarningConfiguration(enabled,
            showIssuer,
            sound,
            notifyUser, alwaysNotifyUser,
            myWarningsPermission,
            myWarningsCmd,
            getThresholds(config),
            getSeverityLevels(config),
            actions);
    }

    private List<WarningThresholdConfiguration> getThresholds(FileConfiguration config) {
        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) config.getList("warnings-module.thresholds", new ArrayList<>());

        return Objects.requireNonNull(list).stream().map(map -> {
            if (!map.containsKey(SCORE) || !map.containsKey(ACTIONS)) {
                throw new ConfigurationException("Invalid warnings configuration. Threshold should define a score and actions");
            }
            int score = (Integer) map.get(SCORE);
            List<ConfiguredAction> actions = loadActions((List<LinkedHashMap<String, Object>>) map.get(ACTIONS));
            List<ConfiguredAction> rollbackActions = map.containsKey("rollback-actions") ? loadActions((List<LinkedHashMap<String, Object>>) map.get("rollback-actions")) : Collections.emptyList();

            return new WarningThresholdConfiguration(score, actions, rollbackActions);
        }).collect(Collectors.toList());
    }

    private List<WarningSeverityConfiguration> getSeverityLevels(FileConfiguration config) {
        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) config.getList("warnings-module.severity-levels", new ArrayList<>());

        return Objects.requireNonNull(list).stream().map(map -> {
            String name = (String) map.get("name");
            int score = (Integer) map.get(SCORE);
            long expirationDuration = map.containsKey("expiresAfter") ? TimeUnit.getDuration((String) map.get("expiresAfter")) : -1;
            return new WarningSeverityConfiguration(name, score, expirationDuration);
        }).collect(Collectors.toList());
    }

}
