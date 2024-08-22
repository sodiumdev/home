package zip.sodium.home.api;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zip.sodium.home.database.DatabaseHolder;
import zip.sodium.home.teleport.HomeTeleportManager;

import java.util.concurrent.CompletableFuture;

public final class HomeApiImpl implements HomeApi {
    public static HomeApi INSTANCE = new HomeApiImpl();

    private HomeApiImpl() {}

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> set(final @NotNull OfflinePlayer player, final @Nullable Location location) {
        Preconditions.checkArgument(player != null, "Player is null");

        return DatabaseHolder.setHome(
                player,
                location
        );
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Location> get(final @NotNull OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Player is null");

        return DatabaseHolder.getHome(
                player
        );
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(final @NotNull Plugin plugin,
                                                     final @NotNull Player player) {
        Preconditions.checkArgument(plugin != null, "Plugin is null");
        Preconditions.checkArgument(player != null, "Player is null");

        return HomeTeleportManager.teleport(
                plugin,
                player
        );
    }

    @Override
    public @NotNull CompletableFuture<Void> teleport(final @NotNull Plugin plugin,
                                                     final @NotNull Player player,
                                                     final @NotNull OfflinePlayer into) {
        Preconditions.checkArgument(plugin != null, "Plugin is null");
        Preconditions.checkArgument(player != null, "Player is null");
        Preconditions.checkArgument(into != null, "Into is null");

        return HomeTeleportManager.teleport(
                plugin,
                player,
                into
        );
    }
}
