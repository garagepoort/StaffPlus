package net.shortninja.staffplus.core.domain.staff.investigate;

import be.garagepoort.mcioc.IocBean;
import net.shortninja.staffplus.core.common.config.Messages;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.BusinessException;
import net.shortninja.staffplus.core.common.utils.BukkitUtils;
import net.shortninja.staffplus.core.common.utils.PermissionHandler;
import net.shortninja.staffplus.core.domain.staff.investigate.database.investigation.InvestigationsRepository;
import net.shortninja.staffplusplus.investigate.InvestigationConcludedEvent;
import net.shortninja.staffplusplus.investigate.InvestigationPausedEvent;
import net.shortninja.staffplusplus.investigate.InvestigationStartedEvent;
import net.shortninja.staffplusplus.investigate.InvestigationStatus;
import net.shortninja.staffplusplus.session.SppPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.shortninja.staffplus.core.common.utils.BukkitUtils.sendEvent;
import static net.shortninja.staffplusplus.investigate.InvestigationStatus.OPEN;
import static net.shortninja.staffplusplus.investigate.InvestigationStatus.PAUSED;

@IocBean
public class InvestigationService {

    private final InvestigationsRepository investigationsRepository;
    private final Options options;
    private final Messages messages;
    private final BukkitUtils bukkitUtils;
    private final PermissionHandler permissionHandler;


    public InvestigationService(InvestigationsRepository investigationsRepository, Options options, Messages messages, BukkitUtils bukkitUtils, PermissionHandler permissionHandler) {
        this.investigationsRepository = investigationsRepository;
        this.options = options;
        this.messages = messages;
        this.bukkitUtils = bukkitUtils;
        this.permissionHandler = permissionHandler;
    }

    public void startInvestigation(Player investigator, SppPlayer investigated) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            permissionHandler.validate(investigator, options.investigationConfiguration.getInvestigatePermission());
            validateInvestigationStart(investigator, investigated);

            Investigation investigation = new Investigation(investigator.getName(), investigator.getUniqueId(), investigated.getUsername(), investigated.getId(), options.serverName);
            int id = investigationsRepository.addInvestigation(investigation);
            investigation.setId(id);
            sendEvent(new InvestigationStartedEvent(investigation));
        });
    }

    public void startInvestigation(Player investigator) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            permissionHandler.validate(investigator, options.investigationConfiguration.getInvestigatePermission());
            validateInvestigationStart(investigator);

            Investigation investigation = new Investigation(investigator.getName(), investigator.getUniqueId(), options.serverName);
            int id = investigationsRepository.addInvestigation(investigation);
            investigation.setId(id);
            sendEvent(new InvestigationStartedEvent(investigation));
        });
    }

    public void resumeInvestigation(Player investigator, int investigationId) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            permissionHandler.validate(investigator, options.investigationConfiguration.getInvestigatePermission());
            validateInvestigationStart(investigator);

            Investigation investigation = getInvestigation(investigationId);
            if(investigation.getStatus() != PAUSED) {
                throw new BusinessException("&CCan not resume investigation. This investigation is not paused");
            }
            investigation.setStatus(OPEN);
            investigation.setInvestigatorName(investigator.getName());
            investigation.setInvestigatorUuid(investigator.getUniqueId());
            investigationsRepository.updateInvestigation(investigation);
            sendEvent(new InvestigationStartedEvent(investigation));
        });
    }

    public Optional<Investigation> findCurrentActiveInvestigation(Player investigator) {
        return investigationsRepository.getInvestigationForInvestigator(investigator.getUniqueId(), Collections.singletonList(OPEN));
    }

    public void resumeInvestigation(Player investigator, SppPlayer investigated) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            permissionHandler.validate(investigator, options.investigationConfiguration.getInvestigatePermission());
            validateInvestigationStart(investigator, investigated);

            Investigation investigation = investigationsRepository.findInvestigationForInvestigated(investigator.getUniqueId(), investigated.getId(), Collections.singletonList(PAUSED))
                .orElseThrow(() -> new BusinessException("Cannot resume investigation. No paused investigation found"));
            investigation.setStatus(OPEN);
            investigation.setInvestigatorName(investigator.getName());
            investigation.setInvestigatorUuid(investigator.getUniqueId());
            investigationsRepository.updateInvestigation(investigation);
            sendEvent(new InvestigationStartedEvent(investigation));
        });
    }

    public Optional<Investigation> getPausedInvestigation(Player investigator, SppPlayer investigated) {
        return investigationsRepository.findInvestigationForInvestigated(investigator.getUniqueId(), investigated.getId(), Collections.singletonList(PAUSED));
    }

    public List<Investigation> getPausedInvestigations(Player investigator) {
        return investigationsRepository.findAllInvestigationsForInvestigator(investigator.getUniqueId(), Collections.singletonList(PAUSED));
    }

    public List<Investigation> getPausedInvestigations(Player investigator, int offset, int amount) {
        return investigationsRepository.findAllInvestigationsForInvestigator(investigator.getUniqueId(), Collections.singletonList(PAUSED), offset, amount);
    }

    public void concludeInvestigation(Player investigator) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            permissionHandler.validate(investigator, options.investigationConfiguration.getInvestigatePermission());
            Investigation investigation = investigationsRepository.getInvestigationForInvestigator(investigator.getUniqueId(), Collections.singletonList(OPEN))
                .orElseThrow(() -> new BusinessException("&CYou currently have no investigation running.", messages.prefixInvestigations));
            investigation.setConclusionDate(System.currentTimeMillis());
            investigation.setStatus(InvestigationStatus.CONCLUDED);

            investigationsRepository.updateInvestigation(investigation);
            sendEvent(new InvestigationConcludedEvent(investigation));
        });
    }

    public void concludeInvestigation(Player investigator, int investigationId) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            permissionHandler.validate(investigator, options.investigationConfiguration.getInvestigatePermission());
            Investigation investigation = investigationsRepository.findInvestigation(investigationId)
                .orElseThrow(() -> new BusinessException("&CNo investigation found with thid id.", messages.prefixInvestigations));

            if (investigation.getStatus() == OPEN && !investigation.getInvestigatorUuid().equals(investigator.getUniqueId())) {
                throw new BusinessException("$CCannot conclude the investigation. This investigation is currently ongoing.");
            }
            investigation.setConclusionDate(System.currentTimeMillis());
            investigation.setStatus(InvestigationStatus.CONCLUDED);

            investigationsRepository.updateInvestigation(investigation);
            sendEvent(new InvestigationConcludedEvent(investigation));
        });
    }

    public void pauseInvestigation(Player investigator) {
        bukkitUtils.runTaskAsync(investigator, () -> {
            Investigation investigation = investigationsRepository.getInvestigationForInvestigator(investigator.getUniqueId(), Collections.singletonList(OPEN))
                .orElseThrow(() -> new BusinessException("&CYou currently have no investigation running", messages.prefixInvestigations));
            investigation.setStatus(InvestigationStatus.PAUSED);

            investigationsRepository.updateInvestigation(investigation);
            sendEvent(new InvestigationPausedEvent(investigation));
        });
    }

    public void tryPausingInvestigation(Player investigator) {
        bukkitUtils.runTaskAsync(investigator, () ->
            investigationsRepository.getInvestigationForInvestigator(investigator.getUniqueId(), Collections.singletonList(OPEN))
            .ifPresent(investigation -> {
                investigation.setStatus(InvestigationStatus.PAUSED);
                investigationsRepository.updateInvestigation(investigation);
                sendEvent(new InvestigationPausedEvent(investigation));
            }));
    }

    public void pauseInvestigationsForInvestigated(Player investigated) {
        bukkitUtils.runTaskAsync(investigated, () -> {
            List<Investigation> investigations = investigationsRepository.getInvestigationsForInvestigated(investigated.getUniqueId(), Collections.singletonList(OPEN));
            for (Investigation investigation : investigations) {
                investigation.setStatus(InvestigationStatus.PAUSED);
                investigationsRepository.updateInvestigation(investigation);
                sendEvent(new InvestigationPausedEvent(investigation));
            }
        });
    }

    public List<Investigation> getAllInvestigations(int offset, int amount) {
        return investigationsRepository.getAllInvestigations(offset, amount);
    }

    public List<Investigation> getInvestigationsForInvestigated(SppPlayer sppPlayer, int offset, int amount) {
        return investigationsRepository.getInvestigationsForInvestigated(sppPlayer.getId(), offset, amount);
    }

    public Investigation getInvestigation(int investigationId) {
        return investigationsRepository.findInvestigation(investigationId)
            .orElseThrow(() -> new BusinessException("Investigation with id [" + investigationId + "] not found", messages.prefixInvestigations));
    }

    private void validateInvestigationStart(Player investigator, SppPlayer investigated) {
        if (!investigated.isOnline() && !options.investigationConfiguration.isAllowOfflineInvestigation()) {
            throw new BusinessException("Not allowed to investigate an offline player");
        }
        investigationsRepository.getInvestigationForInvestigator(investigator.getUniqueId(), Collections.singletonList(OPEN)).ifPresent(investigation1 -> {
            throw new BusinessException("&CCan't start an investigation, you currently have an investigation running", messages.prefixInvestigations);
        });
        List<Investigation> runningInvestigations = investigationsRepository.getInvestigationsForInvestigated(investigated.getId(), Collections.singletonList(OPEN));
        if (options.investigationConfiguration.getMaxConcurrentInvestigation() > 0 && runningInvestigations.size() >= options.investigationConfiguration.getMaxConcurrentInvestigation()) {
            throw new BusinessException("&CCannot start investigation. There are already too many investigations ongoing at this moment.");
        }
    }

    private void validateInvestigationStart(Player investigator) {
        investigationsRepository.getInvestigationForInvestigator(investigator.getUniqueId(), Collections.singletonList(OPEN)).ifPresent(investigation1 -> {
            throw new BusinessException("&CCan't start an investigation, you currently have an investigation running", messages.prefixInvestigations);
        });
    }

}
