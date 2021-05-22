package net.shortninja.staffplus.core.domain.staff.reporting.gui;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.domain.staff.reporting.config.ReportReasonConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.shortninja.staffplus.core.common.JavaUtils.formatLines;

public class ReportReasonItemBuilder {

    public static ItemStack build(ReportReasonConfiguration reportReasonConfiguration) {
        List<String> lore = new ArrayList<>();

        if(reportReasonConfiguration.getLore() != null) {
            for (String line : formatLines(reportReasonConfiguration.getLore(), 30)) {
                lore.add("&7" + line);
            }
        }

        ItemStack item = Items.editor(Items.builder().setMaterial(reportReasonConfiguration.getMaterial()).build())
            .setAmount(1)
            .setName("&6" + reportReasonConfiguration.getReason())
            .setLore(lore)
            .build();

        return StaffPlus.get().getIocContainer().get(IProtocolService.class).getVersionProtocol().addNbtString(item, reportReasonConfiguration.getReason());
    }


}
