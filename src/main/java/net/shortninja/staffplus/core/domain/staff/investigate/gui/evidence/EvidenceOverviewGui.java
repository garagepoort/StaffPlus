package net.shortninja.staffplus.core.domain.staff.investigate.gui.evidence;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.PagedGui;
import net.shortninja.staffplus.core.common.gui.confirmation.ConfirmationGui;
import net.shortninja.staffplus.core.domain.staff.investigate.Investigation;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationEvidenceService;
import net.shortninja.staffplusplus.investigate.evidence.EvidenceGuiClick;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EvidenceOverviewGui extends PagedGui {

    private final InvestigationEvidenceService investigationEvidenceService = StaffPlus.get().getIocContainer().get(InvestigationEvidenceService.class);
    private final IProtocolService protocolService = StaffPlus.get().getIocContainer().get(IProtocolService.class);
    private final InvestigationEvidenceItemBuilder investigationEvidenceItemBuilder = StaffPlus.get().getIocContainer().get(InvestigationEvidenceItemBuilder.class);
    private final List<EvidenceGuiClick> evidenceGuiClicks = StaffPlus.get().getIocContainer().getList(EvidenceGuiClick.class);

    private final Investigation investigation;
    private final Supplier<AbstractGui> goBack;

    public EvidenceOverviewGui(Player player,
                               String title,
                               int currentPage,
                               Investigation investigation, Supplier<AbstractGui> previousGuiSupplier) {
        super(player, title, currentPage, previousGuiSupplier);
        this.investigation = investigation;
        this.goBack = () -> new EvidenceOverviewGui(player, getTitle(), getCurrentPage(), investigation, previousGuiSupplier);
    }

    @Override
    protected AbstractGui getNextUi(Player player, SppPlayer target, String title, int page) {
        return new EvidenceOverviewGui(player, title, page, investigation, goBack);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                String nbtString = protocolService.getVersionProtocol().getNbtString(item);
                int id = Integer.parseInt(nbtString.split(";")[0]);
                String type = nbtString.split(";")[1];
                int evidenceId = Integer.parseInt(nbtString.split(";")[2]);

                if (clickType == ClickType.RIGHT) {
                    new ConfirmationGui("Unlink evidence?", "Are you sure you want to unlink: " + type + "(ID=" + evidenceId + ")",
                        p -> {
                            investigationEvidenceService.unlinkEvidence(p, investigation, id);
                            goBack.get().show(p);
                        },
                        p -> goBack.get().show(p))
                        .closeOnCancel(false)
                        .closeOnConfirmation(false)
                        .show(player);
                } else {
                    evidenceGuiClicks.stream()
                        .filter(e -> e.getType().equals(type))
                        .findFirst()
                        .ifPresent(e -> e.onClick(player, getTarget(), evidenceId, () -> goBack.get().show(player)));
                }
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return investigationEvidenceService.getEvidenceForInvestigation(investigation, offset, amount)
            .stream().map(investigationEvidenceItemBuilder::build)
            .collect(Collectors.toList());
    }
}