package zip.sodium.home.teleport.task;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import zip.sodium.home.api.data.StoredLocation;
import zip.sodium.home.config.builtin.MessageConfig;

public final class TeleportPlayerTask extends BukkitRunnable {
    private static @NotNull Location convertLocation(final @NotNull StoredLocation location) {
        Preconditions.checkArgument(location != null, "Location is null");

        final var worldUuid = location.worldId();
        Preconditions.checkArgument(worldUuid != null, "Location world id is null");

        final var world = Bukkit.getWorld(worldUuid);
        Preconditions.checkArgument(world != null, "Location world id is invalid");

        return new Location(world, location.x(), location.y(), location.z(), location.yaw(), location.pitch());
    }

    public static void start(final @NotNull Plugin plugin,
                             final @NotNull Player player,
                             final @NotNull StoredLocation location) {
        Preconditions.checkArgument(plugin != null, "Plugin is null");
        Preconditions.checkArgument(player != null, "Player is null");

        new TeleportPlayerTask(player, convertLocation(location))
                .runTaskTimer(plugin, 0L, 20L);
    }

    private int seconds = 0;
    private Location lastLocation = null;

    private final Player player;
    private final Location location;

    private TeleportPlayerTask(final @NotNull Player player,
                               final @NotNull Location location) {
        this.player = player;
        this.location = location;
    }

    private void teleport() {
        player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);

        MessageConfig.TELEPORTED.send(player);
    }

    private void moved() {
        MessageConfig.MOVED.send(player);
    }

    @Override
    public void run() {
        final var currentLocation = player.getLocation();

        if (lastLocation != null
                && !lastLocation.equals(currentLocation)) {
            moved();
            cancel();

            return;
        }

        if (seconds >= 5) {
            teleport();
            cancel();

            return;
        }

        lastLocation = currentLocation;

        MessageConfig.TELEPORTING.send(
                player,
                Placeholder.unparsed("after", Integer.toString(5 - seconds))
        );

        seconds++;
    }
}
