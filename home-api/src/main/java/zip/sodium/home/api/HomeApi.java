package zip.sodium.home.api;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface HomeApi {
    /**
     * @param player The player to set home for
     * @param location The home location, null to remove
     * @return Completable future of if the operation succeeded
     */
    @NotNull CompletableFuture<@NotNull Boolean> set(final @NotNull OfflinePlayer player, final @Nullable Location location);

    /**
     * @param player The player to get home from
     * @return Completable future of home, or null if home is not set
     */
    @NotNull CompletableFuture<@Nullable Location> get(final @NotNull OfflinePlayer player);

    /**
     * @param player The player to teleport to home
     */
    @NotNull CompletableFuture<Void> teleport(final @NotNull Plugin plugin,
                                              final @NotNull Player player);

    /**
     * @param player The player to teleport to <code>into</code>'s home
     * @param into The player's home to teleport <code>player</code>
     */
    @NotNull CompletableFuture<Void> teleport(final @NotNull Plugin plugin,
                                              final @NotNull Player player,
                                              final @NotNull OfflinePlayer into);
}
