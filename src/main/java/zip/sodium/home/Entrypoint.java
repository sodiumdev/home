package zip.sodium.home;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import zip.sodium.home.command.CommandHandler;
import zip.sodium.home.config.ConfigHandler;
import zip.sodium.home.api.HomeApi;
import zip.sodium.home.config.builtin.DatabaseConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Entrypoint extends JavaPlugin {
    private static Entrypoint instance = null;
    private static HomeApi api = null;

    public static HomeApi api() {
        return Preconditions.checkNotNull(api, "API not initialized");
    }

    public static Entrypoint instance() {
        return Preconditions.checkNotNull(instance, "Plugin not initialized");
    }

    public static Logger logger() {
        return instance()
                .getLogger();
    }

    public static void disable() {
        Bukkit.getPluginManager().disablePlugin(
                instance()
        );
    }

    public static void disable(final String message) {
        logger().severe(message);

        disable();
    }

    public static void disable(final String message,
                               final Throwable throwable) {
        logger().log(
                Level.SEVERE,
                message,
                throwable
        );

        disable();
    }

    @Override
    public void onEnable() {
        instance = this;

        ConfigHandler.acknowledge(this);

        api = HomeApi.create(
                DatabaseConfig.CONNECTION_STRING.get(),
                DatabaseConfig.DATABASE_NAME.get(),
                DatabaseConfig.DATA_COLLECTION_NAME.get()
        );

        CommandHandler.acknowledge();
    }

    @Override
    public void onDisable() {
        if (api != null) {
            api.cleanup();
            api = null;
        }
    }
}
