package zip.sodium.home.config.builtin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import zip.sodium.home.config.EnumConfig;

import java.util.Map;

public enum PermissionConfig implements EnumConfig {
    USE_HOME("sodium.home.use"),
    USE_SETHOME("sodium.sethome.use"),
    USE_DELHOME("sodium.delhome.use"),
    USE_HOME_OTHERS("sodium.home.others"),
    USE_SETHOME_OTHERS("sodium.sethome.others"),
    USE_DELHOME_OTHERS("sodium.delhome.others");

    private static YamlConfiguration configFile;
    public static void saveDefaults(final Plugin plugin, final String fileName) {
        for (final var value : values()) {
            Bukkit.getPluginManager().addPermission(
                    Permission.loadPermission(
                            value.get(),
                            Map.of()
                    )
            );
        }

        EnumConfig.trySaveDefaults(plugin, fileName, values());

        configFile = EnumConfig.tryGetConfigFile(plugin, fileName);
    }

    private Object cache = null;
    private final @NotNull Object defaultValue;

    PermissionConfig(final @NotNull String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean has(final Permissible permissible) {
        return permissible.hasPermission(get());
    }

    public String get() {
        return get(FileConfiguration::getString);
    }

    @Override
    public Object cache() {
        return cache;
    }

    @Override
    public void setCache(final Object cache) {
        this.cache = cache;
    }

    @Override
    public @NotNull Object defaultValue() {
        return defaultValue;
    }

    @Override
    public FileConfiguration ymlConfiguration() {
        return configFile;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
