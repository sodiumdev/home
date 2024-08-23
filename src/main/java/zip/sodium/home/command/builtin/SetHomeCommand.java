package zip.sodium.home.command.builtin;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import zip.sodium.home.Entrypoint;
import zip.sodium.home.api.data.StoredLocation;
import zip.sodium.home.config.builtin.MessageConfig;
import zip.sodium.home.config.builtin.PermissionConfig;

import java.util.List;

public final class SetHomeCommand extends Command {
    public static final Command INSTANCE = new SetHomeCommand();
    public static final String NAME = "sethome";

    private static StoredLocation convertLocation(final Location location) {
        Preconditions.checkArgument(location != null, "Location is null");
        Preconditions.checkArgument(location.getWorld() != null, "Location world is null");

        return new StoredLocation(
                location.getWorld().getUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    private SetHomeCommand() {
        super(NAME);
    }

    @Override
    public boolean execute(final @NotNull CommandSender sender,
                           final @NotNull String commandLabel,
                           final @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            MessageConfig.NOT_PLAYER.send(sender);

            return false;
        }

        final var location = player.getLocation();

        if (args.length >= 1) {
            if (!PermissionConfig.USE_SETHOME_OTHERS.has(player)) {
                MessageConfig.INSUFFICIENT_PERMISSIONS.send(player);

                return false;
            }

            final String name = args[0];
            Entrypoint.api().setHome(
                    player.getServer().getOfflinePlayer(name)
                            .getUniqueId(),
                    convertLocation(location)
            ).thenAccept(success -> {
                if (success.isOk()) {
                    MessageConfig.SUCCESSFULLY_SET_HOME.send(player);
                } else {
                    MessageConfig.COULDNT_SET_HOME.send(player);
                }
            });

            return true;
        }


        if (!PermissionConfig.USE_HOME.has(player)) {
            MessageConfig.INSUFFICIENT_PERMISSIONS.send(player);

            return false;
        }

        Entrypoint.api().setHome(
                player.getUniqueId(),
                convertLocation(location)
        ).thenAccept(result -> {
            if (result.isOk()) {
                MessageConfig.SUCCESSFULLY_SET_HOME.send(player);
            } else {
                MessageConfig.COULDNT_SET_HOME.send(player);
            }
        });

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender,
                                             final @NotNull String alias,
                                             final @NotNull String @NotNull [] args) throws IllegalArgumentException {
        Preconditions.checkArgument(sender != null, "Sender cannot be null");
        Preconditions.checkArgument(args != null, "Arguments cannot be null");
        Preconditions.checkArgument(alias != null, "Alias cannot be null");

        if (!PermissionConfig.USE_SETHOME_OTHERS.has(sender) || args.length != 1) {
            return List.of();
        }

        final String complete = args[0];

        final var senderPlayer = sender instanceof final Player player ? player : null;
        return sender.getServer()
                .getOnlinePlayers()
                .stream()
                .filter(player -> (senderPlayer == null || senderPlayer.canSee(player))
                        && StringUtil.startsWithIgnoreCase(player.getName(), complete))
                .map(Player::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
