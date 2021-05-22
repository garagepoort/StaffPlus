package net.shortninja.staffplus.core.domain.staff.reporting.config;

import java.util.Map;
import java.util.function.Predicate;

public class CulpritFilterPredicate implements Predicate<Map<String, String>> {
    private static final String CULPRIT = "culprit";
    private final boolean hasCulprit;

    public CulpritFilterPredicate(boolean hasCulprit) {
        this.hasCulprit = hasCulprit;
    }

    @Override
    public boolean test(Map<String, String> filters) {
        if(!filters.containsKey(CULPRIT)) {
            return true;
        }
        return filters.get(CULPRIT).equalsIgnoreCase(String.valueOf(hasCulprit));
    }
}
