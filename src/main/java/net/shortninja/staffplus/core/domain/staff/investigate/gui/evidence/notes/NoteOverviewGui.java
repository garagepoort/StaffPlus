package net.shortninja.staffplus.core.domain.staff.investigate.gui.evidence.notes;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.IProtocolService;
import net.shortninja.staffplus.core.common.Items;
import net.shortninja.staffplus.core.common.gui.AbstractGui;
import net.shortninja.staffplus.core.common.gui.IAction;
import net.shortninja.staffplus.core.common.gui.PagedGui;
import net.shortninja.staffplus.core.common.gui.confirmation.ConfirmationGui;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplus.core.domain.staff.investigate.Investigation;
import net.shortninja.staffplus.core.domain.staff.investigate.InvestigationNoteService;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NoteOverviewGui extends PagedGui {

    private final InvestigationNoteService investigationNoteService = StaffPlus.get().getIocContainer().get(InvestigationNoteService.class);
    private final IProtocolService protocolService = StaffPlus.get().getIocContainer().get(IProtocolService.class);
    private final InvestigationNoteItemBuilder investigationEvidenceItemBuilder = StaffPlus.get().getIocContainer().get(InvestigationNoteItemBuilder.class);

    private final Investigation investigation;
    private final Supplier<AbstractGui> goBack;

    public NoteOverviewGui(Player player,
                           String title,
                           int currentPage,
                           Investigation investigation, Supplier<AbstractGui> previousGuiSupplier) {
        super(player, title, currentPage, previousGuiSupplier);
        this.investigation = investigation;
        this.goBack = () -> new NoteOverviewGui(player, getTitle(), getCurrentPage(), investigation, previousGuiSupplier);
    }

    @Override
    protected AbstractGui getNextUi(Player player, SppPlayer target, String title, int page) {
        return new NoteOverviewGui(player, title, page, investigation, goBack);
    }

    @Override
    public void buildGui() {
        super.buildGui();
        setItem(50, Items.createBook("Add note", ""), new AddNoteAction(investigation));
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot, ClickType clickType) {
                String nbtString = protocolService.getVersionProtocol().getNbtString(item);
                int noteId = Integer.parseInt(nbtString);

                if (clickType == ClickType.RIGHT) {
                    new ConfirmationGui("Delete note?", "Are you sure you want to delete note(ID=" + noteId + ")",
                        p -> {
                            investigationNoteService.deleteNote(p, investigation, noteId);
                            goBack.get().show(p);
                        },
                        p -> goBack.get().show(p))
                        .closeOnCancel(false)
                        .closeOnConfirmation(false)
                        .show(player);
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
        return investigationNoteService.getNotesForInvestigation(investigation, offset, amount)
            .stream().map(investigationEvidenceItemBuilder::build)
            .collect(Collectors.toList());
    }
}