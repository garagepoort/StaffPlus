package net.shortninja.staffplus.core.session;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcioc.IocMultiProvider;
import net.shortninja.staffplus.core.application.data.DataFile;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.session.database.SessionEntity;
import net.shortninja.staffplus.core.session.database.SessionsRepository;
import net.shortninja.staffplusplus.vanish.VanishType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Optional;

@IocBean
@IocMultiProvider(SessionEnhancer.class)
public class DefaultSessionEnhancer implements SessionEnhancer {
    private final SessionsRepository sessionsRepository;
    private final Options options;
    private final FileConfiguration dataFileConfiguration;

    public DefaultSessionEnhancer(SessionsRepository sessionsRepository, Options options, DataFile dataFile) {
        this.sessionsRepository = sessionsRepository;
        this.options = options;
        this.dataFileConfiguration = dataFile.getConfiguration();
    }

    @Override
    public void enhance(PlayerSession playerSession) {
        VanishType vanishType = VanishType.valueOf(dataFileConfiguration.getString(playerSession.getUuid() + ".vanish-type", "NONE"));

        Optional<SessionEntity> session = sessionsRepository.findSession(playerSession.getUuid());
        if (options.serverSyncConfiguration.isVanishSyncEnabled()) {
            playerSession.setVanishType(session.map(SessionEntity::getVanishType).orElse(vanishType));
        }
        playerSession.setMutedStaffChatChannels(session.map(SessionEntity::getMutedStaffChatChannels).orElse(new HashSet<>()));
    }
}
