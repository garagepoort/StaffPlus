package net.shortninja.staffplus.core.domain.staff.ban.playerbans.config;

import be.garagepoort.mcioc.configuration.IConfigTransformer;
import net.shortninja.staffplus.core.domain.staff.ban.playerbans.BanType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BanReasonConfigMapper implements IConfigTransformer<List<BanReasonConfiguration>, List<LinkedHashMap<String, Object>>> {

    @Override
    public List<BanReasonConfiguration> mapConfig(List<LinkedHashMap<String, Object>> list) {
        return list.stream().map(map -> {
            String name = (String) map.get("name");
            String reason = (String) map.get("reason");
            String template = (String) map.get("template");
            BanType banType = map.containsKey("ban-type") ? BanType.valueOf((String) map.get("ban-type")) : null;
            return new BanReasonConfiguration(name, reason, template, banType);
        }).collect(Collectors.toList());
    }
}
