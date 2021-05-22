package net.shortninja.staffplus.core.common.config;

import net.shortninja.staffplus.core.StaffPlus;
import net.shortninja.staffplus.core.common.exceptions.ConfigurationException;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationUtil {

    private static final Logger logger = StaffPlus.get().getLogger();

    private ConfigurationUtil() {
    }

    public static void saveConfiguration(String configurationFile) {
        File dataFolder = StaffPlus.get().getDataFolder();
        String fullConfigResourcePath = (configurationFile).replace('\\', '/');

        InputStream in = getResource(fullConfigResourcePath);
        if (in == null) {
            logger.log(Level.SEVERE, "Could not find configuration file " + fullConfigResourcePath);
            return;
        }

        File outFile = new File(dataFolder, fullConfigResourcePath);
        int lastIndex = fullConfigResourcePath.lastIndexOf(47);
        File outDir = new File(dataFolder, fullConfigResourcePath.substring(0, Math.max(lastIndex, 0)));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists()) {
                try (OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[1024];

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                in.close();
            }
        } catch (IOException var10) {
            logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
        }
    }

    private static InputStream getResource(@NotNull String filename) {
        try {
            URL url = ConfigurationUtil.class.getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            } else {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException var4) {
            return null;
        }
    }

    public static FileConfiguration loadConfiguration(String path) {
        File file = Paths.get(StaffPlus.get().getDataFolder() + File.separator + path).toFile();

        Validate.notNull(file, "File cannot be null");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception e) {
            throw new ConfigurationException("Cannot load " + file, e);
        }

        return config;
    }

    public static Map<String, String> loadFilters(String filtersString) {
        Map<String, String> filterMap = new HashMap<>();
        if (filtersString != null) {
            String[] split = filtersString.split(";");
            for (String filter : split) {
                String[] filterPair = filter.split("=");
                filterMap.put(filterPair[0].toLowerCase(), filterPair[1].toLowerCase());
            }
        }
        return filterMap;
    }
}
