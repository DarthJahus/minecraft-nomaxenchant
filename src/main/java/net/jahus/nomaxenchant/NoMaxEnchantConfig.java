package net.jahus.nomaxenchant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class NoMaxEnchantConfig {

    private static final String FILE_NAME = "nomaxenchant.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int globalCap = -1;

    public Map<String, Integer> perEnchantment = new LinkedHashMap<>();

    public static NoMaxEnchantConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

        if (!Files.exists(configPath)) {
            NoMaxEnchantConfig defaults = createDefault();
            write(configPath, defaults);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            NoMaxEnchantConfig loaded = GSON.fromJson(reader, NoMaxEnchantConfig.class);
            if (loaded == null) {
                NoMaxEnchant.LOGGER.warn("{} is empty or invalid, using defaults", FILE_NAME);
                return createDefault();
            }
            if (loaded.perEnchantment == null) {
                loaded.perEnchantment = new LinkedHashMap<>();
            }
            return loaded;
        } catch (IOException | RuntimeException e) {
            NoMaxEnchant.LOGGER.warn("Failed to read {}, falling back to defaults (vanilla behavior)", FILE_NAME, e);
            return createDefault();
        }
    }

    private static NoMaxEnchantConfig createDefault() {
        NoMaxEnchantConfig config = new NoMaxEnchantConfig();
        config.globalCap = -1;
        config.perEnchantment.put("dummy:dummy", 42);
        return config;
    }

    private static void write(Path path, NoMaxEnchantConfig config) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            NoMaxEnchant.LOGGER.warn("Failed to write default {}", FILE_NAME, e);
        }
    }
}
