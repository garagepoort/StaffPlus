package net.shortninja.staffplus.core.common.config;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.config.migrators.*;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoUpdater extends AbstractConfigUpdater {

    private static final List<ConfigMigrator> MIGRATORS = Arrays.asList(
        new StaffModeCommandMigrator(),
        new StaffModeModulesMigrator(),
        new PermissionsMigrator(),
        new CommandsMigrator(),
        new StaffModesMigrator(),
        new StaffChatChannelMigrator());

    public static boolean updateConfig(ConfigurationFile configurationFile) {
        try {
            validateConfigFile(configurationFile.getPath());

            FileConfiguration config = configurationFile.getFileConfiguration();
            AtomicInteger counter = new AtomicInteger();
            Map<String, Object> defaultConfigMap = loadConfig(configurationFile.getPath());

            defaultConfigMap.forEach((k, v) -> {
                if (!config.contains(k, true) && !(v instanceof ConfigurationSection)) {
                    config.set(k, v);
                    counter.getAndIncrement();
                }
            });

            File file = new File(StaffPlus.get().getDataFolder() + File.separator + configurationFile.getPath());
            config.save(file);
            if (counter.get() > 0) {
                StaffPlus.get().getLogger().info("Configuration file Fixed. [" + counter.get() + "] properties were added. Should StaffPlusPlus still have problems starting up, please compare your config with the default configuration: https://github.com/garagepoort/StaffPlusPlus/blob/master/StaffPlusCore/src/main/resources/config.yml");
            }
            return true;
        } catch (InvalidConfigurationException | IOException | ConfigurationException e) {
            StaffPlus.get().getLogger().severe("Configuration file is INVALID!!! Disabling StaffPlusPlus!");
            StaffPlus.get().getLogger().severe("Full error [" + e.getMessage() + "]");
            return false;
        }
    }

    public static void runMigrations(List<ConfigurationFile> fileConfigurations) {
        try {
            MIGRATORS.forEach(m -> m.migrate(fileConfigurations));
            for (ConfigurationFile configurationFile : fileConfigurations) {
                File file = new File(StaffPlus.get().getDataFolder() + File.separator + configurationFile.getPath());
                configurationFile.getFileConfiguration().options().copyDefaults(true);
                configurationFile.getFileConfiguration().save(file);
            }
        } catch (IOException e) {
            throw new ConfigurationException("Unable to migrate configurations", e);
        }
    }
}
