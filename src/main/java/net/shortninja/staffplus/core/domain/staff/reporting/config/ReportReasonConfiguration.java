package net.shortninja.staffplus.core.domain.staff.reporting.config;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ReportReasonConfiguration {

    private String reason;
    private Material material;
    private String lore;
    private String reportType;
    private Map<String, String> filters;

    public ReportReasonConfiguration(String reason, String reportType, Material material, String lore, Map<String, String> filters) {
        this.reason = reason;
        this.reportType = reportType;
        this.material = material;
        this.lore = lore;
        this.filters = filters;
    }

    public String getReason() {
        return reason;
    }

    public Optional<String> getReportType() {
        return Optional.ofNullable(reportType);
    }

    public Material getMaterial() {
        return material;
    }

    public String getLore() {
        return lore;
    }

    public boolean filterMatches(Predicate<Map<String, String>>[] predicate) {
        return Arrays.stream(predicate).allMatch(p -> p.test(filters));
    }
}
