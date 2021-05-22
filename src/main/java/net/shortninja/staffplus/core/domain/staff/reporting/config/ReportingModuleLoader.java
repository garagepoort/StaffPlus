package net.shortninja.staffplus.core.domain.staff.reporting.config;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.common.Sounds;
import net.shortninja.staffplus.core.common.config.AbstractConfigLoader;

import net.shortninja.staffplus.core.common.config.ConfigurationUtil;
import net.shortninja.staffplus.core.common.gui.GuiItemConfig;
import net.shortninja.staffplus.core.domain.actions.ActionConfigLoader;
import net.shortninja.staffplus.core.domain.actions.ConfiguredAction;
import net.shortninja.staffplusplus.reports.ReportStatus;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@IocBean
public class ReportingModuleLoader extends AbstractConfigLoader<ReportConfiguration> {

    @Override
    protected ReportConfiguration load() {
        boolean enabled = defaultConfig.getBoolean("reports-module.enabled");
        int cooldown = defaultConfig.getInt("reports-module.cooldown");
        boolean showReporter = defaultConfig.getBoolean("reports-module.show-reporter");
        boolean notifyReportOnJoin = defaultConfig.getBoolean("reports-module.reporter-notifications.notify-on-join");
        boolean closingReasonEnabled = defaultConfig.getBoolean("reports-module.closing-reason-enabled", true);
        boolean fixedReason = defaultConfig.getBoolean("reports-module.fixed-reason", false);
        boolean fixedReasonCulprit = defaultConfig.getBoolean("reports-module.fixed-reason-culprit", false);
        Sounds sound = stringToSound(sanitize(defaultConfig.getString("reports-module.sound", "NONE")));
        String myReportsPermission = permissionsConfig.getString("permissions.view-my-reports");
        String myReportsCmd = commandsConfig.getString("commands.my-reports");
        List<ReportStatus> reporterNotifyStatuses = stream(defaultConfig.getString("reports-module.reporter-notifications.status-change-notifications", "").split(";"))
            .filter(s -> !s.isEmpty())
            .map(ReportStatus::valueOf)
            .collect(Collectors.toList());

        boolean modeGuiReports = staffModeModulesConfig.getBoolean("modules.gui-module.reports-gui");
        String modeGuiReportsTitle = staffModeModulesConfig.getString("modules.gui-module.reports-title");
        String modeGuiReportsName = staffModeModulesConfig.getString("modules.gui-module.reports-name");
        String modeGuiReportsLore = staffModeModulesConfig.getString("modules.gui-module.reports-lore");
        String modeGuiMyReportsTitle = staffModeModulesConfig.getString("modules.gui-module.my-reports-title");
        String modeGuiMyReportsLore = staffModeModulesConfig.getString("modules.gui-module.my-reports-lore");
        String modeGuiAssignedReportsTitle = staffModeModulesConfig.getString("modules.gui-module.assigned-reports-title");
        String modeGuiAssignedReportsLore = staffModeModulesConfig.getString("modules.gui-module.assigned-reports-lore");
        String modeGuiClosedReportsTitle = staffModeModulesConfig.getString("modules.gui-module.closed-reports-title");
        String modeGuiClosedReportsLore = staffModeModulesConfig.getString("modules.gui-module.closed-reports-lore");

        GuiItemConfig openReportsGui = new GuiItemConfig(modeGuiReports, modeGuiReportsTitle, modeGuiReportsName, modeGuiReportsLore);
        GuiItemConfig myReportsGui = new GuiItemConfig(modeGuiReports, modeGuiMyReportsTitle, modeGuiMyReportsTitle, modeGuiMyReportsLore);
        GuiItemConfig assignedReportsGui = new GuiItemConfig(modeGuiReports, modeGuiAssignedReportsTitle, modeGuiAssignedReportsTitle, modeGuiAssignedReportsLore);
        GuiItemConfig closedReportsGui = new GuiItemConfig(modeGuiReports, modeGuiClosedReportsTitle, modeGuiClosedReportsTitle, modeGuiClosedReportsLore);

        List<ConfiguredAction> acceptReportActions = ActionConfigLoader.loadActions((List<LinkedHashMap<String, Object>>) defaultConfig.getList("reports-module.accept-commands", new ArrayList<>()));
        List<ConfiguredAction> rejectReportActions = ActionConfigLoader.loadActions((List<LinkedHashMap<String, Object>>) defaultConfig.getList("reports-module.reject-commands", new ArrayList<>()));
        List<ConfiguredAction> reopenReportActions = ActionConfigLoader.loadActions((List<LinkedHashMap<String, Object>>) defaultConfig.getList("reports-module.reopen-commands", new ArrayList<>()));
        List<ConfiguredAction> resolveReportActions = ActionConfigLoader.loadActions((List<LinkedHashMap<String, Object>>) defaultConfig.getList("reports-module.resolve-commands", new ArrayList<>()));

        return new ReportConfiguration(enabled,
            cooldown,
            showReporter,
            sound,
            closingReasonEnabled,
            openReportsGui,
            myReportsGui,
            assignedReportsGui,
            closedReportsGui,
            myReportsPermission,
            myReportsCmd,
            notifyReportOnJoin,
            reporterNotifyStatuses,
            getReportTypes(defaultConfig),
            fixedReason,
            fixedReasonCulprit, getReportReasons(defaultConfig),
            acceptReportActions,
            rejectReportActions,
            reopenReportActions,
            resolveReportActions);
    }

    private List<ReportTypeConfiguration> getReportTypes(FileConfiguration config) {
        List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>) config.getList("reports-module.report-types", new ArrayList<>());

        return Objects.requireNonNull(list).stream().map(map -> {
            String name = map.get("name");
            String lore = map.get("info");
            Material material = map.containsKey("material") ? Material.valueOf(map.get("material")) : Material.PAPER;
            String filtersString = map.containsKey("filters") ? map.get("filters") : null;
            Map<String, String> filterMap = ConfigurationUtil.loadFilters(filtersString);

            return new ReportTypeConfiguration(name, material, lore, filterMap);
        }).collect(Collectors.toList());
    }

    private List<ReportReasonConfiguration> getReportReasons(FileConfiguration config) {
        List<LinkedHashMap<String, String>> list = (List<LinkedHashMap<String, String>>) config.getList("reports-module.reasons", new ArrayList<>());

        return Objects.requireNonNull(list).stream().map(map -> {
            String reason = map.get("reason");
            String type = map.get("type");
            String lore = map.get("info");
            Material material = map.containsKey("material") ? Material.valueOf(map.get("material")) : Material.PAPER;
            String filtersString = map.containsKey("filters") ? map.get("filters") : null;
            Map<String, String> filterMap = ConfigurationUtil.loadFilters(filtersString);
            return new ReportReasonConfiguration(reason, type, material, lore, filterMap);
        }).collect(Collectors.toList());
    }
}
