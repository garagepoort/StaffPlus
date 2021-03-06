package net.shortninja.staffplus.core.domain.staff.reporting.config;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

public class ReportTypeConfiguration {

    private String type;
    private Material material;
    private String lore;
    private Map<String, String> filters;

    public ReportTypeConfiguration(String type, Material material, String lore, Map<String, String> filters) {
        this.type = type;
        this.material = material;
        this.lore = lore;
        this.filters = filters;
    }

    public String getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public String getLore() {
        return lore;
    }

    @SafeVarargs
    public final boolean filterMatches(Predicate<Map<String, String>>... predicate) {
        return Arrays.stream(predicate).allMatch(p -> p.test(filters));
    }
}
