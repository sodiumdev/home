package zip.sodium.home;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import zip.sodium.home.api.HomeApi;
import zip.sodium.home.api.HomeApiImpl;
import zip.sodium.home.command.CommandHandler;
import zip.sodium.home.config.ConfigHandler;
import zip.sodium.home.database.DatabaseHolder;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Entrypoint extends JavaPlugin {
    private static Entrypoint instance = null;

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
        DatabaseHolder.acknowledge();

        getServer().getServicesManager().register(
                HomeApi.class,
                HomeApiImpl.INSTANCE,
                this,
                ServicePriority.Normal
        );

        CommandHandler.acknowledge();
    }

    @Override
    public void onDisable() {
        DatabaseHolder.cleanup();
    }
}
