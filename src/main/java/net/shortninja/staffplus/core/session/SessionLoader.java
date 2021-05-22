package net.shortninja.staffplus.core.session;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMulti;
import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.application.data.DataFile;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.domain.player.PlayerManager;
import net.shortninja.staffplus.core.domain.staff.mode.config.GeneralModeConfiguration;
import net.shortninja.staffplusplus.session.SppPlayer;
import net.shortninja.staffplus.core.domain.staff.mute.MuteService;
import net.shortninja.staffplus.core.session.database.SessionEntity;
import net.shortninja.staffplus.core.session.database.SessionsRepository;
import net.shortninja.staffplusplus.alerts.AlertType;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.Bukkit.getScheduler;

@IocBean
public class SessionLoader {
    private final PlayerManager playerManager;
    private final MuteService muteService;
    private final Options options;
    private final SessionsRepository sessionsRepository;
    private final DataFile dataFile;
    private final FileConfiguration dataFileConfiguration;
    private final List<SessionEnhancer> sessionEnhancers;

    public SessionLoader(PlayerManager playerManager, MuteService muteService, Options options, SessionsRepository sessionsRepository, DataFile dataFile, @IocMulti(SessionEnhancer.class) List<SessionEnhancer> sessionEnhancers) {
        this.playerManager = playerManager;
        this.muteService = muteService;
        this.options = options;
        this.sessionsRepository = sessionsRepository;
        this.dataFileConfiguration = dataFile.getConfiguration();
        this.dataFile = dataFile;
        this.sessionEnhancers = sessionEnhancers;
    }

    PlayerSession loadSession(Player player) {
        return loadSession(player.getUniqueId());
    }

    PlayerSession loadSession(UUID playerUuid) {
        return dataFileConfiguration.contains(playerUuid.toString()) ? buildKnownSession(playerUuid) : buildNewSession(playerUuid);
    }

    private PlayerSession buildNewSession(UUID uuid) {
        Optional<SppPlayer> providedPlayer = playerManager.getOnlinePlayer(uuid);
        if (!providedPlayer.isPresent()) {
            throw new RuntimeException("Trying to instantiate session for offline user");
        }
        return new PlayerSession(uuid, providedPlayer.get().getUsername(), isMuted(uuid));
    }

    private PlayerSession buildKnownSession(UUID uuid) {
        String name = dataFileConfiguration.getString(uuid + ".name");
        String glassColor = dataFileConfiguration.getString(uuid + ".glass-color");
        VanishType vanishType = VanishType.valueOf(dataFileConfiguration.getString(uuid + ".vanish-type", "NONE"));
        Material glassMaterial = Material.WHITE_STAINED_GLASS_PANE;
        if (glassColor != null && !glassColor.equals("0")) {
            glassMaterial = Material.valueOf(glassColor);
        }

        List<String> playerNotes = loadPlayerNotes(uuid);
        Map<AlertType, Boolean> alertOptions = loadAlertOptions(uuid);

        PlayerSession playerSession = new PlayerSession(uuid, name, glassMaterial, playerNotes, alertOptions, isMuted(uuid), vanishType);
        sessionEnhancers.forEach(s -> s.enhance(playerSession));
        return playerSession;
    }

    private boolean isMuted(UUID uuid) {
        return muteService.getMuteByMutedUuid(uuid).isPresent();
    }

    private Map<AlertType, Boolean> loadAlertOptions(UUID uuid) {
        Map<AlertType, Boolean> alertOptions = new HashMap<>();

        for (String string : dataFileConfiguration.getStringList(uuid + ".alert-options")) {
            String[] parts = string.split(";");

            alertOptions.put(AlertType.valueOf(parts[0]), Boolean.valueOf(parts[1]));
        }

        return alertOptions;
    }

    private List<String> loadPlayerNotes(UUID uuid) {
        List<String> playerNotes = new ArrayList<>();

        for (String string : dataFileConfiguration.getStringList(uuid + ".notes")) {
            if (string.contains("&7")) {
                continue;
            }

            playerNotes.add("&7" + string);
        }

        return playerNotes;
    }

    public void saveSession(PlayerSession playerSession) {
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            dataFile.save(playerSession);
            if (options.serverSyncConfiguration.sessionSyncEnabled()) {
                updateSqlSession(playerSession);
            }
        });
    }

    public void saveSessions(Collection<PlayerSession> playerSessions) {
        dataFile.save(playerSessions);
        if (options.serverSyncConfiguration.sessionSyncEnabled()) {
            playerSessions.forEach(this::updateSqlSession);
        }
    }

    private void updateSqlSession(PlayerSession playerSession) {
        Optional<SessionEntity> session = sessionsRepository.findSession(playerSession.getUuid());
        String staffModeName = playerSession.getModeConfiguration().map(GeneralModeConfiguration::getName).orElse(null);
        if (session.isPresent()) {
            if (options.serverSyncConfiguration.isVanishSyncEnabled()) {
                session.get().setVanishType(playerSession.getVanishType());
            }
            if (options.serverSyncConfiguration.isStaffModeSyncEnabled()) {
                session.get().setStaffMode(playerSession.isInStaffMode());
                session.get().setStaffModeName(staffModeName);
            }
            session.get().setMutedStaffChatChannels(playerSession.getMutedStaffChatChannels());
            sessionsRepository.update(session.get());
        } else {
            sessionsRepository.addSession(new SessionEntity(playerSession.getUuid(), playerSession.getVanishType(), playerSession.isInStaffMode(), playerSession.getMutedStaffChatChannels(), staffModeName));
        }
    }
}