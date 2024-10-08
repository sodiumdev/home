package zip.sodium.home.teleport;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import zip.sodium.home.Entrypoint;
import zip.sodium.home.config.builtin.MessageConfig;
import zip.sodium.home.api.HomeApi;
import zip.sodium.home.teleport.task.TeleportPlayerTask;

import java.util.concurrent.CompletableFuture;

public final class HomeTeleportManager {
    private HomeTeleportManager() {}

    public static @NotNull CompletableFuture<Void> teleport(final @NotNull Plugin plugin,
                                                            final @NotNull Player player,
                                                            final @NotNull OfflinePlayer into) {
        Preconditions.checkArgument(plugin != null, "Plugin is null");
        Preconditions.checkArgument(player != null, "Player is null");
        Preconditions.checkArgument(into != null, "Into is null");

        return Entrypoint.api().getHome(into.getUniqueId()).thenAccept(location -> {
            final var unwrapped = location.unwrap();
            if (unwrapped == null) {
                MessageConfig.NO_HOME.send(
                        player,
                        Placeholder.unparsed("player", into.getName())
                );

                return;
            }

            TeleportPlayerTask.start(
                    plugin,
                    player,
                    unwrapped
            );
        });
    }

    public static @NotNull CompletableFuture<Void> teleport(final @NotNull Plugin plugin,
                                                            final @NotNull Player player) {
        return teleport(
                plugin,
                player,
                player
        );
    }
}
